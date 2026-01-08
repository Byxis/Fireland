package fr.byxis.analytics.events;

import fr.byxis.analytics.DataCollector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

public class DataSaveEvent implements Listener
{
    private DataCollector m_collector;
    public DataSaveEvent(DataCollector _collector)
    {
        this.m_collector = _collector;
    }

    @EventHandler
    public void onWorldSaveEvent(WorldSaveEvent _event)
    {
        if (_event.getWorld().getName().equalsIgnoreCase("world"))
            m_collector.flushToDB();
    }

}
