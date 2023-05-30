package fr.byxis.booster;

import fr.byxis.fireland.Fireland;
import fr.byxis.jeton.jetonsCommandManager;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.jeton.jetonSql;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.PermissionUtilities;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.Date;

public class BoosterManager implements CommandExecutor, Listener {

    private final Fireland main;
    public BoosterManager(Fireland main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length == 0 || (!strings[0].equalsIgnoreCase("create") && !strings[0].equalsIgnoreCase("stop")))
        {
            if(main.hashMapManager.getBooster() == null)
            {
                BasicUtilities.sendPlayerInformation((Player) commandSender, "Aucun booster n'est actif pour le moment. Pour en créer un, rendez-vous à l'intendant.");
            }
            else
            {
                BoosterClass booster = main.hashMapManager.getBooster();
                Player creator = (Player) Bukkit.getOfflinePlayer(booster.getUuid());
                BasicUtilities.sendPlayerInformation((Player) commandSender, "Un booster de niveau §d§l"+booster.getLevel()+"§r§7 est actif ! Il a été créé par §6"+creator.getName()+"§7 et se finit dans "+main.getStringTime(booster.getFinished().getTime()-System.currentTimeMillis()));
            }
        }
        else if(strings[0].equalsIgnoreCase("create") && ((Player)commandSender).hasPermission("fireland.command.booster"))
        {
            if(main.hashMapManager.getBooster() == null && commandSender instanceof Player p)
            {//booster create lvl:2 hours:2
                jetonsCommandManager jeton = new jetonsCommandManager(main);
                if(jeton.getJetonsPlayer(p.getUniqueId()) > 100*Integer.parseInt(strings[1])*Integer.parseInt(strings[2]))
                {
                    jeton.removeJetonsPlayer(p.getUniqueId(), 100*Integer.parseInt(strings[1])*Integer.parseInt(strings[2]));
                    main.hashMapManager.setBooster(new BoosterClass(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()+1000*3600*Long.parseLong(strings[2])), p.getUniqueId(), Integer.parseInt(strings[1])));
                    jetonSql jetonsql = new jetonSql(main, p);
                    jetonsql.createFacture(p.getUniqueId().toString(), 100*Integer.parseInt(strings[1])*Integer.parseInt(strings[2]), "Achat Booster lvl:"+Integer.parseInt(strings[1])+" duration:"+Integer.parseInt(strings[2]));
                    BasicUtilities.sendPlayerInformation(p, "Vous avez acheté un Booster de niveau §d"+Integer.parseInt(strings[1])+"§r§7 pour "+Integer.parseInt(strings[2])+" heures !");
                    for(Player players : Bukkit.getOnlinePlayers())
                    {
                        BasicUtilities.sendPlayerInformation(players, "§6§l"+p.getName()+"§r§7 a déclencher un booster de niveau §d"+Integer.parseInt(strings[1])+"§r§7 pour "+Integer.parseInt(strings[2])+" heures ! Vous recevrez plus de loot grâce à lui !");
                        PermissionUtilities.addTempPermission(p, "phatloots.bonus."+main.hashMapManager.getBooster().getBoosterLootPercent(), main.hashMapManager.getBooster().getFinished());
                    }
                }
                else
                {
                    BasicUtilities.sendPlayerError(p, "Vous n'avez pas assez de jetons !");
                }
            }
            else
            {
                BasicUtilities.sendPlayerError((Player) commandSender, "Erreur : un boost est actuellement en cours");
            }
        }
        else if(strings[0].equalsIgnoreCase("delete") && ((Player)commandSender).hasPermission("fireland.command.booster"))
        {
            if(main.hashMapManager.getBooster() == null && commandSender instanceof Player p)
            {
                BasicUtilities.sendPlayerError(p, "Aucun booster n'est actif");
            }
            else if(main.hashMapManager.getBooster() != null)
            {
                Player p = (Player) commandSender;
                BasicUtilities.sendPlayerInformation(p, "Le boost de "+((Player)Bukkit.getOfflinePlayer(main.hashMapManager.getBooster().getUuid())).getName()+" a été supprimé.");
                main.hashMapManager.setBooster(null);
            }
        }
        return false;
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        if(main.hashMapManager.getBooster() != null)
        {
            PermissionUtilities.addTempPermission(p, "phatloots.bonus."+main.hashMapManager.getBooster().getBoosterLootPercent(), main.hashMapManager.getBooster().getFinished());

        }
    }
}
