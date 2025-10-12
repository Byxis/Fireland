package fr.byxis.faction.essaim.services;

import fr.byxis.faction.essaim.essaimClass.EssaimClass;
import fr.byxis.faction.essaim.essaimClass.EssaimGroup;
import fr.byxis.faction.essaim.managers.GroupManager;
import fr.byxis.faction.essaim.managers.SpawnerManager;
import fr.byxis.faction.essaim.repository.EssaimRepository;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Core business logic for Essaim operations
 * Handles essaim lifecycle, state management, and coordination
 */
public class EssaimService {

    private final EssaimConfigService configService;
    private final GroupManager groupManager;
    private final SpawnerManager spawnerManager;
    private final Map<String, EssaimClass> activeEssaims;
    private final EssaimRewardService rewardService;
    private final EssaimExitService exitService;
    private final EssaimCooldownService cooldownService;
    private final EssaimRepository repository;

    public EssaimService(EssaimConfigService configService, GroupManager groupManager, SpawnerManager spawnerManager, Fireland plugin) {
        this.configService = configService;
        this.groupManager = groupManager;
        this.spawnerManager = spawnerManager;
        this.activeEssaims = new HashMap<>();
        this.repository = new EssaimRepository(plugin.getDatabaseManager().getFirelandConnection(), plugin);
        this.rewardService = new EssaimRewardService(plugin, repository);
        this.exitService = new EssaimExitService(plugin, configService);
        this.cooldownService = new EssaimCooldownService(repository);

    }

    /**
     * Enables an essaim for gameplay
     */
    public boolean enableEssaim(String essaimName) {
        if (activeEssaims.containsKey(essaimName)) {
            EssaimClass essaim = activeEssaims.get(essaimName);
            essaim.setClosed(false);
            configService.setEssaimClosed(essaimName, false);
            return false; // Already exists, just reopened
        }

        try {
            EssaimClass essaim = new EssaimClass(essaimName, configService);
            configService.setEssaimClosed(essaimName, false);
            activeEssaims.put(essaimName, essaim);
            return true;
        } catch (Exception e) {
            throw new EssaimException("Failed to enable essaim: " + essaimName, e);
        }
    }

    /**
     * Disables an essaim (only non-event based ones)
     */
    public boolean disableEssaim(String essaimName) {
        //configService.getEssaimSpawners() surement ça puis spawnerManager.disableSpawner()
        EssaimClass essaim = activeEssaims.get(essaimName);
        if (essaim == null) {
            System.out.println("Cannot disable essaim " + essaimName + " - not found in active essaims");
            return false;
        }

        if (essaim.isEventBased()) {
            System.out.println("Cannot disable event-based essaim normally: " + essaimName);
            return false;
        }

        System.out.println("Disabling essaim: " + essaimName);
        return performDisable(essaimName, essaim);
    }

    /**
     * Forces disable of any essaim
     */
    public boolean forceDisableEssaim(String essaimName) {
        EssaimClass essaim = activeEssaims.get(essaimName);
        if (essaim == null) {
            return false;
        }

        return performDisable(essaimName, essaim);
    }

    /**
     * Resets an essaim to its initial state
     */
    public boolean resetEssaim(String essaimName) {
        EssaimClass essaim = activeEssaims.get(essaimName);
        if (essaim == null) {
            return false;
        }

        // Trigger reset redstone block
        setRedstoneBlock(essaim.getReset());

        // Clean up spawners
        spawnerManager.cleanupEssaimSpawners(essaimName);

        return true;
    }

    /**
     * Starts an essaim expedition
     */
    public boolean startEssaim(String essaimName, int difficulty) {
        if (!groupManager.hasGroup(essaimName)) {
            return false;
        }

        EssaimClass essaim = activeEssaims.get(essaimName);
        if (essaim == null) {
            return false;
        }

        // Reset the essaim first
        resetEssaim(essaimName);

        // Set solo mode
        setRedstoneBlock(essaim.getSolo());

        // Set difficulty
        setDifficultyBlocks(essaim, difficulty);

        // Set start block
        setRedstoneBlock(essaim.getStart());

        // Start the group expedition
        return groupManager.startExpedition(essaimName, difficulty);
    }

    /**
     * Finishes an essaim successfully
     */
    public void finishEssaim(String essaimName) {
        EssaimClass essaim = activeEssaims.get(essaimName);
        if (essaim == null) {
            return;
        }

        essaim.setFinish();

        if (groupManager.hasGroup(essaimName)) {
            EssaimGroup group = groupManager.getGroup(essaimName);
            group.finishExpedition();
            rewardService.distributeRewards(group, essaim);

            for (Player player : group.getMembers()) {
                InGameUtilities.sendPlayerInformation(player,
                        "Vous avez terminé l'expédition ! L'essaim se fermera automatiquement dans quelques minutes.");
            }
        }
    }

    /**
     * Unfinishes an essaim (for side missions)
     */
    public void unfinishEssaim(String essaimName) {
        EssaimClass essaim = activeEssaims.get(essaimName);
        if (essaim == null) {
            return;
        }

        essaim.unFinish();

        if (groupManager.hasGroup(essaimName)) {
            EssaimGroup group = groupManager.getGroup(essaimName);
            for (Player player : group.getMembers()) {
                InGameUtilities.sendPlayerInformation(player,
                        "Vous avez activé une mission annexe, le décompte a été annulé.");
            }
        }
    }

    /**
     * Checks if an essaim is currently active
     */
    public boolean isEssaimActive(String essaimName) {
        return activeEssaims.containsKey(essaimName);
    }

    /**
     * Gets an active essaim
     */
    public EssaimClass getActiveEssaim(String essaimName) {
        return activeEssaims.get(essaimName);
    }

    /**
     * Performs periodic maintenance tasks
     */
    public void performPeriodicMaintenance() {
        for (Map.Entry<String, EssaimClass> entry : activeEssaims.entrySet()) {
            String essaimName = entry.getKey();
            EssaimClass essaim = entry.getValue();

            if (essaim.isFinished() && essaim.shouldClose() && groupManager.hasGroup(essaimName)) {
                handleExpiredEssaim(essaimName);
            } else if (essaim.isFinished() && groupManager.hasGroup(essaimName)) {
                notifyExpirationWarning(essaimName, essaim);
            }
        }
    }

    // Private helper methods

    private boolean performDisable(String essaimName, EssaimClass essaim) {
        System.out.println("Performing disable for essaim: " + essaimName);

        essaim.setClosed(true);
        configService.setEssaimClosed(essaimName, true);
        activeEssaims.remove(essaimName);

        // Clean up any active groups
        if (groupManager.hasGroup(essaimName)) {
            System.out.println("Cleaning up group for disabled essaim: " + essaimName);
            groupManager.disbandGroup(essaimName);
        }

        // Clean up spawners
        spawnerManager.cleanupEssaimSpawners(essaimName);

        System.out.println("Successfully disabled essaim: " + essaimName);
        return true;
    }

    private void setRedstoneBlock(Location location) {
        if (location != null) {
            location.getBlock().setType(Material.REDSTONE_BLOCK);
        }
    }

    private void setDifficultyBlocks(EssaimClass essaim, int difficulty) {
        switch (difficulty) {
            case 1 -> setRedstoneBlock(essaim.getDifficulty1());
            case 2 -> setRedstoneBlock(essaim.getDifficulty2());
            case 3 -> setRedstoneBlock(essaim.getDifficulty3());
        }
    }

    private void handleExpiredEssaim(String essaimName) {
        EssaimGroup group = groupManager.getGroup(essaimName);
        if (group != null && !group.getMembers().isEmpty()) {
            Player leader = group.getLeader();
            handleFinishedEssaimLeave(essaimName, leader, true);
        }
    }

    private void handleFinishedEssaimLeave(String essaimName, Player player, boolean forced) {
        // Cette logique devrait être refactorisée ici depuis EssaimFunctions
        // Pour l'instant, on peut déléguer temporairement
        if (groupManager.hasGroup(essaimName)) {
            groupManager.disbandGroup(essaimName);
            // Téléporter le joueur, donner récompenses, etc.
        }
    }

    private void notifyExpirationWarning(String essaimName, EssaimClass essaim) {
        EssaimGroup group = groupManager.getGroup(essaimName);
        if (group != null) {
            long minutesRemaining = calculateMinutesRemaining(essaim.getFinishDate());
            String message = "Vous allez être expulsé de l'essaim dans " + minutesRemaining + " minutes.";

            for (Player player : group.getMembers()) {
                InGameUtilities.sendPlayerInformation(player, message);
            }
        }
    }

    private long calculateMinutesRemaining(Timestamp finishDate) {
        long currentTime = System.currentTimeMillis();
        long expireTime = finishDate.getTime() + (8 * 60 * 1000); // 8 minutes
        return Math.max(0, (expireTime - currentTime) / (60 * 1000));
    }

    public EssaimExitService getExitService()
    {
        return exitService;
    }

    public EssaimCooldownService getCooldownService()
    {
        return cooldownService;
    }

    public static class EssaimException extends RuntimeException {
        public EssaimException(String message) {
            super(message);
        }

        public EssaimException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public EssaimRepository getRepository() {
        return this.repository;
    }
}