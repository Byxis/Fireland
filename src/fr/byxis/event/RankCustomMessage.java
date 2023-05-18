package fr.byxis.event;

import fr.byxis.main.Main;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class RankCustomMessage implements Listener {

    private Main main;

    public RankCustomMessage(Main main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerDeath(PlayerDeathEvent e)
    {
        Entity ent = e.getEntity();
        EntityDamageEvent ede = ent.getLastDamageCause();
        EntityDamageEvent.DamageCause dc = ede.getCause();
        if(ent instanceof Player p && p.hasPermission("fireland.message.admin"))
        {
            switch (dc)
            {
                case FALL -> e.setDeathMessage("§c"+p.getName()+" est mort d'une terrible chute, il était sûrement en train de développer... §kl");
                case FIRE, LAVA, LIGHTNING, FIRE_TICK, MELTING, HOT_FLOOR -> e.setDeathMessage("§c"+p.getName()+" a surchauffé §kl");
                case FREEZE -> e.setDeathMessage("§c"+p.getName()+" a pris un bain d'eau glacée §kl");
                case MAGIC -> e.setDeathMessage("§c"+p.getName()+" est mort après avoir été ensorcelé §kl");
                case SUICIDE -> e.setDeathMessage("§c"+p.getName()+" en avait marre de la vie §kl");
                case DROWNING -> e.setDeathMessage("§c"+p.getName()+" s'est noyé, il était sûrement en train de développer...  §kl");
                case POISON -> e.setDeathMessage("§c"+p.getName()+" a avalé sa capsule de cyanure §kl");
                case CRAMMING, SUFFOCATION -> e.setDeathMessage("§c"+p.getName()+" a été surpassé par les événements §kl");
                case STARVATION -> e.setDeathMessage("§c"+p.getName()+" a oublié de se nourrir d'autres choses que des chips §kl");
                case ENTITY_EXPLOSION, BLOCK_EXPLOSION -> e.setDeathMessage("§c"+p.getName()+" a explosé d'informations §kl");
                case ENTITY_ATTACK -> e.setDeathMessage("§c"+p.getName()+" s'est fait tué par un vilain zombie §kl");
            }
            if(e.getEntity().getKiller() != null)
            {
                e.setDeathMessage("§c"+p.getKiller().getName()+" va se faire ban par "+p.getName()+" pour l'avoir tué §kl");
            }
            else if(main.cfgm.getPlayerDB().getBoolean("infected."+p.getUniqueId()+".state") && e.getEntity().getKiller() == null) {
                e.setDeathMessage("§c"+p.getName()+" s'est fait avoir par sa propre création : l'infection par les zombies §    kl");
            }
        }
        else if(ent instanceof Player p && p.hasPermission("fireland.message.veteran"))
        {

        }
        else if(ent instanceof Player p && p.hasPermission("fireland.message.stratege"))
        {

        }
        else if(ent instanceof Player p && p.hasPermission("fireland.message.mercenaire"))
        {

        }
    }
}
