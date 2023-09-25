package fr.byxis.player.level;

import fr.byxis.fireland.Fireland;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class LevelStorage {

    private static Fireland m_main;

    public enum Nation
    {
        Etat,
        Bannis,
        Neutre,
        Null
    }

    private static HashMap<UUID, PlayerLevel> m_levelMap;

    private static HashMap<UUID, LevelSavings> m_levelSavingsMap;

    public LevelStorage(Fireland main)
    {
        m_main = main;
        m_levelMap = new HashMap<>();
        m_levelSavingsMap = new HashMap<>();
        loadPlayerKillMap();
        main.getServer().getPluginManager().registerEvents(new LevelEvent(main), main);
        main.getCommand("level").setExecutor(new LevelCommand(main));
        main.getCommand("level").setTabCompleter(new LevelTabCompleter());
    }

    public static PlayerLevel getPlayerLevel(UUID _uuid)
    {
        if(!m_levelMap.containsKey(_uuid))
            m_levelMap.put(_uuid, new PlayerLevel(m_main, _uuid));
        return m_levelMap.get(_uuid);
    }

    public static void addPlayerXp(UUID _uuid, int _xp, Nation _nation)
    {
        if(!m_levelMap.containsKey(_uuid))
            m_levelMap.put(_uuid, new PlayerLevel(m_main, _uuid));
        PlayerLevel pl = m_levelMap.get(_uuid);
        if(pl.getNation().equals(_nation))
        {
            pl.addXp(m_main, (int) (_xp*1.5f));
        }
    }

    public static void addPlayerXp(UUID _uuid, int _xp, Nation _nation, boolean isOnly)
    {
        if(!m_levelMap.containsKey(_uuid))
            m_levelMap.put(_uuid, new PlayerLevel(m_main, _uuid));
        PlayerLevel pl = m_levelMap.get(_uuid);
        if(pl.getNation().equals(_nation) && isOnly)
        {
            pl.addXp(m_main, _xp);
        }
    }

    public static void SaveLevels()
    {
        if(m_levelMap.isEmpty()) return;
        for(PlayerLevel pl : m_levelMap.values())
        {
            pl.Save(m_main);
            if(!Bukkit.getOfflinePlayer(pl.getUuid()).isOnline())
                m_levelMap.remove(pl.getUuid());
        }
    }

    private void loadPlayerKillMap()
    {
        if(m_main.cfgm.getPlayerDB().contains("levelmap.date"))
        {
            Date date = (Date) m_main.cfgm.getPlayerDB().get("levelmap.date");
            boolean isNew = date.getDate() != (new Date()).getDate();

            if(m_main.cfgm.getPlayerDB().contains("levelmap.uuid"))
            {
                for(String uuid : m_main.cfgm.getPlayerDB()
                        .getConfigurationSection("levelmap.uuid").getKeys(false))
                {
                    if(uuid != null && !uuid.equalsIgnoreCase(""))
                    {
                        if(isNew)
                            m_main.cfgm.getPlayerDB().set("levelmap.uuid."+uuid+".victims", null);

                        if(m_main.cfgm.getPlayerDB().contains("levelmap.uuid."+uuid))
                        {
                            m_levelSavingsMap.put(UUID.fromString(uuid), new LevelSavings(UUID.fromString(uuid), m_main));
                        }
                    }

                }
                return;
            }
        }
        m_main.cfgm.getPlayerDB().set("levelmap.date", new Date());
    }

    public static void SavePlayerKillMap()
    {
        for(LevelSavings ls : m_levelSavingsMap.values())
        {
            ls.SaveAll(m_main);
        }
        m_main.cfgm.savePlayerDB();
    }

    public static void AddPlayerKill(Player killer, Player victim)
    {
        if(!m_levelSavingsMap.containsKey(killer.getUniqueId()))
            m_levelSavingsMap.put(killer.getUniqueId(), new LevelSavings(killer.getUniqueId(), m_main));

        if(!m_levelSavingsMap.get(killer.getUniqueId()).getVictims().contains(victim.getUniqueId()))
        {
            m_levelSavingsMap.get(killer.getUniqueId()).addVictim(victim.getUniqueId());
            m_levelSavingsMap.get(killer.getUniqueId()).addKills(1);
        }
    }

    public static LevelSavings GetPlayerLevelSavings(UUID p)
    {
        if(!m_levelSavingsMap.containsKey(p))
            m_levelSavingsMap.put(p, new LevelSavings(p, m_main));
        return m_levelSavingsMap.get(p);
    }

    public static void AddPlayerZombieKill(Player killer)
    {
        if(!m_levelSavingsMap.containsKey(killer.getUniqueId()))
            m_levelSavingsMap.put(killer.getUniqueId(), new LevelSavings(killer.getUniqueId(), m_main));
        m_levelSavingsMap.get(killer.getUniqueId()).addZombieKills(1);
    }

    public static boolean HasPlayerAlreadyKilled(Player killer, Player victim)
    {
        if(!m_levelSavingsMap.containsKey(killer.getUniqueId()))
            m_levelSavingsMap.put(killer.getUniqueId(), new LevelSavings(killer.getUniqueId(), m_main));
        return m_levelSavingsMap.get(killer.getUniqueId()).getVictims().contains(victim.getUniqueId());
    }
}
