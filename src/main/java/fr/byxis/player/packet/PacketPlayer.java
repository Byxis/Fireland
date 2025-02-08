package fr.byxis.player.packet;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class PacketPlayer implements CommandExecutor {
    private final Fireland main;
    private final PacketFunctions pf;
    public PacketPlayer(Fireland _main) {
        this.main = _main;
        this.pf = new PacketFunctions(_main);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings)
    {
        if (commandSender instanceof Player p && strings.length >= 1)
        {
            if (strings[0].equalsIgnoreCase("border"))
            {
                if (strings.length < 2)
                {
                    InGameUtilities.sendPlayerInformation(p, "Packet envoyé.");
                    playTestBorderPacket(p);
                    return true;
                }
                else
                {
                    for (Player players : Bukkit.getOnlinePlayers())
                    {
                        if (players.getName().equalsIgnoreCase(strings[1]))
                        {
                            InGameUtilities.sendPlayerInformation(p, "Packet envoyé à " + players.getName() + ".");
                            playTestBorderPacket(players);
                            return true;
                        }
                    }
                    InGameUtilities.sendPlayerError(p, "Personne non trouvée.");
                    return false;
                }
            }
            else if (strings[0].equalsIgnoreCase("opendoor"))
            {
                InGameUtilities.sendPlayerInformation(p, "Packet envoyé. Porte ouverte");
                //noinspection removal
                playTestOpenDoorPacket(p, p.getTargetBlock(50).getLocation());
            }
            else if (strings[0].equalsIgnoreCase("spyglass"))
            {

            }
        }
        else
        {
            InGameUtilities.sendPlayerError((Player) commandSender, "Erreur.");
        }
        return false;
    }

    private void playTestBorderPacket(Player p)
    {
        pf.sendWorldBorderWarningDistancePacket(p, 1);
        new BukkitRunnable()
        {
            @Override
            public void run() {
                pf.sendWorldBorderWarningDistancePacket(p, 0);
            }
        }.runTaskLater(main, 20);
    }

    private void playTestOpenDoorPacket(Player p, Location loc)
    {
        pf.openDoor(p, loc, true);
        new BukkitRunnable()
        {
            @Override
            public void run() {
                pf.openDoor(p, loc, false);
            }
        }.runTaskLater(main, 20);
    }
}
