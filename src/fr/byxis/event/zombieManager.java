package fr.byxis.event;

import fr.byxis.main.Main;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class zombieManager implements Listener {
    private Main main;

    public zombieManager(Main main) {
        this.main = main;
    }


    @EventHandler
    private void zombieSpawnEvent(EntitySpawnEvent e)
    {
        if (e.getEntityType() == EntityType.ZOMBIE_VILLAGER)
        {
            ((LivingEntity)e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,2147483647, 4, false));
        }
    }

    @EventHandler
    private void zombieDamageEvent(EntityDamageEvent e)
    {
        if(e.getEntityType() == EntityType.ZOMBIE_VILLAGER)
            ((LivingEntity)e.getEntity()).removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        {
            new BukkitRunnable() {
                @Override
                public void run() {
                    ((LivingEntity)e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,2147483647, 4, false));
                }
            }.runTaskLater(main, 20*5);
        }
    }

}
