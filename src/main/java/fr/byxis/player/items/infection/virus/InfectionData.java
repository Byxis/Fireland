package fr.byxis.player.items.infection.virus;

import java.time.Instant;

/**
 * Immutable data class representing a player's infection status.
 *
 * @param m_infectionLevel The current level of the infection (0 = safe, 1+ = infected).
 * @param m_infectedSince The timestamp (in milliseconds) when the player was infected.
 * @param m_invincibilityUntil The timestamp until which the player is invincible to new infections.
 */
public record InfectionData(
        int m_infectionLevel,
        long m_infectedSince,
        Instant m_invincibilityUntil
)
{

    public static final InfectionData HEALTHY = new InfectionData(
            0, 0, null);

    public static InfectionData createInfection()
    {
        return new InfectionData(1, System.currentTimeMillis(), null);
    }

    public static InfectionData createInfectionWithLevel(int _level)
    {
        if (_level < 1) throw new IllegalArgumentException("Infection level must be >= 1");
        return new InfectionData(_level, System.currentTimeMillis(), null);
    }

    public boolean isInfected()
    {
        return m_infectionLevel > 0;
    }

    public InfectionData increaseLevel()
    {
        if (m_infectionLevel == 0)
        {
            return createInfection();
        }
        return new InfectionData(m_infectionLevel + 1, System.currentTimeMillis(), m_invincibilityUntil);
    }

    public InfectionData withLevel(int _level)
    {
        if (_level < 0) throw new IllegalArgumentException("Infection level cannot be negative");
        if (_level == 0) return HEALTHY;
        return new InfectionData(_level, System.currentTimeMillis(), m_invincibilityUntil);
    }

    public InfectionData withInvincibility(Instant _until)
    {
        return new InfectionData(m_infectionLevel, m_infectedSince, _until);
    }

    public boolean isCurrentlyInvincible()
    {
        if (m_invincibilityUntil == null) return false;
        return m_invincibilityUntil.isAfter(Instant.now());
    }

    public long getMinutesSinceInfection()
    {
        if (m_infectionLevel == 0) return 0;
        return (System.currentTimeMillis() - m_infectedSince) / 60000;
    }

    public String getInfectionName()
    {
        return switch (m_infectionLevel)
        {
            case 1 -> "Primaire";
            case 2 -> "Kératinienne";
            case 3 -> "Nécrophage";
            case 4 -> "Mycélienne";
            case 5 -> "Brutale";
            default -> "Mortel";
        };
    }
}