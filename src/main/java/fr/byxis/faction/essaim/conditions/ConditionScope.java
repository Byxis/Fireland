package fr.byxis.faction.essaim.conditions;

/**
 * Enum representing the scope of a condition application.
 * <p>
 * Determines who must satisfy the condition in a group.
 */
public enum ConditionScope
{
    /**
     * Condition applies only to the group leader.
     */
    LEADER_ONLY("leader"),

    /**
     * Condition applies to all group members.
     */
    ALL_MEMBERS("all");

    private final String m_configKey;

    /**
     * Constructs a ConditionScope.
     *
     * @param _configKey The key used in configuration files
     */
    ConditionScope(String _configKey)
    {
        this.m_configKey = _configKey;
    }

    /**
     * Gets the configuration key for this scope.
     *
     * @return The config key string
     */
    public String getConfigKey()
    {
        return m_configKey;
    }

    /**
     * Gets a ConditionScope from a configuration key.
     *
     * @param _configKey The key to look up
     * @return The corresponding ConditionScope, or LEADER_ONLY if not found
     */
    public static ConditionScope fromConfigKey(String _configKey)
    {
        if (_configKey == null)
        {
            return LEADER_ONLY;
        }
        for (ConditionScope scope : ConditionScope.values())
        {
            if (scope.m_configKey.equalsIgnoreCase(_configKey))
            {
                return scope;
            }
        }
        return LEADER_ONLY;
    }
}

