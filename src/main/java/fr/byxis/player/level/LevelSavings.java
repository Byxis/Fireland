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
        if(main.cfgm.getPlayerDB().contains("levelmap.uuid."+_uuid+".victims"))
        {
            for (String uuid : main.cfgm.getPlayerDB().getConfigurationSection("levelmap.uuid."+_uuid+".victims").getKeys(false))
            {
                m_victims.add(UUID.fromString(uuid));
            }
        }
        m_kills = main.cfgm.getPlayerDB().getInt("levelmap.uuid."+_uuid+".kills");
        m_zombieKills = main.cfgm.getPlayerDB().getInt("levelmap.uuid."+_uuid+".zombieKills");
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
        if(m_kills < 20)
            return 20;
        if(m_kills < 60)
            return 60;
        if(m_kills <120)
            return 120;
        return -1;
    }

    public void addKills(int _amount) {
        int old = m_kills;
        m_kills += _amount;
        if((old < 20 && m_kills > 20
        || old < 60 && m_kills > 60
        ||old < 120 && m_kills > 120) &&
        getPlayerLevel(m_uuid).getNation().equals(LevelStorage.Nation.Bannis))
        {
            getPlayerLevel(m_uuid).addRang(1);
        }
    }

    public int getZombieKills() {
        return m_zombieKills;
    }
    public int getCurrentMaxZombieKills()
    {
        if(m_zombieKills < 600)
            return 600;
        if(m_zombieKills < 1500)
            return 1500;
        if(m_zombieKills <3000)
            return 3000;
        return -1;
    }

    public void addZombieKills(int _amount) {
        int old = m_kills;
        m_zombieKills += _amount;
        if((old < 200 && m_zombieKills > 200
                || old < 500 && m_zombieKills > 500
                ||old < 1000 && m_zombieKills > 1000) &&
                getPlayerLevel(m_uuid).getNation().equals(LevelStorage.Nation.Etat))
        {
            getPlayerLevel(m_uuid).addRang(1);
        }
    }



    public void SaveAll(Fireland _main)
    {
        if(m_uuid != null)
        {
            for(UUID uuid : m_victims)
            {
                _main.cfgm.getPlayerDB().set("levelmap.uuid."+m_uuid+".victims", uuid);
            }
            _main.cfgm.getPlayerDB().set("levelmap.uuid."+m_uuid+".kills", m_kills);
            _main.cfgm.getPlayerDB().set("levelmap.uuid."+m_uuid+".zombieKills", m_zombieKills);
        }
    }
}
