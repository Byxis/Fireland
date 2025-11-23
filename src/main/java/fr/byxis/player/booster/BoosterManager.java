package fr.byxis.player.booster;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.PermissionUtilities;
import fr.byxis.jeton.JetonManager;
import java.sql.Date;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class BoosterManager implements CommandExecutor, Listener
{

    private final Fireland main;

    public BoosterManager(Fireland _main)
    {
        this.main = _main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings)
    {
        if (strings.length == 0 || (!strings[0].equalsIgnoreCase("create") && !strings[0].equalsIgnoreCase("delete")))
        {
            if (main.getHashMapManager().getBooster() == null)
            {
                InGameUtilities.sendPlayerInformation((Player) commandSender,
                        "Aucun booster n'est actif pour le moment. Pour en créer un, rendez-vous à l'intendant.");
            }
            else
            {
                BoosterClass booster = main.getHashMapManager().getBooster();
                Player creator = (Player) Bukkit.getOfflinePlayer(booster.getUuid());
                InGameUtilities.sendPlayerInformation((Player) commandSender,
                        "Un booster de niveau §d§l " + booster.getLevel() + "§r§7 est actif ! Il a été créé par §6" + creator.getName()
                                + "§7 et se finit dans "
                                + BasicUtilities.getStringTime(booster.getFinished().getTime() - System.currentTimeMillis()));
            }
        }
        else if (strings[0].equalsIgnoreCase("create") && commandSender.hasPermission("fireland.command.booster"))
        {
            if (main.getHashMapManager().getBooster() == null && commandSender instanceof Player p)
            { // booster create lvl:2 hours:2
                if (p.isOp())
                {
                    addBooster(strings, p);
                }
                else if (JetonManager.getJetonsPlayer(p.getUniqueId()) > 100 * Integer.parseInt(strings[1]) * Integer.parseInt(strings[2]))
                {
                    JetonManager.payJetons(p, 100 * Integer.parseInt(strings[1]) * Integer.parseInt(strings[2]),
                            "Achat Booster lvl " + Integer.parseInt(strings[1]) + " pendant " + Integer.parseInt(strings[2]) + "h", false,
                            true);
                    addBooster(strings, p);
                }
                else
                {
                    InGameUtilities.sendPlayerError(p, "Vous n'avez pas assez de jetons !");
                }
            }
            else
            {
                InGameUtilities.sendPlayerError((Player) commandSender, "Erreur: un boost est actuellement en cours");
            }
        }
        else if (strings[0].equalsIgnoreCase("delete") && (commandSender).hasPermission("fireland.admin.booster"))
        {
            if (main.getHashMapManager().getBooster() == null && commandSender instanceof Player p)
            {
                InGameUtilities.sendPlayerError(p, "Aucun booster n'est actif");
            }
            else if (main.getHashMapManager().getBooster() != null)
            {
                Player p = (Player) commandSender;
                InGameUtilities.sendPlayerInformation(p,
                        "Le boost de " + ((Player) Bukkit.getOfflinePlayer(main.getHashMapManager().getBooster().getUuid())).getName()
                                + " a été supprimé.");
                main.getHashMapManager().setBooster(null);
            }
        }
        return false;
    }

    private void addBooster(@NotNull String[] strings, Player p)
    {
        InGameUtilities.sendPlayerInformation(p, "Vous avez acheté un Booster de niveau §d" + Integer.parseInt(strings[1]) + "§r§7 pour "
                + Integer.parseInt(strings[2]) + " heures !");

        main.getHashMapManager()
                .setBooster(new BoosterClass(new Date(System.currentTimeMillis()),
                        new Date(System.currentTimeMillis() + 1000 * 3600 * Long.parseLong(strings[2])), p.getUniqueId(),
                        Integer.parseInt(strings[1])));
        for (Player players : Bukkit.getOnlinePlayers())
        {
            if (!p.getName().equalsIgnoreCase(players.getName()))
            {
                InGameUtilities.sendPlayerInformation(players,
                        "§6§l " + p.getName() + "§r§7 a déclencher un booster de niveau §d" + Integer.parseInt(strings[1]) + "§r§7 pour "
                                + Integer.parseInt(strings[2]) + " heures ! Vous recevrez plus de loot grâce à lui !");
            }
            PermissionUtilities.addTempPermission(p, "phatloots.bonus." + main.getHashMapManager().getBooster().getBoosterLootPercent(),
                    main.getHashMapManager().getBooster().getFinished());
        }
        InGameUtilities.playEveryoneSound("gun.hub.selection", SoundCategory.AMBIENT, 1, 1);
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        if (main.getHashMapManager().getBooster() != null)
        {
            PermissionUtilities.addTempPermission(p, "phatloots.bonus." + main.getHashMapManager().getBooster().getBoosterLootPercent(),
                    main.getHashMapManager().getBooster().getFinished());
        }
    }
}
