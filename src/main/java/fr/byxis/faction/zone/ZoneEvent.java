package fr.byxis.faction.zone;

import fr.byxis.player.intendant.menu.MenuFaction;
import fr.byxis.faction.zone.zoneclass.ZoneClass;
import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.fireland.Fireland;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ZoneEvent implements Listener {

    private DataZone data;
    private Fireland main;
    public ZoneEvent(Fireland main, DataZone data) {
        this.main = main;
        this.data = data;
    }

    @EventHandler
    public void ClickEvent(PlayerInteractEvent e)
    {
        if(e.getClickedBlock() != null
                && (e.getClickedBlock().getType() == Material.TARGET
                || e.getClickedBlock().getType() == Material.OAK_FENCE
                || e.getClickedBlock().getType() == Material.STONE_BRICK_WALL)
                && e.getAction().isRightClick()
                && e.getPlayer().getItemInHand().getType() == Material.AIR)
        {
            for(ZoneClass zone : data.zones)
            {
                if(zone.getLocation().getX() == e.getClickedBlock().getLocation().getX()
                        && zone.getLocation().getZ() == e.getClickedBlock().getLocation().getZ()
                        && (zone.getLocation().getY() == e.getClickedBlock().getLocation().getY()
                || zone.getLocation().getY() == e.getClickedBlock().getLocation().getY()-1
                || zone.getLocation().getY() == e.getClickedBlock().getLocation().getY()-2
                || zone.getLocation().getY() == e.getClickedBlock().getLocation().getY()-3
                || zone.getLocation().getY() == e.getClickedBlock().getLocation().getY()-4))
                {
                    if(zone.isClaimed())
                    {
                        FactionFunctions ff = new FactionFunctions(main, e.getPlayer());
                        if(zone.getClaimer().equalsIgnoreCase(ff.playerFactionName(e.getPlayer())))
                        {
                            MenuFaction.OpenFaction(main, e.getPlayer(), false);
                        }
                    }
                    break;
                }
            }
        }
    }
}
