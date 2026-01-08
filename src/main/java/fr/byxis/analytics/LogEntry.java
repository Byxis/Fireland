package fr.byxis.analytics;

import org.bukkit.Location;

import java.sql.Date;
import java.util.UUID;

public class LogEntry
{
    private UUID m_uuid;
    private Location m_location;
    private LogType m_type;
    private Date m_date;


    public LogEntry(UUID _uuid, Location _location, LogType _type, Date _date)
    {
        this.m_uuid = _uuid;
        this.m_location = _location;
        this.m_type = _type;
        this.m_date = _date;
    }

    public UUID getUuid()
    {
        return m_uuid;
    }

    public void setUuid(UUID _uuid)
    {
        m_uuid = _uuid;
    }

    public Location getLocation()
    {
        return m_location;
    }

    public void setLocation(Location _location)
    {
        m_location = _location;
    }

    public LogType getType()
    {
        return m_type;
    }

    public void setType(LogType _type)
    {
        m_type = _type;
    }

    public Date getDate()
    {
        return m_date;
    }

    public void setDate(Date _date)
    {
        m_date = _date;
    }

    @Override
    public String toString()
    {
        return "LoggedData{" +
                "m_uuid=" + m_uuid +
                ", m_location=" + m_location +
                ", m_type=" + m_type +
                ", m_timestamp=" + m_date +
                '}';
    }
}
