package fr.byxis.player.intendant.menu;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class MenuEssaim {

    public static void OpenEssaimMenu(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory inv = Bukkit.createInventory(null, 54, "Calendrier des Essaims");
        SetItemEssaimMenu(main, inv, p);
        p.openInventory(inv);
    }

    private static void SetItemEssaimMenu(Fireland main, Inventory inv, Player p)
    {
        for (int i = 0; i < 9; i++)
        {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        for (int i = 9; i < 45; i += 9)
        {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            inv.setItem(i + 8, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }

        ArrayList<String> lore = new ArrayList<>();
        if (main.essaimManager.isEssaimActive("immeuble-infeste"))
            lore.add("§8Disponibilité :§a Ouvert");
        else
            lore.add("§8Disponibilité :§7 Vendredi 18h");
        lore.add("§8Difficulté : " + getDifficulty(1));
        inv.setItem(10, InventoryUtilities.setItemMetaLore(Material.CYAN_TERRACOTTA, "§7Immeuble Infesté", (short) 1, lore));

        lore = new ArrayList<>();
        if (main.essaimManager.isEssaimActive("usine-portuaire"))
            lore.add("§8Disponibilité :§a Ouvert");
        else
            lore.add("§8Disponibilité :§7 Mercredi 16h");
        lore.add("§8Difficulté : " + getDifficulty(3));
        inv.setItem(11, InventoryUtilities.setItemMetaLore(Material.BRICKS, "§7Usine", (short) 1, lore));

        lore = new ArrayList<>();
        if (main.essaimManager.isEssaimActive("station-de-traitement-des-eaux"))
            lore.add("§8Disponibilité :§a Ouvert");
        else
            lore.add("§8Disponibilité :§7 Mercredi 18h");
        lore.add("§8Difficulté : " + getDifficulty(4));
        inv.setItem(12, InventoryUtilities.setItemMetaLore(Material.MOSSY_STONE_BRICKS, "§7Station de traitement des eaux", (short) 1, lore));

        lore = new ArrayList<>();
        if (main.essaimManager.isEssaimActive("entrepot-militaire"))
            lore.add("§8Disponibilité :§a Ouvert");
        else
            lore.add("§8Disponibilité :§7 Mercredi 15h");
        lore.add("§8Difficulté : " + getDifficulty(5));
        inv.setItem(13, InventoryUtilities.setItemMetaLore(Material.END_STONE_BRICKS, "§7Entrepôt Militaire", (short) 1, lore));

        lore = new ArrayList<>();
        if (main.essaimManager.isEssaimActive("none"))
            lore.add("§8Disponibilité :§a Ouvert");
        else
            lore.add("§8Disponibilité :§c Bientôt");
        lore.add("§8Difficulté : " + getDifficulty(6));
        inv.setItem(14, InventoryUtilities.setItemMetaLore(Material.PRISMARINE_BRICKS, "§7Epave du porte-avion", (short) 1, lore));

        lore = new ArrayList<>();
        if (main.essaimManager.isEssaimActive("hangar-silencieux"))
            lore.add("§8Disponibilité :§a Ouvert");
        else
            lore.add("§8Disponibilité :§7 Samedi 15h");
        lore.add("§8Difficulté : " + getDifficulty(6));
        inv.setItem(15, InventoryUtilities.setItemMetaLore(Material.COAL_BLOCK, "§7Hangar Silencieux", (short) 1, lore));

        lore = new ArrayList<>();
        if (main.essaimManager.isEssaimActive("crypte"))
            lore.add("§8Disponibilité :§a Ouvert");
        else
            lore.add("§8Disponibilité :§7 Lundi 17h");
        lore.add("§8Difficulté : " + getDifficulty(6));
        inv.setItem(16, InventoryUtilities.setItemMetaLore(Material.SKELETON_SKULL, "§7Crypte", (short) 1, lore));

        lore = new ArrayList<>();
        if (main.essaimManager.isEssaimActive("centrale-nucleaire"))
            lore.add("§8Disponibilité :§a Ouvert");
        else
            lore.add("§8Disponibilité :§7 Vendredi 15h");
        lore.add("§8Difficulté : " + getDifficulty(7));
        inv.setItem(19, InventoryUtilities.setItemMetaLore(Material.WAXED_WEATHERED_COPPER, "§7Centrale Nucléaire", (short) 1, lore));

        lore = new ArrayList<>();
        if (main.essaimManager.isEssaimActive("station-petroliere"))
            lore.add("§8Disponibilité :§a Ouvert");
        else
            lore.add("§8Disponibilité :§7 Samedi 19h");
        lore.add("§8Difficulté : " + getDifficulty(7));
        inv.setItem(20, InventoryUtilities.setItemMetaLore(Material.BLACKSTONE, "§7Station Pétrolière", (short) 1, lore));

        lore = new ArrayList<>();
        if (main.essaimManager.isEssaimActive("laboratoire"))
            lore.add("§8Disponibilité :§a Ouvert");
        else
            lore.add("§8Disponibilité :§7 Dimanche 18h");
        lore.add("§8Difficulté : " + getDifficulty(9));
        inv.setItem(21, InventoryUtilities.setItemMetaLore(Material.SMOOTH_QUARTZ, "§7Laboratoire", (short) 1, lore));

        lore = new ArrayList<>();
        if (main.essaimManager.isEssaimActive("bunker-de-latus"))
            lore.add("§8Disponibilité :§a Ouvert");
        else
            lore.add("§8Disponibilité :§7 Samedi 10h");
        lore.add("§8Difficulté : " + getDifficulty(9));
        inv.setItem(22, InventoryUtilities.setItemMetaLore(Material.DEEPSLATE_BRICKS, "§7Bunker de Latus", (short) 1, lore));

        lore = new ArrayList<>();
        if (main.essaimManager.isEssaimActive("antre-de-la-pondeuse"))
            lore.add("§8Disponibilité :§a Ouvert");
        else
            lore.add("§8Disponibilité :§7 Dimanche 10h");
        lore.add("§8Difficulté : " + getDifficulty(12));

        inv.setItem(23, InventoryUtilities.setItemMetaLore(Material.SCULK, "§7Antre de la Pondeuse", (short) 1, lore));
        lore = new ArrayList<>();
        lore.add("§4§lAbsence de données");
        inv.setItem(24, InventoryUtilities.setItemMetaLore(Material.SEA_LANTERN, "§7Les Abysses", (short) 1, lore));

        inv.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour à l'intendant", (short) 0));
    }

    private static String getDifficulty(int i)
    {
        return switch(i)
        {
            case 1 -> "§a\u2620";
            case 2 -> "§a\u2620\u2620";
            case 3 -> "§a\u2620\u2620\u2620";
            case 4 -> "§e\u2620";
            case 5 -> "§e\u2620\u2620";
            case 6 -> "§e\u2620\u2620\u2620";
            case 7 -> "§c\u2620";
            case 8 -> "§c\u2620\u2620";
            case 9 -> "§c\u2620\u2620\u2620";
            case 10-> "§4\u2620";
            case 11-> "§4\u2620\u2620";
            case 12-> "§4\u2620\u2620\u2620";
            default ->"";
        };
    }

}
