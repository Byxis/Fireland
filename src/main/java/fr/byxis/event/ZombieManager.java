package fr.byxis.event;

import fr.byxis.fireland.Fireland;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ZombieManager implements Listener
{
    private final Fireland main;

    public ZombieManager(Fireland _main)
    {
        this.main = _main;
    }

    private final Map<UUID, BukkitTask> resistanceTasks = new HashMap<>();

    @EventHandler
    public void zombieDamageEvent(EntityDamageEvent e) {
        Set<EntityDamageEvent.DamageCause> vulnerableCauses = EnumSet.of(
                EntityDamageEvent.DamageCause.FIRE,
                EntityDamageEvent.DamageCause.FIRE_TICK,
                EntityDamageEvent.DamageCause.LAVA,
                EntityDamageEvent.DamageCause.MELTING,
                EntityDamageEvent.DamageCause.HOT_FLOOR
        );

        if (e.getEntityType() == EntityType.ZOMBIE_VILLAGER && e.getEntity() instanceof LivingEntity) {
            LivingEntity zombie = (LivingEntity) e.getEntity();
            UUID zombieId = zombie.getUniqueId();



            if (vulnerableCauses.contains(e.getCause())) {
                if (zombie.hasPotionEffect(PotionEffectType.RESISTANCE)) {
                    zombie.removePotionEffect(PotionEffectType.RESISTANCE);

                    if (resistanceTasks.containsKey(zombieId)) {
                        resistanceTasks.get(zombieId).cancel();
                    }

                    BukkitTask task = new BukkitRunnable() {
                        @Override
                        public void run() {
                            zombie.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 50, true, false));
                            resistanceTasks.remove(zombieId);
                        }
                    }.runTaskLater(main, 5 * 20);

                    resistanceTasks.put(zombieId, task);
                }
            } else if (zombie.hasPotionEffect(PotionEffectType.RESISTANCE)) {
                e.setDamage(0);
            }
        }
    }

}
