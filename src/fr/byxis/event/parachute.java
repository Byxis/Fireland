package fr.byxis.event;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.byxis.main.Main;

public class parachute implements Listener {
	
	public boolean parachute = false;
	
	private Main main;

    public parachute(Main main) {
        this.main = main;
    }
    
    @EventHandler
    public void rightClickEvent(PlayerInteractEvent e) {
    	FileConfiguration config = main.cfgm.getPlayerDB();
    	if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		if(e.getItem() != null) {
    			if(e.getItem().getType() == Material.POPPY || e.getItem().getType() == Material.DANDELION) {
    				config.set("parachute."+e.getPlayer().getUniqueId(), true);
    				e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), "minecraft:entity.bat.takeoff",  0.1f, 1);
    				main.cfgm.savePlayerDB();
        		
    				new BukkitRunnable() {

    					@Override
    					public void run() {
    						if(e.getItem() != null) {
    							if(e.getItem().getType() == Material.DANDELION) {
    								if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
    									config.set("parachute."+e.getPlayer().getUniqueId(), false);
    									main.cfgm.savePlayerDB();
    								}else {
    									config.set("parachute."+e.getPlayer().getUniqueId(), true);
    									main.cfgm.savePlayerDB();
    								}
    							}
    						}
    					}
    				}.runTaskLater(main, 20);
    			}
    		}
    	}
    	
    }

}
