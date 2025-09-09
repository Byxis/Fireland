package fr.byxis.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DbConnection {
    private final DbCredentials m_dbCredentials;
    private Connection m_connection;

    public DbConnection(DbCredentials dbCredentials) {
        this.m_dbCredentials = dbCredentials;
        this.connect();
    }

    public DbConnection(String type, String host, String user, String pass, String name, int port) {
        this.m_dbCredentials = new DbCredentials(type, host, user, pass, name, port);
        this.connect();
    }

    private void connect() {
        try {
            Class.forName(m_dbCredentials.getDriverClass());
            this.m_connection = DriverManager.getConnection(
                    m_dbCredentials.toURL(),
                    m_dbCredentials.getUser(),
                    m_dbCredentials.getPass()
            );
            Logger.getLogger("Fireland").info("La base de données est connectée !");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() throws SQLException {
        if (this.m_connection != null && !this.m_connection.isClosed()) {
            this.m_connection.close();
        }
    }

    public Connection getConnection() throws SQLException {
        if (this.m_connection != null && !this.m_connection.isClosed()) {
            return this.m_connection;
        }
        connect();
        return this.m_connection;
    }
}
