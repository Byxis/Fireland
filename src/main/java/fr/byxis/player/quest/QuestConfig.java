package fr.byxis.player.quest;

import fr.byxis.fireland.Fireland;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class QuestConfig {

    private Fireland plugin;
    public QuestConfig(Fireland plugin) {
        this.plugin = plugin;
        this.name = "quest";
        setup();
    }

    public FileConfiguration config;
    public File file;
    private String name;

    public void setup() {
        file = new File(plugin.getDataFolder(), name + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                plugin.getLogger().info(name + ".yml has been created !");
            } catch (IOException e) {
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
        catch(IOException e)
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
