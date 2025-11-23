package fr.byxis.db;

import fr.byxis.fireland.Fireland;
import java.sql.SQLException;

public class DatabaseManager
{
    private final DbConnection m_firelandConnection;

    public DatabaseManager(Fireland _main)
    {
        String type = _main.getConfig().getString("database.type");
        String host = _main.getConfig().getString("database.host");
        String user = _main.getConfig().getString("database.user");
        String pass = _main.getConfig().getString("database.pass");
        String name = _main.getConfig().getString("database.name");
        int port = _main.getConfig().getInt("database.port");

        this.m_firelandConnection = new DbConnection(type, host, user, pass, name, port);
    }

    public DbConnection getFirelandConnection()
    {
        return m_firelandConnection;
    }

    public void close()
    {
        try
        {
            this.m_firelandConnection.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
