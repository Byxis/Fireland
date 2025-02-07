package fr.byxis.fireland.restart;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RestartCommand implements @Nullable CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player p && p.isOp() || commandSender instanceof ConsoleCommandSender)
        {
            if (strings.length != 1)
            {
                commandSender.sendMessage("§cUtilisation : /frestart <minutes/stop>");
            }
            try{
                int amount = Integer.parseInt(strings[0]) *60;
                commandSender.sendMessage("§4Vous avez planifié un redémarrage.");
                RestartManager.StartRestart(amount);
                return true;
            }
            catch (Exception e)
            {
                if (strings[0].equalsIgnoreCase("stop"))
                {
                    commandSender.sendMessage("§4Vous avez annulé le redémarrage planifié.");
                    RestartManager.StopRestart();
                    return true;
                }
            }
            commandSender.sendMessage("§cUtilisation : /frestart <minutes/stop>");
        }
        return false;
    }
}
