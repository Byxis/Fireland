package fr.byxis.event;

import fr.byxis.fireland.utilities.PermissionUtilities;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class VillagerInteraction implements Listener
{

    public VillagerInteraction()
    {
    }

    @EventHandler
    public void onInteractionWithVillager(PlayerInteractEntityEvent e)
    {
        Player p = e.getPlayer();

        if (!(e.getRightClicked() instanceof Villager) || e.getRightClicked().getCustomName() == null)
        {
            return;
        }

        if (e.getRightClicked().getCustomName().contains("Utilitaire"))
        {
            PermissionUtilities.commandExecutor(p, "shop utilitaire", "fireland.command.shop");
        }
        else if (e.getRightClicked().getCustomName().contains("Assaut"))
        {
            PermissionUtilities.commandExecutor(p, "shop assaut", "fireland.command.shop");
        }
        else if (e.getRightClicked().getCustomName().contains("Revolver"))
        {
            PermissionUtilities.commandExecutor(p, "shop revolver", "fireland.command.shop");
        }
        else if (e.getRightClicked().getCustomName().contains("SMG"))
        {
            PermissionUtilities.commandExecutor(p, "shop smg", "fireland.command.shop");
        }
        else if (e.getRightClicked().getCustomName().contains("Fusil"))
        {
            PermissionUtilities.commandExecutor(p, "shop fusil", "fireland.command.shop");
        }
        else if (e.getRightClicked().getCustomName().contains("lourdes"))
        {
            PermissionUtilities.commandExecutor(p, "shop lourd", "fireland.command.shop");
        }
        else if (e.getRightClicked().getCustomName().contains("Héliport"))
        {
            PermissionUtilities.commandExecutor(p, "heliport", "fireland.command.heliport");
        }
        else if (e.getRightClicked().getCustomName().contains("Banquier"))
        {
            PermissionUtilities.commandExecutor(p, "bank", "fireland.command.bank");
        }
        else if (e.getRightClicked().getCustomName().contains("Vendeur de Pass Vert"))
        {
            PermissionUtilities.commandExecutor(p, "shop passv", "fireland.command.shop");
        }
        else if (e.getRightClicked().getCustomName().contains("Vendeur de Pass Bleu"))
        {
            PermissionUtilities.commandExecutor(p, "shop passb", "fireland.command.shop");
        }
        else if (e.getRightClicked().getCustomName().contains("Vendeur de Pass Jaune"))
        {
            PermissionUtilities.commandExecutor(p, "shop passj", "fireland.command.shop");
        }
        else if (e.getRightClicked().getCustomName().contains("Vendeur de Pass Rouge"))
        {
            PermissionUtilities.commandExecutor(p, "shop passr", "fireland.command.shop");
        }
    }
}
