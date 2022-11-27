package fr.byxis.event;

import fr.byxis.main.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class SaveEvent implements Listener {

    private final Main main;
    public SaveEvent(Main main) {
        this.main = main;
    }

    @EventHandler
    public void ServeurSave(WorldSaveEvent e)
    {
        main.getLogger().info("Saved banks");
        for(UUID uuid : main.storageMap.keySet())
        {
            saveEnderchest(main.storageMap.get(uuid), uuid);
        }
    }

    public void onDisable()
    {
        main.getLogger().info("Saved banks");
        for(UUID uuid : main.storageMap.keySet())
        {
            saveEnderchest(main.storageMap.get(uuid), uuid);
        }
    }

    private void saveEnderchest(Inventory inv , UUID uuid)
    {
        if(inv != null)
        {
            FileConfiguration config = main.cfgm.getEnderchest();
            for (int i = 0; i < inv.getSize(); i++) {
                config.set("stockage."+uuid+"."+i, inv.getItem(i));
            }
            main.cfgm.saveEnderchest();
        }

    }
}
