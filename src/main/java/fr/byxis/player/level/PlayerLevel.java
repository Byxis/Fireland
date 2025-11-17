package fr.byxis.player.level;

import fr.byxis.db.DbConnection;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.PermissionUtilities;
import fr.skytasul.quests.api.QuestsAPI;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

import static fr.byxis.jeton.JetonManager.addJetonsPlayer;

public class PlayerLevel {

    private final UUID m_uuid;
    private int m_level;
    private int m_xp;
    private int m_rang;
    private boolean m_canChange;
    private LevelStorage.Nation m_nation;

    private final HashMap<Integer, Boolean> m_rewardsClaimed;

    public PlayerLevel(Fireland main, UUID uuid)
    {
        m_uuid = uuid;
        m_rewardsClaimed = new HashMap<Integer, Boolean>();
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try (Connection connection = firelandConnection.getConnection())
        {
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
        m_level += _level;
    }

    public void setLevel(int _level)
    {
        m_level = _level;
    }

    public void addXp(Fireland _main, int _xp)
    {
        double booster = 1;
        if (_main.getHashMapManager().getBooster() != null)
            booster = 1 + 0.25 * _main.getHashMapManager().getBooster().getLevel();
        addXp((int) Math.ceil((_xp * booster)));
    }

    public void addXp(int _xp)
    {
        m_xp += _xp;
        int oldLevel = m_level;
        while (m_xp >= getRemainingXp() && getRemainingXp() != -1)
        {
            m_xp -= getRemainingXp();
            addLevel(1);
        }
        if (oldLevel < m_level)
        {
            passage("niveau", m_level);
        }
    }

    public void setXp(int _xp)
    {
        m_xp = _xp;
        int oldLevel = m_level;
        while (m_xp >= getRemainingXp() && getRemainingXp() != -1)
        {
            m_xp -= getRemainingXp();
            addLevel(1);
        }
        if (oldLevel < m_level)
        {
            Player p = Bukkit.getPlayer(m_uuid);
            InGameUtilities.playPlayerSound(p, "gun.hud.rangchange", SoundCategory.AMBIENT, 1, 1);
        }

        for (int i = oldLevel; i <= m_level; i++)
            givePlayerMission(i);
    }

    public int getRemainingXp()
    {
        if (m_level < 25)
            return 20 * (m_level + 1);
        else if (m_level < 50)
            return 25 * (m_level + 1 - 24) + 20 * 24;
        else if (m_level < 75)
            return 30 * (m_level + 1 - 49) + 25 * 24 + 20 * 25;
        else if (m_level < 100)
            return 40 * (m_level + 1 - 74) + 30 * 24 + 25 * 25 + 20 * 25;
        else
            return -1;
    }

    public int getLevelCap()
    {
        return m_level / 25;
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

    public String getStringRank()
    {
        return switch (m_nation)
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

    public void save(Fireland main)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try (Connection connection = firelandConnection.getConnection())
        {
            //Préparation de la commande
            PreparedStatement updateInfos = connection.prepareStatement("UPDATE player_level SET level = ?, xp = ?, nation = ?, rang = ?, can_change = ? WHERE uuid = ?");
            updateInfos.setInt(1, m_level);
            updateInfos.setInt(2, m_xp);
            updateInfos.setString(3, m_nation.name());
            updateInfos.setInt(4, m_rang);
            updateInfos.setBoolean(5, m_canChange);
            updateInfos.setString(6, m_uuid.toString());
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
        int cap = getLevelCap();
        if (cap == 4)
            return true;
        if (cap <= 3)
        {
            if (_shop.contains("lourd"))
                return false;
        }
        if (cap <= 2)
        {
            if (_shop.contains("passr") || _shop.contains("anticaire") || _shop.contains("assaut"))
                return false;
        }
        if (cap <= 1)
        {
            if (_shop.contains("passj") || _shop.contains("fusil"))
                return false;
        }
        if (cap <= 0)
        {
            return !_shop.contains("passb") && !_shop.contains("smg");
        }
        return true;
    }

    public int getLevelAccesstoShop(String _shop)
    {
        if (_shop.contains("anticaire"))
            return 100;
        if (_shop.contains("passr") || _shop.contains("assaut"))
            return 75;
        if (_shop.contains("passj") || _shop.contains("fusil"))
            return 50;
        if (_shop.contains("passb") || _shop.contains("smg"))
            return 25;
        return 0;
    }

    public boolean hasAccessToReductions(String _shop)
    {
        if (m_nation.equals(LevelStorage.Nation.Bannis))
            return _shop.contains("_bannis");
        if (m_nation.equals(LevelStorage.Nation.Etat))
            return _shop.contains("_nation");
        return false;
    }

    public boolean hasAccessToAugmentation(String _shop)
    {
        if (m_nation.equals(LevelStorage.Nation.Bannis))
            return _shop.contains("_nation");
        if (m_nation.equals(LevelStorage.Nation.Etat))
            return _shop.contains("_bannis");
        return false;
    }

    public double getReduction()
    {
        return 0.05 * getRang();
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
        if (m_rang < 0)
            m_rang = 0;
        if (m_rang > 4)
            m_rang = 4;
    }

    private void passage(String type, int amount)
    {
        Player p = Bukkit.getPlayer(m_uuid);
        InGameUtilities.playPlayerSound(p, "gun.hud.rangchange", SoundCategory.AMBIENT, 1, 1);
        p.sendTitle("", "§7Passage au " + type + " " + amount);
    }

    public boolean hasClaimedReward(Fireland _main, int _lvl)
    {
        if (!m_rewardsClaimed.containsKey(_lvl))
        {
            final DbConnection firelandConnection = _main.getDatabaseManager().getFirelandConnection();
        try (Connection connection = firelandConnection.getConnection())
        {
                //Préparation de la commande
                PreparedStatement hasClaimedReward = connection.prepareStatement("SELECT * FROM player_level_rewards WHERE uuid = ? AND level = ?");
                hasClaimedReward.setString(1, m_uuid.toString());
                hasClaimedReward.setInt(2, _lvl);
                ResultSet rs = hasClaimedReward.executeQuery();
                if (rs.next())
                {
                    m_rewardsClaimed.put(_lvl, true);
                }
                else
                {
                    m_rewardsClaimed.put(_lvl, false);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return m_rewardsClaimed.get(_lvl);
    }

    private void setClaimedRewards(Fireland _main, int _lvl)
    {
        m_rewardsClaimed.put(_lvl, true);
        DbConnection connectionDb = _main.getDatabaseManager().getFirelandConnection();
        try (Connection connection = connectionDb.getConnection())
        {
            PreparedStatement setClaimedReward = connection.prepareStatement("INSERT INTO player_level_rewards(uuid, level) VALUES (?,?)");
            setClaimedReward.setString(1, m_uuid.toString());
            setClaimedReward.setInt(2, _lvl);
            setClaimedReward.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void claimRewards(Fireland _main, int _lvl)
    {
        Player p = Bukkit.getPlayer(m_uuid);
        if (!hasClaimedReward(_main, _lvl))
        {
            int jetons = getRewardsJetons(_lvl);
            int money = getRewardsMoney(_lvl);
            String item = getRewardsItems(_lvl);
            String pet = getRewardsPets(_lvl);
            if (jetons > 0)
                InGameUtilities.sendPlayerSucces(p, "Vous avez récupéré " + jetons + "§f⛁§a et " + money + "§f$§a.");
            else
                InGameUtilities.sendPlayerSucces(p, "Vous avez récupéré " + money + "§f$§a.");
            if (!item.isEmpty())
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), item.replace("Player", p.getName()));

            if (!pet.isEmpty())
            {
                PermissionUtilities.addPermission(p, pet);
                InGameUtilities.sendPlayerSucces(p, "Vous avez débloqué le familier §6" + getRewardsPetsFormatted(_lvl) + "§a.");
            }

            addJetonsPlayer(m_uuid, jetons);
            Fireland.getEco().depositPlayer(p, money);
            setClaimedRewards(_main, _lvl);
        }
        else
        {
            InGameUtilities.sendPlayerError(p, "Vous avez déjà récupéré cette récompense.");
        }
    }


    public int getRewardsJetons(int lvl)
    {
        return switch (lvl)
        {
            default -> 0;
            case 25 -> 10;
            case 50 -> 15;
            case 75 -> 25;
            case 100 -> 50;
        };
    }

    public int getRewardsMoney(int lvl)
    {
        return lvl * 10;
    }

    public String getRewardsItems(int lvl)
    {
        return switch (lvl)
        {
            default -> "";
            case 5 -> "wm give Player colt";
            case 10 -> "wm give Player SWModel686";
            case 20 -> "wm give Player glock17";
            case 30 -> "wm give Player uzi";
            case 40 -> "wm give Player mp9";
            case 50 -> "wm give Player thompson";
            case 60 -> "wm give Player mosin";
            case 70 -> "wm give Player benellinova";
            case 80 -> "wm give Player hk416";
            case 90 -> "wm give Player m60";
            case 100 -> "wm give Player aa12";
        };
    }

    public String getRewardsPets(int lvl)
    {
        return switch (lvl)
        {
            case 20 -> "fireland.pet.chien-tactique";
            case 40 -> "fireland.pet.dogbot";
            case 60 -> "fireland.pet.drone";
            default -> "";
        };
    }

    public String getRewardsPetsFormatted(int lvl)
    {
        String hasPet = getRewardsPets(lvl);
        return Arrays.stream(hasPet.split("\\.")[2].replace("-", " ").split(" "))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    public void givePlayerMission(int _level)
    {
        ArrayList<Integer> questIds = new ArrayList<Integer>();
        switch (_level)
        {
            //case 1 -> questIds.add(1);
        };
        if (!questIds.isEmpty())
        {
            Player p = Bukkit.getPlayer(m_uuid);
            if (questIds.size() > 1)
            {
                InGameUtilities.sendPlayerSucces(p, "Vous avez débloqué une nouvelle quête !");
            }
            else
            {
                InGameUtilities.sendPlayerSucces(p, "Vous avez débloqué de nouvelles quêtes !");
            }
            for (Integer id : questIds)
            {
                QuestsAPI.getQuests().getQuest(id).start(p, true);
            }
        }
    }

    public int getJetonPriceNationChange()
    {
        return switch (getRang())
        {
            case 0 -> 5;
            case 1 -> 10;
            case 2 -> 15;
            case 3 -> 20;
            default -> 25;
        };
    }

    public int getMoneyPriceNationChange()
    {
        return switch (getRang())
        {
            case 0 -> 1000;
            case 1 -> 3000;
            case 2 -> 5000;
            case 3 -> 7000;
            default -> 10000;
        };
    }

    public void setCanChange(boolean _canChange) {
        this.m_canChange = _canChange;
    }

    public Boolean canChange() {
        return m_canChange;
    }
}
