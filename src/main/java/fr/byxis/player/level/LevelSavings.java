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

    public LevelSavings(UUID uuid, Fireland main)
    {
        m_victims = (ArrayList<UUID>) main.cfgm.getPlayerDB().get("levelmap.uuid."+uuid+".victims");
        m_kills = main.cfgm.getPlayerDB().getInt("levelmap.uuid."+uuid+".kills");
        m_zombieKills = main.cfgm.getPlayerDB().getInt("levelmap.uuid."+uuid+".zombieKills");
    }

    public ArrayList<UUID> getVictims() {
        return m_victims;
    }

    public void addVictim(UUID _victim) {
        m_victims.add(_victim);
    }

    public int getKills() {
        return m_kills;
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
        _main.cfgm.getPlayerDB().set("levelmap.uuid."+m_uuid+".victims", m_victims);
        _main.cfgm.getPlayerDB().set("levelmap.uuid."+m_uuid+".kills", m_kills);
        _main.cfgm.getPlayerDB().set("levelmap.uuid."+m_uuid+".zombieKills", m_zombieKills);
    }
}
