package fr.byxis.faction.essaim.services;

import fr.byxis.faction.essaim.conditions.*;
import fr.byxis.faction.essaim.essaimClass.Spawner;
import fr.byxis.fireland.Fireland;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Service for handling all Essaim (essaim) configuration operations.
 * <p>
 * This service provides type-safe access to configuration values with validation,
 * managing all aspects of essaim configuration including:
 * <ul>
 *   <li>Location management (hub, start, reset, entry, etc.)</li>
 *   <li>Spawner configuration</li>
 *   <li>Reward system with cooldowns</li>
 *   <li>Event settings</li>
 *   <li>General essaim properties</li>
 * </ul>
 */
public class EssaimConfigService
{
    private final File m_configfile;
    private FileConfiguration m_config;
    private final Fireland m_fireland;

    /**
     * Constructs a new EssaimConfigService.
     * <p>
     * Initializes the configuration file and loads it from disk.
     *
     * @param _fireland Main instance of the Fireland plugin
     */
    public EssaimConfigService(Fireland _fireland)
    {
        this.m_fireland = _fireland;
        this.m_configfile = new File(_fireland.getDataFolder(), "essaim.yml");
        setupConfiguration();
    }

    /**
     * Gets a location for a specific essaim and location type.
     * <p>
     * Validates that the location exists in configuration and that the world is loaded.
     *
     * @param _essaimName The essaim name
     * @param _locationType The type of location (hub, start, reset, etc.)
     * @return The location
     * @throws ConfigurationException if location is not properly configured or world is not found
     */
    public Location getEssaimLocation(String _essaimName, LocationType _locationType)
    {
        String basePath = _essaimName + "." + _locationType.getConfigPath() + ".position";

        validateLocationExists(basePath, _essaimName, _locationType);

        String worldName = m_config.getString(basePath + ".world");
        World world = null;
        if (worldName != null)
        {
            world = Bukkit.getWorld(worldName);
        }

        if (world == null)
        {
            throw new ConfigurationException("World not found: " + worldName + " for " + _essaimName);
        }

        double x = m_config.getDouble(basePath + ".x");
        double y = m_config.getDouble(basePath + ".y");
        double z = m_config.getDouble(basePath + ".z");

        return new Location(world, x, y, z);
    }

    /**
     * Sets a location for an essaim.
     * <p>
     * Saves the location coordinates and world name to the configuration file.
     *
     * @param _essaimName The essaim name
     * @param _locationType The type of location to set
     * @param _location The location to save
     */
    public void setEssaimLocation(String _essaimName, LocationType _locationType, Location _location)
    {
        String basePath = _essaimName + "." + _locationType.getConfigPath() + ".position";

        m_config.set(basePath + ".world", _location.getWorld().getName());
        m_config.set(basePath + ".x", _location.getX());
        m_config.set(basePath + ".y", _location.getY());
        m_config.set(basePath + ".z", _location.getZ());

        saveConfiguration();
    }

    /**
     * Gets all spawners for a specific essaim.
     * <p>
     * Returns a map of spawner names to Spawner objects containing all spawner configuration.
     *
     * @param _essaimName The essaim name
     *
     * @return Map of spawner names to Spawner objects (empty map if no spawners configured)
     */
    public Map<String, Spawner> getEssaimSpawners(String _essaimName)
    {
        Map<String, Spawner> spawners = new HashMap<>();
        String spawnersPath = _essaimName + ".spawners";

        if (!m_config.contains(spawnersPath))
        {
            return spawners;
        }

        Set<String> spawnerNames = m_config.getConfigurationSection(spawnersPath).getKeys(false);

        for (String spawnerName : spawnerNames)
        {
            spawners.put(spawnerName, createSpawnerFromConfig(_essaimName, spawnerName));
        }

        return spawners;
    }

    /**
     * Gets a specific spawner by name (searches all essaims).
     * <p>
     * Iterates through all essaims to find a spawner with the specified name.
     *
     * @param _spawnerName The spawner name to search for
     * @return The Spawner object if found, null otherwise
     */
    public Spawner getSpawnerByName(String _spawnerName)
    {
        for (String essaimName : getEssaimNames())
        {
            Map<String, Spawner> spawners = getEssaimSpawners(essaimName);
            if (spawners.containsKey(_spawnerName))
            {
                return spawners.get(_spawnerName);
            }
        }
        return null;
    }

    /**
     * Creates a new spawner configuration.
     * <p>
     * Saves all spawner properties to the configuration file.
     *
     * @param _essaimName The essaim name
     * @param _spawnerName The spawner name (unique identifier)
     * @param _mobType The MythicMobs mob type to spawn
     * @param _amount The number of mobs to spawn
     * @param _activationDelay Delay before the spawner activates (in seconds)
     * @param _spawnDelay Delay between each mob spawn (in seconds)
     * @param _command Optional command to execute when spawner activates
     * @param _location The spawner location
     * @param _affectedByDifficulty Whether this spawner's behavior changes with difficulty
     */
    public void createSpawner(String _essaimName, String _spawnerName, String _mobType,
                              int _amount, double _activationDelay, double _spawnDelay,
                              String _command, Location _location, boolean _affectedByDifficulty)
    {

        String basePath = _essaimName + ".spawners." + _spawnerName;

        m_config.set(basePath + ".type", _mobType);
        m_config.set(basePath + ".amount", _amount);
        m_config.set(basePath + ".command", _command);
        m_config.set(basePath + ".activation-delay", _activationDelay);
        m_config.set(basePath + ".spawn-delay", _spawnDelay);
        m_config.set(basePath + ".affected-by-difficulty", _affectedByDifficulty);

        // Position
        m_config.set(basePath + ".position.x", Math.round(_location.getX()));
        m_config.set(basePath + ".position.y", Math.round(_location.getY()));
        m_config.set(basePath + ".position.z", Math.round(_location.getZ()));

        saveConfiguration();
    }

    /**
     * Removes a spawner configuration.
     * <p>
     * Deletes the spawner from the configuration file.
     *
     * @param _essaimName The essaim name
     * @param _spawnerName The spawner name to remove
     */
    public void removeSpawner(String _essaimName, String _spawnerName)
    {
        m_config.set(_essaimName + ".spawners." + _spawnerName, null);
        saveConfiguration();
    }

    /**
     * Creates a new essaim configuration.
     * <p>
     * Initializes a new essaim with the basic required properties.
     *
     * @param _essaimName The essaim name (unique identifier)
     * @param _region The WorldGuard region name for this essaim
     * @param _hubLocation The hub location where players spawn when entering
     */
    public void createEssaim(String _essaimName, String _region, Location _hubLocation)
    {
        m_config.set(_essaimName + ".region", _region);

        setEssaimLocation(_essaimName, LocationType.HUB, _hubLocation);
        saveConfiguration();
    }

    /**
     * Gets basic essaim information with rewards configuration.
     * <p>
     * Retrieves all essential information about an essaim including region, state,
     * event configuration, and reward settings.
     *
     * @param _essaimName The essaim name
     * @return EssaimInfo object containing all essaim data, or null if not found
     */
    public EssaimInfo getEssaimInfo(String _essaimName)
    {
        if (!m_config.contains(_essaimName))
        {
            return null;
        }

        // Parse rewards with the new system
        RewardConfiguration rewardConfig = parseEssaimRewards(_essaimName);

        return new EssaimInfo(
                _essaimName,
                m_config.getString(_essaimName + ".region"),
                m_config.getBoolean(_essaimName + ".closed", false),
                m_config.getBoolean(_essaimName + ".event.isevent", false),
                m_config.getInt(_essaimName + ".event.delay", 180), // 6 months default
                rewardConfig
        );
    }

    /**
     * Parses the rewards configuration with cooldowns for an essaim.
     * <p>
     * Reads both token rewards and command rewards, supporting both legacy format
     * (simple values) and new format (with cooldown specifications).
     *
     * @param _essaimName The essaim name
     * @return RewardConfiguration object with all reward settings
     */
    private RewardConfiguration parseEssaimRewards(String _essaimName)
    {
        String rewardsPath = _essaimName + ".rewards";

        if (!m_config.contains(rewardsPath))
        {
            return new RewardConfiguration();
        }

        RewardConfiguration rewardConfig = new RewardConfiguration();

        // Parse jetons
        if (m_config.contains(rewardsPath + ".jetons"))
        {
            if (m_config.isConfigurationSection(rewardsPath + ".jetons"))
            {
                int amount = m_config.getInt(rewardsPath + ".jetons.amount");
                String cooldown = m_config.getString(rewardsPath + ".jetons.cooldown", "1w");
                rewardConfig.setJetons(amount, cooldown);
            }
            else
            {
                int amount = m_config.getInt(rewardsPath + ".jetons");
                rewardConfig.setJetons(amount, "1w");
            }
        }

        // Parse commands
        if (m_config.contains(rewardsPath + ".commands"))
        {
            List<?> commandsList = m_config.getList(rewardsPath + ".commands");
            if (commandsList != null)
            {
                for (Object cmdObj : commandsList)
                {
                    if (cmdObj instanceof Map)
                    {
                        Map<String, Object> cmdMap = (Map<String, Object>) cmdObj;
                        String command = (String) cmdMap.get("command");
                        String cooldown = (String) cmdMap.getOrDefault("cooldown", "1w");
                        rewardConfig.addCommand(command, cooldown);
                    }
                    else
                    {
                        String command = String.valueOf(cmdObj);
                        rewardConfig.addCommand(command, "1w");
                    }
                }
            }
        }

        if (m_config.contains(rewardsPath + ".xp"))
        {
            int xp = m_config.getInt(rewardsPath + ".xp", 0);
            rewardConfig.setXp(xp);
        }

        return rewardConfig;
    }

    /**
     * Updates essaim closed state.
     * <p>
     * Sets whether the essaim is currently accessible to players.
     *
     * @param _essaimName The essaim name
     * @param _closed True to close the essaim, false to open it
     */
    public void setEssaimClosed(String _essaimName, boolean _closed)
    {
        m_config.set(_essaimName + ".closed", _closed);
        saveConfiguration();
    }

    /**
     * Gets all essaim names configured in the file.
     *
     * @return Set of all essaim names (top-level configuration keys)
     */
    public Set<String> getEssaimNames()
    {
        return m_config.getKeys(false);
    }

    /**
     * Deletes an essaim completely from the configuration.
     * <p>
     * Removes all associated data including spawners, locations, and rewards.
     *
     * @param _essaimName The essaim name to delete
     */
    public void deleteEssaim(String _essaimName)
    {
        m_config.set(_essaimName, null);
        saveConfiguration();
    }

    /**
     * Gets all conditions for a specific essaim.
     * <p>
     * Loads and parses all conditions from the configuration file.
     *
     * @param _essaimName The essaim name
     * @return A list of EssaimCondition objects (empty if none configured)
     */
    public List<EssaimCondition> getEssaimConditions(String _essaimName)
    {
        List<EssaimCondition> conditions = new ArrayList<>();
        String conditionsPath = _essaimName + ".conditions";

        if (!m_config.contains(conditionsPath))
        {
            return conditions;
        }

        List<?> conditionsList = m_config.getList(conditionsPath);
        if (conditionsList == null)
        {
            return conditions;
        }

        for (Object obj : conditionsList)
        {
            if (obj instanceof Map)
            {
                Map<String, Object> conditionMap = (Map<String, Object>) obj;
                String typeStr = (String) conditionMap.get("type");
                String scopeStr = (String) conditionMap.getOrDefault("scope", "leader");

                ConditionType type = ConditionType.fromConfigKey(typeStr);
                ConditionScope scope = ConditionScope.fromConfigKey(scopeStr);

                if (type == null)
                {
                    continue;
                }

                EssaimCondition condition = switch (type)
                {
                    case LEVEL:
                        int level = ((Number) conditionMap.get("level")).intValue();
                        yield new LevelCondition(level, scope);
                    case HAS_ITEM:
                        String materialStr = (String) conditionMap.get("material");
                        String displayName = (String) conditionMap.get("display-name");
                        try
                        {
                            Material material = Material.valueOf(materialStr.toUpperCase());
                            yield new HasItemCondition(material, scope, displayName);
                        }
                        catch (IllegalArgumentException _)
                        {
                            yield null;
                        }
                };
                if (condition != null)
                    conditions.add(condition);
            }
        }

        return conditions;
    }

    /**
     * Checks if an essaim has any conditions configured.
     *
     * @param _essaimName The essaim name
     * @return true if the essaim has conditions, false otherwise
     */
    public boolean hasConditions(String _essaimName)
    {
        return m_config.contains(_essaimName + ".conditions");
    }


    // Configuration management

    /**
     * Reloads the configuration from disk.
     * <p>
     * Discards any unsaved changes and reads the file again.
     */
    public void reloadConfiguration()
    {
        m_config = YamlConfiguration.loadConfiguration(m_configfile);
        m_fireland.getLogger().info("Essaim configuration reloaded!");
    }

    /**
     * Saves the current configuration to disk.
     * <p>
     * Writes all changes to the essaim.yml file.
     */
    public void saveConfiguration()
    {
        try
        {
            m_config.save(m_configfile);
        }
        catch (IOException e)
        {
            m_fireland.getLogger().severe("Could not save essaim.yml: " + e.getMessage());
        }
    }

    /**
     * Gets direct access to the underlying FileConfiguration.
     * <p>
     * Use with caution - prefer using the typed methods when possible.
     *
     * @return The raw FileConfiguration object
     */
    public FileConfiguration getRawConfig()
    {
        return m_config;
    }

    // Private helper methods

    /**
     * Sets up the configuration file.
     * <p>
     * Creates the file if it doesn't exist and loads it into memory.
     */
    private void setupConfiguration()
    {
        if (!m_configfile.exists())
        {
            try
            {
                m_configfile.createNewFile();
                m_fireland.getLogger().info("essaim.yml has been created!");
            }
            catch (IOException e)
            {
                m_fireland.getLogger().severe("Could not create essaim.yml: " + e.getMessage());
            }
        }

        m_config = YamlConfiguration.loadConfiguration(m_configfile);
        m_fireland.getLogger().info("essaim.yml has been loaded!");
    }

    /**
     * Validates that a location exists in the configuration.
     *
     * @param _basePath The base configuration path
     * @param _essaimName The essaim name (for error messages)
     * @param _locationType The location type (for error messages)
     * @throws ConfigurationException if the location is not configured
     */
    private void validateLocationExists(String _basePath, String _essaimName, LocationType _locationType)
    {
        if (!m_config.contains(_basePath + ".world"))
        {
            throw new ConfigurationException(
                    String.format("Missing world configuration for %s %s", _essaimName, _locationType)
            );
        }
    }

    /**
     * Creates a Spawner object from configuration data.
     *
     * @param _essaimName The essaim name
     * @param _spawnerName The spawner name
     * @return Configured Spawner object
     */
    private Spawner createSpawnerFromConfig(String _essaimName, String _spawnerName)
    {
        String basePath = _essaimName + ".spawners." + _spawnerName;

        // Get position
        Location location = new Location(
                Bukkit.getWorld("essaim"), // Default world for spawners
                m_config.getInt(basePath + ".position.x"),
                m_config.getInt(basePath + ".position.y"),
                m_config.getInt(basePath + ".position.z")
        );

        return new Spawner(
                _spawnerName,
                _essaimName,
                location,
                m_config.getString(basePath + ".type"),
                m_config.getInt(basePath + ".amount"),
                m_config.getDouble(basePath + ".activation-delay"),
                m_config.getDouble(basePath + ".spawn-delay"),
                m_config.getString(basePath + ".command", ""),
                m_config.getBoolean(basePath + ".affected-by-difficulty", false)
        );
    }

    // Enums and data classes

    /**
     * Enum representing different types of locations in an essaim.
     * <p>
     * Each type maps to a specific configuration path.
     */
    public enum LocationType
    {
        /** Hub location where players spawn when entering */
        HUB("hub"),

        /** Start location for the expedition */
        START("start"),

        /** Reset location for teleporting players back */
        RESET("reset"),

        /** Entry location */
        ENTRY("entry"),

        /** Solo mode location */
        SOLO("solo"),

        /** Key door location */
        KEY("key"),

        /** Difficulty 1 spawn location */
        DIFFICULTY_1("difficulty.1"),

        /** Difficulty 2 spawn location */
        DIFFICULTY_2("difficulty.2"),

        /** Difficulty 3 spawn location */
        DIFFICULTY_3("difficulty.3");

        private final String m_configPath;

        /**
         * Constructs a LocationType.
         *
         * @param _configPath The configuration path for this location type
         */
        LocationType(String _configPath)
        {
            this.m_configPath = _configPath;
        }

        /**
         * Gets the configuration path for this location type.
         *
         * @return The configuration path string
         */
        public String getConfigPath()
        {
            return m_configPath;
        }
    }

    /**
     * Reward configuration with cooldowns.
     * <p>
     * Stores all reward information including token amounts and command rewards,
     * each with their associated cooldown periods.
     */
    public static class RewardConfiguration
    {
        private int m_jetonsAmount = 0;
        private String m_jetonsCooldown = "1w";
        private final List<CommandReward> m_commandRewards = new ArrayList<>();

        // Nouveau champ pour XP
        private int m_xpAmount = 0;

        /**
         * Sets the token (jetons) reward amount and cooldown.
         *
         * @param _amount Number of tokens to award
         * @param _cooldown Cooldown period (e.g., "1w", "3d", "12h")
         */
        public void setJetons(int _amount, String _cooldown)
        {
            this.m_jetonsAmount = _amount;
            this.m_jetonsCooldown = _cooldown;
        }

        /**
         * Adds a command reward with cooldown.
         *
         * @param _command The command to execute (without leading slash)
         * @param _cooldown Cooldown period (e.g., "1w", "3d", "12h")
         */
        public void addCommand(String _command, String _cooldown)
        {
            this.m_commandRewards.add(new CommandReward(_command, _cooldown));
        }

        /**
         * Gets the token reward amount.
         *
         * @return Number of tokens awarded
         */
        public int getJetonsAmount()
        {
            return m_jetonsAmount;
        }

        /**
         * Gets the token reward cooldown period.
         *
         * @return Cooldown string (e.g., "1w")
         */
        public String getJetonsCooldown()
        {
            return m_jetonsCooldown;
        }

        /**
         * Gets all command rewards.
         *
         * @return List of command rewards
         */
        public List<CommandReward> getCommandRewards()
        {
            return m_commandRewards;
        }

        /**
         * Checks if this configuration includes token rewards.
         *
         * @return true if tokens are awarded, false otherwise
         */
        public boolean hasJetons()
        {
            return m_jetonsAmount > 0;
        }

        public void setXp(int _amount)
        {
            this.m_xpAmount = Math.max(0, _amount);
        }

        public int getXpAmount()
        {
            return this.m_xpAmount;
        }

        public boolean hasXp()
        {
            return this.m_xpAmount > 0;
        }
    }

    /**
     * Represents a command reward with its cooldown.
     * <p>
     * Stores a command to execute and the cooldown period before it can be claimed again.
     */
    public static class CommandReward
    {
        private final String m_command;
        private final String m_cooldown;

        /**
         * Constructs a new CommandReward.
         *
         * @param _command The command to execute
         * @param _cooldown The cooldown period
         */
        public CommandReward(String _command, String _cooldown)
        {
            this.m_command = _command;
            this.m_cooldown = _cooldown;
        }

        /**
         * Gets the command to execute.
         *
         * @return The command string
         */
        public String getCommand()
        {
            return m_command;
        }

        /**
         * Gets the cooldown period.
         *
         * @return The cooldown string (e.g., "1w", "3d")
         */
        public String getCooldown()
        {
            return m_cooldown;
        }
    }

    /**
     * Record containing essential essaim information.
     * <p>
     * Immutable data structure holding all basic essaim properties including
     * name, region, state, event settings, and reward configuration.
     *
     * @param name The essaim name
     * @param region The WorldGuard region name
     * @param closed Whether the essaim is currently closed
     * @param isEvent Whether this essaim is an event
     * @param eventDelay Event delay in days
     * @param rewards The reward configuration
     */
    public record EssaimInfo(String name, String region, boolean closed, boolean isEvent,
                             int eventDelay, RewardConfiguration rewards)
    {
    }

    /**
     * Exception thrown when configuration is invalid or missing required data.
     */
    public static class ConfigurationException extends RuntimeException
    {
        /**
         * Constructs a new ConfigurationException.
         *
         * @param _message The error message
         */
        public ConfigurationException(String _message)
        {
            super(_message);
        }

        /**
         * Constructs a new ConfigurationException with a cause.
         *
         * @param _message The error message
         * @param _cause The underlying cause
         */
        public ConfigurationException(String _message, Throwable _cause)
        {
            super(_message, _cause);
        }
    }
}
