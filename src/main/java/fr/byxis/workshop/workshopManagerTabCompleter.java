package fr.byxis.workshop;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.Fireland;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class workshopManagerTabCompleter implements TabCompleter {

    private final Fireland main;
    public workshopManagerTabCompleter(Fireland main) {
        this.main = main;
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String arg, @NotNull String[] args) {
        List<String> l = new ArrayList<>();
        if(cmd.getName().equalsIgnoreCase("workshop") && ((Player)sender).hasPermission("fireland.command.workshop.admin"))
        {
            if(args.length == 1)
            {
                l.add("newrecipe");
                l.add("a:newrecipe");
                l.add("learnrecipe");
                l.add("gui");
                l.add("craftinggui");
                l.add("plan");
                l.add("forgetall");
            }
            if(args.length >= 2)
            {
                if(args[0].equalsIgnoreCase("newrecipe"))
                {
                    if(args.length == 2)
                    {
                        l.add("--Nom de la recette");
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
                    }//String _itemName, String _mat, int _durability
                    else if(args.length == 6)
                    {
                        l.add("--Nom de l'item");
                    }
                    else if(args.length == 7)
                    {
                        for(Material mat : Material.values())
                        {
                            l.add(mat.name());
                        }
                        l.add("--Nom du matériaux");
                    }
                    else if(args.length == 8)
                    {
                        l.add("1");
                        l.add("2");
                        l.add("--Durability de l'item");
                    }
                    else
                    {
                        l.add("--Commande");
                    }
                }
                if(args[0].equalsIgnoreCase("a:newrecipe"))
                {
                    if(args.length == 2)
                    {
                        l.add("D");
                        l.add("C");
                        l.add("B");
                        l.add("A");
                        l.add("--Type");
                    }
                    else if(args.length == 3)
                    {
                        l.add("10");
                        l.add("20");
                        l.add("--Nombre de scrap nécessaire");
                    }
                    else if(args.length == 4)
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
                else if(args[0].equalsIgnoreCase("plan"))
                {
                    l.add("--Nom");
                }
            }
        }
        return l;
    }
}
