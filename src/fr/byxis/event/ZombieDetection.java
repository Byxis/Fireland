package fr.byxis.event;

import fr.byxis.main.Main;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ZombieDetection implements Listener {
	
	private final Main main;
	
	public ZombieDetection(Main main) {
		this.main = main;
	}

	@EventHandler
	public void EntityTarget(EntityTargetEvent e)
	{
		Entity entity = e.getEntity();
		Entity target = e.getTarget();
		if((entity instanceof Zombie || entity instanceof Stray ||entity instanceof Husk) && target instanceof Player)
		{
			Player player = (Player) target;
			
			float score = main.cfgm.getPlayerDB().getInt("discretion."+player.getUniqueId()+".score");
			score = (score-100)*(-1);
			score = 40;
			
			float calcul = (float) (Math.pow(2.7, score*0.025)*4);
			if(player.getLocation().distance(entity.getLocation()) >= calcul || main.cfgm.getPlayerDB().getBoolean("safezone."+player.getUniqueId()+".state"))
			{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void playerEat(final PlayerItemConsumeEvent e)
	{
		main.cfgm.getPlayerDB().set("discretion."+e.getPlayer().getUniqueId()+".eat", true);
		main.cfgm.savePlayerDB();
		new BukkitRunnable() 
		{

			@Override
			public void run() {
				main.cfgm.getPlayerDB().set("discretion."+e.getPlayer().getUniqueId()+".eat", false);
				main.cfgm.savePlayerDB();
				
			}
			
		}.runTaskLater(main, 30);
	}
	/*
	@EventHandler
    public void playerMouvement(PlayerMoveEvent e) {
        Location from = new Location(e.getFrom().getWorld(), e.getFrom().getX(), e.getFrom().getY(), e.getFrom().getZ());
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
