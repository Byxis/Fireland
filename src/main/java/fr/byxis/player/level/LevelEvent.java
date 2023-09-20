package fr.byxis.player.level;

import fr.byxis.fireland.Fireland;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import jdk.jfr.Enabled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static fr.byxis.player.level.LevelStorage.AddPlayerZombieKill;
import static fr.byxis.player.level.LevelStorage.addPlayerXp;

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
                case "Infecte", "Blinde", "Vautour" ->
                {
                    addPlayerXp(p.getUniqueId(), 1, LevelStorage.Nation.Etat, true);
                    AddPlayerZombieKill(p);
                }
            }
        }
    }

    @EventHandler
    public void playerKillPlayer(PlayerDeathEvent e)
    {
        if(e.getPlayer().getLastDamageCause().getEntity() instanceof Player killer)
        {
            if(!LevelStorage.HasPlayerAlreadyKilled(killer, e.getPlayer()))
            {
                addPlayerXp(killer.getUniqueId(), 40, LevelStorage.Nation.Bannis, true);
                LevelStorage.AddPlayerKill(killer, e.getPlayer());
            }
        }
    }

}
