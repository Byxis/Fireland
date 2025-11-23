package fr.byxis.player.workshop;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.player.workshop.recycler.RecyclerFunction;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WorkshopManager implements CommandExecutor
{

    private final Fireland main;

    public WorkshopManager(Fireland _main)
    {
        this.main = _main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String arg, @NotNull String[] args)
    {
        WorkshopFunction wf = new WorkshopFunction(main, (Player) sender);
        if (cmd.getName().equalsIgnoreCase("workshop") && args.length >= 1)
        {
            if (args[0].equalsIgnoreCase("newrecipe") && args.length >= 6 && sender.hasPermission("fireland.command.workshop.admin"))
            { // ws newrecipe a:NomRecette Type scrap canon a:Itemname a:materiel a:durability
              // commande

                StringBuilder sb = new StringBuilder();
                for (int i = 11; i < args.length; i++)
                {
                    sb.append(args[i]).append(" ");
                }

                for (int i = 0; i < args.length; i++)
                {
                    System.out.println("args[" + i + "] : " + args[i]);
                }

                String planName = args[1];
                planName = planName.replaceAll("_", " ");
                System.out.println("planName (après traitement) : " + planName);

                String type = args[2];
                System.out.println("type : " + type);

                System.out.println("args[3] avant conversion : " + args[3]);
                int scrap = Integer.parseInt(args[3]);
                System.out.println("scrap (après conversion) : " + scrap);

                System.out.println("args[4] avant conversion : " + args[4]);
                int gunpowder = Integer.parseInt(args[4]);
                System.out.println("gunpowder (après conversion) : " + gunpowder);

                System.out.println("args[5] avant conversion : " + args[5]);
                int medicine = Integer.parseInt(args[5]);
                System.out.println("medicine (après conversion) : " + medicine);

                System.out.println("args[6] avant conversion : " + args[6]);
                int duration = Integer.parseInt(args[6]);
                System.out.println("duration (après conversion) : " + duration);

                String itemName = args[7];
                System.out.println("itemName : " + itemName);

                String material = args[8];
                System.out.println("material : " + material);

                System.out.println("args[9] avant conversion : " + args[9]);
                int durability = Integer.parseInt(args[9]);
                System.out.println("durability (après conversion) : " + durability);

                String command = sb.toString().trim();
                // String _itemName, String _mat, int _durability
                // ws newrecipe Plan_de_fabrication_de_ D 60 0 0 120 NETHERITE_HOE 0 wm give
                // Player SWR8
                /// 0 1 2 3 4 5 6 7 8 9 10 11
                InGameUtilities.debug(155, command);
                wf.createRecipe(planName, command, type, scrap, gunpowder, medicine, duration, itemName, material, durability);
                sender.sendMessage("§aNouveau plan créé : " + planName);
                return true;
            }
            if (args[0].equalsIgnoreCase("a:newrecipe") && args.length >= 5 && sender.hasPermission("fireland.command.workshop.admin"))
            {
                StringBuilder sb = new StringBuilder();
                for (int i = 5; i < args.length; i++)
                {
                    sb.append(args[i]).append(" ");
                }

                String command = sb.toString().trim();

                // ws a:newrecipe E 20 0 10 30 wm give Player antidoul
                // 0 1 2 3 4 5 6 7 8 9
                String type = args[1];
                int scrap = Integer.parseInt(args[2]);
                int gunpowder = Integer.parseInt(args[3]);
                int medicine = Integer.parseInt(args[4]);
                int duration = Integer.parseInt(args[5]);
                wf.saveNewItem(((Player) sender).getPlayer(), type, scrap, gunpowder, medicine, duration, command);
                return true;
            }
            else if (args[0].equalsIgnoreCase("learnrecipe") && args.length >= 3 && sender.hasPermission("fireland.command.workshop.admin"))
            {
                if (args.length >= 4)
                {
                    Player victim = (Player) Bukkit.getOfflinePlayer(BasicUtilities.getUuid(args[3]));
                    wf.craftItemNbr(args[1], victim.getUniqueId().toString(), Integer.parseInt(args[2]));
                    sender.sendMessage("§aLe joueur " + victim.getName() + " à appris " + args[2] + " fois le plan " + args[1]);
                }
                else
                {
                    wf.craftItemNbr(args[1], ((Player) sender).getUniqueId().toString(), Integer.parseInt(args[2]));
                    sender.sendMessage("§aVous avez appris " + args[2] + " fois le plan " + args[1]);
                }
            }
            else if (args[0].equalsIgnoreCase("gui") && sender.hasPermission("fireland.command.workshop.gui"))
            {
                Player p = (Player) sender;
                wf.openCraftMenu(p, 1);
            }
            else if (args[0].equalsIgnoreCase("craftinggui") && sender.hasPermission("fireland.command.workshop.craftinggui"))
            {
                Player p = (Player) sender;
                wf.openCraftingMenu(p, 1);
            }
            else if (args[0].equalsIgnoreCase("plan") && sender.hasPermission("fireland.command.workshop.admin") && args.length >= 2)
            {
                Player p = (Player) sender;

                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < args.length; i++)
                {
                    sb.append(args[i]).append(" ");
                }

                String nom = sb.toString().trim();
                wf.getPlan(p, nom);
            }
            else if (args[0].equalsIgnoreCase("forgetall") && sender.hasPermission("fireland.command.workshop.admin"))
            {
                sender.sendMessage("§cVous avez oublié tous vos plans.");
                wf.forgetAllPlans(((Player) sender).getUniqueId().toString());
            }
            else if (args[0].equalsIgnoreCase("recycler") && sender.hasPermission("fireland.command.workshop.recycler"))
            {
                Player p = (Player) sender;
                RecyclerFunction rf = new RecyclerFunction(main);
                rf.openRecyclingGui(p);
            }
        }
        else if (cmd.getName().equalsIgnoreCase("workshop"))
        {
            if (sender.hasPermission("fireland.command.workshop.admin"))
            {
                sender.sendMessage("§cUtilisation: /workshop (newrecipe/learnrecipe)");
            }
            else
            {
                sender.sendMessage("§cUtilisation:  /workshop ");
            }
        }
        return false;
    }

}
