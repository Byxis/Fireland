package fr.byxis.player.items.infection.virus;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository for managing player infection data.
 * <p>
 * This class handles loading, caching, and saving infection data for players.
 * It uses a ConcurrentHashMap for thread-safe caching and a FileConfiguration
 * for persistent storage.
 */
public class InfectionRepository
{

    private final FileConfiguration m_config;
    private final ConcurrentHashMap<UUID, InfectionData> m_cache;

    public InfectionRepository(FileConfiguration _config)
    {
        m_config = _config;
        m_cache = new ConcurrentHashMap<>();
    }

    public void loadAll()
    {
        if (!m_config.contains("infected")) return;

        for (String uuidStr : m_config.getConfigurationSection("infected").getKeys(false))
        {
            try
            {
                UUID uuid = UUID.fromString(uuidStr);
                InfectionData data = loadFromConfig(uuid);
                m_cache.put(uuid, data);
            }
            catch (IllegalArgumentException ignored)
            {
            }
        }
    }

    public InfectionData get(Player _player)
    {
        return get(_player.getUniqueId());
    }

    public InfectionData get(UUID _uuid)
    {
        return m_cache.computeIfAbsent(_uuid, this::loadFromConfig);
    }

    public void set(Player _player, InfectionData _data)
    {
        set(_player.getUniqueId(), _data);
    }

    public void set(UUID _uuid, InfectionData _data)
    {
        m_cache.put(_uuid, _data);
    }

    public void saveAll()
    {
        for (var entry : m_cache.entrySet())
        {
            saveToConfig(entry.getKey(), entry.getValue());
        }
    }

    private InfectionData loadFromConfig(UUID _uuid)
    {
        String path = "infected." + _uuid;

        if (!m_config.contains(path))
        {
            return InfectionData.HEALTHY;
        }

        int level = m_config.getInt(path + ".level", 0);
        long time = m_config.getLong(path + ".time", 0);

        Instant invincibilityUntil = null;
        if (m_config.contains(path + ".invincibility"))
        {
            long epochMilli = m_config.getLong(path + ".invincibility");
            invincibilityUntil = Instant.ofEpochMilli(epochMilli);
        }

        return new InfectionData(level, time, invincibilityUntil);
    }

    private void saveToConfig(UUID _uuid, InfectionData _data)
    {
        String path = "infected." + _uuid;

        if (_data.m_infectionLevel() == 0 && _data.m_invincibilityUntil() == null)
        {
            m_config.set(path, null);
            return;
        }

        m_config.set(path + ".level", _data.m_infectionLevel());
        m_config.set(path + ".time", _data.m_infectedSince());

        if (_data.m_invincibilityUntil() != null)
        {
            m_config.set(path + ".invincibility", _data.m_invincibilityUntil().toEpochMilli());
        }
        else
        {
            m_config.set(path + ".invincibility", null);
        }
    }
}