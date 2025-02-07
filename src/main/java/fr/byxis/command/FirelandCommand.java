package fr.byxis.command;

import fr.byxis.fireland.Fireland;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static fr.byxis.fireland.Save.ReloadAll;

public class FirelandCommand implements CommandExecutor {

    public FirelandCommand(Fireland _main)
    {
        this.main = _main;
    }

    private final Fireland main;
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length >= 1 && strings[0].equalsIgnoreCase("reload"))
        {
            if (strings.length == 1)
            {
                ReloadAll(main);
            }
        }

        return false;
    }
}
