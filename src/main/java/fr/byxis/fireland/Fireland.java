package fr.byxis.fireland;

//import com.sk89q.worldguard.bukkit.WorldGuardPlugin;


import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import fr.byxis.command.*;
import fr.byxis.db.DatabaseManager;
import fr.byxis.event.*;
import fr.byxis.faction.bunker.BunkerManager;
import fr.byxis.faction.essaim.EssaimCommandCompleter;
import fr.byxis.faction.essaim.EssaimCommandManager;
import fr.byxis.faction.essaim.EssaimManager;
import fr.byxis.faction.faction.FactionEvent;
import fr.byxis.faction.faction.FactionManager;
import fr.byxis.faction.faction.FactionManagerTabCompleter;
import fr.byxis.faction.faction.FactionPvp;
import fr.byxis.faction.zone.ZoneManager;
import fr.byxis.fireland.restart.RestartManager;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.PermissionUtilities;
import fr.byxis.fireland.utilities.WorldUtilities;
import fr.byxis.jeton.FactureCommand;
import fr.byxis.jeton.JetonManager;
import fr.byxis.jeton.JetonsCommandManager;
import fr.byxis.player.PlayerAddonsEnabler;
import fr.byxis.player.PlayerDeath;
import fr.byxis.player.booster.BoosterCommandCompleter;
import fr.byxis.player.booster.BoosterManager;
import fr.byxis.player.discretion.DiscretionClass;
import fr.byxis.player.discretion.RallyCommand;
import fr.byxis.player.discretion.ZombieDetection;
import fr.byxis.player.intendant.IntendantCommand;
import fr.byxis.player.intendant.Manager;
import fr.byxis.player.items.morphine.FallDamage;
import fr.byxis.player.items.toxic.Mask;
import fr.byxis.player.items.water.Thirst;
import fr.byxis.player.packet.PacketPlayer;
import fr.byxis.player.shop.ShopCommandManager;
import fr.byxis.player.shop.ShopEventManager;
import fr.byxis.player.workshop.WorkshopFunction;
import fr.byxis.player.workshop.WorkshopManager;
import fr.byxis.player.workshop.WorkshopManagerEvent;
import fr.byxis.player.workshop.WorkshopManagerTabCompleter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.fusesource.jansi.Ansi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

import static fr.byxis.fireland.Save.SaveAll;
import static fr.byxis.player.items.morphine.FallDamage.hasLegsBroken;
import static fr.byxis.player.items.toxic.InfectedPlayer.*;
import static fr.byxis.player.packet.PacketFunctions.sendWorldBorderWarningDistancePacket;


public class Fireland extends JavaPlugin {

    boolean night = false;
    boolean day = true;
    boolean moonPhase = true;

    public static Economy eco;
    public ConfigManager cfgm;

    private DatabaseManager databaseManager;

    public ProtocolManager protocolManager;
    public HashMapManager hashMapManager;
    public ZoneManager zoneManager;
    public EssaimManager essaimManager;
    public BunkerManager bunkerManager;
    public PermissionUtilities permissionUtilities;
    public PlayerAddonsEnabler playerAddonsEnabler;
    public RestartManager restartManager;

    @SuppressWarnings("ConstantConditions")
    public void enableCommand() {
        getCommand("fireland").setExecutor(new FirelandCommand(this));
        getCommand("speedfly").setExecutor(new SpeedFly());
        getCommand("heliport").setExecutor(new Menu(this));
        getCommand("shop").setExecutor(new ShopCommandManager(this));
        getCommand("shop").setTabCompleter(new ShopCommandManager(this));
        getCommand("rename").setExecutor(new Rename());
        getCommand("rally").setExecutor(new RallyCommand(this));
        getCommand("stack").setExecutor(new Stack());
        getCommand("bank").setExecutor(new Bank(this));
        getCommand("ambientsound").setExecutor(new AmbientSound(this));
        getCommand("n").setExecutor(new NightVision());
        getCommand("faction").setExecutor(new FactionManager(this));
        getCommand("faction").setTabCompleter(new FactionManagerTabCompleter());
        getCommand("discord").setExecutor(new DiscordCommand());
        getCommand("jeton").setExecutor(new JetonsCommandManager(this));
        getCommand("jeton").setTabCompleter(new JetonsCommandManager(this));
        getCommand("facture").setExecutor(new FactureCommand(this));
        getCommand("facture").setTabCompleter(new FactureCommand(this));
        //getCommand("rang").setExecutor(new KarmaManager(this));
        //getCommand("rang").setTabCompleter(new KarmaManager(this));
        getCommand("workshop").setExecutor(new WorkshopManager(this));
        getCommand("workshop").setTabCompleter(new WorkshopManagerTabCompleter(this));
        getCommand("intendant").setExecutor(new IntendantCommand(this));
        getCommand("playpacket").setExecutor(new PacketPlayer(this));
        getCommand("booster").setExecutor(new BoosterManager(this));
        getCommand("booster").setTabCompleter(new BoosterCommandCompleter(this));
        getCommand("essaim").setExecutor(new EssaimCommandManager(this));
        getCommand("essaim").setTabCompleter(new EssaimCommandCompleter(this));
    }

    private void enableEvent() {
        getServer().getPluginManager().registerEvents(new WorldGeneration(), this);
        getServer().getPluginManager().registerEvents(new Menu(this), this);
        getServer().getPluginManager().registerEvents(new DoorPass(this), this);
        getServer().getPluginManager().registerEvents(new shop(this), this);
        getServer().getPluginManager().registerEvents(new UtilitaryMerchantHandler(this), this);
        getServer().getPluginManager().registerEvents(new Bank(this), this);
        getServer().getPluginManager().registerEvents(new PreventCrafting(), this);
        getServer().getPluginManager().registerEvents(new VillagerInteraction(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeath(this), this);
        getServer().getPluginManager().registerEvents(new FallDamage(this), this);
        getServer().getPluginManager().registerEvents(new ZombieDetection(this), this);
        getServer().getPluginManager().registerEvents(new AmbientSound(this), this);
        getServer().getPluginManager().registerEvents(new SilverfishSilent(), this);
        getServer().getPluginManager().registerEvents(new InteractionManager(), this);
        getServer().getPluginManager().registerEvents(new Stairs(this), this);
        getServer().getPluginManager().registerEvents(new ModifiedInteractionItemstackSize(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new TimeAlive(this), this);
        getServer().getPluginManager().registerEvents(new WorldGuardEvent(this), this);
        getServer().getPluginManager().registerEvents(new QuadListener(this), this);
        getServer().getPluginManager().registerEvents(new CobwebDamage(), this);
        getServer().getPluginManager().registerEvents(new Join(this), this);
        getServer().getPluginManager().registerEvents(new NVGoogles(), this);
        getServer().getPluginManager().registerEvents(new FactionPvp(this), this);
        getServer().getPluginManager().registerEvents(new PlayerManager(this), this);
        getServer().getPluginManager().registerEvents(new ZombieManager(this), this);
        getServer().getPluginManager().registerEvents(new JetonsCommandManager(this), this);
        //getServer().getPluginManager().registerEvents(new KarmaManager(this), this);
        getServer().getPluginManager().registerEvents(new WorkshopManagerEvent(this), this);
        getServer().getPluginManager().registerEvents(new EntitySpawn(), this);
        getServer().getPluginManager().registerEvents(new ShopEventManager(this), this);
        getServer().getPluginManager().registerEvents(new FactionEvent(this), this);
        getServer().getPluginManager().registerEvents(new SaveEvent(this), this);
        getServer().getPluginManager().registerEvents(new Manager(this), this);
        getServer().getPluginManager().registerEvents(new BoosterManager(this), this);
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
        getLogger().info(Ansi.ansi().fg(Ansi.Color.GREEN).toString() + "================================");
        getLogger().info(" ");
        getLogger().info(" ");
        getLogger().info(Ansi.ansi().fg(Ansi.Color.GREEN).toString() + "   Fireland is now enabled !");
        getLogger().info(" ");
        getLogger().info(" ");
        getLogger().info(Ansi.ansi().fg(Ansi.Color.GREEN).toString() + "-");
        getLogger().info(" ");

        loadConfigManager();
        databaseManager = new DatabaseManager(this);
        hashMapManager = new HashMapManager(this);

        protocolManager = ProtocolLibrary.getProtocolManager();
        saveDefaultConfig();

        new JetonManager(this);
        zoneManager = new ZoneManager(this);
        essaimManager = new EssaimManager(this);
        bunkerManager = new BunkerManager(this);
        permissionUtilities = new PermissionUtilities(this);
        playerAddonsEnabler = new PlayerAddonsEnabler(this);
        final AmbientSound ambientSoundClass;
        final CobwebDamage cobwebDamageClass;

        final Thirst thirst = null;
        ambientSoundClass = new AmbientSound(this);
        cobwebDamageClass = new CobwebDamage();

        changeItemsStackSize();

        enableCommand();
        enableEvent();

        restartManager = new RestartManager(this);



        //changeItemsStackSize();
        new BukkitRunnable() {

            @Override
            public void run() {
                dateListener();
            }
        }.runTaskTimer(this, 0, 20 *60 *60);

        if (!setupEconomy())
        {
            getLogger().info(ChatColor.RED + "You must have vault !");
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
                if (hashMapManager.getBooster() != null && hashMapManager.getBooster().getFinished().before(new Date(System.currentTimeMillis())))
                {
                    hashMapManager.setBooster(null);
                }
                for (Player p : getServer().getOnlinePlayers())
                {
                    if (p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))
                    {

                        double thirst = playerDBConfig.getDouble("thirst." + p.getUniqueId());
                        if (thirst * 0.01f >= 1)
                        {
                            p.setExp(1);
                        }
                        else if (thirst * 0.01f <= 0)
                        {
                            p.setExp(0);
                        }
                        else
                        {
                            p.setExp((float) thirst *0.01f);
                        }

                        if (p.isClimbing() || p.isSprinting() || p.isSwimming())
                        {
                            if (!p.isOnGround())
                            {
                                playerDBConfig.set("thirst." + p.getUniqueId(), thirst-0.4);
                                cfgm.savePlayerDB();
                            }
                            else
                            {
                                playerDBConfig.set("thirst." + p.getUniqueId(), thirst-0.2);
                                cfgm.savePlayerDB();
                            }
                        }

                        if (thirst <= 0f) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 0, false, false), true);
                            if (p.getHealth() > 1)
                            {
                                p.damage(1);
                            }
                        }

                        if (hasLegsBroken(p))
                        {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 3, false, false));
                            p.damage(0.00001f);
                        }

                        Mask.addEffects(p);
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
                long phase = days%8;

                if (time > 12500 && time < 23000 && !day)
                {
                    if (phase == 0 && moonPhase)
                    {
                        moonPhase = false;
                        if (!getServer().getOnlinePlayers().isEmpty())
                        {
                            InGameUtilities.playEveryoneTitleExcept("", "§4§lLa lune de sang se lève !", 20, 100, 20, Bukkit.getWorld("essaim"));
                            InGameUtilities.playEveryoneSoundExcept("gun.hud.bloodmoon", SoundCategory.WEATHER, 1, 1, Bukkit.getWorld("essaim"));
                            InGameUtilities.playEveryoneSoundExcept("gun.hud.cryofunheard", SoundCategory.WEATHER, 1, 1, Bukkit.getWorld("essaim"));

                        }

                    }else if (phase != 0) {
                        moonPhase = true;
                        if (!getServer().getOnlinePlayers().isEmpty())
                        {
                            InGameUtilities.playEveryoneTitleExcept("", "§cLa nuit se lève !", 20, 100, 20, Bukkit.getWorld("essaim"));
                            InGameUtilities.playEveryoneSoundExcept("gun.hud.inception", SoundCategory.WEATHER, 1, 1, Bukkit.getWorld("essaim"));
                        }
                        night = false;
                        day = true;
                    }
                }

                if ((time > 23000 && day) || (time > 0 && time < 12500 && day)) {
                    night = true;
                    day = false;
                    if (!getServer().getOnlinePlayers().isEmpty())
                    {
                        InGameUtilities.playEveryoneSoundExcept("gun.hud.night_passed", SoundCategory.WEATHER, 1, 1, Bukkit.getWorld("essaim"));
                        InGameUtilities.playEveryoneTitleExcept("", "§6Le soleil se lève !", 20, 100, 20, Bukkit.getWorld("essaim"));
                    }
                }
            }
        }.runTaskTimer(this, 0, 40);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : getServer().getOnlinePlayers())
                {
                    if (p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))
                    {

                        float thirst = (float) playerDBConfig.getDouble("thirst." + p.getUniqueId());

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
                for (Player p : getServer().getOnlinePlayers())
                {
                    if (p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))
                    {

                        boolean infected = isInfected(p);
                        if (infected) {
                            int timer = getTimeInfected(p);
                            int level = getLevelInfection(p);
                            if (level == 0)
                            {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 240, 0, false, false), true);

                                if (playerDBConfig.getString("infected." + p.getUniqueId() + ".time") == null)
                                {
                                    playerDBConfig.set("infected." + p.getUniqueId() + ".time", 1);
                                }else {
                                    playerDBConfig.set("infected." + p.getUniqueId() + ".time", timer + 1);
                                }
                                cfgm.savePlayerDB();
                                if (timer >= 120)
                                {
                                    p.sendMessage("§8Votre infection a causé votre perte....");
                                    p.setHealth(0.0D);
                                }
                                else if (timer == 20 || timer == 40 || timer == 60 || timer == 80)
                                {
                                    p.sendMessage("§8Votre infection s'aggrave !");
                                    p.damage(3);
                                }
                                else if (timer == 100)
                                {
                                    p.sendMessage("§8Votre infection est très grave ! Cherchez vite une seringue !");
                                    p.damage(3);
                                }
                                else if (p.getHealth() > 2)
                                {
                                    p.damage(2);
                                }
                            }
                            else if (level == 1)
                            {
                                if (timer >= 30)
                                {
                                    p.sendMessage("§8Votre infection a causé votre perte....");
                                    p.setHealth(0.0D);
                                }
                            }
                        }
                    }
                }

            }
        }.runTaskTimer(this, 0, 100);

        WorkshopFunction wf = new WorkshopFunction(this, null);
        new BukkitRunnable()
        {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                for (Player p : getServer().getOnlinePlayers())
                {

                    if (!(p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR))
                    {
                        playTimePlayerAdd(p);
                    }
                    checkDiscretionPoint(p);
                    if (p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))
                    {
                        CobwebDamage.damagePlayerInCobweb(p);
                        boolean infected = playerDBConfig.getBoolean("infected." + p.getUniqueId() + ".state");
                        if (infected ) {
                            sendWorldBorderWarningDistancePacket(p, 1);
                            InGameUtilities.playPlayerSound(p, "entity.player.heartbeat", SoundCategory.MASTER, 1, 1);
                        }
                        else if (p.getHealth() < 7)
                        {
                            InGameUtilities.playPlayerSound(p, "entity.player.heartbeat", SoundCategory.MASTER, (float) ((7-p.getHealth())/7), 1);
                            double calcul = 10 *WorldUtilities.getWorldBorderDistance(p.getLocation(), Bukkit.getWorld(p.getLocation().getWorld().getName()).getWorldBorder().getCenter());

                            sendWorldBorderWarningDistancePacket(p, (float) ((7-p.getHealth())/7), calcul);
                        }
                        else
                        {
                            sendWorldBorderWarningDistancePacket(p, 0);
                        }

                        int safezone = cfgm.getPlayerDB().getInt("safezone." + p.getUniqueId() + ".time");

                        if (safezone > 0)
                        {
                            cfgm.getPlayerDB().set("safezone." + p.getUniqueId() + ".time", (safezone-1));
                            cfgm.savePlayerDB();
                            if (safezone % 15 == 0)
                            {
                                p.sendTitle("", "§cIl vous reste " + safezone + " secondes d'invincibilité");
                            }
                        }
                        else
                        {
                            if (safezone == 0)
                            {
                                p.sendMessage("§4Vous n'êtes plus invincible !");
                                cfgm.getPlayerDB().set("safezone." + p.getUniqueId() + ".time", -1);
                                p.setInvulnerable(false);
                                cfgm.savePlayerDB();
                            }
                        }
                    }
                    else
                    {
                        sendWorldBorderWarningDistancePacket(p, 0);
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
                for (DiscretionClass dc : HashMapManager.getDiscretionMap().values())
                {
                    dc.reduceTimeShooting();
                }
            }

        }.runTaskTimer(this, 0, 20);
        cfgm.savePlayerDB();
        new BukkitRunnable()
{

            Player byxis = Bukkit.getPlayer("Byxis_");
            Player fyrelix = Bukkit.getPlayer("Fyrelix");
            @Override
            public void run() {
                if (byxis != null && byxis.isOnline())
                {
                    boost(byxis);
                }
                if (fyrelix != null && fyrelix.isOnline())
                {
                    boost(fyrelix);
                }
                byxis = Bukkit.getPlayer("Byxis_");
                fyrelix = Bukkit.getPlayer("Fyrelix");
            }
        }.runTaskTimer(this, 0, 1);
        getLogger().info(" ");
        getLogger().info(Ansi.ansi().fg(Ansi.Color.GREEN).toString() + "================================");
    }

    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            p.closeInventory();
        }
        SaveAll(this);
        hashMapManager.saveBooster();
        getLogger().info(Ansi.ansi().fg(Ansi.Color.RED).toString() + "================================");
        getLogger().info(" ");
        getLogger().info(" ");
        getLogger().info(" ");
        getLogger().info(Ansi.ansi().fg(Ansi.Color.RED).toString() + "   Fireland is now disabled !");
        getLogger().info(" ");
        getLogger().info(" ");
        getLogger().info(" ");
        getLogger().info(Ansi.ansi().fg(Ansi.Color.RED).toString() + "================================");

        this.databaseManager.close();
    }

    /*void moveNextTick(Entity ent, Location loc)
	{
	    ent.setVelocity(loc.subtract(ent.getLocation()).toVector());
	}*/

    private void changeItemsStackSize()
    {
        modifyStackSize(Material.WHEAT_SEEDS, 4, false);
        modifyStackSize(Material.ECHO_SHARD, 1, false);
        modifyStackSize(Material.IRON_NUGGET, 32, false);
        modifyStackSize(Material.LEATHER, 1, false);
        modifyStackSize(Material.POTION, 2, false);
        modifyStackSize(Material.END_ROD, 1, false);
        modifyStackSize(Material.RABBIT_HIDE, 1, false);
    }

    public void dateListener()
    {
        Date now = new Date();
        @SuppressWarnings("deprecation")
        int today = now.getDate();
        int old = cfgm.getEnderchest().getInt("date");

        if (today != old)
        {
            cfgm.getEnderchest().set("date", today);
            cfgm.saveEnderchest();
            if (cfgm.getEnderchest().get("stockage") != null)
            {
                for (String s : cfgm.getEnderchest().getConfigurationSection("stockage").getKeys(true))
                {
                    if (cfgm.getEnderchest().getInt("stockage." + s + ".money") > 0)
                    {
                        if (cfgm.getEnderchest().getInt("stockage." + s + ".money") <= 50)
                        {
                            cfgm.getEnderchest().set("stockage." + s + ".money", 0);
                        }
                        else
                        {
                            cfgm.getEnderchest().set("stockage." + s + ".money", cfgm.getEnderchest().getInt("bank." + s + ".money")-50);
                        }
                    }
                    else
                    {
                        cfgm.getEnderchest().set(s, null);
                    }
                }
                cfgm.saveEnderchest();
            }
            if (cfgm.getKarmaDB().get("") != null)
            {
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
        if (economy != null)
        {
            eco = economy.getProvider();
        }
        return(eco != null);

    }

    public void loadConfigManager()
    {
        cfgm = new ConfigManager();
        cfgm.setup();
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

    private void checkDiscretionPoint(Player player)
{
        double discretion = 100;
        if (!HashMapManager.getDiscretionMap().containsKey(player.getUniqueId()))
        {
            hashMapManager.addDiscretionMap(player.getUniqueId());
        }

        if (HashMapManager.getDiscretionMap().get(player.getUniqueId()).isMoving())
        {
            discretion -= 30;
            if (player.isSneaking())
            {
                discretion += 20;
            }
        }
        if (HashMapManager.getDiscretionMap().get(player.getUniqueId()).isUsingCamo())
        {
            discretion += 15;
        }

        if (player.isSprinting() || player.isSwimming() || player.isClimbing())
        {
            discretion -= 50;

        }
        if (!player.isFlying() && !player.isOnGround() && !player.isClimbing())
        {
            discretion -= 30;
        }
        if ((player.getItemInHand() != null && player.getItemInHand().getType() == Material.END_ROD) || (player.getInventory().getItemInOffHand() != null && player.getInventory().getItemInOffHand().getType() == Material.END_ROD))
        {
            discretion -= 20;
            hashMapManager.getDiscretionMap().get(player.getUniqueId()).setUsingLights(true);
        }
        else
        {
            hashMapManager.getDiscretionMap().get(player.getUniqueId()).setUsingLights(false);
        }

        if (discretion > 100)
        {
            discretion = 100;
        }
        else if (discretion < 0)
        {
            discretion = 0;
        }

        hashMapManager.getDiscretionMap().get(player.getUniqueId()).setScore(discretion);
        //cfgm.getPlayerDB().set(sDiscretion+"score", discretion);
        //cfgm.savePlayerDB();
    }

    private void playTimePlayerAdd(Player p)
    {
        cfgm.getPlayerDB().set("playtime." + p.getUniqueId(), cfgm.getPlayerDB().getInt("playtime." + p.getUniqueId()) + 1);
    }

    /*private void playBorderPackets(Player p, boolean warn)
	{
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.WORLD_BORDER);
		packet.getWorldBorderActions().writeDefaults();
		packet.getDoubles().write(0, 29999984D);
		protocolManager.broadcastServerPacket(packet, p, false);
	}*/

    private void boost(Player p)
    {
        if (!(p.getName().equalsIgnoreCase("Byxis_") || p.getName().equalsIgnoreCase("Fyrelix")) || p.getInventory().getChestplate() == null || p.getInventory().getChestplate().getType() != Material.ELYTRA || p.getItemInHand().getType() != Material.AIR)
        {
            return;
        }
        if (p.isGliding())
        {
            Vector d = p.getLocation().getDirection();
            Vector v = p.getVelocity();
            if (p.isSneaking())
            {
                v.add(d.multiply(0.1));
            }
            else
            {
                v.add(d.multiply(0.01));
            }
            p.setVelocity(v);
        }

    }
}