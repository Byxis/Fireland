package fr.byxis.fireland;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager
{

    private final Fireland plugin = Fireland.getPlugin(Fireland.class);

    private FileConfiguration enderchestcfg;
    private File playerEnderchestFile;

    private FileConfiguration playerDBcfg;
    private File playerDBFile;

    private FileConfiguration factionDBcfg;
    private File factionDBFile;

    private FileConfiguration jetonsDBcfg;
    private File jetonsDBFile;

    private FileConfiguration karmaDBcfg;
    private File karmaDBFile;

    public void setup()
    {
        if (!plugin.getDataFolder().exists())
        {
            plugin.getDataFolder().mkdir();
        }

        // ----------------------------- player enderchest
        // ----------------------------------
        playerEnderchestFile = new File(plugin.getDataFolder(), "playerEnderchest.yml");
        if (!playerEnderchestFile.exists())
        {
            try
            {
                playerEnderchestFile.createNewFile();
                plugin.getLogger().info("playerEnderchest.yml has been created !");
            }
            catch (IOException e)
            {
                System.err.println("/!\\ Could not create playerEnderchest.yml");
            }
        }

        enderchestcfg = YamlConfiguration.loadConfiguration(playerEnderchestFile);
        plugin.getLogger().info("playerEnderchest.yml has been loaded !");
        // ----------------------------- player enderchest
        // ----------------------------------

        // ----------------------------- player DB ----------------------------------
        playerDBFile = new File(plugin.getDataFolder(), "playerdb.yml");
        if (!playerDBFile.exists())
        {
            try
            {
                playerDBFile.createNewFile();
                plugin.getLogger().info("playerdb.yml has been created !");
            }
            catch (IOException e)
            {
                System.err.println("/!\\ Could not create playerdb.yml");
            }
        }

        playerDBcfg = YamlConfiguration.loadConfiguration(playerDBFile);
        plugin.getLogger().info("playerdb.yml has been loaded !");
        // ----------------------------- player DB ----------------------------------

        // ----------------------------- Faction DB ----------------------------------
        factionDBFile = new File(plugin.getDataFolder(), "factiondb.yml");
        if (!factionDBFile.exists())
        {
            try
            {
                factionDBFile.createNewFile();
                plugin.getLogger().info("factiondb.yml has been created !");
            }
            catch (IOException e)
            {
                System.err.println("/!\\ Could not create factiondb.yml");
            }
        }

        factionDBcfg = YamlConfiguration.loadConfiguration(factionDBFile);
        plugin.getLogger().info("factiondb.yml has been loaded !");
        // ----------------------------- faction DB ----------------------------------

        // ----------------------------- jetons DB ----------------------------------
        jetonsDBFile = new File(plugin.getDataFolder(), "jetonsdb.yml");
        if (!jetonsDBFile.exists())
        {
            try
            {
                jetonsDBFile.createNewFile();
                plugin.getLogger().info("jetonsdb.yml has been created !");
            }
            catch (IOException e)
            {
                System.err.println("/!\\ Could not create jetonsdb.yml");
            }
        }

        jetonsDBcfg = YamlConfiguration.loadConfiguration(jetonsDBFile);
        plugin.getLogger().info("jetonsdb.yml has been loaded !");
        // ----------------------------- jetons DB ----------------------------------

        // ----------------------------- karma DB ----------------------------------
        karmaDBFile = new File(plugin.getDataFolder(), "karmadb.yml");
        if (!karmaDBFile.exists())
        {
            try
            {
                karmaDBFile.createNewFile();
                plugin.getLogger().info("karmadb.yml has been created !");
            }
            catch (IOException e)
            {
                System.err.println("/!\\ Could not create karmadb.yml");
            }
        }

        karmaDBcfg = YamlConfiguration.loadConfiguration(karmaDBFile);
        plugin.getLogger().info("karmadb.yml has been loaded !");
        // ----------------------------- karma DB ----------------------------------
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
        catch (IOException e)
        {
            System.err.println("/!\\ Could not save playerEnderchest.yml");
        }
    }

    public void reloadEnderchest()
    {
        enderchestcfg = YamlConfiguration.loadConfiguration(playerEnderchestFile);
        plugin.getLogger().info("playerEnderchest.yml has been reloaded !");
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
        catch (IOException e)
        {
            System.err.println("/!\\ Could not save playerdb.yml");
        }
    }

    public void reloadPlayerDB()
    {
        playerDBcfg = YamlConfiguration.loadConfiguration(playerDBFile);
        plugin.getLogger().info("playerdb.yml has been reloaded !");
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
        catch (IOException e)
        {
            System.err.println("/!\\ Could not save factiondb.yml");
        }
    }

    public void reloadFactionDB()
    {
        factionDBcfg = YamlConfiguration.loadConfiguration(factionDBFile);
        plugin.getLogger().info("factiondb.yml has been reloaded !");
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
        catch (IOException e)
        {
            System.err.println("/!\\ Could not save jetonsdb.yml");
        }
    }

    public void reloadJetonsDB()
    {
        jetonsDBcfg = YamlConfiguration.loadConfiguration(jetonsDBFile);
        plugin.getLogger().info("jetonsdb.yml has been reloaded !");
    }

    public FileConfiguration getKarmaDB()
    {
        return karmaDBcfg;
    }

    public void saveKarmaDB()
    {
        try
        {
            karmaDBcfg.save(karmaDBFile);
        }
        catch (IOException e)
        {
            System.err.println("/!\\ Could not save karmadb.yml");
        }
    }

    public void reloadKarmaDB()
    {
        karmaDBcfg = YamlConfiguration.loadConfiguration(karmaDBFile);
        plugin.getLogger().info("karmadb.yml has been reloaded !");
    }

    public FileConfiguration getJetonsDBcfg()
    {
        return jetonsDBcfg;
    }
}
