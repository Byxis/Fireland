package fr.byxis.event;

import fr.byxis.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class jetonsManager implements Listener, CommandExecutor, TabCompleter {

    private final Main main;

    public jetonsManager(Main main) {
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

    public void updatePlayer(UUID _uuid)
    {
        FileConfiguration jetonDB = main.cfgm.getJetonsDB();
        if(!jetonDB.contains(_uuid.toString()))
        {
            jetonDB.set(_uuid.toString(), 0);
            main.cfgm.saveJetonsDB();
        }
    }

    public int getJetonsPlayer(UUID _uuid)
    {
        FileConfiguration jetonDB = main.cfgm.getJetonsDB();
        return jetonDB.getInt(_uuid.toString());
    }

    public void setJetonsPlayer(UUID _uuid, int amount)
    {
        FileConfiguration jetonDB = main.cfgm.getJetonsDB();
        jetonDB.set(_uuid.toString(), amount);
        main.cfgm.saveJetonsDB();
    }

    public void addJetonsPlayer(UUID _uuid, int amount)
    {
        FileConfiguration jetonDB = main.cfgm.getJetonsDB();
        jetonDB.set(_uuid.toString(), jetonDB.getInt(_uuid.toString())+amount);
        main.cfgm.saveJetonsDB();
    }

    public void removeJetonsPlayer(UUID _uuid, int amount)
    {
        FileConfiguration jetonDB = main.cfgm.getJetonsDB();
        jetonDB.set(_uuid.toString(), jetonDB.getInt(_uuid.toString())-amount);
        main.cfgm.saveJetonsDB();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String arg, @NotNull String[] args) {
        if(sender instanceof Player && sender.hasPermission("fireland.command.jeton.admin"))
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
                final Player victim = Bukkit.getPlayer(args[0]);
                if(victim != null)
                {
                    sender.sendMessage("§a"+victim.getName()+" possède §d"+getJetonsPlayer(victim.getUniqueId())+"§a jetons !");
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
                            final Player victim = (Player) Bukkit.getOfflinePlayer(args[2]);
                            setJetonsPlayer(victim.getUniqueId(), Integer.parseInt(args[1]));
                            sender.sendMessage("§aLe joueur " + victim.getName() + " a désormais §d" + args[1] + "§a jetons !");
                            victim.sendMessage("§aVous avez désormais §d" + args[1] + "§a jetons !");
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
                                player.sendMessage("§aVous avez gagnés §d"+args[1]+"§a jetons !");
                            }
                            sender.sendMessage("§aTous les joueurs connectés ont gagnés §d"+args[1]+"§a jetons !");
                        }
                        else
                        {
                            final Player victim = (Player) Bukkit.getOfflinePlayer(args[2]);
                            addJetonsPlayer(victim.getUniqueId(), Integer.parseInt(args[1]));
                            sender.sendMessage("§aLe joueur "+victim.getName()+" a désormais §d"+args[1]+"§a jetons !");
                            victim.sendMessage("§aVous avez gagnés §d"+args[1]+"§a jetons !");
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
                                addJetonsPlayer(player.getUniqueId(), Integer.parseInt(args[1]));
                                player.sendMessage("§cVous avez perdus §d"+args[1]+"§c jetons !");
                            }
                            sender.sendMessage("§cTous les joueurs connectés ont perdus §d"+args[1]+"§c jetons !");
                        }
                        else
                        {
                            final Player victim = (Player) Bukkit.getOfflinePlayer(args[2]);
                            removeJetonsPlayer(victim.getUniqueId(), Integer.parseInt(args[1]));
                            sender.sendMessage("§cLe joueur " + victim.getName() + " a perdu §d" + args[1] + "§c jetons !");
                            victim.sendMessage("§cVous avez perdu §d" + args[1] + "§c jetons !");
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
            for(Player player : Bukkit.getServer().getOnlinePlayers())
            {
                l.add(player.getName());
            }
            l.add("@a");
            return l;
        }
        return l;
    }
}
