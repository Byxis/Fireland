package fr.byxis.fireland.utilities;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ListUtilities {

    public static Collection<String> tabList(String arg, String defaultText, Material[] values)
    {
        ArrayList<String> l = new ArrayList();
        if(arg.equalsIgnoreCase(""))
        {
            l.add(defaultText);
        }
        for (Object str : values) {
            if(str.toString().toLowerCase().startsWith(arg.toLowerCase()))
            {
                l.add(str.toString());
            }
        }
        return l;
    }

    public static Collection<String> tabList(String arg, String defaultText, Collection<String> collection)
    {
        ArrayList<String> l = new ArrayList();
        if(arg.equalsIgnoreCase(""))
        {
            l.add(defaultText);
        }
        for (Object str : collection) {
            if(str.toString().toLowerCase().startsWith(arg.toLowerCase()))
            {
                l.add(str.toString());
            }
        }
        return l;
    }

    public static Collection<String> tabList(String arg, String defaultText, Object[] collection)
    {
        ArrayList<String> l = new ArrayList();
        if(arg.equalsIgnoreCase(""))
        {
            l.add(defaultText);
        }
        for (Object str : collection) {
            if(str.toString().toLowerCase().startsWith(arg.toLowerCase()))
            {
                l.add(str.toString());
            }
        }
        return l;
    }

    public static ArrayList<String> tabList(String arg, String defaultText, HashMap set)
    {
        ArrayList<String> l = new ArrayList();
        if(arg.equalsIgnoreCase(""))
        {
            l.add(defaultText);
        }
        for (Object str : set.keySet()) {
            if(str.toString().toLowerCase().startsWith(arg.toLowerCase()))
            {
                l.add(str.toString());
            }
        }
        return l;
    }

}
