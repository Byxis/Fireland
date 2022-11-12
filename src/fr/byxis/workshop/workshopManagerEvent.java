package fr.byxis.workshop;

import fr.byxis.db.jetonSql;
import fr.byxis.event.jetonsManager;
import fr.byxis.main.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;

public class workshopManagerEvent implements Listener {

    private final Main main;
    public workshopManagerEvent(Main main) {
        this.main = main;
    }

    int genererInt(int borneInf, int borneSup){
        Random random = new Random();
        int nb;
        nb = borneInf+random.nextInt(borneSup-borneInf);
        return nb;
    }

    @EventHandler
    public void playerUseRecipe(PlayerInteractEvent e)
    {
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) ||e.getAction().equals(Action.RIGHT_CLICK_AIR) && (e.getItem().getType() != Material.AIR))
        {
            if(e.getItem() == null)return;
            if(e.getItem().getType() == Material.PAPER)
            {
                e.setCancelled(true);
                workshopFunction wf = new workshopFunction(main, e.getPlayer());
                String name = e.getItem().getItemMeta().getDisplayName();
                int crafted = wf.getTimeCrafted(name, e.getPlayer().getUniqueId().toString());
                int max = wf.getCraftedTimeToLearn(name);
                if(!wf.isLearned(name, e.getPlayer().getUniqueId().toString()))
                {
                    if( crafted>= max)
                    {
                        e.getPlayer().playSound(e.getPlayer().getLocation(), "minecraft:entity.player.levelup", 1, 1);
                        wf.learnRecipe(name, e.getPlayer().getUniqueId().toString());
                        e.getItem().setAmount( e.getItem().getAmount()-1);
                        e.getPlayer().sendMessage("Vous avez appris le plan : "+name);
                    }
                    else
                    {
                        e.getPlayer().sendMessage("§cVous devez construire encore "+(max-crafted)+" fois ce plan avant de pouvoir l'apprendre !");
                    }
                }
                else
                {
                    e.getPlayer().sendMessage("§CVous connaissez déjà ce plan !");
                }

            }
        }
    }

    @EventHandler
    public void playerOpenCraftMenu(PlayerInteractEntityEvent e)
    {
        if(e.getRightClicked() instanceof Villager && e.getRightClicked().getName().contains("Atelier"))
        {
            workshopFunction wf = new workshopFunction(main, e.getPlayer());
            wf.openCraftMenu(e.getPlayer(), 1);
        }
    }

    public int getNbrOfMaxCrafting(Player player)
    {
        for (PermissionAttachmentInfo perm : player.getEffectivePermissions())
        {
            String permString = perm.getPermission();
            if (permString.startsWith("fireland.workshop.craftlimit."))
            {
                String[] amount = permString.split("\\.");
                Integer amountOfHeals = Integer.parseInt(amount[3]);
                return amountOfHeals;
            }
        }
        return 0;
    }

    @EventHandler
    public void playerOpenCraftMenuFromAtelier(PlayerInteractEvent e)
    {
        if(e.getClickedBlock() != null)
        {
            if(e.getClickedBlock().getType() == Material.BEEHIVE)
            {
                Player p = e.getPlayer();
                workshopFunction wf = new workshopFunction(main, p);
                wf.openWorkshop(p);
            }
        }
    }

    @EventHandler
    public void playerInteractionOnInv(InventoryClickEvent e)
    {
        if(e.getView().getTitle().contains("Atelier"))
        {
            e.setCancelled(true);
            ItemStack itemclicked = e.getCurrentItem();
            if(itemclicked == null)
            {
                return;
            }
            Player p = (Player) e.getView().getPlayer();
            workshopFunction wf = new workshopFunction(main, p);
            int[] craftItems = wf.getCraftItems(p);
            if(itemclicked.getType() == Material.RED_STAINED_GLASS_PANE || itemclicked.getType() == Material.LIME_STAINED_GLASS_PANE)
            {
                int page = wf.getInvPageMax(e.getView());
                int next = wf.getItemCurrentPage(itemclicked);
                int max = wf.getItemMaxPage(itemclicked);
                if(next != (max+1))
                {
                    wf.openCraftMenu(p, next);
                }
            }
            else if(itemclicked.getType() != Material.WHITE_STAINED_GLASS_PANE)
            {

                int max = getNbrOfMaxCrafting(p);
                if(wf.getNbrOfItemCrafting(p.getUniqueId().toString()) >= max)
                {
                    p.sendMessage("§cVous avez atteint votre limite de craft qui est de "+max+" !");
                    return;
                }
                workshopItemClass craftable = wf.getACraftableItem(p, p.getUniqueId().toString(), craftItems[0], craftItems[1], itemclicked.getItemMeta().getDisplayName().replaceAll("§7", ""));
                if(craftable != null)
                {
                    int page = wf.getInvPageCurrent(e.getView());
                    wf.craftItem(p, craftable);
                    wf.openCraftMenu(p, page);
                }
            }
        }
        else if(e.getView().getTitle().contains("Attente"))
        {
            e.setCancelled(true);
            ItemStack itemclicked = e.getCurrentItem();
            if(itemclicked == null)
            {
                return;
            }
            Player p = (Player) e.getView().getPlayer();
            workshopFunction wf = new workshopFunction(main, p);
            if(itemclicked.getType() == Material.RED_STAINED_GLASS_PANE || itemclicked.getType() == Material.LIME_STAINED_GLASS_PANE)
            {
                int next = wf.getItemCurrentPage(itemclicked);
                int max = wf.getItemMaxPage(itemclicked);
                if(next != (max+1))
                {
                    wf.openCraftingMenu(p, next);
                }
            }
            else if(itemclicked.getType() != Material.WHITE_STAINED_GLASS_PANE)
            {
                Timestamp time = new Timestamp(System.currentTimeMillis());
                ArrayList<workshopCraftingItemClass> items = wf.getAllCraftingItems(p, p.getUniqueId().toString());
                if(p.getInventory().firstEmpty() == -1)return;
                for(workshopCraftingItemClass item:items)
                {
                    if(itemclicked.getItemMeta().getDisplayName().replaceAll("§7","").equalsIgnoreCase(item.itemName) &&
                    itemclicked.getItemMeta().getLore().get(2).contains(item.creationDate.toString()))
                    {
                        if(item.finishDate.before(time))
                        {
                            if(!wf.isBreakable(item, p.getUniqueId().toString()))
                            {
                                main.playSound(p, "minecraft:block.anvil.use");
                                wf.removeFromQueue(item, p.getUniqueId().toString());
                                main.commandExecutor(p, item.command, "crackshot.get.all");
                                wf.openCraftingMenu(p, wf.getInvPageCurrent(e.getView()));
                            }
                            else
                            {
                                int rd = genererInt(0, 101);
                                if(rd <= 10)
                                {
                                    main.playSound(p, "minecraft:block.anvil.destroy");
                                    p.sendMessage("§cPas de chance ! Votre item s'est cassé pendant la fabrication...");
                                }
                                else
                                {
                                    main.playSound(p, "minecraft:block.anvil.use");
                                    wf.removeFromQueue(item, p.getUniqueId().toString());
                                    main.commandExecutor(p, item.command, "crackshot.get.all");
                                    wf.openCraftingMenu(p, wf.getInvPageCurrent(e.getView()));
                                }
                            }
                        }
                        else
                        {
                            jetonsManager jt = new jetonsManager(main);
                            if(jt.getJetonsPlayer(p.getUniqueId()) > 0)
                            {
                                jetonSql jtsql = new jetonSql(main, p);
                                String desc = "Acceleration de craft, nom darme : " +
                                        item.itemName +", date creation :"+item.creationDate.toString();
                                if(jtsql.createFacture(p.getUniqueId().toString(), 1, desc))
                                {
                                    p.sendMessage("§aVous avez payé 1 jetons et accéléré le temps de craft de 30min !");
                                    jt.removeJetonsPlayer(p.getUniqueId(), 1);
                                    wf.setUnbreakable(item, p.getUniqueId().toString());
                                    wf.removeTime(item, p.getUniqueId().toString());
                                    wf.openCraftingMenu(p, wf.getInvPageCurrent(e.getView()));
                                }

                            }
                            else
                            {
                                p.sendMessage("§cVous ne pouvez pas encore récupérer cet item et vous n'avez pas de jetons pour accélérer le craft !");
                            }
                        }
                        break;
                    }
                }
            }
        }
        else if(e.getView().getTitle().contains("Plan de travail"))
        {
            e.setCancelled(true);
            if(e.getCurrentItem().getType() == Material.CHEST)
            {
                main.commandExecutor((Player) e.getView().getPlayer(), "ws craftinggui", "fireland.command.workshop.craftinggui");
            } else if (e.getCurrentItem().getType() == Material.ANVIL) {
                main.commandExecutor((Player) e.getView().getPlayer(), "ws gui", "fireland.command.workshop.gui");
            }
        }
    }
}
