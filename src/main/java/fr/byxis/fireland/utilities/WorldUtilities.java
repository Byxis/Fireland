package fr.byxis.fireland.utilities;

import org.bukkit.Location;

public class WorldUtilities {

    public static Vector2 getPrimaryAxe(int x, int y)
    {
        if (x * x > y * y)
        {
            if (x > 0)
            {
                return new Vector2(1000,0);
            }
            else
            {
                return new Vector2(-1000,0);
            }
        }
        else
        {
            if (y > 0)
            {
                return new Vector2(0,1000);
            }
            else
            {
                return new Vector2(0,-1000);
            }
        }
    }

    public static double getCosAngleFromTwoVector2(Vector2 a, Vector2 b)
    {
        double produitScalaire = (a.x * b.x) + (a.y * b.y);
        double autre = Math.sqrt(Math.pow(a.x, 2) + Math.pow(a.y, 2)) * Math.sqrt(Math.pow(b.x, 2) + Math.pow(b.y, 2));
        return  produitScalaire/autre;
    }

    public static int getWorldBorderDistance(Location loc, Location loc2)
    {
        Vector2 player = new Vector2(loc.getX(), loc.getZ());
        Vector2 primary = getPrimaryAxe(loc.getBlockX(), loc.getBlockZ());
        loc.setY(0);
        loc2.setY(0);
        double distance = loc.distance(loc2);
        double value = 0;
        if (primary.x == 0)
        {
            value = primary.y;
        }
        else
        {
            value = primary.x;
        }
        double calcul = 1000 - distance *getCosAngleFromTwoVector2(primary, player);
        if (calcul < 0)
        {
            calcul *= -1;
        }
        return (int) (calcul);
    }

}
