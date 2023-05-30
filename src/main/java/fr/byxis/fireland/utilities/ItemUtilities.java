package fr.byxis.fireland.utilities;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.List;

public class ItemUtilities {

    public static ItemStack setItemMeta(Material mat, String name, short dura) {
        ItemStack item = new ItemStack(mat);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        item.setDurability(dura);
        return item;
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

    public static ItemStack setItemCustomModelData(ItemStack i, int cmd)
    {
        ItemMeta im = i.getItemMeta();
        im.setCustomModelData(cmd);
        i.setItemMeta(im);
        return i;
    }
}
