package fr.byxis.faction.essaim.conditions;

import fr.byxis.faction.essaim.services.EssaimConfigService;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Interface representing an essaim condition.
 * <p>
 * Conditions can be applied to the leader only or to all group members.
 */
public interface EssaimCondition
{
    /**
     * Checks if a player satisfies this condition.
     *
     * @param _player The player to check
     * @return true if the player meets the condition, false otherwise
     */
    boolean isSatisfied(Player _player);

    /**
     * Gets a human-readable description of this condition.
     *
     * @return A description for error messages
     */
    String getDescription();

    /**
     * Gets the type of this condition.
     *
     * @return The ConditionType
     */
    ConditionType getType();

    /**
     * Gets the scope of this condition.
     *
     * @return The ConditionScope
     */
    ConditionScope getScope();

    /**
     * Checks if a player satisfies all conditions required to enter an essaim.
     * <p>
     * Conditions are verified according to their scope:
     * <ul>
     *   <li>LEADER_ONLY: Only the player initiating must satisfy the condition</li>
     *   <li>ALL_MEMBERS: In group context, all members must satisfy the condition</li>
     * </ul>
     *
     * @param _configService The essaim configuration service
     * @param _player The player attempting to enter
     * @param _essaimName The essaim name
     * @param _conditionScope the condition that should be checked
     *
     * @return true if all conditions are satisfied, false otherwise
     */
    public static boolean checkEssaimConditions(EssaimConfigService _configService, Player _player, String _essaimName, ConditionScope _conditionScope)
    {
        List<EssaimCondition> conditions = _configService.getEssaimConditions(_essaimName);

        if (conditions.isEmpty())
        {
            return true;
        }

        for (EssaimCondition condition : conditions)
        {
            if (condition.getScope().equals(_conditionScope))
            {
                if (!condition.isSatisfied(_player))
                {
                    InGameUtilities.sendPlayerError(_player, "§c" + condition.getDescription());
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks if a player satisfies all conditions required to enter an essaim.
     * <p>
     * Conditions are verified according to their scope:
     * <ul>
     *   <li>LEADER_ONLY: Only the player initiating must satisfy the condition</li>
     *   <li>ALL_MEMBERS: In group context, all members must satisfy the condition</li>
     * </ul>
     *
     * @param _configService The essaim configuration service
     * @param _player The player attempting to enter
     * @param _essaimName The essaim name
     *
     * @return true if all conditions are satisfied, false otherwise
     */
    public static boolean checkEssaimConditions(EssaimConfigService _configService, Player _player, String _essaimName)
    {
        List<EssaimCondition> conditions = _configService.getEssaimConditions(_essaimName);

        if (conditions.isEmpty())
        {
            return true;
        }

        for (EssaimCondition condition : conditions)
        {
            if (!condition.isSatisfied(_player))
            {
                InGameUtilities.sendPlayerError(_player, "§c" + condition.getDescription());
                return false;
            }
        }

        return true;
    }

    /**
     * Gets a description of all unsatisfied conditions for a player attempting to enter an essaim.
     *
     * @param _configService The essaim configuration service
     * @param _player The player attempting to enter
     * @param _essaimName The essaim name
     * @param _conditionScope the condition that should be checked
     *
     * @return A string listing unsatisfied conditions, or null if all are satisfied
     */
    public static String getUnsatisfiedConditionDescription(EssaimConfigService _configService, Player _player, String _essaimName, ConditionScope _conditionScope)
    {
        List<EssaimCondition> conditions = _configService.getEssaimConditions(_essaimName);
        StringBuilder descriptionBuilder = new StringBuilder();

        if (conditions.isEmpty())
        {
            return null;
        }

        for (EssaimCondition condition : conditions)
        {
            if (condition.getScope().equals(_conditionScope))
            {
                if (!condition.isSatisfied(_player))
                {
                    descriptionBuilder.append("\n- " + condition.getDescription());
                }
            }
        }

        return descriptionBuilder.toString();
    }

    /**
     * Gets a description of all unsatisfied conditions for a player attempting to enter an essaim.
     *
     * @param _configService The essaim configuration service
     * @param _player The player attempting to enter
     * @param _essaimName The essaim name
     *
     * @return A string listing unsatisfied conditions, or null if all are satisfied
     */
    public static String getUnsatisfiedConditionDescription(EssaimConfigService _configService, Player _player, String _essaimName)
    {
        List<EssaimCondition> conditions = _configService.getEssaimConditions(_essaimName);
        StringBuilder descriptionBuilder = new StringBuilder();

        if (conditions.isEmpty())
        {
            return null;
        }

        for (EssaimCondition condition : conditions)
        {
            if (!condition.isSatisfied(_player))
            {
                descriptionBuilder.append("\n- ").append(condition.getDescription());
            }
        }

        return descriptionBuilder.toString();
    }
}

