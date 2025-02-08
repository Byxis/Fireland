package fr.byxis.player.booster;

import fr.byxis.fireland.Fireland;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BoosterCommandCompleter implements TabCompleter {

    private final Fireland main;

    public BoosterCommandCompleter(Fireland _main)
    {
        this.main = _main;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> l = new ArrayList<>();
        if (commandSender.hasPermission("fireland.command.booster"))
        {
            if (strings.length == 1)
            {
                l.add("create");
                l.add("delete");
            }
            if (strings.length == 2)
            {
                l.add("Lvl---");
                l.add("1");
                l.add("2");
                l.add("3");
            }
            if (strings.length == 3)
            {
                l.add("Heures---");
                l.add("1");
                l.add("3");
                l.add("5");
                l.add("...");
            }
        }
        return l;
    }
}
