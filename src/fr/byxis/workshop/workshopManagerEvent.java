package fr.byxis.workshop;

import fr.byxis.main.Main;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
}
