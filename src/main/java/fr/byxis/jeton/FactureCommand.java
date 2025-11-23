package fr.byxis.jeton;

import static fr.byxis.fireland.utilities.BasicUtilities.getUuid;
import static fr.byxis.fireland.utilities.ListUtilities.tabList;
import static fr.byxis.jeton.JetonManager.sendPlayerFacture;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FactureCommand implements @Nullable CommandExecutor, @Nullable TabCompleter
{
    private final Fireland m_main;
    public FactureCommand(Fireland fireland)
    {
        m_main = fireland;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        if (!(commandSender instanceof Player))
            return false;
        Player p = (Player) commandSender;
        if (args.length == 0)
        {
            sendPlayerFacture(p, p, 0);
        }
        if (args.length == 1)
        {
            try
            {
                int amount = Integer.parseInt(args[0]);
                sendPlayerFacture(p, p, amount);
            }
            catch (NumberFormatException e)
            {
                if (p.hasPermission("fireland.admin.facture"))
                {
                    Player victim = Bukkit.getPlayer(getUuid(args[0]));
                    if (victim != null)
                    {
                        sendPlayerFacture(p, victim, 0);
                    }
                    else
                    {
                        InGameUtilities.sendPlayerError(p, "Joueur inconnu");
                    }
                    return true;
                }
                InGameUtilities.sendPlayerError(p, "Utilisation: /facture [joueur] [page]");
                return false;
            }
        }
        if (args.length == 2 && p.hasPermission("fireland.admin.facture"))
        {
            try
            {
                int amount = Integer.parseInt(args[0]);
                Player victim = Bukkit.getPlayer(getUuid(args[1]));
                if (victim != null)
                {
                    sendPlayerFacture(p, victim, amount);
                }
                else
                {
                    InGameUtilities.sendPlayerError(p, "Joueur inconnu");
                }
            }
            catch (NumberFormatException e)
            {
                InGameUtilities.sendPlayerError(p, "Utilisation: /facture [player] [page]");
                return false;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
            @NotNull String[] args)
    {
        List<String> list = new ArrayList<String>();

        if (!(commandSender instanceof Player))
            return null;
        Player p = (Player) commandSender;
        if (args.length == 1)
        {
            if (p.hasPermission("fireland.admin.facture"))
            {
                list.addAll(tabList(args[0], "0", Bukkit.getOnlinePlayers()));
            }
            else
            {
                list.add("0");
            }
        }

        return list;
    }

}
