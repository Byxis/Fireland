package fr.byxis.fireland.utilities;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.UUID;

public class InventoryUtilities {

    public static ItemStack setItemMeta(Material mat, String name, short dura) {
        ItemStack item = new ItemStack(mat);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        item.setDurability(dura);
        return item;
    }

    public static void clickManager(InventoryClickEvent e)
    {
        if (e.getClickedInventory() == e.getView().getTopInventory() || e.getClick().isKeyboardClick()) {
            e.setCancelled(true);
        }
        else
        {
            if(e.isShiftClick())
            {
                e.setCancelled(true);
            }
        }
    }
    public static ItemStack setItemMetaLore(Material mat, String name, short dura, List<String> lore) {
        ItemStack item = new ItemStack(mat);

        ItemMeta itemMeta = item.getItemMeta();

        if(mat.equals(Material.POTION))
        {
            item = new ItemStack(Material.POTION, 1);
            ItemMeta meta = item.getItemMeta();
            PotionMeta pmeta = (PotionMeta) meta;
            PotionData pdata = new PotionData(PotionType.WATER);
            pmeta.setBasePotionData(pdata);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.setLore(null);
            item.setItemMeta(meta);
        }

        itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.values());
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        itemMeta.setUnbreakable(true);
        item.setItemMeta(itemMeta);
        item.setDurability(dura);
        return item;
    }

    public static ItemStack GetHead(UUID uuid, String displayName)
    {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        headMeta.setDisplayName(displayName);
        head.setItemMeta(headMeta);
        return head;
    }

    public static ItemStack setItemCustomModelData(ItemStack i, int cmd)
    {
        ItemMeta im = i.getItemMeta();
        im.setCustomModelData(cmd);
        i.setItemMeta(im);
        return i;
    }
}
