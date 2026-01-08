package fr.byxis.event;

import static fr.byxis.fireland.utilities.InGameUtilities.debugp;

import fr.byxis.faction.bunker.BunkerClass;
import fr.byxis.fireland.Fireland;
import fr.byxis.player.level.LevelStorage;
import java.util.Iterator;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.Inventory;

public class SaveEvent implements Listener
{

    private static Fireland m_fireland;

    public SaveEvent(Fireland _main)
    {
        if (m_fireland == null)
        {
            SaveEvent.m_fireland = _main;
        }
    }

    private static void saveEnderchest(Inventory inv, UUID uuid)
    {
        if (inv != null && m_fireland.getHashMapManager().getStorageMap().containsKey(uuid))
        {
            for (int i = 0; i < inv.getSize(); i++)
            {
                m_fireland.getCfgm().getEnderchest().set("stockage." + uuid + "." + i, inv.getItem(i));
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
        if (m_fireland == null)
            return;

        if (m_fireland.getHashMapManager().getStorageMap().isEmpty())
        {
            return;
        }
        Iterator<UUID> iterator = m_fireland.getHashMapManager().getStorageMap().keySet().iterator();
        while (iterator.hasNext())
        {
            UUID uuid = iterator.next();
            if (m_fireland.getHashMapManager().getStorageMap().containsKey(uuid))
            {
                saveEnderchest(m_fireland.getHashMapManager().getStorageMap().get(uuid), uuid);
                if (!Bukkit.getOfflinePlayer(uuid).isOnline())
                {
                    iterator.remove();
                }
            }
        }

        m_fireland.getCfgm().saveEnderchest();
    }

    private static void saveKarma(UUID uuid)
    {
        if (m_fireland.getHashMapManager().getRangMap().containsKey(uuid))
        {
            FileConfiguration config = m_fireland.getCfgm().getKarmaDB();
            config.set(uuid.toString(), m_fireland.getHashMapManager().getRangMap().get(uuid).getRang());
            config.set("max." + uuid, m_fireland.getHashMapManager().getRangMap().get(uuid).getMax());
        }
    }

    private static void saveAllKarma()
    {
        if (m_fireland.getHashMapManager().getRangMap().isEmpty())
        {
            return;
        }
        for (UUID uuid : m_fireland.getHashMapManager().getRangMap().keySet())
        {
            saveKarma(uuid);
        }
        m_fireland.getCfgm().saveKarmaDB();
    }

    private static void saveAllFactionStorages()
    {
        StringBuilder sb = new StringBuilder();
        for (String str : m_fireland.getBunkerManager().getLoadedBunker().keySet())
        {
            sb.append('"').append(str).append('"').append("  ");
        }
        debugp(2, "Values :" + sb);
        for (BunkerClass bk : m_fireland.getBunkerManager().getLoadedBunker().values())
        {
            debugp(2, "Saving storage of bunker " + bk.getName());
            bk.getStorage().saveAllItems();
        }

        for (BunkerClass bk : m_fireland.getBunkerManager().getLoadedBunker().values())
        {
            if (bk.getPlayerInsideSize() <= 0)
            {
                debugp(2, "Suppression of bunker " + bk.getName());
                m_fireland.getBunkerManager().getLoadedBunker().remove(bk.getName());
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
