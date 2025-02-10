package fr.byxis.faction.essaim;

import fr.byxis.fireland.Fireland;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class EssaimConfigManager {

    private final Fireland main;
    public EssaimConfigManager(Fireland _main) {
        this.main = _main;
    }

    private FileConfiguration config;
    private File file;

    public void setup() {
        file = new File(main.getDataFolder(), "essaim.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                main.getLogger().info("essaim.yml has been created !");
            } catch (IOException e) {
                System.err.println("/!\\ Could not create essaim.yml");
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        main.getLogger().info("essaim.yml has been loaded !");
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
            System.err.println("/!\\ Could not save essaim.yml");
        }
    }

    public void reload()
    {
        config = YamlConfiguration.loadConfiguration(file);
        main.getLogger().info("essaim.yml has been reloaded !");
    }
}
