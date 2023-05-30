package fr.byxis.zone;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

public class ZoneSaveEvent implements Listener {

    private DataZone data;
    public ZoneSaveEvent(DataZone data) {
        this.data = data;

    }

    @EventHandler
    public void SaveEvent(WorldSaveEvent e)
    {
        data.SaveAll();
    }


}
