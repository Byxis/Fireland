package fr.byxis.essaim;

import fr.byxis.fireland.Fireland;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class EssaimCommandCompleter implements TabCompleter {

    private static Fireland main;

    public EssaimCommandCompleter(Fireland main)
        {
            this.main = main;
        }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> l = new ArrayList<>();
        if(!(commandSender instanceof Player))
        {
            return null;
        }
        Player player = (Player) commandSender;
        if(!player.hasPermission("fireland.essaim.admin"))
        {
            return null;
        }

        if (strings.length == 1) {
            l.add("create");
            l.add("open");
            l.add("close");
            l.add("spawner");
            l.add("gui");
        }
        else if (strings.length == 2)
        {
            if(strings[0].equalsIgnoreCase("create") ||
                    strings[0].equalsIgnoreCase("open") ||
                    strings[0].equalsIgnoreCase("close"))
            {
                l.add("--name");
            }
            else if(strings[0].equalsIgnoreCase("spawner"))
            {
                l.add("activate");
                l.add("create");
                l.add("remove");
            }
        }
        else if (strings.length == 3)
        {
            if(strings[0].equalsIgnoreCase("create"))
            {
                l.add("--region");
            }
            else if(strings[0].equalsIgnoreCase("spawner"))
            {
                if(strings[1].equalsIgnoreCase("create") ||
                    strings[1].equalsIgnoreCase("activate") ||
                    strings[1].equalsIgnoreCase("remove"))
                {
                    l.add("--essaim");
                    l.addAll(main.essaimManager.existingEssaims.keySet());
                }
            }
        }
        else if (strings.length == 4)
        {
            if(strings[0].equalsIgnoreCase("spawner"))
            {
                if(strings[1].equalsIgnoreCase("create") ||
                        strings[1].equalsIgnoreCase("activate") ||
                        strings[1].equalsIgnoreCase("remove"))
                {
                    l.add("--name");
                    l.addAll(main.essaimManager.existingEssaims.get(strings[2]).keySet());
                }
            }
        }
        else if (strings.length == 5 && strings[0].equalsIgnoreCase("spawner"))
        {
            if(strings[1].equalsIgnoreCase("create"))
            {
                l.add("--mobtype");
                Collection<String> mythicMobsSet = MythicBukkit.inst().getMobManager().getMobNames();
                for (String mobType : mythicMobsSet) {
                    l.add(mobType);
                }
            }
        }
        else if (strings.length == 6)
        {
            if(strings[0].equalsIgnoreCase("spawner"))
            {
                l.add("--amount");
            }
        }
        else if (strings.length >= 7)
        {
            if(strings[0].equalsIgnoreCase("spawner"))
            {
                l.add("--command");
            }
        }
        return l;
    }
}
