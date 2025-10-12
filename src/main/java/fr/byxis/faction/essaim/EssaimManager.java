package fr.byxis.faction.essaim;

import fr.byxis.faction.essaim.essaimClass.EssaimClass;
import fr.byxis.faction.essaim.events.EssaimEventHandler;
import fr.byxis.faction.essaim.managers.GroupManager;
import fr.byxis.faction.essaim.managers.SpawnerManager;
import fr.byxis.faction.essaim.services.*;
import fr.byxis.fireland.Fireland;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * Main manager for the Essaim system
 * Coordinates between different managers and services
 */
public class EssaimManager {

    private final Fireland plugin;
    private final EssaimConfigService configService;
    private final EssaimService essaimService;
    private final GroupManager groupManager;
    private final SpawnerManager spawnerManager;

    public EssaimManager(Fireland plugin) {
        this.plugin = plugin;

        // Initialize services
        this.configService = new EssaimConfigService(plugin);
        this.groupManager = new GroupManager();
        this.spawnerManager = new SpawnerManager(plugin, configService, groupManager);
        this.essaimService = new EssaimService(configService, groupManager, spawnerManager, plugin);

        // Setup existing essaims and spawners
        initializeExistingData();

        // Register events
        plugin.getServer().getPluginManager().registerEvents(
                new EssaimEventHandler(plugin, this),
                plugin
        );

        // Start monitoring loop
        startMonitoringLoop();
    }

    /**
     * Enables an essaim for play
     */
    public boolean enableEssaim(String essaimName) {
        return essaimService.enableEssaim(essaimName);
    }

    /**
     * Disables an essaim
     */
    public boolean disableEssaim(String essaimName) {
        return essaimService.disableEssaim(essaimName);
    }

    /**
     * Forces disable of an essaim (even event-based ones)
     */
    public boolean forceDisableEssaim(String essaimName) {
        return essaimService.forceDisableEssaim(essaimName);
    }

    /**
     * Resets an essaim to its initial state
     */
    public boolean resetEssaim(String essaimName) {
        return essaimService.resetEssaim(essaimName);
    }

    /**
     * Checks if an essaim is currently active
     */
    public boolean isEssaimActive(String essaimName) {
        return essaimService.isEssaimActive(essaimName);
    }

    // Getters for managers and services

    public EssaimConfigService getConfigService() {
        return configService;
    }

    public EssaimService getEssaimService() {
        return essaimService;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    public SpawnerManager getSpawnerManager() {
        return spawnerManager;
    }

    // Private initialization methods

    private void initializeExistingData() {
        // Load existing essaims from configuration
        for (String essaimName : configService.getEssaimNames()) {
            EssaimConfigService.EssaimInfo info = configService.getEssaimInfo(essaimName);

            if (info != null && !info.closed()) {
                essaimService.enableEssaim(essaimName);
            }
        }

        plugin.getLogger().info("Initialized " + configService.getEssaimNames().size() + " essaims");
    }

    private void startMonitoringLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                essaimService.performPeriodicMaintenance();
            }
        }.runTaskTimer(plugin, 0, 20 * 60); // Every minute
    }

    public Map<String, EssaimClass> getActiveEssaims() {
        Map<String, EssaimClass> activeEssaims = new HashMap<>();

        for (String essaimName : configService.getEssaimNames()) {
            if (essaimService.isEssaimActive(essaimName)) {
                EssaimClass essaim = essaimService.getActiveEssaim(essaimName);
                if (essaim != null) {
                    activeEssaims.put(essaimName, essaim);
                }
            }
        }

        return activeEssaims;
    }
}