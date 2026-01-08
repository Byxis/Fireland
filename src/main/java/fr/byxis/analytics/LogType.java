package fr.byxis.analytics;

import java.util.HashMap;
import java.util.Map;

public enum LogType
{
    OPEN_DOMESTIQUE("Domestique"),
    OPEN_POLICE("Police"),
    OPEN_MILITAIRE("Militaire"),
    OPEN_MEDICAL("Medical"),
    OPEN_WEAPON("weaponcabinet"),
    OPEN_ARTIFICIER("Coffre-Artificier"),
    OPEN_GARBAGE("garbage"),
    OPEN_TOOLBOX("caisseoutils"),
    OPEN_AMMO("caissemunition"),
    OPEN_CORPSE("corpse"),

    UNKNOWN_LOOT("Unknown"),

    PLAYER_KILL(null),
    PLAYER_DEATH(null);

    private static final Map<String, LogType> BY_NAME = new HashMap<>();

    static
    {
        for (LogType type : values())
        {
            if (type.m_phatLootName != null)
            {
                BY_NAME.put(type.m_phatLootName, type);
            }
        }
    }

    private final String m_phatLootName;

    LogType(String _phatLootName)
    {
        this.m_phatLootName = _phatLootName;
    }

    public static LogType fromPhatLootName(String _name)
    {
        return BY_NAME.getOrDefault(_name, UNKNOWN_LOOT);
    }
}