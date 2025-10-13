package fr.byxis.player.items.infection;

import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static fr.byxis.player.items.ItemDurability.getDurability;
import static fr.byxis.player.items.ItemDurability.remLoreDurability;

public class Mask implements Listener
{

    public static void addEffects(Player _player)
    {
        if (_player == null) return;

        ItemStack helmet = _player.getInventory().getHelmet();
        boolean isCreative = _player.getGameMode() == GameMode.CREATIVE;

        if (helmet != null)
        {
            Material mat = helmet.getType();

            if (mat == Material.RED_DYE && getDurability(helmet) > 0f)
            {
                _player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 800, 2, false, false));
                remLoreDurability(helmet, 0.1f);
            }
            else if (!isCreative && !_player.hasPermission("fireland.command.n"))
            {
                _player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }

            if (hasGazMask(_player))
            {
                _player.removePotionEffect(PotionEffectType.BLINDNESS);
                _player.removePotionEffect(PotionEffectType.NAUSEA);
            }
        }
        else
        {
            if (!isCreative)
            {
                _player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
        }
    }

    @EventHandler
    public void playerPutMask(PlayerInteractEvent _event)
    {
        Action action = _event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = _event.getPlayer();

        ItemStack hand = _event.getItem();
        if (hand == null) return;

        ItemStack previousHelmet = player.getInventory().getHelmet();
        Material handType = hand.getType();

        if (handType == Material.BROWN_DYE)
        {
            boolean helmetIsDifferent = previousHelmet == null || previousHelmet.getType() != Material.BROWN_DYE;
            if (helmetIsDifferent && player.getCooldown(handType) <= 0)
            {
                InGameUtilities.playWorldSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 1f, 1f);

                putItemOnHelmet(player, hand, previousHelmet);
            }
            return;
        }

        if (handType == Material.RED_DYE)
        {
            boolean helmetIsDifferent = previousHelmet == null || previousHelmet.getType() != Material.RED_DYE;
            if (helmetIsDifferent && player.getCooldown(handType) <= 0)
            {
                InGameUtilities.playWorldSound(player.getLocation(), "gun.nvgoggle.on", SoundCategory.PLAYERS, 1f, 1f);

                putItemOnHelmet(player, hand, previousHelmet);
            }
        }
    }

    private void putItemOnHelmet(Player _player, ItemStack _hand, ItemStack _previousHelmet)
    {
        _player.getInventory().setHelmet(_hand.clone());
        int amount = _hand.getAmount();
        if (amount <= 1)
        {
            _player.getInventory().setItemInMainHand(null);
        }
        else
        {
            _hand.setAmount(amount - 1);
            _player.getInventory().setItemInMainHand(_hand);
        }

        if (_previousHelmet != null)
        {
            _player.getInventory().addItem(_previousHelmet);
            _player.setCooldown(_previousHelmet.getType(), 20);
        }
    }

    @EventHandler
    public void playerDamage(EntityDamageEvent _damageEvent)
    {
        if (!(_damageEvent.getEntity() instanceof Player _player)) return;

        if (hasGazMask(_player))
        {
            if (_damageEvent.getCause() == EntityDamageEvent.DamageCause.POISON)
            {
                _damageEvent.setCancelled(true);
                _damageEvent.setDamage(0.0);
                _player.removePotionEffect(PotionEffectType.POISON);

                ItemStack helmet = _player.getInventory().getHelmet();
                if (helmet != null)
                {
                    remLoreDurability(helmet, 0.5f);
                }
            }
        }
    }

    public static boolean hasGazMask(Player _player)
    {
        if (_player == null) return false;

        ItemStack helmet = _player.getInventory().getHelmet();
        if (helmet == null) return false;

        Material mat = helmet.getType();

        if (mat == Material.BROWN_DYE && getDurability(helmet) > 0f)
        {
            return true;
        }
        else if (mat == Material.NETHERITE_HELMET)
        {
            return true;
        }

        /* TO BE IMPLEMENTED IN 1.12.10
        else if (
                mat == Material.COPPER_HELMET &&
                        _player.getInventory().getChestplate() != null && _player.getInventory().getChestplate().getType() == Material.COPPER_CHESTPLATE &&
                        _player.getInventory().getLeggings() != null && _player.getInventory().getLeggings().getType() == Material.COPPER_LEGGINGS &&
                        _player.getInventory().getBoots() != null && _player.getInventory().getBoots().getType() == Material.COPPER_BOOTS
        )
        {
            return true;
        }
        */
        return false;
    }

}