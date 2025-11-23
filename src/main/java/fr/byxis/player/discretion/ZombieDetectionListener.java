package fr.byxis.player.discretion;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ZombieDetectionListener implements Listener
{
    private final DiscretionManager m_discretionManager;

    public ZombieDetectionListener(DiscretionManager _discretionManager)
    {
        this.m_discretionManager = _discretionManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        m_discretionManager.addPlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        m_discretionManager.removePlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent e)
    {
        Entity entity = e.getEntity();
        Entity target = e.getTarget();

        if (!isDetectable(entity) || !(target instanceof Player))
        {
            return;
        }

        Player player = (Player) target;
        double distance = player.getLocation().distance(entity.getLocation());

        if (!m_discretionManager.canBeDetected(player.getUniqueId(), distance))
        {
            e.setCancelled(true);
        }
    }

    private boolean isDetectable(Entity entity)
    {
        return entity instanceof Zombie || entity instanceof Stray;
    }
}
