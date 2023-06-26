package fr.byxis.faction.faction;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import fr.byxis.fireland.Fireland;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Optional;

public class FactionPacket {

    private final Fireland main;

    public FactionPacket(Fireland main)
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
            updatePlayerNameTags();
        }
    }

    public void updatePlayerNameTags() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("fireland.nametag")) {
                setPlayerNameTagColor(player, "§c");
            } else {
                hidePlayerNameTag(player);
            }
        }
    }

    public void setPlayerNameTagColor(Player player, String color) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
        packet.getIntegers().write(0, 0);
        packet.getStrings().write(0, player.getName());
        packet.getChatComponents().write(1, WrappedChatComponent.fromText(color + player.getName()));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hidePlayerNameTag(Player player) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
        packet.getIntegers().write(0, 1);
        packet.getStrings().write(0, "");
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Utilisation de la fonction pour mettre à jour les pseudos des joueurs


}
