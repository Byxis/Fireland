package fr.byxis.event;

import fr.byxis.faction.bunker.BunkerClass;
import fr.byxis.fireland.Fireland;
import fr.byxis.player.level.LevelStorage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.Inventory;

import java.util.Iterator;
import java.util.UUID;

import static fr.byxis.fireland.utilities.InGameUtilities.debugp;

public class SaveEvent implements Listener {

    private static Fireland main;
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
        Iterator<UUID> iterator = main.hashMapManager.getStorageMap().keySet().iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            if(main.hashMapManager.getStorageMap().containsKey(uuid)) {
                saveEnderchest(main.hashMapManager.getStorageMap().get(uuid), uuid);
                if(!Bukkit.getOfflinePlayer(uuid).isOnline()) {
                    iterator.remove();
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
        StringBuilder sb = new StringBuilder();
        for(String str : main.bunkerManager.getLoadedBunker().keySet())
        {
            sb.append('"').append(str).append('"').append("  ");
        }
        debugp(2, "Values : "+sb);
        for(BunkerClass bk : main.bunkerManager.getLoadedBunker().values())
        {
            debugp(2, "Saving storage of bunker "+bk.GetName());
            bk.GetStorage().SaveAllItems();
        }

        for(BunkerClass bk : main.bunkerManager.getLoadedBunker().values())
        {
            if(bk.GetPlayerInsideSize() <= 0)
            {
                debugp(2, "Suppression of bunker "+bk.GetName());
                main.bunkerManager.getLoadedBunker().remove(bk.GetName());
            }
        }

    }
}
