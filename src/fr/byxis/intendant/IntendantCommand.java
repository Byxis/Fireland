package fr.byxis.intendant;

import fr.byxis.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class IntendantCommand implements CommandExecutor {

    private final Main main;
    public IntendantCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player p)
        {
            if(p.hasPermission("fireland.command.intendant"))
            {
                MenuIndendant function = new MenuIndendant(main);
                function.OpenIntendant(p);
            }
        }
        return false;
    }
}
