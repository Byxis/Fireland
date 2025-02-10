package fr.byxis.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DbConnection {

    private final DbCredentials m_dbCredentials;
    private Connection m_connection;

    public DbConnection(DbCredentials _dbCredentials)
    {
        this.m_dbCredentials = _dbCredentials;
        this.connect();
    }

    public DbConnection(String _host, String _user, String _pass, String _name, int _port)
    {
        this.m_dbCredentials = new DbCredentials(_host, _user, _pass, _name, _port);
        this.connect();
    }
    
    @SuppressWarnings("static-access")
    private void connect()
    {
        try
        {
            getClass().forName("com.mysql.jdbc.Driver");
            this.m_connection = DriverManager.getConnection(this.m_dbCredentials.toURL(), this.m_dbCredentials.getUser(), this.m_dbCredentials.getPass());

            Logger.getLogger("Fireland").info("La base de donnée est connectée !");
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public void close() throws SQLException
    {
        if (this.m_connection != null && !this.m_connection.isClosed())
        {
            this.m_connection.close();
        }
    }
    
    public Connection getConnection() throws SQLException
    {
        if (this.m_connection != null && !this.m_connection.isClosed())
        {
            return this.m_connection;
        }
        connect();
        return this.m_connection;
    }

}
