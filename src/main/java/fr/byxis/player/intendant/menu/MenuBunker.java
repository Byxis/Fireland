package fr.byxis.player.intendant.menu;

import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.faction.faction.FactionPlayerInformation;
import fr.byxis.faction.housing.BunkerClass;
import fr.byxis.faction.zone.zoneclass.FactionZoneInformation;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.BlockUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static fr.byxis.fireland.utilities.InGameUtilities.debugp;
import static fr.byxis.fireland.utilities.InventoryUtilities.GetHead;

public class MenuBunker {

    public static void OpenBunker(Fireland main, Player p) {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        BunkerClass bk = main.bunkerManager.FindBunkerEnteredByPlayer(p.getName());
        Inventory zoneInv = null;
        if (bk == null)
            zoneInv = Bukkit.createInventory(null, 27, "§8Menu du Bunker §a ");
        else
            zoneInv = Bukkit.createInventory(null, 27, "§8Menu du Bunker§a - Nv. " + bk.GetBunkerLevel());
        SetBunkerItem(main, zoneInv, p, bk);
        p.openInventory(zoneInv);
    }

    private static void SetBunkerItem(Fireland main, Inventory inv, Player p, BunkerClass bk) {

        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation pInfos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        if (bk != null && pInfos.getRole() == 2) {
            inv.setItem(10, InventoryUtilities.setItemMeta(Material.BARRIER, "§cQuitter le Bunker", (short) 0));
            List<String> lore = new ArrayList<String>();
            lore.add("§r§8Prix : §6" + bk.GetAmeliorationPriceMoney() + "§r$§f§8 et §b" + bk.GetAmeliorationPriceJetons() + "§f\u26c1");
            inv.setItem(12, InventoryUtilities.setItemMetaLore(Material.ANVIL, "§aAméliorer le bunker", (short) 0, lore));
            inv.setItem(14, InventoryUtilities.setItemMeta(Material.RABBIT_HIDE, "§dChanger de skin", (short) 0));
            inv.setItem(16, InventoryUtilities.setItemMeta(Material.MAP, "§eInviter un joueur", (short) 0));
        } else if (bk != null && pInfos.getRole() == 1) {
            inv.setItem(12, InventoryUtilities.setItemMeta(Material.BARRIER, "§cQuitter le Bunker", (short) 0));
            inv.setItem(14, InventoryUtilities.setItemMeta(Material.MAP, "§eInviter un joueur", (short) 0));
        } else {
            inv.setItem(13, InventoryUtilities.setItemMeta(Material.BARRIER, "§cQuitter le Bunker", (short) 0));
        }
    }

    public static void OpenInviteBunker(Fireland main, Player p, int page) {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory zoneInv = Bukkit.createInventory(null, 54, "§8Inviter dans votre bunker");
        SetInviteBunker(main, zoneInv, p, page);
        p.openInventory(zoneInv);
    }

    private static void SetInviteBunker(Fireland main, Inventory inv, Player p, int page) {
        for (int i = 0; i < 9; i++) {
            inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }

        //TODO: si il y a bcp de joueurs, faire un meilleur systeme pour voir tous les joueurs
        int i = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getName().equalsIgnoreCase(p.getName()) || p.getName().equalsIgnoreCase("Byxis_")) {
                inv.setItem(i, GetHead(player.getUniqueId(), "§eInviter " + player.getName()));
                i++;
            }
            if (i == 45) {
                break;
            }
        }
        inv.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour", (short) 0));
    }

    public static void OpenBunkerFood(Fireland main, Player p) {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory inv = Bukkit.createInventory(null, 9, "§8Nourriture du bunker");
        SetBunkerFoodItem(main, inv, p);
        p.openInventory(inv);
    }

    private static void SetBunkerFoodItem(Fireland main, Inventory inv, Player p) {
        BunkerClass bk = main.bunkerManager.FindBunkerEnteredByPlayer(p.getName());

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(8, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cQuitter", (short) 1));
        if (bk != null) {
            int food = bk.GetFoodAmount();
            List<String> lore = new ArrayList<String>();
            lore.add("§r§8Génère §7" + bk.GetMaxFood() + "§8 steaks par jour");
            if (food > 0) {
                ItemStack beef = new ItemStack(Material.COOKED_BEEF, food);
                ItemMeta meta = beef.getItemMeta();
                meta.setDisplayName("§7Steak");
                meta.setLore(lore);
                beef.setItemMeta(meta);

                inv.setItem(0, beef);
            } else {
                inv.setItem(0, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§cAucun steak en stock", (short) 0, lore));
            }
        }
    }

    public static void OpenBunkerMechanic(Fireland main, Player p) {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory inv = Bukkit.createInventory(null, 9, "§8Atelier du bunker");
        SetBunkerMechanicItem(main, inv, p);
        p.openInventory(inv);
    }

    private static void SetBunkerMechanicItem(Fireland main, Inventory inv, Player p) {
        BunkerClass bk = main.bunkerManager.FindBunkerEnteredByPlayer(p.getName());

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(8, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cQuitter", (short) 1));
        if (bk != null) {
            int scrapNb = bk.GetScrapAmount();
            List<String> lore = new ArrayList<String>();
            lore.add("§r§8Génère §7" + bk.GetMaxScrap() + "§8 scraps par jour");
            if (scrapNb > 0) {
                ItemStack scrap = new ItemStack(Material.NETHERITE_SCRAP, scrapNb);
                ItemMeta meta = scrap.getItemMeta();
                meta.setDisplayName("§7Scrap");
                meta.setLore(lore);
                scrap.setItemMeta(meta);

                inv.setItem(0, scrap);
            } else {
                inv.setItem(0, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§cAucun scrap en stock", (short) 0, lore));
            }

            int powderNb = bk.GetPowderAmount();
            lore = new ArrayList<String>();
            lore.add("§r§8Génère §7" + bk.GetMaxPowder() + "§8 poudre par jour");
            if (powderNb > 0) {
                ItemStack powder = new ItemStack(Material.GUNPOWDER, powderNb);
                ItemMeta meta = powder.getItemMeta();
                meta.setDisplayName("§7Poudre");
                meta.setLore(lore);
                powder.setItemMeta(meta);

                inv.setItem(1, powder);
            } else {
                inv.setItem(1, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§cAucune poudre en stock", (short) 0, lore));
            }

            int repairKitNb = bk.GetRepairKitAmount();
            lore = new ArrayList<String>();
            lore.add("§r§8Génère §7" + bk.GetMaxRepairKit() + "§8 kit de réparation par semaine");
            if (repairKitNb > 0) {
                ItemStack kit = new ItemStack(Material.IRON_INGOT, repairKitNb);
                ItemMeta meta = kit.getItemMeta();
                meta.setDisplayName("§7Kit de réparation");
                meta.setLore(lore);
                kit.setItemMeta(meta);

                inv.setItem(2, kit);
            } else {
                inv.setItem(2, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§cAucun kit de réparation en stock", (short) 0, lore));
            }
        }
    }

    public static void OpenBunkerAlchemy(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory inv = Bukkit.createInventory(null, 9, "§8Atelier d'Alchimie du bunker");
        SetBunkerAlchemyItem(main, inv, p);
        p.openInventory(inv);
    }

    private static void SetBunkerAlchemyItem(Fireland main, Inventory inv, Player p)
    {
        BunkerClass bk = main.bunkerManager.FindBunkerEnteredByPlayer(p.getName());

        for(int i = 0; i < 9; i++)
        {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(8, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cQuitter", (short) 1));
        if(bk != null)
        {
            int medsNb = bk.GetMedsAmount();
            List<String> lore = new ArrayList<String>();
            lore.add("§r§8Génère §7"+bk.GetMaxMeds()+"§8 médicaments par jour");
            if(medsNb > 0)
            {
                ItemStack meds = new ItemStack(Material.HONEYCOMB, medsNb);
                ItemMeta meta = meds.getItemMeta();
                meta.setDisplayName("§7Médicaments");
                meta.setLore(lore);
                meds.setItemMeta(meta);

                inv.setItem(0, meds);
            }
            else {
                inv.setItem(0, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§cAucun médicament en stock", (short) 0, lore));
            }

            int antidouleurNb = bk.GetAntiDouleurAmount();
            lore = new ArrayList<String>();
            lore.add("§r§8Génère §7"+bk.GetMaxAntiDouleur()+"§8 anti-douleurs par jour");
            if(antidouleurNb > 0)
            {
                ItemStack powder = new ItemStack(Material.WHEAT_SEEDS, antidouleurNb);
                ItemMeta meta = powder.getItemMeta();
                meta.setCustomModelData(104);
                meta.setDisplayName("§7Anti-Douleurs");
                meta.setLore(lore);
                powder.setItemMeta(meta);

                inv.setItem(1, powder);
            }
            else {
                inv.setItem(1, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§cAucun Anti-Douleur en stock", (short) 0, lore));
            }

            int serumNb = bk.GetSerumAmount();
            lore = new ArrayList<String>();
            lore.add("§r§8Génère §7"+bk.GetMaxSerum()+"§8 sérum du berserker par semaine");
            if(serumNb > 0)
            {
                ItemStack powder = new ItemStack(Material.WHEAT_SEEDS, serumNb);
                ItemMeta meta = powder.getItemMeta();
                meta.setCustomModelData(113);
                meta.setDisplayName("§7Sérum du Berserker");
                meta.setLore(lore);
                powder.setItemMeta(meta);

                inv.setItem(2, powder);
            }
            else {
                inv.setItem(2, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§cAucun sérum en stock", (short) 0, lore));
            }
        }
    }

    public static void OpenBunkerChest(Fireland main, Player p) {
        BunkerClass bk = main.bunkerManager.FindBunkerEnteredByPlayer(p.getName());
        Inventory zoneInv = null;
        zoneInv = Bukkit.createInventory(null, 54, "§8Stockage du bunker");
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation pInfos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        if(bk != null && (p.hasPermission("fireland.bunker.mod") || pInfos != null && pInfos.getFactionName().equalsIgnoreCase(bk.GetName())))
        {
            InGameUtilities.playPlayerSound(p, Sound.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 1, 1);
            SetBunkerChestItem(main, zoneInv, p, bk);
            p.openInventory(zoneInv);
        }
    }

    private static void SetBunkerChestItem(Fireland main, Inventory inv, Player p, BunkerClass bk) {
        for(int i = 0; i < 9; i++)
        {
            inv.setItem(i+45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(8+45, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cQuitter", (short) 1));

        if(bk.GetBunkerLevel() >= 1)
        {
            inv.setItem(9, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 1", (short) 1));
        }
        else
        {
            inv.setItem(9, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 1", (short) 1));
        }

        if(bk.GetBunkerLevel() >= 3)
        {
            inv.setItem(11, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 2", (short) 1));
        }
        else
        {
            inv.setItem(11, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 2", (short) 1));
        }

        if(bk.GetBunkerLevel() >= 4)
        {
            inv.setItem(13, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 3", (short) 1));
        }
        else
        {
            inv.setItem(13, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 3", (short) 1));
        }

        if(bk.GetBunkerLevel() >= 5)
        {
            inv.setItem(15, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 4", (short) 1));
        }
        else
        {
            inv.setItem(15, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 4", (short) 1));
        }

        if(bk.GetBunkerLevel() >= 8)
        {
            inv.setItem(17, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 5", (short) 1));
            inv.setItem(27, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 6", (short) 1));
        }
        else
        {
            inv.setItem(17, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 5", (short) 1));
            inv.setItem(27, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 6", (short) 1));
        }

        if(bk.GetBunkerLevel() >= 9)
        {
            inv.setItem(29, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 7", (short) 1));
            inv.setItem(31, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 8", (short) 1));
        }
        else
        {
            inv.setItem(29, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 7", (short) 1));
            inv.setItem(31, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 8", (short) 1));
        }

        if(bk.GetBunkerLevel() >= 10)
        {
            inv.setItem(33, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 9", (short) 1));
            inv.setItem(35, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 10", (short) 1));
        }
        else
        {
            inv.setItem(33, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 9", (short) 1));
            inv.setItem(35, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 10", (short) 1));
        }
    }

    public static void OpenBunkerSkin(Fireland main, Player p) {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        BunkerClass bk = main.bunkerManager.FindBunkerEnteredByPlayer(p.getName());
        Inventory zoneInv = null;
        zoneInv = Bukkit.createInventory(null, 54, "§8Skin de Bunker");
        SetBunkerSkinItem(main, zoneInv, p, bk);
        p.openInventory(zoneInv);
    }

    private static void SetBunkerSkinItem(Fireland main, Inventory inv, Player p, BunkerClass bk) {
        ArrayList<Material> possessed = new ArrayList<Material>();
        ArrayList<Material> locked = new ArrayList<Material>();
        Material current = null;
        for(Material mat : main.bunkerManager.GetBunkerSkins().keySet())
        {
            if(p.hasPermission("fireland.bunker.skin."+main.bunkerManager.GetBunkerSkins().get(mat)[1]))
            {
                if(main.bunkerManager.GetBunkerSkins().get(mat)[1].equalsIgnoreCase(bk.GetSkin()))
                {
                    current = mat;
                }
                else
                {
                    possessed.add(mat);
                }
            }
            else
            {
                locked.add(mat);
            }
        }

        for(int i = 0; i < 9; i++)
        {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            inv.setItem(i+45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(9, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        inv.setItem(18, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        inv.setItem(27, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        inv.setItem(36, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        inv.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cQuitter", (short) 1));

        inv.setItem(45, InventoryUtilities.setItemMeta(Material.CLOCK, "§aVous pouvez changer de skin.", (short) 1));
        if(current != null)
        {
            inv.setItem(0, InventoryUtilities.setItemMeta(current, main.bunkerManager.GetBunkerSkins().get(current)[0] + " §d(Équipé)", (short) 0));
        }
        if(!possessed.isEmpty())
        {
            for(int i = 0; i < 27 && i < possessed.size(); i++)
            {
                if(i >= 8)
                {
                    inv.setItem(i+11, InventoryUtilities.setItemMeta(possessed.get(i), main.bunkerManager.GetBunkerSkins().get(possessed.get(i))[0] + " §a(Possédé)", (short) 0));
                }
                else
                {
                    inv.setItem(i+10, InventoryUtilities.setItemMeta(possessed.get(i), main.bunkerManager.GetBunkerSkins().get(possessed.get(i))[0] + " §a(Possédé)", (short) 0));
                }
            }
        }
        if(!locked.isEmpty())
        {
            for(int i = 0; i < 27 && i < locked.size(); i++)
            {
                if(i >= 8)
                {
                    inv.setItem(i+29, InventoryUtilities.setItemMeta(locked.get(i), main.bunkerManager.GetBunkerSkins().get(locked.get(i))[0] + " §c(Verrouillé)", (short) 0));
                }
                else
                {
                    inv.setItem(i+28, InventoryUtilities.setItemMeta(locked.get(i), main.bunkerManager.GetBunkerSkins().get(locked.get(i))[0] + " §c(Verrouillé)", (short) 0));
                }
            }
        }
    }

}
