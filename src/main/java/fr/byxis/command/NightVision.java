package fr.byxis.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVision implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args)
    {
        if (sender instanceof Player && sender.hasPermission("fireland.command.n"))
        {
            Player p = (Player) sender;
            if (p.hasPotionEffect(PotionEffectType.NIGHT_VISION))
            {
                p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
            else
            {
                p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 2, true, false));
            }
        }
        return false;
    }
}
