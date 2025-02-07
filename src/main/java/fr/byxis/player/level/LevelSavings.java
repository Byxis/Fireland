package fr.byxis.player.level;

import fr.byxis.fireland.Fireland;

import java.util.ArrayList;
import java.util.UUID;

import static fr.byxis.player.level.LevelStorage.getPlayerLevel;

public class LevelSavings {

    private UUID m_uuid;
    private ArrayList<UUID> m_victims;
    private int m_kills;
    private int m_zombieKills;

    public LevelSavings(UUID _uuid, Fireland main)
    {
        m_uuid = _uuid;

        m_victims = new ArrayList<>();
        if (main.cfgm.getPlayerDB().contains("levelmap.uuid." + _uuid + ".victims"))
        {
            for (String uuid : main.cfgm.getPlayerDB().getConfigurationSection("levelmap.uuid." + _uuid + ".victims").getKeys(false))
            {
                m_victims.add(UUID.fromString(uuid));
            }
        }
        m_kills = main.cfgm.getPlayerDB().getInt("levelmap.uuid." + _uuid + ".kills");
        m_zombieKills = main.cfgm.getPlayerDB().getInt("levelmap.uuid." + _uuid + ".zombieKills");
    }

    public ArrayList<UUID> getVictims() {
        return m_victims;
    }

    public void addVictim(UUID _victim) {
        m_victims.add(_victim);
    }

    public int getKills()
    {
        return m_kills;
    }

    public int getCurrentMaxKills()
    {
        if (m_kills <= 0)
            return 1;
        else if (m_kills < 60)
            return 60;
        else if (m_kills < 150)
            return 150;
        else if (m_kills < 300)
            return 300;
        return -1;
    }

    public int getKillsRank()
    {
        if (m_kills <= 0)
            return 0;
        else if (m_kills < 60)
            return 1;
        else if (m_kills < 150)
            return 2;
        else if (m_kills < 300)
            return 3;
        return 4;
    }

    public void addKills(int _amount) {
        int old = getCurrentMaxKills();
        m_zombieKills += _amount;
        if (getCurrentMaxKills() > old
                &&
           getPlayerLevel(m_uuid).getNation().equals(LevelStorage.Nation.Bannis)
                && getCurrentMaxKills() != -1
        )
        {
            getPlayerLevel(m_uuid).setRang(getKillsRank());
        }
    }

    public int getZombieKills() {
        return m_zombieKills;
    }
    public int getCurrentMaxZombieKills()
    {
        if (m_zombieKills <= 0)
            return 1;
        else if (m_zombieKills < 1500)
            return 1500;
        else if (m_zombieKills < 5000)
            return 5000;
        else if (m_zombieKills < 10000)
            return 10000;
        return -1;
    }

    public int getZombieKillsRank()
    {
        if (m_zombieKills <= 0)
            return 0;
        else if (m_zombieKills < 600)
            return 1;
        else if (m_zombieKills < 1500)
            return 2;
        else if (m_zombieKills < 3000)
            return 3;
        return 4;
    }

    public void addZombieKills(int _amount) {
        int old = getCurrentMaxZombieKills();
        m_zombieKills += _amount;
        if (getCurrentMaxZombieKills() > old
                &&
           getPlayerLevel(m_uuid).getNation().equals(LevelStorage.Nation.Etat)
                && getCurrentMaxZombieKills() != -1
        )
        {
            getPlayerLevel(m_uuid).setRang(getZombieKillsRank());
        }
    }



    public void SaveAll(Fireland _main)
    {
        if (m_uuid != null)
        {
            for (UUID uuid : m_victims)
            {
                _main.cfgm.getPlayerDB().set("levelmap.uuid." + m_uuid + ".victims " + uuid, 1);
            }
            _main.cfgm.getPlayerDB().set("levelmap.uuid." + m_uuid + ".kills", m_kills);
            _main.cfgm.getPlayerDB().set("levelmap.uuid." + m_uuid + ".zombieKills", m_zombieKills);
        }
    }
}
