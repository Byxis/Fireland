package fr.byxis.player.discretion;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.HashMapManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ZombieDetection implements Listener {
    
    private final Fireland main;
    
    public ZombieDetection(Fireland _main) {
        this.main = _main;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e)
    {
        main.getHashMapManager().addDiscretionMap(e.getPlayer().getUniqueId());
    }
    @EventHandler
    public void playerQuit(PlayerQuitEvent e)
    {
        main.getHashMapManager().removeDiscretionMap(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void entityTarget(EntityTargetEvent e)
    {
        Entity entity = e.getEntity();
        Entity target = e.getTarget();
        if ((entity instanceof Zombie || entity instanceof Stray || entity instanceof Husk) && target instanceof Player)
        {
            Player player = (Player) target;
            if (!main.getHashMapManager().getDiscretionMap().containsKey(player.getUniqueId()))
            {
                main.getHashMapManager().addDiscretionMap(player.getUniqueId());
            }
            //float score = main.getCfgm().getPlayerDB().getInt("discretion."+player.getUniqueId()+".score");
            double score = main.getHashMapManager().getDiscretionMap().get(player.getUniqueId()).getScore();

            double calcul = 0.008 * Math.pow((120 - score), 2);
            if (player.getLocation().distance(entity.getLocation()) >= calcul || HashMapManager.getDiscretionMap().get(player.getUniqueId()).isShooting())
            {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void playerEat(final PlayerItemConsumeEvent e)
    {
        //main.getCfgm().getPlayerDB().set("discretion."+e.getPlayer().getUniqueId()+".eat", true);
        //main.getCfgm().savePlayerDB();
        main.getHashMapManager().getDiscretionMap().get(e.getPlayer().getUniqueId()).setEating(true);
        new BukkitRunnable() 
        {

            @Override
            public void run() {
                //main.getCfgm().getPlayerDB().set("discretion."+e.getPlayer().getUniqueId()+".eat", false);
                //main.getCfgm().savePlayerDB();
                if (main.getHashMapManager().getDiscretionMap().containsKey(e.getPlayer().getUniqueId()))
                {
                    main.getHashMapManager().getDiscretionMap().get(e.getPlayer().getUniqueId()).setEating(false);
                }
                
            }
            
        }.runTaskLater(main, 30);
    }

    @EventHandler
    public void playerEquip(PlayerArmorChangeEvent e)
    {
        Player p = e.getPlayer();
        if (HashMapManager.getDiscretionMap().containsKey(p.getUniqueId()))
            HashMapManager.getDiscretionMap().get(p.getUniqueId()).setUsingCamo(isCamo(p));
        else
        {
            HashMapManager.addDiscretionMap(p.getUniqueId());
            HashMapManager.getDiscretionMap().get(p.getUniqueId()).setUsingCamo(isCamo(p));
        }
    }

    @EventHandler
    public void playerMouvement(PlayerMoveEvent e) {
        if (!(e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ() || e.getFrom().getBlockY() != e.getTo().getBlockY()) || !main.getHashMapManager().getDiscretionMap().containsKey(e.getPlayer().getUniqueId()) || main.getHashMapManager().getDiscretionMap().get(e.getPlayer().getUniqueId()).isListeningMovements())
        {
            return;
        }
        Location from = new Location(e.getFrom().getWorld(), e.getFrom().getX(), e.getFrom().getY(), e.getFrom().getZ());
        final Location to = new Location(e.getTo().getWorld(), e.getTo().getX(), e.getTo().getY(), e.getTo().getZ());

        final Player p = e.getPlayer();
        if (from.getX() != to.getX() || from.getZ() != to.getZ())
        {
            main.getHashMapManager().getDiscretionMap().get(p.getUniqueId()).setMoving(true);
            main.getHashMapManager().getDiscretionMap().get(p.getUniqueId()).setListeningMovements(true);
            new BukkitRunnable()
            {

                @Override
                public void run() {
                    main.getHashMapManager().getDiscretionMap().get(p.getUniqueId()).setListeningMovements(false);
                    main.getHashMapManager().getDiscretionMap().get(p.getUniqueId()).setMoving(false);
                }

            }.runTaskLater(main, 10);
        }
    }

    public boolean isCamo(Player p)
    {
        if (!(p.getInventory().getHelmet() != null && p.getInventory().getHelmet().getType() == Material.GOLDEN_HELMET))
        {
            return false;
        }
        ItemStack helmet = p.getInventory().getHelmet();
        if (!(p.getInventory().getChestplate() != null && p.getInventory().getChestplate().getType() == Material.GOLDEN_CHESTPLATE))
        {
            return false;
        }
        ItemStack chestplate = p.getInventory().getChestplate();
        if (!(p.getInventory().getLeggings() != null && p.getInventory().getLeggings().getType() == Material.GOLDEN_LEGGINGS))
        {
            return false;
        }
        ItemStack leggings = p.getInventory().getLeggings();
        if (!(p.getInventory().getBoots() != null && p.getInventory().getBoots().getType() == Material.GOLDEN_BOOTS))
        {
            return false;
        }
        ItemStack boots = p.getInventory().getBoots();
        if ((!helmet.getItemMeta().hasCustomModelData() || helmet.getItemMeta().getCustomModelData() == 0) &&
                (!chestplate.getItemMeta().hasCustomModelData() || chestplate.getItemMeta().getCustomModelData() == 0) &&
                (!leggings.getItemMeta().hasCustomModelData() || leggings.getItemMeta().getCustomModelData() == 0) &&
                (!boots.getItemMeta().hasCustomModelData() || boots.getItemMeta().getCustomModelData() == 0))
        {
            if (new Location(Bukkit.getWorld("world"), -456, 34, -417).getBlock().getType() == Material.OAK_LEAVES)
            {
                return true;
            }
        }
        else if (helmet.getItemMeta().getCustomModelData() == 1 &&
                chestplate.getItemMeta().getCustomModelData() == 1 &&
                leggings.getItemMeta().getCustomModelData() == 1 &&
                boots.getItemMeta().getCustomModelData() == 1)
        {
            if (new Location(Bukkit.getWorld("world"), -456, 34, -417).getBlock().getType() == Material.SNOW_BLOCK)
            {
                return true;
            }
        }
        return false;
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
        
        final FileConfiguration config = main.getCfgm().getPlayerDB();
        
        
        // jump
        if (from.getY() != to.getY())
        {
        	config.set("discretion."+e.getPlayer().getUniqueId()+".jump", true);
        	
        	/*new BukkitRunnable()
        	{

				@Override
				public void run() {
					if(p.getLocation().getY() == to.getY())
					{
						config.set("discretion."+e.getPlayer().getUniqueId()+".jump", false);
			        	main.getCfgm().savePlayerDB();
					}
					
				}
        		
        	}.runTaskLater(main, 10);
        }
        
        //deplacement
        if (from.getX() != to.getX() || from.getY() != to.getY())
        {
        	config.set("discretion."+e.getPlayer().getUniqueId()+".move", true);
        	/*new BukkitRunnable()
        	{

				@Override
				public void run() {
					if(p.getLocation().getX() == to.getX() || p.getLocation().getY() == to.getY())
					{
						config.set("discretion."+e.getPlayer().getUniqueId()+".move", false);
			        	main.getCfgm().savePlayerDB();
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
                    	main.getCfgm().savePlayerDB();
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
                    	main.getCfgm().savePlayerDB();
                	}
					if(p.getLocation().getX() == to.getX() || p.getLocation().getY() == to.getY())
					{
						config.set("discretion."+e.getPlayer().getUniqueId()+".move", false);
						main.getCfgm().savePlayerDB();
					}
					if(p.getLocation().getY() == to.getY())
					{
						config.set("discretion."+e.getPlayer().getUniqueId()+".jump", false);
						main.getCfgm().savePlayerDB();
					}
                    cancel();
                }
            }).runTaskLater(main, 10L);


		main.getCfgm().savePlayerDB();
    }*/

    /*
	public void checkMovement(Player p)
	{
		Location from = new Location(p.getFrom().getWorld(), p.getFrom().getX(), e.getFrom().getY(), e.getFrom().getZ());
		final Location to = new Location(e.getTo().getWorld(), e.getTo().getX(), e.getTo().getY(), e.getTo().getZ());

		final Player p = e.getPlayer();

		final FileConfiguration config = main.getCfgm().getPlayerDB();


		// jump
		if(from.getY() != to.getY())
		{
			config.set("discretion."+e.getPlayer().getUniqueId()+".jump", true);
			main.getCfgm().savePlayerDB();

			new BukkitRunnable()
			{

				@Override
				public void run() {
					if(p.getLocation().getY() == to.getY())
					{
						config.set("discretion."+e.getPlayer().getUniqueId()+".jump", false);
						main.getCfgm().savePlayerDB();
					}

				}

			}.runTaskLater(main, 10);
		}

		//deplacement
		if(from.getX() != to.getX() || from.getY() != to.getY())
		{
			config.set("discretion."+e.getPlayer().getUniqueId()+".move", true);
			main.getCfgm().savePlayerDB();

			new BukkitRunnable()
			{

				@Override
				public void run() {
					if(p.getLocation().getX() == to.getX() || p.getLocation().getY() == to.getY())
					{
						config.set("discretion."+e.getPlayer().getUniqueId()+".move", false);
						main.getCfgm().savePlayerDB();
					}

				}

			}.runTaskLater(main, 10);
		}

		if (!to.equals(from)) {
			config.set("mouvement."+e.getPlayer().getUniqueId(), true);
			main.getCfgm().savePlayerDB();
			(new BukkitRunnable() {
				public void run() {
					if(e.getPlayer().getLocation().equals(to))
					{
						config.set("mouvement."+e.getPlayer().getUniqueId(), false);
						main.getCfgm().savePlayerDB();
					}
					cancel();
				}
			}).runTaskLater(main, 20L);
		}
	}*/
}
