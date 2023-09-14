package fr.byxis.fireland.utilities;

import org.apache.commons.io.IOUtils;
import org.bukkit.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BasicUtilities {

    public static String getStringTimeHour(long durationInMillis)
    {
        long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;
        long hour = (durationInMillis / (1000 * 60 * 60)) % 24;
        if(hour > 0)
        {
            return String.format("%02dheures, %02dminutes et %02dsecondes", hour, minute, second);
        }
        else if(minute > 0)
        {
            return String.format("%02dminutes et %02dsecondes", hour, minute, second);
        }
        return String.format("%02dsecondes", hour, minute, second);

    }
    public static String getStringTimeDay(long durationInMillis)
    {
        long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;
        long hour = (durationInMillis / (1000 * 60 * 60)) % 24;
        long day = (durationInMillis / (1000 * 60 * 60*24));
        if(day > 0)
        {
            return String.format("%02djours, %02dheures, %02dminutes et %02dsecondes.", day, hour, minute, second);
        }
        else if(hour > 0)
        {
            return String.format("%02dheures, %02dminutes et %02dsecondes", hour, minute, second);
        }
        else if(minute > 0)
        {
            return String.format("%02dminutes et %02dsecondes", hour, minute, second);
        }
        return String.format("%02dsecondes", hour, minute, second);
    }

    public static UUID getUuid(String name) {
        String url = "https://api.mojang.com/users/profiles/minecraft/"+name;
        try {
            @SuppressWarnings("deprecation")
            String UUIDJson = IOUtils.toString(new URL(url));
            if(UUIDJson.isEmpty()) return null;
            JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
            String[] uuid = UUIDObject.get("id").toString().split("");
            StringBuilder sb = new StringBuilder();
            for(int i =0; i<uuid.length; i++)
            {
                sb.append(uuid[i]);
                if(i == 7 | i == 11 || i == 15 || i == 19)
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

    public static int generateInt(int borneInf, int borneSup){
        Random random = new Random();
        int nb;
        nb = borneInf+random.nextInt(borneSup-borneInf);
        return nb;
    }

    public static List<String> listMaker(String str1, String str2, String str3, String str4){
        List<String> lore = new ArrayList<String>();
        if(str1 != "") lore.add(str1);
        if(str2 != "") lore.add(str2);
        if(str3 != "")lore.add(str3);
        if(str4 != "")lore.add(str4);
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
