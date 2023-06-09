package fr.byxis.jeton;

import fr.byxis.fireland.Fireland;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public abstract class JetonManager {
    private static Fireland main;

    public JetonManager(Fireland main)
    {
        JetonManager.main = main;
    }

    public static void updatePlayer(UUID _uuid)
    {
        FileConfiguration jetonDB = main.cfgm.getJetonsDB();
        if(!jetonDB.contains(_uuid.toString()))
        {
            jetonDB.set(_uuid.toString(), 0);
            main.cfgm.saveJetonsDB();
        }
    }

    public static int getJetonsPlayer(UUID _uuid)
    {
        FileConfiguration jetonDB = main.cfgm.getJetonsDB();
        return jetonDB.getInt(_uuid.toString());
    }

    public static void setJetonsPlayer(UUID _uuid, int amount)
    {
        FileConfiguration jetonDB = main.cfgm.getJetonsDB();
        jetonDB.set(_uuid.toString(), amount);
        main.cfgm.saveJetonsDB();
    }

    public static void addJetonsPlayer(UUID _uuid, int amount)
    {
        FileConfiguration jetonDB = main.cfgm.getJetonsDB();
        jetonDB.set(_uuid.toString(), jetonDB.getInt(_uuid.toString())+amount);
        main.cfgm.saveJetonsDB();
    }

    public static void removeJetonsPlayer(UUID _uuid, int amount)
    {
        FileConfiguration jetonDB = main.cfgm.getJetonsDB();
        int jt = jetonDB.getInt(_uuid.toString());
        jt -= amount;
        jetonDB.set(_uuid.toString(), jt);
        main.cfgm.saveJetonsDB();
    }

}
