package fr.byxis.event;

import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractionManager implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler
    public void removeInteraction(PlayerInteractEvent e) {
		if(e.getClickedBlock() == null || e.isCancelled() || e.getPlayer().getGameMode() == GameMode.CREATIVE)
		{
			return;
		}
		if ((e.getClickedBlock().getType() == Material.GRINDSTONE ||
				e.getClickedBlock().getType() == Material.JUKEBOX ||
				e.getClickedBlock().getType() == Material.ENCHANTING_TABLE ||
				e.getClickedBlock().getType() == Material.ANVIL ||
				e.getClickedBlock().getType() == Material.FURNACE ||
				e.getClickedBlock().getType() == Material.BLAST_FURNACE ||
				e.getClickedBlock().getType() == Material.DAMAGED_ANVIL ||
				e.getClickedBlock().getType() == Material.SMOKER
		) && e.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			e.setCancelled(true);
		}

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

	@EventHandler
	public void playSoundInteraction(PlayerInteractEvent e)
	{
		if(e.getClickedBlock() == null || e.isCancelled() || e.getAction() != Action.RIGHT_CLICK_BLOCK)
		{
			return;
		}
		Location loc = e.getPlayer().getLocation();
		switch(e.getClickedBlock().getType())
		{
			case PURPUR_STAIRS -> InGameUtilities.playWorldSound(loc, Sound.BLOCK_IRON_DOOR_OPEN, SoundCategory.PLAYERS, 0.5f, 1);
			case DEAD_FIRE_CORAL,DEAD_HORN_CORAL->  InGameUtilities.playWorldSound(loc, Sound.BLOCK_CHEST_OPEN, SoundCategory.PLAYERS, 0.5f, 0.8f);
			case DEAD_BUBBLE_CORAL,DEAD_BUBBLE_CORAL_FAN ->  InGameUtilities.playWorldSound(loc, "gun.hud.bag_open", SoundCategory.PLAYERS, 0.5f, 1f);
			case DEAD_HORN_CORAL_FAN -> InGameUtilities.playWorldSound(loc, Sound.BLOCK_GILDED_BLACKSTONE_HIT, SoundCategory.PLAYERS, 0.5f, 1f);
			case PURPLE_CANDLE -> InGameUtilities.playWorldSound(loc, "gun.hud.scraps", SoundCategory.PLAYERS, 0.5f, 2f);
			case PINK_CANDLE -> InGameUtilities.playWorldSound(loc, "gun.unload.barrettm107", SoundCategory.PLAYERS, 0.5f, 2f);
			case DEAD_FIRE_CORAL_FAN, DEAD_TUBE_CORAL_FAN, DEAD_BRAIN_CORAL -> InGameUtilities.playWorldSound(loc, Sound.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 0.5f, 0f);
		}
	}

}
