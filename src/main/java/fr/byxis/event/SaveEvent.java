package fr.byxis.event;

import fr.byxis.faction.housing.BunkerClass;
import fr.byxis.fireland.Fireland;
import fr.byxis.player.level.LevelStorage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class SaveEvent implements Listener {

    private static Fireland main = null;
    public SaveEvent(Fireland main) {
        SaveEvent.main = main;
    }

    @EventHandler
    public void ServeurSave(WorldSaveEvent e)
    {
        if(!e.getWorld().getName().equalsIgnoreCase("world"))
            return;
        SaveAllPlayerDatas();
    }

    public void onDisable()
    {
        SaveAllPlayerDatas();
    }

    public static void SaveAllPlayerDatas()
    {
        SaveAllEnderchest();
        SaveAllKarma();
        SaveAllFactionStorages();
        LevelStorage.SaveLevels();
        LevelStorage.SavePlayerKillMap();
    }

    private static void saveEnderchest(Inventory inv, UUID uuid)
    {
        if(inv != null && main.hashMapManager.getStorageMap().containsKey(uuid))
        {
            for (int i = 0; i < inv.getSize(); i++) {
                main.cfgm.getEnderchest().set("stockage."+uuid+"."+i, inv.getItem(i));
            }
        }
    }

    private static void SaveAllEnderchest()
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

    private static void SaveKarma(UUID uuid)
    {
        if(main.hashMapManager.getRangMap().containsKey(uuid))
        {
            FileConfiguration config = main.cfgm.getKarmaDB();
            config.set(uuid.toString(), main.hashMapManager.getRangMap().get(uuid).getRang());
            config.set("max."+ uuid, main.hashMapManager.getRangMap().get(uuid).getMax());
        }
    }

    private static void SaveAllKarma()
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

    private static void SaveAllFactionStorages()
    {
        for(BunkerClass bk : main.bunkerManager.getLoadedBunker().values())
        {
            bk.GetStorage().SaveAllItems();
        }
    }
}
