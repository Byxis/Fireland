package fr.byxis.jeton;

import fr.byxis.main.Main;
import fr.byxis.main.utilities.BasicUtilities;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class jetonsCommandManager extends JetonManager implements Listener, CommandExecutor, TabCompleter {

    private final Main main;

    public jetonsCommandManager(Main main) {
        super(main);
        this.main = main;
    }

    @EventHandler
    public void PlayerFirstJoin(PlayerJoinEvent e)
    {
        if(!e.getPlayer().hasPlayedBefore())
        {
            updatePlayer(e.getPlayer().getUniqueId());
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String arg, @NotNull String[] args) {
        if(sender instanceof Player && !sender.hasPermission("fireland.command.jeton.admin"))
        {
            sender.sendMessage("§aVous avez §d"+getJetonsPlayer(((Player)sender).getUniqueId())+"§a jetons !");
        }
        else
        {
            if(args.length == 0 && sender instanceof Player)
            {
                sender.sendMessage("§aVous avez §d"+getJetonsPlayer(((Player)sender).getUniqueId())+"§a jetons !");
            }
            else if(args.length == 1)
            {
                UUID uuid = BasicUtilities.getUuid(args[2]);
                if(uuid != null)
                {
                    sender.sendMessage("§a"+args[2]+" possède §d"+getJetonsPlayer(uuid)+"§a jetons !");
                }
                else
                {
                    sender.sendMessage("§cErreur ! Utilisation : /jeton (set/add/remove) (int) [player]");
                }
            }
            else if(args.length >= 2)
            {
                try
                {
                    int amount = Integer.parseInt(args[1]);

                } catch (NumberFormatException e) {
                    sender.sendMessage("§cErreur ! Utilisation : /jeton (set/add/remove) (int) [player]");
                    return false;
                }
                if(args[0].equalsIgnoreCase("set"))
                {
                    if(args.length >= 3)
                    {
                        if(args[2].equalsIgnoreCase("@a"))
                        {
                            for(Player p : Bukkit.getServer().getOnlinePlayers())
                            {
                                setJetonsPlayer(p.getUniqueId(), Integer.parseInt(args[1]));
                                p.sendMessage("§aVous avez désormais §d"+args[1]+"§a jetons !");
                            }
                            sender.sendMessage("§aTous les joueurs connectés ont maintenant §d"+args[1]+"§a jetons !");
                        }
                        else
                        {
                            UUID uuid = BasicUtilities.getUuid(args[2]);
                            if(uuid != null)
                            {
                                setJetonsPlayer(uuid, Integer.parseInt(args[1]));
                                sender.sendMessage("§aLe joueur " + args[2] + " a désormais §d" + args[1] + "§a jetons !");
                                if(Bukkit.getOfflinePlayer(uuid).isOnline())
                                {
                                    Bukkit.getPlayer(uuid).sendMessage("§aVous avez désormais §d" + args[1] + "§a jetons !");
                                }
                            }
                        }
                    }
                    else if(sender instanceof Player p)
                    {
                        setJetonsPlayer(p.getUniqueId(), Integer.parseInt(args[1]));
                        sender.sendMessage("§aVous avez désormais §d"+args[1]+"§a jetons !");
                    }
                }
                else if(args[0].equalsIgnoreCase("add"))
                {
                    if(args.length >= 3)
                    {
                        if(args[2].equalsIgnoreCase("@a"))
                        {
                            for(Player player : Bukkit.getServer().getOnlinePlayers())
                            {
                                addJetonsPlayer(player.getUniqueId(), Integer.parseInt(args[1]));
                                player.sendMessage("§aVous avez gagné §d"+args[1]+"§a jetons !");
                            }
                            sender.sendMessage("§aTous les joueurs connectés ont gagnés §d"+args[1]+"§a jetons !");
                        }
                        else
                        {
                            UUID uuid = BasicUtilities.getUuid(args[2]);
                            if(uuid != null)
                            {
                                addJetonsPlayer(uuid, Integer.parseInt(args[1]));
                                sender.sendMessage("§aLe joueur " + args[2] + " a gagné §d" + args[1] + "§a jetons ! Il en a désormais §d"+getJetonsPlayer(uuid)+"§r§a !");
                                if(Bukkit.getOfflinePlayer(uuid).isOnline())
                                {
                                    Bukkit.getPlayer(uuid).sendMessage("§aVous avez gagné §d"+args[1]+"§a jetons !");
                                }
                            }
                        }
                    }
                    else if(sender instanceof Player p)
                    {
                        addJetonsPlayer(p.getUniqueId(), Integer.parseInt(args[1]));
                        sender.sendMessage("§aVous avez désormais §d"+args[1]+"§a jetons !");
                    }
                }
                else if(args[0].equalsIgnoreCase("remove"))
                {
                    if(args.length >= 3)
                    {
                        if(args[2].equalsIgnoreCase("@a"))
                        {
                            for(Player player : Bukkit.getServer().getOnlinePlayers())
                            {
                                removeJetonsPlayer(player.getUniqueId(), Integer.parseInt(args[1]));
                                player.sendMessage("§cVous avez perdu §d"+args[1]+"§c jetons !");
                            }
                            sender.sendMessage("§cTous les joueurs connectés ont perdus §d"+args[1]+"§c jetons !");
                        }
                        else
                        {
                            UUID uuid = BasicUtilities.getUuid(args[2]);
                            if(uuid != null)
                            {
                                removeJetonsPlayer(uuid, Integer.parseInt(args[1]));
                                sender.sendMessage("§cLe joueur " + args[2] + " a perdu §d" + args[1] + "§c jetons !");
                                if(Bukkit.getOfflinePlayer(uuid).isOnline())
                                {
                                    Bukkit.getPlayer(uuid).sendMessage("§cVous avez perdu §d" + args[1] + "§c jetons !");
                                }
                            }


                        }
                    }
                    else if(sender instanceof Player p)
                    {
                        removeJetonsPlayer(p.getUniqueId(), Integer.parseInt(args[1]));
                        sender.sendMessage("§cVous avez perdu §d"+args[1]+"§c jetons !");
                    }
                }
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
    {
        List<String> l = new ArrayList<>();
        if(!(sender instanceof Player p && p.hasPermission("fireland.command.jeton.admin")))
        {
            return l;
        }
        if (args.length == 1)
        {
            l.add("set");
            l.add("add");
            l.add("remove");
        }
        else if (args.length == 2)
        {
            l.add("50");
            l.add("100");
            l.add("1000");
            return l;
        }
        else if (args.length == 3)
        {
            l.add("@a");
            for(Player player : Bukkit.getServer().getOnlinePlayers())
            {
                l.add(player.getName());
            }
            return l;
        }
        return l;
    }
}
