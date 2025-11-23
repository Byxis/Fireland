package fr.byxis.player.primes;

import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import java.sql.Timestamp;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.world.WorldSaveEvent;

public class PrimeEvent implements Listener
{

    private static PrimesConfig config;
    private final Fireland main;

    private static final long DAY = 14;

    public PrimeEvent(Fireland _main)
    {
        if (PrimeEvent.config == null)
            config = new PrimesConfig(_main);
        this.main = _main;
    }
    public static long getPrimeMaxDay()
    {
        return DAY;
    }
    public static void savePrime()
    {
        if (config.getConfig().contains(""))
        {
            for (String uuid : config.getConfig().getConfigurationSection("").getKeys(false))
            {
                if (new Timestamp(getPrimeDate(uuid).getTime() + getPrimeMaxDay() * 24 * 60 * 60 * 1000)
                        .before(new Timestamp(System.currentTimeMillis())))
                {
                    setPrime(uuid, 0);
                }
            }
        }
    }

    public static int getPrime(Player p)
    {
        if (config.getConfig().contains(p.getUniqueId() + ".value"))
        {
            return config.getConfig().getInt(p.getUniqueId() + ".value");
        }
        return 0;
    }

    public static int getPrime(UUID uuid)
    {
        if (config.getConfig().contains(uuid.toString() + ".value"))
        {
            return config.getConfig().getInt(uuid.toString() + ".value");
        }
        return 0;
    }

    public static int getPrime(String uuid)
    {
        if (config.getConfig().contains(uuid + ".value"))
        {
            return config.getConfig().getInt(uuid + ".value");
        }
        return 0;
    }

    public static Timestamp getPrimeDate(String uuid)
    {
        if (config.getConfig().contains(uuid + ".date"))
        {
            return Timestamp.valueOf(config.getConfig().getString(uuid.toString() + ".date"));
        }
        return null;
    }

    private static void setPrime(Player p, int value)
    {
        if (value == 0)
        {
            config.getConfig().set(p.getUniqueId().toString(), null);
            return;
        }
        config.getConfig().set(p.getUniqueId() + ".value", value);
        config.getConfig().set(p.getUniqueId() + ".date", new Timestamp(System.currentTimeMillis()).toString());
        config.save();
    }

    private static void setPrime(UUID uuid, int value)
    {
        if (value == 0)
        {
            config.getConfig().set(uuid.toString(), null);
            return;
        }
        config.getConfig().set(uuid + ".value", value);
        config.getConfig().set(uuid + ".date", new Timestamp(System.currentTimeMillis()).toString());
        config.save();
    }

    private static void setPrime(String uuid, int value)
    {
        if (value == 0)
        {
            config.getConfig().set(uuid, null);
            config.save();
            return;
        }
        config.getConfig().set(uuid + ".value", value);
        config.getConfig().set(uuid + ".date", new Timestamp(System.currentTimeMillis()).toString());
        config.save();
    }

    @EventHandler
    public void worldSave(WorldSaveEvent e)
    {
        if (!e.getWorld().getName().equalsIgnoreCase("world"))
            return;
        savePrime();
    }

    @EventHandler
    public void playerKill(PlayerDeathEvent e)
    {
        if (e.getPlayer().getLastDamageCause().getEntity() instanceof Player killer)
        {
            Player p = e.getPlayer();
            int prime = getPrime(p);

            FactionFunctions ff = new FactionFunctions(main, null);

            if (prime > 0 && !(ff.playerFactionName(p).equalsIgnoreCase(ff.playerFactionName(killer))))
            {
                Fireland.getEco().depositPlayer(killer, prime);
                setPrime(p, 0);
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    if (!player.getName().equals(killer.getName()) && !player.getName().equals(p.getName()))
                    {
                        InGameUtilities.sendPlayerInformation(player,
                                "Le joueur " + killer.getName() + " a gagné §6" + prime + "$§7 de prime en tuant " + p.getName() + ".");
                    }
                    else if (player.getName().equals(killer.getName()))
                    {
                        InGameUtilities.sendPlayerSucces(player,
                                "Vous avez gagné §6" + prime + "$§7 de prime en tuant " + p.getName() + ".");
                    }
                    else if (player.getName().equals(p.getName()))
                    {
                        InGameUtilities.sendPlayerError(player, killer.getName() + " a gagné §6" + prime + "$§c de prime en vous tuant.");
                    }
                }
            }
        }
    }

    public static PrimesConfig getConfig()
    {
        return config;
    }

    public static void addPrime(Player p, int value)
    {
        setPrime(p, getPrime(p) + value);
    }

    public static void addPrime(UUID uuid, int value)
    {
        setPrime(uuid, getPrime(uuid) + value);
    }
}
