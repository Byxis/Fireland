package fr.byxis.fireland.utilities;

import fr.byxis.fireland.Fireland;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.naming.ldap.HasControls;
import java.util.HashMap;

public class InGameUtilities implements Listener {


    private static Fireland main;

    public static HashMap<Player, Boolean> playerMoving = new HashMap<Player, Boolean>();

    public InGameUtilities(Fireland fireland) {
        main = fireland;
    }

    public static boolean teleportPlayer(Player player, Location loc, int duration, String sound, Fireland main)
    {
        player.playSound(player.getLocation(), "minecraft:"+sound, (float) 0.1, (float) 1);
        final boolean[] teleported = {false};

        new BukkitRunnable() {
            private int i = -1;

            @Override
            public void run() {
                i++;
                if(playerMoving.containsKey(player) && playerMoving.get(player)){
                    teleported[0] = false;
                    BasicUtilities.sendPlayerError(player,"Téléportation annulée !");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stopsound "+player.getName()+" * minecraft:"+sound);
                    cancel();
                }
                else
                {
                    if((i%5 == 0 && i != duration) || i == duration-3 ||i  == duration-2 || i  == duration-1)
                    {
                        BasicUtilities.sendPlayerInformation(player,"Téléportation dans " +(duration-i)+" secondes");
                    }
                    if(i == duration)
                    {
                        BasicUtilities.sendPlayerInformation(player,"Téléportation...");
                        player.teleport(loc);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a times 20 100 20");

                        cancel();
                    }
                }

            }
        }.runTaskTimer(main, 0L, 20L);
        return teleported[0];
    }

    @EventHandler
    public void PlayerMove(PlayerMoveEvent event)
    {
        if(event.hasChangedPosition())
        {
            if(!playerMoving.containsKey(event.getPlayer()))
            {
                playerMoving.put(event.getPlayer(), true);
            }
            else
            {
                playerMoving.replace(event.getPlayer(), true);
            }
        }
        else
        {
            if(!playerMoving.containsKey(event.getPlayer()))
            {
                playerMoving.put(event.getPlayer(), false);
            }
            else
            {
                playerMoving.replace(event.getPlayer(), false);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                playerMoving.replace(event.getPlayer(), false);
            }
        }.runTaskLater(main, 20L);
    }

}
