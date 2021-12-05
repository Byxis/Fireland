package fr.byxis.faction;

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
	    	 l.add("join");
	    	 l.add("invite");
	    	 l.add("leave");
			 l.add("disband");
			 l.add("demote");
			 l.add("promote");
			 l.add("money");
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
	     }
	     return l;
	}

}
