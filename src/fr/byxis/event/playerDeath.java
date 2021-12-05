package fr.byxis.event;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.byxis.main.Main;

public class playerDeath implements Listener {
	
	private Main main;
	
	public playerDeath(Main main) {
		this.main = main;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	@EventHandler
	public void PlayerDeath(PlayerDeathEvent e)
	{
		
		Player killed = e.getEntity();
		if(e.getEntity().getKiller() == null)
			return;
		Player killer = e.getEntity().getKiller();
		
		main.cfgm.getPlayerDB().set("discretion."+killer.getName()+".hasKilled", true);
		main.cfgm.savePlayerDB();
		
		int time;

		try 
		{
			time = main.getConfig().getInt("time.playerhaskilled");
			
		} catch (Exception e2) {
			time = 30;
		}
		
		double money = main.eco.getBalance(killed);
		double pay = money/4;
		pay = round(pay, 1);
		killed.sendMessage("§cVous avez perdu "+pay+"$ !");
		main.eco.withdrawPlayer(killed, pay);
		
		if(killer instanceof Player)
		{
			killer.sendMessage("§7Vous avez gagné §c"+pay+"$§7 en tuant "+killed.getName()+" !");
			main.eco.depositPlayer(killer, pay);
		}
		
		new BukkitRunnable()
		{
			@Override
			public void run() {
				main.cfgm.getPlayerDB().set("discretion."+killer.getName()+".hasKilled", false);
				main.cfgm.savePlayerDB();
			}
			
		}.runTaskLater(main, 20 * time);
	}
	
}
