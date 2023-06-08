package fr.byxis.discretion;

import fr.byxis.fireland.Fireland;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ZombieDetection implements Listener {
	
	private final Fireland main;
	
	public ZombieDetection(Fireland main) {
		this.main = main;
	}

	@EventHandler
	public void PlayerJoin(PlayerJoinEvent e)
	{
		main.hashMapManager.addDiscretionMap(e.getPlayer().getUniqueId());
	}
	@EventHandler
	public void PlayerQuit(PlayerQuitEvent e)
	{
		main.hashMapManager.removeDiscretionMap(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void EntityTarget(EntityTargetEvent e)
	{
		Entity entity = e.getEntity();
		Entity target = e.getTarget();
		if((entity instanceof Zombie || entity instanceof Stray ||entity instanceof Husk) && target instanceof Player)
		{
			Player player = (Player) target;
			if(!main.hashMapManager.getDiscretionMap().containsKey(player.getUniqueId()))
			{
				main.hashMapManager.addDiscretionMap(player.getUniqueId());
			}
			//float score = main.cfgm.getPlayerDB().getInt("discretion."+player.getUniqueId()+".score");
			double score = main.hashMapManager.getDiscretionMap().get(player.getUniqueId()).getScore();

			double calcul = 0.008*Math.pow((120-score), 2);
			if(player.getLocation().distance(entity.getLocation()) >= calcul /*|| main.cfgm.getPlayerDB().getBoolean("safezone."+player.getUniqueId()+".state")*/)
			{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void playerEat(final PlayerItemConsumeEvent e)
	{
		//main.cfgm.getPlayerDB().set("discretion."+e.getPlayer().getUniqueId()+".eat", true);
		//main.cfgm.savePlayerDB();
		main.hashMapManager.getDiscretionMap().get(e.getPlayer().getUniqueId()).setEating(true);
		new BukkitRunnable() 
		{

			@Override
			public void run() {
				//main.cfgm.getPlayerDB().set("discretion."+e.getPlayer().getUniqueId()+".eat", false);
				//main.cfgm.savePlayerDB();
				if(main.hashMapManager.getDiscretionMap().containsKey(e.getPlayer().getUniqueId()))
				{
					main.hashMapManager.getDiscretionMap().get(e.getPlayer().getUniqueId()).setEating(false);
				}
				
			}
			
		}.runTaskLater(main, 30);
	}

	@EventHandler
    public void playerMouvement(PlayerMoveEvent e) {
		if(!(e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ() || e.getFrom().getBlockY() != e.getTo().getBlockY()) ||!main.hashMapManager.getDiscretionMap().containsKey(e.getPlayer().getUniqueId())||main.hashMapManager.getDiscretionMap().get(e.getPlayer().getUniqueId()).isListeningMovements())
		{
			return;
		}
		Location from = new Location(e.getFrom().getWorld(), e.getFrom().getX(), e.getFrom().getY(), e.getFrom().getZ());
		final Location to = new Location(e.getTo().getWorld(), e.getTo().getX(), e.getTo().getY(), e.getTo().getZ());

		final Player p = e.getPlayer();
		if(from.getX() != to.getX() || from.getZ() != to.getZ())
		{
			main.hashMapManager.getDiscretionMap().get(p.getUniqueId()).setMoving(true);
			main.hashMapManager.getDiscretionMap().get(p.getUniqueId()).setListeningMovements(true);
        	new BukkitRunnable()
        	{

				@Override
				public void run() {
					main.hashMapManager.getDiscretionMap().get(p.getUniqueId()).setListeningMovements(false);
					main.hashMapManager.getDiscretionMap().get(p.getUniqueId()).setMoving(false);
				}

        	}.runTaskLater(main, 10);
        }
	}

	//@EventHandler
    /*public void playerMouvement(PlayerMoveEvent e) {
		if(!(e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ() || e.getFrom().getBlockY() != e.getTo().getBlockY()))
		{
			return;
		}
			Location from = new Location(e.getFrom().getWorld(), e.getFrom().getX(), e.getFrom().getY(), e.getFrom().getZ());
        final Location to = new Location(e.getTo().getWorld(), e.getTo().getX(), e.getTo().getY(), e.getTo().getZ());
        
        final Player p = e.getPlayer();
        
        final FileConfiguration config = main.cfgm.getPlayerDB();
        
        
        // jump
        if(from.getY() != to.getY())
        {
        	config.set("discretion."+e.getPlayer().getUniqueId()+".jump", true);
        	
        	/*new BukkitRunnable()
        	{

				@Override
				public void run() {
					if(p.getLocation().getY() == to.getY())
					{
						config.set("discretion."+e.getPlayer().getUniqueId()+".jump", false);
			        	main.cfgm.savePlayerDB();
					}
					
				}
        		
        	}.runTaskLater(main, 10);
        }
        
        //deplacement
        if(from.getX() != to.getX() || from.getY() != to.getY())
        {
        	config.set("discretion."+e.getPlayer().getUniqueId()+".move", true);
        	/*new BukkitRunnable()
        	{

				@Override
				public void run() {
					if(p.getLocation().getX() == to.getX() || p.getLocation().getY() == to.getY())
					{
						config.set("discretion."+e.getPlayer().getUniqueId()+".move", false);
			        	main.cfgm.savePlayerDB();
					}
					
				}
        		
        	}.runTaskLater(main, 10);
        }
        if (!to.equals(from)) {
        	config.set("mouvement."+e.getPlayer().getUniqueId(), true);
            (new BukkitRunnable() {
                public void run() {
                	if(e.getPlayer().getLocation().equals(to))
                	{
                		config.set("mouvement."+e.getPlayer().getUniqueId(), false);
                    	main.cfgm.savePlayerDB();
                	}
                    cancel();
                }
            }).runTaskLater(main, 20L);
        }
     *//*

        config.set("mouvement."+e.getPlayer().getUniqueId(), true);
            (new BukkitRunnable() {
                public void run() {
                	if(e.getPlayer().getLocation().equals(to))
                	{
                		config.set("mouvement."+e.getPlayer().getUniqueId(), false);
                    	main.cfgm.savePlayerDB();
                	}
					if(p.getLocation().getX() == to.getX() || p.getLocation().getY() == to.getY())
					{
						config.set("discretion."+e.getPlayer().getUniqueId()+".move", false);
						main.cfgm.savePlayerDB();
					}
					if(p.getLocation().getY() == to.getY())
					{
						config.set("discretion."+e.getPlayer().getUniqueId()+".jump", false);
						main.cfgm.savePlayerDB();
					}
                    cancel();
                }
            }).runTaskLater(main, 10L);


		main.cfgm.savePlayerDB();
    }*/

	/*
	public void checkMovement(Player p)
	{
		Location from = new Location(p.getFrom().getWorld(), p.getFrom().getX(), e.getFrom().getY(), e.getFrom().getZ());
		final Location to = new Location(e.getTo().getWorld(), e.getTo().getX(), e.getTo().getY(), e.getTo().getZ());

		final Player p = e.getPlayer();

		final FileConfiguration config = main.cfgm.getPlayerDB();


		// jump
		if(from.getY() != to.getY())
		{
			config.set("discretion."+e.getPlayer().getUniqueId()+".jump", true);
			main.cfgm.savePlayerDB();

			new BukkitRunnable()
			{

				@Override
				public void run() {
					if(p.getLocation().getY() == to.getY())
					{
						config.set("discretion."+e.getPlayer().getUniqueId()+".jump", false);
						main.cfgm.savePlayerDB();
					}

				}

			}.runTaskLater(main, 10);
		}

		//deplacement
		if(from.getX() != to.getX() || from.getY() != to.getY())
		{
			config.set("discretion."+e.getPlayer().getUniqueId()+".move", true);
			main.cfgm.savePlayerDB();

			new BukkitRunnable()
			{

				@Override
				public void run() {
					if(p.getLocation().getX() == to.getX() || p.getLocation().getY() == to.getY())
					{
						config.set("discretion."+e.getPlayer().getUniqueId()+".move", false);
						main.cfgm.savePlayerDB();
					}

				}

			}.runTaskLater(main, 10);
		}

		if (!to.equals(from)) {
			config.set("mouvement."+e.getPlayer().getUniqueId(), true);
			main.cfgm.savePlayerDB();
			(new BukkitRunnable() {
				public void run() {
					if(e.getPlayer().getLocation().equals(to))
					{
						config.set("mouvement."+e.getPlayer().getUniqueId(), false);
						main.cfgm.savePlayerDB();
					}
					cancel();
				}
			}).runTaskLater(main, 20L);
		}
	}*/
}
