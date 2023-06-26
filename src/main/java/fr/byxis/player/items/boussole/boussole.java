package fr.byxis.player.items.boussole;

import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.faction.faction.FactionPlayerInformation;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class boussole implements @NotNull Listener {

    private Fireland main;

    public boussole(Fireland main)
    {
        this.main = main;
    }

    @EventHandler
    public void PlayerInteraction(PlayerInteractEvent e)
    {
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if (e.getItem() != null) {
                if (e.getItem().getType() == Material.COMPASS && e.getPlayer().getCooldown(Material.COMPASS) <= 0) {
                    e.getPlayer().setCooldown(Material.COMPASS, 200);
                    InGameUtilities.sendPlayerInformation(e.getPlayer(),
                            "X:"+e.getPlayer().getLocation().getBlockX() +"   Y:"+e.getPlayer().getLocation().getBlockY() +"   Z:"+
                                    e.getPlayer().getLocation().getBlockZ());
                    FactionFunctions ff = new FactionFunctions(main, e.getPlayer());
                    String faction = ff.playerFactionName(e.getPlayer());
                    if(!faction.equals(""))
                    {
                        for(FactionPlayerInformation p : ff.getPlayersFromFaction(faction))
                        {
                            Player bukkitPlayer = Bukkit.getPlayer(p.getUuid());
                            if(bukkitPlayer != null && bukkitPlayer.isOnline() && bukkitPlayer.getUniqueId() != e.getPlayer().getUniqueId())
                            {
                                InGameUtilities.sendPlayerInformation(bukkitPlayer,
                                        e.getPlayer().getName()+" se trouve aux coordonnées :   X:"+e.getPlayer().getLocation().getBlockX()
                                                +"   Y:"+e.getPlayer().getLocation().getBlockY()
                                                +"   Z:"+e.getPlayer().getLocation().getBlockZ());
                            }
                        }
                    }

                }
            }
        }
    }

    @EventHandler
    public void firstPlayerJoin(PlayerJoinEvent e)
    {
        if(!e.getPlayer().hasPlayedBefore())
        {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "w give "+e.getPlayer().getName()+" Boussole");
        }
    }
}
