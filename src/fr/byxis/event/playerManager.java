package fr.byxis.event;

import fr.byxis.main.Main;
import fr.byxis.main.utilities.BasicUtilities;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;


public class playerManager implements Listener {

    private Main main;

    public playerManager(Main main)
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
    public void PlayerClickInteraction(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(event.getClickedBlock().getType() == Material.DEAD_HORN_CORAL_FAN){
                event.getPlayer().playSound(event.getClickedBlock().getLocation(), Sound.BLOCK_GILDED_BLACKSTONE_HIT, SoundCategory.BLOCKS,1,1);
            }
            else if(event.getClickedBlock().getType() == Material.DEAD_TUBE_CORAL_FAN || event.getClickedBlock().getType() == Material.DEAD_FIRE_CORAL_FAN ||event.getClickedBlock().getType() == Material.DEAD_BRAIN_CORAL){
                event.getPlayer().playSound(event.getClickedBlock().getLocation(), "minecraft:entity.horse.armor", SoundCategory.BLOCKS,1,1);
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

}
