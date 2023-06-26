package fr.byxis.faction.essaim;

import fr.byxis.fireland.Fireland;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class EssaimConfigManager {

    private Fireland plugin;
    public EssaimConfigManager(Fireland plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration config;
    public File file;

    public void setup() {
        file = new File(plugin.getDataFolder(), "essaim.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                plugin.getLogger().info("essaim.yml has been created !");
            } catch (IOException e) {
                System.err.println("/!\\ Could not create essaim.yml");
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        plugin.getLogger().info("essaim.yml has been loaded !");
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
            System.err.println("/!\\ Could not save essaim.yml");
        }
    }

    public void reload()
    {
        config = YamlConfiguration.loadConfiguration(file);
        plugin.getLogger().info("essaim.yml has been reloaded !");
    }
}
