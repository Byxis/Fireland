package fr.byxis.db;

import java.sql.SQLException;

public class DatabaseManager {
	
	private final DbConnection firelandConnection;
	
	public DatabaseManager() {
		this.firelandConnection = new DbConnection(new DbCredentials("srv1.mariadb10.minestrator.com", "minesr_cyTrnHYk", "1TFVAEoIWd2efe5x", "minesr_cyTrnHYk", 3306));
	}
	/*
	* this.firelandConnection = new DbConnection(new DbCredentials("sql11.freemysqlhosting.net", "sql11495901", "auvYnkMm73", "sql11495901", 3306));
	}*/
	
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
