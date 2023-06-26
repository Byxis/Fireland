package fr.byxis.player.intendant.menu;

import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.faction.faction.FactionPlayerInformation;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuIntendant {

    public static void OpenIntendant(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory craftMenu = Bukkit.createInventory(null, 27, "§8Intendant");
        SetIntendantItems(main, craftMenu, p);
        p.openInventory(craftMenu);
    }

    private static void SetIntendantItems(Fireland main, Inventory craftMenu, Player p)
    {
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation infos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        if(infos.getUuid() == null)
        {
            craftMenu.setItem(4, InventoryUtilities.setItemMeta(Material.NETHER_STAR, "§bSuccès", (short) 0));
            craftMenu.setItem(10, InventoryUtilities.setItemMeta(Material.FIREWORK_ROCKET, "§eBoosters", (short) 0));
            craftMenu.setItem(12, InventoryUtilities.setItemMeta(Material.EMERALD, "§aHôtel des ventes", (short) 0));
            craftMenu.setItem(14, InventoryUtilities.setItemMeta(Material.BELL, "§eMissions quotidiennes", (short) 0));
            craftMenu.setItem(16, InventoryUtilities.setItemMeta(Material.PLAYER_HEAD, "§4Primes", (short) 0));
        }
        else
        {
            craftMenu.setItem(18, InventoryUtilities.setItemMeta(Material.NETHER_STAR, "§bSuccès", (short) 0));
            craftMenu.setItem(11, InventoryUtilities.setItemMeta(Material.FIREWORK_ROCKET, "§eBoosters", (short) 0));
            craftMenu.setItem(0, InventoryUtilities.setItemMeta(Material.BELL, "§eMissions quotidiennes", (short) 0));
            craftMenu.setItem(13, InventoryUtilities.setItemMeta(Material.EMERALD, "§aHôtel des ventes", (short) 0));
            craftMenu.setItem(15, InventoryUtilities.setItemMeta(Material.DIAMOND_SWORD, "§aFactions", (short) 0));
            craftMenu.setItem(8, InventoryUtilities.setItemMeta(Material.DEAD_FIRE_CORAL, "§cEssaims", (short) 0));
            craftMenu.setItem(26, InventoryUtilities.setItemMeta(Material.PLAYER_HEAD, "§4Primes", (short) 0));
        }

    }

}
