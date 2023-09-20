package fr.byxis.player;

import fr.byxis.fireland.Fireland;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class playerDeath implements Listener {
	
	private Fireland main;
	
	public playerDeath(Fireland main) {
		this.main = main;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

	@EventHandler
	public void PlayerRespawn(PlayerRespawnEvent e)
	{
		Player p = e.getPlayer();
		if(p.hasPermission("group.bannis") && !p.hasPermission("fireland.admin"))
		{
			Location loc = new Location(Bukkit.getWorld("world"), 341.5, 72, -209.5);
			e.setRespawnLocation(loc);
		}
	}
	
	@EventHandler
	public void PlayerDeath(PlayerDeathEvent e)
	{
		Player killed = e.getEntity();

		if(e.getEntity().getLastDamageCause() == null || !(e.getEntity().getLastDamageCause().getEntity() instanceof Player killer))
			return;
		
		double money = main.eco.getBalance(killed);
		double pay = money/2;
		if(main.hashMapManager.getBooster() != null)
		{
			pay = money/(2*(1-main.hashMapManager.getBooster().getBoosterLootPercent()/100));
		}
		pay = round(pay, 1);
		killed.sendMessage("§cVous avez perdu "+pay+"$ !");
		main.eco.withdrawPlayer(killed, pay);
		if(e.getEntity().getKiller() != null && !killed.getName().equalsIgnoreCase(killer.getName()))
		{
			killer.sendMessage("§7Vous avez gagné §c"+pay+"$§7 en tuant §c"+killed.getName()+"§7 !");
			main.eco.depositPlayer(killer, pay);
		}
	}
	
}
