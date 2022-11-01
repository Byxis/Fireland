package fr.byxis.workshop;

import fr.byxis.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class workshopManager implements CommandExecutor, TabCompleter {

    private final Main main;
    public workshopManager(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String arg, @NotNull String[] args) {
        workshopFunction wf = new workshopFunction(main, (Player) sender);
        if(cmd.getName().equalsIgnoreCase("workshop") && args.length >= 1)
        {
            if(args[0].equalsIgnoreCase("newrecipe") && args.length >= 6 && ((Player) sender).hasPermission("fireland.command.workshop.admin"))
            {
                StringBuilder sb = new StringBuilder();
                for (int i = 5; i < args.length; i++){
                    sb.append(args[i]).append(" ");
                }

                String command = sb.toString().trim();
                wf.createRecipe(args[1], command, args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                return true;
            }
            else if(args[0].equalsIgnoreCase("learnrecipe") && args.length >= 3)
            {
                if(args.length >= 4)
                {
                    Player victim = (Player) Bukkit.getOfflinePlayer(args[3]);
                    wf.learnRecipe(args[1], victim.getUniqueId().toString(), Integer.parseInt(args[2]));
                }
                else
                {
                    wf.learnRecipe(args[1], ((Player)sender).getUniqueId().toString(), Integer.parseInt(args[2]));
                }
            }
        }
        else if(cmd.getName().equalsIgnoreCase("workshop"))
        {
            if(sender.hasPermission("fireland.command.workshop.admin"))
            {
                sender.sendMessage("§cUtilisation : /workshop (newrecipe/learnrecipe)");
            }
            else
            {
                sender.sendMessage("§cUtilisation :  /workshop ");
            }
        }
        return false;
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String arg, @NotNull String[] args) {
        List<String> l = new ArrayList<>();
        if(cmd.getName().equalsIgnoreCase("workshop") && ((Player)sender).hasPermission("fireland.command.workshop.admin"))
        {
            if(args.length == 1)
            {
                l.add("newrecipe");
                l.add("learnrecipe");
            }
            if(args.length >= 2)
            {
                if(args[0].equalsIgnoreCase("newrecipe"))
                {
                    if(args.length == 2)
                    {
                        l.add("--nom");
                    }
                    else if (args.length == 3)
                    {
                        l.add("D");
                        l.add("C");
                        l.add("B");
                        l.add("A");
                        l.add("--Type");
                    }
                    else if(args.length == 4)
                    {
                        l.add("10");
                        l.add("20");
                        l.add("--Nombre de scrap nécessaire");
                    }
                    else if(args.length == 5)
                    {
                        l.add("10");
                        l.add("20");
                        l.add("--Nombre de poudre à canon nécessaire");
                    }
                    else
                    {
                        l.add("--Commande");
                    }
                }
                else if(args[0].equalsIgnoreCase("learnrecipe"))
                {
                    if(args.length == 2)
                    {
                        l.add("--nom recette");
                    }
                    else if(args.length == 3)
                    {
                        l.add("1");
                        l.add("3");
                        l.add("10");
                        l.add("20");
                        l.add("--nombre de fois");
                    }
                    else if(args.length == 4)
                    {
                        for(Player player : Bukkit.getServer().getOnlinePlayers())
                        {
                            l.add(player.getName());
                        }
                        l.add("--Joueur");
                    }
                }
            }
        }
        return l;
    }
}
