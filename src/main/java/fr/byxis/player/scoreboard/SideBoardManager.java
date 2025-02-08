package fr.byxis.player.scoreboard;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.HashMapManager;
import fr.byxis.jeton.JetonManager;
import fr.byxis.player.primes.PrimeEvent;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import static fr.byxis.player.level.LevelStorage.getPlayerLevel;

public class SideBoardManager {

    private final Fireland m_main;

    public SideBoardManager(Fireland _main)
    {
        m_main = _main;
    }

    public void updateSideBoard(Player _p, Scoreboard _mainBoard, Scoreboard _playerBoard)
    {
        for (Team team : _mainBoard.getTeams()) {
            Team playerTeam = _playerBoard.registerNewTeam(team.getName());
            playerTeam.setDisplayName(team.getDisplayName());
            playerTeam.setPrefix(team.getPrefix());
            playerTeam.setSuffix(team.getSuffix());
            playerTeam.setColor(team.getColor());
            playerTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, team.getOption(Team.Option.NAME_TAG_VISIBILITY));
            for (String entry : team.getEntries()) {
                playerTeam.addEntry(entry);
            }
        }

        Objective obj;

        if (_playerBoard.getObjective("fireland") == null)
            obj = _playerBoard.registerNewObjective("fireland", "dummy");
        else
            obj = _playerBoard.getObjective("fireland");

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("§f§lStatistiques");

        // Remplacer 'getMoney(player)' et 'getTokens(player)' par les méthodes pour obtenir l'argent et les jetons du joueur
        Score line1 = obj.getScore("§7-");
        Score line2 = obj.getScore("§7- ");
        Score none1 = obj.getScore("");
        Score none2 = obj.getScore(" ");
        Score money = obj.getScore(getMoneyText(_p));
        Score bank = obj.getScore(getBankText(_p));
        Score infect = obj.getScore(getStateText(_p));
        Score discretion = obj.getScore(getDiscretionText(_p));
        Score rang = obj.getScore(getRankText(_p));

        line2.setScore(8);
        none2.setScore(7);
        money.setScore(6);
        bank.setScore(5);
        infect.setScore(4);
        discretion.setScore(3);
        rang.setScore(2);
        none1.setScore(1);
        line1.setScore(0);

        _p.setScoreboard(_playerBoard);

       /*

        Score line1 = objective.getScore("§7-");
        Score line2 = objective.getScore("§7- ");
        Score none1 = objective.getScore("");
        Score none2 = objective.getScore(" ");
        Score money = objective.getScore(getMoneyText(p));
        Score bank = objective.getScore(getBankText(p));
        Score infect = objective.getScore(getStateText(p));
        Score discretion = objective.getScore(getDiscretionText(p));
        Score rang = objective.getScore(getRankText(p));

        line2.setScore(8);
        none2.setScore(7);
        money.setScore(6);
        bank.setScore(5);
        infect.setScore(4);
        discretion.setScore(3);
        rang.setScore(2);
        none1.setScore(1);
        line1.setScore(0);
        setPlayerScoreboard(p, board);
        p.setScoreboard(board);*/
    }

    private String getMoneyText(Player p)
    {
        return "§8Monnaie : §6 " + Math.round(Fireland.getEco().getBalance(p)) + "§r$" +
                "  §8| §b " + JetonManager.getJetonsPlayer(p.getUniqueId()) + "§r\u26c1";
    }

    private String getBankText(Player p)
    {
        return "§8Banque : §6 " + Math.round(m_main.getCfgm().getEnderchest().getDouble("bank." + p.getUniqueId() + ".money")) + "§r$";
    }

    private String getStateText(Player p)
    {
        String state = "";
        if (m_main.getCfgm().getPlayerDB().getBoolean("infected." + p.getUniqueId() + ".state"))
        {
            state += "§2infecté";
        }
        if (m_main.getCfgm().getPlayerDB().getDouble("thirst." + p.getUniqueId()) <= 10)
        {
            if (!state.isEmpty())
            {
                state += "§7, ";
            }
            state += "§3assoiffé";
        }
        if (p.getFoodLevel() <= 6)
        {
            if (!state.isEmpty())
            {
                state += "§7, ";
            }
            state += "§caffamé";
        }
        if (state.isEmpty())
        {
            state = "§7sain";
        }
        return "§8État : " + state;
    }

    private String getDiscretionText(Player p)
    {
        if (!HashMapManager.getDiscretionMap().containsKey(p.getUniqueId()))
        {
            m_main.getHashMapManager().addDiscretionMap(p.getUniqueId());
        }

        double numDiscretion = HashMapManager.getDiscretionMap().get(p.getUniqueId()).getScore();
        String shotColor = "§7";

        String prime = "";
        if (PrimeEvent.getConfig().getConfig().contains(p.getUniqueId().toString()))
        {
            prime = " §c(Recherché)";
        }

        if (HashMapManager.getDiscretionMap().get(p.getUniqueId()).isShooting())
        {
            shotColor = "§4";
        }
        else if (HashMapManager.getDiscretionMap().get(p.getUniqueId()).isUsingCamo())
        {
            shotColor = "§2";
        }
        else if (HashMapManager.getDiscretionMap().get(p.getUniqueId()).isUsingLights())
        {
            shotColor = "§e";
        }

        return "§8Discretion : " + shotColor + numDiscretion + "% " + prime;
    }

    private String getRankText(Player p)
    {
        return "§8Niveau : §7 " + getPlayerLevel(p.getUniqueId()).getStringRank() + " §8(Niv. " + getPlayerLevel(p.getUniqueId()).getLevel() + ")";
    }
}
