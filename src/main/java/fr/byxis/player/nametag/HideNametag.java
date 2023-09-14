package fr.byxis.player.nametag;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.nametagedit.plugin.NametagEdit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class HideNametag implements PacketListener {
    private ProtocolManager protocolManager;
    private Plugin plugin;

    public HideNametag(Plugin plugin,  ProtocolManager protocolManager) {
        this.plugin = plugin;
        this.protocolManager = protocolManager;
        this.protocolManager.addPacketListener(this);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();

        if (packet.getType().name().contains("PLAYER_INFO")) {
            if (!player.hasPermission("nametag.see")) {
                event.setCancelled(true);
            } else {
                WrappedChatComponent displayName = packet.getChatComponents().read(0);
                if (displayName != null) {
                    String name = displayName.getJson();
                    Player target = plugin.getServer().getPlayer(name);
                    if (target != null && !target.hasPermission("nametag.see")) {
                        displayName.setJson(NametagEdit.getApi().getNametag(target).getPrefix() + name + NametagEdit.getApi().getNametag(target).getSuffix());
                    }
                }
            }
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent packetEvent) {

    }

    @Override
    public ListeningWhitelist getSendingWhitelist() {
        return null;
    }

    @Override
    public ListeningWhitelist getReceivingWhitelist() {
        return null;
    }

    @Override
    public Plugin getPlugin() {
        return null;
    }
}
