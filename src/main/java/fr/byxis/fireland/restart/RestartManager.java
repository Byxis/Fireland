package fr.byxis.fireland.restart;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static fr.byxis.fireland.Save.saveAll;

public class RestartManager {

    private static double m_timeRemaining = -1;
    private static double m_timeTotal = -1;

    private static BossBar m_bar;
    private final Fireland m_main;

    public RestartManager(Fireland _main)
    {
        m_main = _main;
        m_main.getServer().getPluginManager().registerEvents(new RestartEvent(), m_main);
        m_main.getCommand("frestart").setExecutor(new RestartCommand());
        m_bar = Bukkit.createBossBar("§a§lRedémarrage du serveur", BarColor.GREEN, BarStyle.SOLID);
        m_bar.setProgress(1);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (isServerRestartingSoon())
                {
                    m_timeRemaining -= 1;
                    actualiseBar();
                }
                else if (isRestartingNow())
                {
                    restart(m_main);
                    cancel();
                }
            }
        }.runTaskTimer(m_main, 0, 20);
    }

    public static void stopRestart()
    {
        m_timeRemaining = -1;
        m_timeTotal = -1;
        m_bar.removeAll();
        for (Player p : Bukkit.getOnlinePlayers())
        {
            InGameUtilities.sendPlayerError(p, "Le redémarrage planifié à été annulé.");
        }
    }

    public static void startRestart(int _time)
    {
        m_timeRemaining = _time;
        m_timeTotal = _time;
        for (Player p : Bukkit.getOnlinePlayers())
        {
            InGameUtilities.sendPlayerError(p, "§lLe serveur va redémarrer dans " + BasicUtilities.getStringTime(_time * 1000L));
            m_bar.addPlayer(p);
        }
    }

    public static void restart(Fireland _main)
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            p.kickPlayer("§cLe serveur redémarre.");
        }
        saveAll(_main);
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            }
        }.runTaskLater(_main, 15 * 20);

    }

    public void actualiseBar()
    {
        if (m_timeRemaining > 15)
        {
            if (m_timeRemaining == 60)
            {
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    InGameUtilities.sendPlayerError(p, "Le serveur redémarre dans 1 minute ! Veuillez prendre vos dispositions !");
                }
            }
            m_bar.setProgress(m_timeRemaining / m_timeTotal);
            m_bar.setTitle("§a§lRedémarrage du serveur dans " + BasicUtilities.getStringTime((long) (m_timeRemaining * 1000L)));
        }
        else if (m_timeRemaining > 0)
        {
            if (m_timeRemaining == 15)
            {
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    InGameUtilities.sendPlayerError(p, "§4§lLe serveur redémarre dans 15 secondes ! Veuillez prendre vos dispositions !");
                }
            }
            m_bar.setColor(BarColor.RED);
            m_bar.setProgress(m_timeRemaining / m_timeTotal);
            m_bar.setTitle("§c§lRedémarrage du serveur dans " + BasicUtilities.getStringTime((long) (m_timeRemaining * 1000L)));
        }
        else if (m_timeRemaining == 0)
        {
            m_bar.setProgress(0);
            m_bar.setTitle("§4§lRedémarrage du serveur imminent");
        }
    }


    public static boolean isServerRestartingSoon()
    {
        return m_timeRemaining > 0;
    }

    public static boolean isRestartingNow()
    {
        return m_timeRemaining == 0;
    }

    public static void addPlayerBar(Player p)
    {
        m_bar.addPlayer(p);
    }




}
