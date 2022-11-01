package fr.byxis.workshop;

import fr.byxis.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class workshopManager implements CommandExecutor {

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
                String name = args[1];
                name = name.replaceAll("_", " ");

                String command = sb.toString().trim();
                wf.createRecipe(name, command, args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                sender.sendMessage("§aNouveau plan créé : "+name);
                return true;
            }
            else if(args[0].equalsIgnoreCase("learnrecipe") && args.length >= 3)
            {
                if(args.length >= 4)
                {
                    Player victim = (Player) Bukkit.getOfflinePlayer(args[3]);
                    wf.craftItemNbr(args[1], victim.getUniqueId().toString(), Integer.parseInt(args[2]));
                }
                else
                {
                    wf.craftItemNbr(args[1], ((Player)sender).getUniqueId().toString(), Integer.parseInt(args[2]));
                }
            }
            else {
                if(args[0].equalsIgnoreCase("gui")) {
                    Player p = (Player) sender;
                    wf.openCraftMenu(p);
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

}
