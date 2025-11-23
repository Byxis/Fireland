package fr.byxis.faction.essaim.enums;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Types of cooldowns available for commands or actions.
 * <ul>
 * <li>FIRST_TIME: Applies only the first time an action is performed.</li>
 * <li>DYNAMIC_DAY: Cooldown resets every N days.</li>
 * <li>DYNAMIC_WEEK: Cooldown resets every N weeks.</li>
 * <li>DYNAMIC_MONTH: Cooldown resets every N months.</li>
 * <li>NONE: No cooldown applied.</li>
 * </ul>
 * <p>
 * The enum also provides utility methods to parse cooldown types and durations
 * from strings.
 * </p>
 * <p>
 * Example usage:
 * 
 * <pre>
 * CooldownType type = CooldownType.fromString("3d"); // DYNAMIC_DAY
 * Duration duration = CooldownType.getDuration("3d"); // 3 days
 * int amount = CooldownType.getAmount("3d"); // 3
 * </pre>
 * </p>
 */
public enum CooldownType
{
    FIRST_TIME("first-time", null), DYNAMIC_DAY("Nd", null), DYNAMIC_WEEK("Nw", null), DYNAMIC_MONTH("Nm", null), NONE("none", null);

    private static final CooldownType DEFAULT = DYNAMIC_WEEK;
    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)([dwm])");

    CooldownType(String configPattern, Duration baseDuration)
    {
    }

    /**
     * Parses a string to determine the corresponding CooldownType. Supported
     * formats: - "first-time" for FIRST_TIME - "none" for NONE - "Nd", "Nw", "Nm"
     * for DYNAMIC_DAY, DYNAMIC_WEEK, DYNAMIC_MONTH respectively, where N is a
     * positive integer. If the input does not match any known format, DEFAULT
     * (DYNAMIC_WEEK) is returned.
     *
     * @param value
     *            the string representation of the cooldown type
     * @return the corresponding CooldownType
     */
    public static CooldownType fromString(String value)
    {
        if (value == null)
            return DEFAULT;
        if ("first-time".equals(value))
            return FIRST_TIME;
        if ("none".equals(value))
            return NONE;

        Matcher matcher = DURATION_PATTERN.matcher(value);
        if (matcher.matches())
        {
            String unit = matcher.group(2);
            switch (unit)
            {
                case "d" :
                    return DYNAMIC_DAY;
                case "w" :
                    return DYNAMIC_WEEK;
                case "m" :
                    return DYNAMIC_MONTH;
            }
        }

        return DEFAULT;
    }

    /**
     * Parses a duration string and returns the corresponding Duration object.
     * Supported formats: - "Nd" for N days - "Nw" for N weeks - "Nm" for N months
     * (approximated as 30 days) If the input is null or does not match any known
     * format, a default duration of 7 days is returned.
     *
     * @param value
     *            the string representation of the duration
     * @return the corresponding Duration object
     */
    public static Duration getDuration(String value)
    {
        // ! Default to 7 days if no value is provided
        if (value == null)
            return Duration.ofDays(7);

        Matcher matcher = DURATION_PATTERN.matcher(value);
        if (matcher.matches())
        {
            int amount = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit)
            {
                case "d" :
                    return Duration.ofDays(amount);
                case "w" :
                    return Duration.ofDays(amount * 7L);
                case "m" :
                    return Duration.ofDays(amount * 30L);
            }
        }

        // ! Hardcoded 7 days as a fallback
        return Duration.ofDays(7);
    }

    /**
     * Extracts the numeric amount from a duration string. Supported formats: - "Nd"
     * for N days - "Nw" for N weeks - "Nm" for N months If the input is null or
     * does not match any known format, a default amount of 1 is returned.
     *
     * @param value
     *            the string representation of the duration
     * @return the numeric amount extracted from the string
     */
    public static int getAmount(String value)
    {
        if (value == null)
            return 1;

        Matcher matcher = DURATION_PATTERN.matcher(value);
        if (matcher.matches())
        {
            return Integer.parseInt(matcher.group(1));
        }

        return 1;
    }
}
