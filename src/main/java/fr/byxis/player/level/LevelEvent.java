package fr.byxis.player.level;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import jdk.jfr.Enabled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import static fr.byxis.fireland.utilities.InGameUtilities.debugp;
import static fr.byxis.player.level.LevelStorage.*;

public class LevelEvent implements Listener {

    private final Fireland m_main;
    public LevelEvent(Fireland main) {
        m_main = main;
    }

    @EventHandler
    public void playerKillZombie(MythicMobDeathEvent e)
    {
        if(e.getKiller() != null && e.getKiller() instanceof  Player p)
        {
            switch(e.getMob().getMobType())
            {
                case "Infecte" ->
                {
                    addPlayerXp(p.getUniqueId(), 3, LevelStorage.Nation.Etat);
                    AddPlayerZombieKill(p);
                }
                case "Exploseur", "Blinde" ->
                {
                    addPlayerXp(p.getUniqueId(), 10, LevelStorage.Nation.Etat);
                    AddPlayerZombieKill(p);
                }
                case "Hurleur" ->
                {
                    addPlayerXp(p.getUniqueId(), 5, LevelStorage.Nation.Etat);
                    AddPlayerZombieKill(p);
                }
                case "Vautour" ->
                {
                    addPlayerXp(p.getUniqueId(), 20, LevelStorage.Nation.Etat);
                    AddPlayerZombieKill(p);
                }
            }
        }
    }

    @EventHandler
    public void playerKillPlayer(PlayerDeathEvent e)
    {
        if(e.getEntity().getLastDamageCause().getEntity() instanceof Player vctm)
        {
            if (e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)
            {
                EntityDamageByEntityEvent nEvent = (EntityDamageByEntityEvent) e.getEntity().getLastDamageCause();
                if(nEvent.getDamager() instanceof Player killer)
                {
                    if(!LevelStorage.HasPlayerAlreadyKilled(killer, e.getEntity()))
                    {
                        addPlayerXp(killer.getUniqueId(), 60, LevelStorage.Nation.Bannis);
                        LevelStorage.AddPlayerKill(killer, e.getEntity());
                    }
                }

            }
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager && event.getEntity() instanceof Player target) {

            if(getPlayerLevel(target.getUniqueId()).getLevel() < 10)
            {
                InGameUtilities.sendPlayerError(damager, "Ce joueur n'a pas acc�s au pvp, son niveau est inf�rieur � 10.");
                event.setCancelled(true);
            }
            else if(getPlayerLevel(damager.getUniqueId()).getLevel() < 10)
            {
                InGameUtilities.sendPlayerError(damager, "vous n'avez pas acc�s au pvp, votre niveau est inf�rieur � 10.");
                event.setCancelled(true);
            }
        }
    }


}
