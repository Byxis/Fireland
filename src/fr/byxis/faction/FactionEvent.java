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
            if(ff.HasPerk(fname, "friendly_fire"))
            {
                main.hashMapManager.addFactionMap(e.getPlayer().getUniqueId(), fname);
            }

        }
    }

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        if(main.hashMapManager.getFactionMap().containsKey(p.getUniqueId()))
        {
            main.hashMapManager.removeFactionMap(p.getUniqueId());
        }
    }
}
