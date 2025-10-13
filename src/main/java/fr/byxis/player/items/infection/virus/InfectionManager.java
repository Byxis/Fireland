package fr.byxis.player.items.infection.virus;

import fr.byxis.fireland.Fireland;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.time.Instant;

public class InfectionManager
{

    private final Fireland m_fireland;
    private final InfectionRepository m_repository;

    public InfectionManager(Fireland _fireland, InfectionRepository _repository)
    {
        m_fireland = _fireland;
        m_repository = _repository;
    }

    // ==================== GETTERS ====================

    public InfectionData getData(Player _player)
    {
        return m_repository.get(_player);
    }

    public boolean isInfected(Player _player)
    {
        return getData(_player).isInfected();
    }

    public int getInfectionLevel(Player _player)
    {
        return getData(_player).m_infectionLevel();
    }

    public String getInfectionName(Player _player)
    {
        return getData(_player).getInfectionName();
    }

    public boolean isInvincible(Player _player)
    {
        return getData(_player).isCurrentlyInvincible();
    }


    public boolean tryInfect(Player _player, double _probability)
    {
        if (!canBeInfected(_player)) return false;

        if (probabilityCheck(_player, _probability)) return false;

        return infect(_player);
    }

    public boolean tryInfectWithLevel(Player _player, double _probability, int _level)
    {
        if (!canBeInfected(_player)) return false;

        if (probabilityCheck(_player, _probability)) return false;

        return infectWithLevel(_player, _level);
    }

    public boolean infect(Player _player)
    {
        InfectionData newData = InfectionData.createInfection();
        m_repository.set(_player, newData);

        _player.sendMessage("§8Vous avez été infecté par une infection de type " +
                getInfectionName(_player).toLowerCase() + " !");
        _player.sendTitle("§8Vous avez été infecté !", "", 10, 70, 20);
        _player.playSound(_player.getLocation(), "minecraft:entity.infected.bite", 1, 1);

        return true;
    }

    public boolean infectWithLevel(Player _player, int _level)
    {
        InfectionData newData = InfectionData.createInfectionWithLevel(_level);
        m_repository.set(_player, newData);
        return true;
    }

    public void cure(Player _player)
    {
        m_repository.set(_player, InfectionData.HEALTHY);
    }

    public void increaseLevel(Player _player)
    {
        InfectionData currentData = getData(_player);
        InfectionData newData = currentData.increaseLevel();
        m_repository.set(_player, newData);
    }

    public void setLevel(Player _player, int _level)
    {
        InfectionData currentData = getData(_player);
        InfectionData newData = currentData.withLevel(_level);
        m_repository.set(_player, newData);
    }

    public boolean grantImmunity(Player _player)
    {
        InfectionData currentData = getData(_player);

        if (currentData.isCurrentlyInvincible())
        {
            return false;
        }

        Instant until = Instant.now().plus(InfectionConstants.IMMUNITY_DURATION);
        InfectionData newData = currentData.withInvincibility(until);
        m_repository.set(_player, newData);
        return true;
    }

    public void aggravateInfection(Player _player)
    {
        InfectionData currentData = getData(_player);
        int newLevel = currentData.m_infectionLevel() + 1;
        InfectionData newData = new InfectionData(newLevel, System.currentTimeMillis(), currentData.m_invincibilityUntil());
        m_repository.set(_player, newData);
    }

    public void handleInfectedDeath(Player _player)
    {
        if (!isInfected(_player)) return;

        int level = getInfectionLevel(_player);
        MythicMob mob = getMobForLevel(level);

        if (mob != null)
        {
            mob.spawn(BukkitAdapter.adapt(_player.getLocation()), 1);
        }
        cure(_player);
    }

    private MythicMob getMobForLevel(int _level)
    {
        return switch (_level)
        {
            case 2, 3 -> MythicBukkit.inst().getMobManager().getMythicMob("Malabar").orElse(null);
            case 4 -> MythicBukkit.inst().getMobManager().getMythicMob("Malabar").orElse(null);
            default -> MythicBukkit.inst().getMobManager().getMythicMob("Infecte").orElse(null);
        };
    }

    public void saveAll()
    {
        m_repository.saveAll();
    }

    public void loadAll()
    {
        m_repository.loadAll();
    }

    private boolean canBeInfected(Player _player)
    {
        if (isInfected(_player)) return false;
        if (isInvincible(_player)) return false;
        return !_player.isInvulnerable() && _player.getGameMode() != GameMode.CREATIVE;
    }

    private boolean probabilityCheck(Player _player, double _probability)
    {
        double helmetProtection = InfectionConstants.getReductionForHelmet(_player.getInventory().getHelmet());
        double chestplateProtection = InfectionConstants.getReductionForChestplate(_player.getInventory().getChestplate());
        double leggingsProtection = InfectionConstants.getReductionForLeggings(_player.getInventory().getLeggings());
        double bootsProtection = InfectionConstants.getReductionForBoots(_player.getInventory().getBoots());

        double totalProtection = helmetProtection + chestplateProtection + leggingsProtection + bootsProtection;
        double effectiveProbability = _probability * (1.0 - totalProtection);

        return Math.random() <= effectiveProbability;
    }
}