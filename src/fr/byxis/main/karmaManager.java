package fr.byxis.main;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class karmaManager implements Listener {

    private Main main;
    public karmaManager(Main main) {
        this.main = main;
    }

    public void badAction(UUID _uuid, double _amount)
    {
        updatePlayer(_uuid);
        if(main.cfgm.getKarmaDB().getDouble(_uuid.toString())-_amount < 0)
        {
            main.cfgm.getKarmaDB().set(_uuid.toString(), 0);
        }
        else
        {
            main.cfgm.getKarmaDB().set(_uuid.toString(), main.cfgm.getKarmaDB().getDouble(_uuid.toString())-_amount);
        }

        main.cfgm.saveKarmaDB();
    }

    public void goodAction(UUID _uuid, double _amount)
    {
        updatePlayer(_uuid);
        if(main.cfgm.getKarmaDB().getDouble(_uuid.toString())-_amount > 100)
        {
            main.cfgm.getKarmaDB().set(_uuid.toString(), 100);
        }
        else
        {
            main.cfgm.getKarmaDB().set(_uuid.toString(), main.cfgm.getKarmaDB().getDouble(_uuid.toString())-_amount);
        }
        main.cfgm.saveKarmaDB();
    }

    public void setKarma(UUID _uuid, double _amount)
    {
        updatePlayer(_uuid);
        main.cfgm.getKarmaDB().set(_uuid.toString(), _amount);
        main.cfgm.saveKarmaDB();
    }

    public double getKarma(UUID _uuid)
    {
        return main.cfgm.getKarmaDB().getDouble(_uuid.toString());
    }

    @EventHandler
    public void playerKillPlayer(PlayerDeathEvent e)
    {
        if((e.getEntity().getKiller() != null))
        {
            badAction(e.getEntity().getKiller().getUniqueId(), 10);
        }

        if(getKarma(e.getEntity().getUniqueId())+10 < 50)
        {
            goodAction(e.getEntity().getUniqueId(), 10);
        }
        else
        {
            setKarma(e.getEntity().getUniqueId(), 50);
        }
    }

    @EventHandler
    public void PlayerFirstJoin(PlayerJoinEvent e)
    {
        updatePlayer(e.getPlayer().getUniqueId());
    }

    public void playerKillZombie(EntityDeathEvent e)
    {
        if(e.getEntity() instanceof Zombie && e.getEntity().getKiller() != null)
        {
            goodAction(e.getEntity().getKiller().getUniqueId(), 1/20);
        }
    }

    public void updatePlayer(UUID _uuid)
    {
        FileConfiguration karma = main.cfgm.getKarmaDB();
        if(!karma.contains(_uuid.toString()))
        {
            main.cfgm.getKarmaDB().set(_uuid.toString(), 62D);
            main.cfgm.saveKarmaDB();
        }
    }
}
