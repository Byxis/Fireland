package fr.byxis.faction.zone;

import fr.byxis.fireland.Fireland;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ZoneConfigFileManager
{

    private final Fireland plugin;

    public ZoneConfigFileManager(Fireland main)
    {
        this.plugin = main;
    }

    private FileConfiguration config;
    private File file;

    public void setup()
    {
        file = new File(plugin.getDataFolder(), "zone-capture.yml");
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
                plugin.getLogger().info("zone-capture.yml has been created !");
            }
            catch (IOException e)
            {
                System.err.println("/!\\ Could not create zone-capture.yml");
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        plugin.getLogger().info("zone-capture.yml has been loaded !");
    }

    public FileConfiguration getConfig()
    {
        return config;
    }
    public void notSafeSetup()
    {
        this.file = new File(plugin.getDataFolder(), "zone-capture.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void save()
    {
        try
        {
            config.save(file);
        }
        catch (IOException e)
        {
            System.err.println("/!\\ Could not save zone-capture.yml");
        }
    }

    public void reload()
    {
        config = YamlConfiguration.loadConfiguration(file);
        plugin.getLogger().info("zone-capture.yml has been reloaded !");
    }
}
