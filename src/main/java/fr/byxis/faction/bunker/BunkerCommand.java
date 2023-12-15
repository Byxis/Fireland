package fr.byxis.faction.bunker;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static fr.byxis.fireland.utilities.WGUtilities.isWithinRegion;

public class BunkerCommand implements CommandExecutor {

    private final Fireland main;
    public BunkerCommand(Fireland main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player p)
        {
            if(p.hasPermission("fireland.bunker.mod") && strings.length >= 2 && strings[0].equalsIgnoreCase("join"))
            {
                BunkerClass bunker = main.bunkerManager.getBunker(strings[1]);
                if(!main.bunkerManager.getLoadedBunker().containsKey(strings[1]))
                {
                    bunker.Join(p);
                    InGameUtilities.sendPlayerInformation(p, "Vous avez forcé l'entrée dans le bunker.");
                    return true;
                }
            }
            else if(p.hasPermission("fireland.bunker.mod") && strings.length >= 2 && strings[0].equalsIgnoreCase("info"))
            {
                BunkerClass bk = main.bunkerManager.getBunker(strings[1]);
                p.sendMessage("§8Bunker de la faction §7"+bk.GetName() +" §d(Niv. "+bk.GetBunkerLevel()+")");
                StringBuilder sb = new StringBuilder("§8Joueurs dedans : §7");
                for(Player pl : bk.GetPlayerInside())
                {
                    sb.append(pl.getName()).append(" ");
                }
                p.sendMessage(sb.toString());
                p.sendMessage("§8Location : x§7"+bk.GetBunkerLocation().getBlockX()+"§8 y§7"+bk.GetBunkerLocation().getBlockZ()+"§8 z§7"+bk.GetBunkerLocation().getBlockZ());
                return true;
            }
            else if(p.hasPermission("fireland.bunker.mod") && strings.length == 1 && strings[0].equalsIgnoreCase("info"))
            {
                StringBuilder sb = new StringBuilder("§8Bunkers chargés : §7");
                for(BunkerClass bk : main.bunkerManager.getLoadedBunker().values())
                {
                    sb.append(bk.GetName()).append(" ");
                }
                sb.append("§b");
                for(String str : main.bunkerManager.getLoadedBunker().keySet())
                {
                    sb.append(str).append(" ");
                }
                p.sendMessage(sb.toString());
                return true;
            }
            else if(strings[0].equalsIgnoreCase("join"))
            {
                for(BunkerClass bk : main.bunkerManager.getLoadedBunker().values())
                {
                    if(bk.IsInvited(p) ||p.hasPermission("fireland.bunker.mod"))
                    {
                        if(isWithinRegion(p, "safe-zone_") || p.hasPermission("fireland.bunker.mod"))
                        {
                            bk.Join(p);
                        }
                        else
                        {
                            InGameUtilities.sendPlayerError(p, "Vous devez être en safe zone pour rejoindre un bunker !");
                        }
                        return true;
                    }
                }
                InGameUtilities.sendPlayerError(p, "Utilisation: /bunker <join/info> <faction>");
            }

        }
        return false;
    }
}
