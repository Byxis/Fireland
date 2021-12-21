package fr.byxis.event;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class RemoveInteractionBlock implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler
    public void removeGrindstoneInteraction(PlayerInteractEvent e) {
		if(e.getClickedBlock() == null || e.isCancelled())
		{
			return;
		}
		if ((e.getClickedBlock().getType() == Material.GRINDSTONE ||
				e.getClickedBlock().getType() == Material.JUKEBOX ||
				e.getClickedBlock().getType() == Material.ENCHANTING_TABLE ||
				e.getClickedBlock().getType() == Material.ANVIL
		) && e.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			e.setCancelled(true);
		}
	}

}
