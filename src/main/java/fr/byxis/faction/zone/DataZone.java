package fr.byxis.faction.zone;

import fr.byxis.db.DbConnection;
import fr.byxis.faction.faction.FactionPlayerInformation;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.jeton.JetonManager;
import fr.byxis.jeton.jetonsCommandManager;
import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.fireland.Fireland;
import fr.byxis.faction.zone.zoneclass.FactionCapturingClass;
import fr.byxis.faction.zone.zoneclass.ZoneClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DataZone {

    public final ZoneConfigFileManager configManager;
    public List<ZoneClass> zones;
    public HashMap<String, List<FactionCapturingClass>> zoneInCapture;
    public HashMap<String, List<Player>> m_zoneEnter;
    public HashMap<String, Boolean> m_zoneEnterBool;
    private final Fireland main;

    public DataZone(Fireland main) {
        this.main = main;
        this.configManager = new ZoneConfigFileManager(main);
        this.zones = new ArrayList<>();
        this.zoneInCapture = new HashMap<>();
        m_zoneEnter = new HashMap<>();
        m_zoneEnterBool = new HashMap<>();
        InitZones();
    }

    private void InitZones() {
        configManager.setup();
        for (String zone : configManager.getConfig().getConfigurationSection("zone").getKeys(false)) {
            Location loc = new Location(Bukkit.getWorld(configManager.config.getString("zone." + zone + ".world")),
                    configManager.config.getDouble("zone." + zone + ".x"),
                    configManager.config.getDouble("zone." + zone + ".y"),
                    configManager.config.getDouble("zone." + zone + ".z"));
            ZoneClass zoneClass = new ZoneClass(zone, loc,
                    configManager.config.getInt("zone." + zone + ".teleportation-price"),
                    configManager.config.getInt("zone." + zone + ".daily-gain"),
                    configManager.config.getInt("zone." + zone + ".final-gain.dollars"),
                    configManager.config.getInt("zone." + zone + ".final-gain.jetons"),
                    configManager.config.getDouble("zone." + zone + ".privation-duration"),
                    configManager.config.getDouble("zone." + zone + ".auto-release"),
                    configManager.config.getDouble("zone." + zone + ".capture-time"), true);
            zones.add(zoneClass);
        }
        ActualizeClaiming();
        SaveAll();
    }

    public void SaveAll() {
        for (ZoneClass zone : zones) {
            if (zone.isClaimed()) {
                final long time = zone.getClaimedAt().getTime();
                Timestamp releaseDate = new Timestamp(time + TimeUnit.HOURS.toMillis((long) zone.getAutoRelease()));
                Timestamp today = new Timestamp(System.currentTimeMillis());
                if (today.after(releaseDate)) {
                    zoneInCapture.get(zone.getName()).clear();
                    rewardFaction(zone, zone.getClaimer(), true);
                    RemoveSavedClaiming(zone.getName(), zone.getClaimer());

                    SaveTiming(zone.getClaimer(), zone.getClaimedAt(), zone.getName());
                    zone.unclaim();

                    CaptureZone.changeAnimationStep(-1, zone, "§r");
                    return;
                } else {
                    Timestamp rewardedAt = GetRewardNumber(zone);
                    if (rewardedAt != null && rewardedAt.getDay() != today.getDay()) {
                        rewardFaction(zone, zone.getClaimer(), false);
                        SetRewardNumber(zone, today);
                    }
                    SaveClaiming(zone.getClaimer(), zone.getClaimedAt(), zone.getName());
                    FactionFunctions ff = new FactionFunctions(main, null);

                    CaptureZone.changeAnimationStep(-1, zone, ff.getFactionInfo(zone.getClaimer()).getColorcode());
                }
            } else {

                RemoveSavedClaiming(zone.getName(), null);
                CaptureZone.changeAnimationStep(-1, zone, "§r");
            }
        }
        ActualizeClaiming();
    }

    private void SaveClaiming(String factionName, Timestamp claimedAt, String zone) {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

        try {
            final Connection connection = firelandConnection.getConnection();
            //Préparation de la commande
            PreparedStatement isInDb = connection.prepareStatement("SELECT capture_zone.faction_name FROM capture_zone WHERE capture_zone.zone = ?");
            isInDb.setString(1, zone);
            ResultSet rs = isInDb.executeQuery();
            if (rs.next()) {
                final PreparedStatement ps = connection.prepareStatement("UPDATE capture_zone SET faction_name = ?, capture_time = ?, rewarded_at = ? WHERE zone = ?");
                ps.setString(1, factionName);
                ps.setTimestamp(2, claimedAt);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                ps.setString(4, zone);
                ps.executeUpdate();
            } else {
                final PreparedStatement ps = connection.prepareStatement("INSERT INTO capture_zone(zone,faction_name,capture_time,rewarded_at) VALUES (?,?,?,?)");
                ps.setString(1, zone);
                ps.setString(2, factionName);
                ps.setTimestamp(3, claimedAt);
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void SaveTiming(String factionName, Timestamp claimedAt, String zone) {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

        try {

            final Connection connection = firelandConnection.getConnection();
            PreparedStatement isInDb = connection.prepareStatement("SELECT faction_zone.duration FROM faction_zone WHERE faction_zone.zone = ? AND faction_zone.faction_name =?");
            isInDb.setString(1, zone);
            isInDb.setString(2, factionName);
            ResultSet rs = isInDb.executeQuery();
            long diff = (new Timestamp(System.currentTimeMillis())).getTime() - claimedAt.getTime();
            ;
            if (rs.next()) {
                final PreparedStatement ps = connection.prepareStatement("UPDATE faction_zone SET faction_zone.duration = ? WHERE zone = ? AND faction_name =?");
                ps.setLong(1, rs.getLong(1) + diff);
                ps.setString(2, zone);
                ps.setString(3, factionName);
                ps.executeUpdate();
            } else {
                final PreparedStatement ps = connection.prepareStatement("INSERT INTO faction_zone(faction_name,zone,duration) VALUES (?,?,?)");
                ps.setString(2, zone);
                ps.setString(1, factionName);
                ps.setInt(3, (int) diff);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void RemoveSavedClaiming(String zone, String faction) {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

        try {
            final Connection connection = firelandConnection.getConnection();
            //Préparation de la commande
            final PreparedStatement isInDb = connection.prepareStatement("SELECT faction_name, capture_time FROM capture_zone WHERE zone = ?");
            isInDb.setString(1, zone);

            ResultSet rs = isInDb.executeQuery();
            if (rs.next()) {
                /*if (faction != null && !faction.equalsIgnoreCase("")) {
                    final PreparedStatement hasEverClaimedThisZone = connection.prepareStatement("SELECT duration FROM faction_zone WHERE zone = ? AND faction_name = ?");
                    hasEverClaimedThisZone.setString(1, zone);
                    hasEverClaimedThisZone.setString(2, faction);
                    ResultSet result = hasEverClaimedThisZone.executeQuery();
                    if (rs.next()) {
                        final PreparedStatement update = connection.prepareStatement("UPDATE faction_zone SET duration = ? WHERE faction_name = ? AND zone = ?");
                        update.setInt(1, result.getInt(1) + (int) ((System.currentTimeMillis() - rs.getTimestamp(2).getTime()) + rs.getTimestamp(2).getTime()));
                        update.setString(2, zone);
                        update.setString(3, faction);
                        update.executeUpdate();
                    } else {
                        final PreparedStatement update = connection.prepareStatement("INSERT INTO faction_zone VALUES (?,?,?)");
                        update.setString(1, zone);
                        update.setString(2, faction);
                        update.setInt(3, (int) ((System.currentTimeMillis() - rs.getTimestamp(2).getTime()) + rs.getTimestamp(2).getTime()));
                        update.executeUpdate();
                    }
                }*/
                final PreparedStatement delete = connection.prepareStatement("DELETE FROM capture_zone WHERE zone = ?");
                delete.setString(1, zone);
                delete.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void AddCapturing(String zone, String factionName, Player p) {
        InGameUtilities.playPlayerSound(p.getPlayer(), "entity.player.levelup", SoundCategory.AMBIENT, 1, 0);
        if (zoneInCapture.containsKey(zone)) {
            boolean founded = false;
            for (FactionCapturingClass factionCapturing : zoneInCapture.get(zone)) {
                if (factionCapturing.getName().equalsIgnoreCase(factionName)) {
                    factionCapturing.addPlayerList(p);
                    founded = true;
                    break;
                }
            }
            if (!founded) {
                List<Player> players = new ArrayList<>();
                players.add(p);

                FactionCapturingClass factionCapturing = new FactionCapturingClass(factionName, players, 0);
                zoneInCapture.get(zone).add(factionCapturing);
            }
        } else {
            List<Player> players = new ArrayList<>();
            players.add(p);

            FactionCapturingClass factionCapturing = new FactionCapturingClass(factionName, players, 0);

            List<FactionCapturingClass> factionsCapturing = new ArrayList<>();
            factionsCapturing.add(factionCapturing);
            zoneInCapture.put(zone, factionsCapturing);
        }
    }

    public boolean isCapturing(String zoneName, String factionName) {
        if (zoneInCapture.containsKey(zoneName)) {
            FactionCapturingClass faction = null;
            double progression = 0;
            for (FactionCapturingClass factionCapturing : zoneInCapture.get(zoneName)) {
                if (factionCapturing.getName().equalsIgnoreCase(factionName)) {
                    faction = factionCapturing;
                    progression = factionCapturing.getProgression();
                    break;
                }
            }
            if (faction == null) {
                return false;
            }
            ZoneClass zone = null;

            for (ZoneClass zoneTemp : zones) {
                if (zoneTemp.getName().equalsIgnoreCase(zoneName)) {
                    zone = zoneTemp;
                    break;
                }
            }

            if (zone == null) {
                return false;
            }
            if (zone.isClaimed() && zone.getClaimer().equalsIgnoreCase(factionName)) {
                return progression >= 90;
            }
            return progression < 0;
        }
        return false;
    }

    public void RemoveCapturing(String zone, String factionName, Player p) {
        if (zoneInCapture.containsKey(zone)) {
            for (FactionCapturingClass factionCapturing : zoneInCapture.get(zone)) {
                if (factionCapturing.getName().equalsIgnoreCase(factionName)) {
                    factionCapturing.removePlayerList(p);
                    if (factionCapturing.getProgression() <= 0 && factionCapturing.getPlayerList().size() < 1) {
                        zoneInCapture.get(zone).remove(factionCapturing);
                    }
                    break;
                }
            }
        }
    }

    private void rewardFaction(ZoneClass zone, String factionName, boolean isFinal) {
        FactionFunctions ff = new FactionFunctions(main, null);
        if (isFinal) {
            ff.deposit(factionName, zone.getFinalDollarsGain());
            if (ff.getPlayersFromFaction(factionName) != null) {
                for (FactionPlayerInformation player : ff.getPlayersFromFaction(factionName)) {
                    JetonManager.addJetonsPlayer(player.getUuid(), zone.getFinalJetonsGain());
                }
            }
        } else {
            ff.deposit(factionName, zone.getDaily_gain());
        }
    }

    private void ActualizeClaiming() {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            //Preparation de la commande
            for (ZoneClass zone : this.zones) {
                final PreparedStatement isInDb = connection.prepareStatement("SELECT zone,faction_name,capture_time FROM capture_zone WHERE zone = ?");
                isInDb.setString(1, zone.getName());
                ResultSet rs = isInDb.executeQuery();
                if (rs.next()) {
                    zone.setClaimed(rs.getString(2), rs.getTimestamp(3));
                }
                if (zone.isClaimed() && !zone.isClaimable()) {
                    final long time = zone.getClaimedAt().getTime();
                    Timestamp releaseDate = new Timestamp(time + TimeUnit.HOURS.toMillis((long) zone.getPrivationDuration()));
                    Timestamp today = new Timestamp(System.currentTimeMillis());
                    if (today.after(releaseDate)) {
                        zone.setClaimable();
                        List<Player> list = new ArrayList<>();
                        FactionCapturingClass factionCapturingClass = new FactionCapturingClass(zone.getClaimer(), list, 90);
                        zone.setClaimer(factionCapturingClass.getName());
                        if (zoneInCapture.containsKey(zone.getName())) {
                            boolean founded = false;
                            for (FactionCapturingClass faction : zoneInCapture.get(zone.getName())) {
                                if (faction.getName().equalsIgnoreCase(factionCapturingClass.getName())) {
                                    founded = true;
                                    break;
                                }
                            }
                            if (!founded && zoneInCapture.get(zone.getName()).isEmpty()) {
                                zoneInCapture.get(zone.getName()).add(factionCapturingClass);
                            }
                        } else {
                            List<FactionCapturingClass> factionCapturingClassList = new ArrayList<>();
                            factionCapturingClassList.add(factionCapturingClass);
                            zoneInCapture.put(zone.getName(), factionCapturingClassList);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Timestamp GetRewardNumber(ZoneClass zone) {
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

    private void SetRewardNumber(ZoneClass zone, Timestamp time) {
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

    public void sendTextToPlayerZoneEnter(String zone, String text) {
        if (m_zoneEnter.get(zone).isEmpty()) return;
        for (Player p : m_zoneEnter.get(zone)) {
            InGameUtilities.sendPlayerInformation(p, text);
        }
    }

    public void playSoundToPlayerZoneEnter(String zone, Sound sound, SoundCategory soundCategory, int vol, int pitch) {
        if (m_zoneEnter.get(zone).isEmpty()) return;
        for (Player p : m_zoneEnter.get(zone)) {
            InGameUtilities.playPlayerSound(p, sound, soundCategory, vol, pitch);
        }
    }

    public void addPlayerToZoneEnter(String zone, Player p) {
        if (m_zoneEnter.containsKey(zone)) {
            m_zoneEnter.get(zone).add(p);
        } else {
            List<Player> listP = new ArrayList<>();
            listP.add(p);
            m_zoneEnter.put(zone, listP);
        }
    }

    public void remPlayerToZoneEnter(String zone, Player p) {
        if (m_zoneEnter.containsKey(zone)) {
            m_zoneEnter.get(zone).remove(p);
        }
    }

    public void remPlayerToZoneEnter(Player p) {
        for (List<Player> list : m_zoneEnter.values()) {
            list.remove(p);
        }
    }

    public void setZoneEnterBool(String zone, boolean b) {
        if (m_zoneEnterBool.containsKey(zone)) {
            m_zoneEnterBool.replace(zone, b);
        } else {
            m_zoneEnterBool.put(zone, b);
        }
    }

    public boolean getZoneEnterBool(String zone) {
        if (m_zoneEnterBool.containsKey(zone))
            return m_zoneEnterBool.get(zone);
        return false;
    }
}

