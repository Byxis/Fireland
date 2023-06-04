package fr.byxis.essaim;

import fr.byxis.essaim.essaimClass.Spawner;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.zone.zoneclass.FactionCapturingClass;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.MythicEntity;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EssaimManager {

    private static Fireland main;
    public static EssaimConfigManager configManager;
    public static EssaimFunctions essaimFunctions;

    public HashMap<String, List<ActiveMob>> activeSpawners;
    public HashMap<String, HashMap<String, Spawner>> existingEssaims;

    public EssaimManager(Fireland main)
    {
        EssaimManager.main = main;
        configManager = new EssaimConfigManager(main);
        configManager.setup();
        essaimFunctions = new EssaimFunctions(main, configManager);
        existingEssaims = new HashMap<>();
        activeSpawners = new HashMap<>();
        SetupExistingSpawners();
        main.getServer().getPluginManager().registerEvents(new EssaimEventHandler(main), main);
    }

    public boolean EnableSpawner(Spawner spawner)
    {
        if(activeSpawners.containsKey(spawner.getName()))
        {
            return false;
        }
        List<ActiveMob> mobs = new ArrayList<>();
        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(spawner.getMob()).orElse(null);
        for(int i = 0; i < spawner.getAmount(); i++)
        {
            ActiveMob activeMob = mob.spawn(BukkitAdapter.adapt(spawner.getLoc()),1);
            mobs.add(activeMob);
        }
        activeSpawners.put(spawner.getName(), mobs);
        return true;
    }

    private void SetupExistingSpawners()
    {
        for (String essaim : configManager.getConfig().getConfigurationSection("").getKeys(false))
        {
            HashMap<String, Spawner> existingSpawnerInEssaim = new HashMap<>();
            for (String spawner : configManager.getConfig().getConfigurationSection(essaim+".spawners").getKeys(false))
            {
                Location loc = new Location(Bukkit.getWorld("essaim"),
                        configManager.getConfig().getInt(essaim+".spawners."+spawner+".position.x"),
                        configManager.getConfig().getInt(essaim+".spawners."+spawner+".position.y"),
                        configManager.getConfig().getInt(essaim+".spawners."+spawner+".position.z")
                );
                String type = configManager.getConfig().getString(essaim+".spawners."+spawner+".type");
                Integer amount = configManager.getConfig().getInt(essaim+".spawners."+spawner+".amount");
                String command = configManager.getConfig().getString(essaim+".spawners."+spawner+".command");
                existingSpawnerInEssaim.put(spawner, new Spawner(spawner, essaim, loc, type, amount, command));
            }
            existingEssaims.put(essaim, existingSpawnerInEssaim);
        }
    }
}
