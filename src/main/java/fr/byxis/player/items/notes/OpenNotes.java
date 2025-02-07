package fr.byxis.player.items.notes;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OpenNotes implements @NotNull Listener, CommandExecutor {

    private Fireland main;
    private NotesConfig config;

    public OpenNotes(Fireland main) {
        this.main = main;
        config = new NotesConfig(main, true);
    }

    public static List<String> usingSubstringMethod(String text, int n)
    {
        if (text == null || text.equalsIgnoreCase(""))
            return Collections.singletonList("§4* Le texte n'est pas lisible *");
        List<String> results = new ArrayList<>();
        int length = text.length();

        for (int i = 0; i < length; i += n)
        {
            results.add(text.substring(i, Math.min(length, i + n)));
        }

        return results;
    }

    public static List<String> usingSubstringMethod(String text, int n, String color)
    {
        if (text == null || text.equalsIgnoreCase(""))
            return Collections.singletonList("§4* Le texte n'est pas lisible *");
        List<String> results = new ArrayList<>();
        int length = text.length();

        for (int i = 0; i < length; i += n)
        {
            results.add(color + text.substring(i, Math.min(length, i + n)));
        }

        return results;
    }

    @EventHandler
    public void interactNote(PlayerInteractEvent e)
    {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.SMALL_AMETHYST_BUD)
        {
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta meta = (BookMeta) book.getItemMeta();
            meta.setAuthor("Fireland");
            meta.setTitle("Note");
            int x = e.getClickedBlock().getLocation().getBlockX();
            int y = e.getClickedBlock().getLocation().getBlockY();
            int z = e.getClickedBlock().getLocation().getBlockZ();
            String text = config.getConfig().getString(x + "." + y + "." + z + ".content");

            meta.setPages(usingSubstringMethod(text, 266));

            book.setItemMeta(meta);
            e.getPlayer().openBook(book);
            InGameUtilities.playWorldSound(e.getPlayer().getLocation(), Sound.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 0.1f, 1);
        }
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent e)
    {
        if (e.getBlockPlaced().getType() == Material.SMALL_AMETHYST_BUD)
        {
            int x = e.getBlockPlaced().getLocation().getBlockX();
            int y = e.getBlockPlaced().getLocation().getBlockY();
            int z = e.getBlockPlaced().getLocation().getBlockZ();
            config.getConfig().set(x + "." + y + "." + z + ".content", "");
            config.save();
            InGameUtilities.sendPlayerSucces(e.getPlayer(), "Une nouvelle note a été crée. Pour modifier son contenu, faites /fnote <text> en pointant sur la note");
        }
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent e)
    {
        if (e.getBlock().getType() == Material.SMALL_AMETHYST_BUD)
        {
            int x = e.getBlock().getLocation().getBlockX();
            int y = e.getBlock().getLocation().getBlockY();
            int z = e.getBlock().getLocation().getBlockZ();
            if (config.getConfig().get(x + "." + y + "." + z + ".content") != null)
            {
                config.getConfig().set(x + "." + y + "." + z, null);
            }
            if (config.getConfig().get(x + "." + y + "." + z) == null)
            {
                config.getConfig().set(x + "." + y, null);
            }
            if (config.getConfig().get(x + "." + y) == null)
            {
                config.getConfig().set("" + x, null);
            }
            config.save();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player p)
        {
            if (p.hasPermission("fireland.command.notes"))
            {
                Block block = p.getTargetBlockExact(10);
                if (block != null && block.getType() == Material.SMALL_AMETHYST_BUD)
                {
                    int x = block.getLocation().getBlockX();
                    int y = block.getLocation().getBlockY();
                    int z = block.getLocation().getBlockZ();
                    StringBuilder content = new StringBuilder(strings[0]);
                    for (int i = 0; i < strings.length; i++)
                    {
                        if (i != 0)
                        {
                            content.append(" ");
                            content.append(strings[i]);
                        }

                    }

                    config.getConfig().set(x + "." + y + "." + z + ".content", content.toString());
                    config.save();
                    InGameUtilities.sendPlayerSucces(p, "Vous avez modifié la note, il est écrit : " + content.toString());
                    config = new NotesConfig(main, false);
                }
            }
        }
        return false;
    }
}
