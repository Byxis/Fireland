package fr.byxis.jeton;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class JetonManager {
    private static Fireland main;
    private static jetonSql jt;

    public JetonManager(Fireland main)
    {
        jetonSql jt = new jetonSql(main, null);
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

    public static boolean payJetons(Player _p, int _amount, String _desc, boolean _update, boolean _doLog)
    {
        int jetons = getJetonsPlayer(_p.getUniqueId());
        if(jetons >= _amount)
        {
            int facture = jt.createFacture(_p.getUniqueId().toString(), _amount, _desc, _update);
            if(facture != -1)
            {
                removeJetonsPlayer(_p.getUniqueId(), _amount);
                InGameUtilities.sendPlayerInformation(_p, "Vous avez payé §b"+_amount+"\u26c1§r§7. " +
                        "(Facture n°"+facture+").");
                return  true;
            }
            InGameUtilities.sendPlayerError(_p, "Une erreur est survenue pendant la création de la facture. " +
                    "Vous n'avez pas été débité. Merci de contacter le staff pour résoudre ce problème.");
        }
        else if(_doLog)
        {
            InGameUtilities.sendPlayerError(_p, "Vous n'avez pas assez de jetons.");
        }
        return false;
    }

}
