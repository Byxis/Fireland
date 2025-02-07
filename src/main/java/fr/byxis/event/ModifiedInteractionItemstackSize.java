package fr.byxis.event;

import fr.byxis.fireland.Fireland;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ModifiedInteractionItemstackSize implements Listener
{
    
    private Fireland main;

    public ModifiedInteractionItemstackSize(Fireland main)
    {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            // The creative inventory works differently to the survival inventory and will not work
            // properly when updating the inventory the tick after a click.
            //Bukkit.getScheduler().runTask(main, player::updateInventory);
        }
    }

}
