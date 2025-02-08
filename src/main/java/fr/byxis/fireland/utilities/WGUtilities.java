package fr.byxis.fireland.utilities;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


public class WGUtilities {

    public static boolean isWithinRegion(Player player, String region)
    { return isWithinRegion(player.getLocation(), region); }

    public static boolean isWithinRegion(Block block, String region)
    { return isWithinRegion(block.getLocation(), region); }

    public static boolean isWithinRegion(Location loc, String region)
    {
        BlockVector3 v = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
        RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(loc.getWorld()));
        ApplicableRegionSet set = manager.getApplicableRegions(v);
        for (ProtectedRegion each : set)
            if (each.getId().contains(region))
                return true;
        return false;
    }

}
