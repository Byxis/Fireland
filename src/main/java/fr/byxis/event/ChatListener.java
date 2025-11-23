package fr.byxis.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener
{

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e)
    {
        for (Player on : Bukkit.getServer().getOnlinePlayers())
        {
            if (e.getMessage().contains("@" + on.getName()))
            {
                e.setMessage(e.getMessage().replaceAll("@" + on.getName(), on.getName()));
            }

            if (e.getMessage().contains(on.getName()) && !on.getName().equals(e.getPlayer().getName()))
            {
                e.setMessage(e.getMessage().replaceAll(on.getName(), ChatColor.GOLD + "@" + ChatColor.RESET + "" + ChatColor.BOLD + ""
                        + ChatColor.YELLOW + on.getName() + ChatColor.RESET));
                on.playNote(on.getLocation(), Instrument.PIANO, Note.natural(1, Tone.A));
            }
        }

        if (e.getPlayer().hasPermission("fireland.chat.admin"))
        {
            if (e.getMessage().contains("@everyone"))
            {
                e.setMessage(e.getMessage().replaceAll("@everyone", "everyone"));
            }
            if (e.getMessage().contains("everyone"))
            {
                e.setMessage(e.getMessage().replaceAll("everyone", ChatColor.GOLD + "@" + ChatColor.RESET + "" + ChatColor.BOLD + ""
                        + ChatColor.YELLOW + "everyone " + ChatColor.RESET));
                for (Player on : Bukkit.getServer().getOnlinePlayers())
                {
                    on.playNote(on.getLocation(), Instrument.PIANO, Note.natural(1, Tone.A));
                }
            }
            // e.setMessage(e.getMessage().replaceAll("&", "§"));
        }
    }
}
