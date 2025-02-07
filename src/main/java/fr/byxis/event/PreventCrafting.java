package fr.byxis.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class PreventCrafting implements Listener
{
    
    @EventHandler
    public void OnCraft(CraftItemEvent e)
    {
        if(!e.getWhoClicked().isOp())
        {
            e.setCancelled(true);
        }
    }
    
}
