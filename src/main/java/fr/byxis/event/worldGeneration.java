package fr.byxis.event;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

public class worldGeneration implements Listener {

	@EventHandler
	public void onBlockGrow(BlockSpreadEvent  e) {
		if(e.getNewState().getType().equals(Material.VINE)) {
			e.setCancelled(true);
			return;
		}
	}
	
}
