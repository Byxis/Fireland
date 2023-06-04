package fr.byxis.essaim;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EssaimCommandManager implements @Nullable CommandExecutor {

    private final Fireland main;
    public EssaimCommandManager(Fireland fireland) {
        this.main = fireland;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args)
    {
        if(args.length == 0)
        {
            BasicUtilities.sendPlayerError((Player) sender, "Mauvaise formulation de la commande !");
            return false;
        }
        if(sender.hasPermission("fireland.essaim.spawner"))
        {
            SpawnerCommand(sender, msg, args);
        }
        return false;
    }

    private void SpawnerCommand(CommandSender p, String msg, String[] args)
    {
        if(args.length < 3)
        {
            BasicUtilities.sendPlayerError((Player) p, "Mauvaise formulation de la commande !");
        }
        else if(args.length == 4)
        {
            if(args[1].equalsIgnoreCase("remove"))
            {
                if(!main.essaimManager.existingEssaims.containsKey(args[2]))
                {
                    BasicUtilities.sendPlayerError((Player) p, "L'essaim "+args[2]+" n'existe pas !");
                }
                else if(!main.essaimManager.existingEssaims.get(args[2]).containsKey(args[3]))
                {
                    BasicUtilities.sendPlayerError((Player) p, "Le spawner "+args[3]+" n'existe pas!");
                }
                else
                {
                    EssaimFunctions.removeSpawner(args[2], args[3]);
                    BasicUtilities.sendPlayerError((Player) p, "Le spawner "+args[3]+" a été supprimé avec succès !");
                }
            }
            else if(args[1].equalsIgnoreCase("activate"))
            {
                if(!main.essaimManager.existingEssaims.containsKey(args[2]))
                {
                    BasicUtilities.sendPlayerError((Player) p, "L'essaim "+args[2]+" n'existe pas !");
                }
                else if(!main.essaimManager.existingEssaims.get(args[2]).containsKey(args[3]))
                {
                    BasicUtilities.sendPlayerError((Player) p, "Le spawner "+args[3]+" n'existe pas!'");
                }
                else
                {
                    if(main.essaimManager.EnableSpawner(main.essaimManager.existingEssaims.get(args[2]).get(args[3])))
                    {
                        BasicUtilities.sendPlayerInformation((Player) p, "Le spawner "+args[3]+" activé avec succès !");
                    }
                    else
                    {
                        BasicUtilities.sendPlayerError((Player) p, "Le spawner "+args[3]+" est déjà activé !");
                    }
                }
            }
            else
            {
                BasicUtilities.sendPlayerError((Player) p, "Mauvaise formulation de la commande !");
            }
        }
        else if(args.length  >= 7)
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 6; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            String command = sb.toString().trim();
            EssaimFunctions.createNewSpawner(args[2], args[3], args[4], Integer.parseInt(args[5]), command, ((Player) p).getLocation());
            BasicUtilities.sendPlayerInformation((Player) p, "Spawner "+args[3]+" créé avec succès!");

        }
        else
        {
            BasicUtilities.sendPlayerError((Player) p, "Mauvaise formulation de la commande !");
        }
    }
}
