package fr.byxis.player.intendant;

import fr.byxis.fireland.Fireland;
import fr.byxis.player.intendant.menu.MenuIntendant;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static fr.byxis.player.intendant.menu.MenuChoixNation.OpenChoixNation;
import static fr.byxis.player.level.LevelStorage.getPlayerLevel;

public class IntendantCommand implements CommandExecutor {

    private final Fireland main;

    public IntendantCommand(Fireland _main)
    {
        this.main = _main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player p)
        {
            if (p.hasPermission("fireland.admin.intendant"))
            {
                MenuIntendant.OpenIntendant(main, p);
            }
        }
        else if (strings.length == 1)
        {
            Player p = null;

            for (Player op : Bukkit.getOnlinePlayers())
            {
                if (op.getName().equals(strings[0]))
                {
                    p = op;
                    break;
                }
            }
            if (p != null)
            {
                if (getPlayerLevel(p.getUniqueId()).canChange())
                {
                    OpenChoixNation(main, p);
                }
                else
                {
                    MenuIntendant.OpenIntendant(main, p);
                }
            }
        }
        return false;
    }
}
