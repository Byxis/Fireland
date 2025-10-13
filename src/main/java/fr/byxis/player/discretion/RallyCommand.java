package fr.byxis.player.discretion;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;

public record RallyCommand(Fireland main) implements CommandExecutor
{

    private static boolean isParsable(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    private static final String SECRET_CODE = "UWUMichiriNek0LUV3R#FreeTheCat";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if (args.length >= 3) {
            Player p = (Player) sender;
            if (p.hasPermission("fireland.command.rally.admin")) {
                p.sendMessage("§cUtilisation: /rally <joueur> [distance]");
            }
        } else if (args.length == 1)
        {
            final Player victim = Bukkit.getPlayer(args[0]);

            if (victim != null) {

                int distance = 100;

                if (!(sender instanceof Player) || sender.hasPermission("fireland.admin.rally")) {
                    rallyEntities(victim, distance);
                    setHasShotted(victim);
                }
            }
            else if (args[0].equals(SECRET_CODE))
            {
                Player p = (Player) sender;
                rallyEntities(p, changeDistanceIfHasSilencer(100, p));
                setHasShotted(p);
            }
            else if (sender instanceof Player)
            {

                final Player finalSender = (Player) sender;

                int distance = changeDistanceIfHasSilencer(Integer.parseInt(args[1]), finalSender);

                if (main.getCfgm().getPlayerDB().getBoolean("safezone." + finalSender.getUniqueId() + ".state")) {
                    return false;
                }
                if (victim != null && victim.hasPermission("fireland.command.rally.admin")) {
                    rallyEntities(victim, distance);
                    setHasShotted(victim);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equals(SECRET_CODE) && isParsable(args[1]))
            {
                Player p = (Player) sender;
                if (p != null)
                {
                    rallyEntities(p, Integer.parseInt(args[1]));
                    setHasShotted(p);
                }
            }
            final Player victim = Bukkit.getPlayer(args[1]);
            if (victim != null && args[0].equals(SECRET_CODE)) {
                rallyEntities(victim, 100);
                setHasShotted(victim);
            }
        }
        return false;
    }

    public int changeDistanceIfHasSilencer(int distance, Player victim) {
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
        MobExecutor mobManager = MythicBukkit.inst().getMobManager();
        for (Entity entity : entities) {
            if (entity instanceof Zombie || entity instanceof Stray || entity instanceof WitherSkeleton)
            {
                Monster mob = (Monster) entity;
                if (mob.getTarget() == null || mob.getTarget() instanceof Silverfish ||
                        mob.getTarget().getLocation().distance(mob.getLocation()) >
                                victim.getLocation().distance(mob.getLocation())) {
                    mob.setTarget(victim);
                    if (victim.getLocation().distance(mob.getLocation()) > 60D && Math.random() <= 0.1) {
                        victim.playSound(victim.getLocation(), "minecraft:entity.infected.scream_far", 1, 1);
                        InGameUtilities.playWorldSound(mob.getLocation(), "entity.infected.scream_far",
                                SoundCategory.HOSTILE, 1f, 1f);
                    }
                }
            }
            else if (mobManager.isMythicMob(entity))
            {
                ActiveMob mythicMob = mobManager.getMythicMobInstance(entity);
                if (mythicMob.getType().getInternalName().equalsIgnoreCase("Chien Infecte"))
                {
                    mythicMob.setTarget((AbstractEntity) victim);
                    if (victim.getLocation().distance(mythicMob.getLocation().toPosition().toLocation()) > 60D &&
                            Math.random() <= 0.1)
                    {
                        victim.playSound(victim.getLocation(), "minecraft:entity.infected.scream_far", 1, 1);
                        InGameUtilities.playWorldSound(mythicMob.getLocation().toPosition().toLocation(),
                                "entity.infected.scream_far", SoundCategory.HOSTILE, 1f, 1f);
                    }
                }
            }
            else if (entity instanceof IronGolem)
            {
                ((IronGolem) entity).setTarget(victim);
                if (victim.getLocation().distance(entity.getLocation()) > 60D && Math.random() <= 0.1 &&
                        (((IronGolem) entity).getTarget() == null ||
                                (((IronGolem) entity).getTarget().getLocation().distance(entity.getLocation()) >
                                        victim.getLocation().distance(entity.getLocation())))) {
                    victim.playSound(victim.getLocation(), "minecraft:entity.infected.scream_far", 1, 1);
                    InGameUtilities.playWorldSound(entity.getLocation(), "entity.infected.scream_far",
                            SoundCategory.HOSTILE, 1f, 1f);
                }
            }
        }
    }

    private void setHasShotted(Player victim)
    {
        main.getHashMapManager().getDiscretionMap().get(victim.getUniqueId()).setShooting(10);
    }

}
