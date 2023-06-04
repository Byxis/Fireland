package fr.byxis.intendant.menu;

import fr.byxis.faction.FactionFunctions;
import fr.byxis.faction.FactionInformation;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.ItemUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuColor {

    public static void OpenColorMenu(Fireland main, Player p, FactionInformation infos)
    {
        FactionFunctions ff = new FactionFunctions(main, p);
        Inventory faction = Bukkit.createInventory(null, 54, ff.GetColorCode(infos.getName())+"Changement de couleur");
        SetItemColorMenu(main, faction, p, infos);
        p.openInventory(faction);
    }

    private static void SetItemColorMenu(Fireland main, Inventory inv, Player p, FactionInformation infos)
    {
        for(int i=0;i<9;i++) {
            inv.setItem(i, ItemUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            inv.setItem(i + 45, ItemUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(19, ItemUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§4"+infos.getName(), (short) 0));
        inv.setItem(20, ItemUtilities.setItemMeta(Material.ORANGE_STAINED_GLASS_PANE, "§6"+infos.getName(), (short) 0));
        inv.setItem(21, ItemUtilities.setItemMeta(Material.YELLOW_STAINED_GLASS_PANE, "§e"+infos.getName(), (short) 0));
        inv.setItem(22, ItemUtilities.setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§a"+infos.getName(), (short) 0));
        inv.setItem(23, ItemUtilities.setItemMeta(Material.GREEN_STAINED_GLASS_PANE, "§2"+infos.getName(), (short) 0));
        inv.setItem(24, ItemUtilities.setItemMeta(Material.CYAN_STAINED_GLASS_PANE, "§3"+infos.getName(), (short) 0));
        inv.setItem(25, ItemUtilities.setItemMeta(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "§b"+infos.getName(), (short) 0));
        inv.setItem(34, ItemUtilities.setItemMeta(Material.BLUE_STAINED_GLASS_PANE, "§1"+infos.getName(), (short) 0));
        inv.setItem(33, ItemUtilities.setItemMeta(Material.PURPLE_STAINED_GLASS_PANE, "§5"+infos.getName(), (short) 0));
        inv.setItem(32, ItemUtilities.setItemMeta(Material.MAGENTA_STAINED_GLASS_PANE, "§d"+infos.getName(), (short) 0));
        inv.setItem(31, ItemUtilities.setItemMeta(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "§7"+infos.getName(), (short) 0));
        inv.setItem(30, ItemUtilities.setItemMeta(Material.GRAY_STAINED_GLASS_PANE, "§8"+infos.getName(), (short) 0));
        inv.setItem(29, ItemUtilities.setItemMeta(Material.BLACK_STAINED_GLASS_PANE, "§0"+infos.getName(), (short) 0));
        inv.setItem(53, ItemUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour à l'intendant", (short) 0));
    }

}
