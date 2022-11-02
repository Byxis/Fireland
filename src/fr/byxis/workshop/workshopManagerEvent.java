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

import java.util.Objects;

public class workshopManagerEvent implements Listener {

    private final Main main;
    public workshopManagerEvent(Main main) {
        this.main = main;
    }

    @EventHandler
    public void playerUseRecipe(PlayerInteractEvent e)
    {
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) ||e.getAction().equals(Action.RIGHT_CLICK_AIR) && e.getItem() != null)
        {
            if(Objects.requireNonNull(e.getItem()).getType() == Material.PAPER && (
                    Objects.requireNonNull(e.getItem().getItemMeta()).getCustomModelData() == 1 ||
                    e.getItem().getItemMeta().getCustomModelData() == 2 ||
                    e.getItem().getItemMeta().getCustomModelData() == 3 ||
                    e.getItem().getItemMeta().getCustomModelData() == 4 ))
            {
                e.setCancelled(true);
                workshopFunction wf = new workshopFunction(main, e.getPlayer());
                String name = e.getItem().getItemMeta().getDisplayName();
                int crafted = wf.getTimeCrafted(name, e.getPlayer().getUniqueId().toString());
                int max = wf.getCraftedTimeToLearn(name);
                if(!wf.isLearned(name, e.getPlayer().getUniqueId().toString()) && crafted>= max)
                {
                    e.getPlayer().playSound(e.getPlayer().getLocation(), "minecraft:entity.player.levelup", 1, 1);
                    wf.learnRecipe(name, e.getPlayer().getUniqueId().toString());
                }
                e.getItem().setAmount( e.getItem().getAmount()-1);
            }
        }
    }

    @EventHandler
    public void playerOpenCraftMenu(PlayerInteractEntityEvent e)
    {
        if(e.getRightClicked() instanceof Villager && e.getRightClicked().getName().contains("craft"))
        {
            //TODO
        }
    }

    @EventHandler
    public void playerInteractionOnInv(InventoryClickEvent e)
    {
        if(e.getView().getTitle().contains("Atelier"))
        {
            e.setCancelled(true);
            ItemStack itemclicked = e.getCurrentItem();
            Player p = (Player) e.getView().getPlayer();
            workshopFunction wf = new workshopFunction(main, p);
            int[] craftItems = wf.getCraftItems(p);
            if(itemclicked.getType() == Material.RED_STAINED_GLASS_PANE || itemclicked.getType() == Material.LIME_STAINED_GLASS_PANE)
            {
                int next = itemclicked.getItemMeta().getDisplayName().charAt(4);
                int max = itemclicked.getItemMeta().getDisplayName().charAt(6);
                if(next != max)
                {p.sendMessage(""+next);
                    wf.openCraftMenu(p, next);
                }
            }
            else if(itemclicked.getType() != Material.WHITE_STAINED_GLASS_PANE)
            {
                workshopItemClass craftable = wf.getACraftableItem(p, p.getUniqueId().toString(), craftItems[0], craftItems[1], itemclicked.getItemMeta().getDisplayName());
                if(craftable != null)
                {
                    p.sendMessage(""+e.getView().getTitle().charAt(9));
                    wf.craftItem(p, craftable);
                    wf.openCraftMenu(p, e.getView().getTitle().charAt(9));
                }
            }
        }
    }
}
