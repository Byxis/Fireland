package fr.byxis.faction;

import java.sql.Timestamp;
import java.util.UUID;

public class FactionPlayerInformation {

	private final String name;
	private final String factionName;
	private final int role;

	private final UUID uuid;
	private final Timestamp joinDate;
	
	public FactionPlayerInformation(String name, String factionName, int role, UUID uuid, Timestamp joinDate)
	{
		this.name = name;
		this.uuid = uuid;
		this.role = role;
		this.factionName = factionName;
		this.joinDate = joinDate;
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
	{return this.uuid;
	}

	public Timestamp getJoinDate() {
		return joinDate;
	}
}
