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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InGameUtilities implements Listener {


    private static Fireland main;

    private static final Map<UUID, Boolean> PLAYER_MOVING = new ConcurrentHashMap<>();
    private static final Map<UUID, Date> PLAYER_COOLDOWN = new ConcurrentHashMap<>();

    public InGameUtilities(Fireland fireland) {
        main = fireland;
    }

    public static void setPlayerMoving(UUID playerId, boolean isMoving)
    {
        PLAYER_MOVING.put(playerId, isMoving);
    }

    public static boolean isPlayerMoving(UUID playerId)
    {
        return PLAYER_MOVING.getOrDefault(playerId, false);
    }

    public static void setPlayerCooldown(UUID playerId, Date time)
    {
        PLAYER_COOLDOWN.put(playerId, time);
    }

    public static void removePlayerCooldown(UUID playerId)
    {
        PLAYER_COOLDOWN.remove(playerId);
    }

    public static boolean hasPlayerCooldown(UUID playerId)
    {
        return PLAYER_COOLDOWN.containsKey(playerId) && PLAYER_COOLDOWN.get(playerId).after(new Date());
    }

    public static long getPlayerCooldown(UUID playerId)
    {
        return PLAYER_COOLDOWN.getOrDefault(playerId, null).getTime();
    }

    public static void teleportPlayer(Player player, Location loc, int duration, String sound)
    {
        main.getHashMapManager().addTeleporting(player.getUniqueId());
        player.playSound(player.getLocation(), "minecraft:" + sound, (float) 0.1, (float) 1);

        new BukkitRunnable() {
            private int i = -1;

            @Override
            public void run() {
                i++;
                if (isPlayerMoving(player.getUniqueId()))
                {
                    sendPlayerError(player, "Téléportation annulée !");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stopsound " + player.getName() + " * minecraft:" + sound);
                    main.getHashMapManager().removeTeleporting(player.getUniqueId());
                    cancel();
                }
                else
                {
                    if ((i % 5 == 0 && i != duration) || i == duration - 3 || i == duration - 2 || i == duration - 1)
                    {
                        sendPlayerInformation(player, "Téléportation dans " + (duration - i) + " secondes");
                    }
                    if (i == duration)
                    {
                        sendPlayerInformation(player, "Téléportation...");
                        player.teleport(loc);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a times 20 100 20");
                        main.getHashMapManager().removeTeleporting(player.getUniqueId());
                        cancel();
                    }
                }

            }
        }.runTaskTimer(main, 0L, 20L);
    }
    public static void teleportPlayer(Player player, Location loc, int duration, String sound, int cooldown)
    {
        if (hasPlayerCooldown(player.getUniqueId()))
        {
            InGameUtilities.sendPlayerError(player, "Vous êtes en cooldown. Vous pourrez vous téléporter dans " + BasicUtilities.getStringTime(new Date().getTime() - getPlayerCooldown(player.getUniqueId())));
            return;
        }
        PLAYER_COOLDOWN.remove(player.getUniqueId());
        player.playSound(player.getLocation(), "minecraft:" + sound, (float) 0.1, (float) 1);

        new BukkitRunnable() {
            private int i = -1;

            @Override
            public void run() {
                i++;
                if (isPlayerMoving(player.getUniqueId()))
                {
                    sendPlayerError(player, "Téléportation annulée !");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stopsound " + player.getName() + " * minecraft:" + sound);
                    main.getHashMapManager().removeTeleporting(player.getUniqueId());
                    cancel();
                }
                else
                {
                    if ((i % 5 == 0 && i != duration) || i == duration - 3 || i == duration - 2 || i == duration - 1)
                    {
                        sendPlayerInformation(player, "Téléportation dans " + (duration - i) + " secondes");
                    }
                    if (i == duration)
                    {
                        sendPlayerInformation(player, "Téléportation...");
                        player.teleport(loc);
                        PLAYER_COOLDOWN.put(player.getUniqueId(), new Date(System.currentTimeMillis() + cooldown * 1000L));
                        main.getHashMapManager().addTeleporting(player.getUniqueId());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a times 20 100 20");
                        main.getHashMapManager().removeTeleporting(player.getUniqueId());
                        cancel();
                    }
                }

            }
        }.runTaskTimer(main, 0L, 20L);
    }

    public static void playWorldSound(Location loc, String sound, SoundCategory category, float vol, float pitch)
    {
        World world = Bukkit.getWorld(loc.getWorld().getName());
        world.playSound(loc, "minecraft:" + sound, category, vol, pitch);
    }

    public static void playWorldSound(Location loc, Sound sound, SoundCategory category, float vol, float pitch)
    {
        World world = Bukkit.getWorld(loc.getWorld().getName());
        world.playSound(loc, sound, category, vol, pitch);
    }

    public static void playPlayerSound(Player p, String sound, SoundCategory category, float vol, float pitch)
    {
        if (p != null) {
            p.playSound(p.getLocation(), "minecraft:" + sound, category, vol, pitch);
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
        if (players == null)
        {
            return;
        }
        for (Player p : players)
        {
            playPlayerSound(p, sound, category, vol, pitch);
        }
    }
    public static void playPlayersSound(List<Player> players, Sound sound, SoundCategory category, float vol, float pitch)
    {
        if (players == null)
        {
            return;
        }
        for (Player p : players)
        {
            playPlayerSound(p, sound, category, vol, pitch);
        }
    }

    public static void playEveryoneSound(Sound sound, SoundCategory category, float vol, float pitch)
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            playPlayerSound(p, sound, category, vol, pitch);
        }
    }

    public static void playEveryoneSound(String sound, SoundCategory category, float vol, float pitch)
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            playPlayerSound(p, sound, category, vol, pitch);
        }
    }

    public static void playEveryoneSoundExcept(String sound, SoundCategory category, float vol, float pitch, World world)
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            if (!p.getWorld().getName().equalsIgnoreCase(world.getName()))
            {
                playPlayerSound(p, sound, category, vol, pitch);
            }
        }
    }

    public static void playEveryoneTitle(String title, String subtitle, int i1, int i2, int i3)
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            p.sendTitle(title, subtitle, i1, i2, i3);
        }
    }

    public static void playEveryoneTitleExcept(String title, String subtitle, int i1, int i2, int i3, World world)
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            if (!p.getWorld().getName().equalsIgnoreCase(world.getName()))
                p.sendTitle(title, subtitle, i1, i2, i3);
        }
    }

    public static void sendMessageToAdmin(String msg) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("group.admin"))
            {
                p.sendMessage(msg);
            }
        }
    }

    public static void sendPlayerInformation(Player p, String msg)
    {
        if (p == null)
        {
            return;
        }
        p.sendMessage("§6§lFireland§8§l >> §7 " + msg);
    }

    public static void sendPlayerSucces(Player p, String msg)
    {
        if (p == null)
        {
            return;
        }
        p.sendMessage("§6§lFireland§8§l >> §a " + msg);
    }

    public static void sendPlayerError(Player p, String msg)
    {
        if (p == null)
        {
            return;
        }
        p.sendMessage("§6§lFireland§8§l >> §c " + msg);
    }

    public static void debugp(Object txt)
    {
        Player p = Bukkit.getPlayer("Byxis_");
        if (p != null && p.isOnline())
        {
            p.sendMessage("§c§lFireland§8§l >> §a " + txt);
        }
    }

    public static void debugp(int code, Object txt)
    {
        Player p = Bukkit.getPlayer("Byxis_");
        if (p != null && p.isOnline())
        {
            p.sendMessage("§c[FBUG-§l " + code + "§r§c] >> §a " + txt);
        }
    }

    public static void debug(int _code, String _txt)
    {
        main.getLogger().info(Ansi.ansi().fg(Ansi.Color.GREEN).toString() + "Debug " + _code + " " + Ansi.ansi().fg(Ansi.Color.WHITE).toString() + ">> " + _txt);
    }

    public static void debug(String txt)
    {
        main.getLogger().info(Ansi.ansi().fg(Ansi.Color.GREEN).toString() + "Debug " + Ansi.ansi().fg(Ansi.Color.WHITE).toString() + ">> " + txt);
    }

    public static void sendInteractivePlayerMessage(Player p, String msg, String cmd, String hover, ClickEvent.Action action)
    {
        TextComponent message = new TextComponent("§6§lFireland§8§l >> §7 " + msg);
        message.setClickEvent(new ClickEvent(action, cmd));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        p.spigot().sendMessage(message);
    }

    public static void sendEveryoneCustomText(String text)
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            p.sendMessage(text);
        }
    }

    public static void sendEveryoneCustomText(String text, Player n)
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            if (!n.getName().equalsIgnoreCase(p.getName()))
            {
                p.sendMessage(text);
            }
        }
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent e)
    {
        setPlayerMoving(e.getPlayer().getUniqueId(), e.hasChangedPosition());

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                setPlayerMoving(e.getPlayer().getUniqueId(), false);
            }
        }.runTaskLater(main, 20L);
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
