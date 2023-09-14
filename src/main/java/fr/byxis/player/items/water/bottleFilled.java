package fr.byxis.player.items.water;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.Cauldron;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import static fr.byxis.fireland.HashMapManager.canPurify;
import static fr.byxis.fireland.HashMapManager.setPurify;
import static fr.byxis.fireland.utilities.InGameUtilities.debugp;

public class bottleFilled implements Listener {

    private final Fireland main;

    public bottleFilled(Fireland main)
    {
        this.main = main;
    }

    @EventHandler
    public void playerFillBottle(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();
        if(p.getItemInHand().getType() == Material.GLASS_BOTTLE && !p.hasCooldown(Material.GLASS_BOTTLE))
        {
            if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
            {
                e.setCancelled(true);
                Block block = e.getClickedBlock().getLocation().getBlock();
                Block targetBlock = p.getTargetBlock(null, 5);
                if(block.getType() == Material.WATER_CAULDRON)
                {
                    Cauldron c = (Cauldron) block.getState().getData();
                    if(c != null && c.isFull())
                    {
                        p.setCooldown(Material.GLASS_BOTTLE, 20);
                        InGameUtilities.playWorldSound(p.getLocation(), Sound.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS, 1, 1);
                        getWater(p, true);
                    }
                }
                else if(block.getType() == Material.WATER) {
                    getWater(p, false);
                }
                else if(block.getLocation().add(0,1,0).getBlock().getType() == Material.WATER)
                {
                    InGameUtilities.playWorldSound(p.getLocation(), Sound.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS, 1, 1);
                    getWater(p, false);
                }
                else if (targetBlock.getType() == Material.WATER)
                {
                    InGameUtilities.playWorldSound(p.getLocation(), Sound.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS, 1, 1);
                    getWater(p, false);
                }
                else if(block.getBlockData() instanceof Waterlogged waterlogged)
                {
                    if(waterlogged.isWaterlogged())
                        getWater(p, false);
                }
            }
            else if(e.getAction() == Action.RIGHT_CLICK_AIR)
            {
                Block block = p.getTargetBlock(null, 5);
                if (block.getType() == Material.WATER)
                {
                    InGameUtilities.playWorldSound(p.getLocation(), Sound.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS, 1, 1);
                    getWater(p, false);
                    e.setCancelled(true);
                }
            }

        }
    }

    private void getWater(Player p, boolean purify)
    {
        p.setCooldown(Material.GLASS_BOTTLE, 20);
        if(purify)
        {
            debugp(1);
            givePotion(p, Color.fromRGB(150, 180, 255));
        }
        else if (canPurify(p))
        {
            givePotion(p, Color.fromRGB(150, 180, 255));

            InGameUtilities.sendPlayerInformation(p, "Votre expérience en survie vous a permit de purifier une bouteille d'eau.");
            setPurify(p, false);
            int minutes;
            if(p.hasPermission("fireland.thirst.3"))
            {
                minutes = 15;
            }
            else if(p.hasPermission("fireland.thirst.2"))
            {
                minutes = 30;
            }
            else
            {
                minutes = 45;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    setPurify(p, true);
                    InGameUtilities.sendPlayerInformation(p, "Vous pouvez re-purifier une bouteille d'eau.");
                }
            }.runTaskLater(main, minutes*60*20);
        }
        else
        {
            debugp(3);
            givePotion(p, Color.OLIVE);
        }
    }

    public void givePotion(Player p, Color c)
    {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.WATER));
        meta.setColor(c);
        potion.setItemMeta(meta);
        p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
        p.getInventory().addItem(potion);
    }

    @EventHandler
    public void cauldronEvent(CauldronLevelChangeEvent e)
    {
        if(e.getEntity() instanceof Player p && p.getItemInHand().getType() == Material.GLASS_BOTTLE)
        {
            e.setCancelled(true);
        }
    }

}
