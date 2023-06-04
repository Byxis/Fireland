package fr.byxis.essaim;

import fr.byxis.fireland.Fireland;
import org.bukkit.Location;

public class EssaimFunctions {

    private static Fireland main;
    private static EssaimConfigManager configManager;

    public EssaimFunctions(Fireland main, EssaimConfigManager configManager) {
        EssaimFunctions.main = main;
        EssaimFunctions.configManager = configManager;
    }

    public static void createNewSpawner(String essaim, String name, String mob, int amount, String command, Location location)
    {
        configManager.getConfig().set(essaim+".spawners."+name+".type",mob);
        configManager.getConfig().set(essaim+".spawners."+name+".amount",amount);
        configManager.getConfig().set(essaim+".spawners."+name+".command",command);
        configManager.getConfig().set(essaim+".spawners."+name+".position.x",location.getX());
        configManager.getConfig().set(essaim+".spawners."+name+".position.y",location.getY());
        configManager.getConfig().set(essaim+".spawners."+name+".position.z",location.getZ());
        configManager.save();
    }
    public static void removeSpawner(String essaim, String name)
    {
        configManager.getConfig().set(essaim+".spawners."+name, null);
        configManager.save();
    }


}
