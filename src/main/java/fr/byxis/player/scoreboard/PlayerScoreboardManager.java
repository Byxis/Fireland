package fr.byxis.player.scoreboard;

import fr.byxis.fireland.Fireland;
import fr.byxis.player.items.infection.virus.InfectionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

public class PlayerScoreboardManager implements @NotNull Listener {

    private final Fireland m_main;
    private static SideBoardManager m_sbManager = null;
    private final NameTagManager m_ntManager;
    private static ScoreboardManager m_manager;

    private static Scoreboard m_scoreboard;

    public PlayerScoreboardManager(Fireland _main, InfectionManager _manager)
    {
        m_main = _main;
        m_manager = Bukkit.getScoreboardManager();
        m_scoreboard = m_manager.getMainScoreboard();
        m_sbManager = new SideBoardManager(m_main, _manager);
        m_ntManager = new NameTagManager(m_main);

        m_main.getServer().getPluginManager().registerEvents(m_ntManager, m_main);
        loop();
    }

    public static void refreshScoreboard(Player _p)
    {
        m_sbManager.updateSideBoard(_p, m_scoreboard, m_manager.getNewScoreboard());
    }

    private void loop()
    {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    refreshScoreboard(p);
                }
            }
        }.runTaskTimer(m_main, 0, 10);
    }

    public static Scoreboard getMainScoreboard()
    {
        return m_scoreboard;
    }

    /*public static Scoreboard getPlayerScoreboard(Player p)
    {
        if (!m_scoreboardPlayers.containsKey(p.getUniqueId()))
        {
            m_scoreboardPlayers.put(p.getUniqueId(), m_scoreboard);
        }
        else if (m_hasScoreboardChanged)
        {
            m_scoreboardPlayers.replace(p.getUniqueId(), m_scoreboard);
        }
        return m_scoreboardPlayers.get(p.getUniqueId());
    }

    public static void setPlayerScoreboard(Player p, Scoreboard board)
    {
        m_scoreboardPlayers.replace(p.getUniqueId(), board);
    }*/
}
