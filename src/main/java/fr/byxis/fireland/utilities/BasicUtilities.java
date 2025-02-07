package fr.byxis.fireland.utilities;

import org.apache.commons.io.IOUtils;
import org.bukkit.Material;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BasicUtilities {

    public static String getStringTime(long durationInMillis)
    {
        long second = 1000;
        long minute = 60 * second;
        long hour = 60 * minute;
        long day = 24 * hour;
        long month = 30 * day;
        long year = 12 * month;

        String[] units = {"année", "mois", "jour", "heure", "minute", "seconde"};
        long[] times = {year, month, day, hour, minute, second};

        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (int i = 0; i < times.length; i++) {
            if (durationInMillis >= times[i]) {
                long num = durationInMillis / times[i];
                durationInMillis %= times[i];
                if (num > 0) {
                    sb.append(num).append(units[i]);
                    if (num > 1 && !units[i].equals("mois")) {
                        sb.append("s");
                    }
                    sb.append(", ");
                    if (++count == 3) {
                        break;
                    }
                }
            }
        }

        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }

        return sb.toString();
        /*long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;
        long hour = (durationInMillis / (1000 * 60 * 60)) % 24;
        long day = (durationInMillis / (1000 * 60 * 60*24)%30);
        long month = (durationInMillis / (1000L * 60 * 60*24*30)%364);
        long year = (durationInMillis / (1000L * 60 * 60*24*30*364));
        if (year > 0)
        {
            return String.format("%2dan(s), %2dmois, %2djour(s).", year, month, day);
        }
        if (month > 0)
        {
            return String.format("%2dmois, %2djour(s), %2dheure(s)", month, day, hour);
        }
        if (day > 0)
        {
            return String.format("%2djour(s), %2dheure(s), %2dminute(s)", day, hour, minute);
        }
        else if (hour > 0)
        {
            return String.format("%2dheure(s), %2dminute(s) et %2dseconde(s).", year, hour, minute);
        }
        else if (minute > 0)
        {
            return String.format("%2dminute(s) et %2dseconde(s).", minute, second);
        }
        return String.format("%2dseconde(s).", second);*/
    }

    public static UUID getUuid(String name) {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
        try {
            @SuppressWarnings("deprecation")
            String UUIDJson = IOUtils.toString(new URL(url));
            if (UUIDJson.isEmpty()) return null;
            JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
            String[] uuid = UUIDObject.get("id").toString().split("");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < uuid.length; i++)
            {
                sb.append(uuid[i]);
                if (i == 7 | i == 11 || i == 15 || i == 19)
                {
                    sb.append("-");
                }
            }
            return UUID.fromString(sb.toString());
        } catch (IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int generateInt(int borneInf, int borneSup)
{
        Random random = new Random();
        int nb;
        nb = borneInf + random.nextInt(borneSup-borneInf);
        return nb;
    }

    public static List<String> listMaker(String str1, String str2, String str3, String str4)
{
        List<String> lore = new ArrayList<String>();
        if (str1 != "") lore.add(str1);
        if (str2 != "") lore.add(str2);
        if (str3 != "") lore.add(str3);
        if (str4 != "") lore.add(str4);
        return lore;
    }

    public static String GetStringColor(Material mat)
    {
        switch (mat)
        {
            case BLACK_STAINED_GLASS_PANE -> {
                return "§0";
            }
            case BLUE_STAINED_GLASS_PANE -> {
                return "§1";
            }
            case GREEN_STAINED_GLASS_PANE -> {
                return "§2";
            }
            case CYAN_STAINED_GLASS_PANE -> {
                return "§3";
            }
            case RED_STAINED_GLASS_PANE -> {
                return "§4";
            }
            case PURPLE_STAINED_GLASS_PANE -> {
                return "§5";
            }
            case ORANGE_STAINED_GLASS_PANE -> {
                return "§6";
            }
            case LIGHT_GRAY_STAINED_GLASS_PANE -> {
                return "§7";
            }
            case GRAY_STAINED_GLASS_PANE -> {
                return "§8";
            }
            case LIME_STAINED_GLASS_PANE -> {
                return "§a";
            }
            case LIGHT_BLUE_STAINED_GLASS_PANE -> {
                return "§b";
            }
            case MAGENTA_STAINED_GLASS_PANE -> {
                return "§d";
            }
            case YELLOW_STAINED_GLASS_PANE -> {
                return "§e";
            }
        }
        return "§7";

    }

}
