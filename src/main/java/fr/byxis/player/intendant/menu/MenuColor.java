package fr.byxis.player.intendant.menu;

import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.faction.faction.FactionInformation;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuColor {

    public static void openColorMenu(Fireland main, Player p, FactionInformation infos)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        FactionFunctions ff = new FactionFunctions(main, p);
        Inventory faction = Bukkit.createInventory(null, 54, ff.getColorCode(infos.getName()) + "Changement de couleur");
        setItemColorMenu(main, faction, p, infos);
        p.openInventory(faction);
    }

    private static void setItemColorMenu(Fireland main, Inventory inv, Player p, FactionInformation infos)
    {
        for (int i = 0; i < 9; i++)
        {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(19, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§4" + infos.getName(), (short) 0));
        inv.setItem(20, InventoryUtilities.setItemMeta(Material.ORANGE_STAINED_GLASS_PANE, "§6" + infos.getName(), (short) 0));
        inv.setItem(21, InventoryUtilities.setItemMeta(Material.YELLOW_STAINED_GLASS_PANE, "§e" + infos.getName(), (short) 0));
        inv.setItem(22, InventoryUtilities.setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§a" + infos.getName(), (short) 0));
        inv.setItem(23, InventoryUtilities.setItemMeta(Material.GREEN_STAINED_GLASS_PANE, "§2" + infos.getName(), (short) 0));
        inv.setItem(24, InventoryUtilities.setItemMeta(Material.CYAN_STAINED_GLASS_PANE, "§3" + infos.getName(), (short) 0));
        inv.setItem(25, InventoryUtilities.setItemMeta(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "§b" + infos.getName(), (short) 0));
        inv.setItem(34, InventoryUtilities.setItemMeta(Material.BLUE_STAINED_GLASS_PANE, "§1" + infos.getName(), (short) 0));
        inv.setItem(33, InventoryUtilities.setItemMeta(Material.PURPLE_STAINED_GLASS_PANE, "§5" + infos.getName(), (short) 0));
        inv.setItem(32, InventoryUtilities.setItemMeta(Material.MAGENTA_STAINED_GLASS_PANE, "§d" + infos.getName(), (short) 0));
        inv.setItem(31, InventoryUtilities.setItemMeta(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "§7" + infos.getName(), (short) 0));
        inv.setItem(30, InventoryUtilities.setItemMeta(Material.GRAY_STAINED_GLASS_PANE, "§8" + infos.getName(), (short) 0));
        inv.setItem(29, InventoryUtilities.setItemMeta(Material.BLACK_STAINED_GLASS_PANE, "§0" + infos.getName(), (short) 0));
        inv.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour à l'intendant", (short) 0));
    }

}
