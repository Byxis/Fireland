package fr.byxis.faction.essaim.conditions;

import org.bukkit.entity.Player;

/**
 * Condition requiring a player to have a minimum level.
 */
public class LevelCondition implements EssaimCondition
{
    private final int m_minLevel;
    private final ConditionScope m_scope;

    /**
     * Creates a new level condition.
     *
     * @param _minLevel
     *            The minimum level required
     * @param _scope
     *            The scope of this condition
     */
    public LevelCondition(int _minLevel, ConditionScope _scope)
    {
        this.m_minLevel = Math.max(0, _minLevel);
        this.m_scope = _scope;
    }

    @Override
    public boolean isSatisfied(Player _player)
    {
        if (_player == null)
        {
            return false;
        }
        return _player.getLevel() >= m_minLevel;
    }

    @Override
    public String getDescription()
    {
        return "Level minimum requis: " + m_minLevel;
    }

    @Override
    public ConditionType getType()
    {
        return ConditionType.LEVEL;
    }

    @Override
    public ConditionScope getScope()
    {
        return m_scope;
    }

    /**
     * Gets the minimum level required.
     *
     * @return The minimum level
     */
    public int getMinLevel()
    {
        return m_minLevel;
    }
}
