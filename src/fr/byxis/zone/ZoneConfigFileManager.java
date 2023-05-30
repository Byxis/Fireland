package fr.byxis.zone;

import fr.byxis.main.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ZoneConfigFileManager {

    private final Main plugin;

    public ZoneConfigFileManager(Main main)
    {
        this.plugin = main;
    }

    public FileConfiguration config;
    public File file;

    public void setup() {
        file = new File(plugin.getDataFolder(), "zone-capture.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                plugin.getLogger().info("zone-capture.yml has been created !");
            } catch (IOException e) {
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

    public void save()
    {
        try
        {
            config.save(file);
        }
        catch(IOException e)
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
