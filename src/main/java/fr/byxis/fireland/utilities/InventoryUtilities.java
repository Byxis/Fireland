package fr.byxis.fireland.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class InventoryUtilities
{

    public static ItemStack setItemMeta(Material mat, String name, short dura)
    {
        ItemStack item = new ItemStack(mat);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        item.setDurability(dura);
        return item;
    }

    public static ItemStack setItemMeta(Material mat, String name)
    {
        ItemStack item = new ItemStack(mat);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack setItemMeta(Material mat, String name, short dura, int modelData)
    {
        ItemStack item = new ItemStack(mat);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(name);
        itemMeta.setCustomModelData(modelData);
        item.setItemMeta(itemMeta);
        item.setDurability(dura);
        return item;
    }

    public static void clickManager(InventoryClickEvent e)
    {
        if ((e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER)
                || e.getView().getTopInventory().getType() != InventoryType.PLAYER)
            e.setCancelled(true);
        if (e.getClick().isKeyboardClick() && e.getView().getPlayer().getInventory().getItem(e.getHotbarButton()) != null)
            e.setCancelled(true);
        /*
         * if (e.getClickedInventory() != e.getView().getTopInventory() ||
         * e.getClick().isKeyboardClick()) { e.setCancelled(true); } else { if
         * (e.isShiftClick()) { e.setCancelled(true); } }
         */
    }
    public static ItemStack setItemMetaLore(Material mat, String name, short dura, List<String> lore)
    {
        ItemStack item = new ItemStack(mat);

        if (mat.equals(Material.POTION))
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

        ItemMeta itemMeta;
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

    public static ItemStack setItemMetaLore(Material mat, String name, List<String> lore)
    {
        ItemStack item = new ItemStack(mat);

        if (mat.equals(Material.POTION))
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

        ItemMeta itemMeta;
        itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.values());
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        itemMeta.setUnbreakable(true);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack getHead(UUID uuid, String displayName)
    {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        headMeta.setDisplayName(displayName);
        head.setItemMeta(headMeta);
        return head;
    }

    public static ItemStack getHead(UUID uuid, String displayName, List<String> lore)
    {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        headMeta.setDisplayName(displayName);
        head.setItemMeta(headMeta);
        head.setLore(lore);
        return head;
    }

    public static ItemStack setItemCustomModelData(ItemStack i, int cmd)
    {
        ItemMeta im = i.getItemMeta();
        im.setCustomModelData(cmd);
        i.setItemMeta(im);
        return i;
    }

    public static ItemStack getBannisBanner(String name, List<String> lore)
    {
        ItemStack i = new ItemStack(Material.BLACK_BANNER, 1);
        BannerMeta m = (BannerMeta) i.getItemMeta();
        m.setDisplayName(name);
        m.setLore(lore);

        List<Pattern> patterns = new ArrayList<Pattern>();

        patterns.add(new Pattern(DyeColor.LIGHT_GRAY, PatternType.SMALL_STRIPES));
        patterns.add(new Pattern(DyeColor.GRAY, PatternType.FLOWER));
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.CURLY_BORDER));
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.TRIANGLE_TOP));
        patterns.add(new Pattern(DyeColor.LIGHT_GRAY, PatternType.SKULL));
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.CREEPER));

        m.setPatterns(patterns);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        m.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        i.setItemMeta(m);
        return i;
    }

    public static ItemStack getBannisBanner(String name)
    {
        ItemStack i = new ItemStack(Material.BLACK_BANNER, 1);
        BannerMeta m = (BannerMeta) i.getItemMeta();
        m.setDisplayName(name);

        List<Pattern> patterns = new ArrayList<Pattern>();

        patterns.add(new Pattern(DyeColor.LIGHT_GRAY, PatternType.SMALL_STRIPES));
        patterns.add(new Pattern(DyeColor.GRAY, PatternType.FLOWER));
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.CURLY_BORDER));
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.TRIANGLE_TOP));
        patterns.add(new Pattern(DyeColor.LIGHT_GRAY, PatternType.SKULL));
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.CREEPER));

        m.setPatterns(patterns);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        m.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        i.setItemMeta(m);
        return i;
    }

    public static ItemStack getEtatBanner(String name, List<String> lore)
    {
        ItemStack i = new ItemStack(Material.WHITE_BANNER, 1);
        BannerMeta m = (BannerMeta) i.getItemMeta();
        m.setDisplayName(name);
        m.setLore(lore);

        List<Pattern> patterns = new ArrayList<Pattern>();

        patterns.add(new Pattern(DyeColor.LIGHT_BLUE, PatternType.GRADIENT));
        patterns.add(new Pattern(DyeColor.WHITE, PatternType.CURLY_BORDER));
        patterns.add(new Pattern(DyeColor.WHITE, PatternType.CROSS));
        patterns.add(new Pattern(DyeColor.LIGHT_BLUE, PatternType.CIRCLE));
        patterns.add(new Pattern(DyeColor.LIGHT_BLUE, PatternType.FLOWER));
        patterns.add(new Pattern(DyeColor.WHITE, PatternType.TRIANGLE_TOP));

        m.setPatterns(patterns);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        m.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        i.setItemMeta(m);
        return i;
    }

    public static ItemStack getEtatBanner(String name)
    {
        ItemStack i = new ItemStack(Material.WHITE_BANNER, 1);
        BannerMeta m = (BannerMeta) i.getItemMeta();
        m.setDisplayName(name);

        List<Pattern> patterns = new ArrayList<Pattern>();

        patterns.add(new Pattern(DyeColor.LIGHT_BLUE, PatternType.GRADIENT));
        patterns.add(new Pattern(DyeColor.WHITE, PatternType.CURLY_BORDER));
        patterns.add(new Pattern(DyeColor.WHITE, PatternType.CROSS));
        patterns.add(new Pattern(DyeColor.LIGHT_BLUE, PatternType.CIRCLE));
        patterns.add(new Pattern(DyeColor.LIGHT_BLUE, PatternType.FLOWER));
        patterns.add(new Pattern(DyeColor.WHITE, PatternType.TRIANGLE_TOP));

        m.setPatterns(patterns);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        m.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        i.setItemMeta(m);
        return i;
    }

    public static ItemStack getNeutreBanner(String name, List<String> lore)
    {
        ItemStack i = new ItemStack(Material.WHITE_BANNER, 1);
        BannerMeta m = (BannerMeta) i.getItemMeta();
        m.setDisplayName(name);
        m.setLore(lore);
        i.setItemMeta(m);
        return i;
    }

    public static ItemStack getNeutreBanner(String name)
    {
        ItemStack i = new ItemStack(Material.WHITE_BANNER, 1);
        BannerMeta m = (BannerMeta) i.getItemMeta();
        m.setDisplayName(name);
        i.setItemMeta(m);
        return i;
    }

    public static ItemStack getWhiteGlassPane()
    {
        ItemStack i = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemMeta m = i.getItemMeta();

        m.setDisplayName(" ");
        i.setItemMeta(m);
        return i;
    }
}
