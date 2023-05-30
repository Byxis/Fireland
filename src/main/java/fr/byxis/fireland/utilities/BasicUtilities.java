package fr.byxis.fireland.utilities;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BasicUtilities {

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
}
