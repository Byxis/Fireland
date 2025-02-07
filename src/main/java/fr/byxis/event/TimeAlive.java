package fr.byxis.event;

import fr.byxis.fireland.Fireland;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class TimeAlive implements Listener
{
    
    private final Fireland main;

    public TimeAlive(Fireland main)
    {
        this.main = main;
    }
    
    @EventHandler
    public void playerRespawn(PlayerRespawnEvent e)
    {
        Player p = e.getPlayer();
        
        main.cfgm.getPlayerDB().set("playtime." + p.getUniqueId(), 0);
        main.cfgm.savePlayerDB();
    }

}
