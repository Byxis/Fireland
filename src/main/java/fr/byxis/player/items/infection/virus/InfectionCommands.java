package fr.byxis.player.items.infection.virus;

import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.PermissionUtilities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handle the /infect and /cure commands to manage player infections.
 * <p>
 * Commands:
 * - /infect [player]: Infects the specified player or oneself if no player is specified.
 * - /cure [player]: Cures the specified player or oneself if no player is specified.
 */
public class InfectionCommands implements CommandExecutor
{

    private final InfectionManager m_manager;

    public InfectionCommands(InfectionManager _manager)
    {
        m_manager = _manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender _sender, @NotNull Command _cmd, @NotNull String _label, String @NotNull [] _args)
    {
        if (!(_sender instanceof Player executor))
        {
            _sender.sendMessage("§cCette commande ne peut être exécutée que par un joueur.");
            return true;
        }

        String commandName = _cmd.getName().toLowerCase();

        switch (commandName)
        {
            case "infect" ->
            {
                return handleInfectCommand(executor, _args);
            }
            case "cure" ->
            {
                return handleCureCommand(executor, _args);
            }
            default ->
            {
                return false;
            }
        }
    }

    // ==================== COMMAND /INFECT ====================

    private boolean handleInfectCommand(Player _executor, String[] _args)
    {
        if (!PermissionUtilities.hasPermission(_executor, "fireland.command.infect"))
        {
            _executor.sendMessage("§cVous n'avez pas la permission d'exécuter cette commande.");
            return false;
        }

        if (_args.length == 0)
        {
            infectPlayer(_executor, _executor);
            return true;
        }

        if (_args.length == 1)
        {
            Player target = getPlayerFromName(_args[0]);
            if (target == null)
            {
                _executor.sendMessage("§cJoueur introuvable : " + _args[0]);
                return false;
            }

            infectPlayer(_executor, target);
            return true;
        }

        if (_args.length == 2)
        {
            Player target = getPlayerFromName(_args[0]);
            if (target == null)
            {
                _executor.sendMessage("§cJoueur introuvable : " + _args[0]);
                return false;
            }
            try
            {
                int level = Integer.parseInt(_args[1]);
                if (level < 1)
                {
                    _executor.sendMessage("§cLe niveau d'infection doit être supérieur ou égal à 1.");
                    return false;
                }
                m_manager.infectWithLevel(target, level);
                return true;
            }
            catch (NumberFormatException e)
            {
                _executor.sendMessage("§cLe niveau d'infection doit être un nombre entier.");
                return false;
            }
        }

        _executor.sendMessage("§cMauvaise formulation de la commande ! (/infect [player])");
        return false;
    }

    private void infectPlayer(Player _executor, Player _target)
    {
        m_manager.infect(_target);
        if (!_executor.equals(_target))
        {
            _executor.sendMessage("§8" + _target.getName() + " a été infecté.");
        }
    }

    // ==================== COMMAND /CURE ====================

    private boolean handleCureCommand(Player _executor, String[] _args)
    {
        if (!PermissionUtilities.hasPermission(_executor, "fireland.command.cure"))
        {
            _executor.sendMessage("§cVous n'avez pas la permission d'exécuter cette commande.");
            return false;
        }

        if (_args.length == 0)
        {
            curePlayer(_executor, _executor);
            return true;
        }

        if (_args.length == 1)
        {
            Player target = getPlayerFromName(_args[0]);
            if (target == null)
            {
                _executor.sendMessage("§cJoueur introuvable : " + _args[0]);
                return false;
            }

            curePlayer(_executor, target);
            return true;
        }

        _executor.sendMessage("§cMauvaise formulation de la commande ! (/cure [player])");
        return false;
    }

    private void curePlayer(Player _executor, Player _target)
    {
        m_manager.cure(_target);

        if (_executor.equals(_target))
        {
            _executor.sendMessage("§8Vous avez soigné votre infection !");
        }
        else
        {
            _executor.sendMessage("§8" + _target.getName() + " a été soigné !");
            _target.sendMessage("§8Vous avez été soigné !");
        }
    }

    private Player getPlayerFromName(String _name)
    {
        Player player = Bukkit.getPlayer(_name);
        if (player != null) return player;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(BasicUtilities.getUuid(_name));
        if (offlinePlayer.isOnline())
        {
            return offlinePlayer.getPlayer();
        }

        return null;
    }
}