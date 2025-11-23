package fr.byxis.player.scoreboard;

import static fr.byxis.fireland.utilities.InGameUtilities.getStringColor;
import static fr.byxis.player.scoreboard.PlayerScoreboardManager.getMainScoreboard;

import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.faction.faction.events.FactionBuyPerkEvent;
import fr.byxis.faction.faction.events.PlayerJoinFactionEvent;
import fr.byxis.faction.faction.events.PlayerLeaveFactionEvent;
import fr.byxis.fireland.Fireland;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;

public class NameTagManager implements Listener
{

    private final HashMap<String, Team> m_factionTeams;
    private final FactionFunctions m_ff;
    public NameTagManager(Fireland fireland)
    {
        m_factionTeams = new HashMap<>();
        m_ff = new FactionFunctions(fireland, null);
        m_factionTeams.put("", createFactionTeam("server"));

        for (Player p : Bukkit.getOnlinePlayers())
        {
            actualizeTeam(p);
        }
    }

    public void actualizeTeam(Player p)
    {
        String factionName = m_ff.playerFactionName(p);
        if (!m_ff.hasPerk(factionName, "show_nickname"))
        {
            factionName = "";
        }
        for (Team team : m_factionTeams.values())
        {
            if (team.hasPlayer(p))
            {
                team.removePlayer(p);
            }
        }
        if (!m_factionTeams.containsKey(factionName))
        {
            m_factionTeams.put(factionName, createFactionTeam(factionName));
        }
        m_factionTeams.get(factionName).addPlayer(p);
        p.setScoreboard(getMainScoreboard());
    }

    private Team createFactionTeam(String name)
    {
        Team team = null;
        if (name.isEmpty() || name.equals("server"))
        {
            if (getMainScoreboard().getTeam("server") == null)
            {
                team = getMainScoreboard().registerNewTeam("server");
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            }
            else
            {
                team = getMainScoreboard().getTeam("server");
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            }
            return team;
        }
        if (getMainScoreboard().getTeam(name) == null)
        {
            team = getMainScoreboard().registerNewTeam(name);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.setColor(getStringColor(m_ff.getFactionInfo(name).getColorcode()));
        }
        else
        {
            team = getMainScoreboard().getTeam(name);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.setColor(getStringColor(m_ff.getFactionInfo(name).getColorcode()));
        }
        return team;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e)
    {
        actualizeTeam(e.getPlayer());
    }

    @EventHandler
    public void playerLeaveFaction(PlayerLeaveFactionEvent e)
    {
        if (Bukkit.getPlayer(e.getPlayerUuid()) != null)
            actualizeTeam(Bukkit.getPlayer(e.getPlayerUuid()));
    }

    @EventHandler
    public void playerJoinFaction(PlayerJoinFactionEvent e)
    {
        actualizeTeam(e.getPlayer());
    }

    @EventHandler
    public void factionBuyPerk(FactionBuyPerkEvent e)
    {
        if (e.getPerk().equalsIgnoreCase("show_nickname"))
        {
            for (Player p : Bukkit.getOnlinePlayers())
            {
                actualizeTeam(p);
            }
        }
    }

}
