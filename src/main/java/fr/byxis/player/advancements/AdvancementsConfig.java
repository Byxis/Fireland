package fr.byxis.player.advancements;

import fr.byxis.fireland.Fireland;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class AdvancementsConfig {

    private final Fireland main;
    public AdvancementsConfig(Fireland _main) {
        this.main = _main;
        this.name = "success";
        setup();
    }

    private FileConfiguration config;
    private File file;
    private final String name;

    public void setup() {
        file = new File(main.getDataFolder(), name + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                main.getLogger().info(name + ".yml has been created !");
            } catch (IOException e) {
                System.err.println("/!\\ Could not create " + name + ".yml");
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        main.getLogger().info(name + ".yml has been loaded !");
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
        main.getLogger().info(name + ".yml has been reloaded !");
    }
}
