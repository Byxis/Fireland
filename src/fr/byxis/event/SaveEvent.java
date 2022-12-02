package fr.byxis.event;

import fr.byxis.main.Main;
import org.bukkit.Bukkit;
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
        SaveAllEnderchest();
        SaveAllKarma();
    }

    public void onDisable()
    {
        SaveAllEnderchest();
        SaveAllKarma();
    }

    private void saveEnderchest(Inventory inv , UUID uuid)
    {
        if(inv != null)
        {
            for (int i = 0; i < inv.getSize(); i++) {
                main.cfgm.getEnderchest()   .set("stockage."+uuid+"."+i, inv.getItem(i));
            }
        }
    }

    private void SaveAllEnderchest()
    {
        for(UUID uuid :main.hashMapManager.getStorageMap().keySet())
        {
            saveEnderchest(main.hashMapManager.getStorageMap().get(uuid), uuid);
            if(!Bukkit.getOfflinePlayer(uuid).isOnline())
            {
                main.hashMapManager.getStorageMap().remove(uuid);
            }
        }
        main.cfgm.saveEnderchest();
    }

    private void SaveKarma(UUID uuid)
    {
        FileConfiguration config = main.cfgm.getKarmaDB();
        config.set(uuid.toString(), main.hashMapManager.getRangMap().get(uuid).getRang());
        config.set("max."+ uuid, main.hashMapManager.getRangMap().get(uuid).getMax());
    }

    private void SaveAllKarma()
    {
        for(UUID uuid :main.hashMapManager.getRangMap().keySet())
        {
            SaveKarma(uuid);
        }
        main.cfgm.saveKarmaDB();
    }
}
