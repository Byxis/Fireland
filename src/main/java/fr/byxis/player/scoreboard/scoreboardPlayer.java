package fr.byxis.player.scoreboard;

import fr.byxis.jeton.jetonsCommandManager;
import fr.byxis.player.karma.PlayerKarmaClass;
import fr.byxis.fireland.Fireland;
import fr.byxis.player.primes.PrimeEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;

import java.util.UUID;

import static fr.byxis.fireland.utilities.InGameUtilities.debugp;
import static fr.byxis.player.level.LevelStorage.getPlayerLevel;

public class scoreboardPlayer implements Listener {

	private final Fireland main;
	
	public scoreboardPlayer(Fireland main) {
		this.main = main;
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent e)
	{
		createScoreboard(e.getPlayer());
	}

	private void createScoreboard(Player p)
	{
		Scoreboard board = p.getScoreboard();
		Objective objective = null;
		if(board.getObjective("fireland") == null)
        	objective = board.registerNewObjective("fireland", "dummy");
		else
			objective = board.getObjective("fireland");

		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("§f§lStatistiques");
		
		String state = "";
		if(main.cfgm.getPlayerDB().getBoolean("infected."+p.getUniqueId()+".state"))
		{
			state += "§2infecté";
		}
		if(main.cfgm.getPlayerDB().getDouble("thirst."+p.getUniqueId()) <= 10)
		{
			if(!state.isEmpty())
			{
				state += "§7, ";
			}
			state += "§3assoiffé";
		}
		if(p.getFoodLevel() <= 6)
		{
			if(!state.isEmpty())
			{
				state += "§7, ";
			}
			state += "§caffamé";
		}
		if(!state.isEmpty())
		{
			state = "§7sain";
		}
		if(!main.hashMapManager.getDiscretionMap().containsKey(p.getUniqueId()))
		{
			main.hashMapManager.addDiscretionMap(p.getUniqueId());
		}

		FileConfiguration karma = main.cfgm.getKarmaDB();
		if(!karma.contains(p.getUniqueId().toString()))
		{
			karma.set(p.getUniqueId().toString(), 62D);
			main.cfgm.saveKarmaDB();
		}
		if(!main.hashMapManager.getRangMap().containsKey(p.getUniqueId()))
		{

			main.hashMapManager.getRangMap().put(p.getUniqueId(), new PlayerKarmaClass(main.cfgm.getKarmaDB().getDouble(p.getUniqueId().toString()), main.cfgm.getKarmaDB().getDouble("max."+p.getUniqueId().toString())));
		}

		//double numDiscretion = main.cfgm.getPlayerDB().getDouble("discretion."+p.getUniqueId()+".score");
		double numDiscretion = main.hashMapManager.getDiscretionMap().get(p.getUniqueId()).getScore();
		String shotColor = "§7";
		if(main.hashMapManager.getDiscretionMap().get(p.getUniqueId()).isShooting())
		{
			shotColor = "§4";
		}
		else if(main.hashMapManager.getDiscretionMap().get(p.getUniqueId()).isUsingCamo())
		{
			shotColor = "§2";
		}
		else if(main.hashMapManager.getDiscretionMap().get(p.getUniqueId()).isUsingLights())
		{
			shotColor = "§e";
		}

		jetonsCommandManager jetons = new jetonsCommandManager(main);
		
		Score line1 = objective.getScore("§7-");
		Score line2 = objective.getScore("§7- ");
		Score none1 = objective.getScore("");
		Score none2 = objective.getScore(" ");
		Score money = objective.getScore("§8Monnaie : §6"+Math.round(main.eco.getBalance(p))+"§r$"+
				"  §8| §b "+jetons.getJetonsPlayer(p.getUniqueId()) + "§r\u26c1");
		Score bank = objective.getScore("§8Banque : §6"+Math.round(main.cfgm.getEnderchest().getDouble("bank."+p.getUniqueId()+".money"))+"§r$");
		Score infect = objective.getScore("§8État : "+state);
		Score discretion = objective.getScore("§8Discretion : "+shotColor+numDiscretion+"%");
		String prime = "";
		if(PrimeEvent.config.getConfig().contains(p.getUniqueId().toString()))
		{
			prime = " §c(Recherché)";
		}
		Score rang = objective.getScore("§8Niveau : §7"+getPlayerLevel(p.getUniqueId()).getStringRank() +prime+" §8(Niv. "+getPlayerLevel(p.getUniqueId()).getLevel()+")");

		line2.setScore(8);
		none2.setScore(7);
		money.setScore(6);
		bank.setScore(5);
		infect.setScore(4);
		discretion.setScore(3);
		rang.setScore(2);
		none1.setScore(1);
		line1.setScore(0);
		p.setScoreboard(board);
	}
	
	public void update(Player player) {
		
        for (String str : player.getScoreboard().getEntries())
        {
        	player.getScoreboard().resetScores(str);
        }
        createScoreboard(player);
    }
	
	public static String getTimeString(Fireland main, UUID p)
	{
		double time = main.cfgm.getPlayerDB().getDouble("playtime."+p);
		int iTime = (int) time;
		String sTime = "s";
		
		if(time >= 86400)
		{
			time = ((time/60)/60)/24;
			iTime = (int) time;
			sTime = "j";
		}
		else if(time >= 3600)
		{
			time = (time/60)/60;
			iTime = (int) time;
			sTime = "h";
		}
		else if(time >= 60)
		{
			time = time/60;
			iTime = (int) time;
			sTime = "min";
		}
		return iTime+sTime;
	}

}
