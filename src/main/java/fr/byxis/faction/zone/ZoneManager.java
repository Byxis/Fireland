package fr.byxis.faction.zone;

import fr.byxis.db.DbConnection;
import fr.byxis.faction.zone.zoneclass.FactionZoneInformation;
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
    private final Fireland main;

    public ZoneManager(Fireland _main)
    {
        this.main = _main;
        this.data = new DataZone(_main);
        this.captureZone = new CaptureZone(_main, data);
    }

    public void registerEvents()
    {
        main.getServer().getPluginManager().registerEvents(new ZoneSaveEvent(data), main);
        main.getServer().getPluginManager().registerEvents(new WorldGuardEnterZoneEvent(main, data), main);
        main.getServer().getPluginManager().registerEvents(new ZoneEvent(main, data), main);
        captureZone.loop();
    }

    public List<FactionZoneInformation> getFactionData(String name)
    {
        List<FactionZoneInformation> list = new ArrayList<>();

        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

        try {
            /*
            *
            * Partie zones claims
            *
            */

            final Connection connection = firelandConnection.getConnection();
            //Préparation de la commande
            PreparedStatement isInDb = connection.prepareStatement("SELECT capture_zone.zone, capture_zone.capture_time FROM capture_zone " +
                    "WHERE capture_zone.faction_name = ?;");
            isInDb.setString(1, name);
            ResultSet rs = isInDb.executeQuery();
            int i = 0;
            while (rs.next())
            {
                i++;
                FactionZoneInformation factionZoneInformation = new FactionZoneInformation(name, rs.getString(1), rs.getTimestamp(2), System.currentTimeMillis() - rs.getTimestamp(2).getTime());
                list.add(factionZoneInformation);
            }
            /*
             *
             * Partie zones NON claims
             *
             */

            isInDb = connection.prepareStatement("SELECT faction_zone.zone, faction_zone.duration FROM faction_zone " +
                    "WHERE faction_zone.faction_name = ?;");

            isInDb.setString(1, name);
            rs = isInDb.executeQuery();
            while (rs.next())
            {
                FactionZoneInformation factionZoneInformation = new FactionZoneInformation(name, rs.getString(1), null, rs.getLong(2));
                boolean inside = false;
                int j = 0;
                for (FactionZoneInformation zone : list)
                {
                    if (j > i)
                    {
                        break;
                    }
                    if (zone.getZoneName().equals(factionZoneInformation.getZoneName()))
                    {
                        inside = true;
                        zone.setTotalDuration(zone.getTotalDuration() + factionZoneInformation.getTotalDuration());
                    }
                }
                if (!inside)
                {
                    list.add(factionZoneInformation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public DataZone getData()
    {
        return data;
    }
}
