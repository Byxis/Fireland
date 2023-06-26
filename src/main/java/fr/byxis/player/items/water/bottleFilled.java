package fr.byxis.player.items.water;

import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class bottleFilled implements Listener {

    @EventHandler
    public void playerFillBottle(PlayerInteractEvent e)
    {
        if(e.getPlayer().getItemInHand().getType() == Material.GLASS_BOTTLE)
        {
            if(e.getAction() == Action.RIGHT_CLICK_BLOCK )
            {
                Block block = e.getClickedBlock().getLocation().getBlock();
                if(block.getType() == Material.WATER ||block.getLocation().add(0,1,0).getBlock().getType() == Material.WATER) {
                    e.setCancelled(true);
                    ItemStack potion = new ItemStack(Material.POTION);
                    PotionMeta meta = (PotionMeta) potion.getItemMeta();
                    meta.setBasePotionData(new PotionData(PotionType.WATER));
                    meta.setColor(Color.OLIVE);
                    potion.setItemMeta(meta);
                    e.getPlayer().getItemInHand().setAmount(e.getPlayer().getItemInHand().getAmount() - 1);
                    e.getPlayer().getInventory().addItem(potion);
                }
                else if(block.getBlockData() instanceof Waterlogged waterlogged)
                {
                    if(waterlogged.isWaterlogged())
                    {
                        e.setCancelled(true);
                        ItemStack potion = new ItemStack(Material.POTION);
                        PotionMeta meta = (PotionMeta) potion.getItemMeta();
                        meta.setBasePotionData(new PotionData(PotionType.WATER));
                        meta.setColor(Color.OLIVE);
                        potion.setItemMeta(meta);
                        e.getPlayer().getItemInHand().setAmount(e.getPlayer().getItemInHand().getAmount() - 1);
                        e.getPlayer().getInventory().addItem(potion);
                    }
                }
                else if(block.getType() == Material.CAULDRON)
                {
                    e.setCancelled(false);
                }
                else

                {
                    e.setCancelled(true);
                }
            }
            else if(e.getAction() == Action.RIGHT_CLICK_AIR)
            {
                Block block = e.getPlayer().getTargetBlock(null, 5);
                if (block.getType() == Material.WATER)
                {
                    e.setCancelled(true);
                    ItemStack potion = new ItemStack(Material.POTION);
                    PotionMeta meta = (PotionMeta) potion.getItemMeta();
                    meta.setBasePotionData(new PotionData(PotionType.WATER));
                    meta.setColor(Color.OLIVE);
                    potion.setItemMeta(meta);
                    e.getPlayer().getItemInHand().setAmount(e.getPlayer().getItemInHand().getAmount() - 1);
                    e.getPlayer().getInventory().addItem(potion);
                }
                else
                {
                    e.setCancelled(true);
                }
            }

        }
    }

}
