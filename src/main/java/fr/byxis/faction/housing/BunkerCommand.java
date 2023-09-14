package fr.byxis.faction.housing;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static com.sk89q.jnbt.NBTUtils.toVector;
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
            if(p.hasPermission("fireland.bunker.mod") && strings.length >= 2)
            {
                if(!main.bunkerManager.getLoadedBunker().containsKey(strings[1]))
                {
                    BunkerClass bunker = new BunkerClass(strings[1], main);
                    main.bunkerManager.AddLoadedBunker(bunker);
                    bunker.Join(p);
                    InGameUtilities.sendPlayerInformation(p, "Vous avez forcé l'entrée dans le bunker.");
                    return true;
                }
            }
            InGameUtilities.sendPlayerError(p, "Utilisation: /bunker join <faction>");
        }
        return false;
    }
}
