package fr.byxis.packet;

import fr.byxis.main.Main;
import fr.byxis.main.utilities.BasicUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class PacketPlayer implements CommandExecutor {
    private Main main;
    private PacketFunctions pf;
    public PacketPlayer(Main main) {
        this.main = main;
        this.pf = new PacketFunctions(main);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings)
    {
        if(commandSender instanceof Player p && strings.length >=1)
        {
            if(strings[0].equalsIgnoreCase("border"))
            {
                if(strings.length < 2)
                {
                    BasicUtilities.sendPlayerInformation(p, "Packet envoyé.");
                    PlayTestBorderPacket(p);
                    return true;
                }
                else
                {
                    for(Player players : Bukkit.getOnlinePlayers())
                    {
                        if(players.getName().equalsIgnoreCase(strings[1]))
                        {
                            BasicUtilities.sendPlayerInformation(p, "Packet envoyé à "+players.getName()+".");
                            PlayTestBorderPacket(players);
                            return true;
                        }
                    }
                    BasicUtilities.sendPlayerError(p, "Personne non trouvée.");
                    return false;
                }
            }
            else if(strings[0].equalsIgnoreCase("opendoor"))
            {
                BasicUtilities.sendPlayerInformation(p, "Packet envoyé. Porte ouverte");
                PlayTestOpenDoorPacket(p, p.getTargetBlock(50).getLocation());
            }
        }
        BasicUtilities.sendPlayerError((Player) commandSender, "Erreur.");
        return false;
    }

    private void PlayTestBorderPacket(Player p)
    {
        pf.playBorderPackets(p, true);
        new BukkitRunnable()
        {
            @Override
            public void run() {
                pf.playBorderPackets(p, false);
            }
        }.runTaskLater(main, 20);
    }

    private void PlayTestOpenDoorPacket(Player p, Location loc)
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
