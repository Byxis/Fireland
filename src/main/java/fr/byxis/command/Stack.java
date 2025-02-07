package fr.byxis.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Stack implements CommandExecutor
{

    @SuppressWarnings({ "deprecation"})
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if(sender instanceof Player)
        {
            Player p = (Player) sender;
            
            boolean ignoreMax = p.hasPermission("worldguard.stack.illegitimate");
            boolean ignoreDamaged = p.hasPermission("worldguard.stack.damaged");
           
            ItemStack[] items = p.getInventory().getContents();
            int len = items.length;
            
            int affected = 0;
               
            for (int i = 0; i < len; i++) {
                ItemStack item = items[i];
     
                // Avoid infinite stacks and stacks with durability
                if (item == null || item.getAmount() <= 0
                        || (!ignoreMax && item.getMaxStackSize() == 1)) {
                    continue;
                }
     
                int max = ignoreMax ? 64 : item.getMaxStackSize();
     
                if (item.getAmount() < max) {
                    int needed = max - item.getAmount(); // Number of needed items until max
     
                    // Find another stack of the same type
                    for (int j = i + 1; j < len; j++) {
                        ItemStack item2 = items[j];
     
                        // Avoid infinite stacks and stacks with durability
                        if (item2 == null || item2.getAmount() <= 0 || (!ignoreMax && item.getMaxStackSize() == 1)) {
                            continue;
                        }
     
                        // Same type?
                        // Blocks store their color in the damage value
                        if (item2.getType() == item.getType() && ((/*!ItemType.usesDamageValue(item.getType()) &&*/ ignoreDamaged)|| item.getDurability() == item2.getDurability()) && 
                        ((item.getItemMeta() == null && item2.getItemMeta() == null)
                        || (item.getItemMeta() != null &&
                        item.getItemMeta().equals(item2.getItemMeta())))) {
                            if (item2.getAmount() > needed) {
                                
                                item.setAmount(max);
                                item2.setAmount(item2.getAmount() - needed);
                                break;
                            // This stack will
                            } else {
                                items[j] = null;
                                item.setAmount(item.getAmount() + item2.getAmount());
                                needed = max - item.getAmount();
                            }
     
                            affected++;
                        }
                    }
                }
            }
     
            if (affected > 0) {
                p.getInventory().setContents(items);
            }
            p.sendMessage("§7Items compacted into stacks!");
        }
        return false;
    }
    
    /*
	 if (
	        	            		!(item.getType() == Material.PUMPKIN_SEEDS) ||
	        	            		!(item.getType() == Material.MELON_SEEDS) ||
	        	            		!(item.getType() == Material.GOLD_NUGGET) ||
	        	            		!(item.getType() == Material.PURPLE_DYE) ||
	        	            		!(item.getType() == Material.GRAY_DYE) ||
	        	            		!(item.getType() == Material.PINK_DYE) ||
	        	            		!(item.getType() == Material.LIGHT_BLUE_DYE) ||
	        	            		!(item.getType() == Material.INK_SAC) ||
	        	            		!(item.getType() == Material.LAPIS_LAZULI) ||
	        	            		!(item.getType() == Material.BLUE_DYE) ||
	        	            		!(item.getType() == Material.ORANGE_DYE) ||
	        	            		!(item.getType() == Material.LIME_DYE) ||
	        	            		!(item.getType() == Material.GREEN_DYE) ||
	        	            		!(item.getType() == Material.MAGENTA_DYE) ||
	        	            		!(item.getType() == Material.STICK) ||
	        	            		!(item.getType() == Material.YELLOW_DYE) ||
	        	            		!(item.getType() == Material.BRICK) ||
	        	            		!(item.getType() == Material.BROWN_DYE) ||
	        	            		!(item.getType() == Material.CHARCOAL) ||
	        	            		!(item.getType() == Material.BEETROOT_SEEDS) ||
	        	            		!(item.getType() == Material.LIGHT_GRAY_DYE) ||
	        	            		!(item.getType() == Material.MELON_SEEDS) ||
	        	            		!(item.getType() == Material.IRON_NUGGET) ||
	        	            		!(item.getType() == Material.CYAN_DYE)
	        	            )
	        	            {
	        	            	continue;
	        	            }
	 */

}
