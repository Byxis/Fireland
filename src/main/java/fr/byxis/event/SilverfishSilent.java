package fr.byxis.event;

import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class SilverfishSilent implements Listener
{

    @EventHandler
    public void entitySpawn(EntitySpawnEvent e)
    {
        if (e.getEntity() instanceof Silverfish)
        {
            e.getEntity().setSilent(true);
        }
    }
}
