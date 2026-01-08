package fr.byxis.player.discretion;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

public record RallyCommand(Fireland main, DiscretionManager discretionManager) implements CommandExecutor {

    private static boolean isParsable(String input)
    {
        try
        {
            Integer.parseInt(input);
            return true;
        }
        catch (final NumberFormatException e)
        {
            return false;
        }
    }

    private static final String SECRET_CODE = UUID.randomUUID().toString();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args)
    {

        if (args.length >= 3)
        {
            Player p = (Player) sender;
            if (p.hasPermission("fireland.command.rally.admin"))
            {
                p.sendMessage("§cUtilisation: /rally <joueur> [distance]");
            }
        }
        else if (args.length == 1)
        {
            final Player victim = Bukkit.getPlayer(args[0]);

            if (victim != null)
            {

                int distance = 100;

                if (!(sender instanceof Player) || sender.hasPermission("fireland.admin.rally"))
                {
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

                int distance = changeDistanceIfHasSilencer(Integer.parseInt(args[0]), finalSender);

                if (main.getCfgm().getPlayerDB().getBoolean("safezone." + finalSender.getUniqueId() + ".state"))
                {
                    return false;
                }
                if (victim != null && victim.hasPermission("fireland.command.rally.admin"))
                {
                    rallyEntities(victim, distance);
                    setHasShotted(victim);
                }
            }
        }
        else if (args.length == 2)
        {
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
            if (victim != null && args[0].equals(SECRET_CODE))
            {
                rallyEntities(victim, 100);
                setHasShotted(victim);
            }
        }
        return false;
    }

    public int changeDistanceIfHasSilencer(int distance, Player victim)
    {
        try
        {
            if (victim.getItemInHand().getItemMeta() != null)
            {
                if (victim.getItemInHand().getItemMeta().hasLore() && victim.getItemInHand().getType() != Material.AIR)
                {
                    if (victim.getItemInHand().getItemMeta().getLore() != null)
                    {
                        for (String lore : victim.getItemInHand().getItemMeta().getLore())
                        {
                            if (lore.contains("Silencieux"))
                            {
                                distance /= 5;
                            }
                        }
                    }
                }
            }

        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        return distance;
    }

    public static List<Entity> nearbyEntities(Location location, double radius)
    {
        List<Entity> entities = new ArrayList<>();

        for (Entity entity : location.getWorld().getEntities())
        {
            if (entity.getWorld() == location.getWorld())
            {
                if (!(entity instanceof Player && ((Player) entity).getGameMode().equals(GameMode.SPECTATOR)))
                {
                    if (entity.getLocation().distanceSquared(location) <= radius * radius)
                    {
                        entities.add(entity);
                    }
                }
            }
        }
        return entities;
    }

    private void rallyEntities(Player _victim, long _distance)
    {
        List<Entity> entities = nearbyEntities(_victim.getLocation(), _distance);
        try (MythicBukkit mythicBukkit = MythicBukkit.inst())
        {
            MobExecutor mobManager = mythicBukkit.getMobManager();
            Location victimLoc = _victim.getLocation();

            for (Entity entity : entities)
            {
                if (entity instanceof Zombie || entity instanceof Stray || entity instanceof WitherSkeleton)
                {
                    handleVanillaMob((Monster) entity, _victim, victimLoc);
                }
                else if (mobManager.isMythicMob(entity))
                {
                    handleMythicMob(mobManager.getMythicMobInstance(entity), _victim, victimLoc);
                }
                else if (entity instanceof IronGolem)
                {
                    handleIronGolem((IronGolem) entity, _victim, victimLoc);
                }
            }
        }
        catch (Exception _e)
        {
            return;
        }

    }

    private void handleVanillaMob(Monster mob, Player victim, Location victimLoc)
    {
        Entity currentTarget = mob.getTarget();
        double distanceToVictim = victimLoc.distance(mob.getLocation());

        if (currentTarget == null || currentTarget instanceof Silverfish
                || currentTarget.getLocation().distance(mob.getLocation()) > distanceToVictim)
        {

            mob.setTarget(victim);
            playDistantScreamIfNeeded(victim, victimLoc, mob.getLocation(), distanceToVictim);
        }
    }

    private void handleMythicMob(ActiveMob mythicMob, Player victim, Location victimLoc)
    {
        if (!mythicMob.getType().getInternalName().equalsIgnoreCase("Chieninfecte"))
        {
            return;
        }

        AbstractEntity mythicTarget = BukkitAdapter.adapt(victim);
        mythicMob.setTarget(mythicTarget);

        Location mobLoc = mythicMob.getLocation().toPosition().toLocation();
        double distance = victimLoc.distance(mobLoc);
        playDistantScreamIfNeeded(victim, victimLoc, mobLoc, distance);
    }

    private void handleIronGolem(IronGolem golem, Player victim, Location victimLoc)
    {
        LivingEntity currentTarget = golem.getTarget();
        double distanceToVictim = victimLoc.distance(golem.getLocation());

        if (currentTarget == null || currentTarget.getLocation().distance(golem.getLocation()) > distanceToVictim)
        {

            golem.setTarget(victim);
            playDistantScreamIfNeeded(victim, victimLoc, golem.getLocation(), distanceToVictim);
        }
    }

    private void playDistantScreamIfNeeded(Player victim, Location victimLoc, Location mobLoc, double distance)
    {
        if (distance > 60D && Math.random() <= 0.1)
        {
            victim.playSound(victimLoc, "minecraft:entity.infected.scream_far", 3, 1);
            InGameUtilities.playWorldSound(mobLoc, "entity.infected.scream_far", SoundCategory.HOSTILE, 3f, 1f);
        }
    }

    private void setHasShotted(Player victim)
    {
        discretionManager.recordShot(victim.getUniqueId());
    }

}
