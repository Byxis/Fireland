package fr.byxis.player.discretion;

import fr.byxis.fireland.Fireland;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DiscretionManager
{
    static final double DETECTION_COEFFICIENT = 0.008;
    static final double MAX_SCORE = 100;
    static final int SNEAKING_REDUCTION = 20;
    static final int MOVEMENT_PENALTY = 30;
    static final int ACTIVE_MOVEMENT_PENALTY = 50;
    static final int AIRBORNE_PENALTY = 20;
    static final int EAT_PENALTY = 15;
    static final int LIGHT_PENALTY = 20;
    private final Map<UUID, DiscretionClass> m_discretionMap;

    public DiscretionManager(Fireland _main)
    {
        this.m_discretionMap = new HashMap<>();
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (Map.Entry<UUID, DiscretionClass> entry : m_discretionMap.entrySet())
                {
                    UUID playerUUID = entry.getKey();
                    DiscretionClass discretion = entry.getValue();

                    Player player = Bukkit.getPlayer(playerUUID);
                    if (player != null && player.isOnline())
                    {
                        discretion.reduceTimeShooting();
                        discretion.actualizeDiscretion(player);
                    }
                }
            }
        }.runTaskTimer(_main, 20L, 20L);
    }

    public DiscretionClass getDiscretion(UUID uuid)
    {
        return m_discretionMap.computeIfAbsent(uuid, k -> new DiscretionClass());
    }

    public void addPlayer(UUID uuid)
    {
        m_discretionMap.putIfAbsent(uuid, new DiscretionClass());
    }

    public void removePlayer(UUID uuid)
    {
        m_discretionMap.remove(uuid);
    }

    public double calculateDetectionRange(UUID uuid)
    {
        double score = getDiscretion(uuid).getScore();
        return DETECTION_COEFFICIENT * Math.pow((MAX_SCORE - score), 2);
    }

    public String getDiscretionColor(UUID uuid)
    {
        DiscretionClass discretion = getDiscretion(uuid);

        if (discretion.isShooting())
        {
            return "§4";
        }
        if (discretion.isUsingCamo())
        {
            return "§2";
        }
        if (discretion.isUsingLights())
        {
            return "§e";
        }
        return "§7";
    }

    public void recordShot(UUID uuid)
    {
        getDiscretion(uuid).setShooting(10);
    }

    public void reduceShootingTime(UUID uuid)
    {
        getDiscretion(uuid).reduceTimeShooting();
    }

    public boolean canBeDetected(UUID playerUuid, double distanceToEntity)
    {
        DiscretionClass discretion = getDiscretion(playerUuid);

        if (discretion.isShooting())
        {
            return true;
        }

        double detectionRange = calculateDetectionRange(playerUuid);
        return distanceToEntity < detectionRange;
    }

    public Map<UUID, DiscretionClass> getMap()
    {
        return new HashMap<>(m_discretionMap);
    }
}
