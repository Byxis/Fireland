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
        try
        {
            if(event.getEntity() instanceof Player && event.getDamager() instanceof Player)
            {
                Player damager = (Player) event.getDamager();
                Player victim = (Player) event.getEntity();

                FactionFunctions factionFunctions = new FactionFunctions(main, victim);

                FactionInformation factionDamagerName = factionFunctions.getFactionInfo(victim.getName());
                FactionInformation factionVictimName = factionFunctions.getFactionInfo(damager.getName());

                if(factionDamagerName != null && factionVictimName != null)
                {
                    if(!factionDamagerName.getName().equalsIgnoreCase(factionVictimName.getName()))
                    {
                        event.setCancelled(true);
                    }
                }

            }
        } catch (Exception e) {
            event.setCancelled(false);
        }

    }
}
