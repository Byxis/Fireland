package fr.byxis.fireland.utilities;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class ListUtilities {

    public static Collection<String> tabList(String arg, String defaultText, Material[] values)
    {
        ArrayList<String> l = new ArrayList();
        if (arg.equalsIgnoreCase(""))
        {
            l.add(defaultText);
        }
        for (Object str : values) {
            if (str.toString().toLowerCase().startsWith(arg.toLowerCase()))
            {
                l.add(str.toString());
            }
        }
        return l;
    }

    public static Collection<String> tabList(String arg, String defaultText, ArrayList<String> list)
    {
        ArrayList<String> l = new ArrayList();
        if (arg.equalsIgnoreCase(""))
        {
            l.add(defaultText);
        }
        for (Object str : list) {
            if (str.toString().toLowerCase().startsWith(arg.toLowerCase()))
            {
                l.add(str.toString());
            }
        }
        return l;
    }

    public static Collection<String> tabList(String arg, String defaultText, Set<String> list)
    {
        ArrayList<String> l = new ArrayList();
        if (arg.equalsIgnoreCase(""))
        {
            l.add(defaultText);
        }
        for (Object str : list) {
            if (str.toString().toLowerCase().startsWith(arg.toLowerCase()))
            {
                l.add(str.toString());
            }
        }
        return l;
    }

    public static Collection<String> tabList(String arg, String defaultText, Collection<? extends Player> list)
    {
        ArrayList<String> l = new ArrayList();
        if (arg.equalsIgnoreCase(""))
        {
            l.add(defaultText);
        }
        for (Player p : list) {
            if (p.getName().toLowerCase().startsWith(arg.toLowerCase()))
            {
                l.add(p.getName());
            }
        }
        return l;
    }

    public static Collection<String> tabList(String arg, String defaultText, Object[] collection)
    {
        ArrayList<String> l = new ArrayList();
        if (arg.equalsIgnoreCase(""))
        {
            l.add(defaultText);
        }
        for (Object str : collection) {
            if (str.toString().toLowerCase().startsWith(arg.toLowerCase()))
            {
                l.add(str.toString());
            }
        }
        return l;
    }

    public static ArrayList<String> tabList(String arg, String defaultText, HashMap set)
    {
        ArrayList<String> l = new ArrayList();
        if (arg.equalsIgnoreCase(""))
        {
            l.add(defaultText);
        }
        for (Object str : set.keySet()) {
            if (str.toString().toLowerCase().startsWith(arg.toLowerCase()))
            {
                l.add(str.toString());
            }
        }
        return l;
    }

}
