package fr.byxis.karma;

import fr.byxis.main.Main;
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

    public String getRang(UUID _uuid)
    {
        double rangDouble = main.hashMapManager.getRangMap().get(_uuid).getRang();
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
            rangStr = "§4Banni";
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
        double rang = main.hashMapManager.getRangMap().get(_uuid).getRang();
        if((rang-_amount) < 0)
        {
            main.hashMapManager.getRangMap().get(_uuid).setRang(0);
        }
        else if(rang-_amount > 100)
        {
            main.hashMapManager.getRangMap().get(_uuid).setRang(100);
        }
        else
        {
            main.hashMapManager.getRangMap().get(_uuid).setRang(rang-_amount);
        }
        passageNouveauRang(Bukkit.getPlayer(_uuid), rang, rang-_amount);
    }

    public void goodAction(UUID _uuid, double _amount, boolean... b)
    {
        updatePlayer(_uuid);
        double gain = main.cfgm.getKarmaDB().getDouble("max."+_uuid.toString());
        if(b!=null || 15-gain <=15)
        {
            if(_amount > 15-gain)
            {
                _amount = 15-gain;
            }
            double rang = main.hashMapManager.getRangMap().get(_uuid).getRang();
            if(rang+_amount > 100)
            {
                main.hashMapManager.getRangMap().get(_uuid).setRang(100);
            }
            else if(rang+_amount < 0)
            {
                main.hashMapManager.getRangMap().get(_uuid).setRang(0);
            }
            else
            {
                main.hashMapManager.getRangMap().get(_uuid).setRang(rang+_amount);
                main.hashMapManager.getRangMap().get(_uuid).setMax(gain+_amount);
            }
            passageNouveauRang(Bukkit.getPlayer(_uuid), rang, rang+_amount);
        }
    }

    public void setKarma(UUID _uuid, double _amount)
    {
        updatePlayer(_uuid);
        main.hashMapManager.getRangMap().get(_uuid).setRang(_amount);
    }

    public void updatePlayer(UUID _uuid)
    {
        FileConfiguration karma = main.cfgm.getKarmaDB();
        if(!karma.contains(_uuid.toString()))
        {
            karma.set(_uuid.toString(), 62D);
            karma.set("max."+_uuid.toString(), 0D);
            main.cfgm.saveKarmaDB();
        }
        if(!main.hashMapManager.getRangMap().containsKey(_uuid))
        {
            main.hashMapManager.getRangMap().put(_uuid, new PlayerKarmaClass(main.cfgm.getKarmaDB().getDouble(_uuid.toString()), main.cfgm.getKarmaDB().getDouble("max."+_uuid.toString())));
        }
    }

    private void passageNouveauRang(Player p, double bef, double now)
    {
        String sound = "minecraft:gun.hud.rangchange";
        String crim = "minecraft:gun.hud.rangbanni";
        String heros = "minecraft:gun.hud.rangheros";
        if( bef < 75 && now >= 75)
        {
            for(Player player : Bukkit.getOnlinePlayers())
            {
                if(player != p)
                {
                    player.sendMessage("§d§l"+p.getName()+"§r§d est devenu un Héros !");
                }
            }
            p.sendTitle("", "§dPassage au rang Héros !");
            main.playSound(p, heros);
        }
        else if( bef >= 75 && now < 75)
        {
            p.sendTitle("", "§7Rétrogradage au rang Civil.");
            main.playSound(p, sound);
        }
        else if( bef >= 50 && now < 50)
        {
            p.sendTitle("", "§7Rétrogradage au rang Hors la Loi.");
            main.playSound(p, sound);
        }
        else if( bef < 50 && now >= 50)
        {
            p.sendTitle("", "§7Passage au rang Civil !");
            main.playSound(p, sound);
        }
        else if( bef <25  && now >= 25)
        {
            p.sendTitle("", "§7Passage au rang Hors la Loi.");
            main.playSound(p, sound);
        }
        else if( bef >= 25 && now < 25)
        {
            for(Player player : Bukkit.getOnlinePlayers())
            {
                if(player != p)
                {
                    player.sendMessage("§4§l"+p.getName()+"§r§4 est devenu un Criminel.");
                }
            }
            p.sendTitle("", "§cRétrogradage au rang Criminel.");
            main.playSound(p, crim);
        }
        else if( bef <= 25 && now == 0)
        {
            p.sendTitle("", "§4Rétrogradage au rang Criminel.");
            main.playSound(p, crim);
        }

        if(bef >= 25 && now <25)
        {
            main.addPermission(p, "group.bannis");
        }
        else if(bef < 25 && now >= 25)
        {
            main.removePermission(p, "group.bannis");
        }
    }

    public double getKarma(UUID _uuid)
    {
        return Math.round(main.hashMapManager.getRangMap().get(_uuid).getRang()*100)/100D;
    }

    @EventHandler
    public void playerKillPlayer(PlayerDeathEvent e)
    {
        if((e.getEntity().getKiller() != null))
        {
            badAction(e.getEntity().getKiller().getUniqueId(), 10);
        }

        if(getKarma(e.getEntity().getUniqueId())+10 <= 50)
        {
            goodAction(e.getEntity().getUniqueId(), 10);
        }
        else if(getKarma(e.getEntity().getUniqueId()) > 62)
        {
            badAction(e.getEntity().getUniqueId(), 5);
        }
    }

    @EventHandler
    public void PlayerFirstJoin(PlayerJoinEvent e)
    {
        updatePlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void playerKillZombie(EntityDeathEvent e)
    {
        if(e.getEntity() instanceof Zombie)
        {
            if(e.getEntity().getKiller() != null)
            {
                if(getKarma(e.getEntity().getKiller().getUniqueId()) != 0)
                {
                    goodAction(e.getEntity().getKiller().getUniqueId(), 0.05);
                }
            }

        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String arg, @NotNull String[] args) {
        if(sender instanceof Player)
        {
            if(args.length == 0)
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
                    sender.sendMessage("§cErreur ! Utilisation : /rang [player]");
                }
            }
            else if(args.length >= 2 && ((Player)sender).hasPermission("fireland.command.rang.admin"))
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
        if(sender instanceof Player p && !p.hasPermission("fireland.command.rang.admin"))
        {
            return l;
        }
        if (args.length == 1)
        {
            l.add("set");
            l.add("add");
            l.add("remove");
            for (Player p : Bukkit.getOnlinePlayers())
            {
                l.add(p.getName());
            }
        }
        else if (args.length == 2)
        {
            l.add("10");
            l.add("25");
            l.add("50");
            l.add("100");
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
