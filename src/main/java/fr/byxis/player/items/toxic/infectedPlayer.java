package fr.byxis.player.items.toxic;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.PermissionUtilities;
import org.bukkit.*;
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

import static org.spigotmc.SpigotConfig.config;

public class infectedPlayer implements Listener,CommandExecutor {

	private static Fireland main = null;

	private static FileConfiguration config = null;
	
	public infectedPlayer(Fireland main) {
		infectedPlayer.main = main;
		config = main.cfgm.getPlayerDB();
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity().getPlayer();
		assert p != null;
		if(isInfected(p) && e.getEntity().getKiller() == null)
		{
			e.setDeathMessage(p.getName() + " est mort due à son infection !");
		}
		setInfection(p, false);
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
				if(random && !p.isInvulnerable() /*&& config.getInt("safezone."+p.getName()+".time") > 0*/){

			        
			        if(!isInfected(p)) {
			        	setInfection(p, true);
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
		if(p.getItemInHand().getType() == Material.WHEAT_SEEDS && p.getItemInHand().getItemMeta().hasCustomModelData() && p.getItemInHand().getItemMeta().getCustomModelData() == 102) {

			
			if(isInfected(p)) {
				setInfection(p, false);
				InGameUtilities.playWorldSound(p.getLocation(), "gun.hud.seringue", SoundCategory.PLAYERS, 1, 1);
				
				p.sendMessage("§8Vous avez soigné votre infection !");
				if(!p.getGameMode().equals(GameMode.CREATIVE)) {
					p.getItemInHand().setAmount(p.getItemInHand().getAmount() -1);
				}
			}
		}
		else if(p.getItemInHand().getType() == Material.IRON_INGOT && p.getCooldown(Material.IRON_INGOT) <= 0) {
			PermissionUtilities.commandExecutor(p, "wm repair "+p.getName()+" INVENTORY", "*");
			p.getItemInHand().setAmount(p.getItemInHand().getAmount() -1);
			InGameUtilities.playWorldSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, SoundCategory.PLAYERS, 1, 2f);
			p.setCooldown(Material.IRON_INGOT, 20);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void friendInteraction(PlayerInteractEntityEvent e)
	{
		Player p = e.getPlayer();
		if(p.getItemInHand().getType() == Material.WHEAT_SEEDS && p.getItemInHand().getItemMeta().hasCustomModelData() && p.getItemInHand().getItemMeta().getCustomModelData() == 102) {

			
			if(!(e.getRightClicked() instanceof Player friend) || isInfected(p))
			{
				return;
			}
			if(isInfected(friend)) {
				setInfection(friend, false);

				InGameUtilities.playWorldSound(friend.getLocation(), "gun.hud.seringue", SoundCategory.PLAYERS, 1, 1);
				
				p.sendMessage("§8Vous avez soigné l'infection de "+friend.getName()+"!");
				friend.sendMessage("§8"+p.getName()+" a soigné votre infection !");
				if(!p.getGameMode().equals(GameMode.CREATIVE)) {
					p.getItemInHand().setAmount(p.getItemInHand().getAmount() -1);
				}
			}
		}
	}

	public static void setInfection(Player p, boolean state)
	{
		if(!state)
		{
			config.set("infected."+p.getUniqueId()+".time", 0);
		}
		config.set("infected."+p.getUniqueId()+".state", state);
		main.cfgm.savePlayerDB();
	}

	public static boolean isInfected(Player p)
	{
		return config.getBoolean("infected." + p.getUniqueId() + ".state");
	}

	public static int getTimeInfected(Player p)
	{
		return config.getInt("infected."+p.getUniqueId()+".time");
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if(sender instanceof Player) {
			if(cmd.getName().equalsIgnoreCase("infect")) {
				Player p = (Player) sender;
				FileConfiguration config = main.cfgm.getPlayerDB();
				if(args.length == 0) {
					p.sendMessage("§8Vous avez été infecté !");
					p.playSound(p.getLocation(), "minecraft:entity.infected.bite", 1, 1);
					setInfection(p, false);
					return true;
				}else if(args.length == 1){
					Player victim =  (Player) Bukkit.getOfflinePlayer(BasicUtilities.getUuid(args[0]));
					p.sendMessage("§8"+victim.getName()+" été infecté !");
					victim.sendMessage("§8Vous avez été infecté !");
					victim.sendTitle("§8Vous avez été infecté !", "");
					victim.playSound(victim.getLocation(), "minecraft:entity.infected.bite", 1, 1);
					setInfection(victim, false);
					
				}else {
					p.sendMessage("§cMauvaise formulation de la commande ! (/infect [player]");
				}
			} else if(cmd.getName().equalsIgnoreCase("cure")) {
				Player p = (Player) sender;
				FileConfiguration config = main.cfgm.getPlayerDB();
				if(args.length == 0) {
					p.sendMessage("§8Vous avez soigné votre infection !");
					setInfection(p, false);
					return true;
				}else if(args.length == 1){
					Player victim = (Player) Bukkit.getOfflinePlayer(BasicUtilities.getUuid(args[0]));
					p.sendMessage("§8"+victim.getName()+" été soigné !");
					victim.sendMessage("§8Vous avez été soigné !");
					setInfection(victim, false);
				}else {
					p.sendMessage("§cMauvaise formulation de la commande ! (/cure [player]");
				}
			}
		}
		return false;
	}

}
