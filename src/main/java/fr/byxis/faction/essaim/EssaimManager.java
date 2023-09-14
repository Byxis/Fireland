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
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Timestamp;
import java.util.HashMap;

public class EssaimManager {

    private static Fireland main;
    public static EssaimConfigManager configManager;
    public static EssaimFunctions essaimFunctions;

    public HashMap<String, ActiveMobSpawning> activeSpawners;
    public HashMap<String, HashMap<String, Spawner>> existingEssaims;
    public static HashMap<String, EssaimClass> activeEssaims;

    public static HashMap<String, EssaimGroup> groups;

    public EssaimManager(Fireland main)
    {
        EssaimManager.main = main;
        configManager = new EssaimConfigManager(main);
        configManager.setup();
        essaimFunctions = new EssaimFunctions(main, configManager);
        existingEssaims = new HashMap<>();
        activeSpawners = new HashMap<>();
        activeEssaims = new HashMap<>();
        groups = new HashMap<>();
        SetupExistingSpawners();
        main.getServer().getPluginManager().registerEvents(new EssaimEventHandler(main), main);
        loop();
    }

    public boolean EnableSpawner(Spawner spawner)
    {
        if(activeSpawners.containsKey(spawner.getName()))
        {
            return false;
        }
        main.getLogger().info("Spawner from "+spawner.getEssaim()+" enabled");
        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(spawner.getMob()).orElse(null);
        ActiveMobSpawning activeMobSpawning = new ActiveMobSpawning(spawner.getEssaim(), getEntityNumber(spawner)+1);
        activeSpawners.put(spawner.getName(), activeMobSpawning);
        new BukkitRunnable(){
            int entityNumber = getEntityNumber(spawner);

            @Override
            public void run() {
                ActiveMob activeMob = mob.spawn(BukkitAdapter.adapt(spawner.getLoc()),1);
                entityNumber --;
                activeSpawners.get(spawner.getName()).addActiveMob(activeMob);
                if(!activeMob.validateLoadedMob())
                {
                    activeMob.setDead();
                    activeSpawners.get(spawner.getName()).removeActiveMob(activeMob);
                }
                if(entityNumber <= 0)
                {
                    cancel();
                }
            }
        }.runTaskTimer(main, Math.round(spawner.getActivationDelay()*20), (long) (spawner.getSpawnDelay() * 20));
        return true;
    }

    private int getEntityNumber(Spawner spawner)
    {

        if(spawner.isAffectedByDifficulty() && EssaimManager.groups.containsKey(spawner.getEssaim()))
        {
            main.getLogger().info("Old :"+spawner.getAmount()+"New :"+Math.round(spawner.getAmount()*EssaimManager.groups.get(spawner.getEssaim()).getMultiplicatorDifficulty()));
            return Math.round(spawner.getAmount()*EssaimManager.groups.get(spawner.getEssaim()).getMultiplicatorDifficulty());
        }
        else
        {
            main.getLogger().info("NotChanged :"+spawner.getAmount());
            return spawner.getAmount();
        }
    }

    public boolean EnableEssaim(String name)
    {
        if(activeEssaims.containsKey(name))
        {
            activeEssaims.get(name).setClosed(false);
            return false;
        }
        try
        {
            EssaimClass essaimClass = new EssaimClass(name,
                    configManager.getConfig().getInt(name+".day"),
                    configManager.getConfig().getInt(name+".hour"),
                    new Location(Bukkit.getWorld(configManager.getConfig().getString(name+".hub.position.world")), //hub
                            configManager.getConfig().getInt(name+".hub.position.x"),
                            configManager.getConfig().getInt(name+".hub.position.y"),
                            configManager.getConfig().getInt(name+".hub.position.z")
                    ),
                    new Location(Bukkit.getWorld(configManager.getConfig().getString(name+".start.position.world")),//start
                            configManager.getConfig().getInt(name+".start.position.x"),
                            configManager.getConfig().getInt(name+".start.position.y"),
                            configManager.getConfig().getInt(name+".start.position.z")
                    ),
                    new Location(Bukkit.getWorld(configManager.getConfig().getString(name+".reset.position.world")),//reset
                            configManager.getConfig().getInt(name+".reset.position.x"),
                            configManager.getConfig().getInt(name+".reset.position.y"),
                            configManager.getConfig().getInt(name+".reset.position.z")
                    )
                    ,new Location(Bukkit.getWorld(configManager.getConfig().getString(name+".entry.position.world")),//entry
                    configManager.getConfig().getInt(name+".entry.position.x"),
                    configManager.getConfig().getInt(name+".entry.position.y"),
                    configManager.getConfig().getInt(name+".entry.position.z")
            )
                    ,new Location(Bukkit.getWorld(configManager.getConfig().getString(name+".solo.position.world")),//entry
                    configManager.getConfig().getInt(name+".solo.position.x"),
                    configManager.getConfig().getInt(name+".solo.position.y"),
                    configManager.getConfig().getInt(name+".solo.position.z")
            ), configManager.getConfig().getInt(name+".jetons")
                    ,false);
            configManager.getConfig().set(name+".closed", false);
            configManager.save();
            activeEssaims.put(name, essaimClass);
        } catch (Exception e) {
            main.getLogger().severe("Un problème est survenu lors de l'activation de l'essaim "+name);
            return false;
        }


        return true;
    }

    public static boolean DisableEssaim(String name)
    {
        if(groups.containsKey(name))
        {
            activeEssaims.get(name).setClosed(true);
            configManager.getConfig().set(name+".closed", true);
            configManager.save();
        }
        activeEssaims.remove(name);

        return true;
    }

    private void SetupExistingSpawners()
    {
        for (String essaim : configManager.getConfig().getConfigurationSection("").getKeys(false))
        {
            HashMap<String, Spawner> existingSpawnerInEssaim = new HashMap<>();
            if(configManager.getConfig().get(essaim+".spawners") != null)
            {
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
                    Double activationDelay = configManager.getConfig().getDouble(essaim+".spawners."+spawner+".activation-delay");
                    Double spawnDelay = configManager.getConfig().getDouble(essaim+".spawners."+spawner+".spawn-delay");
                    Boolean isAffectedByDifficulty = configManager.getConfig().getBoolean(essaim+".spawners."+spawner+".affected-by-difficulty");
                    existingSpawnerInEssaim.put(spawner, new Spawner(spawner, essaim, loc, type, amount, activationDelay, spawnDelay, command, isAffectedByDifficulty));
                }
                existingEssaims.put(essaim, existingSpawnerInEssaim);
                if(!configManager.getConfig().getBoolean(essaim+".closed"))
                {
                    EnableEssaim(essaim);
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
        for(String spawner: existingEssaims.get(essaimClass.getName()).keySet())
        {
            if(activeSpawners.get(essaimClass.getName()) != null)
            {
                for(ActiveMob mob : activeSpawners.get(essaimClass.getName()).getActiveMobs())
                {
                    mob.setDead();
                }
                activeSpawners.remove(spawner);
            }

        }
        return true;
    }

    public boolean setSolo(String arg) {
        if(!activeEssaims.containsKey(arg) || groups.get(arg).getMembers().size() != 1)
        {
            return false;
        }
        EssaimClass essaimClass = activeEssaims.get(arg);
        EssaimFunctions.setBlock(essaimClass.getSolo().blockX(),
                essaimClass.getSolo().blockY(),
                essaimClass.getSolo().blockZ(),
                Material.REDSTONE_BLOCK
        );
        return true;
    }

    public void loop()
    {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(isNewHour())
                {
                    for(String essaim : configManager.getConfig().getConfigurationSection("").getKeys(false))
                    {
                        Timestamp today = new Timestamp(System.currentTimeMillis());
                        if(configManager.getConfig().getInt(essaim+".hour") == (today.getHours()) && configManager.getConfig().getInt(essaim+".day") == (today.getDate()))
                        {
                            EnableEssaim(essaim);
                            for(Player p : Bukkit.getOnlinePlayers())
                            {
                                InGameUtilities.sendPlayerInformation(p, "L'essaim "+activeEssaims.get(essaim).getFormattedName()+" est désormais ouvert. Allez vite le pacifier avant que la menace se répande !");
                            }
                        }
                    }
                }
                for(EssaimClass essaimClass : activeEssaims.values())
                {
                    if(essaimClass.isFinished() && essaimClass.shouldClose())
                    {
                        EssaimFunctions.leaveFinishedEssaim(essaimClass.getName(),groups.get(essaimClass.getName()).getMembers().get(0), true);
                    }
                    else if(essaimClass.isFinished() && groups.containsKey(essaimClass.getName()))
                    {
                        for(Player player : groups.get(essaimClass.getName()).getMembers())
                        {
                            InGameUtilities.sendPlayerInformation(player, "Vous allez être expulsé de l'essaim dans "+(8 - new Timestamp(System.currentTimeMillis() - essaimClass.getFinishDate().getTime()).getMinutes()) + " minutes.");
                        }
                    }
                }

            }
        }.runTaskTimer(main, 0, 20*60);
    }
    private boolean isNewHour()
    {
        Timestamp today = new Timestamp(System.currentTimeMillis());
        return today.getMinutes() == 0;
    }

    public boolean isEssaimActive(String essaim)
    {
        return activeEssaims.containsKey(essaim);
    }
}
