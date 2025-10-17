package fr.byxis.player.items.infection.virus;

import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.player.packet.PacketFunctions;
import org.bukkit.GameMode;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class InfectionTickSystem extends BukkitRunnable
{

    private static final int TICK_INTERVAL = 100;

    private final Plugin m_plugin;
    private final InfectionManager m_manager;
    private final Map<InfectionType, InfectionLevelConfig> m_levelConfigs;
    private final Map<UUID, Set<Long>> m_sentWarnings;

    public InfectionTickSystem(Plugin _plugin, InfectionManager _manager)
    {
        m_plugin = _plugin;
        m_manager = _manager;
        m_levelConfigs = new HashMap<>();
        m_sentWarnings = new ConcurrentHashMap<>();

        initializeInfectionLevels();
    }

    private void initializeInfectionLevels()
    {
        m_levelConfigs.put(InfectionType.PRIMARY, InfectionLevelConfig.builder()
                .deathTime(5)
                .continuousDamage(1)
                .addWarning(1, "§8Votre infection commence à se faire sentir...", 3)
                .addWarning(2, "§8Votre infection s'aggrave !", 3)
                .addWarning(3, "§8Votre infection devient douloureuse !", 3)
                .addWarning(4, "§8Votre infection est critique !", 3)
                .addWarning(5, "§8Votre infection est très grave ! Cherchez vite une seringue !", 3)
                .addEffectProgression(PotionEffectType.SLOWNESS, 3, 0)
                .build()
        );

        m_levelConfigs.put(InfectionType.BUBONIC, InfectionLevelConfig.builder()
                .deathTime(5)
                .continuousDamage(1)
                .addWarning(1, "§8Vous sentez une douleur sourde dans vos membres...", 2)
                .addWarning(2, "§8L'infection bubonique progresse !", 3)
                .addWarning(3, "§8Vos ganglions commencent à enfler !", 3)
                .addWarning(4, "§8Vous vous sentez de plus en plus mal ! Trouvez une seringue !", 4)
                .addProgressiveEffect(PotionEffectType.WEAKNESS, 0)
                .addEffectProgression(PotionEffectType.WEAKNESS, 2, 1)
                .addEffectProgression(PotionEffectType.POISON, 3, 0)
                .addEffectProgression(PotionEffectType.WEAKNESS, 4, 1)
                .build()
        );

        m_levelConfigs.put(InfectionType.KERATINIC, InfectionLevelConfig.builder()
                .deathTime(4)
                .continuousDamage(2)
                .addWarning(1, "§8Vos membres commencent à se rigidifier...", 2)
                .addWarning(2, "§8L'infection kératinienne progresse !", 3)
                .addWarning(3, "§8Vos articulations deviennent douloureuses !", 3)
                .addWarning(4, "§8Vous avez du mal à bouger ! Trouvez une seringue !", 4)
                .addProgressiveEffect(PotionEffectType.SLOWNESS, 0)
                .addEffectProgression(PotionEffectType.SLOWNESS, 1, 1)
                .addEffectProgression(PotionEffectType.SLOWNESS, 2, 2)
                .addEffectProgression(PotionEffectType.SLOWNESS, 3, 3)
                .addProgressiveEffect(PotionEffectType.MINING_FATIGUE, 0)
                .addEffectProgression(PotionEffectType.MINING_FATIGUE, 2, 1)
                .build()
        );

        m_levelConfigs.put(InfectionType.NECROPHAGIC, InfectionLevelConfig.builder()
                .deathTime(3)
                .continuousDamage(2)
                .addWarning(1, "§8Vous sentez une faiblesse s'installer...", 3)
                .addWarning(2, "§8Votre force vous abandonne ! Cherchez vite une seringue !", 4)
                .addWarning(3, "§8Votre transformation est bientôt aboutie...", 4)
                .addProgressiveEffect(PotionEffectType.WEAKNESS, 0)
                .addEffectProgression(PotionEffectType.WEAKNESS, 2, 1)
                .addProgressiveEffect(PotionEffectType.HUNGER, 0)
                .addEffectProgression(PotionEffectType.HUNGER, 1, 1)
                .addEffectProgression(PotionEffectType.HUNGER, 2, 2)
                .addEffectProgression(PotionEffectType.BLINDNESS, 2, 0)
                .build()
        );

        m_levelConfigs.put(InfectionType.MYCELIAL, InfectionLevelConfig.builder()
                .deathTime(4)
                .continuousDamage(3)
                .addWarning(0, "§8Des spores commencent à envahir vos poumons...", 3)
                .addWarning(1, "§8Le mycélium se propage dans votre organisme !", 4)
                .addWarning(2, "§8Votre vision se trouble, les spores attaquent vos sens !", 4)
                .addWarning(3, "§8L'infection fongique devient critique ! Seule une seringue peut vous sauver !", 5)
                .addProgressiveEffect(PotionEffectType.POISON, 0)
                .addEffectProgression(PotionEffectType.POISON, 1, 1)
                .addEffectProgression(PotionEffectType.BLINDNESS, 2, 0)
                .addEffectProgression(PotionEffectType.NAUSEA, 3, 0)
                .build()
        );

        m_levelConfigs.put(InfectionType.BRUTAL, InfectionLevelConfig.builder()
                .deathTime(2)
                .continuousDamage(4)
                .addWarning(0, "§8Votre corps commence une transformation horrifiante...", 5)
                .addWarning(1, "§8La mutation progresse rapidement ! Cherchez de l'aide immédiatement !", 6)
                .addProgressiveEffect(PotionEffectType.WITHER, 0)
                .addEffectProgression(PotionEffectType.WITHER, 1, 2)
                .addProgressiveEffect(PotionEffectType.SLOWNESS, 1)
                .addEffectProgression(PotionEffectType.SLOWNESS, 1, 2)
                .addEffectProgression(PotionEffectType.BLINDNESS, 1, 0)
                .build()
        );
    }

    public void start()
    {
        this.runTaskTimer(m_plugin, 0L, TICK_INTERVAL);
    }

    @Override
    public void run()
    {
        for (Player player : m_plugin.getServer().getOnlinePlayers())
        {
            if (!isValidGameMode(player)) continue;

            InfectionData data = m_manager.getData(player);
            if (!data.isInfected())
            {
                m_sentWarnings.remove(player.getUniqueId());
                PacketFunctions.sendWorldBorderWarningDistancePacket(player, 0.0);
                continue;
            }

            InfectionType level = data.m_infectionType();
            InfectionLevelConfig config = m_levelConfigs.get(level);

            if (config == null)
            {
                config = m_levelConfigs.get(InfectionType.PRIMARY);
            }

            processInfection(player, data, config);
        }
    }

    private void processInfection(Player _player, InfectionData _data, InfectionLevelConfig _config)
    {
        long minutesInfected = _data.getMinutesSinceInfection();

        applyProgressiveEffects(_player, minutesInfected, _config);

        playSickSound(_player, minutesInfected, _config.m_deathTime);

        applyWorldBorderEffect(_player, minutesInfected, _config.m_deathTime);

        if (minutesInfected >= _config.m_deathTime)
        {
            _player.sendMessage("§8Votre infection a causé votre perte....");
            _player.setHealth(0.0);
            return;
        }

        Set<Long> playerWarnings = m_sentWarnings.computeIfAbsent(_player.getUniqueId(), k -> ConcurrentHashMap.newKeySet());

        for (var warning : _config.m_specificWarnings.entrySet())
        {
            long warningMinute = warning.getKey();
            WarningData warningData = warning.getValue();
            if (minutesInfected >= warningMinute && !playerWarnings.contains(warningMinute))
            {
                _player.sendMessage(warningData.m_message);

                playerWarnings.add(warningMinute);
            }
        }

        if (_config.m_warningInterval > 0 && minutesInfected > 0)
        {
            if (minutesInfected % _config.m_warningInterval == 0 && !playerWarnings.contains(minutesInfected))
            {
                _player.sendMessage(_config.m_warningMessage);
                playerWarnings.add(minutesInfected);
            }
        }

        if (_player.getHealth() > _config.m_continuousDamage)
        {
            _player.damage(_config.m_continuousDamage);
        }
    }

    /**
     * Applique un effet de bordure mondiale progressif (teinte rouge)
     *
     * @param _player Le joueur infecté
     * @param _currentMinute La minute actuelle d'infection
     * @param _deathTime Le temps de mort configuré
     */
    private void applyWorldBorderEffect(Player _player, long _currentMinute, long _deathTime)
    {
        double progression = Math.min(1.0, (double) _currentMinute / _deathTime);

        double intensity = 0.5 + (0.5 * progression);

        PacketFunctions.sendWorldBorderWarningDistancePacket(_player, intensity);
    }

    /**
     * Joue le son "entity.player.sick" avec une probabilité croissante
     *
     * @param _player Le joueur infecté
     * @param _currentMinute La minute actuelle d'infection
     * @param _deathTime Le temps de mort configuré
     */
    private void playSickSound(Player _player, long _currentMinute, long _deathTime)
    {
        double progression = Math.min(1.0, (double) _currentMinute / _deathTime);
        double baseProbability = 0.15 + (0.7 * progression);

        if (progression > 0.8)
        {
            double finalBoost = (progression - 0.8) / 0.2;
            baseProbability = 0.15 + (0.7 * 0.8) + (0.3 * finalBoost);
        }

        double randomValue = ThreadLocalRandom.current().nextDouble();

        if (randomValue < baseProbability)
        {
            float volume = (float) (0.3 + (0.7 * progression));

            float pitch = 0.9f + ThreadLocalRandom.current().nextFloat() * 0.2f;

            InGameUtilities.playWorldSound(_player.getLocation(), "entity.player.sick", SoundCategory.PLAYERS, volume, pitch);
        }
    }

    private void applyProgressiveEffects(Player _player, long _minutes, InfectionLevelConfig _config)
    {
        for (var effectEntry : _config.m_progressiveEffects.entrySet())
        {
            PotionEffectType effectType = effectEntry.getKey();
            int baseAmplifier = effectEntry.getValue();

            TreeMap<Long, Integer> progressions = _config.m_effectProgressions.get(effectType);

            int currentAmplifier = baseAmplifier;
            if (progressions != null)
            {
                for (var progression : progressions.entrySet())
                {
                    if (_minutes >= progression.getKey())
                    {
                        currentAmplifier = progression.getValue();
                    }
                    else
                    {
                        break;
                    }
                }
            }

            _player.addPotionEffect(
                    new PotionEffect(effectType, 240, currentAmplifier, false, false)
            );
        }
    }

    private boolean isValidGameMode(Player _player)
    {
        GameMode mode = _player.getGameMode();
        return mode == GameMode.SURVIVAL || mode == GameMode.ADVENTURE;
    }

    private static class InfectionLevelConfig
    {
        private final long m_deathTime;
        private final int m_continuousDamage;
        private final Map<Long, WarningData> m_specificWarnings;
        private final long m_warningInterval;
        private final String m_warningMessage;
        private final Map<PotionEffectType, Integer> m_progressiveEffects;
        private final Map<PotionEffectType, TreeMap<Long, Integer>> m_effectProgressions;

        private InfectionLevelConfig(Builder _builder)
        {
            m_deathTime = _builder.m_deathTime;
            m_continuousDamage = _builder.m_continuousDamage;
            m_specificWarnings = _builder.m_specificWarnings;
            m_warningInterval = _builder.m_warningInterval;
            m_warningMessage = _builder.m_warningMessage;
            m_progressiveEffects = _builder.m_progressiveEffects;
            m_effectProgressions = _builder.m_effectProgressions;
        }

        public static Builder builder()
        {
            return new Builder();
        }

        public static class Builder
        {
            private long m_deathTime = 10;
            private int m_continuousDamage = 0;
            private final Map<Long, WarningData> m_specificWarnings = new HashMap<>();
            private long m_warningInterval = 0;
            private String m_warningMessage = "";
            private final Map<PotionEffectType, Integer> m_progressiveEffects = new HashMap<>();
            private final Map<PotionEffectType, TreeMap<Long, Integer>> m_effectProgressions = new HashMap<>();

            public Builder deathTime(long _minutes)
            {
                m_deathTime = _minutes;
                return this;
            }

            public Builder continuousDamage(int _damage)
            {
                m_continuousDamage = _damage;
                return this;
            }

            public Builder addWarning(long _minute, String _message, int _damage)
            {
                m_specificWarnings.put(_minute, new WarningData(_message, _damage));
                return this;
            }

            public Builder warningInterval(long _interval)
            {
                m_warningInterval = _interval;
                return this;
            }

            public Builder warningMessage(String _message)
            {
                m_warningMessage = _message;
                return this;
            }

            public Builder addProgressiveEffect(PotionEffectType _type, int _baseAmplifier)
            {
                m_progressiveEffects.put(_type, _baseAmplifier);
                return this;
            }

            public Builder addEffectProgression(PotionEffectType _type, long _minute, int _amplifier)
            {
                m_effectProgressions.computeIfAbsent(_type, k -> new TreeMap<>())
                        .put(_minute, _amplifier);
                return this;
            }

            public InfectionLevelConfig build()
            {
                return new InfectionLevelConfig(this);
            }
        }
    }

    private static class WarningData
    {
        private final String m_message;
        private final int m_damage;

        public WarningData(String _message, int _damage)
        {
            m_message = _message;
            m_damage = _damage;
        }
    }
}