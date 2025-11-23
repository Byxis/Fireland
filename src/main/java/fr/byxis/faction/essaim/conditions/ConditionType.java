package fr.byxis.faction.essaim.conditions;

/**
 * Enum representing the different types of conditions that can be applied to
 * essaims.
 */
public enum ConditionType
{
    /**
     * Condition requiring a minimum player level.
     */
    LEVEL("level"),

    /**
     * Condition requiring a specific item in inventory.
     */
    HAS_ITEM("has_item");

    private final String m_configKey;

    /**
     * Constructs a ConditionType.
     *
     * @param _configKey
     *            The key used in configuration files
     */
    ConditionType(String _configKey)
    {
        this.m_configKey = _configKey;
    }

    /**
     * Gets the configuration key for this condition type.
     *
     * @return The config key string
     */
    public String getConfigKey()
    {
        return m_configKey;
    }

    /**
     * Gets a ConditionType from a configuration key.
     *
     * @param _configKey
     *            The key to look up
     * @return The corresponding ConditionType, or null if not found
     */
    public static ConditionType fromConfigKey(String _configKey)
    {
        if (_configKey == null)
        {
            return null;
        }
        for (ConditionType type : ConditionType.values())
        {
            if (type.m_configKey.equalsIgnoreCase(_configKey))
            {
                return type;
            }
        }
        return null;
    }
}
