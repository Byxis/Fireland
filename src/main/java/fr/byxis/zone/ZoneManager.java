package fr.byxis.zone;

import fr.byxis.db.DbConnection;
import fr.byxis.zone.zoneclass.FactionZoneInformation;
import fr.byxis.fireland.Fireland;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ZoneManager {

    private final DataZone data;
    private final CaptureZone captureZone;
    private Fireland main;

    public ZoneManager(Fireland main)
    {
        this.main = main;
        this.data = new DataZone(main);
        this.captureZone = new CaptureZone(main, data);
    }

    public void RegisterEvents()
    {
        main.getServer().getPluginManager().registerEvents(new ZoneSaveEvent(data), main);
        main.getServer().getPluginManager().registerEvents(new WorldGuardEnterZoneEvent(main,data), main);
        main.getServer().getPluginManager().registerEvents(new ZoneEvent(main,data), main);
        captureZone.Loop();
    }

    public List<FactionZoneInformation> GetFactionData(String name)
    {
        List<FactionZoneInformation> list = new ArrayList<>();

        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

        try {
            final Connection connection = firelandConnection.getConnection();
            //Préparation de la commande
            final PreparedStatement isInDb = connection.prepareStatement("SELECT zone, capture_time, duration FROM capture_zone INNER JOIN faction_zone ON capture_zone.zone = faction_zone.zone WHERE capture_zone.faction_name = ?");
            isInDb.setString(1, name);
            ResultSet rs = isInDb.executeQuery();
            while(rs.next())
            {
                FactionZoneInformation factionZoneInformation = new FactionZoneInformation(name, rs.getString(1), rs.getTimestamp(2), rs.getInt(3));
                list.add(factionZoneInformation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
