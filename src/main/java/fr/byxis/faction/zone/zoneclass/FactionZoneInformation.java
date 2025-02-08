package fr.byxis.faction.zone.zoneclass;

import java.sql.Timestamp;

public class FactionZoneInformation {

    private String factionName;
    private String zoneName;
    private Timestamp claimedAt;
    private long totalDuration;

    public FactionZoneInformation(String _factionName, String _zoneName, Timestamp _claimedAt, long _totalDuration)
    {
        this.factionName = _factionName;
        this.zoneName = _zoneName;
        this.claimedAt = _claimedAt;
        this.totalDuration = _totalDuration;
    }

    public String getFactionName() {
        return factionName;
    }

    public void setFactionName(String _factionName) {
        this.factionName = _factionName;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String _zoneName) {
        this.zoneName = _zoneName;
    }

    public Timestamp getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(Timestamp _claimedAt) {
        this.claimedAt = _claimedAt;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long _totalDuration) {
        this.totalDuration = _totalDuration;
    }

    public String getFormattedName()
    {
        return (zoneName.substring(0, 1).toUpperCase() + zoneName.substring(1)).replaceAll("-", " ");
    }
}
