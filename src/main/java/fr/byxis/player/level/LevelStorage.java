package fr.byxis.player.level;

import fr.byxis.fireland.Fireland;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LevelStorage
{

    private static Fireland m_main;

    public enum Nation
    {
        Etat, Bannis, Neutre, Null
    }

    private static Map<UUID, PlayerLevel> m_levelMap;

    private static Map<UUID, LevelSavings> m_levelSavingsMap;

    public LevelStorage(Fireland main)
    {
        if (m_main == null)
        {
            m_main = main;
            initializeDatabase();
        }
        if (m_levelMap == null)
            m_levelMap = new ConcurrentHashMap<>();
        if (m_levelSavingsMap == null)
            m_levelSavingsMap = new ConcurrentHashMap<>();
        loadPlayerKillMap();
        main.getServer().getPluginManager().registerEvents(new LevelEvent(main), main);
        main.getCommand("level").setExecutor(new LevelCommand(main));
        main.getCommand("level").setTabCompleter(new LevelTabCompleter());
    }

    public static PlayerLevel getPlayerLevel(UUID _uuid)
    {
        if (!m_levelMap.containsKey(_uuid))
            m_levelMap.put(_uuid, new PlayerLevel(m_main, _uuid));
        return m_levelMap.get(_uuid);
    }

    public static void addPlayerXp(UUID _uuid, int _xp, Nation _nation)
    {
        if (!m_levelMap.containsKey(_uuid))
            m_levelMap.put(_uuid, new PlayerLevel(m_main, _uuid));
        PlayerLevel pl = m_levelMap.get(_uuid);
        if (pl.getNation().equals(_nation))
        {
            pl.addXp(m_main, (int) (_xp * 1.5f));
        }
        else
        {
            pl.addXp(m_main, (int) (_xp * 0.5f));
        }
    }

    public static void addPlayerXp(UUID _uuid, int _xp)
    {
        if (!m_levelMap.containsKey(_uuid))
            m_levelMap.put(_uuid, new PlayerLevel(m_main, _uuid));
        PlayerLevel pl = m_levelMap.get(_uuid);
        pl.addXp(m_main, _xp);
    }

    public static void saveLevels()
    {
        if (m_levelMap.isEmpty())
            return;
        List<UUID> uuidList = new ArrayList<UUID>();
        for (PlayerLevel pl : m_levelMap.values())
        {
            pl.save(m_main);
            if (!Bukkit.getOfflinePlayer(pl.getUuid()).isOnline())
                uuidList.add(pl.getUuid());
        }
        for (UUID uuid : uuidList)
        {
            m_levelMap.remove(uuid);
        }
    }

    public static void savePlayerKillMap()
    {
        for (LevelSavings ls : m_levelSavingsMap.values())
        {
            ls.saveAll(m_main);
        }
        m_main.getCfgm().savePlayerDB();
    }

    public static void addPlayerKill(Player killer, Player victim)
    {
        if (!m_levelSavingsMap.containsKey(killer.getUniqueId()))
            m_levelSavingsMap.put(killer.getUniqueId(), new LevelSavings(killer.getUniqueId(), m_main));

        if (!m_levelSavingsMap.get(killer.getUniqueId()).getVictims().contains(victim.getUniqueId()))
        {
            m_levelSavingsMap.get(killer.getUniqueId()).addVictim(victim.getUniqueId());
            m_levelSavingsMap.get(killer.getUniqueId()).addKills(1);
        }
    }

    public static LevelSavings getPlayerLevelSavings(UUID p)
    {
        if (!m_levelSavingsMap.containsKey(p))
            m_levelSavingsMap.put(p, new LevelSavings(p, m_main));
        return m_levelSavingsMap.get(p);
    }

    public static void addPlayerZombieKill(Player killer)
    {
        if (!m_levelSavingsMap.containsKey(killer.getUniqueId()))
            m_levelSavingsMap.put(killer.getUniqueId(), new LevelSavings(killer.getUniqueId(), m_main));
        m_levelSavingsMap.get(killer.getUniqueId()).addZombieKills(1);
    }

    public static boolean hasPlayerAlreadyKilled(Player killer, Player victim)
    {
        if (!m_levelSavingsMap.containsKey(killer.getUniqueId()))
            m_levelSavingsMap.put(killer.getUniqueId(), new LevelSavings(killer.getUniqueId(), m_main));

        return m_levelSavingsMap.get(killer.getUniqueId()).getVictims().contains(victim.getUniqueId());
    }

    private void loadPlayerKillMap()
    {
        if (m_main.getCfgm().getPlayerDB().contains("levelmap.date"))
        {
            Date date = (Date) m_main.getCfgm().getPlayerDB().get("levelmap.date");
            boolean isNew = date.getDate() != (new Date()).getDate();

            if (m_main.getCfgm().getPlayerDB().contains("levelmap.uuid"))
            {
                for (String uuid : m_main.getCfgm().getPlayerDB().getConfigurationSection("levelmap.uuid").getKeys(false))
                {
                    if (uuid != null && !uuid.equalsIgnoreCase(""))
                    {
                        if (isNew)
                            m_main.getCfgm().getPlayerDB().set("levelmap.uuid." + uuid + ".victims", null);

                        if (m_main.getCfgm().getPlayerDB().contains("levelmap.uuid." + uuid))
                        {
                            m_levelSavingsMap.put(UUID.fromString(uuid), new LevelSavings(UUID.fromString(uuid), m_main));
                        }
                    }
                }
                return;
            }
        }
        m_main.getCfgm().getPlayerDB().set("levelmap.date", new Date());
    }

    private void initializeDatabase()
    {
        try (Connection con = m_main.getDatabaseManager().getFirelandConnection().getConnection();
                Statement stmt = con.createStatement())
        {
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS player_level (" +
                    "uuid VARCHAR(36) NOT NULL PRIMARY KEY," +
                    "level INT(4) NOT NULL," +
                    "xp INT(10) NOT NULL," +
                    "rang INT(1) NOT NULL DEFAULT 0," +
                    "nation VARCHAR(16) NOT NULL," +
                    "can_change TINYINT(1) NOT NULL DEFAULT 1," +
                    "FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE" +
                    ")"
            );


            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS player_level_rewards (" +
                    "uuid VARCHAR(36) NOT NULL," +
                    "level INT(3) NOT NULL," +
                    "PRIMARY KEY (uuid, level)," +
                    "FOREIGN KEY (uuid) REFERENCES player_level(uuid) ON DELETE CASCADE" +
                    ")"
            );

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
