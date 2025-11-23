package fr.byxis.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedFly implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args)
    {

        if (sender instanceof Player player)
        {
            float speed = Float.parseFloat(args[0]);
            if (player.getAllowFlight())
            {
                if (speed <= 10.0f)
                {
                    player.sendMessage("§6Votre vitesse est de " + speed + " !");
                    player.setFlying(true);
                    player.setFlySpeed(speed * 0.1f);
                }
                else
                {
                    player.sendMessage("§6La vitesse ne peux pas être supérieur à 10 !");
                }
                return true;
            }
        }
        return false;
    }
}
