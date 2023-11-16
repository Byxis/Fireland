package fr.byxis.faction.housing;

import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.faction.faction.FactionInformation;
import fr.byxis.faction.faction.FactionPlayerInformation;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.jeton.JetonManager;
import fr.byxis.jeton.jetonSql;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import static fr.byxis.fireland.utilities.BasicUtilities.getUuid;
import static fr.byxis.fireland.utilities.InGameUtilities.debugp;
import static fr.byxis.player.intendant.menu.MenuBunker.*;

public class BunkerEvent implements Listener {
    private Fireland main;
    public BunkerEvent(Fireland main) {
        this.main = main;
    }

    @EventHandler
    public void PlayerInteraction(PlayerInteractEvent e)
    {
        if((!e.getPlayer().getWorld().getName().equalsIgnoreCase("bunker")) || e.getClickedBlock() == null || e.getAction() == Action.LEFT_CLICK_BLOCK)
            return;

        switch(e.getClickedBlock().getType())
        {
            case DEAD_HORN_CORAL_WALL_FAN -> OpenBunker(main , e.getPlayer());
            case SMOKER ->
            {
                e.setCancelled(true);
                OpenBunkerFood(main , e.getPlayer());
            }
            case SMITHING_TABLE ->
            {
                e.setCancelled(true);
                OpenBunkerMechanic(main , e.getPlayer());
            }
            case BREWING_STAND ->
            {
                e.setCancelled(true);
                OpenBunkerAlchemy(main , e.getPlayer());
            }
            case CHEST ->
            {
                e.setCancelled(true);
                OpenBunkerChest(main , e.getPlayer());
            }
        }
    }

    @EventHandler
    public void PlayerClick(InventoryClickEvent e)
    {
        if(!e.getView().getPlayer().getWorld().getName().equalsIgnoreCase("bunker"))
            return;

        Player p = (Player) e.getView().getPlayer();

        if(e.getView().getTitle().contains("Menu du Bunker"))
        {
            /**       Click check        **/

            InventoryUtilities.clickManager(e);

            ItemStack itemclicked = e.getCurrentItem();
            if (itemclicked == null)
                return;

            /**       Click check        **/

            if(e.getCurrentItem().getType() == Material.BARRIER)
            {
                BunkerClass bk = main.bunkerManager.FindBunkerEnteredByPlayer(p.getName());
                if(bk != null)
                    bk.Leave(p);
                else
                    p.teleport(new Location(Bukkit.getWorld("world"), -447.5, 65, -447.5));
            }
            else if(e.getCurrentItem().getType() == Material.MAP)
            {
                OpenInviteBunker(main, p, 1);
            }
            else if(e.getCurrentItem().getType() == Material.RABBIT_HIDE)
            {
                FactionFunctions ff = new FactionFunctions(main, p);
                FactionPlayerInformation pInfos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
                FactionInformation infos = ff.getFactionInfo(pInfos.getFactionName());

                if(infos != null && pInfos != null && pInfos.getRole() == 2)
                {
                    OpenBunkerSkin(main, p);
                }
            }
            else if(e.getCurrentItem().getType() == Material.ANVIL)
            {
                BunkerClass bk = main.bunkerManager.FindBunkerEnteredByPlayer(p.getName());
                FactionFunctions ff = new FactionFunctions(main, p);
                FactionPlayerInformation pInfos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
                FactionInformation infos = ff.getFactionInfo(pInfos.getFactionName());

                if(infos != null && pInfos != null && pInfos.getRole() == 2)
                {
                    if(bk.GetAmeliorationFactionLevel() <= infos.getCurrentUpgrade())
                    {
                        if(infos.getCurrentMoney() > bk.GetAmeliorationPriceMoney())
                        {
                            if(JetonManager.getJetonsPlayer(p.getUniqueId()) >= bk.GetAmeliorationPriceJetons())
                            {
                                if(bk.GetAmeliorationPriceJetons() > 0)
                                {
                                    JetonManager.payJetons(p, bk.GetAmeliorationPriceJetons(),
                                            "Achat de l'amélioration de bunker"+(bk.GetBunkerLevel()+1), false, true);
                                }
                                ff.sendFactionPlayer(infos.getName(), "Le bunker de votre faction a été amélioré !");
                                ff.take(infos.getName(), bk.GetAmeliorationPriceMoney());
                                bk.Upgrade(main);
                            }
                            else
                            {
                                InGameUtilities.sendPlayerError(p, "Vous n'avez pas assez de jetons pour améliorer le bunker.");
                            }
                        }
                        else
                        {
                            InGameUtilities.sendPlayerError(p, "La faction n'a pas les fonds pour améliorer le bunker.");
                        }
                    }
                    else
                    {
                        InGameUtilities.sendPlayerError(p, "Le niveau de la faction est trop bas pour améliorer le bunker, cela nécessite le niveau "+bk.GetAmeliorationFactionLevel()+".");
                    }
                }
            }
        }
        else if(e.getView().getTitle().contains("Inviter dans votre bunker"))
        {
            /**       Click check        **/

            InventoryUtilities.clickManager(e);

            ItemStack itemclicked = e.getCurrentItem();
            if (itemclicked == null)
                return;

            /**       Click check        **/

            if(e.getCurrentItem().getType() == Material.PLAYER_HEAD)
            {
                String name = e.getCurrentItem().getItemMeta().getDisplayName();
                BunkerClass bk = main.bunkerManager.FindBunkerEnteredByPlayer(p.getName());
                Player invitee = Bukkit.getPlayer(getUuid(ChatColor.stripColor(name.split(" ")[1])));
                if (invitee != null && bk != null && invitee.isOnline())
                {
                    if(!bk.IsInvited(invitee))
                    {
                        InGameUtilities.sendPlayerSucces(p, "Le joueur "+invitee.getName()+" a bien été invité !");
                        InGameUtilities.sendInteractivePlayerMessage(invitee, "Vous avez été invité par "+p.getName()+" dans le bunker de "+bk.GetName()+", pour rejoindre, cliquez sur ce message ou tapez §d/bunker join "+bk.GetName()+"§r§7 tout en étant dans une safe zone.", "/bunker join "+bk.GetName(), "§dCliquez ici pour vous téléporter", ClickEvent.Action.RUN_COMMAND);
                        bk.Invite(p, invitee);
                    }
                    else
                    {
                        InGameUtilities.sendPlayerError(p, "Le joueur a déjà été invité !");
                    }
                }
            }
            else if(e.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE)
            {
                e.setCancelled(true);
                OpenBunker(main, p);
            }
        }
        else if(e.getView().getTitle().contains("Nourriture du bunker"))
        {
            /**       Click check        **/

            InventoryUtilities.clickManager(e);

            ItemStack itemclicked = e.getCurrentItem();
            if (itemclicked == null)
                return;

            /**       Click check        **/

            if(e.getCurrentItem().getType() == Material.COOKED_BEEF)
            {
                BunkerClass bk = main.bunkerManager.FindBunkerEnteredByPlayer(p.getName());
                if(bk != null)
                {
                    bk.ClaimFood(p);
                    OpenBunkerFood(main , p);
                }
            }
        }
        else if(e.getView().getTitle().contains("Atelier du bunker"))
        {
            /**       Click check        **/

            InventoryUtilities.clickManager(e);

            ItemStack itemclicked = e.getCurrentItem();
            if (itemclicked == null)
                return;

            /**       Click check        **/

            BunkerClass bk = main.bunkerManager.FindBunkerEnteredByPlayer(p.getName());
            if(bk != null)
            {
                switch(e.getCurrentItem().getType())
                {
                    case NETHERITE_SCRAP ->
                    {
                        bk.ClaimScrap(p);
                        OpenBunkerMechanic(main , p);
                    }
                    case GUNPOWDER ->
                    {
                        bk.ClaimPowder(p);
                        OpenBunkerMechanic(main , p);
                    }
                    case IRON_INGOT ->
                    {
                        bk.ClaimRepairKit(p);
                        OpenBunkerMechanic(main , p);
                    }
                    case RED_STAINED_GLASS_PANE ->  p.closeInventory();
                }
            }
        }
        else if(e.getView().getTitle().contains("Atelier d'Alchimie du bunker"))
        {
            /**       Click check        **/

            InventoryUtilities.clickManager(e);

            ItemStack itemclicked = e.getCurrentItem();
            if (itemclicked == null)
                return;

            /**       Click check        **/

            BunkerClass bk = main.bunkerManager.FindBunkerEnteredByPlayer(p.getName());
            if(bk != null)
            {
                switch(e.getCurrentItem().getType())
                {
                    case HONEYCOMB ->
                    {
                        bk.ClaimMeds(p);
                        OpenBunkerAlchemy(main , p);
                    }
                    case WHEAT_SEEDS ->
                    {
                        if(e.getCurrentItem().getItemMeta().getCustomModelData() == 104)
                        {
                            bk.ClaimAntiDouleur(p);
                            OpenBunkerAlchemy(main , p);
                        }
                        else
                        {
                            bk.ClaimSerum(p);
                            OpenBunkerAlchemy(main , p);
                        }
                    }
                    case RED_STAINED_GLASS_PANE ->  p.closeInventory();
                }
            }
        }
        else if(e.getView().getTitle().contains("Stockage du bunker"))
        {
            /**       Click check        **/

            InventoryUtilities.clickManager(e);

            ItemStack itemclicked = e.getCurrentItem();
            if (itemclicked == null)
                return;

            /**       Click check        **/

            BunkerClass bk = main.bunkerManager.FindBunkerEnteredByPlayer(p.getName());
            if(bk != null)
            {
                switch(e.getCurrentItem().getType())
                {
                    case RED_STAINED_GLASS_PANE ->
                    {
                        p.closeInventory();
                    }
                    case CHEST ->
                    {
                        String name = e.getCurrentItem().getItemMeta().getDisplayName();
                        name = ChatColor.stripColor(name);
                        String[] names = name.split(" ");
                        bk.GetStorage().OpenStorage(p, Integer.parseInt(names[1])-1);
                    }
                    case BARRIER -> InGameUtilities.playPlayerSound(p, Sound.ITEM_SHIELD_BREAK, SoundCategory.AMBIENT, 1, 0);
                }
            }
        }
        else if(e.getView().getTitle().contains("Skin de Bunker"))
        {
            /**       Click check        **/

            InventoryUtilities.clickManager(e);

            ItemStack itemclicked = e.getCurrentItem();
            if (itemclicked == null)
                return;

            /**       Click check        **/

            BunkerClass bk = main.bunkerManager.FindBunkerEnteredByPlayer(p.getName());
            if(bk != null)
            {
                if (e.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE) {
                    OpenBunker(main, p);
                }
                else if(main.bunkerManager.GetBunkerSkins().keySet().contains(e.getCurrentItem().getType()))
                {
                    if(p.hasPermission("fireland.bunker.skin."+main.bunkerManager.GetBunkerSkins().get(e.getCurrentItem().getType())[1]))
                    {
                        bk.ChangeSkin(p, main.bunkerManager.GetBunkerSkins().get(e.getCurrentItem().getType())[1]);
                    }
                }
            }
        }
        else if(e.getView().getTitle().contains("Stockage ") &&e.getView().getTitle().contains(" du bunker"))  {
            /**       Click check        **/
            if(e.getClick().isKeyboardClick() && e.getView().getPlayer().getInventory().getItem(e.getHotbarButton()) != null)
                e.setCancelled(true);

            /**       Click check        **/

            if(e.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE)
            {
                e.setCancelled(true);
                OpenBunkerChest(main, p);
            }
            else if(e.getCurrentItem().getType() == Material.WHITE_STAINED_GLASS_PANE)
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerLeave(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        BunkerClass bk = main.bunkerManager.FindBunkerEnteredByPlayer(p.getName());
        if(bk != null)
        {
            bk.Leave(p);
        }
    }

    @EventHandler
    public void playerQuitInventory(InventoryCloseEvent e)
    {
        if(e.getView().getTitle().contains("Stockage du bunker"))
        {
            InGameUtilities.playPlayerSound((Player) e.getPlayer(), Sound.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 1, 1);
        }
    }
}
