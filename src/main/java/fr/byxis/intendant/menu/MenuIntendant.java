package fr.byxis.intendant.menu;

import fr.byxis.faction.FactionFunctions;
import fr.byxis.faction.FactionPlayerInformation;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.ItemUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuIntendant {

    public static void OpenIntendant(Fireland main, Player p)
    {
        Inventory craftMenu = Bukkit.createInventory(null, 27, "§8Intendant");
        SetIntendantItems(main, craftMenu, p);
        p.openInventory(craftMenu);
    }

    private static void SetIntendantItems(Fireland main, Inventory craftMenu, Player p)
    {
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation infos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        if(infos == null)
        {
            craftMenu.setItem(12, ItemUtilities.setItemMeta(Material.FIREWORK_ROCKET, "§eBoosters", (short) 0));
            craftMenu.setItem(14, ItemUtilities.setItemMeta(Material.EMERALD, "§aHôtel des ventes", (short) 0));
        }
        else
        {
            craftMenu.setItem(11, ItemUtilities.setItemMeta(Material.FIREWORK_ROCKET, "§eBoosters", (short) 0));
            craftMenu.setItem(13, ItemUtilities.setItemMeta(Material.EMERALD, "§aHôtel des ventes", (short) 0));
            craftMenu.setItem(15, ItemUtilities.setItemMeta(Material.DIAMOND_SWORD, "§aFactions", (short) 0));
        }

    }

}
