package fr.byxis.event;

import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.Fireland;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


public class playerManager implements Listener {

    private Fireland main;

    public playerManager(Fireland main)
    {
        this.main =main;
    }

    @EventHandler
    private void onRightClick(PlayerInteractAtEntityEvent e)
    {
        if(e.getRightClicked() instanceof Player interacted && !e.getRightClicked().getName().contains("CIT-"))
        {
            Player player = e.getPlayer();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a"+interacted.getName()+": §c"+Math.round(interacted.getHealth())+"\u2764"));
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

    @EventHandler
    private void PlayerKillZombie(EntityDeathEvent e)
    {
        if(e.getEntity().getKiller() != null && main.hashMapManager.getBooster() != null)
        {
            Player killer = e.getEntity().getKiller();
            int money = BasicUtilities.generateInt((int) main.hashMapManager.getBooster().getMoneyMin(), (int) main.hashMapManager.getBooster().getMoneyMax()+1);
            if(money > 0)
            {
                main.eco.depositPlayer(killer, money);
                killer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7Vous avez gagné "+money+"$ grâce au boost de "+((Player) Bukkit.getOfflinePlayer(main.hashMapManager.getBooster().getUuid())).getName()+"."));

            }
         }
    }

    @EventHandler
    private void playerQuit(PlayerQuitEvent e)
    {
        main.hashMapManager.removeTeleporting(e.getPlayer().getUniqueId());
    }

    @EventHandler
    private void playerPickup(EntityPickupItemEvent e)
    {
        if(e.getEntity() instanceof Player p && !p.isOp())
        {
            if(e.getItem().getItemStack().getType() == Material.IRON_DOOR)
            {
                e.setCancelled(true);
                e.getItem().remove();
            }
        }
    }
}
