package fr.byxis.event;

import fr.byxis.main.Main;
import org.bukkit.event.Listener;

public class zombieManager implements Listener {
    private final Main main;

    public zombieManager(Main main) {
        this.main = main;
    }


    /*@EventHandler
    private void zombieSpawnEvent(CreatureSpawnEvent e)
    {
        if (e.getEntityType() == EntityType.ZOMBIE_VILLAGER)
        {
            ((LivingEntity)e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,2147483647, 0, true));
        }
    }

    @EventHandler
    private void zombieDamageEvent(EntityDamageEvent e)
    {
        if(e.getEntityType() == EntityType.ZOMBIE_VILLAGER && (
                e.getCause() == EntityDamageEvent.DamageCause.FIRE
                        || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                || e.getCause() == EntityDamageEvent.DamageCause.LAVA
            || e.getCause() == EntityDamageEvent.DamageCause.MELTING))
        {
            ((LivingEntity)e.getEntity()).removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            new BukkitRunnable() {
                @Override
                public void run() {
                    ((LivingEntity)e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,2147483647, 0, true));
                }
            }.runTaskLater(main, 20*5);
        }
    }*/

}
