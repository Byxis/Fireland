package fr.byxis.player.rank;

import fr.byxis.fireland.Fireland;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class RankConfig
{

    private final Fireland plugin;
    public RankConfig(Fireland _plugin)
    {
        this.plugin = _plugin;
        this.name = "rank-message";
        setup();
    }

    private FileConfiguration config;
    private File file;
    private final String name;

    public void setup()
    {
        file = new File(plugin.getDataFolder(), name + ".yml");
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
                plugin.getLogger().info(name + ".yml has been created !");
            }
            catch (IOException e)
            {
                System.err.println("/!\\ Could not create " + name + ".yml");
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        plugin.getLogger().info(name + ".yml has been loaded !");
    }

    public FileConfiguration getConfig()
    {
        return config;
    }

    public void save()
    {
        try
        {
            config.save(file);
        }
        catch (IOException e)
        {
            System.err.println("/!\\ Could not save " + name + ".yml");
        }
    }

    public void reload()
    {
        config = YamlConfiguration.loadConfiguration(file);
        plugin.getLogger().info(name + ".yml has been reloaded !");
    }
}
