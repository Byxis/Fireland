package fr.byxis.fireland.utilities;

import fr.byxis.fireland.Fireland;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.fusesource.jansi.Ansi;

import java.util.HashMap;
import java.util.List;

public class InGameUtilities implements Listener {


    private static Fireland main;

    public static HashMap<Player, Boolean> playerMoving = new HashMap<Player, Boolean>();

    public InGameUtilities(Fireland fireland) {
        main = fireland;
    }

    public static void teleportPlayer(Player player, Location loc, int duration, String sound)
    {
        main.hashMapManager.addTeleporting(player.getUniqueId());
        player.playSound(player.getLocation(), "minecraft:"+sound, (float) 0.1, (float) 1);

        new BukkitRunnable() {
            private int i = -1;

            @Override
            public void run() {
                i++;
                if(getPlayerMoving(player)){
                    sendPlayerError(player,"Téléportation annulée !");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stopsound "+player.getName()+" * minecraft:"+sound);
                    main.hashMapManager.removeTeleporting(player.getUniqueId());
                    cancel();
                }
                else
                {
                    if((i%5 == 0 && i != duration) || i == duration-3 ||i  == duration-2 || i  == duration-1)
                    {
                        sendPlayerInformation(player,"Téléportation dans " +(duration-i)+" secondes");
                    }
                    if(i == duration)
                    {
                        sendPlayerInformation(player,"Téléportation...");
                        player.teleport(loc);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a times 20 100 20");
                        main.hashMapManager.removeTeleporting(player.getUniqueId());
                        cancel();
                    }
                }

            }
        }.runTaskTimer(main, 0L, 20L);
    }

    public static Boolean getPlayerMoving(Player player)
    {
        if(!playerMoving.containsKey(player))
        {
            return false;
        }
        return playerMoving.get(player);
    }

    public static void setPlayerMoving(Player player, Boolean moving)
    {
        if(!playerMoving.containsKey(player))
        {
            playerMoving.put(player, moving);
        }
        playerMoving.replace(player, moving);
    }

    public static void playWorldSound(Location loc, String sound, SoundCategory category, float vol, float pitch)
    {
        World world = Bukkit.getWorld(loc.getWorld().getName());
        world.playSound(loc, "minecraft:"+sound, category, vol, pitch);
    }

    public static void playWorldSound(Location loc, Sound sound, SoundCategory category, float vol, float pitch)
    {
        World world = Bukkit.getWorld(loc.getWorld().getName());
        world.playSound(loc, sound, category, vol, pitch);
    }

    public static void playPlayerSound(Player p, String sound, SoundCategory category, float vol, float pitch)
    {
        if (p != null) {
            p.playSound(p.getLocation(), "minecraft:"+sound, category, vol, pitch);
        }
    }
    public static void playPlayerSound(Player p, Sound sound, SoundCategory category, float vol, float pitch)
    {
        if (p != null) {
            p.playSound(p.getLocation(), sound, category, vol, pitch);
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
            playPlayerSound(p, sound, category, vol, pitch);
        }
    }
    public static void playPlayersSound(List<Player> players, Sound sound, SoundCategory category, float vol, float pitch)
    {
        if(players == null)
        {
            return;
        }
        for(Player p : players)
        {
            playPlayerSound(p, sound, category, vol, pitch);
        }
    }

    public static void playEveryoneSound(Sound sound, SoundCategory category, float vol, float pitch)
    {
        for(Player p : Bukkit.getOnlinePlayers())
        {
            playPlayerSound(p, sound, category, vol, pitch);
        }
    }

    public static void playEveryoneSound(String sound, SoundCategory category, float vol, float pitch)
    {
        for(Player p : Bukkit.getOnlinePlayers())
        {
            playPlayerSound(p, sound, category, vol, pitch);
        }
    }

    public static void playEveryoneSoundExcept(String sound, SoundCategory category, float vol, float pitch, World world)
    {
        for(Player p : Bukkit.getOnlinePlayers())
        {
            if(!p.getWorld().getName().equalsIgnoreCase(world.getName()))
            {
                playPlayerSound(p, sound, category, vol, pitch);
            }
        }
    }

    public static void playEveryoneTitle(String title, String subtitle, int i1, int i2, int i3)
    {
        for(Player p : Bukkit.getOnlinePlayers())
        {
            p.sendTitle(title, subtitle, i1, i2, i3);
        }
    }

    public static void playEveryoneTitleExcept(String title, String subtitle, int i1, int i2, int i3, World world)
    {
        for(Player p : Bukkit.getOnlinePlayers())
        {
            if(!p.getWorld().getName().equalsIgnoreCase(world.getName()))
                p.sendTitle(title, subtitle, i1, i2, i3);
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

    public static void sendPlayerSucces(Player p, String msg)
    {
        if(p == null)
        {
            return;
        }
        p.sendMessage("§6§lFireland§8§l >> §a"+msg);
    }

    public static void sendPlayerError(Player p, String msg)
    {
        if(p == null)
        {
            return;
        }
        p.sendMessage("§6§lFireland§8§l >> §c"+msg);
    }

    public static void debugp(Object txt)
    {
        if(Bukkit.getPlayer("Byxis_") != null && Bukkit.getPlayer("Byxis_").isOnline())
        {
            Bukkit.getPlayer("Byxis_").sendMessage("§c§lFireland§8§l >> §a"+txt);
        }
    }

    public static void debugConsole(String txt)
    {
        main.getLogger().info(Ansi.ansi().fg(Ansi.Color.GREEN).toString()+"Debug "+Ansi.ansi().fg(Ansi.Color.WHITE).toString()+">> "+txt);
    }

    public static void sendInteractivePlayerMessage(Player p, String msg, String cmd, String hover, ClickEvent.Action action)
    {
        TextComponent message = new TextComponent("§6§lFireland§8§l >> §7"+msg);
        message.setClickEvent(new ClickEvent(action, cmd));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        p.spigot().sendMessage(message);
    }
    @EventHandler
    public void PlayerMove(PlayerMoveEvent event)
    {
        if(event.hasChangedPosition())
        {
            if(!playerMoving.containsKey(event.getPlayer()))
            {
                playerMoving.put(event.getPlayer(), true);
            }
            else
            {
                playerMoving.replace(event.getPlayer(), true);
            }
        }
        else
        {
            if(!playerMoving.containsKey(event.getPlayer()))
            {
                playerMoving.put(event.getPlayer(), false);
            }
            else
            {
                playerMoving.replace(event.getPlayer(), false);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                playerMoving.replace(event.getPlayer(), false);
            }
        }.runTaskLater(main, 20L);
    }

    public static void sendEveryoneCustomText(String text)
    {
        for(Player p : Bukkit.getOnlinePlayers())
        {
            p.sendMessage(text);
        }
    }

    public static void sendEveryoneCustomText(String text, Player n)
    {
        for(Player p : Bukkit.getOnlinePlayers())
        {
            if(!n.getName().equalsIgnoreCase(p.getName()))
            {
                p.sendMessage(text);
            }
        }
    }

    public static ChatColor getStringColor(String color)
    {
        return switch (color) {
            case "§0" -> ChatColor.BLACK;
            case "§1" -> ChatColor.DARK_BLUE;
            case "§2" -> ChatColor.DARK_GREEN;
            case "§3" -> ChatColor.BLUE;
            case "§4" -> ChatColor.RED;
            case "§5" -> ChatColor.DARK_PURPLE;
            case "§6" -> ChatColor.GOLD;
            case "§8" -> ChatColor.DARK_GRAY;
            case "§a" -> ChatColor.GREEN;
            case "§b" -> ChatColor.AQUA;
            case "§d" -> ChatColor.LIGHT_PURPLE;
            case "§e" -> ChatColor.YELLOW;
            case "§r" -> ChatColor.WHITE;
            default   -> ChatColor.GRAY;
        };
    }
}
