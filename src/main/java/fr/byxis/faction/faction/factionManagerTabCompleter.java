package fr.byxis.faction.faction;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class factionManagerTabCompleter implements TabCompleter {

	@Override
	 public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
	 {
		 List<String> l = new ArrayList<>();
	     if (args.length == 1)
	     {
			 l.add("info");
			 l.add("create");
	    	 l.add("join");
	    	 l.add("invite");
	    	 l.add("leave");
			 l.add("disband");
			 l.add("demote");
			 l.add("promote");
			 l.add("money");
			 l.add("upgrade");
			 l.add("kick");
	    	 return l;
	     }
	     else if(args.length == 2)
	     {
	    	 if (args[0].equalsIgnoreCase("invite")
					 || args[0].equalsIgnoreCase("promote")
					 || args[0].equalsIgnoreCase("demote"))
	    	 {
	    		 for(Player player : Bukkit.getServer().getOnlinePlayers())
	    		 {
	    		     l.add(player.getName());
	    		 }
	    		  return l;
	    	 }
			 else if (args[0].equalsIgnoreCase("withdraw") || args[0].equalsIgnoreCase("deposit"))
			 {
				 l.add("10");
				 l.add("20");
				 l.add("50");
				 l.add("100");
				 l.add("200");
				 l.add("500");
				 l.add("1000");
			 }
	     }
	     return l;
	}

}
