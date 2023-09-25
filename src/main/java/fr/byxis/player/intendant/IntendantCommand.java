package fr.byxis.player.intendant;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.PermissionUtilities;
import fr.byxis.player.intendant.menu.MenuIntendant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static fr.byxis.player.intendant.menu.MenuChoixNation.OpenChoixNation;

public class IntendantCommand implements CommandExecutor {

    private final Fireland main;
    public IntendantCommand(Fireland main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player p)
        {
            if(p.hasPermission("fireland.command.intendant"))
            {
                if(PermissionUtilities.hasPermission(p, "fireland.nation.change") && !p.isOp())
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
