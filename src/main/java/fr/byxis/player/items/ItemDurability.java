package fr.byxis.player.items;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility to manage a "Durabilité" line in item lore.
 * Expected lore second line example (with colors stripped): "Durabilité : 70%"
 */
public class ItemDurability
{
    private static final Pattern PERCENT_PATTERN = Pattern.compile("([0-9]+(?:[.,][0-9]+)?)\\s*%");

    /**
     * Decrease durability by amount (percent). Updates lore.
     * @return true if the item is "broken" (durability <= 0), false otherwise.
     */
    public static boolean remLoreDurability(ItemStack item, float amount)
    {
        if (item == null || amount <= 0f)
        {
            return false;
        }

        float current = getDurability(item);
        float next = Math.max(0f, current - amount);
        setLoreDurability(item, next);

        return next <= 0f;
    }

    /**
     * Sets the lore durability line to the provided amount (0..100).
     */
    public static void setLoreDurability(ItemStack item, float amount)
    {
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.getLore();
        List<String> newLore = new ArrayList<>();

        String firstLine = "";
        if (lore != null && !lore.isEmpty())
        {
            firstLine = lore.get(0);
        }

        newLore.add(firstLine);

        float clamped = Math.max(0f, Math.min(100f, amount));
        String formatted = String.format(Locale.ROOT, "%.1f", clamped).replaceAll("\\.?0+$", ""); // drop .0
        newLore.add("§8Durabilité : §7" + formatted + "%");

        meta.setLore(newLore);
        item.setItemMeta(meta);
    }

    /**
     * Parse the durability from the item lore. If no valid lore found returns 100f.
     */
    public static float getDurability(ItemStack item)
    {
        if (item == null) return 100f;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 100f;

        List<String> lore = meta.getLore();
        if (lore == null || lore.size() <= 1) return 100f;

        String line = ChatColor.stripColor(lore.get(1));
        Float parsed = parsePercent(line);
        if (parsed == null) return 100f;

        // round to 1 decimal
        float rounded = Math.round(parsed * 10f) / 10f;
        return rounded;
    }

    /**
     * Compute new durability value without modifying the item.
     */
    private static float removeDurability(ItemStack item, float amount)
    {
        float current = getDurability(item);
        float next = Math.max(0f, current - amount);
        return Math.round(next * 10f) / 10f;
    }

    private static Float parsePercent(String text)
    {
        if (text == null) return null;
        Matcher m = PERCENT_PATTERN.matcher(text);
        if (!m.find()) return null;
        String num = m.group(1).replace(',', '.');
        try
        {
            return Float.parseFloat(num);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }
}