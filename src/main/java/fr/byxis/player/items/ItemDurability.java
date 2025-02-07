package fr.byxis.player.items;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemDurability
{

    public static boolean remLoreDurability(ItemStack item, float amount)
    {
        if (getDurability(item) <= 0)
        {
            ItemMeta meta = item.getItemMeta();
            ArrayList lore = new ArrayList<>();
            lore.add(item.getLore().get(0));
            lore.add("§8Durabilité : §70%");
            item.setItemMeta(meta);
            return true;
        }
        else
        {
            ArrayList lore = new ArrayList<>();
            lore.add(item.getLore().get(0));
            lore.add("§8Durabilité : §7 " + removeDurability(item, amount) + "%");
            ItemMeta meta = item.getItemMeta();
            meta.setLore(lore);
            item.setItemMeta(meta);
            return false;
        }
    }

    public static void setLoreDurability(ItemStack item, float amount)
    {
        ItemMeta meta = item.getItemMeta();
        ArrayList lore = new ArrayList<>();
        lore.add(item.getLore().get(0));
        lore.add("§8Durabilité : §7 " + amount + "%");
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public static float getDurability(ItemStack item)
    {
        if (item.lore().size() <= 1)
        {
            return 100f;
        }
        String lore = ChatColor.stripColor(item.getLore().get(1));
        String[] parts = lore.split(" ");
        String dura = parts[2].split("%")[0];
        return (float) Math.round(Double.parseDouble(dura) * 10) /10;
    }

    private static float removeDurability(ItemStack item, float amount)
    {
        if (item.lore().size() <= 1)
        {
            return 100f;
        }
        String lore = ChatColor.stripColor(item.getLore().get(1));
        String[] parts = lore.split(" ");
        String dura = parts[2].split("%")[0];
        return (float) Math.round((Double.parseDouble(dura)-amount)* 10) /10;
    }
}
