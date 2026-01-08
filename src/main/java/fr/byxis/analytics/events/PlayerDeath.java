package fr.byxis.analytics.events;

import fr.byxis.analytics.DataCollector;
import fr.byxis.analytics.LogEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.sql.Date;

public class PlayerDeath implements Listener
{
    private DataCollector m_dataCollector;

    public PlayerDeath(DataCollector _dataCollector)
    {
        this.m_dataCollector = _dataCollector;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent _event)
    {
        LogEntry deathEntry = new LogEntry(
                _event.getEntity().getUniqueId(),
                _event.getEntity().getLocation(),
                fr.byxis.analytics.LogType.PLAYER_DEATH,
                new Date(System.currentTimeMillis())
        );
        m_dataCollector.logEvent(deathEntry);

        if (_event.getEntity().getKiller() != null
                && _event.getEntity().getLastDamageCause() != null
                && _event.getEntity().getLastDamageCause().getEntity() instanceof Player _killer
                && _killer.getUniqueId() != _event.getEntity().getUniqueId())
        {
            LogEntry killEntry = new LogEntry(
                    _event.getEntity().getKiller().getUniqueId(),
                    _event.getEntity().getKiller().getLocation(),
                    fr.byxis.analytics.LogType.PLAYER_KILL,
                    new Date(System.currentTimeMillis())
            );
            m_dataCollector.logEvent(killEntry);
        }
    }
}
