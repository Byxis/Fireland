package fr.byxis.event;

import com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntitySpawn implements Listener
{

    @EventHandler
    public void phantomSpawnEvent(PhantomPreSpawnEvent e)
    {
        e.setCancelled(true);
    }

    @EventHandler
    public void pillagerSpawnEvent(EntitySpawnEvent e)
    {
        if (e.getEntityType() == EntityType.PILLAGER)
        {
            e.setCancelled(true);
        }
    }
}
