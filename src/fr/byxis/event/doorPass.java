package fr.byxis.event;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.byxis.main.Main;

public class doorPass implements Listener {
	
	private Main main;

	public doorPass(Main main) {
		this.main = main;
	}
	
	private void openDoor(BlockState blockState, Openable openable, int time)
	{
		openable.setOpen(true);
		blockState.setBlockData(openable);
		blockState.update();
		 
		new BukkitRunnable() {
			
			@Override
			public void run() {
				openable.setOpen(false);
				blockState.setBlockData(openable);
				blockState.update();
				
			}
		}.runTaskLater(main, 20 * time);
	}
	
	@SuppressWarnings("deprecation")
	private Location getDoorLocation(Location init)
	{
		Location block = null;
		
		if(!(new Location(init.getWorld(), init.getX(), init.getY(), init.getZ()-1).getBlock().isPassable()))
		{
			block = new Location(init.getWorld(), init.getX(), init.getY(), init.getZ()-1);
		}
		else if(!(new Location(init.getWorld(), init.getX(), init.getY(), init.getZ()+1).getBlock().isPassable()))
		{
			block = new Location(init.getWorld(), init.getX(), init.getY(), init.getZ()+1);
		}
		else if(!(new Location(init.getWorld(), init.getX()+1, init.getY(), init.getZ()).getBlock().isPassable()))
		{
			block = new Location(init.getWorld(), init.getX()+1, init.getY(), init.getZ());
		}
		else if(!(new Location(init.getWorld(), init.getX()-1, init.getY(), init.getZ()).getBlock().isPassable()))
		{
			block = new Location(init.getWorld(), init.getX()-1, init.getY(), init.getZ());
		}
		
		if(block != null)
		{
			Location result = null;
			if(new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()-1).getBlock().getType() == Material.LEGACY_IRON_DOOR_BLOCK || new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()-1).getBlock().getType() == Material.IRON_DOOR)
			{
				result = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()-1);
			}
			else if(new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()+1).getBlock().getType() == Material.LEGACY_IRON_DOOR_BLOCK || new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()+1).getBlock().getType() == Material.IRON_DOOR)
			{
				result = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()+1);
			}
			else if(new Location(block.getWorld(), block.getX()+1, block.getY(), block.getZ()).getBlock().getType() == Material.LEGACY_IRON_DOOR_BLOCK || new Location(block.getWorld(), block.getX()+1, block.getY(), block.getZ()).getBlock().getType() == Material.IRON_DOOR)
			{
				result = new Location(block.getWorld(), block.getX()+1, block.getY(), block.getZ());
			}
			else if(new Location(block.getWorld(), block.getX()-1, block.getY(), block.getZ()).getBlock().getType() == Material.LEGACY_IRON_DOOR_BLOCK || new Location(block.getWorld(), block.getX()-1, block.getY(), block.getZ()).getBlock().getType() == Material.IRON_DOOR)
			{
				result = new Location(block.getWorld(), block.getX()-1, block.getY(), block.getZ());
			}
			if(result != null)
			{
				return result;
			}
		}
		
		
		return null;
		
	}
	
	@SuppressWarnings("deprecation")
	private Location getDoubleDoorLocation(Location block)
	{
		Location result = null;
		if(new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()-1).getBlock().getType() == Material.LEGACY_IRON_DOOR_BLOCK || new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()-1).getBlock().getType() == Material.IRON_DOOR)
		{
			result = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()-1);
		}
		else if(new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()+1).getBlock().getType() == Material.LEGACY_IRON_DOOR_BLOCK || new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()+1).getBlock().getType() == Material.IRON_DOOR)
		{
			result = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()+1);
		}
		else if(new Location(block.getWorld(), block.getX()+1, block.getY(), block.getZ()).getBlock().getType() == Material.LEGACY_IRON_DOOR_BLOCK || new Location(block.getWorld(), block.getX()+1, block.getY(), block.getZ()).getBlock().getType() == Material.IRON_DOOR)
		{
			result = new Location(block.getWorld(), block.getX()+1, block.getY(), block.getZ());
		}
		else if(new Location(block.getWorld(), block.getX()-1, block.getY(), block.getZ()).getBlock().getType() == Material.LEGACY_IRON_DOOR_BLOCK || new Location(block.getWorld(), block.getX()-1, block.getY(), block.getZ()).getBlock().getType() == Material.IRON_DOOR)
		{
			result = new Location(block.getWorld(), block.getX()-1, block.getY(), block.getZ());
		}
		
		return result;
	}

	@SuppressWarnings({"deprecation" })
	@EventHandler
	public void playerInteract(PlayerInteractEvent e) {
		
		
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			
			int passTime = main.getConfig().getInt("time.doorpass");
			int handTime = main.getConfig().getInt("time.handpass");
			
			if(e.getClickedBlock().getType() == Material.DEAD_TUBE_CORAL_WALL_FAN) 
			{
				if(p.getItemInHand().getType() == Material.MUSIC_DISC_CAT) 
				{
					InitOpenDoor(e, p, passTime);
				}
				else if(p.getItemInHand().getType() == Material.MUSIC_DISC_13 || p.getItemInHand().getType() == Material.MUSIC_DISC_WAIT || p.getItemInHand().getType() == Material.MUSIC_DISC_BLOCKS)
				{
					e.getPlayer().getWorld().playSound(p.getLocation(), "minecraft:gun.hud.carddeny", 0.1f, 1);
					e.getPlayer().sendMessage("§cPass invalide !");
                }
				else
				{
					e.getPlayer().sendMessage("§8Cette porte nécessite un pass vert !");
				}
			}
			else if(e.getClickedBlock().getType() == Material.DEAD_BRAIN_CORAL_WALL_FAN) 
			{
				if(p.getItemInHand().getType() == Material.MUSIC_DISC_WAIT)  
				{
					InitOpenDoor(e, p, passTime);
				}
				else if(p.getItemInHand().getType() == Material.MUSIC_DISC_13 || p.getItemInHand().getType() == Material.MUSIC_DISC_CAT || p.getItemInHand().getType() == Material.MUSIC_DISC_BLOCKS)
				{
					e.getPlayer().getWorld().playSound(p.getLocation(), "minecraft:gun.hud.carddeny", 0.1f, 1);
					e.getPlayer().sendMessage("§cPass invalide !");
                }
				else
				{
					e.getPlayer().sendMessage("§8Cette porte nécessite un pass bleu !");
				}
			}
			else if(e.getClickedBlock().getType() == Material.DEAD_BUBBLE_CORAL_WALL_FAN) 
			{
				if(p.getItemInHand().getType() == Material.MUSIC_DISC_13) 
				{
					InitOpenDoor(e, p, passTime);
				}
				else if(p.getItemInHand().getType() == Material.MUSIC_DISC_WAIT || p.getItemInHand().getType() == Material.MUSIC_DISC_CAT || p.getItemInHand().getType() == Material.MUSIC_DISC_BLOCKS)
				{
					e.getPlayer().getWorld().playSound(p.getLocation(), "minecraft:gun.hud.carddeny", 0.1f, 1);
					e.getPlayer().sendMessage("§cPass invalide !");
                }
				else
				{
					e.getPlayer().sendMessage("§8Cette porte nécessite un pass jaune !");
				}
			}
			else if(e.getClickedBlock().getType() == Material.DEAD_FIRE_CORAL_WALL_FAN) 
			{
				if(p.getItemInHand().getType() == Material.MUSIC_DISC_BLOCKS) 
				{
					InitOpenDoor(e, p, passTime);
				}
				else if(p.getItemInHand().getType() == Material.MUSIC_DISC_13 || p.getItemInHand().getType() == Material.MUSIC_DISC_WAIT || p.getItemInHand().getType() == Material.MUSIC_DISC_CAT)
				{
					e.getPlayer().getWorld().playSound(p.getLocation(), "minecraft:gun.hud.carddeny", 0.1f, 1);
					e.getPlayer().sendMessage("§cPass invalide !");
                }
				else
				{
					e.getPlayer().sendMessage("§8Cette porte nécessite un pass rouge !");
				}
			}
			else if(e.getClickedBlock().getType() == Material.DEAD_HORN_CORAL_WALL_FAN) 
			{
				if(p.getItemInHand().getType() == null || p.getItemInHand().getType() == Material.AIR) 
				{
					Location door = getDoorLocation(e.getClickedBlock().getLocation());
	                if(door != null)
	                {
	                	if(main.cfgm.getPlayerDB().getBoolean("discretion."+p.getName()+".hasKilled"))
	                	{
	                		e.getPlayer().getWorld().playSound(p.getLocation(), "minecraft:gun.hud.carddeny", 0.1f, 1);
	                		e.getPlayer().sendMessage("§cL'accès vous a été refusé ! Vous avez tué un joueur il y a moins de 5 minutes !");
	                	}
	                	else if(main.cfgm.getPlayerDB().getBoolean("infected."+p.getName()+".state"))
	                	{
	                		e.getPlayer().getWorld().playSound(p.getLocation(), "minecraft:gun.hud.carddeny", 0.1f, 1);
	                		e.getPlayer().sendMessage("§cL'accès vous a été refusé ! Vous êtes infecté !");
	                	}
	                	else
	                	{
							InitOpenDoor(e, p, handTime);
	                	}
	                }
	                else
					{
						e.getPlayer().sendMessage("§8Veuillez placer votre main");
					}
				}
			}
		 }
	}

	private void InitOpenDoor(PlayerInteractEvent e, Player p, int passTime)
	{
		boolean waiting = main.getConfig().getBoolean("door."+p.getName());
		if(!waiting)
		{
			main.getConfig().set("door."+p.getName(), true);
			Location door = getDoorLocation(e.getClickedBlock().getLocation());
			if(door != null)
			{
				Location doubleDoor = (getDoubleDoorLocation(door));
				e.getPlayer().getWorld().playSound(p.getLocation(), "minecraft:gun.hud.passcard", 0.1f, 1);
				new BukkitRunnable()
				{
					@Override
					public void run() {
						main.getConfig().set("door."+p.getName(), false);
						openDoor(door.getBlock().getState(), (Openable) door.getBlock().getState().getBlockData(), passTime);
						if(doubleDoor != null)
						{
							openDoor(doubleDoor.getBlock().getState(), (Openable) doubleDoor.getBlock().getState().getBlockData(), passTime);
						}
					}
				}.runTaskLater(main, 50);
				return;
			}
		}
	}
}
