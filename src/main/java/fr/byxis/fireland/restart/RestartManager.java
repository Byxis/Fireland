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
import org.bukkit.scheduler.BukkitTask;

import static fr.byxis.fireland.Save.SaveAll;

public class RestartManager {

    private static int m_timeRemaining;

    private static BossBar m_bar;
    private final Fireland m_main;

    public RestartManager(Fireland _main)
    {
        m_main = _main;
        m_main.getServer().getPluginManager().registerEvents(new restartEvent(), m_main);
        m_main.getCommand("frestart").setExecutor(new restartCommand());
        StopRestart();
        m_bar = Bukkit.createBossBar("§a§lRedémarrage du serveur", BarColor.GREEN, BarStyle.SOLID);
        m_bar.setProgress(1);

        new BukkitRunnable() {
            @Override
            public void run() {
                if(IsRestartingSoon())
                {
                    m_timeRemaining -=1;
                    ActualiseBar();
                }
                else if(IsRestartingNow())
                {
                    cancel();
                }
            }
        }.runTaskTimer(m_main, 0, 20);
    }

    public static void StopRestart()
    {
        m_timeRemaining = -1;
        m_bar.removeAll();
        for(Player p : Bukkit.getOnlinePlayers())
        {
            InGameUtilities.sendPlayerError(p, "Le redémarrage planifié à été annulé.");
        }
    }

    public static void StartRestart(int _time)
    {
        m_timeRemaining = _time;
        for(Player p :Bukkit.getOnlinePlayers())
        {
            InGameUtilities.sendPlayerError(p, "§lLe serveur va redémarrer dans "+BasicUtilities.getStringTime(_time*20L));
            m_bar.addPlayer(p);
        }
    }

    public static void Restart(Fireland _main)
    {
        SaveAll(_main);
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            }
        }.runTaskLater(_main, 30*20);

    }

    public void ActualiseBar()
    {
        if(m_timeRemaining > 15)
        {
            m_bar.setProgress(1 -  1D /m_timeRemaining);
            m_bar.setTitle("§a§lRedémarrage du serveur dans "+ BasicUtilities.getStringTime(m_timeRemaining* 20L));
        }
        else if(m_timeRemaining > 0)
        {
            m_bar.setColor(BarColor.RED);
            m_bar.setProgress(0);
            m_bar.setTitle("§c§lRedémarrage du serveur dans "+ BasicUtilities.getStringTime(m_timeRemaining* 20L));
        }
        else if(m_timeRemaining == 0)
        {
            m_bar.setProgress(0);
            m_bar.setTitle("§4§lRedémarrage du serveur imminent");
        }
    }

    public static boolean IsRestartingSoon()
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
