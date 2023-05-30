package fr.byxis.jeton;

import fr.byxis.main.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public abstract class JetonManager {
    private final Main main;

    public JetonManager(Main main)
    {
        this.main = main;
    }

    public void updatePlayer(UUID _uuid)
    {
        FileConfiguration jetonDB = main.cfgm.getJetonsDB();
        if(!jetonDB.contains(_uuid.toString()))
        {
            jetonDB.set(_uuid.toString(), 0);
            main.cfgm.saveJetonsDB();
        }
    }

    public int getJetonsPlayer(UUID _uuid)
    {
        FileConfiguration jetonDB = main.cfgm.getJetonsDB();
        return jetonDB.getInt(_uuid.toString());
    }

    public void setJetonsPlayer(UUID _uuid, int amount)
    {
        FileConfiguration jetonDB = main.cfgm.getJetonsDB();
        jetonDB.set(_uuid.toString(), amount);
        main.cfgm.saveJetonsDB();
    }

    public void addJetonsPlayer(UUID _uuid, int amount)
    {
        FileConfiguration jetonDB = main.cfgm.getJetonsDB();
        jetonDB.set(_uuid.toString(), jetonDB.getInt(_uuid.toString())+amount);
        main.cfgm.saveJetonsDB();
    }

    public void removeJetonsPlayer(UUID _uuid, int amount)
    {
        FileConfiguration jetonDB = main.cfgm.getJetonsDB();
        int jt = jetonDB.getInt(_uuid.toString());
        jt -= amount;
        jetonDB.set(_uuid.toString(), jt);
        main.cfgm.saveJetonsDB();
    }

}
