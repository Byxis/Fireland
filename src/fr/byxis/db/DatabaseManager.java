package fr.byxis.db;

import java.sql.SQLException;

public class DatabaseManager {
	
	private final DbConnection firelandConnection;
	
	public DatabaseManager()
	{
		this.firelandConnection = new DbConnection(new DbCredentials("178.32.113.35", "minesr_118326", "DRt1os4P", "minesr_118326", 3306));
	}
	
	public DbConnection getFirelandConnection()
	{
		return firelandConnection;
	}
	
	public void close()
	{
		try {
			this.firelandConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
