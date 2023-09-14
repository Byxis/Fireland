package fr.byxis.player.workshop;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.player.workshop.recycler.RecyclerFunction;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class workshopManager implements CommandExecutor {

    private final Fireland main;
    public workshopManager(Fireland main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String arg, @NotNull String[] args) {
        workshopFunction wf = new workshopFunction(main, (Player) sender);
        if(cmd.getName().equalsIgnoreCase("workshop") && args.length >= 1)
        {
            if(args[0].equalsIgnoreCase("newrecipe") && args.length >= 6 && ((Player) sender).hasPermission("fireland.command.workshop.admin"))
            {//ws newrecipe a:NomRecette Type scrap canon a:Itemname a:materiel a:durability commande
                StringBuilder sb = new StringBuilder();
                for (int i = 8; i < args.length; i++){
                    sb.append(args[i]).append(" ");
                }
                String name = args[1];
                name = name.replaceAll("_", " ");

                String command = sb.toString().trim();
                //String _itemName, String _mat, int _durability
                //ws newrecipe Plan_de_fabrication_de_ D 60 0 NETHERITE_HOE 0 wm give Player SWR8
                ///    0                 1             2  3 4           5   6  7  8     9     10
                //public void createRecipe(String _name, String _command, String _type, Integer _scrap, Integer _gunpowder, String _itemName, String _mat, int _durability)
                wf.createRecipe(name, command, args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]), args[5], args[6], Integer.parseInt(args[7]));
                sender.sendMessage("§aNouveau plan créé : "+name);
                return true;
            }
            if(args[0].equalsIgnoreCase("a:newrecipe") && args.length >= 5 && ((Player) sender).hasPermission("fireland.command.workshop.admin"))
            {
                StringBuilder sb = new StringBuilder();
                for (int i = 4; i < args.length; i++){
                    sb.append(args[i]).append(" ");
                }

                String command = sb.toString().trim();
                //String _itemName, String _mat, int _durability
                wf.saveNewItem(((Player) sender).getPlayer(), args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), command);
                return true;
            }
            else if(args[0].equalsIgnoreCase("learnrecipe") && args.length >= 3 && ((Player) sender).hasPermission("fireland.command.workshop.admin"))
            {
                if(args.length >= 4)
                {
                    Player victim =  (Player) Bukkit.getOfflinePlayer(BasicUtilities.getUuid(args[3]));
                    wf.craftItemNbr(args[1], victim.getUniqueId().toString(), Integer.parseInt(args[2]));
                    sender.sendMessage("§aLe joueur "+victim.getName()+" à appris "+args[2]+" fois le plan "+args[1]);
                }
                else
                {
                    wf.craftItemNbr(args[1], ((Player)sender).getUniqueId().toString(), Integer.parseInt(args[2]));
                    sender.sendMessage("§aVous avez appris "+args[2]+" fois le plan "+args[1]);
                }
            }
            else if(args[0].equalsIgnoreCase("gui") && sender.hasPermission("fireland.command.workshop.gui"))
            {
                Player p = (Player) sender;
                wf.openCraftMenu(p, 1);
            }
            else if(args[0].equalsIgnoreCase("craftinggui") && sender.hasPermission("fireland.command.workshop.craftinggui"))
            {
                Player p = (Player) sender;
                wf.openCraftingMenu(p, 1);
            }
            else if(args[0].equalsIgnoreCase("plan") && sender.hasPermission("fireland.command.workshop.admin") && args.length >= 2)
            {
                Player p = (Player) sender;

                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < args.length; i++){
                    sb.append(args[i]).append(" ");
                }

                String nom = sb.toString().trim();
                wf.getPlan(p, nom);
            }
            else if(args[0].equalsIgnoreCase("forgetall") && sender.hasPermission("fireland.command.workshop.admin"))
            {
                sender.sendMessage("§cVous avez oublié tous vos plans.");
                wf.forgetAllPlans(((Player)sender).getUniqueId().toString());
            }
            else if(args[0].equalsIgnoreCase("recycler") && sender.hasPermission("fireland.command.workshop.recycler"))
            {
                Player p = (Player) sender;
                RecyclerFunction rf = new RecyclerFunction(main);
                rf.OpenRecyclingGui(p);
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
