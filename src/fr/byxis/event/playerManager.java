package fr.byxis.event;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;


public class playerManager implements Listener {

    @EventHandler
    private void onRightClick(PlayerInteractAtEntityEvent e)
    {
        if(e.getRightClicked() instanceof Player interacted)
        {
            Player player = e.getPlayer();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a"+interacted.getName()+": §c"+interacted.getHealth()+"\u2764"));
        }
    }

    @EventHandler
    private void playerDestroyItemframe(EntityDamageByEntityEvent e)
    {
        if(e.getEntity() instanceof ItemFrame)
        {
            if(e.getDamager() instanceof Player p)
            {
                if(p.getGameMode() != GameMode.CREATIVE)
                {
                    e.setCancelled(true);
                }
            }
        }
    }

}
