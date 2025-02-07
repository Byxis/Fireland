package fr.byxis.event;

import fr.byxis.fireland.Fireland;
import net.royawesome.jlibnoise.MathHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Stairs implements Listener
{
    
    private final Fireland main;

    public Stairs(Fireland _main)
    {
        this.main = _main;
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void playerInteract(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();
        Block block = e.getClickedBlock();
        if (block == null)
        {
            return;
        }
        BlockData blockData = block.getBlockData();

        if ((blockData instanceof org.bukkit.block.data.type.Stairs) && e.getAction() == Action.RIGHT_CLICK_BLOCK && p.getItemInHand().getType() == Material.AIR)
        {
            org.bukkit.block.data.type.Stairs stairs = (org.bukkit.block.data.type.Stairs) block.getBlockData();
            
            if (stairs.getHalf() == Half.TOP || p.getVehicle() instanceof ArmorStand)
            {
                return;
            }
            
            Location loc = block.getLocation();
            loc.setX(loc.getX() + 0.5);
            loc.setY(loc.getY() - 0.31);
            loc.setZ(loc.getZ() + 0.5);
            
            int face = 0;
            
            if (stairs.getFacing() == BlockFace.NORTH)
            {
                face = 2;
            }
            else if (stairs.getFacing() == BlockFace.WEST)
            {
                face = 1;
            }
            else if (stairs.getFacing() == BlockFace.EAST)
            {
                face = -1;
            }
            
            loc.setYaw((face * 90) - 180);
            Location locUp = block.getLocation();
            locUp.setY(locUp.getY() + 1);
            
            
            if ((p.getLocation().distance(loc) > 1.5 && (p.getLocation().getY() > loc.getX() || p.getLocation().getY() > loc.getX() + 1)))
            {
                return;
            }
            
            for (Entity near : getEntitiesAroundPoint(loc, 2))
            {
                if (near instanceof ArmorStand && near.getLocation() == loc)
                {
                    return;
                }
            }
            
            if (!(new Location(locUp.getWorld(), locUp.getX(), locUp.getY(), locUp.getZ()).getBlock().isPassable()))
            {
                return;
            }


            final ArmorStand chair = (ArmorStand) p.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            
            chair.setPassenger(p);
            chair.setGravity(false);
            chair.setVisible(false);
            chair.setCollidable(false);
            chair.setSmall(true);
            chair.setCanPickupItems(false);
            chair.setSilent(true);
            chair.setInvulnerable(true);
            
            new BukkitRunnable() 
            {
                @Override
                public void run() {
                    if (chair.getPassengers().isEmpty())
                    {
                        chair.remove();
                        this.cancel();
                    }
                }
            }.runTaskTimer(main, 1, 20);
        }
    }
    
    public static List<Entity> getEntitiesAroundPoint(Location location, double radius) {
        List<Entity> entities = new ArrayList<>();
        World world = location.getWorld();

        // To find chunks we use chunk coordinates (not block coordinates!
        // )
        int smallX = MathHelper.floor((location.getX() - radius) / 16.0D);
        int bigX = MathHelper.floor((location.getX() + radius) / 16.0D);
        int smallZ = MathHelper.floor((location.getZ() - radius) / 16.0D);
        int bigZ = MathHelper.floor((location.getZ() + radius) / 16.0D);

        for (int x = smallX; x <= bigX; x++) 
        {
            for (int z = smallZ; z <= bigZ; z++) 
            {
                assert world != null;
                if (world.isChunkLoaded(x, z)) 
                {
                    entities.addAll(Arrays.asList(world.getChunkAt(x, z).getEntities())); // Add all entities from this chunk to the list
                }
            }
        }
        // Remove the entities that are within the box above but not actually in the sphere we defined with the radius and location
        // This code below could probably be replaced in Java 8 with a stream -> filter
        // Create an iterator so we can loop through the list while removing entries
        // If the entity is outside of the sphere...
        // Remove it
        entities.removeIf(entity -> entity.getLocation().distanceSquared(location) > radius * radius);
        return entities;
    }
}
