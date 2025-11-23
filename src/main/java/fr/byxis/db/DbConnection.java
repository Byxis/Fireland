package fr.byxis.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbConnection
{
    private static final Logger LOGGER = Logger.getLogger("Fireland");
    private final DbCredentials m_dbCredentials;
    private HikariDataSource m_dataSource;

    public DbConnection(DbCredentials dbCredentials)
    {
        this.m_dbCredentials = dbCredentials;
        this.initializePool();
    }

    public DbConnection(String type, String host, String user, String pass, String name, int port)
    {
        this.m_dbCredentials = new DbCredentials(type, host, user, pass, name, port);
        this.initializePool();
    }

    private void initializePool()
    {
        try
        {
            Class.forName(m_dbCredentials.getDriverClass());

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(m_dbCredentials.toURL());
            config.setUsername(m_dbCredentials.getUser());
            config.setPassword(m_dbCredentials.getPass());

            config.setMaximumPoolSize(30);
            config.setMinimumIdle(10);
            config.setConnectionTimeout(60 * 1000);
            config.setIdleTimeout(10 * 60 * 1000);
            config.setMaxLifetime(20 * 60 * 1000);
            config.setAutoCommit(true);
            config.setPoolName("FirelandPool");
            config.setLeakDetectionThreshold(60 * 1000);

            config.setThreadFactory(runnable ->
            {
                Thread thread = new Thread(runnable, "HikariCP-Fireland-Pool");
                thread.setDaemon(true);
                return thread;
            });

            this.m_dataSource = new HikariDataSource(config);
            LOGGER.info("The database connection pool has been connected !");
        }
        catch (ClassNotFoundException e)
        {
            LOGGER.log(Level.SEVERE, "Error while connecting to the database: ", e);
        }
    }

    public void close() throws SQLException
    {
        if (this.m_dataSource != null && !this.m_dataSource.isClosed())
        {
            this.m_dataSource.close();
            LOGGER.info("The database connection pool has been closed.");
        }
    }

    public Connection getConnection() throws SQLException
    {
        if (this.m_dataSource == null)
        {
            throw new SQLException("The pool is not initialized.");
        }
        return this.m_dataSource.getConnection();
    }
}
