package fr.byxis.fireland.utilities;

import org.bukkit.ChatColor;

public class TextUtilities {

    public static String convertCleanToStorable(String text, String split)
    {
        text = ChatColor.stripColor(text);
        String[] nameList = text.split(split);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nameList.length; i++)
        {
            if (nameList[i].startsWith("("))
            {
                break;
            }
            else if (i != 0)
            {
                sb.append("-");
            }
            sb.append(nameList[i]);
        }
        return sb.toString().toLowerCase();
    }

    public static String convertStorableToClean(String text)
    {
        return (text.substring(0, 1).toUpperCase() + text.substring(1)).replaceAll("-", " ");
    }

}
