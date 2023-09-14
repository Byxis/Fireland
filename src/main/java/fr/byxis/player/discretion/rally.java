package fr.byxis.player.discretion;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static fr.byxis.fireland.utilities.InGameUtilities.debugp;

public record rally(fr.byxis.fireland.Fireland main) implements CommandExecutor {

	private static boolean isParsable(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (final NumberFormatException e) {
			return false;
		}
	}

	private static String secretCode = "UWUMichiriNek0LUV3R#FreeTheCat";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

		if (args.length >= 3) {
			Player p = (Player) sender;
			if (p.hasPermission("fireland.command.rally.admin")) {
				p.sendMessage("§cUtilisation : /rally <joueur> [distance]");
			}
		} else if (args.length == 1) {
			final Player victim = Bukkit.getPlayer(args[0]);
			if (victim != null) {

				int distance = 100;

				if (main.cfgm.getPlayerDB().getBoolean("safezone." + victim.getUniqueId() + ".state")) {
					return false;
				}

				if (victim.hasPermission("fireland.command.rally.admin")) {
					rallyEntities(victim, distance);
					setHasShotted(victim);
				}
			}
			else if(args[0].equals(secretCode))
			{
				Player p = (Player) sender;
				rallyEntities(p, ChangeDistanceIfHasSilencer(100, p));
				setHasShotted(p);
			}
			else {
				final Player Sender = (Player) sender;

				int distance = ChangeDistanceIfHasSilencer(Integer.parseInt(args[1]),Sender);

				if (main.cfgm.getPlayerDB().getBoolean("safezone." + Sender.getUniqueId() + ".state")) {
					return false;
				}
				if (victim.hasPermission("fireland.command.rally.admin")) {
					rallyEntities(victim, distance);
					setHasShotted(victim);
				}
			}
		} else if (args.length == 2) {
			if(args[0].equals(secretCode) && isParsable(args[1]))
			{
				Player p = (Player) sender;
				rallyEntities(p, Integer.parseInt(args[1]));
				setHasShotted(p);
			}
			final Player victim = Bukkit.getPlayer(args[0]);
			if (victim != null) {

				int distance = Integer.parseInt(args[1]);

				if (main.cfgm.getPlayerDB().getBoolean("safezone." + victim.getUniqueId() + ".state")) {
					return false;
				}

				if (victim.hasPermission("fireland.command.rally.admin")) {
					rallyEntities(victim, ChangeDistanceIfHasSilencer(distance, victim));
					setHasShotted(victim);
				}
			}
		}
		return false;
	}

	public int ChangeDistanceIfHasSilencer(int distance, Player victim) {
		try {
			if (victim.getItemInHand().getItemMeta() != null) {
				if (victim.getItemInHand().getItemMeta().hasLore() && victim.getItemInHand().getType() != Material.AIR) {
					if (victim.getItemInHand().getItemMeta().getLore() != null) {
						for (String lore : victim.getItemInHand().getItemMeta().getLore()) {
							if (lore.contains("Silencieux")) {
								distance /= 5;
							}
						}
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return distance;
	}

	public static List<Entity> nearbyEntities(Location location, double radius) {
        List<Entity> entities = new ArrayList<>();

        for (Entity entity : location.getWorld().getEntities()) {
            if (entity.getWorld() == location.getWorld()) {
				if (!(entity instanceof Player && ((Player) entity).getGameMode().equals(GameMode.SPECTATOR))) {
					if (entity.getLocation().distanceSquared(location) <= radius * radius) {
						entities.add(entity);
					}
				}
            }
        }
        return entities;
    }

	private void rallyEntities(Player victim, long distance) {
		List<Entity> entities = nearbyEntities(victim.getLocation(), distance);
		for (Entity entity : entities) {
			if (entity instanceof Zombie || entity instanceof Stray  || entity instanceof Husk || entity instanceof Drowned ||  entity instanceof WitherSkeleton) {
				Monster mob = (Monster) entity;

				if (mob.getTarget() == null || mob.getTarget() instanceof Silverfish ||mob.getTarget().getLocation().distance(mob.getLocation()) > victim.getLocation().distance(mob.getLocation())) {
					mob.setTarget(victim);
					if (victim.getLocation().distance(mob.getLocation()) > 60D && Math.random() <= 0.1) {
						victim.playSound(victim.getLocation(), "minecraft:entity.infected.scream_far", 1, 1);
					}
				}
			}
			else if (entity instanceof IronGolem )
			{
				((IronGolem) entity).setTarget(victim);
				if (victim.getLocation().distance(entity.getLocation()) > 60D && Math.random() <= 0.1 && (((IronGolem) entity).getTarget() == null ||(((IronGolem) entity).getTarget().getLocation().distance(entity.getLocation()) > victim.getLocation().distance(entity.getLocation())))) {
					victim.playSound(victim.getLocation(), "minecraft:entity.infected.scream_far", 1, 1);
				}
			}
		}
	}

	private void setHasShotted(Player victim)
	{
		main.hashMapManager.getDiscretionMap().get(victim.getUniqueId()).setShooting(true);

		new BukkitRunnable() {

			@Override
			public void run() {
				main.hashMapManager.getDiscretionMap().get(victim.getUniqueId()).setShooting(false);
			}

		}.runTaskLater(main, 20 * 10);
	}

}
