package fr.byxis.player.items.parachute;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class Parachute implements Listener
{

    private final HashMap<UUID, Boolean> parachuting;
    
    private final Fireland main;

    public Parachute(Fireland _main)
    {
        this.main = _main;
        parachuting = new HashMap<UUID, Boolean>();
        loop();
    }
    
    @EventHandler
    public void rightClickEvent(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getItem() != null && e.getPlayer().getLocation().add(0, -1, 0).getBlock().getType() == Material.AIR && e.getPlayer().getLocation().add(0, -2, 0).getBlock().getType() == Material.AIR) {
                if (e.getItem().getType() == Material.POPPY)
                {
                    if (e.getPlayer().getWorld().getName().equalsIgnoreCase("essaim"))
                    {
                        InGameUtilities.sendPlayerError(e.getPlayer(), "Le parachute a été désactivé dans les essaims !");
                        return;
                    }
                    e.getItem().setType(Material.DANDELION);
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 6, 0, false, false), true);
                    InGameUtilities.playWorldSound(e.getPlayer().getLocation(), "entity.bat.takeoff", SoundCategory.PLAYERS, 0.1f, 1);
                    setParachuting(e.getPlayer(), true);
                }
                else if (e.getItem().getType() == Material.DANDELION)
                {
                    if (e.getPlayer().getWorld().getName().equalsIgnoreCase("essaim"))
                    {
                        InGameUtilities.sendPlayerError(e.getPlayer(), "Le parachute a été désactivé dans les essaims !");
                        return;
                    }
                    setParachuting(e.getPlayer(), true);
                }
            }
        }
        
    }

    private void setParachuting(Player p, Boolean b)
    {
        if (parachuting.containsKey(p.getUniqueId()))
        {
            parachuting.replace(p.getUniqueId(), b);
        }
        else
        {
            parachuting.put(p.getUniqueId(), b);
        }
    }
    private void loop()
    {
        new BukkitRunnable()
        {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    if (parachuting.containsKey(p.getUniqueId()))
                    {
                        if (parachuting.get(p.getUniqueId()))
                        {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 6, 0, false, false), true);
                            setParachuting(p, false);
                        }
                        else
                        {
                            for (ItemStack item : p.getInventory().getContents())
                            {
                                if (item != null)
                                {
                                    if (item.getType() == Material.DANDELION)
                                    {
                                        item.setType(Material.POPPY);
                                    }
                                }
                            }
                            parachuting.remove(p.getUniqueId());
                        }
                    }
                }
            }
        }.runTaskTimer(main, 0, 5);
    }
}
