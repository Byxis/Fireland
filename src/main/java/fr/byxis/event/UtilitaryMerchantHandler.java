package fr.byxis.event;

import fr.byxis.fireland.Fireland;
import org.bukkit.Bukkit;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class UtilitaryMerchantHandler implements Listener
{
    
    
    private Fireland main;

    public UtilitaryMerchantHandler(Fireland main)
    {
        this.main = main;
    }

    @EventHandler
    public void EntitySpawn(EntitySpawnEvent e) {
        if(e.getEntity() instanceof Villager && e.getEntity().getName() == "Marchand Utilitaire") {
            
            e.getEntity().setSilent(true);
            
            new BukkitRunnable() {

                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give @e[type=minecraft:villager] minecraft:invisibility 999999 1 true");
                }
                
            }.runTaskLater(main, 1);
            
            
        }
    }
}
