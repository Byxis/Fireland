package fr.byxis.faction.essaim;

import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.fireland.utilities.TextUtilities;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.*;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import static fr.byxis.fireland.utilities.InGameUtilities.debugp;

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
                InGameUtilities.sendPlayerError(e.getPlayer(), "Vous devez être dans une faction pour entrer dans un essaim.");
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

                sb.append(str[str.length-1].toLowerCase());
                String essaim = sb.toString();
                if(EssaimManager.activeEssaims.get(essaim).isFinished())
                {
                    switch (itemclicked.getType()) {
                        case RED_STAINED_GLASS_PANE -> {
                            InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
                            EssaimFunctions.leaveFinishedEssaim(essaim, p, false);
                        }
                    }
                }
                else
                {
                    switch (itemclicked.getType()) {
                        case LIME_STAINED_GLASS_PANE -> {
                            if (EssaimManager.groups.get(essaim).getLeader().getName().equalsIgnoreCase(p.getName())) {
                                EssaimFunctions.openStartingMenu(essaim, p);
                            } else {
                                InGameUtilities.playPlayerSound(p, "item.shield.break", SoundCategory.BLOCKS, 1, 0);
                                InGameUtilities.sendPlayerError(p, "§cVous n'êtes pas le leader du groupe.");
                            }
                        }
                        case RED_STAINED_GLASS_PANE -> EssaimFunctions.leaveGroup(essaim, p);
                        case YELLOW_STAINED_GLASS_PANE -> {
                            if (EssaimManager.groups.get(essaim).getLeader().getName().equalsIgnoreCase(p.getName())) {
                                EssaimFunctions.openInvitation(essaim, p);
                                InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
                            } else {
                                InGameUtilities.playPlayerSound(p, "item.shield.break", SoundCategory.BLOCKS, 1, 0);
                                InGameUtilities.sendPlayerError(p, "§cVous n'êtes pas le leader du groupe.");
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
                sb.append(str[str.length-1].toLowerCase());
                String essaim = sb.toString();
                switch (itemclicked.getType())
                {
                    case PLAYER_HEAD :
                        if(itemclicked.hasItemMeta() && itemclicked.getItemMeta().hasDisplayName())
                        {
                            String name = ChatColor.stripColor(itemclicked.getItemMeta().getDisplayName());
                            EssaimFunctions.inviteGroup(essaim,Bukkit.getPlayer(name));

                            InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
                        }
                        break;
                    case RED_STAINED_GLASS_PANE:
                        EssaimFunctions.openMenu(essaim, p);
                        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
                        break;
                }
            }
            else if (inv.getTitle().contains("Lancement de "))
            {
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

                sb.append(str[str.length-1].toLowerCase());
                String essaim = sb.toString();
                if(!EssaimManager.activeEssaims.get(essaim).isFinished())
                {
                    if (EssaimManager.groups.get(essaim).getLeader().getName().equalsIgnoreCase(p.getName())) {
                        switch(itemclicked.getType())
                        {
                            case PLAYER_HEAD -> launchEssaimStart(essaim, p, 1);
                            case SKELETON_SKULL -> launchEssaimStart(essaim, p, 2);
                            case WITHER_SKELETON_SKULL -> InGameUtilities.sendPlayerError(p, "Cette difficulté n'est pas encore disponible.");//launchEssaimStart(essaim, p, 3);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerDeath(PlayerDeathEvent e)
    {
        String current = null;
        if(!main.essaimManager.existingEssaims.isEmpty())
        {
            for(String essaim : main.essaimManager.existingEssaims.keySet())
            {
                if(!EssaimManager.groups.isEmpty() && EssaimManager.groups.containsKey(essaim))
                {
                    if(!EssaimManager.groups.get(essaim).getMembers().isEmpty())
                    {
                        for(Player member : EssaimManager.groups.get(essaim).getMembers())
                        {
                            if(member.getName().equalsIgnoreCase(e.getPlayer().getName()))
                            {
                                current = essaim;
                                if(EssaimManager.groups.get(essaim).shouldKeepInventory())
                                {
                                    e.setKeepInventory(true);
                                    e.getDrops().clear();
                                }
                                InGameUtilities.sendPlayerInformation(member, "§cVous avez échoué l'expédition !");
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
        }
        if(current!= null)
        {
            EssaimManager.groups.get(current).loose(e.getPlayer());
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
                if(!EssaimManager.groups.isEmpty() && EssaimManager.groups.containsKey(essaim))
                {
                    if(!EssaimManager.groups.get(essaim).getMembers().isEmpty()) {
                        for(Player member : EssaimManager.groups.get(essaim).getMembers())
                        {
                            if(member.getName().equalsIgnoreCase(e.getPlayer().getName()))
                            {
                                if(member.getGameMode() != GameMode.CREATIVE || member.getGameMode() != GameMode.SPECTATOR && EssaimManager.groups.get(essaim).hasStarted())
                                {
                                    member.setInvulnerable(false);
                                    member.setHealth(0);
                                }
                                break;
                            }
                        }
                    }
                }

            }
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e)
    {
        String current = null;
        if(!main.essaimManager.existingEssaims.isEmpty())
        {
            for(String essaim : main.essaimManager.existingEssaims.keySet())
            {
                if(!EssaimManager.groups.isEmpty() && EssaimManager.groups.containsKey(essaim))
                {
                    if(!EssaimManager.groups.get(essaim).getMembers().isEmpty()) {
                        for(Player member : EssaimManager.groups.get(essaim).getMembers())
                        {
                            if(member.getName().equalsIgnoreCase(e.getPlayer().getName()))
                            {
                                EssaimManager.groups.get(essaim).leaveGroup(e.getPlayer());
                            }
                        }
                    }
                }

            }
        }
    }

    @EventHandler
    public void playerChangeWorld(PlayerChangedWorldEvent e)
    {
        if(!e.getFrom().getName().equalsIgnoreCase("essaim"))
            return;
        String current = null;
        if(!main.essaimManager.existingEssaims.isEmpty())
        {
            for(String essaim : main.essaimManager.existingEssaims.keySet())
            {
                if(!EssaimManager.groups.isEmpty() && EssaimManager.groups.containsKey(essaim))
                {
                    if(!EssaimManager.groups.get(essaim).getMembers().isEmpty()) {
                        for(Player member : EssaimManager.groups.get(essaim).getMembers())
                        {
                            if(member.getName().equalsIgnoreCase(e.getPlayer().getName()))
                            {
                                EssaimManager.groups.get(essaim).leaveGroup(e.getPlayer());
                            }
                        }
                    }
                }

            }
        }
    }

    @EventHandler
    public void PlayerInteractBlock(PlayerInteractEvent e)
    {
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null) {
            if (e.getClickedBlock().getType() == Material.IRON_DOOR && e.getItem()!= null && e.getItem().getType() == Material.ECHO_SHARD) {
                if(e.getItem().getItemMeta().hasCustomModelData())
                {
                    String name = "";
                    if(e.getItem().getItemMeta().getCustomModelData() == 1)
                    {
                        name = "bunker-de-latus";
                    }
                    else if(e.getItem().getItemMeta().getCustomModelData() == 2)
                    {
                        name = "usine-portuaire";
                    }
                    else if(e.getItem().getItemMeta().getCustomModelData() == 3)
                    {
                        name = "station-de-traitement-des-eaux";
                    }
                    else if(e.getItem().getItemMeta().getCustomModelData() == 4)
                    {
                        name = "entrepot-militaire";
                    }
                    else if(e.getItem().getItemMeta().getCustomModelData() == 5)
                    {
                        name = "immeuble-infeste";
                    }
                    else if(e.getItem().getItemMeta().getCustomModelData() == 6)
                    {
                        name = "hangar-silencieux";
                    }
                    else if(e.getItem().getItemMeta().getCustomModelData() == 8)
                    {
                        name = "centrale-nucleaire";
                    }
                    else if(e.getItem().getItemMeta().getCustomModelData() == 9)
                    {
                        name = "epave-du-porte-avion";
                    }
                    else if(e.getItem().getItemMeta().getCustomModelData() == 10)
                    {
                        name = "crypte";
                    }
                    else if(e.getItem().getItemMeta().getCustomModelData() == 11)
                    {
                        name = "station-petroliere";
                    }
                    else if(e.getItem().getItemMeta().getCustomModelData() == 12)
                    {
                        name = "laboratoire";
                    }
                    if(!name.equals(""))
                    {
                        Location loc = new Location(Bukkit.getWorld("essaim"),
                                EssaimManager.configManager.getConfig().getInt(name+".key.position.x"),
                                EssaimManager.configManager.getConfig().getInt(name+".key.position.y"),
                                EssaimManager.configManager.getConfig().getInt(name+".key.position.z")
                        );
                        openDoor(e, loc);
                    }
                }
            }
        }
    }

    private void openDoor(PlayerInteractEvent e, Location loc)
    {
        Location locH = new Location(loc.getWorld(), loc.getX(), loc.getY()+1, loc.getZ());
        Door door = (Door) e.getClickedBlock().getBlockData();
        if((e.getClickedBlock().getLocation().distance(loc) < 1 ||e.getClickedBlock().getLocation().distance(locH) < 1) && !door.isOpen())
        {
            door.setOpen(true);
            InGameUtilities.playWorldSound(loc, Sound.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1, 1);
            e.getClickedBlock().setBlockData(door);
            if(e.getPlayer().getGameMode() != GameMode.CREATIVE)
            {
                InGameUtilities.playWorldSound(loc, Sound.ITEM_SHIELD_BREAK, SoundCategory.BLOCKS, 1, 1);
                e.getItem().setAmount(e.getItem().getAmount()-1);
            }
        }
    }

    @EventHandler
    public void save(WorldSaveEvent e)
    {
        if(!e.getWorld().getName().equalsIgnoreCase("world"))
            return;
        EssaimFunctions.SaveEssaim();
    }

    private void launchEssaimStart(String essaim, Player p, int difficulty)
    {
        EssaimFunctions.startEssaim(essaim, difficulty);
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        for (Player member : EssaimManager.groups.get(essaim).getMembers()) {
            member.closeInventory();
            InGameUtilities.sendPlayerInformation(member, "§aL'expédition a démarrée !");
        }
    }


}
