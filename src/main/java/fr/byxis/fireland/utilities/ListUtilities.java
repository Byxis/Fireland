package fr.byxis.fireland.utilities;

import java.util.*;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ListUtilities
{

    public static Collection<String> tabList(String arg, String defaultText, Material[] values)
    {
        ArrayList<String> l = new ArrayList();
        if (arg.equalsIgnoreCase(""))
        {
            l.add(defaultText);
        }
        for (Object str : values)
        {
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
        for (Object str : list)
        {
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
        for (Object str : list)
        {
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
        for (Player p : list)
        {
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
        for (Object str : collection)
        {
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
        for (Object str : set.keySet())
        {
            if (str.toString().toLowerCase().startsWith(arg.toLowerCase()))
            {
                l.add(str.toString());
            }
        }
        return l;
    }

    public static List<String> filterSuggestions(String input, List<String> options)
    {
        List<String> filtered = new ArrayList<>();
        String lowerInput = input.toLowerCase();

        for (String option : options)
        {
            if (option.toLowerCase().startsWith(lowerInput))
            {
                filtered.add(option);
            }
        }

        return filtered;
    }

    public static List<String> getOnlinePlayerNames()
    {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

}
