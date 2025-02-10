package fr.byxis.db;

public class DbCredentials {

    private final String m_host;
    private final String m_user;
    private final String m_pass;
    private final String m_dbName;
    private final int m_port;

    public DbCredentials(String _host, String _user, String _pass, String _dbName, int _port)
    {
        this.m_host = _host;
        this.m_user = _user;
        this.m_pass = _pass;
        this.m_dbName = _dbName;
        this.m_port = _port;
    }
    
    public String toURL()
    {

        return "jdbc:mysql://" + m_host +
                ":" + m_port +
                "/" +
                m_dbName;
    }
    
    public String getUser()
    {
        return m_user;
    }
    
    public String getPass()
    {
        return m_pass;
    }

}
