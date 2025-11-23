package fr.byxis.player.scoreboard;

import static fr.byxis.fireland.Fireland.getEco;
import static fr.byxis.player.level.LevelStorage.getPlayerLevel;

import fr.byxis.fireland.Fireland;
import fr.byxis.jeton.JetonsCommandManager;
import fr.byxis.player.discretion.DiscretionManager;
import fr.byxis.player.karma.PlayerKarmaClass;
import fr.byxis.player.primes.PrimeEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardPlayer implements Listener
{

    private final Fireland m_fireland;
    private final DiscretionManager m_discretionManager;

    public ScoreboardPlayer(Fireland _fireland, DiscretionManager _discretionManager)
    {
        this.m_fireland = _fireland;
        this.m_discretionManager = _discretionManager;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e)
    {
        createScoreboard(e.getPlayer());
    }

    public void update(Player _player)
    {

        for (String str : _player.getScoreboard().getEntries())
        {
            _player.getScoreboard().resetScores(str);
        }
        createScoreboard(_player);
    }

    private void createScoreboard(Player _p)
    {
        Scoreboard board = _p.getScoreboard();
        Objective objective = null;
        if (board.getObjective("fireland") == null)
            objective = board.registerNewObjective("fireland", "dummy");
        else
            objective = board.getObjective("fireland");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§f§lStatistiques");

        String state = "";
        if (m_fireland.getCfgm().getPlayerDB().getBoolean("infected." + _p.getUniqueId() + ".state"))
        {
            state += "§2infecté";
        }
        if (m_fireland.getCfgm().getPlayerDB().getDouble("thirst." + _p.getUniqueId()) <= 10)
        {
            if (!state.isEmpty())
            {
                state += "§7, ";
            }
            state += "§3assoiffé";
        }
        if (_p.getFoodLevel() <= 6)
        {
            if (!state.isEmpty())
            {
                state += "§7, ";
            }
            state += "§caffamé";
        }
        if (!state.isEmpty())
        {
            state = "§7sain";
        }

        FileConfiguration karma = m_fireland.getCfgm().getKarmaDB();
        if (!karma.contains(_p.getUniqueId().toString()))
        {
            karma.set(_p.getUniqueId().toString(), 62D);
            m_fireland.getCfgm().saveKarmaDB();
        }
        if (!m_fireland.getHashMapManager().getRangMap().containsKey(_p.getUniqueId()))
        {

            m_fireland.getHashMapManager().getRangMap().put(_p.getUniqueId(),
                    new PlayerKarmaClass(m_fireland.getCfgm().getKarmaDB().getDouble(_p.getUniqueId().toString()),
                            m_fireland.getCfgm().getKarmaDB().getDouble("max." + _p.getUniqueId().toString())));
        }

        double numDiscretion = m_discretionManager.getDiscretion(_p.getUniqueId()).getScore();
        String shotColor = m_discretionManager.getDiscretionColor(_p.getUniqueId());

        JetonsCommandManager jetons = new JetonsCommandManager(m_fireland);

        Score line1 = objective.getScore("§7-");
        Score line2 = objective.getScore("§7- ");
        Score none1 = objective.getScore("");
        Score none2 = objective.getScore(" ");
        Score money = objective.getScore("§8Monnaie : §6" + Math.round(getEco().getBalance(_p)) + "$§r" + "  §8| §b"
                + jetons.getJetonsPlayer(_p.getUniqueId()) + "§r⛁");
        Score bank = objective.getScore("§8Banque : §6"
                + Math.round(m_fireland.getCfgm().getEnderchest().getDouble("bank." + _p.getUniqueId() + ".money")) + "$§r");
        Score infect = objective.getScore("§8État : " + state);
        Score discretion = objective.getScore("§8Discretion : " + shotColor + numDiscretion + "%");
        String prime = "";
        if (PrimeEvent.getConfig().getConfig().contains(_p.getUniqueId().toString()))
        {
            prime = " §c(Recherché)";
        }
        Score rang = objective.getScore("§8Niveau : §7" + getPlayerLevel(_p.getUniqueId()).getStringRank() + prime + " §8(Niv. "
                + getPlayerLevel(_p.getUniqueId()).getLevel() + ")");

        line2.setScore(8);
        none2.setScore(7);
        money.setScore(6);
        bank.setScore(5);
        infect.setScore(4);
        discretion.setScore(3);
        rang.setScore(2);
        none1.setScore(1);
        line1.setScore(0);
        _p.setScoreboard(board);
    }

}
