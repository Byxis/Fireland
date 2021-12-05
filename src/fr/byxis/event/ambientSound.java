package fr.byxis.event;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import fr.byxis.main.ConfigManager;
import fr.byxis.main.Main;

public class ambientSound implements Listener, CommandExecutor {

	private Main main;
	
	public ambientSound(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			ConfigManager config = main.cfgm;
			if(config.getPlayerDB().getBoolean("ambientsound."+p.getName()))
			{
				config.getPlayerDB().set("ambientsound."+p.getName(), false);
				p.sendMessage("§8Les sons d'ambiance ont été désactivés !");
			}
			else
			{
				config.getPlayerDB().set("ambientsound."+p.getName(), true);
				p.sendMessage("§8Les sons d'ambiance ont été activés !");
			}
			config.savePlayerDB();
		}
		return false;
	}
	
	public void playSound()
	{
		Integer random = (int) (Math.random()*10);
		World world = main.getServer().getWorld("world");
	    long time = world.getTime();
		
		if((random < 1 && (time > 23000) || (time > 0 && time < 12500)) || (random <= 2 && (time > 12500 && time < 23000)))
		{
			for (Player p : main.getServer().getOnlinePlayers())
			{
				if(main.cfgm.getPlayerDB().getBoolean("ambientsound."+p.getName()))
				{
					p.playSound(p.getLocation(), "minecraft:ambient.cave", 1, 1);
				}
			}
		}
	}
}
