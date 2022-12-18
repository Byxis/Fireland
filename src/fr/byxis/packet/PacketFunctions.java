package fr.byxis.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import fr.byxis.main.Main;
import org.bukkit.entity.Player;

public class PacketFunctions {

    private final Main main;

    public PacketFunctions(Main main)
    {
        this.main = main;
    }

    public void playBorderPackets(Player player, boolean warn)
    {

        @SuppressWarnings("deprecation")
        PacketContainer container = main.protocolManager.createPacket(PacketType.Play.Server.SET_BORDER_WARNING_DISTANCE);

        if(warn)
        {
            container.getIntegers().write(0, 2999997);
            main.protocolManager.broadcastServerPacket(container, player, false);
        }
        else
        {
            container.getIntegers().write(0, 0);
            main.protocolManager.broadcastServerPacket(container, player, false);
        }

    }
}
