package fr.byxis.essaim;

import fr.byxis.essaim.essaimClass.EssaimClass;
import fr.byxis.faction.FactionFunctions;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.fireland.utilities.TextUtilities;
import fr.byxis.intendant.menu.MenuBooster;
import fr.byxis.intendant.menu.MenuFaction;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import org.bukkit.*;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;

public class EssaimEventHandler implements Listener {

    private Fireland main;

    public EssaimEventHandler(Fireland main)
    {
        this.main = main;
    }

    @EventHandler
    public void mobKilled(MythicMobDeathEvent e)
    {

        MobDisappearingHandler(e.getMob());
    }

    @EventHandler
    public void mobDispawn(MythicMobDespawnEvent e)
    {
        MobDisappearingHandler(e.getMob());
    }

    @EventHandler
    public void mobSpawnedDisabled(MythicMobSpawnEvent e)
    {
        if(e.isCancelled())
        {
            MobDisappearingHandler(e.getMob());
        }
    }

    private void MobDisappearingHandler(ActiveMob mob)
    {
        if(mob.getLocation().getWorld().getName().equals("essaim"))
        {
            for(String spawner : main.essaimManager.activeSpawners.keySet())
            {
                if(main.essaimManager.activeSpawners.get(spawner).getActiveMobs().contains(mob))
                {
                    main.essaimManager.activeSpawners.get(spawner).removeActiveMob(mob);
                    if(main.essaimManager.activeSpawners.get(spawner).isSpawnerFinished())
                    {
                        for(String essaim : main.essaimManager.existingEssaims.keySet())
                        {
                            if(main.essaimManager.existingEssaims.get(essaim).containsKey(spawner))
                            {
                                if(!main.essaimManager.existingEssaims.get(essaim).get(spawner).getCommand().equalsIgnoreCase(""))
                                {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), main.essaimManager.existingEssaims.get(essaim).get(spawner).getCommand());
                                }
                                break;
                            }
                        }

                        main.essaimManager.activeSpawners.remove(spawner);
                    }
                    break;
                }
            }
        }
    }

    @EventHandler
    public void interactionItemFrame(PlayerInteractEntityEvent e)
    {
        if(!(e.getRightClicked() instanceof ItemFrame itemFrame))
        {
            return;
        }
        itemFrame.getItem();
        if(itemFrame.getItem().getType() == Material.DEAD_FIRE_CORAL && itemFrame.getItem().getItemMeta() != null)
        {

            e.setCancelled(true);
            FactionFunctions ff = new FactionFunctions(main, e.getPlayer());
            if(!ff.playerFactionName(e.getPlayer()).equalsIgnoreCase("") && itemFrame.getItem().getItemMeta().hasDisplayName())
            {

                EssaimFunctions.createGroup(TextUtilities.convertCleanToStorable(itemFrame.getItem().getItemMeta().getDisplayName(), " "), e.getPlayer());
            }
            else
            {
                BasicUtilities.sendPlayerError(e.getPlayer(), "Vous devez être dans une faction pour entrer dans un essaim.");
            }
        }
    }

    @EventHandler
    public void ClickInventoryEvent(InventoryClickEvent e) {
        InventoryView inv = e.getView();
        if (e.getView().getPlayer() instanceof Player p) {
            if (inv.getTitle().contains("Essaim :")) {
                /**       Click check        **/

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }
               InventoryUtilities.clickManager(e);

                /**       Click check        **/

                StringBuilder sb = new StringBuilder();
                String[] str = (ChatColor.stripColor(e.getView().getTitle())).split(" ");
                for(int i = 2; i < str.length-1; i++)
                {
                    sb.append((str[i]).toLowerCase()).append("-");
                }
                sb.append(str[str.length-1]);
                String essaim = sb.toString();
                if(main.essaimManager.activeEssaims.get(essaim).isFinished())
                {
                    switch (itemclicked.getType()) {
                        case RED_STAINED_GLASS_PANE -> {
                            EssaimFunctions.leaveFinishedEssaim(essaim, p);
                        }
                    }
                }
                else
                {
                    switch (itemclicked.getType()) {
                        case LIME_STAINED_GLASS_PANE -> {
                            if (main.essaimManager.groups.get(essaim).getLeader().getName().equalsIgnoreCase(p.getName())) {
                                EssaimFunctions.startEssaim(essaim);
                                for (Player member : main.essaimManager.groups.get(essaim).getMembers()) {
                                    member.closeInventory();
                                    BasicUtilities.sendPlayerInformation(member, "§aL'expédition a démarrée !");
                                }
                            } else {
                                BasicUtilities.sendPlayerError(p, "§cVous n'êtes pas le leader du groupe.");
                            }
                        }
                        case RED_STAINED_GLASS_PANE -> EssaimFunctions.leaveGroup(essaim, p);
                        case YELLOW_STAINED_GLASS_PANE -> {
                            if (main.essaimManager.groups.get(essaim).getLeader().getName().equalsIgnoreCase(p.getName())) {
                                EssaimFunctions.openInvitation(essaim, p);
                            } else {
                                BasicUtilities.sendPlayerError(p, "§cVous n'êtes pas le leader du groupe.");
                            }
                        }
                    }
                }

            }else if (inv.getTitle().contains("Invitation :")) {
                /**       Click check        **/

                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }
                InventoryUtilities.clickManager(e);

                /**       Click check        **/

                StringBuilder sb = new StringBuilder();
                String[] str = (ChatColor.stripColor(e.getView().getTitle())).split(" ");
                for(int i = 2; i < str.length-1; i++)
                {
                    sb.append((str[i]).toLowerCase()).append("-");
                }
                sb.append(str[str.length-1]);
                String essaim = sb.toString();
                switch (itemclicked.getType())
                {
                    case PLAYER_HEAD :
                        if(itemclicked.hasItemMeta() && itemclicked.getItemMeta().hasDisplayName())
                        {
                            String name = ChatColor.stripColor(itemclicked.getItemMeta().getDisplayName());
                            EssaimFunctions.inviteGroup(essaim,Bukkit.getPlayer(name));
                        }
                        break;
                    case RED_STAINED_GLASS_PANE:
                        EssaimFunctions.openMenu(essaim, p);
                        break;
                }
            }
        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent e)
    {
        String current = null;
        if(!main.essaimManager.existingEssaims.isEmpty())
        {
            for(String essaim : main.essaimManager.existingEssaims.keySet())
            {
                if(!main.essaimManager.groups.isEmpty())
                {
                    for(Player member : main.essaimManager.groups.get(essaim).getMembers())
                    {
                        if(member.getName().equalsIgnoreCase(e.getPlayer().getName()))
                        {
                            current = essaim;
                            BasicUtilities.sendPlayerInformation(member, "§cVous avez échoué l'expédition !");
                            break;

                        }
                    }
                    if(current!= null)
                    {
                        break;
                    }
                }

            }
        }
        if(current!= null)
        {
            main.essaimManager.groups.get(current).loose(e.getPlayer());
        }
    }

    @EventHandler
    public void playerLeft(PlayerQuitEvent e)
    {
        String current = null;
        if(!main.essaimManager.existingEssaims.isEmpty())
        {
            for(String essaim : main.essaimManager.existingEssaims.keySet())
            {
                if(!main.essaimManager.groups.isEmpty())
                {
                    for(Player member : main.essaimManager.groups.get(essaim).getMembers())
                    {
                        if(member.getName().equalsIgnoreCase(e.getPlayer().getName()))
                        {
                            if(member.getGameMode() != GameMode.CREATIVE || member.getGameMode() != GameMode.SPECTATOR)
                            {
                                member.setInvulnerable(false);
                                member.setHealth(0);
                            }
                            current = essaim;
                            break;

                        }
                    }
                    if(current!= null)
                    {
                        break;
                    }
                }

            }
        }

        if(current!= null)
        {
            main.essaimManager.groups.get(current).loose(e.getPlayer());
        }
    }

    @EventHandler
    public void PlayerInteractBlock(PlayerInteractEvent e)
    {
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null) {
            if (e.getClickedBlock().getType() == Material.IRON_DOOR && e.getItem()!= null && e.getItem().getType() == Material.ECHO_SHARD) {
                if(e.getItem().getItemMeta().hasCustomModelData() && e.getItem().getItemMeta().getCustomModelData() == 1)
                {
                    Location loc = new Location(Bukkit.getWorld("essaim"),
                            EssaimManager.configManager.getConfig().getInt("bunker-de-latus.key.position.x"),
                            EssaimManager.configManager.getConfig().getInt("bunker-de-latus.key.position.y"),
                            EssaimManager.configManager.getConfig().getInt("bunker-de-latus.key.position.z")
                    );
                    Location locH = new Location(Bukkit.getWorld("essaim"),
                            EssaimManager.configManager.getConfig().getInt("bunker-de-latus.key.position.x"),
                            EssaimManager.configManager.getConfig().getInt("bunker-de-latus.key.position.y" +1),
                            EssaimManager.configManager.getConfig().getInt("bunker-de-latus.key.position.z")
                    );
                    Door door = (Door) e.getClickedBlock().getBlockData();
                    if((e.getClickedBlock().getLocation().distance(loc) < 1 ||e.getClickedBlock().getLocation().distance(locH) < 1) && !door.isOpen())
                    {
                        door.setOpen(true);
                        BasicUtilities.playWorldSound("essaim", loc, Sound.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1, 1);
                        e.getClickedBlock().setBlockData(door);
                        if(e.getPlayer().getGameMode() != GameMode.CREATIVE)
                        {
                            BasicUtilities.playWorldSound("essaim", loc, Sound.ITEM_SHIELD_BREAK, SoundCategory.BLOCKS, 1, 1);
                            e.getItem().setAmount(e.getItem().getAmount()-1);
                        }
                    }
                }
            }
        }
    }
}
