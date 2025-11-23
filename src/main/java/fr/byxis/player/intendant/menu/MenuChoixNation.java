package fr.byxis.player.intendant.menu;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuChoixNation
{

    public static void openChoixNation(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory inv = Bukkit.createInventory(null, 36, "§8Choisir sa nation");
        setChoixNationItems(main, inv, p);
        p.openInventory(inv);
    }

    private static void setChoixNationItems(Fireland main, Inventory inv, Player p)
    {
        List<String> lore = new ArrayList<>();
        lore.add("§8Bonus d'xp sur les captures de zone.");
        lore.add("§8Bonus d'xp sur en tuant un joueur.");
        lore.add("");
        lore.add("§8Augmentation du prix dans les magasins d'Etat.");
        lore.add("§8Diminution du prix dans les magasins de Bannis.");
        inv.setItem(11, InventoryUtilities.getBannisBanner("§cRejoindre la nation des Bannis", lore));

        lore = new ArrayList<>();
        lore.add("§8Aucun avantage/désavantage.");
        inv.setItem(13, InventoryUtilities.getNeutreBanner("§cRester dans la nation Neutre", lore));

        lore = new ArrayList<>();
        lore.add("§8Bonus d'xp en finissant un essaim");
        lore.add("§8Bonus d'xp sur en tuant des infectés.");
        lore.add("");
        lore.add("§8Augmentation du prix dans les magasins de Bannis.");
        lore.add("§8Diminution du prix dans les magasins d'Etat.");
        inv.setItem(15, InventoryUtilities.getEtatBanner("§cRejoindre la nation de l'Etat", lore));

        for (int i = 0; i < 9; i++)
            inv.setItem(i + 27, InventoryUtilities.getWhiteGlassPane());

        lore = new ArrayList<>();
        lore.add("§cVotre choix n'est pas définitif, mais il");
        lore.add("§cvous sera coûteux de changer plus tard");
        inv.setItem(31, InventoryUtilities.setItemMetaLore(Material.BOOK, "§7Veuillez cliquer sur la nation à rejoindre", (short) 0, lore));
    }

}
