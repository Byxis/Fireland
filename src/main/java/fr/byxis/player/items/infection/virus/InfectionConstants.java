package fr.byxis.player.items.infection.virus;

import org.bukkit.inventory.ItemStack;

import java.time.Duration;

/**
 * Constants related to infection mechanics.
 */
public class InfectionConstants
{
    // Infection chances
    public static final double ZOMBIE_INFECTION_CHANCE = 1;
    public static final double PLAYER_INFECTION_CHANCE = 1;

    // Infection invincibility duration for immunity vaccine
    public static final Duration IMMUNITY_DURATION = Duration.ofMinutes(10);

    // Custom model data values
    public static final int SYRINGE_CUSTOM_MODEL_DATA = 102;
    public static final int VACCINE_CUSTOM_MODEL_DATA = 114;
    public static final int BERSERKER_SERUM_CUSTOM_MODEL_DATA = 113;
    public static final int FUNGAL_VACCINE_CUSTOM_MODEL_DATA = 118;

    // Infection risk reductions

    public static double getReductionForHelmet(ItemStack _helmet)
    {
        if (_helmet == null) return 0.0D;
        return switch (_helmet.getType())
        {
            case GOLDEN_HELMET, LEATHER_HELMET -> 0.01D;
            case CHAINMAIL_HELMET -> 0.02D;
            case IRON_HELMET -> 0.03D;
            //case COPPER_HELMET -> 0.25D;
            case DIAMOND_HELMET -> 0.07D;
            case NETHERITE_HELMET -> 0.15D;
            default -> 0.0D;
        };
    }

    public static double getReductionForChestplate(ItemStack _chestplate)
    {
        if (_chestplate == null) return 0.0D;
        return switch (_chestplate.getType())
        {
            case CHAINMAIL_CHESTPLATE, LEATHER_CHESTPLATE -> 0.2D;
            case GOLDEN_CHESTPLATE -> 0.05D;
            case IRON_CHESTPLATE -> 0.20D;
            //case COPPER_CHESTPLATE -> 0.45D;
            case DIAMOND_CHESTPLATE -> 0.25D;
            case NETHERITE_CHESTPLATE -> 0.40D;
            default -> 0.0D;
        };
    }

    public static double getReductionForLeggings(ItemStack _leggings)
    {
        if (_leggings == null) return 0.0D;
        return switch (_leggings.getType())
        {
            case LEATHER_LEGGINGS, GOLDEN_LEGGINGS -> 0.01D;
            case CHAINMAIL_LEGGINGS -> 0.02D;
            case IRON_LEGGINGS -> 0.1D;
            //case COPPER_LEGGINGS -> 0.35D;
            case DIAMOND_LEGGINGS -> 0.15D;
            case NETHERITE_LEGGINGS -> 0.20D;
            default -> 0.0D;
        };
    }

    public static double getReductionForBoots(ItemStack _boots)
    {
        if (_boots == null) return 0.0D;
        return switch (_boots.getType())
        {
            case LEATHER_BOOTS, GOLDEN_BOOTS -> 0.01D;
            case CHAINMAIL_BOOTS -> 0.01D;
            case IRON_BOOTS -> 0.2D;
            //case COPPER_BOOTS -> 0.05D;
            case DIAMOND_BOOTS -> 0.03D;
            case NETHERITE_BOOTS -> 0.05D;
            default -> 0.0D;
        };
    }
}
