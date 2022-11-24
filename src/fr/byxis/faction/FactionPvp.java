package fr.byxis.faction;

import fr.byxis.main.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class FactionPvp implements Listener {

    private Main main;

    public FactionPvp(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player)
        {
            Player p = ((Player) event.getEntity()).getPlayer();
            Player d = ((Player) event.getDamager()).getPlayer();
            if(main.faction.containsKey(p.getUniqueId()) && main.faction.containsKey(d.getUniqueId()))
            {
                if(main.faction.get(p.getUniqueId()).equals(main.faction.get(d.getUniqueId())))
                {
                    event.setCancelled(true);
                }
            }
        }
    }
}
