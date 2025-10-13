package fr.byxis.player.items.infection.virus;

import java.time.Instant;

/**
 * Immutable data class representing a player's infection status.
 *
 * @param m_infectionType The current type of the infection (0 = safe, 1+ = infected).
 * @param m_infectedSince The timestamp (in milliseconds) when the player was infected.
 * @param m_invincibilityUntil The timestamp until which the player is invincible to new infections.
 */
public record InfectionData(
        InfectionType m_infectionType,
        long m_infectedSince,
        Instant m_invincibilityUntil
)
{

    public static final InfectionData HEALTHY = new InfectionData(
            InfectionType.SAFE, 0, null);

    public static InfectionData createInfection()
    {
        return new InfectionData(InfectionType.PRIMARY, System.currentTimeMillis(), null);
    }

    public static InfectionData createInfectionWithLevel(InfectionType _type)
    {
        if (_type == InfectionType.SAFE) throw new IllegalArgumentException("Infection level must be greater than SAFE");
        return new InfectionData(_type, System.currentTimeMillis(), null);
    }

    public boolean isInfected()
    {
        return m_infectionType != InfectionType.SAFE;
    }

    public InfectionData increaseLevel()
    {
        if (m_infectionType == InfectionType.SAFE)
        {
            return createInfection();
        }
        if (m_infectionType.ordinal() == InfectionType.values().length - 1)
        {
            return this;
        }
        return new InfectionData(InfectionType.values()[m_infectionType.ordinal() + 1], System.currentTimeMillis(), m_invincibilityUntil);
    }

    public InfectionData withLevel(InfectionType _type)
    {
        if (m_infectionType == InfectionType.SAFE) return HEALTHY;
        return new InfectionData(_type, System.currentTimeMillis(), m_invincibilityUntil);
    }

    public InfectionData withInvincibility(Instant _until)
    {
        return new InfectionData(m_infectionType, m_infectedSince, _until);
    }

    public boolean isCurrentlyInvincible()
    {
        if (m_invincibilityUntil == null) return false;
        return m_invincibilityUntil.isAfter(Instant.now());
    }

    public long getMinutesSinceInfection()
    {
        if (m_infectionType == InfectionType.SAFE) return 0;
        return (System.currentTimeMillis() - m_infectedSince) / 60000;
    }

    public String getInfectionName()
    {
        return switch (m_infectionType)
        {
            case PRIMARY -> "Primaire";
            case KERATINIC -> "Kératinienne";
            case NECROPHAGIC -> "Nécrophage";
            case MYCELIAL -> "Mycélienne";
            case BRUTAL -> "Brutale";
            case BUBONIC -> "Bubonique";
            default -> "Mortel";
        };
    }
}