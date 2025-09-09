package fr.byxis.player.intendant.menu;

import fr.byxis.faction.essaim.EssaimFunctions;
import fr.byxis.faction.essaim.EssaimManager;
import fr.byxis.faction.essaim.essaimClass.EssaimClass;
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

    public static void openEssaimMenu(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory inv = Bukkit.createInventory(null, 54, "Calendrier des Essaims");
        setItemEssaimMenu(main, inv, p);
        p.openInventory(inv);
    }

    private static void setItemEssaimMenu(Fireland main, Inventory inv, Player p) {
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        for (int i = 9; i < 45; i += 9) {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            inv.setItem(i + 8, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }

        // Immeuble Infesté
        setEssaimItem(main, inv, p, "immeuble-infeste", Material.CYAN_TERRACOTTA, "§7Immeuble Infesté", 10, 1, "Vendredi 18h");

        // Usine Portuaire
        setEssaimItem(main, inv, p, "usine-portuaire", Material.BRICKS, "§7Usine", 11, 3, "Mercredi 16h");

        // Station de traitement des eaux
        setEssaimItem(main, inv, p, "station-de-traitement-des-eaux", Material.MOSSY_STONE_BRICKS, "§7Station de traitement des eaux", 12, 4, "Mercredi 18h");

        // Entrepôt Militaire
        setEssaimItem(main, inv, p, "entrepot-militaire", Material.END_STONE_BRICKS, "§7Entrepôt Militaire", 13, 5, "Mercredi 15h");

        // Épave du porte-avion
        setEssaimItem(main, inv, p, "none", Material.PRISMARINE_BRICKS, "§7Épave du porte-avion", 14, 6, "Bientôt");

        // Hangar Silencieux
        setEssaimItem(main, inv, p, "hangar-silencieux", Material.COAL_BLOCK, "§7Hangar Silencieux", 15, 6, "Samedi 15h");

        // Crypte
        setEssaimItem(main, inv, p, "crypte", Material.SKELETON_SKULL, "§7Crypte", 16, 6, "Lundi 17h");

        // Centrale Nucléaire
        setEssaimItem(main, inv, p, "centrale-nucleaire", Material.WAXED_WEATHERED_COPPER, "§7Centrale Nucléaire", 19, 7, "Vendredi 15h");

        // Station Pétrolière
        setEssaimItem(main, inv, p, "station-petroliere", Material.BLACKSTONE, "§7Station Pétrolière", 20, 7, "Samedi 19h");

        // Laboratoire
        setEssaimItem(main, inv, p, "laboratoire", Material.SMOOTH_QUARTZ, "§7Laboratoire", 21, 9, "Dimanche 18h");

        // Bunker de Latus
        setEssaimItem(main, inv, p, "bunker-de-latus", Material.DEEPSLATE_BRICKS, "§7Bunker de Latus", 22, 9, "Samedi 10h");

        // Antre de la Pondeuse
        setEssaimItem(main, inv, p, "antre-de-la-pondeuse", Material.SCULK, "§7Antre de la Pondeuse", 23, 12, "Dimanche 10h");

        // Les Abysses
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§4§lAbsence de données");
        inv.setItem(24, InventoryUtilities.setItemMetaLore(Material.SEA_LANTERN, "§7Les Abysses", (short) 1, lore));

        // Retour à l'intendant
        inv.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour à l'intendant", (short) 0));
    }

    private static void setEssaimItem(Fireland main, Inventory inv, Player p, String essaimName, Material material, String displayName, int slot, int difficulty, String schedule) {
        ArrayList<String> lore = new ArrayList<>();
        boolean isActive = main.getEssaimManager().isEssaimActive(essaimName);

        if (isActive) {
            EssaimClass essaimClass = EssaimManager.getActiveEssaims().get(essaimName);
            boolean isEventBased = essaimClass.isEvenBased();
            if (isEventBased) {
                if (EssaimFunctions.hasFinishedEvent(p, essaimClass))
                {
                    lore.add("§8Disponibilité : §aÉvènement déjà réalisé");
                }
                else
                {
                    lore.add("§8Disponibilité : §aOuvert §d(Événementiel)");
                }
            } else {
                lore.add("§8Disponibilité : §aOuvert");
            }
        } else {
            lore.add("§8Disponibilité : §7" + schedule);
        }

        lore.add("§8Difficulté : " + getDifficulty(difficulty));
        inv.setItem(slot, InventoryUtilities.setItemMetaLore(material, displayName, (short) 1, lore));
    }


    private static String getDifficulty(int i)
    {
        return switch (i)
        {
            case 1 -> "§a☠";
            case 2 -> "§a☠☠";
            case 3 -> "§a☠☠☠";
            case 4 -> "§e☠";
            case 5 -> "§e☠☠";
            case 6 -> "§e☠☠☠";
            case 7 -> "§c☠";
            case 8 -> "§c☠☠";
            case 9 -> "§c☠☠☠";
            case 10 -> "§4☠";
            case 11 -> "§4☠☠";
            case 12 -> "§4☠☠☠";
            default -> "";
        };
    }

}
