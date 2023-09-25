package fr.byxis.player.intendant;

import fr.byxis.faction.housing.BunkerClass;
import fr.byxis.fireland.utilities.*;
import fr.byxis.player.intendant.menu.*;
import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.faction.faction.FactionInformation;
import fr.byxis.faction.faction.FactionPlayerInformation;
import fr.byxis.fireland.Fireland;
import fr.byxis.faction.zone.ZoneConfigFileManager;
import fr.byxis.faction.zone.zoneclass.FactionZoneInformation;
import fr.byxis.player.level.LevelStorage;
import fr.byxis.player.level.PlayerLevel;
import fr.byxis.player.quest.QuestManager;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static fr.byxis.player.intendant.menu.MenuEssaim.OpenEssaimMenu;
import static fr.byxis.player.intendant.menu.MenuIntendant.OpenIntendant;
import static fr.byxis.player.intendant.menu.MenuLevel.OpenLevelMenu;
import static fr.byxis.player.level.LevelStorage.getPlayerLevel;
import static fr.byxis.player.primes.PrimeEvent.addPrime;

public class Manager implements Listener {

    private final Fireland main;
    public Manager(Fireland main) {
        this.main = main;
    }

    @EventHandler
    public void ClickInventoryEvent(InventoryClickEvent e)
    {
        InventoryView inv = e.getView();
        if(e.getView().getPlayer() instanceof Player p)
        {
            if(inv.getTitle().contains("Intendant"))
            {
                /**       Click check        **/
                InventoryUtilities.clickManager(e);

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }

                /**       Click check        **/


                switch(itemclicked.getType())
                {
                    case EMERALD -> {
                        PermissionUtilities.commandExecutor(p, "ah", "crazyauctions.access");
                        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
                    }
                    case NETHERITE_SWORD -> {
                        MenuFaction.OpenFaction(main, p, true);
                    }
                    case FIREWORK_ROCKET -> {
                        MenuBooster.OpenBoosters(main,p);
                    }
                    case BELL -> {
                        MenuQuest.OpenQuestMenu(main, p);
                    }
                    case NETHER_STAR -> {
                        //TODO: Succès
                    }
                    case WHITE_BANNER, BLACK_BANNER -> {
                        OpenLevelMenu(main, p, 0);
                    }
                    case DEAD_FIRE_CORAL -> {
                        OpenEssaimMenu(main, p);
                    }
                    case PLAYER_HEAD -> {
                        MenuPrime.OpenPrime(main, p);
                    }
                }
            }
            else if(inv.getTitle().contains("Votre faction"))
            {
                /**       Click check        **/
                InventoryUtilities.clickManager(e);

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }

                /**       Click check        **/


                FactionFunctions ff = new FactionFunctions(main, p);
                FactionPlayerInformation infos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
                FactionInformation finfos = ff.getFactionInfo(infos.getFactionName());
                switch (itemclicked.getType()) {
                    case RED_STAINED_GLASS_PANE -> {
                        OpenIntendant(main, p);
                    }
                    case ANVIL -> {
                        MenuPerks.OpenPerks(main, p);
                    }
                    case PLAYER_HEAD -> {
                        MenuPlayerList.OpenPlayerList(main, p);
                    }
                    case BARRIER -> {
                        PermissionUtilities.commandExecutor(p, "faction leave", "fireland.command.faction.leave");
                        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
                        p.closeInventory();
                    }
                    case ENDER_CHEST -> {
                        if (ff.GetAmeliorationsUpgrades(finfos.getCurrentUpgrade())[2] != 0) {

                            InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
                            p.openInventory(ff.LoadStorage(infos.getFactionName(), finfos.getCurrentUpgrade()));
                        } else {
                            InGameUtilities.playPlayerSound(p, "item.shield.break", SoundCategory.BLOCKS, 1, 0);
                        }
                    } case GOLD_INGOT -> {
                        if (e.isRightClick() && infos.getRole() == 2) {
                            if (e.isShiftClick()) {
                                PermissionUtilities.commandExecutor(p, "faction withdraw 1000", "fireland.command.faction.withdraw");
                            } else {
                                PermissionUtilities.commandExecutor(p, "faction withdraw 100", "fireland.command.faction.withdraw");
                            }
                        } else if (e.isLeftClick()) {
                            if (e.isShiftClick()) {
                                PermissionUtilities.commandExecutor(p, "faction deposit 1000", "fireland.command.faction.deposit");
                            } else {
                                PermissionUtilities.commandExecutor(p, "faction deposit 100", "fireland.command.faction.deposit");
                            }
                        }
                        MenuFaction.OpenFaction(main, p, true);
                    }
                    case STONE -> {
                        if(!main.bunkerManager.getLoadedBunker().containsKey(finfos.getName()))
                        {
                            BunkerClass bunker = new BunkerClass(finfos.getName(), main);
                            main.bunkerManager.AddLoadedBunker(bunker);
                        }
                        main.bunkerManager.getLoadedBunker().get(finfos.getName()).Join(p);
                    }
                    case GRASS_BLOCK -> {
                        MenuZone.OpenZone(main, p);
                    }
                }
            }
            else if(inv.getTitle().contains("Membres de "))
            {
                /**       Click check        **/
                InventoryUtilities.clickManager(e);

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }

                /**       Click check        **/


                switch(itemclicked.getType())
                {
                    case RED_STAINED_GLASS_PANE -> {
                        MenuFaction.OpenFaction(main, p, true);
                    }
                }
            }
            else if(inv.getTitle().contains("Améliorations pour"))
            {
                /**       Click check        **/
                InventoryUtilities.clickManager(e);

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }

                /**       Click check        **/


                FactionFunctions ff = new FactionFunctions(main, p);
                FactionPlayerInformation pInfos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
                FactionInformation finfos = ff.getFactionInfo(pInfos.getFactionName());
                final Material color = BlockUtilities.getGlassPaneColor(finfos.getColorcode());

                switch(itemclicked.getType())
                {
                    case RED_STAINED_GLASS_PANE -> MenuFaction.OpenFaction(main, p, true);
                    case LIME_STAINED_GLASS_PANE -> {
                        PermissionUtilities.commandExecutor(p, "faction upgrade", "fireland.command.faction.upgrade");
                        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
                    }
                    case SHIELD -> {
                        if(!finfos.hasFriendlyFirePerk())
                        {
                            if(finfos.getCurrentUpgrade() < 2)
                            {
                                InGameUtilities.playPlayerSound(p, "item.shield.break", SoundCategory.BLOCKS, 1, 0);
                                InGameUtilities.sendPlayerError(p, "Vous devez être niveau de faction 2 pour débloquer cette amélioration.");
                            }
                            else
                            {
                                PermissionUtilities.commandExecutor(p, "faction perk friendly_fire", "fireland.command.faction.perk");
                                MenuPerks.OpenPerks(main, p);
                            }

                        }
                        return;
                    }
                    case NAME_TAG -> {
                        if(!finfos.hasNicknameVisibilityPerk())
                        {
                            if(finfos.getCurrentUpgrade() < 5)
                            {
                                InGameUtilities.playPlayerSound(p, "item.shield.break", SoundCategory.BLOCKS, 1, 0);
                                InGameUtilities.sendPlayerError(p, "Vous devez être niveau de faction 5 pour débloquer cette amélioration.");
                            }
                            else
                            {
                                PermissionUtilities.commandExecutor(p, "faction perk show_nickname", "fireland.command.faction.perk");
                                MenuPerks.OpenPerks(main, p);
                            }

                        }
                        else
                        {
                            InGameUtilities.playPlayerSound(p, "item.shield.break", SoundCategory.BLOCKS, 1, 0);
                        }
                        return;
                    }
                    case LEATHER -> {
                        if(!finfos.hasSkinPerk())
                        {
                            if(finfos.getCurrentUpgrade() < 4)
                            {
                                InGameUtilities.playPlayerSound(p, "item.shield.break", SoundCategory.BLOCKS, 1, 0);
                                InGameUtilities.sendPlayerError(p, "Vous devez être niveau de faction 4 pour débloquer cette amélioration.");
                            }
                            else
                            {
                                PermissionUtilities.commandExecutor(p, "faction perk has_skin", "fireland.command.faction.perk");
                                MenuPerks.OpenPerks(main, p);
                            }

                        }
                        else
                        {
                            InGameUtilities.playPlayerSound(p, "item.shield.break", SoundCategory.BLOCKS, 1, 0);
                        }
                        return;
                    }
                    case FLOWER_BANNER_PATTERN -> {
                        if(!finfos.DoShowPrefix())
                        {
                            if(finfos.getCurrentUpgrade() < 3)
                            {
                                InGameUtilities.playPlayerSound(p, "item.shield.break", SoundCategory.BLOCKS, 1, 0);
                                InGameUtilities.sendPlayerError(p, "Vous devez être niveau de faction 3 pour débloquer cette amélioration.");
                            }
                            else
                            {
                                PermissionUtilities.commandExecutor(p, "faction perk show_prefix", "fireland.command.faction.perk");
                                MenuPerks.OpenPerks(main, p);
                            }

                        }
                        else
                        {
                            InGameUtilities.playPlayerSound(p, "item.shield.break", SoundCategory.BLOCKS, 1, 0);
                        }
                        return;
                    }
                    case GRASS_BLOCK -> {
                        if(!finfos.hasCapturePerk())
                        {
                            if(finfos.getCurrentUpgrade() < 2)
                            {
                                InGameUtilities.playPlayerSound(p, "item.shield.break", SoundCategory.BLOCKS, 1, 0);
                                InGameUtilities.sendPlayerError(p, "Vous devez être niveau de faction 3 pour débloquer cette amélioration.");
                            }
                            else
                            {
                                PermissionUtilities.commandExecutor(p, "faction perk capture_perk", "fireland.command.faction.perk");
                                MenuPerks.OpenPerks(main, p);
                            }

                        }
                        else
                        {
                            InGameUtilities.playPlayerSound(p, "item.shield.break", SoundCategory.BLOCKS, 1, 0);
                        }
                        return;
                    }
                    case BEACON -> {
                        if(!finfos.hasZoneTpPerk())
                        {
                            if(finfos.getCurrentUpgrade() < 6)
                            {
                                InGameUtilities.playPlayerSound(p, "item.shield.break", SoundCategory.BLOCKS, 1, 0);
                                InGameUtilities.sendPlayerError(p, "Vous devez être niveau de faction 6 pour débloquer cette amélioration.");
                            }
                            else
                            {
                                PermissionUtilities.commandExecutor(p, "faction perk zone_tp", "fireland.command.faction.perk");
                                MenuPerks.OpenPerks(main, p);
                            }

                        }
                        else
                        {
                            InGameUtilities.playPlayerSound(p, "item.shield.break", SoundCategory.BLOCKS, 1, 0);
                        }
                        return;
                    }
                }
                if(itemclicked.getType() == color && p.hasPermission("fireland.command.faction.color") && !itemclicked.getItemMeta().getDisplayName().contains("Améliorer la faction au rang")
                        && !itemclicked.getItemMeta().getDisplayName().contains("Retour au menu Faction") && pInfos.getRole() == 2)
                {
                    MenuColor.OpenColorMenu(main, p, finfos);
                }
            }
            else if(inv.getTitle().contains("Changement de couleur"))
            {
                /**       Click check        **/
                InventoryUtilities.clickManager(e);

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }

                /**       Click check        **/


                if(itemclicked.getType() == Material.RED_STAINED_GLASS_PANE && itemclicked.getItemMeta().getDisplayName().contains("Retour à l'intendant"))
                {
                    MenuPerks.OpenPerks(main, p);
                }
                else if(itemclicked.getType() != Material.WHITE_STAINED_GLASS_PANE)
                {
                    FactionFunctions ff = new FactionFunctions(main, p);
                    FactionPlayerInformation pInfos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
                    FactionInformation finfos = ff.getFactionInfo(pInfos.getFactionName());
                    ff.SetColorCode(pInfos.getFactionName(), BasicUtilities.GetStringColor(itemclicked.getType()));
                    MenuColor.OpenColorMenu(main, p, finfos);
                }
            }
            else if(inv.getTitle().contains("Zones"))
            {

                /**       Click check        **/
                InventoryUtilities.clickManager(e);

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }

                /**       Click check        **/

                if(itemclicked.getType().toString().endsWith("_BANNER"))
                {
                    FactionFunctions ff = new FactionFunctions(main, p);

                    FactionPlayerInformation pInfos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
                    FactionInformation finfos = ff.getFactionInfo(pInfos.getFactionName());
                    List<FactionZoneInformation> list = main.zoneManager.GetFactionData(finfos.getName());
                    FactionZoneInformation zoneinfo = null;
                    for(FactionZoneInformation fz : list)
                    {
                        if(itemclicked.getItemMeta().getDisplayName().contains(fz.getFormattedName()))
                        {
                            zoneinfo = fz;
                            break;
                        }
                    }
                    if(zoneinfo != null && zoneinfo.getClaimedAt() != null && finfos.hasZoneTpPerk() && !main.hashMapManager.isTeleporting(p.getUniqueId()))
                    {
                        ZoneConfigFileManager configManager = new ZoneConfigFileManager(main);
                        configManager.notSafeSetup();
                        Location loc = new Location(Bukkit.getWorld("world"), configManager.config.getDouble("zone."+zoneinfo.getZoneName()+".teleportation.x"),
                                configManager.config.getDouble("zone."+zoneinfo.getZoneName()+".teleportation.y"),
                                configManager.config.getDouble("zone."+zoneinfo.getZoneName()+".teleportation.z"));
                        InGameUtilities.teleportPlayer(p, loc, 15, "gun.hub.helico");
                    }
                }

                if(itemclicked.getType() == Material.RED_STAINED_GLASS_PANE && itemclicked.getItemMeta().getDisplayName().contains("Retour au menu Faction"))
                {
                    MenuFaction.OpenFaction(main, p, true);
                }
            }
            else if(inv.getTitle().contains("Boosters"))
            {
                /**       Click check        **/

                InventoryUtilities.clickManager(e);

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }

                /**       Click check        **/


                if(itemclicked.getType() == Material.RED_STAINED_GLASS_PANE && itemclicked.getItemMeta().getDisplayName().contains("Retour à l'intendant"))
                {
                    OpenIntendant(main, p);
                }
                else if(itemclicked.getType() == Material.FIREWORK_ROCKET)
                {
                    ItemMeta meta = itemclicked.getItemMeta();
                    int duration = 1;
                    if(meta.getDisplayName().contains("3h"))
                    {
                        duration = 3;
                    }
                    else if(meta.getDisplayName().contains("5h"))
                    {
                        duration = 5;
                    }
                    int level = 1;
                    if(meta.getDisplayName().contains("Lvl. 2"))
                    {
                        level = 2;
                    }
                    else if(meta.getDisplayName().contains("Lvl. 3"))
                    {
                        level = 3;
                    }
                    PermissionUtilities.commandExecutor(p, "booster create "+level+" "+duration, "fireland.command.booster");
                    MenuBooster.OpenBoosters(main,p);
                }
            }
            else if(inv.getTitle().contains("Quêtes quotidiennes"))
            {
                /**       Click check        **/

                InventoryUtilities.clickManager(e);

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null)
                    return;

                /**       Click check        **/


                switch(itemclicked.getType())
                {
                    case RED_STAINED_GLASS_PANE -> {
                        OpenIntendant(main, p);
                    }
                    case STRUCTURE_VOID -> {
                        QuestManager.claimRewards(p);
                        MenuQuest.OpenQuestMenu(main, p);
                    }
                }
            }
            else if(inv.getTitle().equalsIgnoreCase("§4Primes"))
            {
                /**       Click check        **/

                InventoryUtilities.clickManager(e);

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null)
                    return;

                /**       Click check        **/


                switch(itemclicked.getType())
                {
                    case RED_STAINED_GLASS_PANE -> {
                        OpenIntendant(main, p);
                    }
                    case PLAYER_HEAD -> {
                        MenuPrime.OpenPrimePlayer(main, p, 1);
                    }
                    case CHEST -> {
                        MenuPrime.OpenPrimeList(main, p);
                    }
                }
            }
            else if(inv.getTitle().equalsIgnoreCase("§4Primes: Sélectionner un joueur"))
            {
                /**       Click check        **/

                InventoryUtilities.clickManager(e);

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null)
                    return;

                /**       Click check        **/


                switch(itemclicked.getType())
                {
                    case RED_STAINED_GLASS_PANE -> {
                        MenuPrime.OpenPrime(main, p);
                    }
                    case PLAYER_HEAD -> {
                        MenuPrime.OpenPrimeMoney(main, p, ChatColor.stripColor(itemclicked.getItemMeta().getDisplayName()), 0 );
                    }
                }
            }
            else if(inv.getTitle().equalsIgnoreCase("§4Primes: Ajouter un montant"))
            {
                /**       Click check        **/

                InventoryUtilities.clickManager(e);

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null)
                    return;

                /**       Click check        **/
                String player = ChatColor.stripColor(e.getClickedInventory().getItem(13).getItemMeta().getDisplayName());
                int money = Integer.parseInt(ChatColor.stripColor(e.getClickedInventory().getItem(13).getItemMeta().getLore().get(0).split(" ")[1]));

                switch(itemclicked.getType())
                {
                    case RED_STAINED_GLASS_PANE -> {
                        MenuPrime.OpenPrimePlayer(main, p, 1);
                    }
                    case RAW_GOLD -> {
                        if(itemclicked.getItemMeta().hasCustomModelData() && itemclicked.getItemMeta().getCustomModelData() == 1)
                        {
                            if(money-1000 > 0)
                            {
                                MenuPrime.OpenPrimeMoney(main, p, player, money -1000 );
                            }
                            else
                            {

                                MenuPrime.OpenPrimeMoney(main, p, player, 0 );
                            }
                        }
                        else
                        {
                            MenuPrime.OpenPrimeMoney(main, p, player, money +1000 );
                        }
                    }
                    case GOLD_INGOT -> {
                        if(itemclicked.getItemMeta().hasCustomModelData() && itemclicked.getItemMeta().getCustomModelData() == 1)
                        {
                            if(money-100 > 0)
                            {
                                MenuPrime.OpenPrimeMoney(main, p, player, money -100 );
                            }
                            else
                            {

                                MenuPrime.OpenPrimeMoney(main, p, player, 0 );
                            }
                        }
                        else
                        {
                            MenuPrime.OpenPrimeMoney(main, p, player, money +100 );
                        }
                    }
                    case GOLD_NUGGET -> {
                        if(itemclicked.getItemMeta().hasCustomModelData() && itemclicked.getItemMeta().getCustomModelData() == 1)
                        {
                            if(money-10 > 0)
                            {
                                MenuPrime.OpenPrimeMoney(main, p, player, money -10 );
                            }
                            else
                            {

                                MenuPrime.OpenPrimeMoney(main, p, player, 0 );
                            }
                        }
                        else
                        {
                            MenuPrime.OpenPrimeMoney(main, p, player, money +10 );
                        }
                    }
                    case STRUCTURE_VOID,BARRIER ->
                    {
                        if(Fireland.eco.getBalance(p) > money)
                        {
                            if(money > 0)
                            {
                                Fireland.eco.withdrawPlayer(p, money);
                                addPrime(BasicUtilities.getUuid(player), money);
                                if(Bukkit.getPlayer(player) != null && Bukkit.getPlayer(player).isOnline())
                                {
                                    InGameUtilities.sendPlayerError(Bukkit.getPlayer(player), "Une prime de "+money+"$ vous a été attribué.");
                                }
                                InGameUtilities.sendPlayerSucces(p, "Une prime de "+money+"$ a été attribué à "+player+".");
                            }
                            else
                            {
                                InGameUtilities.sendPlayerError(p, "Vous ne pouvez pas mettre une prime de 0$.");
                            }
                        }
                    }
                }
            }
            else if(inv.getTitle().equalsIgnoreCase("§4Primes disponibles"))
            {
                /**       Click check        **/

                InventoryUtilities.clickManager(e);

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null)
                    return;

                /**       Click check        **/


                switch(itemclicked.getType())
                {
                    case RED_STAINED_GLASS_PANE -> {
                        MenuPrime.OpenPrime(main, p);
                    }
                }
            }
            else if(inv.getTitle().equalsIgnoreCase("Calendrier des Essaims"))
            {
                /**       Click check        **/

                InventoryUtilities.clickManager(e);

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null)
                    return;

                /**       Click check        **/


                switch(itemclicked.getType())
                {
                    case RED_STAINED_GLASS_PANE -> {
                        OpenIntendant(main, p);
                    }
                }
            }
            else if(inv.getTitle().contains("Choisir sa nation"))
            {
                /**       Click check        **/
                InventoryUtilities.clickManager(e);

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }

                /**       Click check        **/


                if(itemclicked.getType().name().contains("_BANNER"))
                {
                    if(itemclicked.getItemMeta().getDisplayName().contains("Bannis"))
                    {
                        getPlayerLevel(p.getUniqueId()).setNation(LevelStorage.Nation.Bannis);
                        InGameUtilities.sendPlayerError(p, "Vous avez rejoint la nation des Bannis.");
                        InGameUtilities.playPlayerSound(p, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 1, 1);
                        for(Player player : Bukkit.getOnlinePlayers())
                        {
                            if(!player.getName().equalsIgnoreCase(p.getName()))
                                player.sendMessage("§cLe joueur "+p.getName()+" a rejoint la nation des Bannis.");
                        }
                    }
                    else if(itemclicked.getItemMeta().getDisplayName().contains("Neutre"))
                    {
                        getPlayerLevel(p.getUniqueId()).setNation(LevelStorage.Nation.Neutre);
                        InGameUtilities.sendPlayerInformation(p, "Vous avez rejoint la nation des Neutres.");
                        InGameUtilities.playPlayerSound(p, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 1, 1);
                        for(Player player : Bukkit.getOnlinePlayers())
                        {
                            if(!player.getName().equalsIgnoreCase(p.getName()))
                                player.sendMessage("§7Le joueur "+p.getName()+" a rejoint la nation des Neutres.");
                        }
                    }
                    else if(itemclicked.getItemMeta().getDisplayName().contains("Etat"))
                    {
                        getPlayerLevel(p.getUniqueId()).setNation(LevelStorage.Nation.Etat);
                        InGameUtilities.sendPlayerSucces(p, "Vous avez rejoint la nation de l'Etat.");
                        InGameUtilities.playPlayerSound(p, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 1, 1);
                        for(Player player : Bukkit.getOnlinePlayers())
                        {
                            if(!player.getName().equalsIgnoreCase(p.getName()))
                                player.sendMessage("§aLe joueur "+p.getName()+" a rejoint la nation de l'Etat.");
                        }
                    }
                    p.closeInventory();
                    PermissionUtilities.removePermission(p, "fireland.nation.change");
                }
            }
            else if(inv.getTitle().contains("Votre niveau : "))
            {
                /**       Click check        **/

                InventoryUtilities.clickManager(e);

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null)
                    return;

                /**       Click check        **/


                switch(itemclicked.getType())
                {
                    case RED_STAINED_GLASS_PANE, LIME_STAINED_GLASS_PANE -> {
                        OpenLevelMenu(main, p, getPageNumber(itemclicked)-1);
                    }
                    case DIAMOND_BLOCK, DIAMOND-> {
                        getPlayerLevel(p.getUniqueId()).ClaimRewards(main, Integer.parseInt(itemclicked.getItemMeta().getDisplayName().split(" ")[1]));
                        OpenLevelMenu(main, p, Integer.parseInt(itemclicked.getItemMeta().getDisplayName().split(" ")[1])/44);
                    }
                    case WHITE_BANNER , BLACK_BANNER-> {
                        PlayerLevel pl = getPlayerLevel(p.getUniqueId());
                        InGameUtilities.sendInteractivePlayerMessage(p, "§cPour changer de nation, cliquez sur ce message. Vous devez payer "+pl.GetJetonPriceNationChange()+ "§f\u26C1§c et "+pl.GetMoneyPriceNationChange()+"§f$", "/level changeNation", "§cCliquez ici pour changer de nation", ClickEvent.Action.RUN_COMMAND);
                        p.closeInventory();
                    }
                }
            }
        }
    }

    public int getPageNumber(ItemStack item) {
        // Remove color codes
        String strippedInput = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        // Extract the number before the slash
        String numberString = strippedInput.substring(strippedInput.indexOf('[') + 1, strippedInput.indexOf('/'));

        // Convert the number string to an integer and return it
        return Integer.parseInt(numberString);
    }

}
