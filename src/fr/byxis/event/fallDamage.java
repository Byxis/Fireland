package fr.byxis.event;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class fallDamage implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void playerFallEvent(EntityDamageEvent  e)
	{
		if(e.getEntity() instanceof Player)
		{
			Player player = (Player) e.getEntity();
			if(e.getCause() == DamageCause.FALL)
			{
				e.setDamage(e.getDamage()*1.25);
				boolean reduce = false;
				switch (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType()) {
					case ACACIA_LEAVES, LEGACY_LEAVES_2, BIRCH_LEAVES, DARK_OAK_LEAVES, JUNGLE_LEAVES,
							OAK_LEAVES, SPRUCE_LEAVES, LEGACY_LEAVES, WHEAT -> reduce = true;
					default -> {
					}
				}
				
				if(reduce) 
				{
					e.setDamage(e.getDamage()/2);
				}
			}
		}
	}
	
}
