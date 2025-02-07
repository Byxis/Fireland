package fr.byxis.event;

import fr.byxis.fireland.Fireland;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ZombieManager implements Listener
{
    private final Fireland main;

    public ZombieManager(Fireland _main)
    {
        this.main = _main;
    }

    @EventHandler
    private void zombieSpawnEvent(CreatureSpawnEvent e)
    {
        if (e.getEntityType() == EntityType.ZOMBIE_VILLAGER)
        {
            e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,99999999, 50, true, false));
        }
    }

    @EventHandler
    private void zombieDamageEvent(EntityDamageEvent e)
    {
        if (e.getEntityType() == EntityType.ZOMBIE_VILLAGER)
        {
            if (e.getCause() == EntityDamageEvent.DamageCause.FIRE
                    || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                    || e.getCause() == EntityDamageEvent.DamageCause.LAVA
                    || e.getCause() == EntityDamageEvent.DamageCause.MELTING
                    || e.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR
            )
            {
                if (((LivingEntity) e.getEntity()).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
                {
                    ((LivingEntity)e.getEntity()).removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ((LivingEntity)e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,99999999, 50, true, false));
                        }
                    }.runTaskLater(main, 20 *5);
                }

            }
            else if (((LivingEntity) e.getEntity()).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
            {
                e.setDamage(0);
            }
        }
    }

}
