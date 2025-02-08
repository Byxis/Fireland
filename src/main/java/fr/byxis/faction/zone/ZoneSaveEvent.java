package fr.byxis.faction.zone;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

public class ZoneSaveEvent implements Listener {

    private final DataZone data;
    public ZoneSaveEvent(DataZone _data) {
        this.data = _data;

    }

    @EventHandler
    public void saveEvent(WorldSaveEvent e)
    {
        if (!e.getWorld().getName().equalsIgnoreCase("world"))
            return;
        data.saveAll();
    }

}
