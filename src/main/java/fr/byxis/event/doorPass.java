package fr.byxis.event;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.player.pvpmanager.PvPManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static fr.byxis.fireland.utilities.InGameUtilities.debugp;

public class doorPass implements Listener {
	
	private Fireland main;

	public doorPass(Fireland main) {
		this.main = main;
		canAcces = new ArrayList<>();
	}
	
	private void openDoor(Player p, Location door, BlockState blockState, Openable openable, int time)
	{
		openable.setOpen(true);
		InGameUtilities.playWorldSound(p.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, SoundCategory.PLAYERS, 1, 1f);

		blockState.setBlockData(openable);
		blockState.update();
		new BukkitRunnable() {
			
			@Override
			public void run() {
				InGameUtilities.playWorldSound(p.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, SoundCategory.PLAYERS, 1, 1f);

				openable.setOpen(false);
				blockState.setBlockData(openable);
				blockState.update();
			}
		}.runTaskLater(main, 20L * time);
	}
	
	@SuppressWarnings("deprecation")
	private Location getDoorLocation(Location init)
	{
		ArrayList<Location> blocks = new ArrayList<>();
		
		if(!(new Location(init.getWorld(), init.getX(), init.getY(), init.getZ()-1).getBlock().isPassable()))
		{
			blocks.add(new Location(init.getWorld(), init.getX(), init.getY(), init.getZ()-1));
		}
		if(!(new Location(init.getWorld(), init.getX(), init.getY(), init.getZ()+1).getBlock().isPassable()))
		{
			blocks.add(new Location(init.getWorld(), init.getX(), init.getY(), init.getZ()+1));
		}
		if(!(new Location(init.getWorld(), init.getX()+1, init.getY(), init.getZ()).getBlock().isPassable()))
		{
			blocks.add(new Location(init.getWorld(), init.getX()+1, init.getY(), init.getZ()));
		}
		if(!(new Location(init.getWorld(), init.getX()-1, init.getY(), init.getZ()).getBlock().isPassable()))
		{
			blocks.add(new Location(init.getWorld(), init.getX()-1, init.getY(), init.getZ()));
		}
		if(!blocks.isEmpty())
		{
			Location result = null;
			for(Location block : blocks)
			{
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
					break;
				}
			}
			return result;
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

	private ArrayList<UUID> canAcces;

	@SuppressWarnings({"deprecation" })
	@EventHandler
	public void playerInteract(PlayerInteractEvent e) {

		if(!e.getPlayer().getWorld().getName().equalsIgnoreCase("world"))
			return;
		
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			if(canAcces.contains(p.getUniqueId()))
			{
				return;
			}
			canAcces.add(p.getUniqueId());
			new BukkitRunnable() {
				@Override
				public void run() {
					canAcces.remove(p.getUniqueId());
				}
			}.runTaskLater(main, 20);
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
						if(PvPManager.isOnTimer(p))
						{
							e.getPlayer().getWorld().playSound(p.getLocation(), "minecraft:gun.hud.carddeny", 0.1f, 1);
							InGameUtilities.sendPlayerError(p, "L'accès à cette zone vous a été refusé car vous êtes en combat.");
						}
	                	else if(main.cfgm.getPlayerDB().getBoolean("infected."+p.getUniqueId()+".state"))
	                	{
	                		e.getPlayer().getWorld().playSound(p.getLocation(), "minecraft:gun.hud.carddeny", 0.1f, 1);
							InGameUtilities.sendPlayerError(p, "L'accès à cette zone vous a été refusé car vous êtes infecté.");
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
		boolean waiting = main.getConfig().getBoolean("door."+p.getUniqueId());
		if(!waiting)
		{
			Location door = getDoorLocation(e.getClickedBlock().getLocation());
			if(door != null)
			{
				main.getConfig().set("door."+p.getUniqueId(), true);
				Location doubleDoor = (getDoubleDoorLocation(door));
				e.getPlayer().getWorld().playSound(p.getLocation(), "minecraft:gun.hud.passcard", 0.1f, 1);
				new BukkitRunnable()
				{
					@Override
					public void run() {
						main.getConfig().set("door."+p.getUniqueId(), false);
						openDoor(p, door, door.getBlock().getState(), (Openable) door.getBlock().getState().getBlockData(), passTime);
						openDoor(p, door.add(0, -1, 0), door.getBlock().getState(), (Openable) door.getBlock().getState().getBlockData(), passTime);
						if(doubleDoor != null)
						{
							openDoor(p, doubleDoor, doubleDoor.getBlock().getState(), (Openable) doubleDoor.getBlock().getState().getBlockData(), passTime);
							openDoor(p, doubleDoor.add(0, -1, 0), doubleDoor.getBlock().getState(), (Openable) doubleDoor.getBlock().getState().getBlockData(), passTime);
						}
					}
				}.runTaskLater(main, 50);
				return;
			}
		}
	}
}
