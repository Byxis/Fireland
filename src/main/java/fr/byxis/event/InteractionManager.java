package fr.byxis.event;

import fr.byxis.fireland.utilities.InGameUtilities;
import java.util.ArrayList;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractionManager implements Listener
{

    private final ArrayList<Material> m_forbiddenMats;

    public InteractionManager()
    {
        m_forbiddenMats = new ArrayList<>();
        m_forbiddenMats.add(Material.GRINDSTONE);
        m_forbiddenMats.add(Material.JUKEBOX);
        m_forbiddenMats.add(Material.ENCHANTING_TABLE);
        m_forbiddenMats.add(Material.ANVIL);
        m_forbiddenMats.add(Material.FURNACE);
        m_forbiddenMats.add(Material.BLAST_FURNACE);
        m_forbiddenMats.add(Material.DAMAGED_ANVIL);
        m_forbiddenMats.add(Material.SMOKER);
        m_forbiddenMats.add(Material.HOPPER);
        m_forbiddenMats.add(Material.SHULKER_BOX);
        m_forbiddenMats.add(Material.BREWING_STAND);
        m_forbiddenMats.add(Material.DROPPER);
        m_forbiddenMats.add(Material.DISPENSER);
        m_forbiddenMats.add(Material.BARREL);
        m_forbiddenMats.add(Material.BLACK_BED);
        m_forbiddenMats.add(Material.BLUE_BED);
        m_forbiddenMats.add(Material.BROWN_BED);
        m_forbiddenMats.add(Material.CYAN_BED);
        m_forbiddenMats.add(Material.GRAY_BED);
        m_forbiddenMats.add(Material.GREEN_BED);
        m_forbiddenMats.add(Material.LIGHT_BLUE_BED);
        m_forbiddenMats.add(Material.LIGHT_GRAY_BED);
        m_forbiddenMats.add(Material.MAGENTA_BED);
        m_forbiddenMats.add(Material.ORANGE_BED);
        m_forbiddenMats.add(Material.PINK_BED);
        m_forbiddenMats.add(Material.PURPLE_BED);
        m_forbiddenMats.add(Material.RED_BED);
        m_forbiddenMats.add(Material.WHITE_BED);
        m_forbiddenMats.add(Material.YELLOW_BED);
        m_forbiddenMats.add(Material.MAGENTA_BED);
        m_forbiddenMats.add(Material.CAVE_VINES);
        m_forbiddenMats.add(Material.CAVE_VINES_PLANT);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void removeInteraction(PlayerInteractEvent e)
    {
        if (e.getClickedBlock() == null || e.isCancelled() || e.getPlayer().getGameMode() == GameMode.CREATIVE)
        {
            return;
        }
        if (m_forbiddenMats.contains(e.getClickedBlock().getType()) && e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            e.setCancelled(true);
            return;
        }

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            Block blk = e.getClickedBlock();
            if ((blk.getType().name().startsWith("POTTED_") || blk.getType() == Material.FLOWER_POT)
                    && !(e.getPlayer().getGameMode() == GameMode.CREATIVE))
            {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void playSoundInteraction(PlayerInteractEvent e)
    {
        if (e.getClickedBlock() == null || e.isCancelled() || e.getAction() != Action.RIGHT_CLICK_BLOCK)
        {
            return;
        }
        Location loc = e.getPlayer().getLocation();
        switch (e.getClickedBlock().getType())
        {
            case PURPUR_STAIRS -> InGameUtilities.playWorldSound(loc, Sound.BLOCK_IRON_DOOR_OPEN, SoundCategory.PLAYERS, 0.5f, 1);
            case DEAD_FIRE_CORAL, DEAD_HORN_CORAL ->
                    InGameUtilities.playWorldSound(loc, Sound.BLOCK_CHEST_OPEN, SoundCategory.PLAYERS, 0.5f, 0.8f);
            case DEAD_BUBBLE_CORAL, DEAD_BUBBLE_CORAL_FAN ->
                    InGameUtilities.playWorldSound(loc, "gun.hud.bag_open", SoundCategory.PLAYERS, 0.5f, 1f);
            case DEAD_HORN_CORAL_FAN ->
                    InGameUtilities.playWorldSound(loc, Sound.BLOCK_GILDED_BLACKSTONE_HIT, SoundCategory.PLAYERS, 0.5f, 1f);
            case PURPLE_CANDLE -> InGameUtilities.playWorldSound(loc, "gun.hud.scraps", SoundCategory.PLAYERS, 0.5f, 2f);
            case PINK_CANDLE -> InGameUtilities.playWorldSound(loc, "gun.unload.barrettm107", SoundCategory.PLAYERS, 0.5f, 2f);
            case DEAD_FIRE_CORAL_FAN, DEAD_TUBE_CORAL_FAN, DEAD_BRAIN_CORAL ->
                    InGameUtilities.playWorldSound(loc, Sound.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 0.5f, 0f);
        }
    }
}
