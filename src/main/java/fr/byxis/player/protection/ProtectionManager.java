package fr.byxis.player.protection;

import java.util.EnumSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ProtectionManager implements Listener
{

    private static final Set<EntityDamageEvent.DamageCause> FIRE_CAUSES = EnumSet.of(EntityDamageEvent.DamageCause.FIRE,
            EntityDamageEvent.DamageCause.FIRE_TICK, EntityDamageEvent.DamageCause.LAVA, EntityDamageEvent.DamageCause.MELTING,
            EntityDamageEvent.DamageCause.HOT_FLOOR);

    @EventHandler
    public static void playerDamaged(EntityDamageEvent _event)
    {
        if (FIRE_CAUSES.contains(_event.getCause()))
        {
            handleFireResistance(_event);
        }
        else if (_event.getCause() == EntityDamageEvent.DamageCause.WITHER)
        {
            handleWitherResistance(_event);
        }
    }

    static void handleFireResistance(EntityDamageEvent _event)
    {
        if (!(_event.getEntity() instanceof Player _player))
        {
            return;
        }

        float protection = 0;

        if (_player.getInventory().getHelmet() != null && _player.getInventory().getHelmet().getType() == Material.CHAINMAIL_HELMET)
        {
            protection += 0.1f;
        }
        if (_player.getInventory().getHelmet() != null && _player.getInventory().getHelmet().getType() == Material.CHAINMAIL_CHESTPLATE)
        {
            protection += 0.1f;
        }
        if (_player.getInventory().getHelmet() != null && _player.getInventory().getHelmet().getType() == Material.CHAINMAIL_LEGGINGS)
        {
            protection += 0.1f;
        }
        if (_player.getInventory().getHelmet() != null && _player.getInventory().getHelmet().getType() == Material.CHAINMAIL_BOOTS)
        {
            protection += 0.1f;
        }

        _event.setDamage(_event.getDamage() * (1 - protection));
    }

    public static void handleWitherResistance(EntityDamageEvent _event)
    {

        if (!(_event.getEntity() instanceof Player _player))
        {
            return;
        }

        PlayerInventory inventory = _player.getInventory();
        double totalReduction = 0;

        totalReduction += getReductionForItem(inventory.getHelmet());
        totalReduction += getReductionForItem(inventory.getChestplate());
        totalReduction += getReductionForItem(inventory.getLeggings());
        totalReduction += getReductionForItem(inventory.getBoots());

        double damage = _event.getDamage();
        double reducedDamage = damage * (1 - (totalReduction / 100));
        _event.setDamage(reducedDamage);
    }

    private static double getReductionForItem(ItemStack item)
    {
        if (item == null)
        {
            return 0;
        }

        Material material = item.getType();
        String materialName = material.name();

        if (materialName.contains("IRON"))
        {
            if (materialName.contains("HELMET"))
                return 2;
            if (materialName.contains("CHESTPLATE"))
                return 5;
            if (materialName.contains("LEGGINGS"))
                return 3;
            if (materialName.contains("BOOTS"))
                return 2;
        }
        else if (materialName.contains("DIAMOND"))
        {
            if (materialName.contains("HELMET"))
                return 3;
            if (materialName.contains("CHESTPLATE"))
                return 7;
            if (materialName.contains("LEGGINGS"))
                return 5;
            if (materialName.contains("BOOTS"))
                return 3;
        }
        else if (materialName.contains("COPPER"))
        {
            if (materialName.contains("HELMET"))
                return 4;
            if (materialName.contains("CHESTPLATE"))
                return 10;
            if (materialName.contains("LEGGINGS"))
                return 7;
            if (materialName.contains("BOOTS"))
                return 4;
        }
        else if (materialName.contains("NETHERITE"))
        {
            if (materialName.contains("HELMET"))
                return 5;
            if (materialName.contains("CHESTPLATE"))
                return 12;
            if (materialName.contains("LEGGINGS"))
                return 8;
            if (materialName.contains("BOOTS"))
                return 5;
        }

        return 0;
    }
}
