package fr.byxis.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DbConnection {
	
	private final DbCredentials dbCredentials;
	private Connection connection;
	
	public DbConnection (DbCredentials dbCredentials)
	{
		this.dbCredentials = dbCredentials;
		this.connect();
	}
	
	@SuppressWarnings("static-access")
	private void connect()
	{
		try
		{
			getClass().forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection(this.dbCredentials.toURL(), this.dbCredentials.getUser(), this.dbCredentials.getPass());

			Logger.getLogger("Minecraft").info("La base de donnée est connectée !");
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

	}
	
	public void close() throws SQLException
	{
		if(this.connection != null)
		{
			if (!this.connection.isClosed())
			{
				this.connection.close();
			}
		}
	}
	
	public Connection getConnection() throws SQLException
	{
		if(this.connection != null)
		{
			if(!this.connection.isClosed())
			{
				return this.connection;
			}
		}
		connect();
		return this.connection;
	}

}
