package fr.byxis.faction;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import fr.byxis.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Optional;

public class FactionPacket {

    private final Main main;

    public FactionPacket(Main main)
    {
        this.main = main;
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation infos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        if(infos != null && p.hasPermission("fireland.admin.debug"))
        {
            ProtocolManager pm = ProtocolLibrary.getProtocolManager();
            PacketContainer packet = pm.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            packet.getModifier().writeDefaults();
            WrappedChatComponent wrappedChatComponent = WrappedChatComponent.fromText("§4TEST");
            Optional<WrappedChatComponent> opt = Optional.of(wrappedChatComponent);

            WrappedDataWatcher metadata = new WrappedDataWatcher();
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.get(WrappedChatComponent.class)), opt);
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x01 | 0x08 | 0x10)); //isSmall, noBasePlate, set Marker

            packet.getIntegers().write(0, p.getEntityId());
            for(Player ppl : Bukkit.getOnlinePlayers())
            {
                if(ppl.hasPermission("fireland.admin.debug"))
                {
                    pm.sendServerPacket(ppl, packet);
                }
            }
        }
    }
}
