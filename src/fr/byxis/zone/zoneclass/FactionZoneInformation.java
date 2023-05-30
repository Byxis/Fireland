package fr.byxis.zone.zoneclass;

import java.sql.Timestamp;

public class FactionZoneInformation {

    public String factionName;
    public String zoneName;
    public Timestamp claimedAt;
    public int totalDuration;

    public FactionZoneInformation(String factionName, String zoneName, Timestamp claimedAt, int totalDuration)
    {
        this.factionName = factionName;
        this.zoneName = zoneName;
        this.claimedAt = claimedAt;
        this.totalDuration = totalDuration;
    }
}
