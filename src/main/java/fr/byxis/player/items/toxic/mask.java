package fr.byxis.player.items.toxic;

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

import static fr.byxis.player.items.itemDurability.getDurability;
import static fr.byxis.player.items.itemDurability.remLoreDurability;

public class mask implements Listener {

    @EventHandler
    public void playerPutMask(PlayerInteractEvent e)
    {
        if(e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        ItemStack item = e.getPlayer().getInventory().getHelmet();
        //Masque a gaz
        if(e.getPlayer().getItemInHand().getType() == Material.BROWN_DYE && ( e.getPlayer().getInventory().getHelmet() == null || (
                e.getPlayer().getInventory().getHelmet().getType() != Material.BROWN_DYE &&
                e.getPlayer().getCooldown(e.getPlayer().getInventory().getItemInHand().getType()) <= 0)))
        {


            InGameUtilities.playWorldSound(e.getPlayer().getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS,1, 1);
            e.getPlayer().getInventory().setHelmet(e.getPlayer().getItemInHand());
            e.getPlayer().getItemInHand().setAmount(0);
            if(item != null)
            {
                e.getPlayer().getInventory().addItem(item);
                e.getPlayer().setCooldown(item.getType(), 20);
            }
        }
        //Vision nocturne
        else if(e.getPlayer().getItemInHand().getType() == Material.RED_DYE && ( e.getPlayer().getInventory().getHelmet() == null || (
                e.getPlayer().getInventory().getHelmet().getType() != Material.RED_DYE &&
                        e.getPlayer().getCooldown(e.getPlayer().getInventory().getItemInHand().getType()) <= 0)))
        {

            InGameUtilities.playWorldSound(e.getPlayer().getLocation(), "gun.nvgoggle.on", SoundCategory.PLAYERS,1, 1);;
            e.getPlayer().getInventory().setHelmet(e.getPlayer().getItemInHand());
            e.getPlayer().getItemInHand().setAmount(0);
            if(item != null)
            {
                e.getPlayer().getInventory().addItem(item);
                e.getPlayer().setCooldown(item.getType(), 20);
            }
        }
    }

    @EventHandler
    public void playerDamage(EntityDamageEvent e)
    {
        if(e.getEntity() instanceof Player p)
        {
            if(p.getInventory().getHelmet() != null  &&p.getInventory().getHelmet().getType() == Material.BROWN_DYE && getDurability(p.getInventory().getHelmet()) >0)
            {
                if(e.getCause() == EntityDamageEvent.DamageCause.POISON)
                {
                    e.setCancelled(true);
                    e.setDamage(0);
                    p.removePotionEffect(PotionEffectType.POISON);
                    remLoreDurability(p.getInventory().getHelmet(), 0.5f);
                }
            }
        }
    }


    public static void addEffects(Player p)
    {
        if(p.getInventory().getHelmet() != null) {
            if(p.getInventory().getHelmet().getType() == Material.RED_DYE && getDurability(p.getInventory().getHelmet()) >= 0) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 800, 2, false, false), true);
                remLoreDurability(p.getInventory().getHelmet(), 0.1f);
            }
            else if(p.getGameMode() != GameMode.CREATIVE && !p.hasPermission("fireland.command.n"))
            {
                p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
            if(p.getInventory().getHelmet().getType() == Material.BROWN_DYE && getDurability(p.getInventory().getHelmet()) >0)
            {
                p.removePotionEffect(PotionEffectType.BLINDNESS);
                p.removePotionEffect(PotionEffectType.CONFUSION);
            }
        }
        else if(p.getGameMode() != GameMode.CREATIVE)
        {
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }

}
