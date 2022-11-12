package fr.byxis.command;

import fr.byxis.main.Main;
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

public record rally(Main main) implements CommandExecutor {

	private static boolean isParsable(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (final NumberFormatException e) {
			return false;
		}
	}

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

				distance = ChangeDistanceIfHasSilencer(distance, victim);

				if (victim.hasPermission("fireland.command.rally.admin")) {
					rallyEntities(victim, distance);
					setHasShotted(victim);
				}
			}
			else if(args[0].equals("UWUMichiriNek0LUV3R#FreeTheCat"))
			{
				Player p = (Player) sender;
				rallyEntities(p, 100);
				setHasShotted(p);
			}
			else {
				final Player Sender = (Player) sender;

				int distance = Integer.parseInt(args[1]);

				if (main.cfgm.getPlayerDB().getBoolean("safezone." + Sender.getUniqueId() + ".state")) {
					return false;
				}

				distance = ChangeDistanceIfHasSilencer(distance, victim);
				if (victim.hasPermission("fireland.command.rally.admin")) {
					rallyEntities(victim, distance);
					setHasShotted(victim);
				}
			}
		} else if (args.length == 2) {
			if(args[0].equals("UWUMichiriNek0LUV3R#FreeTheCat") && isParsable(args[1]))
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

				distance = ChangeDistanceIfHasSilencer(distance, victim);

				if (victim.hasPermission("fireland.command.rally.admin")) {
					rallyEntities(victim, distance);
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
			if (entity instanceof Zombie || entity instanceof Stray ||  entity instanceof WitherSkeleton) {
				Monster mob = (Monster) entity;

				if (mob.getTarget() == null || mob.getTarget() instanceof Silverfish) {
					mob.setTarget(victim);
					if (victim.getLocation().distance(mob.getLocation()) > 60D && Math.random() <= 0.1) {
						victim.playSound(victim.getLocation(), "minecraft:entity.infected.scream_far", 1, 1);
					}
				}
			}
			else if (entity instanceof IronGolem )
			{
				((IronGolem) entity).setTarget(victim);
				if (victim.getLocation().distance(entity.getLocation()) > 60D && Math.random() <= 0.1) {
					victim.playSound(victim.getLocation(), "minecraft:entity.infected.scream_far", 1, 1);
				}
			}
		}
	}

	private void setHasShotted(Player victim)
	{
		main.cfgm.getPlayerDB().set("discretion." + victim.getUniqueId() + ".shot", true);
		main.cfgm.savePlayerDB();

		new BukkitRunnable() {

			@Override
			public void run() {
				main.cfgm.getPlayerDB().set("discretion." + victim.getUniqueId() + ".shot", false);
				main.cfgm.savePlayerDB();
			}

		}.runTaskLater(main, 20 * 10);
	}

}
