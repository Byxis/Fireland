package fr.byxis.faction.essaim.services;

import fr.byxis.faction.essaim.essaimClass.EssaimGroup;
import fr.byxis.faction.essaim.services.EssaimConfigService;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Gère la sortie des joueurs des essaims
 */
public class EssaimExitService {

    private final Fireland plugin;
    private final EssaimConfigService configService;

    public EssaimExitService(Fireland plugin, EssaimConfigService configService) {
        this.plugin = plugin;
        this.configService = configService;
    }

    /**
     * Fait sortir un joueur de l'essaim avec téléportation
     */
    public void exitPlayer(Player player, EssaimGroup group, boolean isFinished) {
        String essaimName = group.getEssaimName();

        // Téléporter devant l'essaim (position ENTRY)
        Location exitLocation = getEssaimExitLocation(essaimName);
        player.teleport(exitLocation);

        // Messages selon le contexte
        if (isFinished) {
            InGameUtilities.sendPlayerInformation(player,
                    "§aVous avez quitté l'essaim terminé. Bien joué !");
        } else {
            InGameUtilities.sendPlayerInformation(player,
                    "§eVous avez quitté l'essaim.");
        }

        // Sons
        InGameUtilities.playPlayerSound(player, "entity.enderman.teleport",
                org.bukkit.SoundCategory.PLAYERS, 1, 1);
    }

    /**
     * Fait sortir tout un groupe
     */
    public void exitGroup(EssaimGroup group, boolean isFinished) {
        for (Player member : group.getMembers()) {
            exitPlayer(member, group, isFinished);
        }
    }

    /**
     * Obtient la position de sortie devant l'essaim
     */
    private Location getEssaimExitLocation(String essaimName) {
        try {
            // Utiliser la position ENTRY de l'essaim (devant l'essaim)
            return configService.getEssaimLocation(essaimName, EssaimConfigService.LocationType.ENTRY);
        } catch (Exception e) {
            plugin.getLogger().warning("Could not get ENTRY location for essaim " + essaimName +
                    ", falling back to world spawn");

            // Fallback vers le spawn du monde principal
            return Bukkit.getWorld("world").getSpawnLocation();
        }
    }

    /**
     * Téléporte un joueur devant un essaim spécifique
     */
    public void teleportToEssaimEntry(Player player, String essaimName) {
        Location entryLocation = getEssaimExitLocation(essaimName);
        player.teleport(entryLocation);

        InGameUtilities.sendPlayerInformation(player,
                "§eVous avez été téléporté devant l'essaim.");
        InGameUtilities.playPlayerSound(player, "entity.enderman.teleport",
                org.bukkit.SoundCategory.PLAYERS, 1, 1);
    }
}