package fr.byxis.faction.zone.zoneclass;

import java.sql.Timestamp;

public class FactionZoneInformation {

    private String factionName;
    private String zoneName;
    private Timestamp claimedAt;
    private long totalDuration;

    public FactionZoneInformation(String factionName, String zoneName, Timestamp claimedAt, long totalDuration)
    {
        this.factionName = factionName;
        this.zoneName = zoneName;
        this.claimedAt = claimedAt;
        this.totalDuration = totalDuration;
    }

    public String getFactionName() {
        return factionName;
    }

    public void setFactionName(String factionName) {
        this.factionName = factionName;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public Timestamp getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(Timestamp claimedAt) {
        this.claimedAt = claimedAt;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getFormattedName()
    {
        return (zoneName.substring(0, 1).toUpperCase() + zoneName.substring(1)).replaceAll("-", " ");
    }
}
