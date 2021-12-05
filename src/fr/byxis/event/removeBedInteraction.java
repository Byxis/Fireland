package fr.byxis.event;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class removeBedInteraction implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void playerInteraction(PlayerInteractEvent e)
	{
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) 
		{
			if(e.getClickedBlock().getType() == Material.LEGACY_BED || 
				e.getClickedBlock().getType() == Material.LEGACY_BED_BLOCK ||
				e.getClickedBlock().getType() == Material.BLACK_BED ||
				e.getClickedBlock().getType() == Material.BLUE_BED ||
				e.getClickedBlock().getType() == Material.BROWN_BED ||
				e.getClickedBlock().getType() == Material.CYAN_BED ||
				e.getClickedBlock().getType() == Material.GRAY_BED ||
				e.getClickedBlock().getType() == Material.GREEN_BED ||
				e.getClickedBlock().getType() == Material.LIGHT_BLUE_BED ||
				e.getClickedBlock().getType() == Material.LIGHT_GRAY_BED ||
				e.getClickedBlock().getType() == Material.MAGENTA_BED ||
				e.getClickedBlock().getType() == Material.ORANGE_BED ||
				e.getClickedBlock().getType() == Material.PINK_BED ||
				e.getClickedBlock().getType() == Material.PURPLE_BED ||
				e.getClickedBlock().getType() == Material.RED_BED ||
				e.getClickedBlock().getType() == Material.WHITE_BED ||
				e.getClickedBlock().getType() == Material.YELLOW_BED ||
				e.getClickedBlock().getType() == Material.MAGENTA_BED
			)
			{
				e.setCancelled(true);
			}
			Block blk = e.getClickedBlock();
	        if ((blk.getType().name().startsWith("POTTED_") || blk.getType() == Material.FLOWER_POT) && !(e.getPlayer().getGameMode() == GameMode.CREATIVE)) {
	            e.setCancelled(true);
	            return;
	        }

		}
		
	}
	
}
