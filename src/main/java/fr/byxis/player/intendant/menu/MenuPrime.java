package fr.byxis.player.intendant.menu;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.player.primes.PrimeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static fr.byxis.fireland.utilities.InventoryUtilities.GetHead;
import static fr.byxis.player.primes.PrimeEvent.*;

public class MenuPrime {

    public static void OpenPrime(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory zoneInv = Bukkit.createInventory(null, 9*4, "§4Primes");
        SetPrimeItem(main, zoneInv, p);
        p.openInventory(zoneInv);
    }

    private static void SetPrimeItem(Fireland main, Inventory inv, Player p)
    {
        for(int i=0;i<9;i++)
        {
            inv.setItem(i + 9*3, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(12, InventoryUtilities.setItemMeta(Material.PLAYER_HEAD, "§cAjouter une prime", (short) 0));
        inv.setItem(14, InventoryUtilities.setItemMeta(Material.CHEST, "§eConsulter les primes", (short) 0));

        inv.setItem(35, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour à l'intendant", (short) 0));
    }

    public static void OpenPrimePlayer(Fireland main, Player p, int page)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory zoneInv = Bukkit.createInventory(null, 54, "§4Primes: Sélectionner un joueur");
        SetPrimePlayerItem(main, zoneInv, p, page);
        p.openInventory(zoneInv);
    }

    private static void SetPrimePlayerItem(Fireland main, Inventory inv, Player p, int page)
    {
        for(int i=0;i<9;i++)
        {
            inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }

        //TODO: si il y a bcp de joueurs, faire un meilleur systeme pour voir tous les joueurs
        int i = 0;
        for(Player player : Bukkit.getOnlinePlayers())
        {
            if(!player.getName().equalsIgnoreCase(p.getName()) || p.getName().equalsIgnoreCase("Byxis_"))
            {
                inv.setItem(i, GetHead(player.getUniqueId(), "§e"+player.getName()));
                i++;
            }
            if(i == 45)
            {
                break;
            }
        }
        inv.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour au menu des primes", (short) 0));
    }

    public static void OpenPrimeMoney(Fireland main, Player p, String player, int amount)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory zoneInv = Bukkit.createInventory(null, 9*4, "§4Primes: Ajouter un montant");
        SetPrimeMoneyItem(main, zoneInv, p, player, amount);
        p.openInventory(zoneInv);
    }

    private static void SetPrimeMoneyItem(Fireland main, Inventory inv, Player p, String player, int amount)
    {
        for(int i=0;i<9;i++)
        {
            inv.setItem(i + 9*3, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(10, InventoryUtilities.setItemMeta(Material.RAW_GOLD, "§cEnlever 1000$", (short) 0, 1));
        inv.setItem(11, InventoryUtilities.setItemMeta(Material.GOLD_INGOT, "§cEnlever 100$", (short) 0, 1));
        inv.setItem(12, InventoryUtilities.setItemMeta(Material.GOLD_NUGGET, "§cEnlever 10$", (short) 0, 1));
        List<String> lore = new ArrayList<>();
        lore.add("§8Montant: §6"+amount+" $");
        ItemStack item = GetHead(BasicUtilities.getUuid(player), "§e"+player);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(13, item);
        inv.setItem(14, InventoryUtilities.setItemMeta(Material.GOLD_NUGGET, "§eAjouter 10$", (short) 0));
        inv.setItem(15, InventoryUtilities.setItemMeta(Material.GOLD_INGOT, "§eAjouter 100$", (short) 0));
        inv.setItem(16, InventoryUtilities.setItemMeta(Material.RAW_GOLD, "§eAjouter 1000$", (short) 0));

        if(main.eco.getBalance(p) >= amount)
        {
            inv.setItem(27, InventoryUtilities.setItemMeta(Material.STRUCTURE_VOID, "§aAjouter la prime", (short) 0, 1));
        }
        else
        {
            inv.setItem(27, InventoryUtilities.setItemMeta(Material.BARRIER, "§cVous n'avez pas assez d'argent", (short) 0, 1));
        }

        inv.setItem(35, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cChanger de joueur", (short) 0));
    }

    public static void OpenPrimeList(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory zoneInv = Bukkit.createInventory(null, 54, "§4Primes disponibles");
        SetPrimeListItem(main, zoneInv, p);
        p.openInventory(zoneInv);
    }

    private static void SetPrimeListItem(Fireland main, Inventory inv, Player p)
    {
        for(int i=0;i<9;i++)
        {
            inv.setItem(i + 9*5, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        int i =0;
        for(String uuid : PrimeEvent.config.getConfig().getConfigurationSection("").getKeys(false))
        {
            ItemStack item = GetHead(UUID.fromString(uuid), "§r"+Bukkit.getPlayer(UUID.fromString(uuid)).getName());
            ItemMeta meta = item.getItemMeta();
            ArrayList<String> lore = new ArrayList<String>();
            lore.add("§8Valeur: §6"+getPrime(uuid)+"$");
            lore.add("§8Durée restante: §7"+BasicUtilities.getStringTime(getPrimeMaxDay()* 24 * 60 * 60 * 1000+getPrimeDate(uuid).getTime()-System.currentTimeMillis()));
            meta.setLore(lore);

            item.setItemMeta(meta);
            inv.setItem(i, item);
            i++;
        }

        inv.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour au menu des primes", (short) 0));
    }

}
