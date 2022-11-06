package fr.byxis.workshop;

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

public class workshopManagerEvent implements Listener {

    private final Main main;
    public workshopManagerEvent(Main main) {
        this.main = main;
    }

    @EventHandler
    public void playerUseRecipe(PlayerInteractEvent e)
    {
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) ||e.getAction().equals(Action.RIGHT_CLICK_AIR) && (e.getItem() != null || e.getItem().getType() == Material.AIR))
        {
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

    @EventHandler
    public void playerOpenCraftMenuFromAtelier(PlayerInteractEvent e)
    {
        if(e.getClickedBlock() != null)
        {
            if(e.getClickedBlock().getType() == Material.BEEHIVE)
            {
                Player p = e.getPlayer();
                workshopFunction wf = new workshopFunction(main, p);
                wf.openCraftMenu(p, 1);
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
                int page = Integer.parseInt(String.valueOf(e.getView().getTitle().charAt(9)));
                int next = Integer.parseInt(String.valueOf(itemclicked.getItemMeta().getDisplayName().charAt(3)));
                int max = Integer.parseInt(String.valueOf(itemclicked.getItemMeta().getDisplayName().charAt(5)));
                if(next != max || max == page+1)
                {
                    wf.openCraftMenu(p, next);
                }
            }
            else if(itemclicked.getType() != Material.WHITE_STAINED_GLASS_PANE)
            {
                workshopItemClass craftable = wf.getACraftableItem(p, p.getUniqueId().toString(), craftItems[0], craftItems[1], itemclicked.getItemMeta().getDisplayName());
                if(craftable != null)
                {
                    int page = Integer.parseInt(String.valueOf(e.getView().getTitle().charAt(9)));
                    wf.craftItem(p, craftable);
                    wf.openCraftMenu(p, page);
                }
            }
        }
    }
}
