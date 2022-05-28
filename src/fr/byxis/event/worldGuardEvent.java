	package fr.byxis.event;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.byxis.main.Main;
import net.raidstone.wgevents.events.RegionLeftEvent;
import net.raidstone.wgevents.events.RegionEnteredEvent;

public class worldGuardEvent implements Listener {
	
	private Main main;

	public worldGuardEvent(Main main) {
		this.main = main;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void leftedRegion(RegionLeftEvent e)
	{
		try {
			Player p = e.getPlayer();
			
			if(e.getRegionName().contains("sf"))
			{
				if(p.getGameMode() != GameMode.SPECTATOR || p.getGameMode() != GameMode.CREATIVE)
				{
					p.sendTitle("", "§cVous êtes invincible pendant 15 secondes");
					p.playSound(p.getLocation(), "minecraft:gun.hud.leaving_safezone", 1, 1);
					p.setInvulnerable(true);

					main.cfgm.getPlayerDB().set("safezone."+p.getName()+".time", 15);
					main.cfgm.getPlayerDB().set("safezone."+p.getName()+".state", false);
					main.cfgm.savePlayerDB();
				}
			}
		} catch (Exception e2) {
			// TODO: handle exception
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void joinedRegion(RegionEnteredEvent e)
	{
		
		try {
			Player p = e.getPlayer();
			
			if(e.getRegionName().equalsIgnoreCase("sf1") || e.getRegionName().equalsIgnoreCase("sf2") || e.getRegionName().equalsIgnoreCase("sf3") || e.getRegionName().equalsIgnoreCase("sf4") || e.getRegionName().equalsIgnoreCase("airportsf") || e.getRegionName().equalsIgnoreCase("spawnsf"))
			{
				if(p.getGameMode() != GameMode.SPECTATOR || p.getGameMode() != GameMode.CREATIVE)
				{
					p.sendTitle("", "");
					p.setInvulnerable(false);
					p.playSound(p.getLocation(), "minecraft:gun.hud.enter_safezone", 1, 1);
					main.cfgm.getPlayerDB().set("safezone."+p.getName()+".time", -1);
					main.cfgm.getPlayerDB().set("safezone."+p.getName()+".state", true);
					main.cfgm.savePlayerDB();
				}
				
			}
		} catch (Exception e2) {
			// TODO: handle exception
		}
		
	}
	
	@EventHandler
	public void playerPVP(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player)
		{
			if(e.getDamager().isInvulnerable())
			{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent e)
	{
		main.cfgm.getPlayerDB().set("safezone."+e.getPlayer().getName()+".time", 0);
		main.cfgm.savePlayerDB();
		e.getPlayer().setInvulnerable(false);
	}

}
