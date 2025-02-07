package fr.byxis.fireland;

import fr.byxis.player.booster.BoosterClass;
import fr.byxis.player.discretion.DiscretionClass;
import fr.byxis.player.karma.PlayerKarmaClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.sql.Date;
import java.util.HashMap;
import java.util.UUID;

public class HashMapManager {
    private HashMap<UUID, String> factionMap;
    private HashMap<UUID, Inventory> storageMap;
    private static HashMap<UUID, DiscretionClass> discretionMap;
    private HashMap<UUID, PlayerKarmaClass> rangMap;
    private HashMap<String, Inventory> storageFactionMap;
    private HashMap<UUID, String> factionPrefixMap;
    private HashMap<UUID, Boolean> isTeleporting;
    private static HashMap<UUID, Boolean> purify;
    private BoosterClass booster;
    private static Fireland main;

    public HashMapManager(Fireland main) {
        HashMapManager.main = main;
        Init();
    }

    public void Init()
    {
        this.factionMap = new HashMap<>();
        this.storageMap = new HashMap<>();
        discretionMap = new HashMap<>();
        this.rangMap = new HashMap<>();
        this.storageFactionMap = new HashMap<>();
        this.factionPrefixMap = new HashMap<>();
        this.booster = null;
        loadBooster();
        this.isTeleporting = new HashMap<>();
        purify = new HashMap<>();
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

    public static HashMap<UUID, DiscretionClass> getDiscretionMap() {
        return discretionMap;
    }

    public static void addDiscretionMap(UUID uuid) {
        discretionMap.put(uuid, new DiscretionClass());
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

    public static boolean canPurify(Player p)
    {
        UUID uuid = p.getUniqueId();
        if (p.hasPermission("fireland.thirst.1") || p.hasPermission("fireland.thirst.2") || p.hasPermission("fireland.thirst.3"))
        {
            if (!purify.containsKey(uuid))
            {
                purify.put(uuid, true);
            }
            return purify.get(uuid);
        }
        return false;
    }

    public static void setPurify(Player p, boolean b)
    {

        UUID uuid = p.getUniqueId();
        if (purify.containsKey(uuid))
        {
            purify.replace(uuid, b);
        }
        else
        {
            purify.put(uuid, b);
        }
    }

    public boolean isTeleporting(UUID uuid) {
        if (isTeleporting.containsKey(uuid))
        {
            return isTeleporting.get(uuid);
        }
        return false;
    }

    public void addTeleporting(UUID uuid) {
        if (isTeleporting.containsKey(uuid))
        {
            isTeleporting.replace(uuid, true);
        }
        else
        {
            isTeleporting.put(uuid, true);
        }
    }

    public void removeTeleporting(UUID uuid) {
        if (isTeleporting.containsKey(uuid))
        {
            isTeleporting.replace(uuid, false);
        }
        else
        {
            isTeleporting.put(uuid, false);
        }
    }

    private void loadBooster()
    {
        if (main.cfgm.getPlayerDB().contains("booster.player"))
        {
            booster = new BoosterClass(
                    new Date(main.cfgm.getPlayerDB().getLong("booster.started")),
                    new Date(main.cfgm.getPlayerDB().getLong("booster.finished")),
                    UUID.fromString(main.cfgm.getPlayerDB().getString("booster.player")),
                    main.cfgm.getPlayerDB().getInt("booster.level"));
        }
    }

    public void saveBooster()
    {
        if (booster != null && booster.getFinished().after(new java.util.Date()))
        {
            main.cfgm.getPlayerDB().set("booster.started", booster.getStarted().getTime());
            main.cfgm.getPlayerDB().set("booster.finished", booster.getFinished().getTime());
            main.cfgm.getPlayerDB().set("booster.player", booster.getUuid().toString());
            main.cfgm.getPlayerDB().set("booster.level", booster.getLevel());
            main.cfgm.savePlayerDB();
        }
        else
        {
            main.cfgm.getPlayerDB().set("booster", null);
            main.cfgm.savePlayerDB();
        }
    }
}
