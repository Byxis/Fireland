package fr.byxis.event;

import fr.byxis.karma.PlayerKarmaClass;
import fr.byxis.main.Main;
import fr.byxis.karma.karmaManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;

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
		
		String state = "";
		if(main.cfgm.getPlayerDB().getBoolean("infected."+p.getUniqueId()+".state"))
		{
			state += "§2infecté";
		}
		if(main.cfgm.getPlayerDB().getDouble("thirst."+p.getUniqueId()) <= 10)
		{
			if(state != "")
			{
				state += "§7, ";
			}
			state += "§3assoiffé";
		}
		if(p.getFoodLevel() <= 6)
		{
			if(state != "")
			{
				state += "§7, ";
			}
			state += "§caffamé";
		}
		if(state.equals(""))
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
		else if(main.hashMapManager.getDiscretionMap().get(p.getUniqueId()).isUsingLights())
		{
			shotColor = "§e";
		}

		jetonsManager jetons = new jetonsManager(main);
		karmaManager km = new karmaManager(main);
		
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
		Score rang = objective.getScore("§8Rang : "+km.getRang(p.getUniqueId()));
		
		line2.setScore(9);
		none2.setScore(8);
		money.setScore(7);
		bank.setScore(6);
		infect.setScore(5);
		discretion.setScore(4);
		temps.setScore(3);
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
	
	public void updateScoreboard(Player p)
	{
		Objective obj = p.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		
		String state = "§7sain";
		if(main.cfgm.getPlayerDB().getBoolean("infected."+p.getUniqueId()))
		{
			state = "§2infecté";
		}
		
		//double numDiscretion = main.cfgm.getPlayerDB().getDouble("discretion."+p.getUniqueId()+".score");
		double numDiscretion = main.hashMapManager.getDiscretionMap().get(p.getUniqueId()).getScore();
		
		String time = getTimeString(p);
		jetonsManager jetons = new jetonsManager(main);
		karmaManager km = new karmaManager(main);
		Score money = null;
		if (obj != null) {
			money = obj.getScore("§8Monnaie : §6"+Math.round(main.eco.getBalance(p))+"$"+
					" §8 | §b "+jetons.getJetonsPlayer(p.getUniqueId()) + " \u26c1");
		}



		Score bank = obj.getScore("§8Banque : §6"+Math.round(main.cfgm.getEnderchest().getDouble("bank."+p.getUniqueId()+".money"))+"$" );
		Score discretion = obj.getScore("§8Discretion : §7"+numDiscretion+"%");
		Score infecte = obj.getScore("§8État : "+state);
		Score temps = obj.getScore("§8Temps survécu : §7"+time);
		Score rang = obj.getScore("§8Rang : "+km.getRang(p.getUniqueId()));


		money.setScore(7);
		bank.setScore(6);
		infecte.setScore(5);
		discretion.setScore(4);
		temps.setScore(3);
		rang.setScore(2);
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
