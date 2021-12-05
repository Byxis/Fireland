package fr.byxis.db;

public class DbCredentials {
	
	private final String host;
	
	private final String user;
	
	private final String pass;
	
	private final String dbName;
	
	private final int port;
	
	public DbCredentials (String host, String user, String pass,  String dbName, int port)
	{
		this.host = host;
		this.user = user;
		this.pass = pass;
		this.dbName = dbName;
		this.port = port;
	}
	
	public String toURL()
	{

		return "jdbc:mysql://" + host +
				":" + port +
				"/" +
				dbName;
	}
	
	public String getUser()
	{
		return user;
	}
	
	public String getPass()
	{
		return pass;
	}

}
