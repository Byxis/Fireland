package fr.byxis.faction;

import fr.byxis.fireland.Fireland;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class FactionPvp implements Listener {

    private final Fireland main;

    public FactionPvp(Fireland main) {
        this.main = main;
    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player)
        {
            Player p = ((Player) event.getEntity()).getPlayer();
            Player d = ((Player) event.getDamager()).getPlayer();

            if(main.hashMapManager.getFactionMap().containsKey(p.getUniqueId()) && main.hashMapManager.getFactionMap().containsKey(d.getUniqueId()))
            {
                if(main.hashMapManager.getFactionMap().get(p.getUniqueId()).equals(main.hashMapManager.getFactionMap().get(d.getUniqueId())))
                {
                    event.setCancelled(true);
                }
            }
        }
    }
}
