package fr.byxis.zone;

import fr.byxis.db.DbConnection;
import fr.byxis.faction.FactionFunctions;
import fr.byxis.jeton.jetonsCommandManager;
import fr.byxis.main.Main;
import fr.byxis.main.utilities.BasicUtilities;
import fr.byxis.zone.zoneclass.FactionCapturingClass;
import fr.byxis.zone.zoneclass.ZoneClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataZone {

    private final ZoneConfigFileManager configManager;
    public List<ZoneClass> zones;
    public HashMap<String, List<FactionCapturingClass>> zoneInCapture;
    private Main main;

    public DataZone(Main main)
    {
        this.configManager = new ZoneConfigFileManager(main);
        this.zones = new ArrayList<>();
        this.zoneInCapture = new HashMap<>();
        this.main = main;
        InitZones();
    }

    private void InitZones()
    {
        configManager.setup();
        for (String zone : configManager.getConfig().getConfigurationSection("zone").getKeys(false))
        {
            Location loc = new Location(Bukkit.getWorld("world"),
                    configManager.config.getDouble("zone."+zone+".x"),
                    configManager.config.getDouble("zone."+zone+".y"),
                    configManager.config.getDouble("zone."+zone+".z"));
            ZoneClass zoneClass = new ZoneClass(zone, loc,
                    configManager.config.getInt("zone."+zone+".teleportation-price"),
                    configManager.config.getInt("zone."+zone+".daily-gain"),
                    configManager.config.getInt("zone."+zone+".final-gain.dollars"),
                    configManager.config.getInt("zone."+zone+".final-gain.jetons"),
                    configManager.config.getInt("zone."+zone+".privation-duration"),
                    configManager.config.getInt("zone."+zone+".auto-release"),
                    configManager.config.getInt("zone."+zone+".capture-time"), false);
            zones.add(zoneClass);
        }
        ActualizeClaiming();
        SaveAll();
    }

    public void SaveAll()
    {
        ActualizeClaiming();
        for (ZoneClass zone: zones) {
            main.getLogger().info(zone.getName()+" : "+zone.isClaimed());
            if(zone.isClaimed())
            {
                final long time = zone.getClaimedAt().getTime();
                Timestamp releaseDate = new Timestamp(time+ TimeUnit.HOURS.toMillis(zone.getAutoRelease()));
                main.getLogger().info(releaseDate.toString());
                main.getLogger().info(zone.getClaimedAt().toString());
                Timestamp today = new Timestamp(System.currentTimeMillis());
                if(today.after(releaseDate))
                {
                    CaptureZone.changeAnimationStep(-1, zone, "§r");
                    if(zoneInCapture.containsKey(zone.getName()))
                    {
                        for(FactionCapturingClass factionCapturingClass : zoneInCapture.get(zone.getName()))
                        {
                            if(factionCapturingClass.getName().equalsIgnoreCase(zone.getClaimer()))
                            {
                                factionCapturingClass.setProgression(0);
                                break;
                            }
                        }
                    }
                    rewardFaction(zone, zone.getClaimer(), true);
                    RemoveSavedClaiming(zone.getName(), zone.getClaimer());
                    zone.unclaim();
                    return;
                }
                else
                {
                    Timestamp rewardedAt = GetRewardNumber(zone);
                    if(rewardedAt != null && rewardedAt.getDay() != today.getDay())
                    {
                        rewardFaction(zone, zone.getClaimer(), false);
                        SetRewardNumber(zone, today);
                    }
                    SaveClaiming(zone.getClaimer(), zone.getClaimedAt(), zone.getName());
                }
            }
        }
    }

    private void SaveClaiming(String factionName, Timestamp claimedAt, String zone)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

        try {
            final Connection connection = firelandConnection.getConnection();
            //Préparation de la commande
            final PreparedStatement isInDb = connection.prepareStatement("SELECT capture_zone.faction_name FROM capture_zone WHERE capture_zone.zone = ?");
            isInDb.setString(1, zone);
            ResultSet rs = isInDb.executeQuery();
            if(rs.next())
            {

            }
            else
            {
                final PreparedStatement ps = connection.prepareStatement("INSERT INTO capture_zone(zone,faction_name,capture_time,rewarded_at) VALUES (?,?,?,?)");
                ps.setString(1, zone);
                ps.setString(2, factionName);
                ps.setTimestamp(3, claimedAt);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void RemoveSavedClaiming(String zone, String faction)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

        try {
            final Connection connection = firelandConnection.getConnection();
            //Préparation de la commande
            final PreparedStatement isInDb = connection.prepareStatement("SELECT faction_name, capture_time FROM capture_zone WHERE zone = ?");
            isInDb.setString(1, zone);

            ResultSet rs = isInDb.executeQuery();
            if(rs.next())
            {
                final PreparedStatement hasEverClaimedThisZone = connection.prepareStatement("SELECT duration FROM faction_zone WHERE zone = ? AND faction_name = ?");
                hasEverClaimedThisZone.setString(1, zone);
                hasEverClaimedThisZone.setString(2, faction);
                ResultSet result = hasEverClaimedThisZone.executeQuery();
                if(rs.next())
                {
                    final PreparedStatement update = connection.prepareStatement("UPDATE INTO faction_zone SET duration = ? WHERE faction_name = ? AND zone = ?");
                    update.setInt(1, result.getInt(1)+ (int) ((System.currentTimeMillis()-rs.getTimestamp(2).getTime())+rs.getTimestamp(2).getTime()));
                    update.setString(2, zone);
                    update.setString(3, faction);
                    update.executeUpdate();
                }
                else
                {
                    final PreparedStatement update = connection.prepareStatement("INSERT INTO faction_zone VALUES (?,?,?)");
                    update.setString(1, zone);
                    update.setString(2, faction);
                    update.setInt(3, (int) ((System.currentTimeMillis()-rs.getTimestamp(2).getTime())+rs.getTimestamp(2).getTime()));
                    update.executeUpdate();
                }

                final PreparedStatement delete = connection.prepareStatement("DELETE FROM capture_zone WHERE zone = ?");
                delete.setString(1, zone);
                delete.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void AddCapturing(String zone, String factionName, Player p)
    {
        BasicUtilities.playPlayerSound(p.getPlayer(), "entity.player.levelup", SoundCategory.AMBIENT, 1, 0);
        if(zoneInCapture.containsKey(zone))
        {
            boolean founded = false;
            for(FactionCapturingClass factionCapturing : zoneInCapture.get(zone))
            {
                if(factionCapturing.getName().equalsIgnoreCase(factionName))
                {
                    factionCapturing.addPlayerList(p);
                    founded = true;
                    break;
                }
            }
            if(!founded)
            {
                List<Player> players = new ArrayList<>();
                players.add(p);

                FactionCapturingClass factionCapturing = new FactionCapturingClass(factionName, players, 0);
                zoneInCapture.get(zone).add(factionCapturing);
            }
        }
        else
        {
            List<Player> players = new ArrayList<>();
            players.add(p);

            FactionCapturingClass factionCapturing = new FactionCapturingClass(factionName, players, 0);

            List<FactionCapturingClass> factionsCapturing = new ArrayList<>();
            factionsCapturing.add(factionCapturing);
            zoneInCapture.put(zone, factionsCapturing);
        }
    }
    public void RemoveCapturing(String zone, String factionName, Player p)
    {
        if(zoneInCapture.containsKey(zone))
        {
            for(FactionCapturingClass factionCapturing : zoneInCapture.get(zone))
            {
                if(factionCapturing.getName().equalsIgnoreCase(factionName))
                {
                    factionCapturing.removePlayerList(p);
                    if(factionCapturing.getProgression() <= 0 && factionCapturing.getPlayerList().size() < 1)
                    {
                        zoneInCapture.get(zone).remove(factionCapturing);
                    }
                    break;
                }
            }
        }
    }

    private void rewardFaction(ZoneClass zone, String factionName, boolean isFinal)
    {
        FactionFunctions ff = new FactionFunctions(main, null);
        if(isFinal)
        {
            ff.deposit(factionName, zone.getFinalDollarsGain());
            jetonsCommandManager jt = new jetonsCommandManager(main);
            jt.addJetonsPlayer(ff.getFactionInfo(factionName).getLeader(), zone.getFinalJetonsGain());
        }
        else
        {
            ff.deposit(factionName, zone.getDaily_gain());
        }
    }

    private void ActualizeClaiming()
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

        try {
            final Connection connection = firelandConnection.getConnection();
            //Préparation de la commande
            for(ZoneClass zone : this.zones)
            {

                final PreparedStatement isInDb = connection.prepareStatement("SELECT zone,faction_name,capture_time FROM capture_zone WHERE zone = ?");
                isInDb.setString(1, zone.getName());
                ResultSet rs = isInDb.executeQuery();
                if (rs.next()) {
                    zone.setClaimed(rs.getString(2), rs.getTimestamp(3));
                }

                if(zone.isClaimed() && !zone.isClaimable())
                {
                    final long time = zone.getClaimedAt().getTime();
                    Timestamp releaseDate = new Timestamp(time+ TimeUnit.HOURS.toMillis(zone.getPrivationDuration()));
                    main.getLogger().info(releaseDate.toString());
                    main.getLogger().info(zone.getClaimedAt().toString());
                    Timestamp today = new Timestamp(System.currentTimeMillis());
                    if(today.after(releaseDate))
                    {
                        zone.setClaimable();
                        List<Player> list = new ArrayList<>();
                        FactionCapturingClass factionCapturingClass = new FactionCapturingClass(zone.getClaimer(), list, 100);
                        if(zoneInCapture.containsKey(zone.getName()))
                        {

                            zoneInCapture.get(zone.getName()).add(factionCapturingClass);
                        }
                        else
                        {
                            List<FactionCapturingClass> factionCapturingClassList = new ArrayList<>();
                            factionCapturingClassList.add(factionCapturingClass);
                            zoneInCapture.put(zone.getName(), factionCapturingClassList);
                        }
                        zone.setClaimer(factionCapturingClass.getName());
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Timestamp GetRewardNumber(ZoneClass zone)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

        try {
            final Connection connection = firelandConnection.getConnection();
            //Préparation de la commande
            final PreparedStatement isInDb = connection.prepareStatement("SELECT rewarded_at FROM capture_zone WHERE zone = ?");
            isInDb.setString(1, zone.getName());
            ResultSet rs = isInDb.executeQuery();
            if (rs.next()) {
                return rs.getTimestamp(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void SetRewardNumber(ZoneClass zone, Timestamp time)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

        try {
            final Connection connection = firelandConnection.getConnection();
            //Préparation de la commande
            final PreparedStatement insert = connection.prepareStatement("UPDATE capture_zone SET rewarded_at = ? WHERE zone = ? AND faction_name = ? ");
            insert.setTimestamp(1, time);
            insert.setString(2, zone.getName());
            insert.setString(3, zone.getClaimer());
            insert.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

