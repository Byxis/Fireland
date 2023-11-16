package fr.byxis.fireland.utilities;

import org.bukkit.Material;
import org.bukkit.boss.BarColor;

public class BlockUtilities {

    public static Material getGlassPaneColor(String color)
    {
        return switch (color) {
            case "§0" -> Material.BLACK_STAINED_GLASS_PANE;
            case "§1" -> Material.BLUE_STAINED_GLASS_PANE;
            case "§2" -> Material.GREEN_STAINED_GLASS_PANE;
            case "§3" -> Material.CYAN_STAINED_GLASS_PANE;
            case "§4" -> Material.RED_STAINED_GLASS_PANE;
            case "§5" -> Material.PURPLE_STAINED_GLASS_PANE;
            case "§6" -> Material.ORANGE_STAINED_GLASS_PANE;
            case "§8" -> Material.GRAY_STAINED_GLASS_PANE;
            case "§a" -> Material.LIME_STAINED_GLASS_PANE;
            case "§b" -> Material.LIGHT_BLUE_STAINED_GLASS_PANE;
            case "§d" -> Material.MAGENTA_STAINED_GLASS_PANE;
            case "§e" -> Material.YELLOW_STAINED_GLASS_PANE;
            case "§r" -> Material.WHITE_STAINED_GLASS_PANE;
            default   -> Material.LIGHT_GRAY_STAINED_GLASS_PANE;
        };
    }

    public static Material getGlassBlockColor(String color)
    {
        return switch (color) {
            case "§0" -> Material.BLACK_STAINED_GLASS;
            case "§1" -> Material.BLUE_STAINED_GLASS;
            case "§2" -> Material.GREEN_STAINED_GLASS;
            case "§3" -> Material.CYAN_STAINED_GLASS;
            case "§4" -> Material.RED_STAINED_GLASS;
            case "§5" -> Material.PURPLE_STAINED_GLASS;
            case "§6" -> Material.ORANGE_STAINED_GLASS;
            case "§8" -> Material.GRAY_STAINED_GLASS;
            case "§a" -> Material.LIME_STAINED_GLASS;
            case "§b" -> Material.LIGHT_BLUE_STAINED_GLASS;
            case "§d" -> Material.MAGENTA_STAINED_GLASS;
            case "§e" -> Material.YELLOW_STAINED_GLASS;
            case "§r" -> Material.WHITE_STAINED_GLASS;
            default   -> Material.LIGHT_GRAY_STAINED_GLASS;
        };
    }

    public static Material getWoolColor(String color)
    {
        return switch (color) {
            case "§0" -> Material.BLACK_WOOL;
            case "§1" -> Material.BLUE_WOOL;
            case "§2" -> Material.GREEN_WOOL;
            case "§3" -> Material.CYAN_WOOL;
            case "§4" -> Material.RED_WOOL;
            case "§5" -> Material.PURPLE_WOOL;
            case "§6" -> Material.ORANGE_WOOL;
            case "§8" -> Material.GRAY_WOOL;
            case "§a" -> Material.LIME_WOOL;
            case "§b" -> Material.LIGHT_BLUE_WOOL;
            case "§d" -> Material.MAGENTA_WOOL;
            case "§e" -> Material.YELLOW_WOOL;
            case "§r" -> Material.WHITE_WOOL;
            default   -> Material.LIGHT_GRAY_WOOL;
        };
    }

    public static Material getBannerWallColor(String color)
    {
        return switch (color) {
            case "§0" -> Material.BLACK_WALL_BANNER;
            case "§1" -> Material.BLUE_WALL_BANNER;
            case "§2" -> Material.GREEN_WALL_BANNER;
            case "§3" -> Material.CYAN_WALL_BANNER;
            case "§4" -> Material.RED_WALL_BANNER;
            case "§5" -> Material.PURPLE_WALL_BANNER;
            case "§6" -> Material.ORANGE_WALL_BANNER;
            case "§8" -> Material.GRAY_WALL_BANNER;
            case "§a" -> Material.LIME_WALL_BANNER;
            case "§b" -> Material.LIGHT_BLUE_WALL_BANNER;
            case "§d" -> Material.MAGENTA_WALL_BANNER;
            case "§e" -> Material.YELLOW_WALL_BANNER;
            case "§r" -> Material.WHITE_WALL_BANNER;
            default   -> Material.LIGHT_GRAY_WALL_BANNER;
        };
    }

    public static Material getBannerColor(String color)
    {
        return switch (color) {
            case "§0" -> Material.BLACK_BANNER;
            case "§1" -> Material.BLUE_BANNER;
            case "§2" -> Material.GREEN_BANNER;
            case "§3" -> Material.CYAN_BANNER;
            case "§4" -> Material.RED_BANNER;
            case "§5" -> Material.PURPLE_BANNER;
            case "§6" -> Material.ORANGE_BANNER;
            case "§8" -> Material.GRAY_BANNER;
            case "§a" -> Material.LIME_BANNER;
            case "§b" -> Material.LIGHT_BLUE_BANNER;
            case "§d" -> Material.MAGENTA_BANNER;
            case "§e" -> Material.YELLOW_BANNER;
            case "§r" -> Material.WHITE_BANNER;
            default   -> Material.LIGHT_GRAY_BANNER;
        };
    }

    public static BarColor getBossBarColor(String color)
    {
        return switch (color) {
            case "§0" -> BarColor.PURPLE;
            case "§1" -> BarColor.BLUE;
            case "§2" -> BarColor.GREEN;
            case "§3" -> BarColor.BLUE;
            case "§4" -> BarColor.RED;
            case "§5" -> BarColor.PURPLE;
            case "§6" -> BarColor.RED;
            case "§8" -> BarColor.WHITE;
            case "§a" -> BarColor.GREEN;
            case "§b" -> BarColor.BLUE;
            case "§d" -> BarColor.PURPLE;
            case "§e" -> BarColor.YELLOW;
            case "§r" -> BarColor.WHITE;
            default   -> BarColor.WHITE;
        };
    }
}
