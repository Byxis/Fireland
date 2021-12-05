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
				switch(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType())
				{
					case ACACIA_LEAVES:
						reduce = true;
						break;
					case BIRCH_LEAVES:
						reduce = true;
						break;
					case DARK_OAK_LEAVES:
						reduce = true;
						break;
					case JUNGLE_LEAVES:
						reduce = true;
						break;
					case OAK_LEAVES:
						reduce = true;
						break;
					case SPRUCE_LEAVES:
						reduce = true;
						break;
					case LEGACY_LEAVES:
						reduce = true;
						break;
					case LEGACY_LEAVES_2:
						reduce = true;
						break;
					case WHEAT:
						reduce = true;
						break;
				default:
					break;
				}
				
				if(reduce) 
				{
					e.setDamage(e.getDamage()/2);
				}
			}
		}
	}
	
}
