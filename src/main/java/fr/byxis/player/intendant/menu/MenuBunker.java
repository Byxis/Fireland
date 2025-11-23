package fr.byxis.player.intendant.menu;

import fr.byxis.faction.bunker.BunkerClass;
import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.faction.faction.FactionPlayerInformation;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuBunker
{

    public static void openBunker(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        BunkerClass bk = main.getBunkerManager().findBunkerEnteredByPlayer(p.getName());
        Inventory zoneInv = null;
        if (bk == null)
            zoneInv = Bukkit.createInventory(null, 27, "§8Menu du Bunker §a");
        else
            zoneInv = Bukkit.createInventory(null, 27, "§8Menu du Bunker§a - Nv. " + bk.getBunkerLevel());
        setBunkerItem(main, zoneInv, p, bk);
        p.openInventory(zoneInv);
    }

    private static void setBunkerItem(Fireland main, Inventory inv, Player p, BunkerClass bk)
    {

        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation pInfos = ff.getInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        if (bk != null && pInfos.getRole() == 2 && bk.getName().equalsIgnoreCase(pInfos.getFactionName()))
        {
            inv.setItem(10, InventoryUtilities.setItemMeta(Material.BARRIER, "§cQuitter le Bunker", (short) 0));
            List<String> lore = new ArrayList<String>();
            lore.add("§r§8Prix : §6" + bk.getAmeliorationPriceMoney() + "§r§f$§8 et §b" + bk.getAmeliorationPriceJetons() + "§f⛁");
            inv.setItem(12, InventoryUtilities.setItemMetaLore(Material.ANVIL, "§aAméliorer le bunker", (short) 0, lore));
            inv.setItem(14, InventoryUtilities.setItemMeta(Material.RABBIT_HIDE, "§dChanger de skin", (short) 0));
            inv.setItem(16, InventoryUtilities.setItemMeta(Material.MAP, "§eInviter un joueur", (short) 0));
        }
        else if (bk != null && pInfos.getRole() == 1 && bk.getName().equalsIgnoreCase(pInfos.getFactionName()))
        {
            inv.setItem(12, InventoryUtilities.setItemMeta(Material.BARRIER, "§cQuitter le Bunker", (short) 0));
            inv.setItem(14, InventoryUtilities.setItemMeta(Material.MAP, "§eInviter un joueur", (short) 0));
        }
        else
        {
            inv.setItem(13, InventoryUtilities.setItemMeta(Material.BARRIER, "§cQuitter le Bunker", (short) 0));
        }
    }

    public static void openInviteBunker(Fireland main, Player p, int page)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory zoneInv = Bukkit.createInventory(null, 54, "§8Inviter dans votre bunker");
        setInviteBunker(main, zoneInv, p, page);
        p.openInventory(zoneInv);
    }

    private static void setInviteBunker(Fireland main, Inventory inv, Player p, int page)
    {
        for (int i = 0; i < 9; i++)
        {
            inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }

        // TODO: si il y a bcp de joueurs, faire un meilleur systeme pour voir tous les
        // joueurs
        int i = 0;
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (!player.getName().equalsIgnoreCase(p.getName()))
            {
                inv.setItem(i, InventoryUtilities.getHead(player.getUniqueId(), "§eInviter " + player.getName()));
                i++;
            }
            if (i == 45)
            {
                break;
            }
        }
        inv.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour", (short) 0));
    }

    public static void openBunkerFood(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory inv = Bukkit.createInventory(null, 9, "§8Nourriture du bunker");
        setBunkerFoodItem(main, inv, p);
        p.openInventory(inv);
    }

    private static void setBunkerFoodItem(Fireland main, Inventory inv, Player p)
    {
        BunkerClass bk = main.getBunkerManager().findBunkerEnteredByPlayer(p.getName());

        for (int i = 0; i < 9; i++)
        {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(8, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cQuitter", (short) 1));
        if (bk != null)
        {
            int food = bk.getFoodAmount();
            List<String> lore = new ArrayList<String>();
            lore.add("§r§8Génère §7" + bk.getMaxFood() + "§8 steaks par jour");
            if (food > 0)
            {
                ItemStack beef = new ItemStack(Material.COOKED_BEEF, food);
                ItemMeta meta = beef.getItemMeta();
                meta.setDisplayName("§7Steak");
                meta.setLore(lore);
                beef.setItemMeta(meta);

                inv.setItem(0, beef);
            }
            else
            {
                inv.setItem(0, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§cAucun steak en stock", (short) 0, lore));
            }
        }
    }

    public static void openBunkerMechanic(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory inv = Bukkit.createInventory(null, 9, "§8Atelier du bunker");
        setBunkerMechanicItem(main, inv, p);
        p.openInventory(inv);
    }

    private static void setBunkerMechanicItem(Fireland main, Inventory inv, Player p)
    {
        BunkerClass bk = main.getBunkerManager().findBunkerEnteredByPlayer(p.getName());

        for (int i = 0; i < 9; i++)
        {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(8, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cQuitter", (short) 1));
        if (bk != null)
        {
            int scrapNb = bk.getScrapAmount();
            List<String> lore = new ArrayList<String>();
            lore.add("§r§8Génère §7" + bk.getMaxScrap() + "§8 scraps par jour");
            if (scrapNb > 0)
            {
                ItemStack scrap = new ItemStack(Material.NETHERITE_SCRAP, scrapNb);
                ItemMeta meta = scrap.getItemMeta();
                meta.setDisplayName("§7Scrap");
                meta.setLore(lore);
                scrap.setItemMeta(meta);

                inv.setItem(0, scrap);
            }
            else
            {
                inv.setItem(0, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§cAucun scrap en stock", (short) 0, lore));
            }

            int powderNb = bk.getPowderAmount();
            lore = new ArrayList<String>();
            lore.add("§r§8Génère §7" + bk.getMaxPowder() + "§8 poudre par jour");
            if (powderNb > 0)
            {
                ItemStack powder = new ItemStack(Material.GUNPOWDER, powderNb);
                ItemMeta meta = powder.getItemMeta();
                meta.setDisplayName("§7Poudre");
                meta.setLore(lore);
                powder.setItemMeta(meta);

                inv.setItem(1, powder);
            }
            else
            {
                inv.setItem(1, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§cAucune poudre en stock", (short) 0, lore));
            }

            int repairKitNb = bk.getRepairKitAmount();
            lore = new ArrayList<String>();
            lore.add("§r§8Génère §7" + bk.getMaxRepairKit() + "§8 kit de réparation par semaine");
            if (repairKitNb > 0)
            {
                ItemStack kit = new ItemStack(Material.IRON_INGOT, repairKitNb);
                ItemMeta meta = kit.getItemMeta();
                meta.setDisplayName("§7Kit de réparation");
                meta.setLore(lore);
                kit.setItemMeta(meta);

                inv.setItem(2, kit);
            }
            else
            {
                inv.setItem(2, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§cAucun kit de réparation en stock", (short) 0, lore));
            }
        }
    }

    public static void openBunkerAlchemy(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory inv = Bukkit.createInventory(null, 9, "§8Atelier d'Alchimie du bunker");
        setBunkerAlchemyItem(main, inv, p);
        p.openInventory(inv);
    }

    private static void setBunkerAlchemyItem(Fireland main, Inventory inv, Player p)
    {
        BunkerClass bk = main.getBunkerManager().findBunkerEnteredByPlayer(p.getName());

        for (int i = 0; i < 9; i++)
        {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(8, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cQuitter", (short) 1));
        if (bk != null)
        {
            int medsNb = bk.getMedsAmount();
            List<String> lore = new ArrayList<String>();
            lore.add("§r§8Génère §7" + bk.getMaxMeds() + "§8 médicaments par jour");
            if (medsNb > 0)
            {
                ItemStack meds = new ItemStack(Material.HONEYCOMB, medsNb);
                ItemMeta meta = meds.getItemMeta();
                meta.setDisplayName("§7Médicaments");
                meta.setLore(lore);
                meds.setItemMeta(meta);

                inv.setItem(0, meds);
            }
            else
            {
                inv.setItem(0, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§cAucun médicament en stock", (short) 0, lore));
            }

            int antidouleurNb = bk.getAntiDouleurAmount();
            lore = new ArrayList<String>();
            lore.add("§r§8Génère §7" + bk.getMaxAntiDouleur() + "§8 anti-douleurs par jour");
            if (antidouleurNb > 0)
            {
                ItemStack powder = new ItemStack(Material.WHEAT_SEEDS, antidouleurNb);
                ItemMeta meta = powder.getItemMeta();
                meta.setCustomModelData(104);
                meta.setDisplayName("§7Anti-Douleurs");
                meta.setLore(lore);
                powder.setItemMeta(meta);

                inv.setItem(1, powder);
            }
            else
            {
                inv.setItem(1, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§cAucun Anti-Douleur en stock", (short) 0, lore));
            }

            int serumNb = bk.getSerumAmount();
            lore = new ArrayList<String>();
            lore.add("§r§8Génère §7" + bk.getMaxSerum() + "§8 sérum du berserker par semaine");
            if (serumNb > 0)
            {
                ItemStack powder = new ItemStack(Material.WHEAT_SEEDS, serumNb);
                ItemMeta meta = powder.getItemMeta();
                meta.setCustomModelData(113);
                meta.setDisplayName("§7Sérum du Berserker");
                meta.setLore(lore);
                powder.setItemMeta(meta);

                inv.setItem(2, powder);
            }
            else
            {
                inv.setItem(2, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§cAucun sérum en stock", (short) 0, lore));
            }
        }
    }

    public static void openBunkerChest(Fireland main, Player p)
    {
        BunkerClass bk = main.getBunkerManager().findBunkerEnteredByPlayer(p.getName());
        Inventory zoneInv = null;
        zoneInv = Bukkit.createInventory(null, 54, "§8Stockage du bunker");
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation pInfos = ff.getInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        if (bk != null
                && (p.hasPermission("fireland.bunker.mod") || pInfos != null && pInfos.getFactionName().equalsIgnoreCase(bk.getName())))
        {
            InGameUtilities.playPlayerSound(p, Sound.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 1, 1);
            setBunkerChestItem(main, zoneInv, p, bk);
            p.openInventory(zoneInv);
        }
    }

    private static void setBunkerChestItem(Fireland main, Inventory inv, Player p, BunkerClass bk)
    {
        for (int i = 0; i < 9; i++)
        {
            inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(8 + 45, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cQuitter", (short) 1));

        if (bk.getBunkerLevel() >= 1)
        {
            inv.setItem(9, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 1", (short) 1));
        }
        else
        {
            inv.setItem(9, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 1", (short) 1));
        }

        if (bk.getBunkerLevel() >= 3)
        {
            inv.setItem(11, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 2", (short) 1));
        }
        else
        {
            inv.setItem(11, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 2", (short) 1));
        }

        if (bk.getBunkerLevel() >= 4)
        {
            inv.setItem(13, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 3", (short) 1));
        }
        else
        {
            inv.setItem(13, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 3", (short) 1));
        }

        if (bk.getBunkerLevel() >= 5)
        {
            inv.setItem(15, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 4", (short) 1));
        }
        else
        {
            inv.setItem(15, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 4", (short) 1));
        }

        if (bk.getBunkerLevel() >= 8)
        {
            inv.setItem(17, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 5", (short) 1));
            inv.setItem(27, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 6", (short) 1));
        }
        else
        {
            inv.setItem(17, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 5", (short) 1));
            inv.setItem(27, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 6", (short) 1));
        }

        if (bk.getBunkerLevel() >= 9)
        {
            inv.setItem(29, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 7", (short) 1));
            inv.setItem(31, InventoryUtilities.setItemMeta(Material.CHEST, "§aStockage 8", (short) 1));
        }
        else
        {
            inv.setItem(29, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 7", (short) 1));
            inv.setItem(31, InventoryUtilities.setItemMeta(Material.BARRIER, "§4Stockage 8", (short) 1));
        }

        if (bk.getBunkerLevel() >= 10)
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

    public static void openBunkerSkin(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        BunkerClass bk = main.getBunkerManager().findBunkerEnteredByPlayer(p.getName());
        Inventory zoneInv = null;
        zoneInv = Bukkit.createInventory(null, 54, "§8Skin de Bunker");
        setBunkerSkinItem(main, zoneInv, p, bk);
        p.openInventory(zoneInv);
    }

    private static void setBunkerSkinItem(Fireland main, Inventory inv, Player p, BunkerClass bk)
    {
        ArrayList<Material> possessed = new ArrayList<Material>();
        ArrayList<Material> locked = new ArrayList<Material>();
        Material current = null;
        for (Material mat : main.getBunkerManager().getBunkerSkins().keySet())
        {
            if (p.hasPermission("fireland.bunker.skin." + main.getBunkerManager().getBunkerSkins().get(mat)[1]))
            {
                if (main.getBunkerManager().getBunkerSkins().get(mat)[1].equalsIgnoreCase(bk.getSkin()))
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

        for (int i = 0; i < 9; i++)
        {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(9, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        inv.setItem(18, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        inv.setItem(27, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        inv.setItem(36, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        inv.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cQuitter", (short) 1));

        long delay = bk.getDelayForSkinChange();
        if (delay > 0)
        {
            inv.setItem(45, InventoryUtilities.setItemMeta(Material.BARRIER,
                    "§cVous pouvez changer de skin dans " + BasicUtilities.getStringTime(delay), (short) 1));
        }
        else
        {
            inv.setItem(45, InventoryUtilities.setItemMeta(Material.CLOCK, "§aVous pouvez changer de skin.", (short) 1));
        }
        if (current != null)
        {
            inv.setItem(0, InventoryUtilities.setItemMeta(current, main.getBunkerManager().getBunkerSkins().get(current)[0] + " §d(équipé)",
                    (short) 0));
        }
        if (!possessed.isEmpty())
        {
            for (int i = 0; i < 27 && i < possessed.size(); i++)
            {
                if (i >= 8)
                {
                    inv.setItem(i + 11, InventoryUtilities.setItemMeta(possessed.get(i),
                            main.getBunkerManager().getBunkerSkins().get(possessed.get(i))[0] + " §a(Possédé)", (short) 0));
                }
                else
                {
                    inv.setItem(i + 10, InventoryUtilities.setItemMeta(possessed.get(i),
                            main.getBunkerManager().getBunkerSkins().get(possessed.get(i))[0] + " §a(Possédé)", (short) 0));
                }
            }
        }
        if (!locked.isEmpty())
        {
            for (int i = 0; i < 27 && i < locked.size(); i++)
            {
                if (i >= 8)
                {
                    inv.setItem(i + 29, InventoryUtilities.setItemMeta(locked.get(i),
                            main.getBunkerManager().getBunkerSkins().get(locked.get(i))[0] + " §c(Verrouillé)", (short) 0));
                }
                else
                {
                    inv.setItem(i + 28, InventoryUtilities.setItemMeta(locked.get(i),
                            main.getBunkerManager().getBunkerSkins().get(locked.get(i))[0] + " §c(Verrouillé)", (short) 0));
                }
            }
        }
    }

}
