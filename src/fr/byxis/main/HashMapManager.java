package fr.byxis.main;

import fr.byxis.discretion.DiscretionClass;
import fr.byxis.karma.PlayerKarmaClass;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.UUID;

public class HashMapManager {
    private HashMap<UUID, String> factionMap;
    private HashMap<UUID, Inventory> storageMap;
    private HashMap<UUID, DiscretionClass> discretionMap;
    private HashMap<UUID, PlayerKarmaClass> rangMap;

    public void Init()
    {
        this.factionMap = new HashMap<UUID, String>();
        this.storageMap = new HashMap<UUID,Inventory>();
        this.discretionMap = new HashMap<UUID,DiscretionClass>();
        this.rangMap = new HashMap<UUID,PlayerKarmaClass>();
    }

    public HashMap<UUID,String> getFactionMap()
    {
        return factionMap;
    }
    public void setFactionMap(HashMap<UUID,String> h)
    {
        this.factionMap = h;
    }
    public void addFactionMap(UUID uuid, String s)
    {
        this.factionMap.put(uuid, s);
    }

    public void removeFactionMap(UUID uuid)
    {
        this.factionMap.remove(uuid);
    }

    public HashMap<UUID,Inventory> getStorageMap()
    {
        return storageMap;
    }

    public void setStorageMap(HashMap<UUID,Inventory> h)
    {
        this.storageMap = h;
    }

    public void addStorageMap(UUID uuid,Inventory i)
    {
        this.storageMap.put(uuid, i);
    }

    public void replaceStorageMap(UUID uuid, Inventory i)
    {
        this.storageMap.replace(uuid, i);
    }

    public HashMap<UUID, DiscretionClass> getDiscretionMap() {
        return discretionMap;
    }

    public void addDiscretionMap(UUID uuid) {
        this.discretionMap.put(uuid, new DiscretionClass());
    }

    public void removeDiscretionMap(UUID uuid) {
        this.discretionMap.remove(uuid, new DiscretionClass());
    }

    public void replaceDiscretionMap(UUID uuid, DiscretionClass discretion) {
        this.discretionMap.replace(uuid, discretion);
    }

    public HashMap<UUID, PlayerKarmaClass> getRangMap() {
        return rangMap;
    }

    public void setRangMap(HashMap<UUID, PlayerKarmaClass> rangMap) {
        this.rangMap = rangMap;
    }
}
