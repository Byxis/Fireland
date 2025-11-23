package fr.byxis.player.items;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomStackSizeManager
{
    // Map pour stocker les tailles de stack personnalisées
    private final Map<Material, Integer> customStackSizes;

    public CustomStackSizeManager()
    {
        this.customStackSizes = new HashMap<>();
    }

    /**
     * Change la taille maximale du stack pour un matériau donné.
     * 
     * @param material
     *            Le matériau à modifier.
     * @param newMaxStackSize
     *            La nouvelle taille maximale du stack.
     */
    public void changeMaterialStack(Material material, int newMaxStackSize)
    {
        if (material == null || newMaxStackSize <= 0)
        {
            throw new IllegalArgumentException("Material et newMaxStackSize doivent être valides.");
        }
        customStackSizes.put(material, newMaxStackSize);
    }

    /**
     * Crée un ItemStack avec la taille de stack personnalisée.
     * 
     * @param material
     *            Le matériau de l'ItemStack.
     * @param amount
     *            La quantité initiale (sera ajustée si nécessaire).
     * @return Un ItemStack avec la taille de stack personnalisée.
     */
    public ItemStack createCustomStack(Material material, int amount)
    {
        int maxStackSize = customStackSizes.getOrDefault(material, material.getMaxStackSize());
        int adjustedAmount = Math.min(amount, maxStackSize);

        ItemStack itemStack = new ItemStack(material, adjustedAmount);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null)
        {
            // Optionnel : Ajouter un lore pour indiquer la taille de stack personnalisée
            meta.setLore(java.util.Arrays.asList("§7Taille de stack personnalisée: " + maxStackSize));
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    /**
     * Récupère la taille de stack personnalisée pour un matériau.
     * 
     * @param material
     *            Le matériau.
     * @return La taille de stack personnalisée, ou la taille par défaut si non
     *         définie.
     */
    public int getCustomStackSize(Material material)
    {
        return customStackSizes.getOrDefault(material, material.getMaxStackSize());
    }
}
