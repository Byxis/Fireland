package fr.byxis.faction;

import fr.byxis.main.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FactionEvent implements Listener {

    private final Main main;
    public FactionEvent(Main main) {
        this.main = main;
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e)
    {
        FactionFunctions ff = new FactionFunctions(main, e.getPlayer());
        String fname = ff.playerFactionName(e.getPlayer());
        if(!fname.equals(""))
        {
            main.factionMap.put(fname, e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        if(main.factionMap.containsKey(p.getUniqueId()))
        {
            main.factionMap.remove(p.getUniqueId());
        }
    }
}
