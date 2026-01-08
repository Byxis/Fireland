package fr.byxis.db;

public class DbCredentials
{
    private final String m_host;
    private final String m_user;
    private final String m_pass;
    private final String m_dbName;
    private final int m_port;
    private final DatabaseType m_type;

    public DbCredentials(String type, String host, String user, String pass, String dbName, int port)
    {
        this.m_type = DatabaseType.valueOf(type.toUpperCase());
        this.m_host = host;
        this.m_user = user;
        this.m_pass = pass;
        this.m_dbName = dbName;
        this.m_port = port;
    }

    public String toURL()
    {
        return m_type.getUrlPrefix() + m_host + ":" + m_port + "/" + m_dbName;
    }

    public String getDriverClass()
    {
        return m_type.getDriverClass();
    }

    public String getUser()
    {
        return m_user;
    }

    public String getPass()
    {
        return m_pass;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DbCredentials that = (DbCredentials) o;

        return m_port == that.m_port &&
                m_type == that.m_type &&
                m_host.equals(that.m_host) &&
                m_user.equals(that.m_user) &&
                m_pass.equals(that.m_pass) &&
                m_dbName.equals(that.m_dbName);
    }

    @Override
    public int hashCode()
    {
        return java.util.Objects.hash(m_host, m_user, m_pass, m_dbName, m_port, m_type);
    }
}
