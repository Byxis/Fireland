package fr.byxis.event;

import fr.byxis.main.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

		Player killer = e.getEntity().getKiller();
		//TODO: Système de Karma
		/*main.cfgm.getPlayerDB().set("discretion."+killer.getUniqueId()+".hasKilled", true);
		main.cfgm.getPlayerDB().set("discretion."+killed.getUniqueId()+".hasKilled", false);
		main.cfgm.savePlayerDB();
		
		int time;

		try 
		{
			time = main.getConfig().getInt("time.playerhaskilled");
			
		} catch (Exception e2) {
			time = 30;
		}*/
		
		double money = main.eco.getBalance(killed);
		double pay = money/2;
		pay = round(pay, 1);
		killed.sendMessage("§cVous avez perdu "+pay+"$ !");
		main.eco.withdrawPlayer(killed, pay);
		if(e.getEntity().getKiller() != null)
		{
			killer.sendMessage("§7Vous avez gagné §c"+pay+"$§7 en tuant §c"+killed.getName()+"§7 !");
			main.eco.depositPlayer(killer, pay);
		}

		/*
		new BukkitRunnable()
		{
			@Override
			public void run() {
				main.cfgm.getPlayerDB().set("discretion."+killer.getName()+".hasKilled", false);
				main.cfgm.savePlayerDB();
			}
			
		}.runTaskLater(main, 20L * time);*/
	}
	
}
