package fr.byxis.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player)
        {
            Player p = (Player) commandSender;
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + p.getName() + " [\"\",{\"text\":\"[Fireland]\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://discord.gg/GagAyGEbNF\"}},{\"text\":\" \",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://discord.gg/GagAyGEbNF\"}},{\"text\":\"Envie de partager votre expérience et vos impressions ? Besoin d'un membre du staff ? Rejoignez notre serveur Discord dès maintenant en cliquant sur ce message.\",\"color\":\"green\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://discord.gg/GagAyGEbNF\"}}]");
        }
        return false;
    }
}
