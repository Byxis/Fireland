package fr.byxis.faction.essaim;

import fr.byxis.fireland.Fireland;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EssaimCommandCompleter implements TabCompleter {

    private static Fireland main;

    public EssaimCommandCompleter(Fireland main)
        {
            this.main = main;
        }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> l = new ArrayList<>();
        if(!(commandSender instanceof Player player))
        {
            return null;
        }
        if(!player.hasPermission("fireland.essaim.admin"))
        {
            return null;
        }

        if (strings.length == 1) {
            ArrayList<String> i1 = new ArrayList<>();
            i1.add("create");
            i1.add("open");
            i1.add("close");
            i1.add("spawner");
            i1.add("gui");
            i1.add("setblock");
            i1.add("set");
            i1.add("finish");
            i1.add("unfinish");


            l.addAll(addAll(strings[0], "", i1));

        }
        else if (strings.length == 2)
        {
            if(strings[0].equalsIgnoreCase("create") ||
                    strings[0].equalsIgnoreCase("open") ||
                    strings[0].equalsIgnoreCase("close")||
                    strings[0].equalsIgnoreCase("finish")||
                    strings[0].equalsIgnoreCase("unfinish")||
                    strings[0].equalsIgnoreCase("reset"))
            {
                l.addAll(addAll(strings[1], "--name", main.essaimManager.existingEssaims.keySet()));
            }
            else if(strings[0].equalsIgnoreCase("set"))
            {
                ArrayList<String> i1 = new ArrayList<>();
                i1.add("reset");
                i1.add("start");
                i1.add("entry");
                i1.add("hub");
                i1.add("key");
                i1.add("solo");

                l.addAll(addAll(strings[1], "", i1));

            }
            else if(strings[0].equalsIgnoreCase("spawner"))
            {
                ArrayList<String> i1 = new ArrayList<>();
                i1.add("activate");
                i1.add("create");
                i1.add("remove");
                i1.add("list");

                l.addAll(addAll(strings[1], "", i1));

            }
            else if(strings[0].equalsIgnoreCase("setblock") )
            {
                l.add("--x");
                Player p = (Player) commandSender;
                l.add(String.valueOf(Math.round(p.getTargetBlock(null, 50).getX())));
            }
        }
        else if (strings.length == 3)
        {
            if(strings[0].equalsIgnoreCase("create"))
            {
                l.add("--region");
            }
            else if(strings[0].equalsIgnoreCase("setblock"))
            {
                l.add("--y");
                Player p = (Player) commandSender;
                l.add(String.valueOf(Math.round(p.getTargetBlock(null, 50).getY())));
            }
            else if(strings[0].equalsIgnoreCase("set"))
            {
                l.add("--x");
                Player p = (Player) commandSender;
                l.add(String.valueOf(Math.round(p.getTargetBlock(null, 50).getX())));
            }
            else if(strings[0].equalsIgnoreCase("spawner"))
            {
                if(strings[1].equalsIgnoreCase("create") ||
                    strings[1].equalsIgnoreCase("activate") ||
                        strings[1].equalsIgnoreCase("remove")||
                        strings[1].equalsIgnoreCase("list"))
                {
                    l.addAll(addAll(strings[2], "--essaim", main.essaimManager.existingEssaims.keySet()));
                }
            }
        }
        else if (strings.length == 4)
        {
            if(strings[0].equalsIgnoreCase("create"))
            {
                ArrayList<String> i1 = new ArrayList<>();
                i1.add("--jour");
                i1.add("Lundi");
                i1.add("Mardi");
                i1.add("Mercredi");
                i1.add("Jeudi");
                i1.add("Vendredi");
                i1.add("Samedi");
                i1.add("Dimanche");

                l.addAll(addAll(strings[3], "--jour", i1));
            }
            else if(strings[0].equalsIgnoreCase("set"))
            {
                l.add("--y");
                Player p = (Player) commandSender;
                l.add(String.valueOf(Math.round(p.getTargetBlock(null, 50).getY())));
            }
            else if(strings[0].equalsIgnoreCase("setblock"))
            {
                l.add("--z");
                Player p = (Player) commandSender;
                l.add(String.valueOf(Math.round(p.getTargetBlock(null, 50).getZ())));
            }
            else if(strings[0].equalsIgnoreCase("spawner"))
            {
                if(strings[1].equalsIgnoreCase("create") ||
                        strings[1].equalsIgnoreCase("activate") ||
                        strings[1].equalsIgnoreCase("remove"))
                {
                    l.addAll(addAll(strings[3], "--name", main.essaimManager.existingEssaims.get(strings[2]).keySet()));
                }
            }
        }
        else if (strings.length == 5)
        {
            if(strings[0].equalsIgnoreCase("create"))
            {
                ArrayList<String> i1 = new ArrayList<>();
                for(int i =0; i < 24; i++)
                {
                    i1.add(""+i);
                }
                l.addAll(addAll(strings[4], "--heure", i1));
            }
            else if(strings[0].equalsIgnoreCase("set"))
            {
                l.add("--z");
                Player p = (Player) commandSender;
                l.add(String.valueOf(Math.round(p.getTargetBlock(null, 50).getZ())));
            }
            else if(strings[0].equalsIgnoreCase("setblock"))
            {
                l.addAll(addAll(strings[4], "--material", Material.values()));
            }
            else if(strings[0].equalsIgnoreCase("spawner"))
            {
                if(strings[1].equalsIgnoreCase("create"))
                {
                    l.addAll(addAll(strings[3], "--mobtype", MythicBukkit.inst().getMobManager().getMobNames()));
                }
            }

        }
        else if (strings.length == 6)
        {
            if(strings[0].equalsIgnoreCase("spawner"))
            {
                l.add("--amount");
            }
            else if(strings[0].equalsIgnoreCase("set"))
            {
                l.addAll(addAll(strings[5], "--essaim", main.essaimManager.existingEssaims.keySet()));
            }
        }
        else if (strings.length == 7)
        {
            if(strings[0].equalsIgnoreCase("spawner") && strings[1].equalsIgnoreCase("create"))
            {
                l.add("--activationdelay");
            }
        }
        else if (strings.length == 8)
        {
            if(strings[0].equalsIgnoreCase("spawner") && strings[1].equalsIgnoreCase("create"))
            {
                l.add("--spawndelay");
            }
        }
        else if (strings.length == 9)
        {
            if(strings[0].equalsIgnoreCase("spawner") && strings[1].equalsIgnoreCase("create"))
            {
                l.add("--isAffectedByDifficulty");
                l.add("true");
                l.add("false");
            }
        }
        else if (strings.length >= 9)
        {
            if(strings[0].equalsIgnoreCase("spawner") && strings[1].equalsIgnoreCase("create"))
            {
                l.add("--command");
            }
        }

        /*for(String str : l)
        {
            if(!l.toString().toLowerCase().startsWith(strings[strings.length - 1].toLowerCase()))
            {
                l.remove(str);
            }
        }*/
        return l;
    }

    private Collection<String> addAll(String arg, String defaultText, Material[] values)
    {
        ArrayList<String> l = new ArrayList();
        if(arg.equalsIgnoreCase(""))
        {
            l.add(defaultText);
        }
        for (Object str : values) {
            if(str.toString().toLowerCase().startsWith(arg.toLowerCase()))
            {
                l.add(str.toString());
            }
        }
        return l;
    }

    private Collection<String> addAll(String arg, String defaultText, Collection<String> collection)
    {
        ArrayList<String> l = new ArrayList();
        if(arg.equalsIgnoreCase(""))
        {
            l.add(defaultText);
        }
        for (Object str : collection) {
            if(str.toString().toLowerCase().startsWith(arg.toLowerCase()))
            {
                l.add(str.toString());
            }
        }
        return l;
    }

    private ArrayList<String> addAll(String arg, String defaultText, HashMap set)
    {
        ArrayList<String> l = new ArrayList();
        if(arg.equalsIgnoreCase(""))
        {
            l.add(defaultText);
        }
        for (Object str : set.keySet()) {
            if(str.toString().toLowerCase().startsWith(arg.toLowerCase()))
            {
                l.add(str.toString());
            }
        }
        return l;
    }
}
