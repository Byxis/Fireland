package fr.byxis.event;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class cobwebDamage implements Listener {
	
	public void damagePlayerInCobweb(Player p)
	{
		if((p.getLocation().getBlock() != null && p.getLocation().getBlock().getType() == Material.COBWEB) 
				|| (p.getLocation().getBlock().getRelative(BlockFace.UP) != null && p.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.COBWEB))
		{
			p.damage(1.5D);
		}
	}
}
