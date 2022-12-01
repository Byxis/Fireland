package fr.byxis.intendant;

import fr.byxis.main.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;

public class MenuIndendant implements Listener {

    private final Main main;
    public MenuIndendant(Main main) {
        this.main = main;
    }

    public void ClickInventoryEvent(InventoryClickEvent e)
    {
        InventoryView inv = e.getView();
        if(e.getView().getPlayer() instanceof Player p && (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getType() != Material.AIR))
        {
            if(inv.getTitle().contains("Intendant"))
            {

            }
        }
    }

}
