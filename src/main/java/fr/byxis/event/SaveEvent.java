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

    public SaveEvent(Fireland _main) {
        if (main == null)
        {
            SaveEvent.main = _main;
        }
    }

    private static void saveEnderchest(Inventory inv, UUID uuid)
    {
        if (inv != null && main.getHashMapManager().getStorageMap().containsKey(uuid))
        {
            for (int i = 0; i < inv.getSize(); i++)
            {
                main.getCfgm().getEnderchest().set("stockage." + uuid + "." + i, inv.getItem(i));
            }
        }
    }

    public void onDisable()
    {
        saveAllPlayerDatas();
    }

    public static void saveAllPlayerDatas()
    {
        saveAllEnderchest();
        saveAllKarma();
        saveAllFactionStorages();
        LevelStorage.saveLevels();
        LevelStorage.savePlayerKillMap();
    }

    private static void saveAllEnderchest()
    {
        if (main.getHashMapManager().getStorageMap().isEmpty())
        {
            return;
        }
        Iterator<UUID> iterator = main.getHashMapManager().getStorageMap().keySet().iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            if (main.getHashMapManager().getStorageMap().containsKey(uuid))
            {
                saveEnderchest(main.getHashMapManager().getStorageMap().get(uuid), uuid);
                if (!Bukkit.getOfflinePlayer(uuid).isOnline())
                {
                    iterator.remove();
                }
            }
        }

        main.getCfgm().saveEnderchest();
    }

    private static void saveKarma(UUID uuid)
    {
        if (main.getHashMapManager().getRangMap().containsKey(uuid))
        {
            FileConfiguration config = main.getCfgm().getKarmaDB();
            config.set(uuid.toString(), main.getHashMapManager().getRangMap().get(uuid).getRang());
            config.set("max." + uuid, main.getHashMapManager().getRangMap().get(uuid).getMax());
        }
    }

    private static void saveAllKarma()
    {
        if (main.getHashMapManager().getRangMap().isEmpty())
        {
            return;
        }
        for (UUID uuid : main.getHashMapManager().getRangMap().keySet())
        {
            saveKarma(uuid);
        }
        main.getCfgm().saveKarmaDB();
    }

    private static void saveAllFactionStorages()
    {
        StringBuilder sb = new StringBuilder();
        for (String str : main.getBunkerManager().getLoadedBunker().keySet())
        {
            sb.append('"').append(str).append('"').append("  ");
        }
        debugp(2, "Values :" + sb);
        for (BunkerClass bk : main.getBunkerManager().getLoadedBunker().values())
        {
            debugp(2, "Saving storage of bunker " + bk.getName());
            bk.getStorage().saveAllItems();
        }

        for (BunkerClass bk : main.getBunkerManager().getLoadedBunker().values())
        {
            if (bk.getPlayerInsideSize() <= 0)
            {
                debugp(2, "Suppression of bunker " + bk.getName());
                main.getBunkerManager().getLoadedBunker().remove(bk.getName());
            }
        }
    }

    @EventHandler
    public void serveurSave(WorldSaveEvent e)
    {
        if (!e.getWorld().getName().equalsIgnoreCase("world"))
            return;
        saveAllPlayerDatas();
    }
}
