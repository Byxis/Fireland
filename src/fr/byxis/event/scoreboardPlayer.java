package fr.byxis.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import fr.byxis.main.Main;

public class scoreboardPlayer implements Listener {

	private final Main main;
	
	public scoreboardPlayer(Main main) {
		this.main = main;
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent e)
	{
		createScoreboard(e.getPlayer());
	}
	
	@SuppressWarnings("deprecation")
	private void createScoreboard(Player p)
	{
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = null;
		if (manager != null) {
			board = manager.getNewScoreboard();
		}
		Objective objective = board.registerNewObjective("fireland", "dummy");
		
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("§f§lStatistiques");
		
		String state;
		if(main.cfgm.getPlayerDB().getBoolean("infected."+p.getUniqueId()+".state"))
		{
			state = "§2infecté";
		}
		else
		{
			state = "§7sain";
		}
		
		double numDiscretion = main.cfgm.getPlayerDB().getDouble("discretion."+p.getUniqueId()+".score");
		
		if(numDiscretion < 0)
		{
			numDiscretion = 0;
		}
		String shotColor = "§7";
		if(main.cfgm.getPlayerDB().getBoolean("discretion." + p.getUniqueId() + ".shot"))
		{
			shotColor = "§4";
		}

		jetonsManager jetons = new jetonsManager(main);
		
		String time = getTimeString(p);
		
		Score line1 = objective.getScore("§7-");
		Score line2 = objective.getScore("§7- ");
		Score none1 = objective.getScore("");
		Score none2 = objective.getScore(" ");
		Score money = objective.getScore("§8Monnaie : §6"+Math.round(main.eco.getBalance(p))+"$"+
				"  §8| §b "+jetons.getJetonsPlayer(p.getUniqueId()) + " \u26c1");
		Score bank = objective.getScore("§8Banque : §6"+Math.round(main.cfgm.getEnderchest().getDouble("bank."+p.getUniqueId()+".money"))+"$");
		Score infect = objective.getScore("§8État : "+state);
		Score discretion = objective.getScore("§8Discretion : "+shotColor+numDiscretion+"%");
		Score temps = objective.getScore("§8Temps survécu : §7"+time);
		
		line2.setScore(8);
		none2.setScore(7);
		money.setScore(6);
		bank.setScore(5);
		infect.setScore(4);
		discretion.setScore(3);
		temps.setScore(2);
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
	
	public void updateScoreboard(Player p)
	{
		Objective obj = p.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		
		String state = "§7sain";
		if(main.cfgm.getPlayerDB().getBoolean("infected."+p.getUniqueId()))
		{
			state = "§2infecté";
		}
		
		double numDiscretion = main.cfgm.getPlayerDB().getDouble("discretion."+p.getUniqueId()+".score");
		
		if(numDiscretion < 0 || main.cfgm.getPlayerDB().getBoolean("discretion."+p.getUniqueId()+"shot"))
		{
			numDiscretion = 0;
		}
		
		String time = getTimeString(p);
		jetonsManager jetons = new jetonsManager(main);
		Score money = null;
		if (obj != null) {
			money = obj.getScore("§8Monnaie : §6"+Math.round(main.eco.getBalance(p))+"$"+
					" §8 | §b "+jetons.getJetonsPlayer(p.getUniqueId()) + " \u26c1");
		}



		Score bank = obj.getScore("§8Banque : §6"+Math.round(main.cfgm.getEnderchest().getDouble("bank."+p.getUniqueId()+".money"))+"$" );
		Score discretion = obj.getScore("§8Discretion : §7"+numDiscretion+"%");
		Score infecte = obj.getScore("§8État : "+state);
		Score temps = obj.getScore("§8Temps survécu : §7"+time);

		money.setScore(6);
		bank.setScore(5);
		infecte.setScore(4);
		discretion.setScore(3);
		temps.setScore(2);
	}
	
	private String getTimeString(Player p)
	{
		double time = main.cfgm.getPlayerDB().getDouble("playtime."+p.getUniqueId());
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
