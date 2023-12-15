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

import static fr.byxis.fireland.Save.SaveAll;

public class RestartManager {

    private static double m_timeRemaining;
    private static double m_timeTotal;

    private static BossBar m_bar;
    private final Fireland m_main;

    public RestartManager(Fireland _main)
    {
        m_main = _main;
        m_main.getServer().getPluginManager().registerEvents(new restartEvent(), m_main);
        m_main.getCommand("frestart").setExecutor(new restartCommand());
        m_timeRemaining = -1;
        m_timeTotal = -1;
        m_bar = Bukkit.createBossBar("§a§lRedémarrage du serveur", BarColor.GREEN, BarStyle.SOLID);
        m_bar.setProgress(1);

        new BukkitRunnable() {
            @Override
            public void run() {
                if(IsServerRestartingSoon())
                {
                    m_timeRemaining -=1;
                    ActualiseBar();
                }
                else if(IsRestartingNow())
                {
                    Restart(m_main);
                    cancel();
                }
            }
        }.runTaskTimer(m_main, 0, 20);
    }

    public static void StopRestart()
    {
        m_timeRemaining = -1;
        m_timeTotal = -1;
        m_bar.removeAll();
        for(Player p : Bukkit.getOnlinePlayers())
        {
            InGameUtilities.sendPlayerError(p, "Le redémarrage planifié à été annulé.");
        }
    }

    public static void StartRestart(int _time)
    {
        m_timeRemaining = _time;
        m_timeTotal = _time;
        for(Player p :Bukkit.getOnlinePlayers())
        {
            InGameUtilities.sendPlayerError(p, "§lLe serveur va redémarrer dans "+BasicUtilities.getStringTime(_time*1000L));
            m_bar.addPlayer(p);
        }
    }

    public static void Restart(Fireland _main)
    {
        for(Player p : Bukkit.getOnlinePlayers())
        {
            p.kickPlayer("§cLe serveur redémarre.");
        }
        SaveAll(_main);
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            }
        }.runTaskLater(_main, 15*20);

    }

    public void ActualiseBar()
    {
        double totalTime = 60.0; // Remplacez par le temps total en secondes
        if(m_timeRemaining > 15)
        {
            if(m_timeRemaining == 60)
            {
                for(Player p : Bukkit.getOnlinePlayers())
                {
                    InGameUtilities.sendPlayerError(p, "Le serveur redémarre dans 1 minute ! Veuillez prendre vos dispositions !");
                }
            }
            m_bar.setProgress(m_timeRemaining / totalTime);
            m_bar.setTitle("§a§lRedémarrage du serveur dans "+ BasicUtilities.getStringTime((long) (m_timeRemaining*1000L)));
        }
        else if(m_timeRemaining > 0)
        {
            if(m_timeRemaining == 15)
            {
                for(Player p : Bukkit.getOnlinePlayers())
                {
                    InGameUtilities.sendPlayerError(p, "§4§lLe serveur redémarre dans 15 secondes ! Veuillez prendre vos dispositions !");
                }
            }
            m_bar.setColor(BarColor.RED);
            m_bar.setProgress(m_timeRemaining / totalTime);
            m_bar.setTitle("§c§lRedémarrage du serveur dans "+ BasicUtilities.getStringTime((long) (m_timeRemaining*1000L)));
        }
        else if(m_timeRemaining == 0)
        {
            m_bar.setProgress(0);
            m_bar.setTitle("§4§lRedémarrage du serveur imminent");
        }
    }


    public static boolean IsServerRestartingSoon()
    {
        return m_timeRemaining > 0;
    }

    public static boolean IsRestartingNow()
    {
        return m_timeRemaining == 0;
    }

    public static void AddPlayerBar(Player p)
    {
        m_bar.addPlayer(p);
    }




}
