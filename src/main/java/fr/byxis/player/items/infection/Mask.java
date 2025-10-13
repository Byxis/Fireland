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

    public static void addEffects(Player p)
    {
        if (p.getInventory().getHelmet() != null)
        {
            if (p.getInventory().getHelmet().getType() == Material.RED_DYE && getDurability(p.getInventory().getHelmet()) >= 0)
            {
                p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 800, 2, false, false));
                remLoreDurability(p.getInventory().getHelmet(), 0.1f);
            }
            else if (p.getGameMode() != GameMode.CREATIVE && !p.hasPermission("fireland.command.n"))
            {
                p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
            if (hasGazMask(p))
            {
                p.removePotionEffect(PotionEffectType.BLINDNESS);
                p.removePotionEffect(PotionEffectType.NAUSEA);
            }
        }
        else if (p.getGameMode() != GameMode.CREATIVE)
        {
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }

    @EventHandler
    public void playerPutMask(PlayerInteractEvent e)
    {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        ItemStack item = e.getPlayer().getInventory().getHelmet();
        //Masque a gaz
        if (e.getPlayer().getItemInHand().getType() == Material.BROWN_DYE && (e.getPlayer().getInventory().getHelmet() == null || (
                e.getPlayer().getInventory().getHelmet().getType() != Material.BROWN_DYE &&
                e.getPlayer().getCooldown(e.getPlayer().getInventory().getItemInHand().getType()) <= 0)))
        {


            InGameUtilities.playWorldSound(e.getPlayer().getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 1, 1);
            e.getPlayer().getInventory().setHelmet(e.getPlayer().getItemInHand());
            e.getPlayer().getItemInHand().setAmount(0);
            if (item != null)
            {
                e.getPlayer().getInventory().addItem(item);
                e.getPlayer().setCooldown(item.getType(), 20);
            }
        }
        //Vision nocturne
        else if (e.getPlayer().getItemInHand().getType() == Material.RED_DYE && (e.getPlayer().getInventory().getHelmet() == null || (
                e.getPlayer().getInventory().getHelmet().getType() != Material.RED_DYE &&
                        e.getPlayer().getCooldown(e.getPlayer().getInventory().getItemInHand().getType()) <= 0)))
        {

            InGameUtilities.playWorldSound(e.getPlayer().getLocation(), "gun.nvgoggle.on", SoundCategory.PLAYERS, 1, 1);;
            e.getPlayer().getInventory().setHelmet(e.getPlayer().getItemInHand());
            e.getPlayer().getItemInHand().setAmount(0);
            if (item != null)
            {
                e.getPlayer().getInventory().addItem(item);
                e.getPlayer().setCooldown(item.getType(), 20);
            }
        }
    }

    @EventHandler
    public void playerDamage(EntityDamageEvent _damageEvent)
    {
        if (_damageEvent.getEntity() instanceof Player _player)
        {
            if (hasGazMask(_player))
            {
                if (_damageEvent.getCause() == EntityDamageEvent.DamageCause.POISON)
                {
                    _damageEvent.setCancelled(true);
                    _damageEvent.setDamage(0);
                    _player.removePotionEffect(PotionEffectType.POISON);
                    remLoreDurability(_player.getInventory().getHelmet(), 0.5f);
                }
            }
        }
    }

    public static boolean hasGazMask(Player _player)
    {
        if (_player.getInventory().getHelmet() == null) return false;
        Material mat = _player.getInventory().getHelmet().getType();
        if (mat == Material.BROWN_DYE && getDurability(_player.getInventory().getHelmet()) > 0)
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
