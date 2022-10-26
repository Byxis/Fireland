package fr.byxis.event;

import fr.byxis.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class jetonsManager implements Listener, CommandExecutor {

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
    }

    public void addJetonsPlayer(UUID _uuid, int amount)
    {
        FileConfiguration jetonDB = main.cfgm.getJetonsDB();
        jetonDB.set(_uuid.toString(), jetonDB.getInt(_uuid.toString())+amount);
    }

    public void removeJetonsPlayer(UUID _uuid, int amount)
    {
        FileConfiguration jetonDB = main.cfgm.getJetonsDB();
        jetonDB.set(_uuid.toString(), jetonDB.getInt(_uuid.toString())-amount);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String arg, @NotNull String[] args) {
        if(sender instanceof Player && sender.hasPermission("fireland.jeton.admin"))
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
                        final Player victim = (Player) Bukkit.getOfflinePlayer(args[2]);
                        setJetonsPlayer(victim.getUniqueId(), Integer.parseInt(args[1]));
                        sender.sendMessage("§aLe joueur "+victim.getName()+" a désormais §d"+args[1]+"§a jetons !");
                        victim.sendMessage("§aVous avez désormais §d"+args[1]+"§a jetons !");
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
                        final Player victim = (Player) Bukkit.getOfflinePlayer(args[2]);
                        addJetonsPlayer(victim.getUniqueId(), Integer.parseInt(args[1]));
                        sender.sendMessage("§aLe joueur "+victim.getName()+" a désormais §d"+args[1]+"§a jetons !");
                        victim.sendMessage("§aVous avez désormais §d"+args[1]+"§a jetons !");
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
                        final Player victim = (Player) Bukkit.getOfflinePlayer(args[2]);
                        removeJetonsPlayer(victim.getUniqueId(), Integer.parseInt(args[1]));
                        sender.sendMessage("§aLe joueur "+victim.getName()+" a désormais §d"+args[1]+"§a jetons !");
                        victim.sendMessage("§aVous avez désormais §d"+args[1]+"§a jetons !");
                    }
                    else if(sender instanceof Player p)
                    {
                        removeJetonsPlayer(p.getUniqueId(), Integer.parseInt(args[1]));
                        sender.sendMessage("§aVous avez désormais §d"+args[1]+"§a jetons !");
                    }
                }
            }
        }

        return false;
    }
}
