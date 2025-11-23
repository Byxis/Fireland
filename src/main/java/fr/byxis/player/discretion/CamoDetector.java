package fr.byxis.player.discretion;

import fr.byxis.fireland.Fireland;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CamoDetector
{
    private final Fireland m_fireland;
    private final int modelDataForCamo;

    private static final int HELMET_REDUCTION = 10;
    private static final int CHESTPLATE_REDUCTION = 20;
    private static final int LEGGINGS_REDUCTION = 15;
    private static final int BOOTS_REDUCTION = 5;

    public CamoDetector(Fireland _fireland)
    {
        this.m_fireland = _fireland;
        this.modelDataForCamo = _fireland.getConfig().getInt("discretion.camo.model-data", 0);
    }

    public int camoReduction(Player p)
    {
        if (!hasFullGoldenArmor(p))
        {
            return 0;
        }
        int reduction = 0;
        reduction += modelDataForCamo == getModelData(p.getInventory().getHelmet()) ? HELMET_REDUCTION : 0;
        reduction += modelDataForCamo == getModelData(p.getInventory().getChestplate()) ? CHESTPLATE_REDUCTION : 0;
        reduction += modelDataForCamo == getModelData(p.getInventory().getLeggings()) ? LEGGINGS_REDUCTION : 0;
        reduction += modelDataForCamo == getModelData(p.getInventory().getBoots()) ? BOOTS_REDUCTION : 0;

        return reduction;
    }

    private boolean hasFullGoldenArmor(Player p)
    {
        return hasItem(p.getInventory().getHelmet(), Material.GOLDEN_HELMET)
                && hasItem(p.getInventory().getChestplate(), Material.GOLDEN_CHESTPLATE)
                && hasItem(p.getInventory().getLeggings(), Material.GOLDEN_LEGGINGS)
                && hasItem(p.getInventory().getBoots(), Material.GOLDEN_BOOTS);
    }

    private boolean hasItem(ItemStack item, Material material)
    {
        return item != null && item.getType() == material;
    }

    private int getModelData(ItemStack item)
    {
        if (item != null && item.getItemMeta() != null && item.getItemMeta().hasCustomModelData())
        {
            return item.getItemMeta().getCustomModelData();
        }
        return 0;
    }
}
