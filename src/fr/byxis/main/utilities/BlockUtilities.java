package fr.byxis.main.utilities;

import org.bukkit.Material;

public class BlockUtilities {

    public static Material getGlassPaneColor(String color)
    {
        if(color.equals("§0"))
        {
            return Material.BLACK_STAINED_GLASS_PANE;
        }
        else if(color.equals("§1"))
        {
            return Material.BLUE_STAINED_GLASS_PANE;//
        }
        else if(color.equals("§2"))
        {
            return Material.GREEN_STAINED_GLASS_PANE;//
        }
        else if(color.equals("§3"))
        {
            return Material.CYAN_STAINED_GLASS_PANE;//
        }
        else if(color.equals("§4"))
        {
            return Material.RED_STAINED_GLASS_PANE;//
        }
        else if(color.equals("§5"))
        {
            return Material.PURPLE_STAINED_GLASS_PANE;//
        }
        else if(color.equals("§6"))
        {
            return Material.ORANGE_STAINED_GLASS_PANE;//
        }
        else if(color.equals("§7"))
        {
            return Material.LIGHT_GRAY_STAINED_GLASS_PANE;
        }
        else if(color.equals("§8"))
        {
            return Material.GRAY_STAINED_GLASS_PANE;
        }
        else if(color.equals("§a"))
        {
            return Material.LIME_STAINED_GLASS_PANE;//
        }
        else if(color.equals("§b"))
        {
            return Material.LIGHT_BLUE_STAINED_GLASS_PANE;//
        }
        else if(color.equals("§d"))
        {
            return Material.MAGENTA_STAINED_GLASS_PANE;//
        }
        else if(color.equals("§e"))
        {
            return Material.YELLOW_STAINED_GLASS_PANE;//
        }
        else if(color.equals("§r"))
        {
            return Material.WHITE_STAINED_GLASS_PANE;//
        }
        return Material.LIGHT_GRAY_STAINED_GLASS_PANE;
    }

    public static Material getGlassBlockColor(String color)
    {
        if(color.equals("§0"))
        {
            return Material.BLACK_STAINED_GLASS;
        }
        else if(color.equals("§1"))
        {
            return Material.BLUE_STAINED_GLASS;//
        }
        else if(color.equals("§2"))
        {
            return Material.GREEN_STAINED_GLASS;//
        }
        else if(color.equals("§3"))
        {
            return Material.CYAN_STAINED_GLASS;//
        }
        else if(color.equals("§4"))
        {
            return Material.RED_STAINED_GLASS;//
        }
        else if(color.equals("§5"))
        {
            return Material.PURPLE_STAINED_GLASS;//
        }
        else if(color.equals("§6"))
        {
            return Material.ORANGE_STAINED_GLASS;//
        }
        else if(color.equals("§7"))
        {
            return Material.LIGHT_GRAY_STAINED_GLASS;
        }
        else if(color.equals("§8"))
        {
            return Material.GRAY_STAINED_GLASS;
        }
        else if(color.equals("§a"))
        {
            return Material.LIME_STAINED_GLASS;//
        }
        else if(color.equals("§b"))
        {
            return Material.LIGHT_BLUE_STAINED_GLASS;//
        }
        else if(color.equals("§d"))
        {
            return Material.MAGENTA_STAINED_GLASS;//
        }
        else if(color.equals("§e"))
        {
            return Material.YELLOW_STAINED_GLASS;//
        }
        else if(color.equals("§r"))
        {
            return Material.WHITE_STAINED_GLASS;//
        }
        return Material.LIGHT_GRAY_STAINED_GLASS;
    }

    public static Material getWoolColor(String color)
    {
        if(color.equals("§0"))
        {
            return Material.BLACK_WOOL;
        }
        else if(color.equals("§1"))
        {
            return Material.BLUE_WOOL;//
        }
        else if(color.equals("§2"))
        {
            return Material.GREEN_WOOL;//
        }
        else if(color.equals("§3"))
        {
            return Material.CYAN_WOOL;//
        }
        else if(color.equals("§4"))
        {
            return Material.RED_WOOL;//
        }
        else if(color.equals("§5"))
        {
            return Material.PURPLE_WOOL;//
        }
        else if(color.equals("§6"))
        {
            return Material.ORANGE_WOOL;//
        }
        else if(color.equals("§7"))
        {
            return Material.LIGHT_GRAY_WOOL;
        }
        else if(color.equals("§8"))
        {
            return Material.GRAY_WOOL;
        }
        else if(color.equals("§a"))
        {
            return Material.LIME_WOOL;//
        }
        else if(color.equals("§b"))
        {
            return Material.LIGHT_BLUE_WOOL;//
        }
        else if(color.equals("§d"))
        {
            return Material.MAGENTA_WOOL;//
        }
        else if(color.equals("§e"))
        {
            return Material.YELLOW_WOOL;//
        }
        else if(color.equals("§r"))
        {
            return Material.WHITE_WOOL;//
        }
        return Material.LIGHT_GRAY_WOOL;
    }

    public static Material getBannerWallColor(String color)
    {
        if(color.equals("§0"))
        {
            return Material.BLACK_WALL_BANNER;
        }
        else if(color.equals("§1"))
        {
            return Material.BLUE_WALL_BANNER;//
        }
        else if(color.equals("§2"))
        {
            return Material.GREEN_WALL_BANNER;//
        }
        else if(color.equals("§3"))
        {
            return Material.CYAN_WALL_BANNER;//
        }
        else if(color.equals("§4"))
        {
            return Material.RED_WALL_BANNER;//
        }
        else if(color.equals("§5"))
        {
            return Material.PURPLE_WALL_BANNER;//
        }
        else if(color.equals("§6"))
        {
            return Material.ORANGE_WALL_BANNER;//
        }
        else if(color.equals("§7"))
        {
            return Material.LIGHT_GRAY_WALL_BANNER;
        }
        else if(color.equals("§8"))
        {
            return Material.GRAY_WALL_BANNER;
        }
        else if(color.equals("§a"))
        {
            return Material.LIME_WALL_BANNER;//
        }
        else if(color.equals("§b"))
        {
            return Material.LIGHT_BLUE_WALL_BANNER;//
        }
        else if(color.equals("§d"))
        {
            return Material.MAGENTA_WALL_BANNER;//
        }
        else if(color.equals("§e"))
        {
            return Material.YELLOW_WALL_BANNER;//
        }
        else if(color.equals("§r"))
        {
            return Material.WHITE_WALL_BANNER;//
        }
        return Material.LIGHT_GRAY_WALL_BANNER;
    }
}
