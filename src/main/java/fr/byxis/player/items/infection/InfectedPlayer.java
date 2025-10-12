package fr.byxis.player.items.infection;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.PermissionUtilities;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Stray;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class InfectedPlayer implements Listener, CommandExecutor
{

    private static Fireland main = null;
    private static FileConfiguration config = null;
    private static HashMap<UUID, Instant> m_invincibility = new HashMap<>();

    public InfectedPlayer(Fireland _main)
    {
        if (InfectedPlayer.main == null)
            InfectedPlayer.main = _main;
        if (InfectedPlayer.config == null)
            InfectedPlayer.config = _main.getCfgm().getPlayerDB();
    }
    
    @EventHandler
    public void playerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity().getPlayer();
        assert p != null;
        if (isInfected(p) && !(e.getEntity().getLastDamageCause().getEntity() instanceof Player))
        {
            e.setDeathMessage(p.getName() + " est mort due à son infection !");
        }
        if (isInfected(p))
        {
            int level = getLevelInfection(p);
            MythicMob mob = null;
            if (level == 0)
            {
                mob = MythicBukkit.inst().getMobManager().getMythicMob("Infecte").orElse(null);

            }
            else if (level >= 1)
            {
                mob = MythicBukkit.inst().getMobManager().getMythicMob("Malabar").orElse(null);
            }
            if (mob != null)
                mob.spawn(BukkitAdapter.adapt(p.getLocation()), 1);
            setInfection(p, false);
        }
        p.playSound(p.getLocation(), "minecraft:gun.hud.death", 10, 1);
    }

    @EventHandler
    public void playerHit(EntityDamageByEntityEvent  e) {
        Entity damaged = e.getEntity();
        Entity damager = e.getDamager();

        boolean randomZombie = Math.random() < 0.2;
        boolean randomPlayer = Math.random() <= 0.5;
        
        if (damaged instanceof Player p) {
            if (damager instanceof Zombie || damager instanceof Stray) {
                if (randomZombie && !p.isInvulnerable())
                {
                    tryApplyInfection(p);
                }
            }
            else if (damager instanceof Player damagerPlayer && isInfected(damagerPlayer))
            {
                if (randomPlayer && !p.isInvulnerable() && damagerPlayer.getItemInHand().getType() == Material.AIR)
                {
                    tryApplyInfection(p);
                }
            }
        }
    }

    private void tryApplyInfection(Player p)
    {
        if (!isInfected(p) && !canBeInfected(p))
        {
            setInfection(p, true);
            p.sendMessage("§8Vous avez été infecté ! Trouvez vite une seringue avant l'infection ne vous tue");
            p.sendTitle("§8Vous avez été infecté !", "");
            p.playSound(p.getLocation(), "minecraft:entity.infected.bite", 1, 1);
        }
    }

    private boolean canBeInfected(Player p) {
        return !isInvincible(p);
    }

    private boolean isInvincible(Player p) {
        Instant now = Instant.now();
        Instant until = m_invincibility.get(p.getUniqueId());
        if (until == null) return false;
        if (until.isAfter(now)) return true;
        m_invincibility.remove(p.getUniqueId(), until);
        return false;
    }

    private boolean addInvincibility(Player p) {
        Instant now = Instant.now();
        Instant existing = m_invincibility.get(p.getUniqueId());
        if (existing != null && existing.isAfter(now)) {
            return false; // déjà invincible
        }
        Instant until = now.plus(Duration.ofMinutes(10));
        m_invincibility.put(p.getUniqueId(), until);
        return true;
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void playerSoin(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (p.getItemInHand().getType() == Material.WHEAT_SEEDS && p.getItemInHand().getItemMeta().hasCustomModelData() && p.getItemInHand().getItemMeta().getCustomModelData() == 102) {


            if (isInfected(p)) {
                setInfection(p, false);
                InGameUtilities.playWorldSound(p.getLocation(), "gun.hud.seringue", SoundCategory.PLAYERS, 1, 1);

                p.sendMessage("§8Vous avez soigné votre infection !");
                if (!p.getGameMode().equals(GameMode.CREATIVE)) {
                    p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
                }
            }
        }
        if (p.getItemInHand().getType() == Material.WHEAT_SEEDS && p.getItemInHand().getItemMeta().hasCustomModelData() && p.getItemInHand().getItemMeta().getCustomModelData() == 114) {


            if (!isInfected(p)) {
                addInvincibility(p);
                InGameUtilities.playWorldSound(p.getLocation(), "gun.hud.seringue", SoundCategory.PLAYERS, 1, 1);

                InGameUtilities.sendPlayerSucces(p, "§8Vous êtes immunisé de l'infection pendant 10 minutes !");
                if (!p.getGameMode().equals(GameMode.CREATIVE)) {
                    p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
                }
            }
            else
            {
                InGameUtilities.playWorldSound(p.getLocation(), "gun.hud.seringue", SoundCategory.PLAYERS, 1, 1);
                InGameUtilities.sendPlayerError(p, "§8Vous avez aggravé votre infection.");

                setInfection(p, true, getLevelInfection(p) + 1);
                setInfectionTime(p, 1);

                if (!p.getGameMode().equals(GameMode.CREATIVE)) {
                    p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
                }
            }
        }
        else if (p.getItemInHand().getType() == Material.IRON_INGOT && p.getCooldown(Material.IRON_INGOT) <= 0)
        {
            PermissionUtilities.commandExecutor(p, "wm repair " + p.getName() + " INVENTORY", "*");
            p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
            InGameUtilities.playWorldSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, SoundCategory.PLAYERS, 1, 2f);
            p.setCooldown(Material.IRON_INGOT, 20);
        }
    }
    
    @SuppressWarnings("deprecation")
    @EventHandler
    public void friendInteraction(PlayerInteractEntityEvent e)
    {
        Player p = e.getPlayer();
        if (p.getItemInHand().getType() == Material.WHEAT_SEEDS && p.getItemInHand().getItemMeta().hasCustomModelData() && p.getItemInHand().getItemMeta().getCustomModelData() == 102) {

            
            if (!(e.getRightClicked() instanceof Player friend) || isInfected(p))
            {
                return;
            }
            if (isInfected(friend)) {
                setInfection(friend, false);

                InGameUtilities.playWorldSound(friend.getLocation(), "gun.hud.seringue", SoundCategory.PLAYERS, 1, 1);
                
                p.sendMessage("§8Vous avez soigné l'infection de " + friend.getName() + "!");
                friend.sendMessage("§8" + p.getName() + " a soigné votre infection !");
                if (!p.getGameMode().equals(GameMode.CREATIVE)) {
                    p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
                }
            }
        }
    }

    public static void setInfection(Player p, boolean state, int... level)
    {
        if (!state)
        {
            if (level != null)
            {
                config.set("infected." + p.getUniqueId() + ".level", level);
            }
            else
            {
                config.set("infected." + p.getUniqueId() + ".level", 0);
            }
            config.set("infected." + p.getUniqueId() + ".time", 0);
        }
        config.set("infected." + p.getUniqueId() + ".state", state);
        main.getCfgm().savePlayerDB();
    }

    public static void setInfectionTime(Player _p, int _time)
    {
        config.set("infected." + _p.getUniqueId() + ".time", _time);
        main.getCfgm().savePlayerDB();
    }

    public static boolean isInfected(Player p)
    {
        return config.getBoolean("infected." + p.getUniqueId() + ".state");
    }

    public static int getTimeInfected(Player p)
    {
        return config.getInt("infected." + p.getUniqueId() + ".time");
    }

    public static int getLevelInfection(Player p)
    {
        if (config.contains("infected." + p.getUniqueId() + ".level"))
            return config.getInt("infected." + p.getUniqueId() + ".level");
        return 0;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String @NotNull [] args) {
        if (sender instanceof Player) {
            if (cmd.getName().equalsIgnoreCase("infect")) {
                Player p = (Player) sender;
                if (args.length == 0) {
                    p.sendMessage("§8Vous avez été infecté !");
                    p.playSound(p.getLocation(), "minecraft:entity.infected.bite", 1, 1);
                    setInfection(p, false);
                    return true;
                }
                else if (args.length == 1)
                {
                    Player victim =  (Player) Bukkit.getOfflinePlayer(BasicUtilities.getUuid(args[0]));
                    p.sendMessage("§8" + victim.getName() + " été infecté !");
                    victim.sendMessage("§8Vous avez été infecté !");
                    victim.sendTitle("§8Vous avez été infecté !", "");
                    victim.playSound(victim.getLocation(), "minecraft:entity.infected.bite", 1, 1);
                    setInfection(victim, false);
                    
                }
                else
                {
                    p.sendMessage("§cMauvaise formulation de la commande ! (/infect [player]");
                }
            }
            else if (cmd.getName().equalsIgnoreCase("cure"))
            {
                Player p = (Player) sender;
                if (args.length == 0) {
                    p.sendMessage("§8Vous avez soigné votre infection !");
                    setInfection(p, false);
                    return true;
                }
                else if (args.length == 1)
                {
                    Player victim = (Player) Bukkit.getOfflinePlayer(BasicUtilities.getUuid(args[0]));
                    p.sendMessage("§8" + victim.getName() + " été soigné !");
                    victim.sendMessage("§8Vous avez été soigné !");
                    setInfection(victim, false);
                }
                else
                {
                    p.sendMessage("§cMauvaise formulation de la commande ! (/cure [player]");
                }
            }
        }
        return false;
    }

}
