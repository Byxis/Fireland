package fr.byxis.analytics.events;

import com.codisimus.plugins.phatloots.events.PlayerLootEvent;
import fr.byxis.analytics.DataCollector;
import fr.byxis.analytics.LogEntry;
import fr.byxis.analytics.LogType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

public class ChestOpeningEvent implements Listener
{
    private DataCollector m_dataCollector;

    public ChestOpeningEvent(DataCollector _dataCollector)
    {
        m_dataCollector = _dataCollector;
    }

    @EventHandler
    public void onPhatLoot(PlayerLootEvent _event)
    {
        String lootName = _event.getPhatLoot().getName();

        LogType type = LogType.fromPhatLootName(lootName);
        LogEntry entry = new LogEntry(
                _event.getLooter().getUniqueId(),
                _event.getLooter().getLocation(),
                type,
                new java.sql.Date(System.currentTimeMillis())
        );

        m_dataCollector.logEvent(entry);
        if (type == LogType.UNKNOWN_LOOT)
        {
            Logger.getLogger("Fireland").warning("Unknown loot chest opened: " + lootName);
        }
    }
}
