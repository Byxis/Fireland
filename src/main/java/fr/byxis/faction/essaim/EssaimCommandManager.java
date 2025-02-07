package fr.byxis.faction.essaim;

import fr.byxis.faction.essaim.essaimClass.EssaimGroup;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.PermissionUtilities;
import fr.byxis.fireland.utilities.TextUtilities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class EssaimCommandManager implements @Nullable CommandExecutor {

    private final Fireland main;

    public EssaimCommandManager(Fireland fireland) {
        this.main = fireland;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if (args.length == 0) {
            if (sender instanceof Player p)
            {
                InGameUtilities.sendPlayerError(p, "Mauvaise formulation de la commande !");
            }
            return false;
        }
        else if (sender.hasPermission("fireland.essaim.admin") && args[0].equalsIgnoreCase("spawner")) {
            SpawnerCommand(sender, msg, args);
        }
        else if (sender.hasPermission("fireland.command.essaim") &&
                (args[0].equalsIgnoreCase("create") ||
                        args[0].equalsIgnoreCase("open") ||
                        args[0].equalsIgnoreCase("close") ||
                        args[0].equalsIgnoreCase("remove")||
                        args[0].equalsIgnoreCase("set")||
                        args[0].equalsIgnoreCase("join")||
                        args[0].equalsIgnoreCase("finish")||
                        args[0].equalsIgnoreCase("info")||
                        args[0].equalsIgnoreCase("unfinish"))) {
            GeneralCommand(sender, msg, args);
        }
        else if (sender.hasPermission("fireland.essaim.setblock")
                && args[0].equalsIgnoreCase("setblock")
                && args.length ==  5)
{
            EssaimFunctions.setBlock(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Material.getMaterial(args[4].toUpperCase()));
            if (sender instanceof Player p)
            {
                InGameUtilities.sendPlayerInformation(p, "Le block a été mis à jour avec succès !");
            }
            else
            {
                sender.sendMessage("Le block a été mis à jour avec succès!");
            }
        }
        return false;
    }

    private void SpawnerCommand(CommandSender sender, String msg, String[] args) {
        if (args.length < 2) {
            InGameUtilities.sendPlayerError((Player) sender, "Mauvaise formulation de la commande !");
        }
        else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("list")) {
                EssaimFunctions.sendAllSpawners(args[2], (Player) sender);
            }
        } else if (args.length == 4) {
            if (args[1].equalsIgnoreCase("remove")) {
                if (!main.essaimManager.existingEssaims.containsKey(args[2])) {
                    if (sender instanceof Player p)
                    {
                        InGameUtilities.sendPlayerError(p, "L'essaim " + args[2] + " n'existe pas !");
                    }
                } else if (!main.essaimManager.existingEssaims.get(args[2]).containsKey(args[3])) {
                    if (sender instanceof Player p)
                    {
                        InGameUtilities.sendPlayerError(p, "Le spawner " + args[3] + " n'existe pas!");
                    }
                } else {
                    EssaimFunctions.removeSpawner(args[2], args[3]);
                    if (sender instanceof Player p)
                    {
                        InGameUtilities.sendPlayerError(p, "Le spawner " + args[3] + " a été supprimé avec succès !");
                    }
                }
            } else if (args[1].equalsIgnoreCase("activate")) {
                if (!main.essaimManager.existingEssaims.containsKey(args[2])) {
                    if (sender instanceof Player p)
                    {
                        InGameUtilities.sendPlayerError(p, "L'essaim " + args[2] + " n'existe pas !");
                    }
                } else if (!main.essaimManager.existingEssaims.get(args[2]).containsKey(args[3])) {
                    if (sender instanceof Player p)
                    {
                        InGameUtilities.sendPlayerError(p, "Le spawner " + args[3] + " n'existe pas !");
                    }
                } else {
                    if (main.essaimManager.EnableSpawner(main.essaimManager.existingEssaims.get(args[2]).get(args[3]))) {
                        if (sender instanceof Player p)
                        {
                            InGameUtilities.sendPlayerInformation(p, "Le spawner " + args[3] + " activé avec succès !");
                        }
                    } else {
                        if (sender instanceof Player p)
                        {
                            InGameUtilities.sendPlayerError(p, "Le spawner " + args[3] + " est déjà activé !");
                        }
                    }
                }
            } else {
                if (sender instanceof Player p)
                {
                    InGameUtilities.sendPlayerError((Player) sender, "Mauvaise formulation de la commande !");
                }
            }

        } else if (args.length >= 9 && args[1].equalsIgnoreCase("create") && sender instanceof Player p) {
            StringBuilder sb = new StringBuilder();
            for (int i = 9; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            String command = sb.toString().trim();
            EssaimFunctions.createNewSpawner(args[2], args[3], args[4], Integer.parseInt(args[5]),Integer.parseInt(args[6]),Integer.parseInt(args[7]), command, p.getLocation(), Boolean.valueOf(args[8]));

            InGameUtilities.sendPlayerInformation(p, "Spawner " + args[3] + " créé avec succès!");

        } else {
            if (sender instanceof Player p)
            {
                InGameUtilities.sendPlayerError(p, "Mauvaise formulation de la commande !");
            }
        }
    }

    private void GeneralCommand(CommandSender sender, String msg, String[] args) {
        if (args.length == 5 && args[0].equalsIgnoreCase("create") && sender.hasPermission("fireland.essaim.admin")) {
            if (sender instanceof Player p)
            {
                Location loc = p.getLocation();
                loc.setX((double) Math.round(loc.getX() * 10) /10);
                loc.setY((double) Math.round(loc.getY() * 10) /10);
                loc.setZ((double) Math.round(loc.getZ() * 10) /10);
                EssaimFunctions.createNewEssaim(args[1], args[2], getDate(args[3]), Integer.parseInt(args[4]), loc);
                InGameUtilities.sendPlayerInformation(p, "Essaim " + args[1] + " créé avec succès !");
            }
        }
        else if (args.length == 2 && args[0].equalsIgnoreCase("finish") && sender.hasPermission("fireland.essaim.admin"))
        {
            EssaimFunctions.finishEssaim(args[1]);
            if (sender instanceof Player p)
            {
                InGameUtilities.sendPlayerInformation(p, "L'essaim " + TextUtilities.convertStorableToClean(args[1]) + " a été fini !");
            }
        }else if (args.length == 2 && args[0].equalsIgnoreCase("unfinish") && sender.hasPermission("fireland.essaim.admin"))
        {
            EssaimFunctions.unfinishEssaim(args[1]);
            if (sender instanceof Player p)
            {
                InGameUtilities.sendPlayerError(p, "L'essaim " + TextUtilities.convertStorableToClean(args[1]) + " n'est plus fini !");
            }
        }else if (args.length == 2 && args[0].equalsIgnoreCase("remove") && sender.hasPermission("fireland.essaim.admin")) {
            EssaimFunctions.deleteEssaim(args[1]);
            if (sender instanceof Player p)
            {
                InGameUtilities.sendPlayerError(p, "Essaim " + args[1] + " a été supprimé !");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("open") && sender.hasPermission("fireland.essaim.admin")) {
            //open;
            if (sender instanceof Player p)
            {
                if (main.essaimManager.EnableEssaim(args[1]))
                {
                    InGameUtilities.sendPlayerInformation(p, "L'essaim " + TextUtilities.convertStorableToClean(args[1]) + " a été ouvert avec succès !");
                }
                else
                {
                    InGameUtilities.sendPlayerError(p, "Essaim " + args[1] + " est déjà activé !");
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("close") && sender.hasPermission("fireland.essaim.admin")) {
            //close
            if (sender instanceof Player p)
            {
                if (EssaimManager.DisableEssaim(args[1]))
                {
                    InGameUtilities.sendPlayerError(p, "Essaim " + args[1] + " a été fermé !");
                }
                else
                {
                    InGameUtilities.sendPlayerError(p, "Essaim " + args[1] + " est déjà fermé !");
                }
            }

        }
        else if (args.length == 2 && args[0].equalsIgnoreCase("reset") && sender.hasPermission("fireland.essaim.admin")) {
            //close
            if (sender instanceof Player p) {
                if (main.essaimManager.resetEssaim(args[1])) {
                    InGameUtilities.sendPlayerError(p, "Essaim " + args[1] + " a été reset !");
                } else {
                    InGameUtilities.sendPlayerError(p, "Essaim " + args[1] + " est déjà reset !");
                }
            }
        }
        else if (args.length == 2 && args[0].equalsIgnoreCase("info") && sender.hasPermission("fireland.essaim.admin")) {
            if (sender instanceof Player p)
            {
                if (EssaimManager.groups.containsKey(args[1]))
                {
                    EssaimGroup grp = EssaimManager.groups.get(args[1]);
                    p.sendMessage("§8Info de l'essaim §f " + args[1] + " §8 -");
                    StringBuilder members = new StringBuilder("§8Joueur(s) :");
                    for (int i = 0; i < grp.getMembers().size() - 2; i++)
                    {
                        if (grp.getMembers().get(i).getName().equalsIgnoreCase(grp.getLeader().getName()))
                        {
                            members.append("§6").append(grp.getMembers().get(i).getName()).append("§7,");
                        }
                        else
                        {
                            members.append("§f").append(grp.getMembers().get(i).getName()).append("§7,");
                        }
                    }
                    if (grp.getMembers().get(grp.getMembers().size() - 1).getName().equalsIgnoreCase(grp.getLeader().getName()))
                    {
                        members.append("§6").append(grp.getMembers().get(grp.getMembers().size()-1).getName()).append("§7.");
                    }
                    else
                    {
                        members.append("§f").append(grp.getMembers().get(grp.getMembers().size()-1).getName()).append("§7.");
                    }
                    p.sendMessage(members.toString());
                    p.sendMessage("§8Difficulté : §f " + grp.getDifficulty());
                    p.sendMessage("§8Début : §f " + grp.getStartTime());
                }
                else
                {
                    InGameUtilities.sendPlayerError(p, "L'essaim est inconnu ou inactif.");
                }
            }
        }
        else if (args.length == 3 && args[0].equalsIgnoreCase("join")) {
            //close
            if (sender instanceof Player p && args[2].equalsIgnoreCase("Wowowowowowowowowowow1234567890") && !main.hashMapManager.isTeleporting(p.getUniqueId()))
            {
                if (!EssaimManager.groups.get(args[1]).getMembers().contains(p))
                {
                    if (!PermissionUtilities.hasPermission(p.getUniqueId(), "fireland.essaim.access." + args[1]))
                    {
                        InGameUtilities.sendPlayerError(p, "Vous n'avez pas complété la quête requise ou n'avez pas l'extension DLC.");
                        return;
                    }
                    EssaimFunctions.teleportJoinEssaim(p, EssaimManager.activeEssaims.get(args[1]).getHub(), "gun.hub.helico",10, args[1]);
                }
                else
                {
                    InGameUtilities.sendPlayerError(p, "Vous êtes déjà dans l'essaim.s");
                }
            }
        }
        else if (args.length == 6 && sender.hasPermission("fireland.essaim.admin")) {
            if (args[0].equalsIgnoreCase("set") && sender instanceof Player p)
            {
                Location loc = p.getLocation();
                loc.setX((double) Math.round(loc.getX()));
                loc.setY((double) Math.round(loc.getY()));
                loc.setZ((double) Math.round(loc.getZ()));
                EssaimFunctions.setPoint(args[5], loc, args[1].toLowerCase());
                InGameUtilities.sendPlayerInformation(p, "Point créé avec succès!");
            }
        }
    }

    private int getDate(String s)
    {
        return switch (s.toLowerCase()) {
            case "lundi" -> 0;
            case "mardi" -> 1;
            case "mercredi" -> 2;
            case "jeudi" -> 3;
            case "vendredi" -> 4;
            case "samedi" -> 5;
            case "dimanche" -> 6;
            default -> -1;
        };
    }
}
