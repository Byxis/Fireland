package fr.byxis.faction.essaim;

import fr.byxis.faction.essaim.essaimClass.EssaimClass;
import fr.byxis.faction.essaim.events.EssaimEventHandler;
import fr.byxis.faction.essaim.managers.GroupManager;
import fr.byxis.faction.essaim.managers.SpawnerManager;
import fr.byxis.faction.essaim.services.*;
import fr.byxis.fireland.Fireland;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Main manager for the Essaim system Coordinates between different managers and
 * services
 */
public class EssaimManager
{

    private final Fireland m_fireland;
    private final EssaimConfigService m_configService;
    private final EssaimService m_essaimService;
    private final GroupManager m_groupManager;
    private final SpawnerManager m_spawnerManager;

    public EssaimManager(Fireland _fireland)
    {
        this.m_fireland = _fireland;

        // Initialize services
        this.m_configService = new EssaimConfigService(_fireland);
        this.m_groupManager = new GroupManager();
        this.m_spawnerManager = new SpawnerManager(_fireland, m_configService, m_groupManager);
        this.m_essaimService = new EssaimService(m_configService, m_groupManager, m_spawnerManager, _fireland);

        // Setup existing essaims and spawners
        initializeExistingData();

        // Register events
        _fireland.getServer().getPluginManager().registerEvents(new EssaimEventHandler(_fireland, this), _fireland);

        // Start monitoring loop
        startMonitoringLoop();
    }

    /**
     * Enables an essaim for play
     *
     * @param _essaimName
     *            The name of the essaim to enable
     *
     * @return true if the essaim was enabled successfully, false otherwise
     */
    public boolean enableEssaim(String _essaimName)
    {
        return m_essaimService.enableEssaim(_essaimName);
    }

    /**
     * Disables an essaim
     *
     * @param _essaimName
     *            The name of the essaim to disable
     *
     * @return true if the essaim was disabled successfully, false otherwise
     */
    public boolean disableEssaim(String _essaimName)
    {
        return m_essaimService.disableEssaim(_essaimName);
    }

    /**
     * Forces disable of an essaim (even event-based ones)
     *
     * @param _essaimName
     *            The name of the essaim to force disable
     *
     * @return true if the essaim was force disabled successfully, false otherwise
     */
    public boolean forceDisableEssaim(String _essaimName)
    {
        return m_essaimService.forceDisableEssaim(_essaimName);
    }

    /**
     * Resets an essaim to its initial state
     *
     * @param _essaimName
     *            The name of the essaim to reset
     *
     * @return true if the essaim was reset successfully, false otherwise
     */
    public boolean resetEssaim(String _essaimName)
    {
        return m_essaimService.resetEssaim(_essaimName);
    }

    /**
     * Checks if an essaim is currently active
     *
     * @param _essaimName
     *            The name of the essaim to check
     *
     * @return true if the essaim is active, false otherwise
     */
    public boolean isEssaimActive(String _essaimName)
    {
        return m_essaimService.isEssaimActive(_essaimName);
    }

    // Getters for managers and services

    public EssaimConfigService getConfigService()
    {
        return m_configService;
    }

    public EssaimService getEssaimService()
    {
        return m_essaimService;
    }

    public GroupManager getGroupManager()
    {
        return m_groupManager;
    }

    public SpawnerManager getSpawnerManager()
    {
        return m_spawnerManager;
    }

    // Private initialization methods

    private void initializeExistingData()
    {
        // Load existing essaims from configuration
        for (String essaimName : m_configService.getEssaimNames())
        {
            EssaimConfigService.EssaimInfo info = m_configService.getEssaimInfo(essaimName);

            if (info != null && !info.closed())
            {
                m_essaimService.enableEssaim(essaimName);
            }
        }

        m_fireland.getLogger().info("Initialized " + m_configService.getEssaimNames().size() + " essaims");
    }

    private void startMonitoringLoop()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                m_essaimService.performPeriodicMaintenance();
            }
        }.runTaskTimer(m_fireland, 0, 20 * 60); // Every minute
    }

    public Map<String, EssaimClass> getActiveEssaims()
    {
        Map<String, EssaimClass> activeEssaims = new HashMap<>();

        for (String essaimName : m_configService.getEssaimNames())
        {
            if (m_essaimService.isEssaimActive(essaimName))
            {
                EssaimClass essaim = m_essaimService.getActiveEssaim(essaimName);
                if (essaim != null)
                {
                    activeEssaims.put(essaimName, essaim);
                }
            }
        }

        return activeEssaims;
    }
}
