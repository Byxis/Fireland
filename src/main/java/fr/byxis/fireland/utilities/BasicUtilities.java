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

    private static final Random RANDOM = new Random();

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
    }

    public static UUID getUuid(String name) {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
        try {
            String uuidJson = IOUtils.toString(new URL(url));
            if (uuidJson.isEmpty()) return null;
            JSONObject uuidObject = (JSONObject) JSONValue.parseWithException(uuidJson);
            String[] uuid = uuidObject.get("id").toString().split("");
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
        return borneInf + RANDOM.nextInt(borneSup - borneInf);
    }

    public static List<String> listMaker(String str1, String str2, String str3, String str4)
    {
        List<String> lore = new ArrayList<>();
        if (!str1.isEmpty()) lore.add(str1);
        if (!str2.isEmpty()) lore.add(str2);
        if (!str3.isEmpty()) lore.add(str3);
        if (!str4.isEmpty()) lore.add(str4);
        return lore;
    }

    public static String getStringColor(Material mat)
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
