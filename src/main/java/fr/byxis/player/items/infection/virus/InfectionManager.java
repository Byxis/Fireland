package fr.byxis.player.items.infection.virus;

import fr.byxis.fireland.Fireland;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.MobExecutor;
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

    public InfectionType getInfectionType(Player _player)
    {
        return getData(_player).m_infectionType();
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

        if (!probabilityCheck(_player, _probability)) return false;

        return infect(_player);
    }

    public boolean tryInfectWithLevel(Player _player, double _probability, InfectionType _type)
    {
        if (!canBeInfected(_player)) return false;

        if (probabilityCheck(_player, _probability)) return false;

        return infectWithLevel(_player, _type);
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

    public boolean infectWithLevel(Player _player, InfectionType _type)
    {
        InfectionData newData = InfectionData.createInfectionWithLevel(_type);
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

    public void setLevel(Player _player, InfectionType _type)
    {
        InfectionData currentData = getData(_player);
        InfectionData newData = currentData.withLevel(_type);
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
        m_repository.set(_player, currentData.increaseLevel());
    }

    public void handleInfectedDeath(Player _player)
    {
        if (!isInfected(_player)) return;

        InfectionType level = getInfectionType(_player);
        MythicMob mob = getMobForInfectionType(level);

        if (mob != null)
        {
            mob.spawn(BukkitAdapter.adapt(_player.getLocation()), 1);
        }
        cure(_player);
    }

    private MythicMob getMobForInfectionType(InfectionType _type)
    {
        MobExecutor mobExecutor = MythicBukkit.inst().getMobManager();
        return switch (_type)
        {
            case SAFE -> null;
            case PRIMARY -> mobExecutor.getMythicMob("Infecte").orElse(null);
            case KERATINIC -> mobExecutor.getMythicMob("Blinde").orElse(null);
            case BUBONIC -> mobExecutor.getMythicMob("Putrifieur").orElse(null);
            case MYCELIAL -> mobExecutor.getMythicMob("Mycoris").orElse(null);
            case BRUTAL -> mobExecutor.getMythicMob("Malabar").orElse(null);
            case NECROPHAGIC -> mobExecutor.getMythicMob("Vautour").orElse(null);
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