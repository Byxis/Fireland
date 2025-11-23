package fr.byxis.player;

import static fr.byxis.fireland.Fireland.getEco;
import static fr.byxis.player.level.LevelStorage.getPlayerLevel;

import fr.byxis.fireland.Fireland;
import fr.byxis.player.level.LevelStorage;
import fr.byxis.player.level.PlayerLevel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerDeath implements Listener
{

    private final Fireland main;

    public PlayerDeath(Fireland _main)
    {
        this.main = _main;
    }

    public static double round(double value, int places)
    {
        if (places < 0)
            throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent e)
    {
        PlayerLevel pl = getPlayerLevel(e.getPlayer().getUniqueId());
        if (pl.getNation().equals(LevelStorage.Nation.Bannis))
        {
            Location loc = new Location(Bukkit.getWorld("world"), 341.5, 72, -209.5);
            e.setRespawnLocation(loc);
        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent e)
    {
        Player killed = e.getEntity();

        if (e.getEntity().getLastDamageCause() == null || !(e.getEntity().getLastDamageCause().getEntity() instanceof Player killer))
            return;

        double money = getEco().getBalance(killed);
        double pay = money / 2;
        if (main.getHashMapManager().getBooster() != null)
        {
            pay = money / (2 * (1 - main.getHashMapManager().getBooster().getBoosterLootPercent() / 100));
        }
        pay = round(pay, 1);
        killed.sendMessage("§cVous avez perdu " + pay + "$ !");
        getEco().withdrawPlayer(killed, pay);
        if (e.getEntity().getKiller() != null && !killed.getName().equalsIgnoreCase(killer.getName()))
        {
            killer.sendMessage("§7Vous avez gagné §c" + pay + "$§7 en tuant §c" + killed.getName() + "§7 !");
            getEco().depositPlayer(killer, pay);
        }
    }

}
