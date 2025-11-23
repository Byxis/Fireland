package fr.byxis.faction.essaim.repository;

import fr.byxis.db.DbConnection;
import fr.byxis.fireland.Fireland;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Repository for Essaim-related database operations Provides clean abstraction
 * over database access with proper error handling
 */
public class EssaimRepository
{

    private final DbConnection m_connection;
    private final Fireland m_fireland;

    public EssaimRepository(DbConnection _connection, Fireland _fireland)
    {
        this.m_connection = _connection;
        this.m_fireland = _fireland;
    }

    /**
     * Records a player's completion of an essaim
     *
     * @param _playerId
     *            The player's UUID
     * @param _essaimName
     *            The essaim name
     *
     * @throws DatabaseException
     *             if the operation fails
     */
    public void recordPlayerCompletion(UUID _playerId, String _essaimName)
    {
        final String sql = "INSERT INTO player_essaim_log (uuid, essaim, date) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = m_connection.getConnection().prepareStatement(sql))
        {
            stmt.setString(1, _playerId.toString());
            stmt.setString(2, _essaimName);
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();
            m_fireland.getLogger().info("Recorded completion for player " + _playerId + " in essaim " + _essaimName);
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Failed to record player completion", e);
        }
    }

    public void recordPlayerReward(UUID _playerId, String _essaimName, String _rewardType, String _rewardId)
    {
        final String sql = "INSERT INTO player_essaim_rewards (uuid, essaim, reward_type, reward_id, date) VALUES " + "(?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = m_connection.getConnection().prepareStatement(sql))
        {
            stmt.setString(1, _playerId.toString());
            stmt.setString(2, _essaimName);
            stmt.setString(3, _rewardType);
            stmt.setString(4, _rewardId);
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Failed to record player reward", e);
        }
    }

    public LocalDateTime getLastRewardDate(UUID _playerId, String _essaimName, String _rewardType, String _rewardId)
    {
        final String sql = "SELECT date FROM player_essaim_rewards WHERE uuid = ? AND essaim = ? AND reward_type = ? "
                + "AND reward_id = ? ORDER BY date DESC LIMIT 1";

        try (PreparedStatement stmt = m_connection.getConnection().prepareStatement(sql))
        {
            stmt.setString(1, _playerId.toString());
            stmt.setString(2, _essaimName);
            stmt.setString(3, _rewardType);
            stmt.setString(4, _rewardId);

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    return rs.getTimestamp("date").toLocalDateTime();
                }
                return null;
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Failed to get last reward date", e);
        }
    }

    public boolean hasPlayerEverReceived(UUID _playerId, String _essaimName, String _rewardType, String _rewardId)
    {
        final String sql = "SELECT 1 FROM player_essaim_rewards WHERE uuid = ? AND essaim = ? AND reward_type = ? "
                + "AND reward_id = ? LIMIT 1";

        try (PreparedStatement stmt = m_connection.getConnection().prepareStatement(sql))
        {
            stmt.setString(1, _playerId.toString());
            stmt.setString(2, _essaimName);
            stmt.setString(3, _rewardType);
            stmt.setString(4, _rewardId);

            try (ResultSet rs = stmt.executeQuery())
            {
                return rs.next();
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Failed to check if player ever received reward", e);
        }
    }

    /**
     * Checks if a player has completed an event-based essaim recently
     *
     * @param _playerId
     *            The player's UUID
     * @param _essaimName
     *            The essaim name
     * @param _cooldownDays
     *            Number of days the player must wait between completions
     *
     * @return true if player is still in a cooldown period
     */
    public boolean isPlayerInCooldown(UUID _playerId, String _essaimName, int _cooldownDays)
    {
        final String sql = """
                SELECT date
                FROM player_essaim_log
                WHERE uuid = ? AND essaim = ?
                ORDER BY date DESC
                LIMIT 1
                """;

        try (PreparedStatement stmt = m_connection.getConnection().prepareStatement(sql))
        {
            stmt.setString(1, _playerId.toString());
            stmt.setString(2, _essaimName);

            try (ResultSet rs = stmt.executeQuery())
            {
                if (!rs.next())
                {
                    return false; // Never completed before
                }

                LocalDateTime lastCompletion = rs.getTimestamp("date").toLocalDateTime();
                LocalDateTime cooldownEnd = lastCompletion.plusDays(_cooldownDays);

                return LocalDateTime.now().isBefore(cooldownEnd);
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Failed to check player cooldown", e);
        }
    }

    /**
     * Gets the last completion date for a player in an essaim
     *
     * @param _playerId
     *            The player's UUID
     * @param _essaimName
     *            The essaim name
     *
     * @return The last completion date, or null if never completed
     */
    public LocalDateTime getLastCompletionDate(UUID _playerId, String _essaimName)
    {
        final String sql = """
                 SELECT date\s
                 FROM player_essaim_log\s
                 WHERE uuid = ? AND essaim = ?\s
                 ORDER BY date DESC\s
                 LIMIT 1
                \s""";

        try (PreparedStatement stmt = m_connection.getConnection().prepareStatement(sql))
        {
            stmt.setString(1, _playerId.toString());
            stmt.setString(2, _essaimName);

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    return rs.getTimestamp("date").toLocalDateTime();
                }
                return null; // Never completed
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Failed to get last completion date", e);
        }
    }

    /**
     * Gets completion statistics for an essaim
     *
     * @param _essaimName
     *            The essaim name
     *
     * @return Stats including total completions, unique players, first and last
     *         completion dates
     */
    public EssaimStats getEssaimStats(String _essaimName)
    {
        final String sql = """
                SELECT
                    COUNT(*) as total_completions,
                    COUNT(DISTINCT uuid) as unique_players,
                    MIN(date) as first_completion,
                    MAX(date) as last_completion
                FROM player_essaim_log
                WHERE essaim = ?
                """;

        try (PreparedStatement stmt = m_connection.getConnection().prepareStatement(sql))
        {
            stmt.setString(1, _essaimName);

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    return new EssaimStats(_essaimName, rs.getInt("total_completions"), rs.getInt("unique_players"),
                            rs.getTimestamp("first_completion"), rs.getTimestamp("last_completion"));
                }
                return new EssaimStats(_essaimName, 0, 0, null, null);
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Failed to get essaim stats", e);
        }
    }

    /**
     * Gets all completions for a player
     *
     * @param _playerId
     *            The player's UUID
     *
     * @return List of completions with essaim names and dates
     */
    public List<PlayerCompletion> getPlayerCompletions(UUID _playerId)
    {
        final String sql = """
                SELECT essaim, date
                FROM player_essaim_log
                WHERE uuid = ?
                ORDER BY date DESC
                """;

        try (PreparedStatement stmt = m_connection.getConnection().prepareStatement(sql))
        {
            stmt.setString(1, _playerId.toString());

            List<PlayerCompletion> completions = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    completions.add(new PlayerCompletion(_playerId, rs.getString("essaim"), rs.getTimestamp("date").toLocalDateTime()));
                }
            }

            return completions;
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Failed to get player completions", e);
        }
    }

    // Data classes

    public record EssaimStats(String essaimName, int totalCompletions, int uniquePlayers, Timestamp firstCompletion,
            Timestamp lastCompletion) {
    }

    public record PlayerCompletion(UUID playerId, String essaimName, LocalDateTime completionDate) {
    }

    public static class DatabaseException extends RuntimeException
    {
        public DatabaseException(String _message)
        {
            super(_message);
        }

        public DatabaseException(String _message, Throwable _cause)
        {
            super(_message, _cause);
        }
    }
}