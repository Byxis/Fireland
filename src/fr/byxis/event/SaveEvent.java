package fr.byxis.event;

import fr.byxis.faction.FactionFunctions;
import fr.byxis.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
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
        SaveAll();
    }

    public void onDisable()
    {
        SaveAll();
    }

    private void SaveAll()
    {
        SaveAllEnderchest();
        SaveAllKarma();
        SaveAllFactionStorages();
    }

    private void saveEnderchest(Inventory inv , UUID uuid)
    {
        if(inv != null && main.hashMapManager.getStorageMap().containsKey(uuid))
        {
            for (int i = 0; i < inv.getSize(); i++) {
                main.cfgm.getEnderchest().set("stockage."+uuid+"."+i, inv.getItem(i));
            }
        }
    }

    private void SaveAllEnderchest()
    {
        if(main.hashMapManager.getStorageMap().isEmpty())
        {
            return;
        }
        for(UUID uuid :main.hashMapManager.getStorageMap().keySet())
        {
            if(main.hashMapManager.getStorageMap().containsKey(uuid))
            {
                saveEnderchest(main.hashMapManager.getStorageMap().get(uuid), uuid);
                if(!Bukkit.getOfflinePlayer(uuid).isOnline())
                {
                    main.hashMapManager.getStorageMap().remove(uuid);
                }
            }

        }
        main.cfgm.saveEnderchest();
    }

    private void SaveKarma(UUID uuid)
    {
        if(main.hashMapManager.getRangMap().containsKey(uuid))
        {
            FileConfiguration config = main.cfgm.getKarmaDB();
            config.set(uuid.toString(), main.hashMapManager.getRangMap().get(uuid).getRang());
            config.set("max."+ uuid, main.hashMapManager.getRangMap().get(uuid).getMax());
        }
    }

    private void SaveAllKarma()
    {
        if(main.hashMapManager.getRangMap().isEmpty())
        {
            return;
        }
        for(UUID uuid : main.hashMapManager.getRangMap().keySet())
        {
            SaveKarma(uuid);
        }
        main.cfgm.saveKarmaDB();
    }

    private void SaveAllFactionStorages()
    {
        FactionFunctions ff = new FactionFunctions(main, null);
        for(String name : main.hashMapManager.getStorageFactionMap().keySet())
        {
            ff.SaveAllItemsFactionStorage(name, main.hashMapManager.getStorageFactionMap().get(name));
            boolean found = false;
            for(Player p : Bukkit.getOnlinePlayers())
            {
                String playerFactionName = ff.playerFactionName(p);
                if(!playerFactionName.equals("") && playerFactionName.equalsIgnoreCase(name))
                {
                    found = true;
                }
            }
            if(!found)
            {
                main.hashMapManager.removeStorageFactionMap(name);
            }
        }
    }
}
