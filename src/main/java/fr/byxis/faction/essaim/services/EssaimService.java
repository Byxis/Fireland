package fr.byxis.faction.essaim.services;

import fr.byxis.faction.essaim.essaimClass.EssaimClass;
import fr.byxis.faction.essaim.essaimClass.EssaimGroup;
import fr.byxis.faction.essaim.managers.GroupManager;
import fr.byxis.faction.essaim.managers.SpawnerManager;
import fr.byxis.faction.essaim.repository.EssaimRepository;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Core business logic for Essaim operations Handles essaim lifecycle, state
 * management, and coordination
 */
public class EssaimService
{

    private final EssaimConfigService m_configService;
    private final GroupManager m_groupManager;
    private final SpawnerManager m_spawnerManager;
    private final Map<String, EssaimClass> m_activeEssaims;
    private final EssaimRewardService m_rewardservice;
    private final EssaimExitService m_exitService;
    private final EssaimCooldownService m_cooldownService;
    private final EssaimRepository m_repository;

    public EssaimService(EssaimConfigService _configservice, GroupManager _groupManager, SpawnerManager _spawnerManager, Fireland _fireland)
    {
        this.m_configService = _configservice;
        this.m_groupManager = _groupManager;
        this.m_spawnerManager = _spawnerManager;
        this.m_activeEssaims = new HashMap<>();
        this.m_repository = new EssaimRepository(_fireland.getDatabaseManager().getFirelandConnection(), _fireland);
        this.m_rewardservice = new EssaimRewardService(_fireland, m_repository);
        this.m_exitService = new EssaimExitService(_fireland, _configservice);
        this.m_cooldownService = new EssaimCooldownService(m_repository);

    }

    /**
     * Enables an essaim for gameplay
     *
     * @param _essaimName
     *            The name of the essaim to enable
     *
     * @return true if the essaim was newly enabled, false if it was already active
     */
    public boolean enableEssaim(String _essaimName)
    {
        if (m_activeEssaims.containsKey(_essaimName))
        {
            EssaimClass essaim = m_activeEssaims.get(_essaimName);
            essaim.setClosed(false);
            m_configService.setEssaimClosed(_essaimName, false);
            return false; // Already exists, just reopened
        }

        try
        {
            EssaimClass essaim = new EssaimClass(_essaimName, m_configService);
            m_configService.setEssaimClosed(_essaimName, false);
            m_activeEssaims.put(_essaimName, essaim);
            return true;
        }
        catch (Exception e)
        {
            throw new EssaimException("Failed to enable essaim: " + _essaimName, e);
        }
    }

    /**
     * Disables an essaim (only non-event based ones)
     *
     * @param _essaimName
     *            The name of the essaim to disable
     *
     * @return true if the essaim was disabled successfully, false otherwise
     */
    public boolean disableEssaim(String _essaimName)
    {
        EssaimClass essaim = m_activeEssaims.get(_essaimName);
        if (essaim == null)
        {
            System.out.println("Cannot disable essaim " + _essaimName + " - not found in active essaims");
            return false;
        }

        if (essaim.isEventBased())
        {
            System.out.println("Cannot disable event-based essaim normally: " + _essaimName);
            return false;
        }

        System.out.println("Disabling essaim: " + _essaimName);
        return performDisable(_essaimName, essaim);
    }

    /**
     * Forces disable of any essaim
     *
     * @param _essaimName
     *            The name of the essaim to disable
     *
     * @return true if the essaim was disabled successfully, false otherwise
     */
    public boolean forceDisableEssaim(String _essaimName)
    {
        EssaimClass essaim = m_activeEssaims.get(_essaimName);
        if (essaim == null)
        {
            return false;
        }

        return performDisable(_essaimName, essaim);
    }

    /**
     * Resets an essaim to its initial state
     *
     * @param _essaimName
     *            The name of the essaim to disable
     *
     * @return true if the essaim was disabled successfully, false otherwise
     */
    public boolean resetEssaim(String _essaimName)
    {
        EssaimClass essaim = m_activeEssaims.get(_essaimName);
        if (essaim == null)
        {
            return false;
        }

        // Trigger reset redstone block
        setRedstoneBlock(essaim.getReset());

        // Clean up spawners
        m_spawnerManager.cleanupEssaimSpawners(_essaimName);

        return true;
    }

    /**
     * Starts an essaim expedition
     *
     * @param _essaimName
     *            The name of the essaim to start
     * @param _difficulty
     *            The selected difficulty level
     *
     * @return true if the essaim was started successfully, false otherwise
     */
    public boolean startEssaim(String _essaimName, int _difficulty)
    {
        if (!m_groupManager.hasGroup(_essaimName))
        {
            return false;
        }

        EssaimClass essaim = m_activeEssaims.get(_essaimName);
        if (essaim == null)
        {
            return false;
        }

        resetEssaim(_essaimName);
        setRedstoneBlock(essaim.getSolo());
        setDifficultyBlocks(essaim, _difficulty);
        setRedstoneBlock(essaim.getStart());

        return m_groupManager.startExpedition(_essaimName, _difficulty);
    }

    /**
     * Finishes an essaim successfully
     *
     * @param _essaimName
     *            The name of the essaim to finish
     */
    public void finishEssaim(String _essaimName)
    {
        EssaimClass essaim = m_activeEssaims.get(_essaimName);
        if (essaim == null)
        {
            return;
        }

        essaim.setFinish();

        if (m_groupManager.hasGroup(_essaimName))
        {
            EssaimGroup group = m_groupManager.getGroup(_essaimName);
            group.finishExpedition();
            m_rewardservice.distributeRewards(group, essaim);

            for (Player player : group.getMembers())
            {
                InGameUtilities.sendPlayerInformation(player,
                        "Vous avez terminé l'expédition ! L'essaim se fermera automatiquement dans quelques minutes.");
            }
        }
    }

    /**
     * Unfinishes an essaim (for side missions)
     *
     * @param _essaimName
     *            The name of the essaim to unfinish
     */
    public void unfinishEssaim(String _essaimName)
    {
        EssaimClass essaim = m_activeEssaims.get(_essaimName);
        if (essaim == null)
        {
            return;
        }

        essaim.unFinish();

        if (m_groupManager.hasGroup(_essaimName))
        {
            EssaimGroup group = m_groupManager.getGroup(_essaimName);
            for (Player player : group.getMembers())
            {
                InGameUtilities.sendPlayerInformation(player, "Vous avez activé une mission annexe, le décompte a été annulé.");
            }
        }
    }

    /**
     * Checks if an essaim is currently active
     *
     * @param _essaimName
     *            The name of the essaim to check
     *
     * @return true if the essaim is active, false otherwise
     */
    public boolean isEssaimActive(String _essaimName)
    {
        return m_activeEssaims.containsKey(_essaimName);
    }

    /**
     * Gets an active essaim
     *
     * @param _essaimName
     *            The name of the essaim to get
     *
     * @return The EssaimClass instance if active, null otherwise
     */
    public EssaimClass getActiveEssaim(String _essaimName)
    {
        return m_activeEssaims.get(_essaimName);
    }

    /**
     * Performs periodic maintenance tasks
     */
    public void performPeriodicMaintenance()
    {
        for (Map.Entry<String, EssaimClass> entry : m_activeEssaims.entrySet())
        {
            String essaimName = entry.getKey();
            EssaimClass essaim = entry.getValue();

            if (essaim.isFinished() && essaim.shouldClose() && m_groupManager.hasGroup(essaimName))
            {
                handleExpiredEssaim(essaimName);
            }
            else if (essaim.isFinished() && m_groupManager.hasGroup(essaimName))
            {
                notifyExpirationWarning(essaimName, essaim);
            }
        }
    }

    // Private helper methods

    private boolean performDisable(String essaimName, EssaimClass essaim)
    {
        System.out.println("Performing disable for essaim: " + essaimName);

        essaim.setClosed(true);
        m_configService.setEssaimClosed(essaimName, true);
        m_activeEssaims.remove(essaimName);

        // Clean up any active groups
        if (m_groupManager.hasGroup(essaimName))
        {
            System.out.println("Cleaning up group for disabled essaim: " + essaimName);
            m_groupManager.disbandGroup(essaimName);
        }

        // Clean up spawners
        m_spawnerManager.cleanupEssaimSpawners(essaimName);

        System.out.println("Successfully disabled essaim: " + essaimName);
        return true;
    }

    private void setRedstoneBlock(Location location)
    {
        if (location != null)
        {
            location.getBlock().setType(Material.REDSTONE_BLOCK);
        }
    }

    private void setDifficultyBlocks(EssaimClass essaim, int difficulty)
    {
        switch (difficulty)
        {
            case 1 -> setRedstoneBlock(essaim.getDifficulty1());
            case 2 -> setRedstoneBlock(essaim.getDifficulty2());
            case 3 -> setRedstoneBlock(essaim.getDifficulty3());
        }
    }

    private void handleExpiredEssaim(String essaimName)
    {
        EssaimGroup group = m_groupManager.getGroup(essaimName);
        if (group != null && !group.getMembers().isEmpty())
        {
            Player leader = group.getLeader();
            handleFinishedEssaimLeave(essaimName, leader, true);
        }
    }

    private void handleFinishedEssaimLeave(String essaimName, Player player, boolean forced)
    {
        // Cette logique devrait être refactorisée ici depuis EssaimFunctions
        // Pour l'instant, on peut déléguer temporairement
        if (m_groupManager.hasGroup(essaimName))
        {
            m_groupManager.disbandGroup(essaimName);
            // Téléporter le joueur, donner récompenses, etc.
        }
    }

    private void notifyExpirationWarning(String essaimName, EssaimClass essaim)
    {
        EssaimGroup group = m_groupManager.getGroup(essaimName);
        if (group != null)
        {
            long minutesRemaining = calculateMinutesRemaining(essaim.getFinishDate());
            String message = "Vous allez être expulsé de l'essaim dans " + minutesRemaining + " minutes.";

            for (Player player : group.getMembers())
            {
                InGameUtilities.sendPlayerInformation(player, message);
            }
        }
    }

    private long calculateMinutesRemaining(Timestamp finishDate)
    {
        long currentTime = System.currentTimeMillis();
        long expireTime = finishDate.getTime() + (8 * 60 * 1000); // 8 minutes
        return Math.max(0, (expireTime - currentTime) / (60 * 1000));
    }

    public EssaimExitService getExitService()
    {
        return m_exitService;
    }

    public EssaimCooldownService getCooldownService()
    {
        return m_cooldownService;
    }

    public static class EssaimException extends RuntimeException
    {
        public EssaimException(String message)
        {
            super(message);
        }

        public EssaimException(String message, Throwable cause)
        {
            super(message, cause);
        }
    }

    public EssaimRepository getRepository()
    {
        return this.m_repository;
    }
}