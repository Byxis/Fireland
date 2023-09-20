package fr.byxis.player.level;

import fr.byxis.db.DbConnection;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import static fr.byxis.player.level.LevelStorage.getPlayerLevel;

public class PlayerLevel {

    private UUID m_uuid;
    private int m_level;
    private int m_xp;
    private int m_rang;
    private LevelStorage.Nation m_nation;

    public PlayerLevel(Fireland main, UUID uuid)
    {
        m_uuid = uuid;
        DbConnection connectionDb = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = connectionDb.getConnection();
            //Préparation de la commande
            PreparedStatement getInfos = connection.prepareStatement("SELECT level, xp, nation, rang FROM player_level WHERE uuid = ?");
            getInfos.setString(1, uuid.toString());
            ResultSet rs = getInfos.executeQuery();
            if (rs.next())
            {
                m_level = rs.getInt(1);
                m_xp = rs.getInt(2);
                m_nation = LevelStorage.Nation.valueOf(rs.getString(3));
                m_rang = rs.getInt(4);
            }
            else
            {
                createPlayerLevel(connection, m_uuid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createPlayerLevel(Connection connection, UUID uuid) throws SQLException {
        m_level = 0;
        m_xp = 0;
        m_nation = LevelStorage.Nation.Neutre;
        m_rang = 0;

        PreparedStatement setInfos = connection.prepareStatement("INSERT INTO player_level(uuid, level, xp, nation) VALUES(?,?,?,?)");
        setInfos.setString(1, uuid.toString());
        setInfos.setInt(2, m_level);
        setInfos.setInt(3, m_xp);
        setInfos.setString(4, m_nation.name());

        setInfos.executeUpdate();
    }

    public int getLevel()
    {
        return m_level;
    }

    public int getXp()
    {
        return m_xp;
    }

    public void addLevel(int _level)
    {
        m_level+=_level;
    }

    public void setLevel(int _level)
    {
        m_level=_level;
    }

    public void addXp(Fireland _main, int _xp)
    {
        double booster = 1;
        if(_main.hashMapManager.getBooster() != null)
            booster = 1 + 0.25*_main.hashMapManager.getBooster().getLevel();
        addXp((int) (_xp*booster));
    }

    public void addXp(int _xp)
    {
        m_xp += _xp;
        int oldLevel = m_level;
        while(m_xp >= getRemainingXp() && getRemainingXp() != -1)
        {
            m_xp -= getRemainingXp();
            addLevel(1);
        }
        if(oldLevel < m_level)
        {
            passage("niveau", m_level);
        }
    }

    public void setXp(int _xp)
    {
        m_xp = _xp;
        int oldLevel = m_level;
        while(m_xp >= getRemainingXp() && getRemainingXp() != -1)
        {
            m_xp -= getRemainingXp();
            addLevel(1);
        }
        if(oldLevel < m_level)
        {
            Player p = Bukkit.getPlayer(m_uuid);
            InGameUtilities.playPlayerSound(p, "gun.hud.rangchange", SoundCategory.AMBIENT, 1, 1);
        }
    }

    public int getRemainingXp()
    {
        if(m_level < 25)
            return 20*(m_level+1);
        else if(m_level < 50)
            return 25*(m_level+1) - 20*25;
        else if(m_level < 75)
            return 30*(m_level+1) - 25*25 - 20*25;
        else if(m_level < 100)
            return 40*(m_level+1) - 30*25 - 25*25 - 20*25;
        else
            return -1;
    }

    public int getRang()
    {
        return m_rang;
    }

    public LevelStorage.Nation getNation() {
        return m_nation;
    }
    public void setNation(LevelStorage.Nation _nation)
    {
        m_nation = _nation;
    }

    public String getSringRang()
    {
        return switch(m_nation)
        {
            case Null -> "";
            case Neutre -> "Libre";
            case Bannis -> switch (getRang())
            {
                default -> "Vagabond";
                case 1 -> "Survivant";
                case 2 -> "Guerrier";
                case 3 -> "Chef de Guerre";
                case 4 -> "Tyrant";
            };
            case Etat -> switch (getRang())
            {
                default -> "Vagabond";
                case 1 -> "Survivant";
                case 2 -> "Soldat";
                case 3 -> "Colonel";
                case 4 -> "Héros";
            };
        };
    }

    public void Save(Fireland main)
    {
        DbConnection connectionDb = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = connectionDb.getConnection();
            //Préparation de la commande
            PreparedStatement updateInfos = connection.prepareStatement("UPDATE player_level SET level = ?, xp = ?, nation = ?, rang = ? WHERE uuid = ?");
            updateInfos.setInt(1, m_level);
            updateInfos.setInt(2, m_xp);
            updateInfos.setString(3, m_nation.name());
            updateInfos.setInt(4, m_rang);
            updateInfos.setString(5, m_uuid.toString());
            updateInfos.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UUID getUuid()
    {
        return m_uuid;
    }

    public boolean hasAccesstoShop(String _shop)
    {
        int rang = getRang();
        if(rang == 4)
            return true;
        if(rang <= 3)
        {
            if(_shop.contains("lourd") ||
                    _shop.equalsIgnoreCase("passr"))
                return false;
        }
        if(rang <= 2)
        {
            if(_shop.equalsIgnoreCase("passjr") ||
                    _shop.contains("assaut"))
                return false;
        }
        if(rang <= 1)
        {
            if(_shop.equalsIgnoreCase("passb") ||
                    _shop.contains("fusil"))
                return false;
        }
        if(rang <= 0)
        {
            if(_shop.equalsIgnoreCase("passv") ||
                    _shop.contains("smg"))
                return false;
        }
        return true;
    }

    public boolean hasAccessToReductions(String _shop)
    {
        if(m_nation.equals(LevelStorage.Nation.Bannis))
            return _shop.contains("_bannis");
        if(m_nation.equals(LevelStorage.Nation.Etat))
            return _shop.contains("_nation");
        return false;
    }

    public boolean hasAccessToAugmentation(String _shop)
    {
        if(m_nation.equals(LevelStorage.Nation.Bannis))
            return _shop.contains("_nation");
        if(m_nation.equals(LevelStorage.Nation.Etat))
            return _shop.contains("_bannis");
        return false;
    }

    public double getReduction()
    {
        return 0.05*getRang();
    }

    public void addRang(int _rang)
    {
        m_rang += _rang;
        setRangLimit();
        passage("rang", m_rang);
    }

    public void setRang(int _rang)
    {
        m_rang = _rang;
        setRangLimit();
    }

    public void setRangLimit()
    {
        if(m_rang < 0)
            m_rang = 0;
        if (m_rang > 4)
            m_rang = 4;
    }

    private void passage(String type, int amount)
    {
        Player p = Bukkit.getPlayer(m_uuid);
        InGameUtilities.playPlayerSound(p, "gun.hud.rangchange", SoundCategory.AMBIENT, 1, 1);
        p.sendTitle("", "§7Passage au "+type+ " "+ amount);
    }
}
