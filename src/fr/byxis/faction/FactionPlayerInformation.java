package fr.byxis.faction;

public class FactionPlayerInformation {

	private final String name;
	private final String factionName;
	private final int role;
	
	public FactionPlayerInformation(String name, String factionName, int role)
	{
		this.name = name;
		this.role = role;
		this.factionName = factionName;
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
}
