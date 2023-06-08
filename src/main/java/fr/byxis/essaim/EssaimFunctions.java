package fr.byxis.essaim;

import fr.byxis.essaim.essaimClass.EssaimClass;
import fr.byxis.essaim.essaimClass.EssaimGroup;
import fr.byxis.faction.FactionFunctions;
import fr.byxis.faction.FactionInformation;
import fr.byxis.faction.FactionPlayerInformation;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.fireland.utilities.TextUtilities;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class EssaimFunctions {

    private static Fireland main;
    private static EssaimConfigManager configManager;
    private static FactionFunctions ff;

    public EssaimFunctions(Fireland main, EssaimConfigManager configManager) {
        EssaimFunctions.main = main;
        EssaimFunctions.configManager = configManager;
        EssaimFunctions.ff = new FactionFunctions(main, null);
    }

    public static void createNewSpawner(String essaim, String name, String mob, int amount, int activationDelay, int spawnDelay, String command, Location location, boolean isAffectedByDifficulty) {
        configManager.getConfig().set(essaim + ".spawners." + name + ".type", mob);
        configManager.getConfig().set(essaim + ".spawners." + name + ".amount", amount);
        configManager.getConfig().set(essaim + ".spawners." + name + ".command", command);
        configManager.getConfig().set(essaim + ".spawners." + name + ".activation-delay", activationDelay);
        configManager.getConfig().set(essaim + ".spawners." + name + ".spawn-delay", spawnDelay);
        configManager.getConfig().set(essaim + ".spawners." + name + ".position.x", Math.round(location.getX()));
        configManager.getConfig().set(essaim + ".spawners." + name + ".position.y", Math.round(location.getY()));
        configManager.getConfig().set(essaim + ".spawners." + name + ".position.z", Math.round(location.getZ()));
        configManager.getConfig().set(essaim + ".spawners." + name + ".affected-by-difficulty", isAffectedByDifficulty);
        configManager.save();
    }

    public static void removeSpawner(String essaim, String name) {
        configManager.getConfig().set(essaim + ".spawners." + name, null);
        configManager.save();
    }



    public static void createNewEssaim(String name, String region, int day, int hour, Location location) {
        configManager.getConfig().set(name + ".region", region);
        configManager.getConfig().set(name + ".day", day);
        configManager.getConfig().set(name + ".hour", hour);
        configManager.getConfig().set(name + ".hub.position.x", location.getX());
        configManager.getConfig().set(name + ".hub.position.y", location.getY());
        configManager.getConfig().set(name + ".hub.position.z", location.getZ());
        configManager.getConfig().set(name + ".hub.position.world", location.getWorld().getName());
        configManager.save();
    }
    public static void setPoint(String name, Location location, String point) {
        configManager.getConfig().set(name + "."+point+".position.x", location.getX());
        configManager.getConfig().set(name + "."+point+".position.y", location.getY());
        configManager.getConfig().set(name + "."+point+".position.z", location.getZ());
        configManager.getConfig().set(name + "."+point+".position.world", location.getWorld().getName());
        configManager.save();
    }

    public static void deleteEssaim(String name) {
        configManager.getConfig().set(name, null);
        configManager.save();
    }

    public static void sendAllSpawners(String name, Player p) {
        for (String spawner : configManager.getConfig().getConfigurationSection(name + ".spawners").getKeys(false)) {
            String xyz = configManager.getConfig().getString(name + ".spawners." + spawner + ".position.x") + " "
                    + configManager.getConfig().getString(name + ".spawners." + spawner + ".position.y") + " "
                    + configManager.getConfig().getString(name + ".spawners." + spawner + ".position.z");
            BasicUtilities.sendInteractivePlayerMessage(p, spawner + " : §d§l" + xyz, "/tp " + p.getName() + " " + xyz, "§aCliquez ici pour vous téléporter", ClickEvent.Action.RUN_COMMAND);
        }
    }

    public static void setBlock(int x, int y, int z, Material material) {
        Bukkit.getWorld("essaim").getBlockAt(x, y, z).setType(material);
    }

    public static boolean isEssaimOccuped(String essaim) {
        if(!main.essaimManager.groups.containsKey(essaim))
        {
            return false;
        }
        return !main.essaimManager.groups.get(essaim).isEmpty();
    }

    public static boolean isPlayerInEssaim(String essaim, Player p)
    {
        if(!main.essaimManager.groups.containsKey(essaim))
        {
            return false;
        }
        for(Player member : main.essaimManager.groups.get(essaim).getMembers())
        {
            if(member.getName().equalsIgnoreCase(p.getName()))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isEssaimOpened(String essaim)
    {
        return main.essaimManager.activeEssaims.containsKey(essaim);
    }

    public static void createGroup(String essaim, Player p) {
        if(isEssaimOpened(essaim))
        {
            if (isEssaimOccuped(essaim)) {
                if(isPlayerInEssaim(essaim, p))
                {
                    openMenu(essaim, p);
                }
                else
                {
                    BasicUtilities.sendPlayerError(p, "Un groupe est déjà entré dans l'essaim !");
                }
            } else if (!main.hashMapManager.isTeleporting(p.getUniqueId())){
                InGameUtilities.setPlayerMoving(p, false);
                if(p.getGameMode() != GameMode.CREATIVE)
                {
                    teleportCreateEssaim(p, main.essaimManager.activeEssaims.get(essaim).getHub(), "gun.hub.helico",10, essaim);
                }
                else
                {
                    teleportCreateEssaim(p, main.essaimManager.activeEssaims.get(essaim).getHub(), "gun.hub.helico",0, essaim);
                }
            }
        }
        else
        {
            BasicUtilities.sendPlayerError(p, "L'essaim n'est pas ouvert !");
        }
    }

    public static void leaveGroup(String essaim, Player p) {
        if(main.essaimManager.groups.get(essaim).getLeader().getName().equalsIgnoreCase(p.getName()))
        {
            for(Player member : main.essaimManager.groups.get(essaim).getMembers())
            {
                member.teleport(main.essaimManager.activeEssaims.get(essaim).getEntry());
                member.closeInventory();
                BasicUtilities.sendPlayerError(member, "L'expédition a été abandonnée !");
            }
        }
        else
        {
            for(Player member : main.essaimManager.groups.get(essaim).getMembers())
            {
                if(p.getName().equalsIgnoreCase(member.getName()))
                {
                    member.teleport(main.essaimManager.activeEssaims.get(essaim).getEntry());
                    member.closeInventory();
                    BasicUtilities.sendPlayerError(member, "Vous avez abandonné l'expédition !");
                }
                else
                {
                    BasicUtilities.sendPlayerError(member, p.getName()+" a abandonné l'expédition !");
                }
            }
        }
        main.essaimManager.groups.get(essaim).leaveGroup(p);
    }

    public static void inviteGroup(String essaim, Player p)
    {
        if (isEssaimOccuped(essaim))
        {
            if(!main.essaimManager.groups.get(essaim).getLeader().getName().equalsIgnoreCase(p.getName()))
            {
                if(main.essaimManager.groups.get(essaim).invitePlayer(p))
                {
                    BasicUtilities.sendPlayerInformation(main.essaimManager.groups.get(essaim).getLeader(), "Vous avez invité "+p.getName()+" à votre groupe.");
                }
                else
                {
                    BasicUtilities.sendPlayerError(main.essaimManager.groups.get(essaim).getLeader(), "Vous ne pouvez pas inviter ce joueur !");
                }
            }
            else
            {
                BasicUtilities.sendPlayerInformation(p, "Vous ne pouvez pas vous inviter vous-même !");
            }
        }
        else
        {
            BasicUtilities.sendPlayerError(p, "L'essaim n'est pas occupé!");
        }
    }

    public static boolean joinGroup(String essaim, Player p)
    {
        if (isEssaimOccuped(essaim))
        {
            if (!main.essaimManager.groups.get(essaim).getMembers().contains(p))
            {
                if(ff.getFactionInfo(ff.playerFactionName(p)).getName().equalsIgnoreCase(ff.getFactionInfo(ff.playerFactionName(main.essaimManager.groups.get(essaim).getLeader())).getName()))
                {
                    BasicUtilities.sendPlayerInformation(p, "Vous êtes entré dans le groupe.");

                    for(Player member : main.essaimManager.groups.get(essaim).getMembers())
                    {
                        BasicUtilities.sendPlayerInformation(member, p.getName()+" a rejoint l'expédition.");
                        if(member.getOpenInventory().getTitle().contentEquals("Invitation :"))
                        {
                            EssaimFunctions.openInvitation(essaim, member);
                        }
                        else if(member.getOpenInventory().getTitle().contentEquals("Essaim :"))
                        {
                            EssaimFunctions.openMenu(essaim, member);
                        }
                    }
                    main.essaimManager.groups.get(essaim).joinGroup(p);
                    return true;
                }
                else
                {
                    BasicUtilities.sendPlayerError(p, "Vous n'êtes pas dans la même faction que la personne qui vous invite !");
                }

            }
            else
            {
                BasicUtilities.sendPlayerError(p, "Vous êtes déjà dans le groupe !");
            }
        } else
        {
            BasicUtilities.sendPlayerError(p, "Ce groupe n'existe plus !");
        }
        return false;
    }

    public static void openMenu(String essaim, Player p)
    {
        Inventory essaimInv = Bukkit.createInventory(null, 27, "§8Essaim : §c"+ TextUtilities.convertStorableToClean(essaim));
        setItemMenu(essaimInv, p, essaim);
        p.openInventory(essaimInv);
    }

    private static void setItemMenu(Inventory inv, Player p, String essaim) {
        if(main.essaimManager.activeEssaims.get(essaim).isFinished())
        {
            inv.setItem(13, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cQuitter l'essaim", (short) 1));
        }
        else
        {
            ItemStack head = InventoryUtilities.GetHead(main.essaimManager.groups.get(essaim).getLeader().getUniqueId(), "§eMembres - §7("+main.essaimManager.groups.get(essaim).getMembers().size()+"/4)");
            ArrayList<String> lore = new ArrayList<>();
            for(Player member : main.essaimManager.groups.get(essaim).getMembers())
            {
                lore.add("§8"+member.getName());
            }
            head.setLore(lore);
            inv.setItem(4, head);
            inv.setItem(10, InventoryUtilities.setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§aLancer l'expédition", (short) 1));
            inv.setItem(13, InventoryUtilities.setItemMeta(Material.YELLOW_STAINED_GLASS_PANE, "§eInviter des membres de votre faction", (short) 1));
            inv.setItem(16, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cQuitter l'expédition", (short) 1));

        }
    }

    private static void creationGroup(String essaim, Player p)
    {
        if (!main.essaimManager.groups.containsKey(essaim)) {
            main.essaimManager.groups.put(essaim, new EssaimGroup(essaim, p));
        } else {

            main.essaimManager.groups.get(essaim).joinGroup(p);
        }
        BasicUtilities.sendPlayerInformation(p, "Vous avez créé un groupe dans l'essaim !");
    }

    private static void teleportCreateEssaim(Player player, Location loc, String sound, int duration, String essaim)
    {
        player.playSound(player.getLocation(), "minecraft:"+sound, (float) 0.1, (float) 1);
        main.hashMapManager.addTeleporting(player.getUniqueId());

        new BukkitRunnable() {
            private int i = -1;

            @Override
            public void run() {
                i++;
                if(InGameUtilities.getPlayerMoving(player)){
                    BasicUtilities.sendPlayerError(player,"Téléportation annulée !");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stopsound "+player.getName()+" * minecraft:"+sound);
                    main.hashMapManager.removeTeleporting(player.getUniqueId());
                    cancel();
                }
                else
                {
                    if((i%5 == 0 && i != duration) || i == duration-3 ||i  == duration-2 || i  == duration-1)
                    {
                        BasicUtilities.sendPlayerInformation(player,"Téléportation dans " +(duration-i)+" secondes");
                    }
                    if(i == duration)
                    {
                        BasicUtilities.sendPlayerInformation(player,"Téléportation...");
                        player.teleport(loc);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a times 20 100 20");
                        creationGroup(essaim, player);
                        main.hashMapManager.removeTeleporting(player.getUniqueId());
                        cancel();
                    }
                }

            }
        }.runTaskTimer(main, 0L, 20L);
    }

    public static void teleportJoinEssaim(Player player, Location loc, String sound, int duration, String essaim)
    {
        player.playSound(player.getLocation(), "minecraft:"+sound, (float) 0.1, (float) 1);
        main.hashMapManager.addTeleporting(player.getUniqueId());

        new BukkitRunnable() {
            private int i = -1;

            @Override
            public void run() {
                i++;
                if(InGameUtilities.getPlayerMoving(player)){
                    BasicUtilities.sendPlayerError(player,"Téléportation annulée !");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stopsound "+player.getName()+" * minecraft:"+sound);
                    main.hashMapManager.removeTeleporting(player.getUniqueId());
                    cancel();
                }
                else
                {
                    if((i%5 == 0 && i != duration) || i == duration-3 ||i  == duration-2 || i  == duration-1)
                    {
                        BasicUtilities.sendPlayerInformation(player,"Téléportation dans " +(duration-i)+" secondes");
                    }
                    if(i == duration)
                    {
                        if(joinGroup(essaim, player))
                        {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a times 20 100 20");
                            BasicUtilities.sendPlayerInformation(player,"Téléportation...");
                            main.hashMapManager.removeTeleporting(player.getUniqueId());
                            player.teleport(loc);
                        }
                        cancel();
                    }
                }

            }
        }.runTaskTimer(main, 0L, 20L);
    }

    public static void startEssaim(String name)
    {
        resetEssaim(name);
        EssaimClass essaim = main.essaimManager.activeEssaims.get(name);
        setBlock(essaim.getStart().blockX(), essaim.getStart().blockY(), essaim.getStart().blockZ(), Material.REDSTONE_BLOCK);
        main.essaimManager.groups.get(name).setDifficulty();
    }

    public static void resetEssaim(String name)
    {
        EssaimClass essaim = main.essaimManager.activeEssaims.get(name);
        setBlock(essaim.getReset().blockX(), essaim.getReset().blockY(), essaim.getReset().blockZ(), Material.REDSTONE_BLOCK);
    }

    public static void openInvitation(String essaim, Player p)
    {
        Inventory invitationInv = Bukkit.createInventory(null, 54, "§8Invitation : §c"+ TextUtilities.convertStorableToClean(essaim));
        setItemInvitation(invitationInv, p, essaim);
        p.openInventory(invitationInv);
    }

    private static void setItemInvitation(Inventory inv, Player p, String essaim) {

        inv.setItem(0, InventoryUtilities.setItemMeta(Material.BOOK, "§eMembres - §7("+main.essaimManager.groups.get(essaim).getMembers().size()+"/4)", (short) 1));

        for(int i=0;i<9;i++) {
            inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            inv.setItem(i + 9, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, "§rCliquez sur un joueur pour l'inviter", (short) 1));
        }

        inv.setItem(17, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, "§rCliquez sur un joueur pour l'inviter", (short) 1));
        int i = 1;
        for(Player member : main.essaimManager.groups.get(essaim).getMembers())
        {
            inv.setItem(i, InventoryUtilities.GetHead(member.getUniqueId(),"§8"+member.getName()));
            i++;
        }
        i = 18;
        boolean show = true;
        for(FactionPlayerInformation member : ff.getPlayersFromFaction(ff.playerFactionName(p)))
        {
            show = true;
            for(Player player : main.essaimManager.groups.get(essaim).getMembers())
            {
                if(player.getName().equalsIgnoreCase(member.getName()))
                {
                    show = false;
                    break;
                }
            }
            if(show)
            {
                inv.setItem(i, InventoryUtilities.GetHead(member.getUuid(),"§8"+member.getName()));
                i++;
            }
        }
        inv.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour", (short) 0));

    }

    public static void finishEssaim(String essaimName)
    {
        main.essaimManager.activeEssaims.get(essaimName).setFinish();
        String faction = ff.playerFactionName(main.essaimManager.groups.get(essaimName).getLeader());
        String color = ff.getFactionInfo(faction).getColorcode();
        for(Player player : Bukkit.getOnlinePlayers())
        {
            if(main.essaimManager.groups.get(essaimName).getMembers().contains(player))
            {
                BasicUtilities.sendPlayerInformation(player,"Vous avez terminé l'expédition ! L'essaim se fermera automatiquement dans 8 minutes.");
            }

        }
    }

    public static void looseEssaim(String essaimName, Player p)
    {
        FactionInformation pInfo = ff.getFactionInfo(ff.playerFactionName(p));
        String color = pInfo.getColorcode();
        for(Player player : Bukkit.getOnlinePlayers())
        {
            if(main.essaimManager.groups.get(essaimName).getMembers().contains(player))
            {
                BasicUtilities.sendPlayerError(player,"Vous avez échoué l'expédition.");
            }
            else
            {
                BasicUtilities.sendPlayerError(player,"L'expédition de la faction "+color+pInfo.getName()+"§r§c dans l'essaim "+TextUtilities.convertStorableToClean(essaimName)+" a échoué.");
            }
            BasicUtilities.playSound(player, "minecraft:entity.wither.spawn");
        }
        main.essaimManager.groups.remove(essaimName);
    }

    public static void leaveFinishedEssaim(String essaimName, Player p)
    {
        FactionInformation pInfo = ff.getFactionInfo(ff.playerFactionName(p));
        String color = pInfo.getColorcode();
        if(main.essaimManager.groups.get(essaimName).getLeader().getName().equals(p.getName()) )
        {
            for(Player player : Bukkit.getOnlinePlayers())
            {
                if(main.essaimManager.groups.get(essaimName).getMembers().contains(player))
                {
                    if(main.essaimManager.groups.get(essaimName).getLeader().getName().equals(player.getName()))
                    {
                        BasicUtilities.sendPlayerInformation(player, "Vous avez quittez l'essaim, par conséquent, l'expédition est terminée");
                    }
                    else
                    {
                        BasicUtilities.sendPlayerInformation(player, "Le leader a quitté l'essaim, par conséquent, l'expédition est terminée !");
                    }
                    player.teleport(main.essaimManager.activeEssaims.get(essaimName).getEntry());
                }
                else
                {
                    BasicUtilities.sendPlayerInformation(player,"L'essaim "+TextUtilities.convertStorableToClean(essaimName)+" a été pacifié par la faction "+color+pInfo.getName()+"§r§7.");
                }
                BasicUtilities.playSound(player, "minecraft:gun.hud.boss_killed");
            }
            main.essaimManager.activeEssaims.remove(essaimName);
            main.essaimManager.groups.remove(essaimName);
        }
        else
        {
            main.essaimManager.groups.get(essaimName).finish(p);
            for(Player player : main.essaimManager.groups.get(essaimName).getMembers())
            {
                if(player.getName().equalsIgnoreCase(p.getName()))
                {
                    BasicUtilities.sendPlayerInformation(player, "Vous avez quitté l'essaim");
                    player.teleport(main.essaimManager.activeEssaims.get(essaimName).getEntry());
                }
                else
                {
                    BasicUtilities.sendPlayerInformation(player, "Le joueur "+p.getName()+" a quitté l'essaim");
                }
            }
        }
    }
}
