package fr.byxis.player.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import fr.byxis.fireland.Fireland;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;

public class PacketFunctions {

    private final Fireland main;

    public PacketFunctions(Fireland _main)
    {
        this.main = _main;
    }

    public static void sendWorldBorderWarningDistancePacket(Player player, double intensity) {
        int warningDistance = (int) (intensity * Integer.MAX_VALUE);
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SET_BORDER_WARNING_DISTANCE);
        packet.getIntegers().write(0, warningDistance);
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendWorldBorderWarningDistancePacket(Player player, double intensity, double distance) {
        int warningDistance = (int) (intensity * distance);
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SET_BORDER_WARNING_DISTANCE);
        packet.getIntegers().write(0, warningDistance);
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openDoor(Player p, Location loc, boolean openable)
    {
        Block doorBlock = loc.getBlock();

        // Make sure the block at the location is actually a door
        if (doorBlock.getType() != Material.IRON_DOOR) {
            return;
        }

        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
        packet.getBlockPositionModifier().write(0, new BlockPosition(loc.toVector()));
        packet.getBlockData().write(0, WrappedBlockData.createData(Material.AIR));

// Envoyer seulement au joueur autorisé
        ProtocolLibrary.getProtocolManager().sendServerPacket(p, packet);
    }

}
