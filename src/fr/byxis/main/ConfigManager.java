package fr.byxis.main;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
	
	private Main plugin = Main.getPlugin(Main.class);
	
	public FileConfiguration enderchestcfg;
	public File playerEnderchestFile;
	
	public FileConfiguration playerDBcfg;
	public File playerDBFile;

	public FileConfiguration factionDBcfg;
	public File factionDBFile;

	public FileConfiguration jetonsDBcfg;
	public File jetonsDBFile;
	
	public void setup() 
	{
		if(!plugin.getDataFolder().exists()) 
		{
			plugin.getDataFolder().mkdir();
		}
		
		//----------------------------- player enderchest ----------------------------------
		playerEnderchestFile = new File(plugin.getDataFolder(), "playerEnderchest.yml");
		if(!playerEnderchestFile.exists())
		{
			try 
			{
				playerEnderchestFile.createNewFile();
				System.out.println("playerEnderchest.yml has been created !");
			}
			catch(IOException e)
			{
				System.err.println("/!\\ Could not create playerEnderchest.yml");
			}
		}
		
		enderchestcfg = YamlConfiguration.loadConfiguration(playerEnderchestFile);
		System.out.println("playerEnderchest.yml has been loaded !");
		//----------------------------- player enderchest ----------------------------------
		
		//----------------------------- player DB ----------------------------------
		playerDBFile = new File(plugin.getDataFolder(), "playerdb.yml");
		if(!playerDBFile.exists())
		{
			try 
			{
				playerDBFile.createNewFile();
				System.out.println("playerdb.yml has been created !");
			}
			catch(IOException e)
			{
				System.err.println("/!\\ Could not create playerdb.yml");
			}
		}
		
		playerDBcfg = YamlConfiguration.loadConfiguration(playerDBFile);
		System.out.println("playerdb.yml has been loaded !");
		//----------------------------- player DB ----------------------------------
		
		//----------------------------- Faction DB ----------------------------------
		factionDBFile = new File(plugin.getDataFolder(), "factiondb.yml");
		if(!factionDBFile.exists())
		{
			try 
			{
				factionDBFile.createNewFile();
				System.out.println("factiondb.yml has been created !");
			}
			catch(IOException e)
			{
				System.err.println("/!\\ Could not create factiondb.yml");
			}
		}
		
		factionDBcfg = YamlConfiguration.loadConfiguration(factionDBFile);
		System.out.println("factiondb.yml has been loaded !");
		//----------------------------- faction DB ----------------------------------

		//----------------------------- jetons DB ----------------------------------
		jetonsDBFile = new File(plugin.getDataFolder(), "jetonsdb.yml");
		if(!jetonsDBFile.exists())
		{
			try
			{
				jetonsDBFile.createNewFile();
				System.out.println("jetonsdb.yml has been created !");
			}
			catch(IOException e)
			{
				System.err.println("/!\\ Could not create jetonsdb.yml");
			}
		}

		jetonsDBcfg = YamlConfiguration.loadConfiguration(jetonsDBFile);
		System.out.println("jetonsdb.yml has been loaded !");
		//----------------------------- jetons DB ----------------------------------
	}
	
	public FileConfiguration getEnderchest()
	{
		return enderchestcfg;
	}
	
	public void saveEnderchest()
	{
		try 
		{
			enderchestcfg.save(playerEnderchestFile);
		}
		catch(IOException e)
		{
			System.err.println("/!\\ Could not save playerEnderchest.yml");
		}
	}
	
	public void reloadEnderchest() 
	{
		enderchestcfg = YamlConfiguration.loadConfiguration(playerEnderchestFile);
		System.out.println("playerEnderchest.yml has been reloaded !");
	}
	
	public FileConfiguration getPlayerDB()
	{
		return playerDBcfg;
	}
	
	public void savePlayerDB()
	{
		try 
		{
			playerDBcfg.save(playerDBFile);
		}
		catch(IOException e)
		{
			System.err.println("/!\\ Could not save playerdb.yml");
		}
	}
	
	public void reloadPlayerDB() 
	{
		playerDBcfg = YamlConfiguration.loadConfiguration(playerDBFile);
		System.out.println("playerdb.yml has been reloaded !");
	}
	
	public FileConfiguration getFactionDB()
	{
		return factionDBcfg;
	}
	
	public void saveFactionDB()
	{
		try 
		{
			factionDBcfg.save(factionDBFile);
		}
		catch(IOException e)
		{
			System.err.println("/!\\ Could not save factiondb.yml");
		}
	}
	
	public void reloadFactionDB() 
	{
		factionDBcfg = YamlConfiguration.loadConfiguration(factionDBFile);
		System.out.println("factiondb.yml has been reloaded !");
	}

	public FileConfiguration getJetonsDB()
	{
		return jetonsDBcfg;
	}

	public void saveJetonsDB()
	{
		try
		{
			jetonsDBcfg.save(jetonsDBFile);
		}
		catch(IOException e)
		{
			System.err.println("/!\\ Could not save jetonsdb.yml");
		}
	}

	public void reloadJetonsDB()
	{
		jetonsDBcfg = YamlConfiguration.loadConfiguration(jetonsDBFile);
		System.out.println("jetonsdb.yml has been reloaded !");
	}
}
