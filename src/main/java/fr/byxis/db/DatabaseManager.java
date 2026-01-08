package fr.byxis.db;

import fr.byxis.fireland.Fireland;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager
{
    private final Map<DbCredentials, DbConnection> m_activeConnections = new HashMap<>();
    private final DbCredentials m_firelandCredentials;

    public DatabaseManager(Fireland _main)
    {
        m_firelandCredentials = new DbCredentials(
                _main.getConfig().getString("databases.fireland.type"),
                _main.getConfig().getString("databases.fireland.host"),
                _main.getConfig().getString("databases.fireland.user"),
                _main.getConfig().getString("databases.fireland.pass"),
                _main.getConfig().getString("databases.fireland.name"),
                _main.getConfig().getInt("databases.fireland.port")
        );
        getOrCreate(m_firelandCredentials);
        initCoreTables();
    }

    public synchronized DbConnection getOrCreate(DbCredentials _creds)
    {

        if (m_activeConnections.containsKey(_creds)) {
            return m_activeConnections.get(_creds);
        }

        DbConnection newConnection = new DbConnection(_creds);
        m_activeConnections.put(_creds, newConnection);

        return newConnection;
    }

    public DbConnection getFirelandConnection()
    {
        return m_activeConnections.get(m_firelandCredentials);
    }

    public void close()
    {
        for (DbConnection conn : m_activeConnections.values()) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        m_activeConnections.clear();
    }

    private void initCoreTables() {
        try (Connection conn = getFirelandConnection().getConnection();
                Statement stmt = conn.createStatement())
        {
            // Player table
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS players (" +
                            "uuid VARCHAR(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_uca1400_ai_ci NOT NULL PRIMARY KEY," +
                            "name VARCHAR(16) NOT NULL," +
                            "first_joined DATETIME NOT NULL" +
                            ")"
            );

            // Items table
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS items (" +
                            "item_name VARCHAR(64) NOT NULL PRIMARY KEY," +
                            "recipe_name VARCHAR(128) DEFAULT NULL," +
                            "item VARCHAR(32) NOT NULL," +
                            "durability INT(3) NOT NULL," +
                            "custom_model_data INT(20) NOT NULL DEFAULT 0," +
                            "command VARCHAR(512) NOT NULL" +
                            ")"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}