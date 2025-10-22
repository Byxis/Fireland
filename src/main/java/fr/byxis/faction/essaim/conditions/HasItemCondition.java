package fr.byxis.faction.essaim.conditions;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Condition requiring a player to have a specific item in their inventory.
 */
public class HasItemCondition implements EssaimCondition
{
    private final Material m_requiredMaterial;
    private final ConditionScope m_scope;
    private final String m_displayName;

    /**
     * Creates a new item condition with an optional display name.
     *
     * @param _requiredMaterial The material the player must have
     * @param _scope The scope of this condition
     * @param _displayName Optional display name for custom items (null to auto-format)
     */
    public HasItemCondition(Material _requiredMaterial, ConditionScope _scope, String _displayName)
    {
        this.m_requiredMaterial = _requiredMaterial;
        this.m_scope = _scope;
        this.m_displayName = _displayName;
    }

    @Override
    public boolean isSatisfied(Player _player)
    {
        if (_player == null || m_requiredMaterial == null)
        {
            return false;
        }
        return _player.getInventory().contains(m_requiredMaterial);
    }

    @Override
    public String getDescription()
    {
        String itemName = m_displayName != null ? m_displayName : formatMaterialName(m_requiredMaterial);
        return "Vous devez avoir sur vous: " + itemName;
    }

    @Override
    public ConditionType getType()
    {
        return ConditionType.HAS_ITEM;
    }

    @Override
    public ConditionScope getScope()
    {
        return m_scope;
    }

    /**
     * Gets the required material.
     *
     * @return The material this condition requires
     */
    public Material getRequiredMaterial()
    {
        return m_requiredMaterial;
    }

    /**
     * Gets the display name for this item.
     *
     * @return The display name, or null if using auto-formatted name
     */
    public String getDisplayName()
    {
        return m_displayName;
    }

    /**
     * Formats a material name for display.
     * <p>
     * Converts SCREAMING_SNAKE_CASE to Title Case.
     *
     * @param _material The material to format
     * @return A human-readable material name
     */
    private static String formatMaterialName(Material _material)
    {
        if (_material == null)
        {
            return "Unknown";
        }
        String[] parts = _material.name().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts)
        {
            if (sb.length() > 0)
            {
                sb.append(" ");
            }
            sb.append(part.charAt(0)).append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }
}

