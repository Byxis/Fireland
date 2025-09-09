package fr.byxis.db;

public enum DatabaseType {
    MYSQL("com.mysql.jdbc.Driver", "jdbc:mysql://", "MySQL"),
    POSTGRESQL("org.postgresql.Driver", "jdbc:postgresql://", "PostgreSQL");

    private final String m_driverClass;
    private final String m_urlPrefix;
    private final String m_databaseType;

    DatabaseType(String _driverClass, String _urlPrefix, String _databaseType) {
        this.m_driverClass = _driverClass;
        this.m_urlPrefix = _urlPrefix;
        this.m_databaseType = _databaseType;
    }

    public String getDriverClass() {
        return m_driverClass;
    }

    public String getUrlPrefix() {
        return m_urlPrefix;
    }

    public String getDatabaseType() {
        return m_databaseType;
    }
}
