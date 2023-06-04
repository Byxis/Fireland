package fr.byxis.fireland;

//import com.sk89q.worldguard.bukkit.WorldGuardPlugin;


import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import fr.byxis.backpack.BackPack;
import fr.byxis.booster.BoosterCommandCompleter;
import fr.byxis.booster.BoosterManager;
import fr.byxis.command.*;
import fr.byxis.db.DatabaseManager;
import fr.byxis.discretion.ZombieDetection;
import fr.byxis.discretion.rally;
import fr.byxis.essaim.EssaimCommandCompleter;
import fr.byxis.essaim.EssaimCommandManager;
import fr.byxis.essaim.EssaimFunctions;
import fr.byxis.essaim.EssaimManager;
import fr.byxis.event.*;
import fr.byxis.faction.FactionEvent;
import fr.byxis.faction.FactionPvp;
import fr.byxis.faction.factionManager;
import fr.byxis.faction.factionManagerTabCompleter;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.intendant.IntendantCommand;
import fr.byxis.intendant.Manager;
import fr.byxis.jeton.jetonsCommandManager;
import fr.byxis.karma.karmaManager;
import fr.byxis.packet.PacketPlayer;
import fr.byxis.shop.ShopCommandManager;
import fr.byxis.shop.ShopEventManager;
import fr.byxis.workshop.workshopFunction;
import fr.byxis.workshop.workshopManager;
import fr.byxis.workshop.workshopManagerEvent;
import fr.byxis.workshop.workshopManagerTabCompleter;
import fr.byxis.zone.ZoneManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.fusesource.jansi.Ansi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;


public class Fireland extends JavaPlugin {

    boolean night = false;
    boolean day = true;
    boolean moonPhase = true;

    public Economy eco;
    public ConfigManager cfgm;

    private DatabaseManager databaseManager;


    public ProtocolManager protocolManager;
    public HashMapManager hashMapManager;
    public ZoneManager zoneManager;
    public EssaimManager essaimManager;

    @SuppressWarnings("ConstantConditions")
    public void enableCommand() {
        getCommand("speedfly").setExecutor(new speedFly());
        getCommand("heliport").setExecutor(new menu(this));
        getCommand("thirst").setExecutor(new thirst(this));
        getCommand("cure").setExecutor(new infectedPlayer(this));
        getCommand("infect").setExecutor(new infectedPlayer(this));
        getCommand("shop").setExecutor(new ShopCommandManager(this));
        getCommand("shop").setTabCompleter(new ShopCommandManager(this));
        getCommand("rename").setExecutor(new rename());
        getCommand("rally").setExecutor(new rally(this));
        getCommand("stack").setExecutor(new stack());
        getCommand("bank").setExecutor(new bank(this));
        getCommand("ambientsound").setExecutor(new ambientSound(this));
        getCommand("n").setExecutor(new nightVision());
        getCommand("faction").setExecutor(new factionManager(this));
        getCommand("faction").setTabCompleter(new factionManagerTabCompleter());
        getCommand("discord").setExecutor(new DiscordCommand());
        getCommand("jeton").setExecutor(new jetonsCommandManager(this));
        getCommand("jeton").setTabCompleter(new jetonsCommandManager(this));
        getCommand("rang").setExecutor(new karmaManager(this));
        getCommand("rang").setTabCompleter(new karmaManager(this));
        getCommand("workshop").setExecutor(new workshopManager(this));
        getCommand("workshop").setTabCompleter(new workshopManagerTabCompleter(this));
        getCommand("intendant").setExecutor(new IntendantCommand(this));
        getCommand("playpacket").setExecutor(new PacketPlayer(this));
        getCommand("booster").setExecutor(new BoosterManager(this));
        getCommand("booster").setTabCompleter(new BoosterCommandCompleter(this));
        getCommand("backpack").setExecutor(new BackPack(this));
        getCommand("essaim").setExecutor(new EssaimCommandManager(this));
        getCommand("essaim").setTabCompleter(new EssaimCommandCompleter(this));
    }

    private void enableEvent() {
        getServer().getPluginManager().registerEvents(new worldGeneration(), this);
        getServer().getPluginManager().registerEvents(new menu(this), this);
        getServer().getPluginManager().registerEvents(new thirst(this), this);
        getServer().getPluginManager().registerEvents(new infectedPlayer(this), this);
        getServer().getPluginManager().registerEvents(new doorPass(this), this);
        getServer().getPluginManager().registerEvents(new shop(this), this);
        getServer().getPluginManager().registerEvents(new spawn(this), this);
        getServer().getPluginManager().registerEvents(new bank(this), this);
        getServer().getPluginManager().registerEvents(new parachute(this), this);
        getServer().getPluginManager().registerEvents(new craft(), this);
        getServer().getPluginManager().registerEvents(new villagerInteraction(this), this);
        getServer().getPluginManager().registerEvents(new playerDeath(this), this);
        getServer().getPluginManager().registerEvents(new removeBedInteraction(), this);
        getServer().getPluginManager().registerEvents(new fallDamage(), this);
        getServer().getPluginManager().registerEvents(new ZombieDetection(this), this);
        getServer().getPluginManager().registerEvents(new ambientSound(this), this);
        getServer().getPluginManager().registerEvents(new scoreboardPlayer(this), this);
        getServer().getPluginManager().registerEvents(new silverfishSilent(), this);
        getServer().getPluginManager().registerEvents(new RemoveInteractionBlock(), this);
        getServer().getPluginManager().registerEvents(new stairs(this), this);
        getServer().getPluginManager().registerEvents(new modifiedInteractionItemstackSize(this), this);
        getServer().getPluginManager().registerEvents(new chatListener(), this);
        getServer().getPluginManager().registerEvents(new timeAlive(this), this);
        getServer().getPluginManager().registerEvents(new worldGuardEvent(this), this);
        getServer().getPluginManager().registerEvents(new quadListener(this), this);
        getServer().getPluginManager().registerEvents(new cobwebDamage(), this);
        getServer().getPluginManager().registerEvents(new join(this), this);
        getServer().getPluginManager().registerEvents(new NVGoogles(), this);
        getServer().getPluginManager().registerEvents(new FactionPvp(this), this);
        getServer().getPluginManager().registerEvents(new playerManager(this), this);
        getServer().getPluginManager().registerEvents(new zombieManager(this), this);
        getServer().getPluginManager().registerEvents(new jetonsCommandManager(this), this);
        getServer().getPluginManager().registerEvents(new karmaManager(this), this);
        getServer().getPluginManager().registerEvents(new workshopManagerEvent(this), this);
        getServer().getPluginManager().registerEvents(new entitySpawn(), this);
        getServer().getPluginManager().registerEvents(new ShopEventManager(this), this);
        getServer().getPluginManager().registerEvents(new FactionEvent(this), this);
        getServer().getPluginManager().registerEvents(new SaveEvent(this), this);
        getServer().getPluginManager().registerEvents(new Manager(this), this);
        getServer().getPluginManager().registerEvents(new RankCustomMessage(this), this);
        getServer().getPluginManager().registerEvents(new BoosterManager(this), this);
        getServer().getPluginManager().registerEvents(new BackPack(this), this);
        getServer().getPluginManager().registerEvents(new InGameUtilities(this), this);
        zoneManager.RegisterEvents();
        //getServer().getPluginManager().registerEvents(new packetListener(this), this);
		/*protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.STEER_VEHICLE)
		{
			@Override
			public void onPacketReceiving(PacketEvent event)
			{
				if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE)
				{
					Player p = event.getPlayer();
					PacketContainer packet = event.getPacket();
					Entity vehicle = p.getVehicle();
					Location loc = vehicle.getLocation();
					loc.setY(loc.getY()-0.1);
					Vector vec = loc.getDirection();

					if(packet.getFloat().getValues().get(0) != 0)
					{
						loc.setYaw(loc.getYaw()+(packet.getFloat().getValues().get(0)*-1));
						loc.setYaw(0);
						vehicle.teleport(loc);
					}
					vehicle.setVelocity(vec.multiply(packet.getFloat().getValues().get(1)*0.8));

					/*locVehicleDestination.setX(locVehicleDestination.getX()+((packet.getFloat().getValues().get(1)*0.8)));
					locVehicleDestination.setZ(locVehicleDestination.getZ()+((packet.getFloat().getValues().get(0)*-1*0.8)));

					moveNextTick(vehicle, locVehicleDestination);
				}
			}
		});*/
    }


    public void onEnable() {
        getLogger().info("================================");
        getLogger().info(" ");
        getLogger().info(" ");
        getLogger().info("   Fireland is now enabled !");
        getLogger().info(" ");
        getLogger().info(" ");
        getLogger().info("-");
        getLogger().info(" ");

        databaseManager = new DatabaseManager();
        hashMapManager = new HashMapManager();
        hashMapManager.Init();

        protocolManager = ProtocolLibrary.getProtocolManager();
        saveDefaultConfig();
        loadConfigManager();

        zoneManager = new ZoneManager(this);
        essaimManager = new EssaimManager(this);

        final scoreboardPlayer scoreboardPlayerClass;
        final ambientSound ambientSoundClass;
        final cobwebDamage cobwebDamageClass;

        final thirst thirst = null;
        scoreboardPlayerClass = new scoreboardPlayer(this);
        ambientSoundClass = new ambientSound(this);
        cobwebDamageClass = new cobwebDamage();

        changeItemsStackSize();

        enableCommand();
        enableEvent();



        //changeItemsStackSize();
        new BukkitRunnable() {

            @Override
            public void run() {
                dateListener();
            }
        }.runTaskTimer(this, 0, 20*60*60);


        if(!setupEconomy()) {
            getLogger().info(ChatColor.RED+"You must have vault !");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        final FileConfiguration playerDBConfig = cfgm.getPlayerDB();

        new BukkitRunnable() {

            @Override
            public void run() {
                ambientSoundClass.playSound();
            }
        }.runTaskTimer(this, 0, 2000);

        new BukkitRunnable() {
            @SuppressWarnings({ "deprecation" })
            @Override
            public void run() {
                if(hashMapManager.getBooster() != null && hashMapManager.getBooster().getFinished().before(new Date(System.currentTimeMillis())))
                {
                    hashMapManager.setBooster(null);
                }
                for(Player p : getServer().getOnlinePlayers()) {
                    if(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)){

                        double thirst = playerDBConfig.getDouble("thirst."+p.getUniqueId());
                        if(thirst*0.01f >= 1)
                        {
                            p.setExp(1);
                        }
                        else if(thirst*0.01f <= 0)
                        {
                            p.setExp(0);
                        }
                        else
                        {
                            p.setExp((float) thirst*0.01f);
                        }

                        if(p.isClimbing() || p.isSprinting() ||p.isSwimming())
                        {
                            if(!p.isOnGround())
                            {
                                playerDBConfig.set("thirst."+p.getUniqueId(), thirst-0.4);
                                cfgm.savePlayerDB();
                            }
                            else
                            {
                                playerDBConfig.set("thirst."+p.getUniqueId(), thirst-0.2);
                                cfgm.savePlayerDB();
                            }
                        }

                        if (thirst <= 0f) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 0, false, false), true);
                            if(p.getHealth() > 1) {
                                p.damage(1);
                            }
                        }
                    }

                    if(p.getInventory().getHelmet() != null) {
                        if(p.getInventory().getHelmet().getType() == Material.RED_DYE) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 80, 2, false, false), true);
                        }
                    }
                }
                Server server = getServer();
                World world = server.getWorld("world");
                long time = 0;
                if (world != null) {
                    time = world.getTime();
                }
                long days = 0;
                if (world != null) {
                    days = world.getFullTime()/24000;
                }
                long phase= days%8;

                if(time > 12500 && time < 23000 && !day) {
                    if(phase == 0 && moonPhase) {
                        moonPhase = false;
                        if(!getServer().getOnlinePlayers().isEmpty()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a times 20 100 20");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a title {\"text\":\" \"}");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a subtitle {\"text\":\"La lune de sang arrive !\",\"bold\":true,\"color\":\"dark_red\"}");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound minecraft:gun.hud.bloodmoon ambient @a ~ ~ ~ 99999999");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound minecraft:gun.hud.cryofunheard ambient @a");

                        }

                    }else if (phase != 0) {
                        moonPhase = true;
                        if(!getServer().getOnlinePlayers().isEmpty()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a times 20 100 20");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a title {\"text\":\" \"}");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a subtitle {\"text\":\"La nuit arrive !\",\"bold\":true,\"color\":\"red\"}");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound minecraft:gun.hud.inception ambient @a ~ ~ ~ 99999999");
                        }
                        night = false;
                        day = true;
                    }
                }

                if ((time > 23000 && day) || (time > 0 && time < 12500 && day)) {
                    night = true;
                    day = false;
                    if(!getServer().getOnlinePlayers().isEmpty()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a times 20 100 20");
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a title {\"text\":\" \"}");
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a subtitle {\"text\":\"Le soleil se lève\",\"bold\":true,\"color\":\"gold\"}");
                        for(Player p : getServer().getOnlinePlayers())
                        {
                            p.playSound(p.getLocation(), "minecraft:gun.hud.night_passed", 1, 1);
                        }

                    }
                }
            }
        }.runTaskTimer(this, 0, 40);

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player p : getServer().getOnlinePlayers()) {
                    if(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)){

                        float thirst = (float) playerDBConfig.getDouble("thirst."+p.getUniqueId());

                        if (thirst <= 0f) {
                            p.sendMessage("§8Vous avez soif !");
                        }
                    }

                }

            }
        }.runTaskTimer(this, 0, 400);

        new BukkitRunnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                for(Player p : getServer().getOnlinePlayers()) {
                    if(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)){

                        boolean infected = playerDBConfig.getBoolean("infected."+p.getUniqueId()+".state");
                        if (infected) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 240, 0, false, false), true);
                            int timer = playerDBConfig.getInt("infected."+p.getUniqueId()+".time");

                            if(playerDBConfig.getString("infected."+p.getUniqueId()+".time") == null) {
                                playerDBConfig.set("infected."+p.getUniqueId()+".time", 1);
                            }else {
                                playerDBConfig.set("infected."+p.getUniqueId()+".time", playerDBConfig.getInt("infected."+p.getUniqueId()+".time")+1);
                            }
                            cfgm.savePlayerDB();
                            if(timer >= 120){
                                p.sendMessage("§8Votre infection a causé votre perte....");
                                p.setHealth(0.0D);

                            }else if(timer == 20 || timer == 40 || timer == 60 || timer == 80) {
                                p.sendMessage("§8Votre infection s'aggrave !");
                                p.damage(3);
                            }else if(timer == 100) {
                                p.sendMessage("§8Votre infection est très grave ! Cherchez vite une seringue !");
                                p.damage(3);
                            }else if(p.getHealth() > 2) {
                                p.damage(2);
                            }
                        }
                    }
                }

            }
        }.runTaskTimer(this, 0, 100);


        new BukkitRunnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if(playerDBConfig.getBoolean("parachute."+p.getUniqueId())) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5, 2, false, false), true);
                        if(p.getItemInHand().getType() == Material.POPPY) {
                            p.getItemInHand().setType(Material.DANDELION);
                        }
                    }else {
                        if(p.getItemInHand().getType() == Material.DANDELION) {
                            p.getItemInHand().setType(Material.POPPY);
                        }
                    }
                }

            }

        }.runTaskTimer(this, 0, 1);
        workshopFunction wf = new workshopFunction(this, null);
        new BukkitRunnable()
        {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                for(Player p : getServer().getOnlinePlayers()) {

                    if(p.getGameMode() != GameMode.CREATIVE ||p.getGameMode() != GameMode.SPECTATOR)
                    {
                        playTimePlayerAdd(p);
                    }
                    checkDiscretionPoint(p);
                    scoreboardPlayerClass.update(p);
                    if(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))
                    {
                        boolean infected = playerDBConfig.getBoolean("infected."+p.getUniqueId()+".state");
                        if (infected || p.getHealth() < 6) {
                            //playBorderPackets(p, true);
                            p.playSound(p.getLocation(), "minecraft:entity.player.heartbeat", 1, 1);
                        }
                        else
                        {
                            //playBorderPackets(p, false);
                        }

                        int safezone = cfgm.getPlayerDB().getInt("safezone."+p.getUniqueId()+".time");

                        if (safezone > 0)
                        {
                            cfgm.getPlayerDB().set("safezone."+p.getUniqueId()+".time", (safezone-1));
                            cfgm.savePlayerDB();
                            if(safezone == 5 || safezone == 10)
                            {
                                p.sendTitle("", "§cIl vous reste "+safezone+" secondes d'invincibilité");
                            }
                        }
                        else
                        {
                            if(safezone == 0)
                            {
                                p.sendMessage("§cVous n'êtes plus invincible !");
                                cfgm.getPlayerDB().set("safezone."+p.getUniqueId()+".time", -1);
                                p.setInvulnerable(false);
                                cfgm.savePlayerDB();
                            }
                        }

                    }
                    else
                    {
                        //	playBorderPackets(p, false);
                        //ATELIER
                    }
					/*if(p.getOpenInventory().getTitle().contains("Attente"))
					{
						wf.sender = p;
						int max = wf.getInvPageMax(p.getOpenInventory());
						int current = wf.getInvPageCurrent(p.getOpenInventory());
						wf.setItemsCraftingInv((Inventory) p.getOpenInventory(), wf.getAllCraftingItems(p, p.getUniqueId().toString()), current, max);
					}*/
                }
            }

        }.runTaskTimer(this, 0, 20);
        cfgm.savePlayerDB();
        new BukkitRunnable(){
            @Override
            public void run() {
                for(Player p : getServer().getOnlinePlayers())
                {
                    Location from = p.getLocation();
                    //thirst.updateThirst(from, p);
                }
            }
        }.runTaskTimer(this, 0, 10);
        getLogger().info(" ");
        getLogger().info("================================");
    }

    public void onDisable() {
        getLogger().info("================================");
        getLogger().info(" ");
        getLogger().info(" ");
        getLogger().info(Ansi.ansi().fg(Ansi.Color.GREEN).toString()+"   Fireland is now disabled !");
        this.databaseManager.close();
        SaveEvent se = new SaveEvent(this);
        se.onDisable();
        getLogger().info(" ");
        getLogger().info(" ");
        getLogger().info("================================");
    }

	/*void moveNextTick(Entity ent, Location loc)
	{
	    ent.setVelocity(loc.subtract(ent.getLocation()).toVector());
	}*/

    private void changeItemsStackSize()
    {
        modifyStackSize(Material.PUMPKIN_SEEDS, 4, false);
        modifyStackSize(Material.MELON_SEEDS, 4, false);
        modifyStackSize(Material.GOLD_NUGGET, 4, false);
        modifyStackSize(Material.PURPLE_DYE, 4, false);
        modifyStackSize(Material.GRAY_DYE, 4, false);
        modifyStackSize(Material.PINK_DYE, 4, false);
        modifyStackSize(Material.LIGHT_BLUE_DYE, 4, false);
        modifyStackSize(Material.INK_SAC, 4, false);
        modifyStackSize(Material.LAPIS_LAZULI, 4, false);
        modifyStackSize(Material.BLUE_DYE, 4, false);
        modifyStackSize(Material.ORANGE_DYE, 4, false);
        modifyStackSize(Material.LIME_DYE, 4, false);
        modifyStackSize(Material.GREEN_DYE, 4, false);
        modifyStackSize(Material.MAGENTA_DYE, 4, false);
        modifyStackSize(Material.STICK, 4, false);
        modifyStackSize(Material.YELLOW_DYE, 4, false);
        modifyStackSize(Material.BRICK, 4, false);
        modifyStackSize(Material.BROWN_DYE, 4, false);
        modifyStackSize(Material.CHARCOAL, 12, false);
        modifyStackSize(Material.BEETROOT_SEEDS, 32, false);
        modifyStackSize(Material.LIGHT_GRAY_DYE, 4, false);
        modifyStackSize(Material.MELON_SEEDS, 4, false);
        modifyStackSize(Material.IRON_NUGGET, 4, false);
        modifyStackSize(Material.CYAN_DYE, 32, false);
        modifyStackSize(Material.SLIME_BALL, 4, false);
        modifyStackSize(Material.PAPER, 1, false);
        modifyStackSize(Material.END_ROD, 1, false);
        modifyStackSize(Material.RED_DYE, 1, false);
        modifyStackSize(Material.GHAST_TEAR, 4, false);
        modifyStackSize(Material.NETHER_BRICK, 2, false);
        modifyStackSize(Material.FIREWORK_ROCKET, 8, false);
    }

    public void dateListener()
    {
        Date now = new Date();
        @SuppressWarnings("deprecation")
        int today = now.getDate();
        int old = cfgm.getEnderchest().getInt("date");

        if(today != old)
        {
            cfgm.getEnderchest().set("date", today);
            cfgm.saveEnderchest();
            if(cfgm.getEnderchest().get("stockage") != null)
            {
                for(String s : cfgm.getEnderchest().getConfigurationSection("stockage").getKeys(true))
                {
                    if(cfgm.getEnderchest().getInt("stockage."+s+".money") > 0)
                    {
                        if(cfgm.getEnderchest().getInt("stockage."+s+".money") <= 50)
                        {
                            cfgm.getEnderchest().set("stockage."+s+".money", 0);
                        }
                        else
                        {
                            cfgm.getEnderchest().set("stockage."+s+".money", cfgm.getEnderchest().getInt("bank."+s+".money")-50);
                        }
                    }
                    else
                    {
                        cfgm.getEnderchest().set(s, null);
                    }
                }
                cfgm.saveEnderchest();
            }
            if(cfgm.getKarmaDB().get("") != null) {
                for (String s : cfgm.getKarmaDB().getConfigurationSection("").getKeys(false)) {
                    if (!s.equals("max")) {
                        cfgm.getKarmaDB().set("max." + s, 0);
                    }
                }
                cfgm.saveKarmaDB();
            }
        }
    }

    public DatabaseManager getDatabaseManager()
    {
        return databaseManager;
    }



    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economy = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if(economy != null) {
            eco = economy.getProvider();
        }
        return(eco != null);

    }

    public void loadConfigManager()
    {
        cfgm = new ConfigManager();
        cfgm.setup();
    }

    public void commandExecutor(Player p, String cmd, String perm)
    {
        HashMap<UUID, PermissionAttachment> perms = new HashMap<UUID, PermissionAttachment>();

        PermissionAttachment attachment = p.addAttachment(this);
        perms.put(p.getUniqueId(), attachment);

        PermissionAttachment permissions = perms.get(p.getUniqueId());

        try
        {
            permissions.setPermission(perm, true);
            Bukkit.dispatchCommand(p, cmd);
        }
        catch(Exception e1)
        {
            e1.printStackTrace();
        }
        finally
        {
            perms.get(p.getUniqueId()).unsetPermission(perm);
        }
    }

    /*
    public void modifyStackSize(Material mat, int size)
    {
        try {
            Field f = Itr.class.getDeclaredField("OverStackSize");
            f.setAccessible(true);
            f.setInt(mat, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
    public boolean modifyStackSize(Material material, int size, boolean log) {
        // Verify that the material is an item (that can be stored in an inventory).
        if (!material.isItem()) {
            this.getLogger().warning(String.format("%s is not an item.", material.name()));
            return false;
        }
        // Do nothing if the stack size is already correct.
        if (material.getMaxStackSize() == size) {
            if (log) {
                this.getLogger().info(String.format("%s already has maximum stack size %d.", material.name(), size));
            }
            return true;
        }

        try {
            // Get the server package version.
            // In 1.14, the package that the server class CraftServer is in, is called "org.bukkit.craftbukkit.v1_14_R1".
            String packageVersion = this.getServer().getClass().getPackage().getName().split("\\.")[3];
            // Convert a Material into its corresponding Item by using the getItem method on the Material.
            Class<?> magicClass = Class.forName("org.bukkit.craftbukkit." + packageVersion + ".util.CraftMagicNumbers");
            Method method = magicClass.getDeclaredMethod("getItem", Material.class);
            Object item = method.invoke(null, material);
            // Get the maxItemStack field in Item and change it.
            Class<?> itemClass = Class.forName("net.minecraft.world.item.Item");
            Field field = itemClass.getDeclaredField("d");
            field.setAccessible(true);
            field.setInt(item, size);
            // Change the maxStack field in the Material.
            Field mf = Material.class.getDeclaredField("maxStack");
            mf.setAccessible(true);
            mf.setInt(material, size);
            if (log) {
                this.getLogger().info(String.format("Applied a maximum stack size of %d to %s.", size, material.name()));
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            this.getLogger().severe(String.format("Reflection error while modifying maximum stack size of %s.", material.name()));
            return false;
        }
    }/**/

	/*public WorldGuardPlugin getWorldGuard()
	{
		Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
		if(!(plugin instanceof WorldGuardPlugin))
		{
			return null;
		}
		return (WorldGuardPlugin) plugin;
	}*/

    private void checkDiscretionPoint(Player player){
        double discretion = 100;

        //check player movement
		/*if(cfgm.getPlayerDB().getBoolean(sDiscretion+"move"))
		{
			discretion -= 40;
			if(player.isSneaking())
			{
				discretion += 30;
			}
			else
			{
				//check player jump
				if(cfgm.getPlayerDB().getBoolean(sDiscretion+"jump"))
				{
					discretion -= 10;
				}
				//check player sprint
				if(player.isSprinting())
				{
					discretion -= 50;
				}
			}
		}
		//check player noise
		if(cfgm.getPlayerDB().getBoolean(sDiscretion+"eat"))
		{
			discretion -= 15;
		}*/
        if(!hashMapManager.getDiscretionMap().containsKey(player.getUniqueId()))
        {
            hashMapManager.addDiscretionMap(player.getUniqueId());
        }

        if(hashMapManager.getDiscretionMap().get(player.getUniqueId()).isMoving())
        {
            discretion -= 30;
            if(player.isSneaking())
            {
                discretion += 20;
            }
        }

        if(player.isSprinting() ||player.isSwimming() ||player.isClimbing())
        {
            discretion -= 50;

        }
        if(!player.isFlying() && !player.isOnGround() && !player.isClimbing())
        {
            discretion -= 30;
        }
        if((player.getItemInHand() != null && player.getItemInHand().getType() == Material.END_ROD) || (player.getInventory().getItemInOffHand() != null && player.getInventory().getItemInOffHand().getType() == Material.END_ROD))
        {
            discretion -= 20;
            hashMapManager.getDiscretionMap().get(player.getUniqueId()).setUsingLights(true);
        }
        else
        {
            hashMapManager.getDiscretionMap().get(player.getUniqueId()).setUsingLights(false);
        }

        if(discretion > 100)
        {
            discretion = 100;
        }
        else if(discretion < 0)
        {
            discretion =0;
        }

        hashMapManager.getDiscretionMap().get(player.getUniqueId()).setScore(discretion);
        //cfgm.getPlayerDB().set(sDiscretion+"score", discretion);
        //cfgm.savePlayerDB();
    }

    private void playTimePlayerAdd(Player p)
    {
        cfgm.getPlayerDB().set("playtime."+p.getUniqueId(), cfgm.getPlayerDB().getInt("playtime."+p.getUniqueId())+ 1);
    }

	/*private void playBorderPackets(Player p, boolean warn)
	{
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.WORLD_BORDER);
		packet.getWorldBorderActions().writeDefaults();
		packet.getDoubles().write(0, 29999984D);
		protocolManager.broadcastServerPacket(packet, p, false);
	}*/





}