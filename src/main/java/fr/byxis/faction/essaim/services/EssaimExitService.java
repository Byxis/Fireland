package fr.byxis.faction.essaim.services;

import fr.byxis.faction.essaim.essaimClass.EssaimGroup;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Gère la sortie des joueurs des essaims
 */
public class EssaimExitService
{

    private final Fireland m_fireland;
    private final EssaimConfigService m_configService;

    public EssaimExitService(Fireland _fireland, EssaimConfigService _configService)
    {
        this.m_fireland = _fireland;
        this.m_configService = _configService;
    }

    public void exitPlayer(Player player, EssaimGroup group, boolean isFinished)
    {
        String essaimName = group.getEssaimName();

        // Téléporter devant l'essaim (position ENTRY)
        Location exitLocation = getEssaimExitLocation(essaimName);
        player.teleport(exitLocation);

        // Messages selon le contexte
        if (isFinished)
        {
            InGameUtilities.sendPlayerInformation(player, "§aVous avez quitté l'essaim terminé. Bien joué !");
        }
        else
        {
            InGameUtilities.sendPlayerInformation(player, "§eVous avez quitté l'essaim.");
        }

        // Sons
        InGameUtilities.playPlayerSound(player, "entity.enderman.teleport", org.bukkit.SoundCategory.PLAYERS, 1, 1);
    }

    public void exitGroup(EssaimGroup group, boolean isFinished)
    {
        for (Player member : group.getMembers())
        {
            exitPlayer(member, group, isFinished);
        }
    }

    private Location getEssaimExitLocation(String essaimName)
    {
        try
        {
            return m_configService.getEssaimLocation(essaimName, EssaimConfigService.LocationType.ENTRY);
        }
        catch (Exception e)
        {
            m_fireland.getLogger().warning("Could not get ENTRY location for essaim " + essaimName + ", falling back to world spawn");
            return Bukkit.getWorld("world").getSpawnLocation();
        }
    }

    public void teleportToEssaimEntry(Player player, String essaimName)
    {
        Location entryLocation = getEssaimExitLocation(essaimName);
        player.teleport(entryLocation);

        InGameUtilities.sendPlayerInformation(player, "§eVous avez été téléporté devant l'essaim.");
        InGameUtilities.playPlayerSound(player, "entity.enderman.teleport", org.bukkit.SoundCategory.PLAYERS, 1, 1);
    }
}