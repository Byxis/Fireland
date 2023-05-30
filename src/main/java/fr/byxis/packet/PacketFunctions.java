package fr.byxis.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import fr.byxis.fireland.Fireland;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;

public class PacketFunctions {

    private final Fireland main;

    public PacketFunctions(Fireland main)
    {
        this.main = main;
    }

    public void playBorderPackets(Player player, boolean warn)
    {

        @SuppressWarnings("deprecation")
        PacketContainer container = main.protocolManager.createPacket(PacketType.Play.Server.SET_BORDER_WARNING_DISTANCE);
        container.getModifier().writeDefaults();
        if(warn)
        {
            container.getIntegers().write(0, 999999999);
            main.protocolManager.broadcastServerPacket(container, player, false);
        }
        else
        {
            container.getIntegers().write(0, 0);
            main.protocolManager.broadcastServerPacket(container, player, false);
        }


    }

    public void openDoor(Player p, Location loc, boolean openable)
    {
        Block doorBlock = loc.getBlock();

        // Make sure the block at the location is actually a door
        if (doorBlock.getType() != Material.IRON_DOOR) {
            return;
        }

        // Open the door
        Openable doorData = (Openable) doorBlock.getState().getBlockData();
        //doorData.setOpen(openable);
        doorBlock.setBlockData(doorData);
        // Send a packet to the player to update the door's appearance
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
        WrappedBlockData wrappedDoorData = WrappedBlockData.createData(doorBlock.getType(), doorBlock.getData());
        packet.getBlockData().write(0, wrappedDoorData);
        packet.getIntegers().write(0, doorBlock.getX()).write(1, doorBlock.getY()).write(2, doorBlock.getZ());
        try {
            main.protocolManager.sendServerPacket(p, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
