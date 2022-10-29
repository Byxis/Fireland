package fr.byxis.main;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class karmaManager implements Listener, CommandExecutor, TabCompleter {

    private Main main;
    public karmaManager(Main main) {
        this.main = main;
    }

    private String getRang(UUID _uuid)
    {
        double rangDouble = main.cfgm.getKarmaDB().getDouble(_uuid.toString());
        String rangStr = "";
        if(rangDouble == 100)
        {
            rangStr = "§dHéros";
        }
        else if(rangDouble>=75)
        {
            rangStr = "§7Héros";
        }
        else if(rangDouble>=50)
        {
            rangStr = "§7Civil";
        }
        else if(rangDouble>=25)
        {
            rangStr = "§7Hors la loi";
        }
        else if(rangDouble>0)
        {
            rangStr = "§cCriminel";
        }
        else if(rangDouble == 0)
        {
            rangStr = "§4Criminel";
        }
        return rangStr;
    }

    private String rangText(Player p)
    {
        return getRang(p.getUniqueId())+ " §7("+getKarma(p.getUniqueId()) +"§7)";
    }

    public void badAction(UUID _uuid, double _amount)
    {
        updatePlayer(_uuid);
        if(main.cfgm.getKarmaDB().getDouble(_uuid.toString())-_amount < 0)
        {
            main.cfgm.getKarmaDB().set(_uuid.toString(), 0);
        }
        else
        {
            main.cfgm.getKarmaDB().set(_uuid.toString(), main.cfgm.getKarmaDB().getDouble(_uuid.toString())-_amount);
        }

        main.cfgm.saveKarmaDB();
    }

    public void goodAction(UUID _uuid, double _amount)
    {
        updatePlayer(_uuid);
        if(main.cfgm.getKarmaDB().getDouble(_uuid.toString())-_amount > 100)
        {
            main.cfgm.getKarmaDB().set(_uuid.toString(), 100);
        }
        else
        {
            main.cfgm.getKarmaDB().set(_uuid.toString(), main.cfgm.getKarmaDB().getDouble(_uuid.toString())+_amount);
        }
        main.cfgm.saveKarmaDB();
    }

    public void setKarma(UUID _uuid, double _amount)
    {
        updatePlayer(_uuid);
        main.cfgm.getKarmaDB().set(_uuid.toString(), _amount);
        main.cfgm.saveKarmaDB();
    }

    public void updatePlayer(UUID _uuid)
    {
        FileConfiguration karma = main.cfgm.getKarmaDB();
        if(!karma.contains(_uuid.toString()))
        {
            karma.set(_uuid.toString(), 62D);
            main.cfgm.saveKarmaDB();
        }
    }

    public double getKarma(UUID _uuid)
    {
        return main.cfgm.getKarmaDB().getDouble(_uuid.toString());
    }

    @EventHandler
    public void playerKillPlayer(PlayerDeathEvent e)
    {
        if((e.getEntity().getKiller() != null))
        {
            badAction(e.getEntity().getKiller().getUniqueId(), 10);
        }

        if(getKarma(e.getEntity().getUniqueId())+10 < 50)
        {
            goodAction(e.getEntity().getUniqueId(), 10);
        }
        else
        {
            setKarma(e.getEntity().getUniqueId(), 50);
        }
    }

    @EventHandler
    public void PlayerFirstJoin(PlayerJoinEvent e)
    {
        updatePlayer(e.getPlayer().getUniqueId());
    }

    public void playerKillZombie(EntityDeathEvent e)
    {
        if(e.getEntity() instanceof Zombie && e.getEntity().getKiller() != null)
        {
            goodAction(e.getEntity().getKiller().getUniqueId(), 1/20);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String arg, @NotNull String[] args) {
        if(sender instanceof Player && !sender.hasPermission("fireland.command.rang.admin"))
        {
            sender.sendMessage("§aVotre rang : "+rangText((Player) sender));
        }
        else
        {
            if(args.length == 0 && sender instanceof Player)
            {
                sender.sendMessage("§aVotre rang : "+rangText((Player) sender));
            }
            else if(args.length == 1)
            {
                final Player victim = Bukkit.getPlayer(args[0]);
                if(victim != null)
                {
                    sender.sendMessage("§aVoici le rang de "+victim.getName()+" : "+rangText(victim));
                }
                else
                {
                    sender.sendMessage("§cErreur ! Utilisation : /rang (set/add/remove) (int) [player]");
                }
            }
            else if(args.length >= 2)
            {
                try
                {
                    int amount = Integer.parseInt(args[1]);

                } catch (NumberFormatException e) {
                    sender.sendMessage("§cErreur ! Utilisation : /rang (set/add/remove) (int) [player]");
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
                                setKarma(p.getUniqueId(), Integer.parseInt(args[1]));
                                p.sendMessage("§aVotre rang est maintenant : "+rangText(p));
                            }
                            sender.sendMessage("§aLe nouveau rang de tous les joueurs est : "+rangText((Player) sender));
                        }
                        else
                        {
                            final Player victim = (Player) Bukkit.getOfflinePlayer(args[2]);
                            setKarma(victim.getUniqueId(), Integer.parseInt(args[1]));
                            sender.sendMessage("§aLe nouveau rang de "+victim.getName()+" est : "+rangText(victim));
                            victim.sendMessage("§aVotre rang est maintenant : "+rangText(victim));
                        }
                    }
                    else if(sender instanceof Player p)
                    {
                        setKarma(p.getUniqueId(), Integer.parseInt(args[1]));
                        p.sendMessage("§aVotre rang est maintenant : "+rangText((Player) sender));
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
                                goodAction(player.getUniqueId(), Integer.parseInt(args[1]));
                                player.sendMessage("§aVotre rang est maintenant : "+rangText(player));
                            }
                            sender.sendMessage("§aLe nouveau rang de tous les joueurs est : "+rangText((Player) sender));
                        }
                        else
                        {
                            final Player victim = (Player) Bukkit.getOfflinePlayer(args[2]);
                            goodAction(victim.getUniqueId(), Integer.parseInt(args[1]));
                            sender.sendMessage("§aLe nouveau rang de "+victim.getName()+" est : "+rangText(victim));
                            victim.sendMessage("§aVotre rang est maintenant : "+rangText(victim));
                        }
                    }
                    else if(sender instanceof Player p)
                    {
                        goodAction(p.getUniqueId(), Integer.parseInt(args[1]));
                        p.sendMessage("§aVotre rang est maintenant : "+rangText((Player) sender));
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
                                badAction(player.getUniqueId(), Integer.parseInt(args[1]));
                                player.sendMessage("§aVotre rang est maintenant : "+rangText(player));
                            }
                            sender.sendMessage("§aLe nouveau rang de tous les joueurs est : "+rangText((Player) sender));
                        }
                        else
                        {
                            final Player victim = (Player) Bukkit.getOfflinePlayer(args[2]);
                            badAction(victim.getUniqueId(), Integer.parseInt(args[1]));
                            sender.sendMessage("§aLe nouveau rang de "+victim.getName()+" est : "+rangText(victim));
                            victim.sendMessage("§aVotre rang est maintenant : "+rangText(victim));
                        }
                    }
                    else if(sender instanceof Player p)
                    {
                        badAction(p.getUniqueId(), Integer.parseInt(args[1]));
                        p.sendMessage("§aVotre rang est maintenant : "+rangText((Player) sender));
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
        if(!(sender instanceof Player p && p.hasPermission("fireland.command.rang.admin")))
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
