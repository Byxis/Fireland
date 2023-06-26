package fr.byxis.player.items.lamp;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

import static fr.byxis.player.items.itemDurability.*;

public class lamp implements Listener {

    private final HashMap<UUID, Double> delay;
    private final Fireland main;

    public lamp(Fireland main)
    {
        this.main = main;
        delay = new HashMap<UUID, Double>();
        loop();
    }



    public void saveFloatInItem(ItemStack item, float value) {
        NamespacedKey key = new NamespacedKey("fireland", "durability");
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(key, PersistentDataType.FLOAT, value);
        item.setItemMeta(meta);
    }

    public float loadFloatFromItem(ItemStack item) {
        NamespacedKey key = new NamespacedKey("fireland", "durability");
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(key, PersistentDataType.FLOAT)) {
            return container.get(key, PersistentDataType.FLOAT);
        } else {
            return 100;
        }
    }

    @EventHandler
    public void rightClickEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getItem() != null) {
            if(e.getItem().getType() == Material.END_ROD || (e.getItem().getType() == Material.RABBIT_HIDE && e.getItem().getItemMeta().hasCustomModelData() && e.getItem().getItemMeta().getCustomModelData() == 1))
            {
                if(!delay.containsKey(p.getUniqueId()))
                {
                    if(e.getItem().getType() == Material.END_ROD)
                    {
                        InGameUtilities.playWorldSound(p.getLocation(), "gun.torch.click", SoundCategory.PLAYERS, 0.1f, 1);
                        e.getItem().setType(Material.RABBIT_HIDE);
                        ItemMeta meta = e.getItem().getItemMeta();
                        meta.setCustomModelData(1);
                        e.getItem().setItemMeta(meta);
                        delay.put(p.getUniqueId(), 20D);
                    }
                    else if(getDurability(e.getItem()) > 0)
                    {
                        InGameUtilities.playWorldSound(p.getLocation(), "gun.torch.click", SoundCategory.PLAYERS, 0.1f, 1);
                        e.getItem().setType(Material.END_ROD);
                        delay.put(p.getUniqueId(), 20D);
                    }
                    else
                    {
                        InGameUtilities.playWorldSound(p.getLocation(), "gun.torch.click", SoundCategory.PLAYERS, 0.1f, 1);
                        delay.put(p.getUniqueId(), 20D);
                    }
                }
            }
        }
    }

    private void loop()
    {
        int period = 20;
        new BukkitRunnable(){
            @Override
            public void run() {
                for(Player p : Bukkit.getOnlinePlayers())
                {
                    if(delay.containsKey(p.getUniqueId()))
                    {
                        delay.replace(p.getUniqueId(), delay.get(p.getUniqueId()) - period);
                        if(delay.get(p.getUniqueId()) <= 0)
                        {
                            delay.remove(p.getUniqueId());
                        }
                    }
                    for(ItemStack item : p.getInventory().getContents())
                    {
                        if(item != null)
                        {
                            if(item.getType() == Material.END_ROD)
                            {
                                if(remLoreDurability(item, 0.1f))
                                {
                                    item.setType(Material.RABBIT_HIDE);
                                    ItemMeta meta = item.getItemMeta();
                                    meta.setCustomModelData(1);
                                    item.setItemMeta(meta);
                                }
                            }
                        }

                    }
                }
            }
        }.runTaskTimer(main, 0, period);
    }

    @EventHandler
    public void recharge(InventoryClickEvent e)
    {
        if(e.getAction() != InventoryAction.SWAP_WITH_CURSOR)
        {
            return;
        }
        ItemStack cursor = e.getCursor();
        ItemStack item = e.getCurrentItem();
        if(e.getCursor().getType() == Material.INK_SAC && (e.getCurrentItem().getType() == Material.END_ROD
                || e.getCurrentItem().getType() == Material.RABBIT_HIDE
                || e.getCurrentItem().getType() == Material.RED_DYE))
        {
            cursor.setAmount(cursor.getAmount()-1);
            setLoreDurability(item, 100);
            e.setCancelled(true);
        }
    }

}
