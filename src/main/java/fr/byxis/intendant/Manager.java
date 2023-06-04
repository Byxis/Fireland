package fr.byxis.intendant;

import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.intendant.menu.*;
import fr.byxis.faction.FactionFunctions;
import fr.byxis.faction.FactionInformation;
import fr.byxis.faction.FactionPlayerInformation;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.BlockUtilities;
import fr.byxis.zone.ZoneConfigFileManager;
import fr.byxis.zone.zoneclass.FactionZoneInformation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

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

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }
                clickManager(e);

                /**       Click check        **/


                switch(itemclicked.getType())
                {
                    case EMERALD -> main.commandExecutor(p, "ah", "crazyauctions.access");
                    case DIAMOND_SWORD -> MenuFaction.OpenFaction(main, p, true);
                    case FIREWORK_ROCKET -> MenuBooster.OpenBoosters(main,p);
                }
            }
            else if(inv.getTitle().contains("Votre faction"))
            {
                /**       Click check        **/

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }
                clickManager(e);

                /**       Click check        **/


                FactionFunctions ff = new FactionFunctions(main, p);
                FactionPlayerInformation infos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
                FactionInformation finfos = ff.getFactionInfo(infos.getFactionName());
                switch(itemclicked.getType())
                {
                    case RED_STAINED_GLASS_PANE :
                        MenuIntendant.OpenIntendant(main, p);
                        break;
                    case ANVIL:
                        MenuPerks.OpenPerks(main, p);
                        break;
                    case PLAYER_HEAD:
                        MenuPlayerList.OpenPlayerList(main, p);
                        break;
                    case BARRIER :
                        main.commandExecutor(p, "faction leave", "fireland.command.faction.leave");
                        p.closeInventory();
                        break;
                    case ENDER_CHEST:
                        if(ff.GetAmeliorationsUpgrades(finfos.getCurrentUpgrade())[2] != 0)
                        {
                            p.openInventory(ff.LoadStorage(infos.getFactionName(), finfos.getCurrentUpgrade()));
                        }
                        break;
                    case GOLD_INGOT :
                        if(e.isRightClick() && infos.getRole() == 2)
                        {
                            if(e.isShiftClick())
                            {
                                main.commandExecutor(p, "faction withdraw 1000", "fireland.command.faction.withdraw");
                            }
                            else
                            {
                                main.commandExecutor(p, "faction withdraw 100", "fireland.command.faction.withdraw");
                            }
                        }
                        else if(e.isLeftClick())
                        {
                            if(e.isShiftClick())
                            {
                                main.commandExecutor(p, "faction deposit 1000", "fireland.command.faction.deposit");
                            }
                            else
                            {
                                main.commandExecutor(p, "faction deposit 100", "fireland.command.faction.deposit");
                            }
                        }
                        MenuFaction.OpenFaction(main, p, true);
                        break;
                    case GRASS_BLOCK:
                        MenuZone.OpenZone(main, p);
                        break;
                }
            }
            else if(inv.getTitle().contains("Membres de "))
            {
                /**       Click check        **/

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }
                clickManager(e);

                /**       Click check        **/


                switch(itemclicked.getType())
                {
                    case RED_STAINED_GLASS_PANE -> MenuFaction.OpenFaction(main, p, true);
                }
            }
            else if(inv.getTitle().contains("Améliorations pour"))
            {
                /**       Click check        **/

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }
                clickManager(e);

                /**       Click check        **/


                FactionFunctions ff = new FactionFunctions(main, p);
                FactionPlayerInformation pInfos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
                FactionInformation finfos = ff.getFactionInfo(pInfos.getFactionName());
                final Material color = BlockUtilities.getGlassPaneColor(finfos.getColorcode());

                switch(itemclicked.getType())
                {
                    case RED_STAINED_GLASS_PANE -> MenuFaction.OpenFaction(main, p, true);
                    case LIME_STAINED_GLASS_PANE -> main.commandExecutor(p, "faction upgrade", "fireland.command.faction.upgrade");
                    case SHIELD -> {
                        if(!finfos.hasFriendlyFirePerk())
                        {
                            if(finfos.getCurrentUpgrade() < 2)
                            {
                                BasicUtilities.sendPlayerError(p, "Vous devez être niveau de faction 2 pour débloquer cette amélioration.");
                            }
                            else
                            {
                                main.commandExecutor(p, "faction perk friendly_fire", "fireland.command.faction.perk");
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
                                BasicUtilities.sendPlayerError(p, "Vous devez être niveau de faction 5 pour débloquer cette amélioration.");
                            }
                            else
                            {
                                main.commandExecutor(p, "faction perk show_nickname", "fireland.command.faction.perk");
                                MenuPerks.OpenPerks(main, p);
                            }

                        }
                        return;
                    }
                    case LEATHER -> {
                        if(!finfos.hasSkinPerk())
                        {
                            if(finfos.getCurrentUpgrade() < 4)
                            {
                                BasicUtilities.sendPlayerError(p, "Vous devez être niveau de faction 4 pour débloquer cette amélioration.");
                            }
                            else
                            {
                                main.commandExecutor(p, "faction perk has_skin", "fireland.command.faction.perk");
                                MenuPerks.OpenPerks(main, p);
                            }

                        }
                        return;
                    }
                    case FLOWER_BANNER_PATTERN -> {
                        if(!finfos.DoShowPrefix())
                        {
                            if(finfos.getCurrentUpgrade() < 3)
                            {
                                BasicUtilities.sendPlayerError(p, "Vous devez être niveau de faction 3 pour débloquer cette amélioration.");
                            }
                            else
                            {
                                main.commandExecutor(p, "faction perk show_prefix", "fireland.command.faction.perk");
                                MenuPerks.OpenPerks(main, p);
                            }

                        }
                        return;
                    }
                    case GRASS_BLOCK -> {
                        if(!finfos.hasCapturePerk())
                        {
                            if(finfos.getCurrentUpgrade() < 2)
                            {
                                BasicUtilities.sendPlayerError(p, "Vous devez être niveau de faction 3 pour débloquer cette amélioration.");
                            }
                            else
                            {
                                main.commandExecutor(p, "faction perk capture_perk", "fireland.command.faction.perk");
                                MenuPerks.OpenPerks(main, p);
                            }

                        }
                        return;
                    }
                    case BEACON -> {
                        if(!finfos.hasZoneTpPerk())
                        {
                            if(finfos.getCurrentUpgrade() < 6)
                            {
                                BasicUtilities.sendPlayerError(p, "Vous devez être niveau de faction 6 pour débloquer cette amélioration.");
                            }
                            else
                            {
                                main.commandExecutor(p, "faction perk zone_tp", "fireland.command.faction.perk");
                                MenuPerks.OpenPerks(main, p);
                            }

                        }
                        return;
                    }
                }
                if(itemclicked.getType() == color && p.hasPermission("fireland.command.faction.color") && !itemclicked.getItemMeta().getDisplayName().contains("Améliorer la faction au rang") && pInfos.getRole() == 2)
                {
                    MenuColor.OpenColorMenu(main, p, finfos);
                }
            }
            else if(inv.getTitle().contains("Changement de couleur"))
            {
                /**       Click check        **/

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }
                clickManager(e);

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

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }
                clickManager(e);

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
                    if(zoneinfo != null && zoneinfo.getClaimedAt() != null && finfos.hasZoneTpPerk())
                    {
                        ZoneConfigFileManager configManager = new ZoneConfigFileManager(main);
                        configManager.notSafeSetup();
                        Location loc = new Location(Bukkit.getWorld("world"), configManager.config.getDouble("zone."+zoneinfo.getZoneName()+".x"),
                                configManager.config.getDouble("zone."+zoneinfo.getZoneName()+".y"),
                                configManager.config.getDouble("zone."+zoneinfo.getZoneName()+".z"));
                        InGameUtilities.teleportPlayer(p, loc, 15, "gun.hub.helico", main);
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

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }
                clickManager(e);

                /**       Click check        **/


                if(itemclicked.getType() == Material.RED_STAINED_GLASS_PANE && itemclicked.getItemMeta().getDisplayName().contains("Retour à l'intendant"))
                {
                    MenuIntendant.OpenIntendant(main, p);
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
                    main.commandExecutor(p, "booster create "+level+" "+duration, "fireland.command.booster");
                    MenuBooster.OpenBoosters(main,p);
                }
            }
        }
    }

    private void clickManager(InventoryClickEvent e)
    {
        if (e.getClickedInventory() == e.getView().getTopInventory() || e.getClick().isKeyboardClick()) {
            e.setCancelled(true);
        }
        else
        {
            if(e.isShiftClick())
            {
                e.setCancelled(true);
            }
        }
    }








}
