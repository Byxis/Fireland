package fr.byxis.event;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockSpreadEvent;

public class WorldGeneration implements Listener
{

    @EventHandler
    public void onBlockGrow(BlockSpreadEvent e)
    {
        if (e.getNewState().getType().equals(Material.VINE))
        {
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onCoralDead(BlockFadeEvent e)
    {
        if (e.getBlock().getType().toString().contains("CORAL"))
        {
            e.setCancelled(true);
        }
    }
}
