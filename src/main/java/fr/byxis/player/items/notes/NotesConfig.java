package fr.byxis.player.items.notes;

import fr.byxis.fireland.Fireland;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class NotesConfig {

    private final Fireland plugin;
    public NotesConfig(Fireland _plugin, boolean log) {
        this.plugin = _plugin;
        this.name = "note";
        setup(log);
    }

    private FileConfiguration config;
    private File file;
    private final String name;

    public void setup(boolean log) {
        file = new File(plugin.getDataFolder(), name + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                if (log) plugin.getLogger().info(name + ".yml has been created !");
            } catch (IOException e) {
                if (log) System.err.println("/!\\ Could not create " + name + ".yml");
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        if (log) plugin.getLogger().info(name + ".yml has been loaded !");
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

    public FileConfiguration reload()
    {
        config = YamlConfiguration.loadConfiguration(file);
        plugin.getLogger().info(name + ".yml has been reloaded !");
        return config;
    }
}
