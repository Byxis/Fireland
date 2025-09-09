package fr.byxis.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Rename implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player)
        {
            ItemStack current = player.getItemInHand();
            if (args.length >= 1)
            {
                StringBuilder hl = new StringBuilder();
                int i = 1;
                for (String part : args)
                {
                    
                    String part2 = part.replace('&', '§');
                    
                    if (i < args.length)
                        hl.append(part2).append(" ");
                    else
                        hl.append(part2);
                    i++;
                }
                ItemMeta itm = current.getItemMeta();
                if (itm != null) {
                    itm.setDisplayName("§r" + hl);
                }
                current.setItemMeta(itm);

                player.sendMessage("You have Rename this item to :" + hl + "§r.");
                
            }
            else
            {
                player.sendMessage("§cUsage : /Rename <name>");
            }
        }
        return false;
    }

}
