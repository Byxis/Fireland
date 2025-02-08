package fr.byxis.event;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class QuadListener implements Listener
{

    public QuadListener()
    {
    }

    @EventHandler
    public void spawnQuad(PlayerInteractEvent e)
    {
        if (e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType() == Material.DEAD_TUBE_CORAL)
        {
            spawnQuad(e.getPlayer());
            e.getPlayer().getItemInHand().setAmount(e.getPlayer().getItemInHand().getAmount() - 1);
        }
    }

    private void spawnQuad(Player p)
    {
        boolean block = true;
        Location init = p.getLocation();
        
        if (!(new Location(init.getWorld(), init.getX(), init.getY(), init.getZ()).getBlock().isPassable()) && block)
        {
            block = false;
        }
        if (!(new Location(init.getWorld(), init.getX(), init.getY(), init.getZ() - 1).getBlock().isPassable()) && block)
        {
            block = false;
        }
        if (!(new Location(init.getWorld(), init.getX(), init.getY(), init.getZ() + 1).getBlock().isPassable()) && block)
        {
            block = false;
        }
        if (!(new Location(init.getWorld(), init.getX() + 1, init.getY(), init.getZ()).getBlock().isPassable()) && block)
        {
            block = false;
        }
        if (!(new Location(init.getWorld(), init.getX() - 1, init.getY(), init.getZ()).getBlock().isPassable()) && block)
        {
            block = false;
        }
        
        if (block)
        {
            Location loc = p.getLocation();
            loc.setYaw(0);
            loc.setPitch(0);
            
            Zombie quad = (Zombie) p.getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE);
            
            quad.setBaby(true);
            quad.setPassenger(p);
            quad.setCanPickupItems(false);
            quad.setCustomName("QUAD_ " + quad.getUniqueId());
            quad.getEquipment().setHelmet(new ItemStack(Material.DEAD_TUBE_CORAL));
            quad.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999999, 1, true, false, false));
            quad.setSilent(true);
            quad.setInvulnerable(true);
            quad.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
        }
        else
        {
            p.sendMessage("§cIl n'y a pas assez d'espace ici !");
        }
    }
}
