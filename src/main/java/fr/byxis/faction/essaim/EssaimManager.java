package fr.byxis.faction.essaim;

import fr.byxis.faction.essaim.essaimClass.ActiveMobSpawning;
import fr.byxis.faction.essaim.essaimClass.EssaimClass;
import fr.byxis.faction.essaim.essaimClass.EssaimGroup;
import fr.byxis.faction.essaim.essaimClass.Spawner;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;

import static fr.byxis.faction.essaim.EssaimFunctions.isEssaimOpened;
import static fr.byxis.fireland.utilities.InGameUtilities.debug;

public class EssaimManager {

    private static Fireland main;

    private static EssaimConfigManager configManager;
    private static EssaimFunctions essaimFunctions;
    private final HashMap<String, ActiveMobSpawning> activeSpawners;
    private final HashMap<String, HashMap<String, Spawner>> existingEssaims;
    private static HashMap<String, EssaimClass> activeEssaims;
    private static HashMap<String, EssaimGroup> groups;

    public EssaimManager(Fireland _main)
    {
        EssaimManager.main = _main;
        configManager = new EssaimConfigManager(_main);
        configManager.setup();
        essaimFunctions = new EssaimFunctions(_main, configManager);
        existingEssaims = new HashMap<>();
        activeSpawners = new HashMap<>();
        activeEssaims = new HashMap<>();
        groups = new HashMap<>();
        setupExistingSpawners();
        _main.getServer().getPluginManager().registerEvents(new EssaimEventHandler(_main), _main);
        loop();
    }

    public static boolean disableEssaim(String name)
    {
        if (groups.containsKey(name))
        {
            activeEssaims.get(name).setClosed(true);
            configManager.getConfig().set(name + ".closed", true);
            configManager.save();
        }
        activeEssaims.remove(name);

        return true;
    }

    public boolean enableSpawner(Spawner spawner)
    {
        if (activeSpawners.containsKey(spawner.getName()))
        {
            return false;
        }
        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(spawner.getMob()).orElse(null);
        ActiveMobSpawning activeMobSpawning = new ActiveMobSpawning(spawner.getEssaim(), getEntityNumber(spawner) + 1);
        activeSpawners.put(spawner.getName(), activeMobSpawning);
        new BukkitRunnable()
        {
            private int entityNumber = getEntityNumber(spawner);

            @Override
            public void run() {
                ActiveMob activeMob = mob.spawn(BukkitAdapter.adapt(spawner.getLoc()), 1);
                entityNumber--;
                activeSpawners.get(spawner.getName()).addActiveMob(activeMob);
                if (!activeMob.validateLoadedMob())
                {
                    activeMob.setDead();
                    activeSpawners.get(spawner.getName()).removeActiveMob(activeMob);
                }
                if (entityNumber <= 0)
                {
                    cancel();
                }
            }
        }.runTaskTimer(main, Math.round(spawner.getActivationDelay() * 20), (long) (spawner.getSpawnDelay() * 20));
        return true;
    }

    private int getEntityNumber(Spawner spawner)
    {

        if (spawner.isAffectedByDifficulty() && EssaimManager.getGroups().containsKey(spawner.getEssaim()))
        {
            return Math.round(spawner.getAmount() * EssaimManager.getGroups().get(spawner.getEssaim()).getAdaptativeDifficulty());
        }
        else
        {
            return spawner.getAmount();
        }
    }

    public boolean enableEssaim(String name)
    {
        if (activeEssaims.containsKey(name))
        {
            activeEssaims.get(name).setClosed(false);
            return false;
        }
        try
        {
            EssaimClass essaimClass = new EssaimClass(name, configManager);
            configManager.getConfig().set(name + ".closed", false);
            configManager.save();
            activeEssaims.put(name, essaimClass);
        } catch (Exception e) {
            main.getLogger().severe("Un problème est survenu lors de l'activation de l'essaim " + name);
            return false;
        }


        return true;
    }

    private void setupExistingSpawners()
    {
        for (String essaim : configManager.getConfig().getConfigurationSection("").getKeys(false))
        {
            HashMap<String, Spawner> existingSpawnerInEssaim = new HashMap<>();
            if (configManager.getConfig().get(essaim + ".spawners") != null)
            {
                for (String spawner : configManager.getConfig().getConfigurationSection(essaim + ".spawners").getKeys(false))
                {
                    Location loc = new Location(Bukkit.getWorld("essaim"),
                            configManager.getConfig().getInt(essaim + ".spawners." + spawner + ".position.x"),
                            configManager.getConfig().getInt(essaim + ".spawners." + spawner + ".position.y"),
                            configManager.getConfig().getInt(essaim + ".spawners." + spawner + ".position.z")
                    );
                    String type = configManager.getConfig().getString(essaim + ".spawners." + spawner + ".type");
                    int amount = configManager.getConfig().getInt(essaim + ".spawners." + spawner + ".amount");
                    String command = configManager.getConfig().getString(essaim + ".spawners." + spawner + ".command");
                    double activationDelay = configManager.getConfig().getDouble(essaim + ".spawners." + spawner + ".activation-delay");
                    double spawnDelay = configManager.getConfig().getDouble(essaim + ".spawners." + spawner + ".spawn-delay");
                    Boolean isAffectedByDifficulty = configManager.getConfig().getBoolean(essaim + ".spawners." + spawner + ".affected-by-difficulty");
                    existingSpawnerInEssaim.put(spawner, new Spawner(spawner, essaim, loc, type, amount, activationDelay, spawnDelay, command, isAffectedByDifficulty));
                }
                existingEssaims.put(essaim, existingSpawnerInEssaim);
                if (!configManager.getConfig().getBoolean(essaim + ".closed"))
                {
                    enableEssaim(essaim);
                }
            }
        }
    }

    public boolean resetEssaim(String arg) {
        EssaimClass essaimClass = activeEssaims.get(arg);
        EssaimFunctions.setBlock(essaimClass.getReset().blockX(),
                essaimClass.getReset().blockY(),
                essaimClass.getReset().blockZ(),
                Material.REDSTONE_BLOCK
        );
        for (String spawner : existingEssaims.get(essaimClass.getName()).keySet())
        {
            if (activeSpawners.get(essaimClass.getName()) != null)
            {
                for (ActiveMob mob : activeSpawners.get(essaimClass.getName()).getActiveMobs())
                {
                    mob.setDead();
                }
                activeSpawners.remove(spawner);
            }

        }
        return true;
    }

    public void setSolo(String essaim) {
        if (!activeEssaims.containsKey(essaim) || groups.get(essaim).getMembers().size() != 1)
        {
            return;
        }
        EssaimClass essaimClass = activeEssaims.get(essaim);
        EssaimFunctions.setBlock(essaimClass.getSolo().blockX(),
                essaimClass.getSolo().blockY(),
                essaimClass.getSolo().blockZ(),
                Material.REDSTONE_BLOCK
        );
    }

    public void setDifficulty(String essaim, int difficulty) {
        if (!activeEssaims.containsKey(essaim) || groups.get(essaim).getMembers().size() != 1)
        {
            return;
        }
        EssaimClass essaimClass = activeEssaims.get(essaim);
        switch (difficulty)
        {
            case 1 ->
            {
                if (essaimClass.getDifficulty1() == null)
                    return;
                EssaimFunctions.setBlock(essaimClass.getDifficulty1().blockX(),
                        essaimClass.getDifficulty1().blockY(),
                        essaimClass.getDifficulty1().blockZ(),
                        Material.REDSTONE_BLOCK
                );
            }
            case 2 ->
            {
                if (essaimClass.getDifficulty2() == null)
                    return;
                EssaimFunctions.setBlock(essaimClass.getDifficulty2().blockX(),
                        essaimClass.getDifficulty2().blockY(),
                        essaimClass.getDifficulty2().blockZ(),
                        Material.REDSTONE_BLOCK
                );
            }
            case 3 ->
            {
                if (essaimClass.getDifficulty3() == null)
                    return;
                EssaimFunctions.setBlock(essaimClass.getDifficulty3().blockX(),
                        essaimClass.getDifficulty3().blockY(),
                        essaimClass.getDifficulty3().blockZ(),
                        Material.REDSTONE_BLOCK
                );
            }
        }

    }

    public void loop()
    {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isNewHour())
                {
                    debug(7, "Tentative d'ouverture");
                    for (String essaim : configManager.getConfig().getConfigurationSection("").getKeys(false))
                    {
                        debug(7, "Tentative d'ouverture de l'essaim " + essaim);
                        LocalDateTime today = LocalDateTime.now();
                        if (isEssaimOpened(essaim))
                        {
                            debug(7, "Essaim déjà ouvert " + essaim);
                            continue;
                        }
                        if (configManager.getConfig().getInt(essaim + ".hour") == today.getHour() &&
                                configManager.getConfig().getInt(essaim + ".day") == today.getDayOfWeek().getValue())
                        {

                            debug(7, "Ouverture de l'essaim " + essaim);
                            enableEssaim(essaim);
                            for (Player p : Bukkit.getOnlinePlayers())
                            {
                                InGameUtilities.sendPlayerInformation(p, "L'essaim " + activeEssaims.get(essaim).getFormattedName() + " est désormais ouvert. Allez vite le pacifier avant que la menace se répande !");
                            }
                        }
                        debug(7, "L'essaim " + essaim + " est : " + activeEssaims.containsKey(essaim));
                    }
                }
                for (EssaimClass essaimClass : activeEssaims.values())
                {
                    if (essaimClass.isFinished() && essaimClass.shouldClose())
                    {
                        EssaimFunctions.leaveFinishedEssaim(essaimClass.getName(), groups.get(essaimClass.getName()).getMembers().get(0), true);
                    }
                    else if (essaimClass.isFinished() && groups.containsKey(essaimClass.getName()))
                    {
                        for (Player player : groups.get(essaimClass.getName()).getMembers())
                        {
                            InGameUtilities.sendPlayerInformation(player, "Vous allez être expulsé de l'essaim dans " + (8 - new Timestamp(System.currentTimeMillis() - essaimClass.getFinishDate().getTime()).getMinutes()) + " minutes.");
                        }
                    }
                }
            }
        }.runTaskTimer(main, 0, 20 * 60);
    }
    private boolean isNewHour()
    {
        LocalDateTime today = LocalDateTime.now();
        return today.getMinute() == 0;
    }

    public boolean isEssaimActive(String essaim)
    {
        return activeEssaims.containsKey(essaim);
    }

    public FileConfiguration getConfig()
    {
        return configManager.getConfig();
    }

    public static EssaimConfigManager getConfigManager()
    {
        return configManager;
    }

    public static EssaimFunctions getEssaimFunctions()
    {
        return essaimFunctions;
    }

    public HashMap<String, ActiveMobSpawning> getActiveSpawners()
    {
        return activeSpawners;
    }

    public HashMap<String, HashMap<String, Spawner>> getExistingEssaims()
    {
        return existingEssaims;
    }

    public static HashMap<String, EssaimClass> getActiveEssaims()
    {
        return activeEssaims;
    }

    public static HashMap<String, EssaimGroup> getGroups()
    {
        return groups;
    }
}
