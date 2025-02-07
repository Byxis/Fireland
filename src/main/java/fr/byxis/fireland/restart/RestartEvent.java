package fr.byxis.fireland.restart;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class RestartEvent implements Listener
{


    @EventHandler
    public void playerJoin(PlayerJoinEvent e)
    {
        if (RestartManager.IsServerRestartingSoon())
        {
            RestartManager.AddPlayerBar(e.getPlayer());
        }
        else if (RestartManager.IsRestartingNow())
        {
            e.getPlayer().kickPlayer("§cLe serveur est en train de redémarrer.");
        }
    }


}
