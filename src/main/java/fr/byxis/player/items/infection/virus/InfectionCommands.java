package fr.byxis.player.items.infection.virus;

import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.PermissionUtilities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handle the /infect and /cure commands to manage player infections.
 *
 * Commands:
 * - /infect [player] [infectionType]: Infects the specified player or oneself if no player is specified.
 *   If infectionType is provided it must match one of InfectionType enum names (case-insensitive).
 * - /cure [player]: Cures the specified player or oneself if no player is specified.
 *
 * Also provides tab-completion for player names and infection types.
 */
public class InfectionCommands implements CommandExecutor, TabCompleter
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
                String typeStr = _args[1].toUpperCase();
                InfectionType type = InfectionType.valueOf(typeStr);
                m_manager.infectWithLevel(target, type);
                if (!_executor.equals(target))
                {
                    _executor.sendMessage("§8" + target.getName() + " a été infecté avec le type " + type.name().toLowerCase() + ".");
                }
                else
                {
                    _executor.sendMessage("§8Vous vous êtes infecté avec le type " + type.name().toLowerCase() + ".");
                }
                return true;
            }
            catch (IllegalArgumentException e)
            {
                String valid = Arrays.stream(InfectionType.values())
                        .map(Enum::name)
                        .map(String::toLowerCase)
                        .collect(Collectors.joining(", "));
                _executor.sendMessage("§cType d'infection invalide. Types valides: " + valid);
                return false;
            }
        }

        _executor.sendMessage("§cMauvaise formulation de la commande ! (/infect [player] [infectionType])");
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

    // ==================== TAB COMPLETION ====================

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1)
        {
            String prefix = args[0].toLowerCase();
            for (Player online : Bukkit.getOnlinePlayers())
            {
                String name = online.getName();
                if (name.toLowerCase().startsWith(prefix))
                {
                    suggestions.add(name);
                }
            }
            return suggestions;
        }

        if (args.length == 2 && command.getName().equalsIgnoreCase("infect"))
        {
            String prefix = args[1].toLowerCase();
            for (InfectionType type : InfectionType.values())
            {
                String name = type.name().toLowerCase();
                if (name.startsWith(prefix))
                {
                    suggestions.add(name);
                }
            }
            return suggestions;
        }

        return suggestions;
    }
}