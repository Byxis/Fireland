package fr.byxis.faction.essaim.essaimClass;

import fr.byxis.faction.essaim.services.EssaimConfigService;
import fr.byxis.fireland.utilities.TextUtilities;
import org.bukkit.Location;

import java.sql.Timestamp;

/**
 * Class representing an Essaim
 */
public class EssaimClass {

    // Basic information
    private final String m_name;
    private final String m_formattedName;
    private final EssaimConfigService.EssaimInfo m_essaimInfo;

    // Locations
    private final Location m_hubLocation;
    private final Location m_entryLocation;
    private final Location m_resetLocation;
    private final Location m_startLocation;
    private final Location m_soloLocation;
    private final Location m_difficulty1Location;
    private final Location m_difficulty2Location;
    private final Location m_difficulty3Location;

    // State
    private boolean m_isClosed;
    private Timestamp m_finishDate;

    // Constants
    private static final int DEFAULT_AUTO_CLOSE_MINUTES = 8;

    public EssaimClass(String _name, EssaimConfigService _configService) {
        this.m_name = validateName(_name);
        this.m_formattedName = TextUtilities.convertStorableToClean(_name);
        this.m_essaimInfo = loadEssaimInfo(_configService, _name);

        // Load locations with validation
        this.m_hubLocation = loadRequiredLocation(_configService, _name, EssaimConfigService.LocationType.HUB);
        this.m_entryLocation = loadRequiredLocation(_configService, _name, EssaimConfigService.LocationType.ENTRY);
        this.m_resetLocation = loadRequiredLocation(_configService, _name, EssaimConfigService.LocationType.RESET);
        this.m_startLocation = loadRequiredLocation(_configService, _name, EssaimConfigService.LocationType.START);
        this.m_soloLocation = loadRequiredLocation(_configService, _name, EssaimConfigService.LocationType.SOLO);

        // Optional difficulty locations
        this.m_difficulty1Location = loadOptionalLocation(_configService, _name, EssaimConfigService.LocationType.DIFFICULTY_1);
        this.m_difficulty2Location = loadOptionalLocation(_configService, _name, EssaimConfigService.LocationType.DIFFICULTY_2);
        this.m_difficulty3Location = loadOptionalLocation(_configService, _name, EssaimConfigService.LocationType.DIFFICULTY_3);

        // Initialize state
        this.m_isClosed = false;
        this.m_finishDate = null;
    }

    /**
     * Marks the essaim as finished
     */
    public void setFinish() {
        this.m_finishDate = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Unmarks the essaim as finished (for side missions of annex)
     */
    public void unFinish() {
        this.m_finishDate = null;
    }

    /**
     * Checks if the essaim is finished
     * @return true if finished, false otherwise
     */
    public boolean isFinished() {
        return m_finishDate != null;
    }

    /**
     * Checks if the essaim should auto-close due to timeout
     * @return true if it should close, false otherwise
     */
    public boolean shouldClose() {
        if (!isFinished()) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        long closeTime = m_finishDate.getTime() + (DEFAULT_AUTO_CLOSE_MINUTES * 60 * 1000L);

        return currentTime >= closeTime;
    }

    /**
     * Gets the remaining time before auto-close in minutes
     * @return remaining minutes, or -1 if not finished
     */
    public long getMinutesUntilClose() {
        if (!isFinished()) {
            return -1;
        }

        long currentTime = System.currentTimeMillis();
        long closeTime = m_finishDate.getTime() + (DEFAULT_AUTO_CLOSE_MINUTES * 60 * 1000L);
        long remainingMs = closeTime - currentTime;

        return Math.max(0, remainingMs / (60 * 1000L));
    }

    /**
     * Checks if this essaim is event-based
     * @return true if event-based, false otherwise
     */
    public boolean isEventBased() {
        return m_essaimInfo.isEvent();
    }

    /**
     * Gets the delay between event completions in days
     * @return delay in days
     */
    public int getDelayBetweenEvents() {
        return m_essaimInfo.eventDelay();
    }


    /**
     * Gets the essaim name
     * @return essaim name
     */
    public String getName() {
        return m_name;
    }

    /**
     * Gets the formatted essaim name (with spaces and capitalization)
     * @return formatted essaim name
     */
    public String getFormattedName() {
        return m_formattedName;
    }

    /**
     * Gets the hub location
     * @return hub location
     */
    public Location getHub() {
        return m_hubLocation.clone();
    }

    /**
     * Gets the entry location
     * @return entry location
     */
    public Location getEntry() {
        return m_entryLocation.clone();
    }

    /**
     * Gets the reset location
     * @return reset location
     */
    public Location getReset() {
        return m_resetLocation.clone();
    }

    /**
     * Gets the start location
     * @return start location
     */
    public Location getStart() {
        return m_startLocation.clone();
    }

    /**
     * Gets the solo location
     * @return solo location
     */
    public Location getSolo() {
        return m_soloLocation.clone();
    }

    /**
     * Gets the difficulty 1 location, or null if not configured
     * @return difficulty 1 location or null
     */
    public Location getDifficulty1() {
        return m_difficulty1Location != null ? m_difficulty1Location.clone() : null;
    }

    /**
     * Gets the difficulty 2 location, or null if not configured
     * @return difficulty 2 location or null
     */
    public Location getDifficulty2() {
        return m_difficulty2Location != null ? m_difficulty2Location.clone() : null;
    }

    /**
     * Gets the difficulty 3 location, or null if not configured
     * @return difficulty 3 location or null
     */
    public Location getDifficulty3() {
        return m_difficulty3Location != null ? m_difficulty3Location.clone() : null;
    }

    /**
     * Gets the finish date as a Timestamp
     * @return finish date or null if not finished
     */
    public Timestamp getFinishDate() {
        return m_finishDate != null ? new Timestamp(m_finishDate.getTime()) : null;
    }

    /**
     * Checks if the essaim is manually closed
     * @return true if closed, false otherwise
     */
    public boolean isClosed() {
        return m_isClosed;
    }

    /**
     * Sets the essaim as manually closed or open
     * @param _isClosed true to close, false to open
     */
    public void setClosed(boolean _isClosed) {
        this.m_isClosed = _isClosed;
    }

    /**
     * Gets the region name associated with this essaim
     * @return region name
     */
    public String getRegion() {
        return m_essaimInfo.region();
    }


    /**
     * Validates the essaim name
     * @param _name essaim name to validate
     * @return trimmed essaim name
     * @throws IllegalArgumentException if name is null or empty
     */
    private String validateName(String _name)
    {
        if (_name == null || _name.trim().isEmpty())
        {
            throw new IllegalArgumentException("Essaim name cannot be null or empty");
        }
        return _name.trim();
    }

    /**
     * Loads the essaim info from the configuration service
     * @param _configService configuration service
     * @param _name essaim name
     * @return EssaimInfo object
     * @throws EssaimConfigurationException if essaim info is not found
     */
    private EssaimConfigService.EssaimInfo loadEssaimInfo(EssaimConfigService _configService, String _name)
    {
        EssaimConfigService.EssaimInfo info = _configService.getEssaimInfo(_name);
        if (info == null) {
            throw new EssaimConfigurationException("Essaim configuration not found: " + _name);
        }
        return info;
    }

    /**
     * Loads a required location from the configuration service
     * @param _configService configuration service
     * @param _name essaim name
     * @param _locationType type of location to load
     * @return Location object
     * @throws EssaimConfigurationException if location is not found
     */
    private Location loadRequiredLocation(EssaimConfigService _configService, String _name,
                                          EssaimConfigService.LocationType _locationType) {
        try {
            return _configService.getEssaimLocation(_name, _locationType);
        } catch (EssaimConfigService.ConfigurationException e) {
            throw new EssaimConfigurationException(
                    "Required location " + _locationType + " not configured for essaim " + _name, e
            );
        }
    }

    /**
     * Loads an optional location from the configuration service
     * @param _configService configuration service
     * @param _name essaim name
     * @param _locationType type of location to load
     * @return Location object or null if not found
     */
    private Location loadOptionalLocation(EssaimConfigService _configService, String _name,
                                          EssaimConfigService.LocationType _locationType) {
        try {
            return _configService.getEssaimLocation(_name, _locationType);
        } catch (EssaimConfigService.ConfigurationException e) {
            // Optional location, return null if not configured
            return null;
        }
    }

    @Override
    public String toString() {
        return "EssaimClass{" +
                "name='" + m_name + '\'' +
                ", formattedName='" + m_formattedName + '\'' +
                ", isFinished=" + isFinished() +
                ", isClosed=" + m_isClosed +
                ", isEventBased=" + isEventBased() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EssaimClass that = (EssaimClass) obj;
        return m_name.equals(that.m_name);
    }

    @Override
    public int hashCode() {
        return m_name.hashCode();
    }

    public static class EssaimConfigurationException extends RuntimeException {
        public EssaimConfigurationException(String message) {
            super(message);
        }

        public EssaimConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}