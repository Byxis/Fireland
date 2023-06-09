package fr.byxis.zone;

import fr.byxis.faction.FactionInformation;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.zone.zoneclass.ZoneClass;
import de.netzkronehd.wgregionevents.events.RegionEnterEvent;
import de.netzkronehd.wgregionevents.events.RegionLeftEvent;
import fr.byxis.faction.FactionFunctions;
import fr.byxis.fireland.Fireland;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class WorldGuardEnterZoneEvent implements Listener {

    private DataZone data;
    private FactionFunctions ff;
    private final Fireland main;

    public WorldGuardEnterZoneEvent(Fireland main, DataZone data) {
        this.main = main;
        this.data = data;
        this.ff = new FactionFunctions(main, null);
    }

    @EventHandler
    public void ZoneEnter(RegionEnterEvent e) {
        ZoneClass currentZone = null;
        for (ZoneClass zone : data.zones) {
            if (e.getRegion().getId().contains("zonecapture-"+zone.getName())) {
                currentZone = zone;
                break;
            }
        }
        if (currentZone == null) {
            return;
        }
        Player p = e.getPlayer();
        if (currentZone.isClaimed()) {
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7Vous entrez dans une zone contrôlée par " + currentZone.getClaimer()));
            InGameUtilities.playPlayerSound(p.getPlayer(), "gun.hud.enter_area", SoundCategory.AMBIENT, 1, 1);
        }
        FactionInformation info = ff.getFactionInfo(ff.playerFactionName(p));
        if (info == null || !info.hasCapturePerk()) {
            return;
        }
        if(currentZone.isClaimed() && !currentZone.isClaimable())
        {
            return;
        }
        if(!data.isCapturing(currentZone.getName(), info.getName()) && currentZone.isClaimable())
        {
            data.AddCapturing(currentZone.getName(), info.getName(), p);
        }

    }

    @EventHandler
    public void ZoneLeft(RegionLeftEvent e) {
        ZoneClass currentZone = null;
        for (ZoneClass zone : data.zones) {
            if (e.getRegion().getId().contains(zone.getName())) {
                currentZone = zone;
                break;
            }
        }
        if (currentZone == null) {
            return;
        }
        Player p = e.getPlayer();
        if (currentZone.isClaimed()) {
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cVous quittez une zone contrôlée par " + currentZone.getClaimer()));
            InGameUtilities.playPlayerSound(p.getPlayer(), "gun.hud.leaving_area", SoundCategory.AMBIENT, 1, 1);
        }
        FactionInformation info = ff.getFactionInfo(ff.playerFactionName(p));

        if (info == null || !info.hasCapturePerk()) {
            return;
        }
        data.RemoveCapturing(currentZone.getName(), info.getName(), p);
    }

    @EventHandler
    public void OnDisconnect(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        FactionInformation info = ff.getFactionInfo(ff.playerFactionName(p));
        if (info == null)
        {
            return;
        }
        for(ZoneClass zone : data.zones)
        {
            data.RemoveCapturing(zone.getName(), info.getName(), p);
        }

    }

}
