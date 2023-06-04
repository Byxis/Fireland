package fr.byxis.fireland.utilities;

import org.apache.commons.io.IOUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BasicUtilities {

    public static String getStringTime(long durationInMillis)
    {
        long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;
        long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

        return String.format("%02dh%02dmin%02ds", hour, minute, second);
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
    public static void playSound(Player p, String sound)
    {
        p.playSound(p.getLocation(), sound, 1, 1);
    }

    public static void playWorldSound(String worldName, Location loc, String sound, SoundCategory category, float vol, float pitch)
    {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            world.playSound(loc, "minecraft:"+sound, category, vol, pitch);
        }
    }

    public static void playPlayerSound(Player p, String sound, SoundCategory category, float vol, float pitch)
    {
        if (p != null) {
            p.playSound(p.getLocation(), "minecraft:"+sound, category, vol, pitch);
        }
    }

    public static void playPlayersSound(List<Player> players, String sound, SoundCategory category, float vol, float pitch)
    {
        if(players == null)
        {
            return;
        }
        for(Player p : players)
        {
            p.playSound(p.getLocation(), "minecraft:"+sound, category, vol, pitch);
        }
    }

    public static void sendMessageToAdmin(String msg) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if(p.hasPermission("group.admin"))
            {
                p.sendMessage(msg);
            }
        }
    }
    public static void sendPlayerInformation(Player p, String msg)
    {
        if(p == null)
        {
            return;
        }
        p.sendMessage("§6§lFireland§8§l >> §7"+msg);
    }
    public static void sendPlayerError(Player p, String msg)
    {
        if(p == null)
        {
            return;
        }
        p.sendMessage("§6§lFireland§8§l >> §c"+msg);
    }
    public static int generateInt(int borneInf, int borneSup){
        Random random = new Random();
        int nb;
        nb = borneInf+random.nextInt(borneSup-borneInf);
        return nb;
    }

    public static ItemStack GetHead(UUID uuid, String name)
    {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        headMeta.setDisplayName(name);
        head.setItemMeta(headMeta);
        return head;
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
