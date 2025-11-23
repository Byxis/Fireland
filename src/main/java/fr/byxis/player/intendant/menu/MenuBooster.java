package fr.byxis.player.intendant.menu;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.jeton.JetonsCommandManager;
import fr.byxis.player.booster.BoosterClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuBooster
{

    public static void openBoosters(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory boosterInv = Bukkit.createInventory(null, 54, "§8Boosters");
        setBoostersItem(main, boosterInv, p);
        p.openInventory(boosterInv);
    }

    private static void setBoostersItem(Fireland main, Inventory inv, Player p)
    {
        for (int i = 0; i < 9; i++)
        {
            inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        JetonsCommandManager jeton = new JetonsCommandManager(main);

        if (main.getHashMapManager().getBooster() != null)
        {
            BoosterClass booster = main.getHashMapManager().getBooster();
            inv.setItem(0,
                    InventoryUtilities.setItemMetaLore(Material.LIME_WOOL, "§a§lUn Booster est actif !", (short) 0,
                            BasicUtilities.listMaker("§8Créé par " + ((Player) Bukkit.getPlayer(booster.getUuid())).getName(),
                                    "§8Expiration dans "
                                            + BasicUtilities.getStringTime(booster.getFinished().getTime() - System.currentTimeMillis()),
                                    "", "")));
        }
        else
        {
            inv.setItem(0, InventoryUtilities.setItemMetaLore(Material.RED_WOOL, "§cAucun Booster n'est actif.", (short) 0,
                    BasicUtilities.listMaker("", "", "", "")));
        }
        ItemStack head = InventoryUtilities.getHead(p.getUniqueId(), "§d" + p.getName());
        ItemMeta meta = head.getItemMeta();
        meta.setLore(BasicUtilities.listMaker("§8Jetons : §b" + jeton.getJetonsPlayer(p.getUniqueId()) + " §f⛁", "", "", ""));
        head.setItemMeta(meta);
        inv.setItem(8, head);

        inv.setItem(11,
                InventoryUtilities.setItemMetaLore(Material.PAPER, "§eBooster - Lvl. 1", (short) 0,
                        BasicUtilities.listMaker("§8Permet de gagner 0 à 1$ sur les zombies", "§8Donne 5% plus d'argent lors du kill",
                                "§8Donne 5% plus de loot dans les coffres.", "§8Donne 25% plus d'xp.")));
        inv.setItem(13,
                InventoryUtilities.setItemMetaLore(Material.PAPER, "§eBooster - Lvl. 2", (short) 0,
                        BasicUtilities.listMaker("§8Permet de gagner 0 à 2$ sur les zombies", "§8Donne 7.5% plus d'argent lors du kill",
                                "§8Donne 7.5% plus de loot dans les coffres.", "§8Donne 50% plus d'xp.")));
        inv.setItem(15,
                InventoryUtilities.setItemMetaLore(Material.PAPER, "§eBooster - Lvl. 3", (short) 0,
                        BasicUtilities.listMaker("§8Permet de gagner 0 à 3$ sur les zombies", "§8Donne 10% plus d'argent lors du kill",
                                "§8Donne 10% plus de loot dans les coffres.", "§8Donne 75% plus d'xp.")));

        inv.setItem(20, InventoryUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 1 - 1h", (short) 0,
                BasicUtilities.listMaker("§8Avantages: voir ci-dessus", "§8Coût : §b100 §f⛁", "", "")));
        inv.setItem(29, InventoryUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 1 - 3h", (short) 0,
                BasicUtilities.listMaker("§8Avantages: voir ci-dessus", "§8Coût : §b300 §f⛁", "", "")));
        inv.setItem(38, InventoryUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 1 - 5h", (short) 0,
                BasicUtilities.listMaker("§8Avantages: voir ci-dessus", "§8Coût : §b500 §f⛁", "", "")));

        inv.setItem(22, InventoryUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 2 - 1h", (short) 0,
                BasicUtilities.listMaker("§8Avantages: voir ci-dessus", "§8Coût : §b200 §f⛁", "", "")));
        inv.setItem(31, InventoryUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 2 - 3h", (short) 0,
                BasicUtilities.listMaker("§8Avantages: voir ci-dessus", "§8Coût : §b600 §f⛁", "", "")));
        inv.setItem(40, InventoryUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 2 - 5h", (short) 0,
                BasicUtilities.listMaker("§8Avantages: voir ci-dessus", "§8Coût : §b1000 §f⛁", "", "")));

        inv.setItem(24, InventoryUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 3 - 1h", (short) 0,
                BasicUtilities.listMaker("§8Avantages: voir ci-dessus", "§8Coût : §b300 §f⛁", "", "")));
        inv.setItem(33, InventoryUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 3 - 3h", (short) 0,
                BasicUtilities.listMaker("§8Avantages: voir ci-dessus", "§8Coût : §b900 §f⛁", "", "")));
        inv.setItem(42, InventoryUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 3 - 5h", (short) 0,
                BasicUtilities.listMaker("§8Avantages: voir ci-dessus", "§8Coût : §b1500 §f⛁", "", "")));

        inv.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour à l'intendant", (short) 0));
    }

}
