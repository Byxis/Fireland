package fr.byxis.fireland;

import fr.byxis.booster.BoosterClass;
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
    private HashMap<String, Inventory> storageFactionMap;
    private HashMap<UUID, String> factionPrefixMap;
    private BoosterClass booster;

    public HashMapManager() {
    }

    public void Init()
    {
        this.factionMap = new HashMap<>();
        this.storageMap = new HashMap<>();
        this.discretionMap = new HashMap<>();
        this.rangMap = new HashMap<>();
        this.storageFactionMap = new HashMap<>();
        this.factionPrefixMap = new HashMap<>();
        this.booster = null;
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

    public HashMap<String, Inventory> getStorageFactionMap() {
        return storageFactionMap;
    }

    public void addStorageFactionMap(String name, Inventory i) {
        this.storageFactionMap.put(name,i);
    }

    public void removeStorageFactionMap(String name) {
        this.storageFactionMap.remove(name);
    }

    public void replaceStorageFactionMap(String name, Inventory i) {
        this.storageFactionMap.replace(name, i);
    }

    public HashMap<UUID, String> getFactionPrefixMap() {
        return factionPrefixMap;
    }

    public void addFactionPrefixMap(UUID uuid, String prefix) {
        this.factionPrefixMap.put(uuid, prefix);
    }

    public void removeFactionPrefixMap(UUID uuid) {
        this.factionPrefixMap.remove(uuid);
    }

    public BoosterClass getBooster() {
        return booster;
    }

    public void setBooster(BoosterClass booster) {
        this.booster = booster;
    }
}
