package fr.byxis.player.items.infection;

import static fr.byxis.player.items.infection.Mask.hasGazMask;

import fr.byxis.fireland.Fireland;
import fr.byxis.player.items.infection.virus.InfectionManager;
import fr.byxis.player.items.infection.virus.InfectionType;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SporeDamage
{

    private final Fireland m_main;
    private final InfectionManager m_manager;
    private final HashMap<UUID, Boolean> m_isAffectedByToxicity;

    public SporeDamage(Fireland _main, InfectionManager _infectionManager)
    {
        this.m_main = _main;
        this.m_isAffectedByToxicity = new HashMap<>();
        this.m_manager = _infectionManager;
        loop();
    }

    public void loop()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    toxicityDamageHandler(p);
                }
            }
        }.runTaskTimer(m_main, 0, 20);
    }

    private void toxicityDamageHandler(Player p)
    {
        if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR || p.isInvulnerable())
        {
            return;
        }
        if (m_isAffectedByToxicity.containsKey(p.getUniqueId()))
        {
            if (m_isAffectedByToxicity.get(p.getUniqueId()))
            {
                if (!m_manager.getData(p).isInfected())
                {
                    p.damage(1);
                    EntityDamageEvent e = new EntityDamageEvent(p, EntityDamageEvent.DamageCause.POISON, 1);
                    p.setLastDamageCause(e);
                    if (m_manager.tryInfectWithLevel(p, 1, InfectionType.MYCELIAL))
                    {
                        p.sendMessage("§8Vous avez été infecté par les spores ! Trouvez vite une seringue avant l'infection ne vous tue");
                        p.sendTitle("§8Vous avez été infecté !", "");
                        p.playSound(p.getLocation(), "minecraft:entity.infected.bite", 1, 1);
                    }
                }
            }
            m_isAffectedByToxicity.replace(p.getUniqueId(), isAffected(p));

        }
        else
        {
            m_isAffectedByToxicity.put(p.getUniqueId(), isAffected(p));
        }
    }

    private boolean isAffected(Player p)
    {
        Location loc = p.getLocation();
        if (hasGazMask(p))
        {
            return false;
        }
        boolean detected = false;
        int h = 0;
        for (int x = -10; x <= 10; x++)
        {
            for (int y = 0; y <= 20; y++)
            {
                for (int z = -10; z <= 10; z++)
                {
                    Location newLoc = new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z);
                    if (newLoc.getBlock().getType() == Material.SPORE_BLOSSOM)
                    {
                        detected = true;
                        h = y;
                        break;
                    }
                }
            }
        }
        if (detected)
        {
            boolean toit = false;
            for (int y = 0; y <= h; y++)
            {
                if (new Location(loc.getWorld(), loc.getX(), loc.getY() + y, loc.getZ()).getBlock().getType() != Material.AIR)
                {
                    toit = true;
                    h = y;
                    break;
                }
            }
            if (toit)
            {
                for (int x = -1; x <= 1; x++)
                {
                    for (int z = -1; z <= 1; z++)
                    {
                        if (new Location(loc.getWorld(), loc.getX() + x, loc.getY() + h, loc.getZ() + z).getBlock()
                                .getType() == Material.AIR)
                        {
                            return true;
                        }
                    }
                }
                return false;
            }
            return true;
        }
        return false;
    }

}
