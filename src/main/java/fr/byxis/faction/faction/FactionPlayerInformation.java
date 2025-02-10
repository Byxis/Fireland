package fr.byxis.faction.faction;

import java.sql.Timestamp;
import java.util.UUID;

public class FactionPlayerInformation {

    private final String name;
    private final String factionName;
    private final int role;

    private final UUID uuid;
    private final Timestamp joinDate;
    
    public FactionPlayerInformation(String _name, String _factionName, int _role, UUID _uuid, Timestamp _joinDate)
    {
        this.name = _name;
        this.uuid = _uuid;
        this.role = _role;
        this.factionName = _factionName;
        this.joinDate = _joinDate;
    }

    public String getName()
    {
        return name;
    }
    public String getFactionName()
    {
        return factionName;
    }
    public int getRole()
    {
        return role;
    }
    public UUID getUuid()
    {
        return this.uuid;
    }

    public Timestamp getJoinDate() {
        return joinDate;
    }
}
