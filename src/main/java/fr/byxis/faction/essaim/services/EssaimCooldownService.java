package fr.byxis.faction.essaim.services;

import fr.byxis.faction.essaim.enums.CooldownType;
import fr.byxis.faction.essaim.essaimClass.EssaimClass;
import fr.byxis.faction.essaim.repository.EssaimRepository;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Service for managing essaim cooldowns.
 * <p>
 * This service handles various types of cooldowns for essaims:
 * <ul>
 *   <li>Event-based cooldowns (for event essaims with delay between participations)</li>
 *   <li>Participation cooldowns (short-term cooldown after completing an essaim)</li>
 *   <li>Reward cooldowns (per-reward cooldown system with multiple types)</li>
 * </ul>
 * <p>
 * Cooldown types include:
 * <ul>
 *   <li>FIRST_TIME - Only once per player</li>
 *   <li>NONE - No cooldown</li>
 *   <li>Time-based (days, weeks, months)</li>
 *   <li>Dynamic time-based (calculated from configuration)</li>
 * </ul>
 */
public class EssaimCooldownService
{

    private static final int PARTICIPATION_COOLDOWN_HOURS = 1;
    private static final int REWARD_COOLDOWN_DAYS = 7;
    private final EssaimRepository m_repository;

    /**
     * Constructs a new EssaimCooldownService.
     *
     * @param _repository The repository for managing cooldown data
     */
    public EssaimCooldownService(EssaimRepository _repository)
    {
        this.m_repository = _repository;
    }

    /**
     * Checks if a player can enter an essaim.
     * <p>
     * For event-based essaims, checks if the player has completed the event cooldown period.
     * For regular essaims, checks if the participation cooldown has expired.
     *
     * @param _player The player to check
     * @param _essaim The essaim to check entry for
     *
     * @return true if the player can enter, false if they're in cooldown
     */
    public boolean canPlayerEnter(Player _player, EssaimClass _essaim)
    {
        if (_essaim.isEventBased())
        {
            return !m_repository.isPlayerInCooldown(
                    _player.getUniqueId(),
                    _essaim.getName(),
                    _essaim.getDelayBetweenEvents()
            );
        }
        return canPlayerParticipate(_player, _essaim.getName());
    }

    /**
     * Checks if a player can participate in an essaim based on participation cooldown.
     * <p>
     * Players must wait {@value #PARTICIPATION_COOLDOWN_HOURS} hour(s) after completing
     * an essaim before they can participate again.
     *
     * @param _player The player to check
     * @param _essaimName The essaim name
     *
     * @return true if the player can participate, false if they're in cooldown
     */
    public boolean canPlayerParticipate(Player _player, String _essaimName)
    {
        LocalDateTime lastParticipation = m_repository.getLastCompletionDate(
                _player.getUniqueId(), _essaimName
        );

        if (lastParticipation == null)
        {
            return true;
        }

        return ChronoUnit.HOURS.between(lastParticipation, LocalDateTime.now()) >= PARTICIPATION_COOLDOWN_HOURS;
    }

    /**
     * Gets the remaining cooldown days for an event-based essaim.
     * <p>
     * Calculates how many days remain before the player can participate in
     * the event essaim again.
     *
     * @param _player The player to check
     * @param _essaim The essaim to check
     *
     * @return Number of days remaining (0 if not in cooldown or not event-based)
     */
    public long getRemainingCooldownDays(Player _player, EssaimClass _essaim)
    {
        if (!_essaim.isEventBased())
        {
            return 0;
        }

        LocalDateTime lastCompletion = m_repository.getLastCompletionDate(
                _player.getUniqueId(), _essaim.getName()
        );

        if (lastCompletion == null)
        {
            return 0;
        }

        LocalDateTime nextAvailable = lastCompletion.plusDays(_essaim.getDelayBetweenEvents());
        return ChronoUnit.DAYS.between(LocalDateTime.now(), nextAvailable);
    }

    /**
     * Gets the remaining participation cooldown in minutes.
     * <p>
     * Calculates how many minutes remain before the player can participate
     * in the essaim again after their last completion.
     *
     * @param _player The player to check
     * @param _essaimName The essaim name
     *
     * @return Number of minutes remaining (0 if not in cooldown)
     */
    public long getRemainingParticipationCooldownMinutes(Player _player, String _essaimName)
    {
        LocalDateTime lastParticipation = m_repository.getLastCompletionDate(
                _player.getUniqueId(), _essaimName
        );

        if (lastParticipation == null)
        {
            return 0;
        }

        LocalDateTime nextAvailable = lastParticipation.plusHours(PARTICIPATION_COOLDOWN_HOURS);
        return Math.max(0, ChronoUnit.MINUTES.between(LocalDateTime.now(), nextAvailable));
    }

    /**
     * Checks if a player can receive a specific reward.
     * <p>
     * Evaluates the reward's cooldown type and determines if enough time has passed
     * or if the conditions are met for the player to receive the reward.
     * <p>
     * Cooldown types:
     * <ul>
     *   <li>FIRST_TIME: Player can only receive this reward once ever</li>
     *   <li>NONE: No cooldown, always available</li>
     *   <li>DYNAMIC_DAY/WEEK/MONTH: Time-based cooldown from configuration</li>
     *   <li>Other: Static time-based cooldowns</li>
     * </ul>
     *
     * @param _playerUuid The player's UUID
     * @param _essaimName The essaim name
     * @param _rewardType The type of reward (e.g., "jetons", "command")
     * @param _rewardId The unique identifier for this reward
     * @param _cooldownValue The cooldown configuration value (e.g., "1w", "first-time", "none")
     *
     * @return true if the player can receive the reward, false otherwise
     */
    public boolean canReceiveReward(UUID _playerUuid, String _essaimName, String _rewardType, String _rewardId, String _cooldownValue)
    {
        CooldownType cooldownType = CooldownType.fromString(_cooldownValue);

        switch (cooldownType)
        {
            case FIRST_TIME:
                return !m_repository.hasPlayerEverReceived(_playerUuid, _essaimName, _rewardType, _rewardId);
            case NONE:
                return true;
            case DYNAMIC_DAY:
            case DYNAMIC_WEEK:
            case DYNAMIC_MONTH:
                return hasPassedCooldown(_playerUuid, _essaimName, _rewardType, _rewardId, _cooldownValue);
            default:
                return hasPassedCooldown(_playerUuid, _essaimName, _rewardType, _rewardId, _cooldownValue);
        }
    }

    /**
     * Checks if enough time has passed since the last reward was received.
     * <p>
     * Compares the last reward date with the current time, taking into account
     * the specified cooldown duration.
     *
     * @param _playerUuid The player's UUID
     * @param _essaimName The essaim name
     * @param _rewardType The type of reward
     * @param _rewardId The reward identifier
     * @param _cooldownValue The cooldown configuration value
     *
     * @return true if the cooldown has passed, false otherwise
     */
    private boolean hasPassedCooldown(UUID _playerUuid, String _essaimName, String _rewardType, String _rewardId, String _cooldownValue)
    {
        Duration cooldownDuration = CooldownType.getDuration(_cooldownValue);

        LocalDateTime lastReward = m_repository.getLastRewardDate(_playerUuid, _essaimName, _rewardType, _rewardId);
        if (lastReward == null) return true;

        return lastReward.plus(cooldownDuration).isBefore(LocalDateTime.now());
    }
}