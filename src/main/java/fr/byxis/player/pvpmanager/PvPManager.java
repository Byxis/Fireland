package fr.byxis.player.pvpmanager;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PvPManager implements Listener {

    private static Map<UUID, Integer> pvpTimer;
    private final Fireland main;

    public PvPManager(Fireland _main)
    {
        this.main = _main;
        if (PvPManager.pvpTimer == null)
            pvpTimer = new HashMap<>();
        loop();
    }

    public static void putPvpTimer(Player p)
    {
        if (pvpTimer.containsKey(p.getUniqueId()))
        {
            pvpTimer.replace(p.getUniqueId(), 30);
        }
        else
        {
            pvpTimer.put(p.getUniqueId(), 30);
        }
    }

    public static boolean isOnTimer(Player p)
    {
        if (pvpTimer.containsKey(p.getUniqueId()))
        {
            return pvpTimer.get(p.getUniqueId()) > 0;
        }
        return false;
    }

    public static void removeTimer(UUID uuid)
    {
        if (pvpTimer.containsKey(uuid))
        {
            pvpTimer.replace(uuid, pvpTimer.get(uuid) - 1);
        }
    }

    public static void deleteTimer(UUID uuid)
    {
        pvpTimer.remove(uuid);
    }

    public static void deleteTimer(Player p)
    {
        pvpTimer.remove(p.getUniqueId());
    }

    @EventHandler
    public void playerHit(EntityDamageByEntityEvent e)
    {
        if (e.getEntity() instanceof Player p && !e.isCancelled())
        {
            Entity d = e.getDamager();
            ItemStack itemCrackData = new ItemStack(Material.REDSTONE_BLOCK);
            p.getWorld().spawnParticle(Particle.ITEM, p.getLocation().add(0, 1, 0), 20, 0, 0, 0, 0.1,  itemCrackData);

            if (p.getName().equalsIgnoreCase(d.getName()))
                return;

            if (main.getHashMapManager().getFactionMap().containsKey(p.getUniqueId()) && main.getHashMapManager().getFactionMap().containsKey(d.getUniqueId()))
            {
                if (main.getHashMapManager().getFactionMap().get(p.getUniqueId()).equals(main.getHashMapManager().getFactionMap().get(d.getUniqueId())))
                {
                    return;
                }
            }

            if (d instanceof Player dp && dp.getGameMode() != GameMode.CREATIVE && !d.isInvulnerable())
            {
                if (!pvpTimer.containsKey(d.getUniqueId()))
                {
                    InGameUtilities.sendPlayerError(dp, "Vous entrez en combat pendant 30s. Ne vous déconnectez pas où vous perdrez votre stuff.");
                }
                putPvpTimer(dp);
            }

            if (p.getGameMode() != GameMode.CREATIVE && !p.isInvulnerable())
            {
                if (!pvpTimer.containsKey(p.getUniqueId()))
                {
                    InGameUtilities.sendPlayerError(p, "Vous entrez en combat pendant 30s. Ne vous déconnectez pas où vous perdrez votre stuff.");
                }
                putPvpTimer(p);
            }

        }
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent e)
    {
        if (isOnTimer(e.getPlayer()))
        {
            e.getPlayer().setHealth(0);
            InGameUtilities.sendEveryoneCustomText("§c" + e.getPlayer().getName() + " est mort par déco-combat.");
            deleteTimer(e.getPlayer());
        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent e)
    {
        if (isOnTimer(e.getPlayer()))
        {
            deleteTimer(e.getPlayer());
        }
    }

    private void loop()
    {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (pvpTimer.keySet().isEmpty())
                {
                    return;
                }
                for (UUID uuid : pvpTimer.keySet())
                {
                    removeTimer(uuid);
                    Player p = Bukkit.getPlayer(uuid);
                    if (p != null && p.isOnline())
                    {
                        showTimerBar(p);
                    }
                    if (p != null && pvpTimer.get(p.getUniqueId()) < 0)
                    {
                        deleteTimer(p.getUniqueId());
                        InGameUtilities.sendPlayerInformation(p, "Vous n'êtes plus en combat.");
                    }
                }
            }
        }.runTaskTimer(main, 0, 20);
    }

    private void showTimerBar(Player p) {
        BossBar bar = Bukkit.createBossBar("§c§lEn combat pendant " + (pvpTimer.get(p.getUniqueId()) + 1) + "s", BarColor.RED, BarStyle.SOLID);
        bar.setVisible(true);
        bar.setProgress((double) (pvpTimer.get(p.getUniqueId()) + 1) / 30);
        bar.addPlayer(p);
        new BukkitRunnable() {
            @Override
            public void run() {
                bar.removeAll();
            }
        }.runTaskLater(main, 20);
    }
}
