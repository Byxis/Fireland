package fr.byxis.faction.essaim.managers;

import fr.byxis.faction.essaim.essaimClass.ActiveMobSpawning;
import fr.byxis.faction.essaim.essaimClass.Spawner;
import fr.byxis.faction.essaim.services.EssaimConfigService;
import fr.byxis.fireland.Fireland;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Optional;

/**
 * Manages spawner operations for the Essaim system
 * Handles activation, deactivation, and monitoring of spawners
 */
public class SpawnerManager
{

    private final Fireland m_fireland;
    private final EssaimConfigService m_configService;
    private final GroupManager m_groupManager;
    private final HashMap<String, ActiveMobSpawning> m_activeSpawners;

    public SpawnerManager(Fireland _fireland, EssaimConfigService _configService, GroupManager _groupManager)
    {
        this.m_fireland = _fireland;
        this.m_configService = _configService;
        this.m_groupManager = _groupManager;
        this.m_activeSpawners = new HashMap<>();
    }

    /**
     * Enables a spawner with proper validation and error handling
     *
     * @param _spawner The spawner to enable
     *
     * @return true if spawner was enabled successfully, false if already active
     *
     * @throws IllegalArgumentException if the mythic mob type doesn't exist
     */
    public boolean enableSpawner(Spawner _spawner)
    {
        if (isSpawnerActive(_spawner.getName()))
        {
            return false;
        }

        Optional<MythicMob> mythicMob = getMythicMob(_spawner.getMob());
        if (mythicMob.isEmpty())
        {
            throw new IllegalArgumentException("Mythic mob not found: " + _spawner.getMob());
        }

        int entityCount = calculateEntityCount(_spawner);
        ActiveMobSpawning activeMobSpawning = new ActiveMobSpawning(_spawner.getEssaim(), entityCount + 1);
        m_activeSpawners.put(_spawner.getName(), activeMobSpawning);

        scheduleSpawning(_spawner, mythicMob.get(), entityCount);
        return true;
    }

    /**
     * Removes an active mob from tracking when it dies or despawns
     *
     * @param _mob The active mob that was removed
     */
    public void onMobRemoved(ActiveMob _mob)
    {
        for (String spawnerName : m_activeSpawners.keySet())
        {
            ActiveMobSpawning activeMobSpawning = m_activeSpawners.get(spawnerName);

            if (activeMobSpawning.getActiveMobs().contains(_mob))
            {
                activeMobSpawning.removeActiveMob(_mob);

                if (activeMobSpawning.isSpawnerFinished())
                {
                    handleSpawnerCompletion(spawnerName);
                }
                break;
            }
        }
    }

    /**
     * Cleans up all spawners for a specific essaim
     *
     * @param _essaimName The name of the essaim whose spawners should be cleaned up
     */
    public void cleanupEssaimSpawners(String _essaimName)
    {
        m_activeSpawners.entrySet().removeIf(entry ->
        {
            if (entry.getValue().getEssaim().equals(_essaimName))
            {
                entry.getValue().getActiveMobs().forEach(ActiveMob::setDead);
                return true;
            }
            return false;
        });
    }

    /**
     * Checks if a spawner is currently active
     *
     * @param _spawnerName The name of the spawner to check
     *
     * @return true if the spawner is active, false otherwise
     */
    public boolean isSpawnerActive(String _spawnerName)
    {
        return m_activeSpawners.containsKey(_spawnerName);
    }

    /**
     * Retrieves a copy of the current active spawners
     *
     * @return A HashMap of active spawners
     */
    public HashMap<String, ActiveMobSpawning> getActiveSpawners()
    {
        return new HashMap<>(m_activeSpawners);
    }

    // Private helper methods

    /**
     * Retrieves a MythicMob by name
     *
     * @param _mobName The name of the mythic mob
     *
     * @return An Optional containing the MythicMob if found, or empty if not found
     */
    private Optional<MythicMob> getMythicMob(String _mobName)
    {
        return MythicBukkit.inst().getMobManager().getMythicMob(_mobName);
    }

    /**
     * Calculates the number of entities to spawn based on spawner settings and group difficulty
     *
     * @param _spawner The spawner configuration
     *
     * @return The calculated number of entities to spawn
     */
    private int calculateEntityCount(Spawner _spawner)
    {
        if (!_spawner.isAffectedByDifficulty())
        {
            return _spawner.getAmount();
        }

        if (m_groupManager.hasGroup(_spawner.getEssaim()))
        {
            float difficultyMultiplier = m_groupManager.getGroup(_spawner.getEssaim()).getAdaptiveDifficultyMultiplier();
            return Math.round(_spawner.getAmount() * difficultyMultiplier);
        }

        return _spawner.getAmount();
    }

    /**
     * Schedules the spawning of entities over time based on spawner settings
     *
     * @param _spawner The spawner configuration
     * @param _mythicMob The mythic mob to spawn
     * @param _entityCount The total number of entities to spawn
     */
    private void scheduleSpawning(Spawner _spawner, MythicMob _mythicMob, int _entityCount)
    {
        new BukkitRunnable()
        {
            private int m_remainingEntities = _entityCount;

            @Override
            public void run()
            {
                if (m_remainingEntities <= 0)
                {
                    cancel();
                    return;
                }

                ActiveMob activeMob = _mythicMob.spawn(BukkitAdapter.adapt(_spawner.getLoc()), 1);
                m_remainingEntities--;

                ActiveMobSpawning activeMobSpawning = m_activeSpawners.get(_spawner.getName());
                if (activeMobSpawning != null)
                {
                    activeMobSpawning.addActiveMob(activeMob);

                    if (!activeMob.validateLoadedMob())
                    {
                        activeMob.setDead();
                        activeMobSpawning.removeActiveMob(activeMob);
                    }
                }
            }
        }.runTaskTimer(m_fireland,
                Math.round(_spawner.getActivationDelay() * 20),
                (long) (_spawner.getSpawnDelay() * 20)
        );
    }

    /**
     * Handles the completion of a spawner's lifecycle,
     * Executes any associated commands and removes the spawner from active tracking
     *
     * @param _spawnerName The name of the completed spawner
     */
    private void handleSpawnerCompletion(String _spawnerName)
    {
        Spawner spawner = findSpawnerByName(_spawnerName);
        if (spawner != null && !spawner.getCommand().isEmpty())
        {
            m_fireland.getServer().dispatchCommand(m_fireland.getServer().getConsoleSender(), spawner.getCommand());
        }
        m_activeSpawners.remove(_spawnerName);
    }

    /**
     * Finds a spawner configuration by its name
     *
     * @param _spawnerName The name of the spawner to find
     *
     * @return The Spawner object if found, null otherwise
     */
    private Spawner findSpawnerByName(String _spawnerName)
    {
        return m_configService.getSpawnerByName(_spawnerName);
    }
}