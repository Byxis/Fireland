package fr.byxis.faction.essaim.commands;

import static fr.byxis.fireland.utilities.ListUtilities.getOnlinePlayerNames;

import fr.byxis.faction.essaim.managers.GroupManager;
import fr.byxis.faction.essaim.services.EssaimConfigService;
import fr.byxis.faction.essaim.services.EssaimService;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.ListUtilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

/**
 * Completer for the "essaim" command, providing tab completion suggestions
 * based on the command context.
 */
public class EssaimCommandCompleter implements TabCompleter
{
    private final Fireland m_fireland;

    public EssaimCommandCompleter(Fireland _fireland)
    {
        this.m_fireland = _fireland;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender _sender, @NotNull Command _command, @NotNull String _alias, String[] _args)
    {

        if (_args.length == 1)
        {
            return ListUtilities.filterSuggestions(_args[0], Arrays.asList("spawner", "create", "open", "close", "remove", "set", "join",
                    "finish", "unfinish", "info", "reset", "setblock", "forcejoin", "forcekick"));
        }

        if (_args.length == 2)
        {
            switch (_args[0].toLowerCase())
            {
                case "spawner" :
                    return ListUtilities.filterSuggestions(_args[1], Arrays.asList("list", "remove", "activate", "create"));
                case "open" :
                case "close" :
                case "info" :
                case "reset" :
                case "finish" :
                case "unfinish" :
                case "remove" :
                    return ListUtilities.filterSuggestions(_args[1],
                            new ArrayList<>(m_fireland.getEssaimManager().getConfigService().getEssaimNames()));
                case "set" :
                    return ListUtilities.filterSuggestions(_args[1],
                            Arrays.asList("hub", "start", "reset", "entry", "solo", "key", "difficulty.1", "difficulty.2", "difficulty.3"));
                case "forcejoin" :
                    return ListUtilities.filterSuggestions(_args[1], getActiveEssaimNames());
                case "forcekick" :
                    return ListUtilities.filterSuggestions(_args[1], getOnlinePlayerNames());
            }
        }

        if (_args.length == 3)
        {
            if (_args[0].equalsIgnoreCase("forcejoin"))
            {
                return ListUtilities.filterSuggestions(_args[2], getOnlinePlayerNames());
            }
        }

        return new ArrayList<>();
    }

    /**
     * Retrieves a list of active essaim names from the EssaimManager. It first
     * checks the GroupManager for active groups, and if none are found, it falls
     * back to checking the EssaimConfigService and EssaimService for active
     * essaims.
     *
     * @return a list of active essaim names
     */
    private List<String> getActiveEssaimNames()
    {
        GroupManager groupManager = m_fireland.getEssaimManager().getGroupManager();

        List<String> activeEssaims = new ArrayList<>(groupManager.getAllGroups().keySet());

        if (activeEssaims.isEmpty())
        {
            EssaimConfigService configService = m_fireland.getEssaimManager().getConfigService();
            EssaimService essaimService = m_fireland.getEssaimManager().getEssaimService();

            for (String essaimName : configService.getEssaimNames())
            {
                if (essaimService.isEssaimActive(essaimName))
                {
                    activeEssaims.add(essaimName);
                }
            }
        }

        return activeEssaims;
    }
}
