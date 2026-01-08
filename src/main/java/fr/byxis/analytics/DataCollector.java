package fr.byxis.analytics;

import fr.byxis.db.DatabaseManager;
import fr.byxis.db.DbConnection;
import fr.byxis.db.DbCredentials;
import fr.byxis.fireland.Fireland;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class DataCollector
{
    private final ConcurrentLinkedQueue<LogEntry> m_buffer = new ConcurrentLinkedQueue<>();
    private DbConnection m_connection;

    public DataCollector(Fireland _fireland, DatabaseManager _databaseManager)
    {
        DbCredentials creds = new DbCredentials(
                _fireland.getConfig().getString("databases.fireland.type"),
                _fireland.getConfig().getString("databases.fireland.host"),
                _fireland.getConfig().getString("databases.fireland.user"),
                _fireland.getConfig().getString("databases.fireland.pass"),
                _fireland.getConfig().getString("databases.fireland.name"),
                _fireland.getConfig().getInt("databases.fireland.port")
        );
        m_connection = _databaseManager.getOrCreate(creds);
        initTable();
    }

    public void initTable()
    {
        try (Connection conn = m_connection.getConnection();
                Statement stmt = conn.createStatement())
        {
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS analytics_logs (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "uuid VARCHAR(36) NOT NULL," +
                            "world VARCHAR(32) NOT NULL," +
                            "x DOUBLE NOT NULL," +
                            "y DOUBLE NOT NULL," +
                            "z DOUBLE NOT NULL," +
                            "type VARCHAR(50) NOT NULL," +
                            "date DATE NOT NULL" +
                            ")"
            );
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void flushToDB() {
        if (m_buffer.isEmpty()) return;

        try (Connection conn = m_connection.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement("INSERT INTO analytics_logs (uuid, world, x, y, z, type, date) VALUES (?, ?, ?, ?, ?, ?, ?)");

            while (!m_buffer.isEmpty()) {
                LogEntry log = m_buffer.poll();
                ps.setString(1, log.getUuid().toString());
                ps.setString(2, log.getLocation().getWorld().getName());
                ps.setDouble(3, log.getLocation().getX());
                ps.setDouble(4, log.getLocation().getY());
                ps.setDouble(5, log.getLocation().getZ());
                ps.setString(6, log.getType().name());
                ps.setDate(7, log.getDate());
                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();
            Logger.getLogger("Fireland").info("Flushed analytics data to the database.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logEvent(LogEntry _entry)
    {
        m_buffer.add(_entry);
    }
}
