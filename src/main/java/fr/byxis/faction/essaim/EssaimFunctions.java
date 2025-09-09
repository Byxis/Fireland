package fr.byxis.faction.essaim;

import fr.byxis.faction.essaim.essaimClass.EssaimClass;
import fr.byxis.faction.essaim.essaimClass.EssaimGroup;
import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.faction.faction.FactionInformation;
import fr.byxis.faction.faction.FactionPlayerInformation;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.fireland.utilities.PermissionUtilities;
import fr.byxis.fireland.utilities.TextUtilities;
import fr.byxis.jeton.JetonManager;
import fr.byxis.player.level.LevelStorage;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static fr.byxis.faction.essaim.EssaimManager.disableEssaim;
import static fr.byxis.fireland.restart.RestartManager.isServerRestartingSoon;
import static fr.byxis.fireland.utilities.InGameUtilities.debugp;
import static fr.byxis.player.level.LevelStorage.addPlayerXp;

public class EssaimFunctions {

    private static Fireland main;
    private static EssaimConfigManager configManager;
    private static FactionFunctions ff;

    public EssaimFunctions(Fireland _main, EssaimConfigManager _configManager) {
        if (EssaimFunctions.main == null)
        {
            EssaimFunctions.main = _main;
        }
        if (EssaimFunctions.configManager == null)
        {
            EssaimFunctions.configManager = _configManager;
        }
        if (EssaimFunctions.ff == null)
        {
            EssaimFunctions.ff = new FactionFunctions(_main, null);
        }
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
        configManager.getConfig().set(name + "." + point + ".position.x", location.getX());
        configManager.getConfig().set(name + "." + point + ".position.y", location.getY());
        configManager.getConfig().set(name + "." + point + ".position.z", location.getZ());
        configManager.getConfig().set(name + "." + point + ".position.world", location.getWorld().getName());
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
            InGameUtilities.sendInteractivePlayerMessage(p, spawner + " :§d§l" + xyz, "/tp " + p.getName() + " " + xyz, "§aCliquez ici pour vous téléporter", ClickEvent.Action.RUN_COMMAND);
        }
    }

    public static void setBlock(int x, int y, int z, Material material) {
        Bukkit.getWorld("essaim").getBlockAt(x, y, z).setType(material);
    }

    public static boolean isEssaimOccuped(String essaim) {
        if (!EssaimManager.getGroups().containsKey(essaim))
        {
            return false;
        }
        return !EssaimManager.getGroups().get(essaim).isEmpty();
    }

    public static boolean isPlayerInEssaim(String essaim, Player p)
    {
        if (!EssaimManager.getGroups().containsKey(essaim))
        {
            return false;
        }
        for (Player member : EssaimManager.getGroups().get(essaim).getMembers())
        {
            if (member.getName().equalsIgnoreCase(p.getName()))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isEssaimOpened(String essaim)
    {
        if (EssaimManager.getActiveEssaims().containsKey(essaim))
        {
            return !EssaimManager.getActiveEssaims().get(essaim).isClosed();
        }
        return true;
    }

    public static void createGroup(String essaim, Player p) {
        if (isEssaimOpened(essaim))
        {
            if (!PermissionUtilities.hasPermission(p.getUniqueId(), "fireland.essaim.access." + essaim))
            {
                InGameUtilities.sendPlayerError(p, "Vous n'avez pas complété la quête requise ou n'avez pas l'extension DLC.");
                return;
            }
            if (isEssaimOccuped(essaim)) {
                if (isPlayerInEssaim(essaim, p))
                {
                    openMenu(essaim, p);
                }
                else
                {
                    InGameUtilities.sendPlayerError(p, "Un groupe est déjà entré dans l'essaim !");
                }
            }
            else if (!main.getHashMapManager().isTeleporting(p.getUniqueId()) && EssaimManager.getActiveEssaims().containsKey(essaim))
            {
                InGameUtilities.setPlayerMoving(p.getUniqueId(), false);
                if (p.getGameMode() != GameMode.CREATIVE)
                {
                    teleportCreateEssaim(p, EssaimManager.getActiveEssaims().get(essaim).getHub(), "gun.hub.helico", 10, essaim);
                }
                else
                {
                    teleportCreateEssaim(p, EssaimManager.getActiveEssaims().get(essaim).getHub(), "gun.hub.helico", 0, essaim);
                }
            }
            else
            {
                InGameUtilities.sendPlayerError(p, "L'essaim n'est pas ouvert !");
            }
        }
        else if (isPlayerInEssaim(essaim, p))
        {
            openMenu(essaim, p);
        }
        else
        {
            InGameUtilities.sendPlayerError(p, "L'essaim n'est pas ouvert !");
        }
    }

    public static void leaveGroup(String essaim, Player p) {
        debugp("FBUG03 " + "leaveGroup " + EssaimManager.getGroups().get(essaim).getLeader().getName() + " " + p.getName());
        if (EssaimManager.getGroups().get(essaim).getLeader().getName().equalsIgnoreCase(p.getName()))
        {
            for (Player member : EssaimManager.getGroups().get(essaim).getMembers())
            {
                member.teleport(EssaimManager.getActiveEssaims().get(essaim).getEntry());
                member.closeInventory();
                InGameUtilities.sendPlayerError(member, "L'expédition a été abandonnée !");
            }
        }
        else
        {
            for (Player member : EssaimManager.getGroups().get(essaim).getMembers())
            {
                if (p.getName().equalsIgnoreCase(member.getName()))
                {
                    member.teleport(EssaimManager.getActiveEssaims().get(essaim).getEntry());
                    member.closeInventory();
                    InGameUtilities.sendPlayerError(member, "Vous avez abandonné l'expédition !");
                }
                else
                {
                    InGameUtilities.sendPlayerError(member, p.getName() + " a abandonné l'expédition !");
                }
            }
        }
        EssaimManager.getGroups().get(essaim).leaveGroup(p);
    }

    public static void inviteGroup(String essaim, Player p)
    {
        if (isEssaimOccuped(essaim))
        {
            if (!EssaimManager.getGroups().get(essaim).getLeader().getName().equalsIgnoreCase(p.getName()))
            {
                if (EssaimManager.getGroups().get(essaim).invitePlayer(p))
                {
                    InGameUtilities.sendPlayerInformation(EssaimManager.getGroups().get(essaim).getLeader(), "Vous avez invité " + p.getName() + " à votre groupe.");
                }
                else
                {
                    InGameUtilities.sendPlayerError(EssaimManager.getGroups().get(essaim).getLeader(), "Vous ne pouvez pas inviter ce joueur !");
                }
            }
            else
            {
                InGameUtilities.sendPlayerInformation(p, "Vous ne pouvez pas vous inviter vous-même !");
            }
        }
        else
        {
            InGameUtilities.sendPlayerError(p, "L'essaim n'est pas occupé!");
        }
    }

    public static boolean joinGroup(String essaim, Player p)
    {
        if (isEssaimOccuped(essaim))
        {
            if (!EssaimManager.getGroups().get(essaim).getMembers().contains(p))
            {
                if (ff.getFactionInfo(ff.playerFactionName(p)).getName().equalsIgnoreCase(ff.getFactionInfo(ff.playerFactionName(EssaimManager.getGroups().get(essaim).getLeader())).getName()))
                {
                    if (PermissionUtilities.hasPermission(p.getUniqueId(), "fireland.essaim.access." + essaim))
                    {
                        InGameUtilities.sendPlayerInformation(p, "Vous êtes entré dans le groupe.");
                        if (isServerRestartingSoon())
                            InGameUtilities.sendPlayerError(p, "Le serveur redémarre bientôt, lancer un essaim est fortement déconseillé.");
                        for (Player member : EssaimManager.getGroups().get(essaim).getMembers())
                        {
                            InGameUtilities.sendPlayerInformation(member, p.getName() + " a rejoint l'expédition.");
                            if (member.getOpenInventory().getTitle().contentEquals("Invitation :"))
                            {
                                EssaimFunctions.openInvitation(essaim, member);
                            }
                            else if (member.getOpenInventory().getTitle().contentEquals("Essaim :"))
                            {
                                EssaimFunctions.openMenu(essaim, member);
                            }
                        }
                        EssaimManager.getGroups().get(essaim).joinGroup(p);
                        return true;
                    }
                    else
                    {
                        InGameUtilities.sendPlayerError(p, "Vous n'avez pas complété la quête requise ou n'avez pas l'extension DLC.");
                    }
                }
                else
                {
                    InGameUtilities.sendPlayerError(p, "Vous n'êtes pas dans la même faction que la personne qui vous invite !");
                }

            }
            else
            {
                InGameUtilities.sendPlayerError(p, "Vous êtes déjà dans le groupe !");
            }
        } else
        {
            InGameUtilities.sendPlayerError(p, "Ce groupe n'existe plus !");
        }
        return false;
    }

    public static void openMenu(String essaim, Player p)
    {
        InGameUtilities.playWorldSound(p.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1, 1);
        Inventory essaimInv = Bukkit.createInventory(null, 27, "§8Essaim : §c" + TextUtilities.convertStorableToClean(essaim));
        setItemMenu(essaimInv, p, essaim);
        p.openInventory(essaimInv);
    }

    private static void setItemMenu(Inventory inv, Player p, String essaim) {
        if (EssaimManager.getActiveEssaims().get(essaim).isFinished())
        {
            inv.setItem(13, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cQuitter l'essaim", (short) 1));
        }
        else
        {
            ItemStack head = InventoryUtilities.getHead(EssaimManager.getGroups().get(essaim).getLeader().getUniqueId(), "§eMembres - §7(" + EssaimManager.getGroups().get(essaim).getMembers().size() + "/4)");
            ArrayList<String> lore = new ArrayList<>();
            for (Player member : EssaimManager.getGroups().get(essaim).getMembers())
            {
                lore.add("§8" + member.getName());
            }
            head.setLore(lore);
            inv.setItem(4, head);
            lore = new ArrayList<>();
            if (main.getEssaimManager().getConfig().contains(essaim + ".recommendations.1"))
                lore.add(main.getEssaimManager().getConfig().getString(essaim + ".recommendations.1"));

            if (main.getEssaimManager().getConfig().contains(essaim + ".recommendations.2"))
                lore.add(main.getEssaimManager().getConfig().getString(essaim + ".recommendations.2"));

            if (main.getEssaimManager().getConfig().contains(essaim + ".recommendations.3"))
                lore.add(main.getEssaimManager().getConfig().getString(essaim + ".recommendations.3"));

            inv.setItem(10, InventoryUtilities.setItemMetaLore(Material.LIME_STAINED_GLASS_PANE, "§aLancer l'expédition", (short) 1, lore));
            inv.setItem(13, InventoryUtilities.setItemMeta(Material.YELLOW_STAINED_GLASS_PANE, "§eInviter des membres de votre faction", (short) 1));
            inv.setItem(16, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cQuitter l'expédition", (short) 1));

        }
    }

    private static void creationGroup(String essaim, Player p)
    {
        if (isServerRestartingSoon())
            InGameUtilities.sendPlayerError(p, "Le serveur redémarre bientôt, lancer un essaim est fortement déconseillé.");
        main.getEssaimManager().resetEssaim(essaim);
        if (!EssaimManager.getGroups().containsKey(essaim)) {
            EssaimManager.getGroups().put(essaim, new EssaimGroup(essaim, p));
        } else {
            if (!EssaimManager.getGroups().get(essaim).getMembers().isEmpty())
            {
                if (ff.playerFactionName(p).equalsIgnoreCase(ff.playerFactionName(EssaimManager.getGroups().get(essaim).getLeader())))
                {
                    EssaimManager.getGroups().get(essaim).joinGroup(p);
                }
            }
            else
            {
                EssaimManager.getGroups().replace(essaim, new EssaimGroup(essaim, p));
                EssaimManager.getGroups().get(essaim).joinGroup(p);
            }
        }
        InGameUtilities.sendPlayerInformation(p, "Vous avez créé un groupe dans l'essaim !");
    }

    private static void teleportCreateEssaim(Player player, Location loc, String sound, int duration, String essaim)
    {
        player.playSound(player.getLocation(), "minecraft:" + sound, (float) 0.1, (float) 1);
        main.getHashMapManager().addTeleporting(player.getUniqueId());

        new BukkitRunnable() {
            private int i = -1;

            @Override
            public void run() {
                i++;
                if (InGameUtilities.isPlayerMoving(player.getUniqueId()))
                {
                    InGameUtilities.sendPlayerError(player, "Téléportation annulée !");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stopsound " + player.getName() + " * minecraft:" + sound);
                    main.getHashMapManager().removeTeleporting(player.getUniqueId());
                    cancel();
                }
                else
                {
                    if ((i % 5 == 0 && i != duration) || i == duration - 3 || i == duration - 2 || i == duration - 1)
                    {
                        InGameUtilities.sendPlayerInformation(player, "Téléportation dans " + (duration - i) + " secondes");
                    }
                    if (i == duration)
                    {
                        InGameUtilities.sendPlayerInformation(player, "Téléportation...");
                        player.teleport(loc);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a times 20 100 20");
                        creationGroup(essaim, player);
                        main.getHashMapManager().removeTeleporting(player.getUniqueId());
                        cancel();
                    }
                }

            }
        }.runTaskTimer(main, 0L, 20L);
    }

    public static void teleportJoinEssaim(Player player, Location loc, String sound, int duration, String essaim)
    {
        player.playSound(player.getLocation(), "minecraft:" + sound, (float) 0.1, (float) 1);
        InGameUtilities.setPlayerMoving(player.getUniqueId(), false);
        main.getHashMapManager().addTeleporting(player.getUniqueId());

        new BukkitRunnable() {
            private int i = -1;

            @Override
            public void run() {
                i++;
                if (InGameUtilities.isPlayerMoving(player.getUniqueId()))
                {
                    InGameUtilities.sendPlayerError(player, "Téléportation annulée !");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stopsound " + player.getName() + " * minecraft:" + sound);
                    main.getHashMapManager().removeTeleporting(player.getUniqueId());
                    cancel();
                }
                else
                {
                    if ((i % 5 == 0 && i != duration) || i == duration - 3 || i == duration - 2 || i == duration - 1)
                    {
                        InGameUtilities.sendPlayerInformation(player, "Téléportation dans " + (duration - i) + " secondes");
                    }
                    if (i == duration)
                    {
                        if (joinGroup(essaim, player))
                        {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a times 20 100 20");
                            InGameUtilities.sendPlayerInformation(player, "Téléportation...");
                            main.getHashMapManager().removeTeleporting(player.getUniqueId());
                            player.teleport(loc);
                        }
                        cancel();
                    }
                }

            }
        }.runTaskTimer(main, 0L, 20L);
    }

    public static void startEssaim(String name, int difficulty)
    {
        resetEssaim(name);
        main.getEssaimManager().setSolo(name);
        main.getEssaimManager().setDifficulty(name, difficulty);
        EssaimClass essaim = EssaimManager.getActiveEssaims().get(name);
        setBlock(essaim.getStart().blockX(), essaim.getStart().blockY(), essaim.getStart().blockZ(), Material.REDSTONE_BLOCK);
        EssaimManager.getGroups().get(name).startEssaim(difficulty);
        new BukkitRunnable() {
            @Override
            public void run() {
                resetEssaim(name);
            }
        }.runTaskLater(main, 5 * 20);
    }

    public static void resetEssaim(String name)
    {
        EssaimClass essaim = EssaimManager.getActiveEssaims().get(name);
        setBlock(essaim.getReset().blockX(), essaim.getReset().blockY(), essaim.getReset().blockZ(), Material.REDSTONE_BLOCK);
        if (!main.getEssaimManager().getActiveSpawners().keySet().isEmpty())
        {
            for (String spawner : main.getEssaimManager().getActiveSpawners().keySet())
            {
                if (main.getEssaimManager().getActiveSpawners().get(spawner).getEssaim().equalsIgnoreCase(essaim.getName()))
                {
                    main.getEssaimManager().getActiveSpawners().remove(spawner);
                }
            }
        }
    }

    public static void openInvitation(String essaim, Player p)
    {
        Inventory invitationInv = Bukkit.createInventory(null, 54, "§8Invitation : §c" + TextUtilities.convertStorableToClean(essaim));
        setItemInvitation(invitationInv, p, essaim);
        p.openInventory(invitationInv);
    }

    private static void setItemInvitation(Inventory inv, Player p, String essaim) {

        inv.setItem(0, InventoryUtilities.setItemMeta(Material.BOOK, "§eMembres - §7(" + EssaimManager.getGroups().get(essaim).getMembers().size() + "/4)", (short) 1));

        for (int i = 0; i < 9; i++)
        {
            inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            inv.setItem(i + 9, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, "§rCliquez sur un joueur pour l'inviter", (short) 1));
        }

        inv.setItem(17, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, "§rCliquez sur un joueur pour l'inviter", (short) 1));
        int i = 1;
        for (Player member : EssaimManager.getGroups().get(essaim).getMembers())
        {
            inv.setItem(i, InventoryUtilities.getHead(member.getUniqueId(), "§8" + member.getName()));
            i++;
        }
        i = 18;
        boolean show = true;
        for (FactionPlayerInformation member : ff.getPlayersFromFaction(ff.playerFactionName(p)))
        {
            show = true;
            for (Player player : EssaimManager.getGroups().get(essaim).getMembers())
            {
                if (player.getName().equalsIgnoreCase(member.getName()))
                {
                    show = false;
                    break;
                }
            }
            if (show)
            {
                inv.setItem(i, InventoryUtilities.getHead(member.getUuid(), "§8" + member.getName()));
                i++;
            }
        }
        inv.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour", (short) 0));

    }

    public static void finishEssaim(String essaimName)
    {
        if (EssaimManager.getActiveEssaims().containsKey(essaimName))
        {
            EssaimManager.getActiveEssaims().get(essaimName).setFinish();
            for (Player player : EssaimManager.getGroups().get(essaimName).getMembers())
            {
                InGameUtilities.sendPlayerInformation(player, "Vous avez terminé l'expédition ! L'essaim se fermera automatiquement dans quelques minutes.");
            }
        }

    }
    public static void unfinishEssaim(String essaimName)
    {
        if (EssaimManager.getActiveEssaims().containsKey(essaimName))
        {
            EssaimManager.getActiveEssaims().get(essaimName).unFinish();
            for (Player player : EssaimManager.getGroups().get(essaimName).getMembers())
            {
                InGameUtilities.sendPlayerInformation(player, "Vous avez activé une mission annexe, le décompte a été annulé.");

            }
        }
    }

    public static void looseEssaim(String essaimName, Player p)
    {
        FactionInformation pInfo = ff.getFactionInfo(ff.playerFactionName(p));
        String color = pInfo.getColorcode();
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (EssaimManager.getGroups().get(essaimName).getMembers().contains(player))
            {
                InGameUtilities.sendPlayerError(player, "Vous avez échoué l'expédition.");
            }
            else
            {
                InGameUtilities.sendPlayerError(player, "L'expédition de la faction " + color + pInfo.getName() + "§r§c dans l'essaim " + TextUtilities.convertStorableToClean(essaimName) + " a échoué.");
            }
            InGameUtilities.playPlayerSound(player, "entity.wither.death", SoundCategory.AMBIENT, 1, 1);
        }
        EssaimManager.getGroups().remove(essaimName);
        main.getEssaimManager().resetEssaim(essaimName);
    }

    public static void leaveFinishedEssaim(String essaimName, Player p, boolean forced)
    {
        FactionInformation pInfo = ff.getFactionInfo(ff.playerFactionName(p));
        String color = pInfo.getColorcode();
        if (EssaimManager.getGroups().get(essaimName).getLeader().getName().equals(p.getName()) || forced)
        {
            for (Player player : Bukkit.getOnlinePlayers())
            {
                if (EssaimManager.getGroups().get(essaimName).getMembersName().contains(player.getName()))
                {
                    if (forced)
                    {
                        InGameUtilities.sendPlayerError(player, "Vous avez été expulsé de l'essaim, par conséquent, l'expédition est terminée.");
                    }
                    else
                    {
                        if (EssaimManager.getGroups().get(essaimName).getLeader().getName().equals(player.getName()))
                        {
                            InGameUtilities.sendPlayerInformation(player, "Vous avez quitté l'essaim, par conséquent, l'expédition est terminée.");
                        }
                        else
                        {
                            InGameUtilities.sendPlayerInformation(player, "Le leader a quitté l'essaim, par conséquent, l'expédition est terminée.");
                        }

                    }
                    addPlayerXp(player.getUniqueId(), 200, LevelStorage.Nation.Etat);
                    JetonManager.addJetonsPlayer(player.getUniqueId(), EssaimManager.getGroups().get(essaimName).getRewardJetons());
                    InGameUtilities.sendPlayerInformation(player, "Vous avez gagné §d" + EssaimManager.getGroups().get(essaimName).getRewardJetons() + "§r§7 jetons !");
                    player.teleport(EssaimManager.getActiveEssaims().get(essaimName).getEntry());
                }
                else
                {
                    InGameUtilities.sendPlayerInformation(player, "L'essaim " + TextUtilities.convertStorableToClean(essaimName) + " a été pacifié par la faction " + color + pInfo.getName() + "§r§7.");
                }
                InGameUtilities.playPlayerSound(player, "gun.hud.boss_killed", SoundCategory.AMBIENT, 1, 1);
            }
            disableEssaim(essaimName);
            EssaimManager.getActiveEssaims().remove(essaimName);
            EssaimManager.getGroups().remove(essaimName);
        }
        else
        {
            EssaimManager.getGroups().get(essaimName).finish(p);
            for (Player player : EssaimManager.getGroups().get(essaimName).getMembers())
            {
                if (player.getName().equalsIgnoreCase(p.getName()))
                {
                    addPlayerXp(player.getUniqueId(), 200, LevelStorage.Nation.Etat);
                    InGameUtilities.sendPlayerInformation(player, "Vous avez quitté l'essaim");
                    JetonManager.addJetonsPlayer(p.getUniqueId(), EssaimManager.getGroups().get(essaimName).getRewardJetons());
                    InGameUtilities.sendPlayerInformation(player, "Vous avez gagné §d" + EssaimManager.getGroups().get(essaimName).getRewardJetons() + " §r§7 jetons !");
                    player.teleport(EssaimManager.getActiveEssaims().get(essaimName).getEntry());
                }
                else
                {
                    InGameUtilities.sendPlayerInformation(player, "Le joueur " + p.getName() + " a quitté l'essaim");
                }
            }
        }
    }

    public static void saveEssaim()
    {
        for (String essaim : EssaimManager.getConfigManager().getConfig().getConfigurationSection("").getKeys(false))
        {
            EssaimManager.getConfigManager().getConfig().set(essaim + ".closed", !EssaimManager.getActiveEssaims().containsKey(essaim));
        }
        EssaimManager.getConfigManager().save();
    }

    public static void openStartingMenu(String essaim, Player p)
    {
        InGameUtilities.playWorldSound(p.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1, 1);
        Inventory essaimInv = Bukkit.createInventory(null, 27, "§8Lancement de §c" + TextUtilities.convertStorableToClean(essaim));
        setStartingMenu(essaimInv, p, essaim);
        p.openInventory(essaimInv);
    }

    private static void setStartingMenu(Inventory inv, Player p, String essaim) {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("§8Une issue s'offre à vous. Vous garderez votre inventaire");
        lore.add("§8en cas de mort mais la récompense finale sera amoindrie.");
        inv.setItem(10, InventoryUtilities.setItemMetaLore(Material.PLAYER_HEAD, "§eÉchappatoire", (short) 1, lore));

        lore = new ArrayList<String>();
        lore.add("§8Aucun retour possible. Vous perdrez votre inventaire");
        lore.add("§8en cas de mort mais la récompense finale sera augmentée.");
        inv.setItem(13, InventoryUtilities.setItemMetaLore(Material.SKELETON_SKULL, "§cêtreinte Mortelle", (short) 1, lore));

        lore = new ArrayList<String>();
        lore.add("§8Personne ne peux vous retenir de choisir cette voie, mais");
        lore.add("§8restez sur vos gardes. La récompense finale sera maximale et");
        lore.add("§8suivie d'un bonus de 5 jetons.");
        lore.add("§cÀ venir bientôt...");
        inv.setItem(16, InventoryUtilities.setItemMetaLore(Material.WITHER_SKELETON_SKULL, "§4§lLune de Sang", (short) 1, lore));
    }

    public static EssaimConfigManager getConfigManager()
    {
        return configManager;
    }
}
