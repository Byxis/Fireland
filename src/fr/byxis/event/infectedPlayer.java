package fr.byxis.event;

import fr.byxis.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class infectedPlayer implements Listener,CommandExecutor {

	private final Main main;
	
	public infectedPlayer(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity().getPlayer();
		assert p != null;
		FileConfiguration config = main.cfgm.getPlayerDB();
		if(config.getBoolean("infected."+p.getUniqueId()+".state"))
		{
			e.setDeathMessage(p.getName() + " est mort due à son infection !");
		}
		config.set("infected."+p.getUniqueId()+".state", false);
		config.set("infected."+p.getUniqueId()+".time", 0);
		main.cfgm.savePlayerDB();
		String cmd = "mm m spawn Infecte 1 world,"+p.getLocation().getX()+","+p.getLocation().getY()+","+p.getLocation().getZ();
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		p.playSound(p.getLocation(), "minecraft:gun.hud.death", 10, 1);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void playerHit(EntityDamageByEntityEvent  e) {
		Entity damaged = e.getEntity();
		Entity damager = e.getDamager();

		boolean random = Math.random() < 0.2;
		
		if(damaged instanceof Player p) {
			if(damager instanceof Zombie || damager instanceof Stray || damager instanceof IronGolem) {
				FileConfiguration config = main.cfgm.getPlayerDB();
				if(random && !p.isInvulnerable() /*&& config.getInt("safezone."+p.getName()+".time") > 0*/){

			        
			        if(!config.getBoolean("infected." + p.getUniqueId() + ".state")) {
			        	config.set("infected."+p.getUniqueId()+".state", true);
			        	main.cfgm.savePlayerDB();
						p.sendMessage("§8Vous avez été infecté ! Trouvez vite une seringue avant l'infection ne vous tue");
						p.sendTitle("§8Vous avez été infecté !", "");
						p.playSound(p.getLocation(), "minecraft:entity.infected.bite", 1, 1);
			        }
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void playerSoin(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(p.getItemInHand().getType() == Material.GHAST_TEAR) {
			
			FileConfiguration config = main.cfgm.getPlayerDB();
			
			if(config.getBoolean("infected." + p.getUniqueId() + ".state")) {
				config.set("infected."+p.getUniqueId()+".state", false);
				main.cfgm.savePlayerDB();
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound minecraft:gun.hud.seringue ambient "+p.getName()+" "+p.getLocation().getX()+" "+p.getLocation().getY()+" "+p.getLocation().getZ()+" 1");
				
				p.sendMessage("§8Vous avez soigné votre infection !");
				if(!p.getGameMode().equals(GameMode.CREATIVE)) {
					p.getItemInHand().setAmount(p.getItemInHand().getAmount() -1);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void friendInteraction(PlayerInteractEntityEvent e)
	{
		Player p = e.getPlayer();
		if(p.getItemInHand().getType() == Material.GHAST_TEAR) {
			FileConfiguration config = main.cfgm.getPlayerDB();
			
			if(!(e.getRightClicked() instanceof Player friend) || config.getBoolean("infected."+p.getUniqueId()+".state"))
			{
				return;
			}

			if(config.getBoolean("infected." + friend.getUniqueId() + ".state")) {
				config.set("infected."+friend.getUniqueId()+".state", false);
				main.cfgm.savePlayerDB();
				
				friend.getWorld().playSound(friend.getLocation(), "minecraft:gun.hud.seringue", 1, 1);
				
				p.sendMessage("§8Vous avez soigné l'infection de "+friend.getName()+"!");
				friend.sendMessage("§8"+p.getName()+" a soigné votre infection !");
				if(!p.getGameMode().equals(GameMode.CREATIVE)) {
					p.getItemInHand().setAmount(p.getItemInHand().getAmount() -1);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if(sender instanceof Player) {
			if(cmd.getName().equalsIgnoreCase("infect")) {
				Player p = (Player) sender;
				FileConfiguration config = main.cfgm.getPlayerDB();
				if(args.length == 0) {
					p.sendMessage("§8Vous avez été infécté !");
					config.set("infected."+p.getName()+".state", true);
					p.playSound(p.getLocation(), "minecraft:entity.infected.bite", 1, 1);
					main.cfgm.savePlayerDB();
					return true;
				}else if(args.length == 1 && Bukkit.getPlayer(args[0]) != null){
					p.sendMessage("§8"+args[0]+" été infécté !");
					Bukkit.getPlayer(args[0]).sendMessage("§8Vous avez été infecté !");
					Bukkit.getPlayer(args[0]).sendTitle("§8Vous avez été infecté !", "");
					Bukkit.getPlayer(args[0]).playSound(Bukkit.getPlayer(args[0]).getLocation(), "minecraft:entity.infected.bite", 1, 1);
					config.set("infected."+args[0]+".state", true);
					main.cfgm.savePlayerDB();
					
				}else {
					p.sendMessage("§cMauvaise formulation de la commande ! (/infect [online player]");
				}
			} else if(cmd.getName().equalsIgnoreCase("cure")) {
				Player p = (Player) sender;
				FileConfiguration config = main.cfgm.getPlayerDB();
				if(args.length == 0) {
					p.sendMessage("§8Vous avez soigné votre infection !");
					config.set("infected."+p.getName()+".state", false);
					config.set("infected."+p.getName()+".time", 0);
					main.cfgm.savePlayerDB();
					return true;
				}else if(args.length == 1){
					p.sendMessage("§8"+args[0]+" été soigné !");
					config.set("infected."+args[0]+".state", false);
					config.set("infected."+p.getName()+".time", 0);
					main.cfgm.savePlayerDB();
				}else {
					p.sendMessage("§cMauvaise formulation de la commande ! (/cure [player]");
				}
			}
		}
		return false;
	}

}
