package fr.byxis.player.items.infection.virus;

import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.PermissionUtilities;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Gère tous les événements liés au système d'infection.
 */
public class InfectionListeners implements Listener
{

    private final InfectionManager m_manager;

    public InfectionListeners(InfectionManager _manager)
    {
        m_manager = _manager;
    }

    // ==================== COMBAT EVENTS ====================

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent _event)
    {
        if (!(_event.getEntity() instanceof Player _victim)) return;
        if (_victim.isInvulnerable()) return;

        Entity damager = _event.getDamager();

        MobExecutor mobExecutor = MythicBukkit.inst().getMobManager();

        boolean isMythicMob = mobExecutor.isMythicMob(damager);

        if (isMythicMob)
        {
            String internalName = mobExecutor.getMythicMobInstance(damager).getType().getInternalName();
            if (internalName.equals("Infecte"))
            {
                if (m_manager.tryInfect(_victim, InfectionConstants.ZOMBIE_INFECTION_CHANCE))
                {
                    notifyInfection(_victim);
                }
            }
            else if (internalName.equals("Blinde"))
            {
                if (m_manager.tryInfectWithLevel(_victim, InfectionConstants.ZOMBIE_INFECTION_CHANCE, InfectionType.KERATINIC))
                {
                    notifyInfection(_victim);
                }
            }
            else if (internalName.equals("Malabar"))
            {
                if (m_manager.tryInfectWithLevel(_victim, InfectionConstants.ZOMBIE_INFECTION_CHANCE, InfectionType.BRUTAL))
                {
                    notifyInfection(_victim);
                }
            }
            else if (internalName.equals("Vautour"))
            {
                if (m_manager.tryInfectWithLevel(_victim, InfectionConstants.ZOMBIE_INFECTION_CHANCE, InfectionType.NECROPHAGIC))
                {
                    notifyInfection(_victim);
                }
            }
            else if (internalName.equals("Mycoris") || internalName.equals("Rejeton"))
            {
                if (m_manager.tryInfectWithLevel(_victim, InfectionConstants.ZOMBIE_INFECTION_CHANCE, InfectionType.NECROPHAGIC))
                {
                    notifyInfection(_victim);
                }
            }
            else if (internalName.equals("Exploseur") || internalName.equals("Hurleur") || internalName.equals("Putrifieur"))
            {
                if (m_manager.tryInfectWithLevel(_victim, InfectionConstants.ZOMBIE_INFECTION_CHANCE, InfectionType.NECROPHAGIC))
                {
                    notifyInfection(_victim);
                }
            }
        }
        // Infection by player with bare hands
        else if (damager instanceof Player _attacker)
        {
            if (m_manager.isInfected(_attacker) && _attacker.getInventory().getItemInMainHand().getType() == Material.AIR)
            {
                InfectionType type = m_manager.getInfectionType(_attacker);
                if (m_manager.tryInfectWithLevel(_victim, InfectionConstants.PLAYER_INFECTION_CHANCE, type))
                {
                    notifyInfection(_victim);
                }
            }
        }
    }

    // ==================== DEATH EVENTS ====================

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent _event)
    {
        Player player = _event.getEntity();
        player.playSound(player.getLocation(), "minecraft:gun.hud.death", 10, 1);

        if (m_manager.isInfected(player))
        {
            Entity killer = _event.getEntity().getKiller();
            if (killer == null)
            {
                _event.setDeathMessage(player.getName() + " est mort due à son infection !");
            }

            m_manager.handleInfectedDeath(player);
        }

    }

    // ==================== ITEMS EVENTS ====================

    @Warning(reason = "Also implement the repair of items in inventory")
    @EventHandler
    public void onPlayerUseItem(PlayerInteractEvent _event)
    {
        if (_event.getAction() != Action.RIGHT_CLICK_AIR && _event.getAction() != Action.RIGHT_CLICK_BLOCK)
        {
            return;
        }

        Player player = _event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (isSyringe(item))
        {
            handleSyringeUse(player);
        }
        else if (isVaccine(item))
        {
            handleVaccineUse(player);
        }
        else if (isBerserkerSerum(item))
        {
            handleBerserkerSerumUse(player);
        }
        else if (item.getType() == Material.IRON_INGOT && player.getCooldown(Material.IRON_INGOT) <= 0)
        {
            handleInventoryRepair(player);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent _event)
    {
        Player player = _event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (isSyringe(item) && _event.getRightClicked() instanceof Player _target)
        {
            handleSyringeOnFriend(player, _target);
        }
        else if (isBerserkerSerum(item) && _event.getRightClicked() instanceof Player _target)
        {
            handleBerserkerSerumOnFriend(player, _target);
        }
    }

    // ==================== HANDLERS ITEMS ====================

    private void handleSyringeUse(Player _player)
    {
        if (!m_manager.isInfected(_player)) return;

        m_manager.cure(_player);
        InGameUtilities.playWorldSound(_player.getLocation(), "gun.hud.seringue", SoundCategory.PLAYERS, 1, 1);
        _player.sendMessage("§8Vous avez soigné votre infection !");

        consumeItem(_player);
    }

    private void handleVaccineUse(Player _player)
    {
        if (!m_manager.isInfected(_player))
        {
            if (m_manager.grantImmunity(_player))
            {
                InGameUtilities.playWorldSound(_player.getLocation(), "gun.hud.seringue", SoundCategory.PLAYERS, 1, 1);
                InGameUtilities.sendPlayerSucces(_player, "§8Vous êtes immunisé de l'infection pendant 10 minutes !");
                consumeItem(_player);
            }
        }
        else
        {
            InGameUtilities.playWorldSound(_player.getLocation(), "gun.hud.seringue", SoundCategory.PLAYERS, 1, 1);
            InGameUtilities.sendPlayerError(_player, "§8Vous avez aggravé votre infection.");

            m_manager.aggravateInfection(_player);
            consumeItem(_player);
        }
    }

    private void handleSyringeOnFriend(Player _healer, Player _target)
    {
        if (m_manager.isInfected(_healer)) return; // An infected player cannot heal others
        if (!m_manager.isInfected(_target)) return; // The target is not infected

        m_manager.cure(_target);
        InGameUtilities.playWorldSound(_target.getLocation(), "gun.hud.seringue", SoundCategory.PLAYERS, 1, 1);

        _healer.sendMessage("§8Vous avez soigné l'infection de " + _target.getName() + "!");
        _target.sendMessage("§8" + _healer.getName() + " a soigné votre infection !");

        consumeItem(_healer);
    }

    private void handleInventoryRepair(Player _player)
    {
        PermissionUtilities.commandExecutor(_player, "wm repair " + _player.getName() + " INVENTORY", "*");
        _player.getInventory().getItemInMainHand().setAmount(_player.getInventory().getItemInMainHand().getAmount() - 1);
        InGameUtilities.playWorldSound(_player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, SoundCategory.PLAYERS, 1, 2f);
        _player.setCooldown(Material.IRON_INGOT, 20);
    }

    private void handleBerserkerSerumUse(Player _player)
    {
        InGameUtilities.playWorldSound(_player.getLocation(), "gun.hud.seringue", SoundCategory.PLAYERS, 1, 1);
        _player.sendMessage("§cVous avez utilisé un sérum du berserker !");

        applyBerserkerSerum(_player);
        consumeItem(_player);
    }

    private void handleBerserkerSerumOnFriend(Player _healer, Player _target)
    {
        InGameUtilities.playWorldSound(_target.getLocation(), "gun.hud.seringue", SoundCategory.PLAYERS, 1, 1);

        _healer.sendMessage("§cVous avez utilisé un sérum du berserker sur " + _target.getName() + "!");
        _target.sendMessage("§c" + _healer.getName() + " a utilisé un sérum du berserker sur vous !");

        applyBerserkerSerum(_target);
        consumeItem(_healer);
    }

    private void applyBerserkerSerum(Player _player)
    {
        int random = fr.byxis.fireland.utilities.BasicUtilities.generateInt(0, 3); // 0, 1, 2, 3
        if (m_manager.getData(_player).isInfected())
        {
            _player.sendMessage("§cEn raison de votre infection, le sérum vous a tué...");
            _player.setHealth(0);
        }

        if (random == 2)
        {
            _player.sendMessage("§a§lLe sérum fonctionne ! Vous ressentez une puissance immense !");

            _player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SPEED, 20 * 120, 4, true, false));
            _player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.HASTE, 20 * 120, 4, true, false));
            _player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.REGENERATION, 20 * 20, 4, true, false));
            _player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.RESISTANCE, 20 * 40, 1, true, false));

            _player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.WEAKNESS, 20 * 120, 1, true, false));
            _player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.NAUSEA, 20 * 5, 0, true, false));

            _player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SLOWNESS, 20 * 300, 2, true, false));
            _player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.MINING_FATIGUE, 20 * 300, 2, true, false));
            _player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.NIGHT_VISION, 20 * 300, 2, true, false));
        }
        else
        {
            m_manager.infectWithLevel(_player, InfectionType.BRUTAL);
            _player.sendMessage("§c§lLe sérum a échoué... Vous êtes infecté !");
            _player.sendTitle("§c§lÉchec du sérum !", "§8Vous êtes infecté", 10, 70, 20);
            _player.playSound(_player.getLocation(), "minecraft:entity.infected.bite", 1, 1);
        }
    }

    // ==================== UTILS ====================

    private void notifyInfection(Player _player)
    {
        _player.sendMessage("§8Vous avez été infecté ! Trouvez vite une seringue avant que l'infection ne vous tue");
        _player.sendTitle("§8Vous avez été infecté !", "", 10, 70, 20);
        _player.playSound(_player.getLocation(), "minecraft:entity.infected.bite", 1, 1);
    }

    private boolean isSyringe(ItemStack _item)
    {
        return hasCustomModelData(_item, InfectionConstants.SYRINGE_CUSTOM_MODEL_DATA);
    }

    private boolean isVaccine(ItemStack _item)
    {
        return hasCustomModelData(_item, InfectionConstants.VACCINE_CUSTOM_MODEL_DATA);
    }

    private boolean isBerserkerSerum(ItemStack _item)
    {
        return hasCustomModelData(_item, InfectionConstants.BERSERKER_SERUM_CUSTOM_MODEL_DATA);
    }

    private boolean hasCustomModelData(ItemStack _item, int _modelData)
    {
        if (_item == null || _item.getType() != Material.WHEAT_SEEDS) return false;

        ItemMeta meta = _item.getItemMeta();
        if (meta == null || !meta.hasCustomModelData()) return false;

        return meta.getCustomModelData() == _modelData;
    }

    private void consumeItem(Player _player)
    {
        if (_player.getGameMode() == GameMode.CREATIVE) return;

        ItemStack item = _player.getInventory().getItemInMainHand();
        item.setAmount(item.getAmount() - 1);
    }
}