package fr.byxis.player.discretion;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import fr.byxis.fireland.Fireland;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DiscretionEventListener implements Listener
{
    private final Fireland m_fireland;
    private final DiscretionManager m_discretionManager;
    private final CamoDetector m_camoDetector;

    public DiscretionEventListener(Fireland _fireland, DiscretionManager _discretionManager)
    {
        this.m_fireland = _fireland;
        this.m_discretionManager = _discretionManager;
        this.m_camoDetector = new CamoDetector(_fireland);
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e)
    {
        m_discretionManager.getDiscretion(e.getPlayer().getUniqueId()).setEating(true);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (m_discretionManager.getMap().containsKey(e.getPlayer().getUniqueId()))
                {
                    m_discretionManager.getDiscretion(e.getPlayer().getUniqueId()).setEating(false);
                }
            }
        }.runTaskLater(m_fireland, 30);
    }

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent e)
    {
        Player p = e.getPlayer();
        int isCamo = m_camoDetector.camoReduction(p);
        m_discretionManager.getDiscretion(p.getUniqueId()).setCamoReduction(isCamo);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e)
    {
        if (isSameBlock(e))
        {
            return;
        }

        Player p = e.getPlayer();
        DiscretionClass discretion = m_discretionManager.getDiscretion(p.getUniqueId());

        if (discretion.isListeningMovements())
        {
            return;
        }

        discretion.setMoving(true);
        discretion.setListeningMovements(true);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                discretion.setListeningMovements(false);
                discretion.setMoving(false);
            }
        }.runTaskLater(m_fireland, 10);
    }

    private boolean isSameBlock(PlayerMoveEvent e)
    {
        return e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockY() == e.getTo().getBlockY()
                && e.getFrom().getBlockZ() == e.getTo().getBlockZ();
    }
}
