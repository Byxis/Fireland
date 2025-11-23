package fr.byxis.player.level;

import fr.byxis.fireland.utilities.ListUtilities;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LevelTabCompleter implements TabCompleter
{
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
            @NotNull String[] args)
    {
        ArrayList<String> list = new ArrayList<String>();

        if (commandSender instanceof Player p && !p.hasPermission("fireland.admin.level"))
        {
            list.addAll(ListUtilities.tabList(args[0], "", Bukkit.getOnlinePlayers()));
        }
        else
        {
            ArrayList<String> texts = new ArrayList<String>();
            if (args.length == 1)
            {
                texts.add("set");
                texts.add("add");
                texts.add("remove");
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    texts.add(p.getName());
                }
                texts.addAll(ListUtilities.tabList(args[0], "", texts));
            }
            if (args.length == 2)
            {
                if (args[0].equalsIgnoreCase("set"))
                {
                    texts.add("canchange");
                    texts.add("nation");
                    texts.add("rang");
                }
                texts.add("level");
                texts.add("xp");
                texts.addAll(ListUtilities.tabList(args[0], "", texts));
            }
            if (args.length == 3)
            {
                if (args[1].equalsIgnoreCase("nation"))
                {
                    texts.add("Neutre");
                    texts.add("Bannis");
                    texts.add("Etat");
                }
                else
                {
                    texts.add("10");
                    texts.add("100");
                    texts.add("1000");
                }
                texts.addAll(ListUtilities.tabList(args[0], "", texts));
            }
            if (args.length == 4)
            {
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    texts.add(p.getName());
                }
                texts.addAll(ListUtilities.tabList(args[0], "", texts));
            }
            list.addAll(texts);
        }

        return list;
    }
}
