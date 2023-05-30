package fr.byxis.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import fr.byxis.fireland.Fireland;

public class timeAlive implements Listener {
	
	private final Fireland main;

	public timeAlive(Fireland main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void playerRespawn(PlayerRespawnEvent e)
	{
		Player p = e.getPlayer();
		
		main.cfgm.getPlayerDB().set("playtime."+p.getUniqueId(), 0);
		main.cfgm.savePlayerDB();
	}

}
