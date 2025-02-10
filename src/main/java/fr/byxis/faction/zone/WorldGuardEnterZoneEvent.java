package fr.byxis.faction.zone;

import de.netzkronehd.wgregionevents.events.RegionEnterEvent;
import de.netzkronehd.wgregionevents.events.RegionLeftEvent;
import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.faction.faction.FactionInformation;
import fr.byxis.faction.zone.zoneclass.ZoneClass;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.player.level.PlayerLevel;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;

import static fr.byxis.player.level.LevelStorage.getPlayerLevel;

public class WorldGuardEnterZoneEvent implements Listener {

    private final DataZone data;
    private final FactionFunctions ff;
    private final Fireland main;

    public WorldGuardEnterZoneEvent(Fireland _main, DataZone _data) {
        this.main = _main;
        this.data = _data;
        this.ff = new FactionFunctions(_main, null);
    }

    @EventHandler
    public void zoneEnter(RegionEnterEvent e) {
        ZoneClass captureZone = null;
        ZoneClass capturedZone = null;
        Player p = e.getPlayer();
        for (ZoneClass zone : data.getZones()) {
            if (e.getRegion().getId().contains("zonecapture-" + zone.getName())) {
                captureZone = zone;
                break;
            }
            else if (e.getRegion().getId().contains("zoneenter-" + zone.getName())) {
                capturedZone = zone;
                data.addPlayerToZoneEnter(zone.getName(), p);
                break;
            }
        }
        if (captureZone == null) {
            if (capturedZone != null && capturedZone.isClaimed()) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7Vous entrez dans la zone " + capturedZone.getFormattedName() + " contrôlée par " + ff.getFactionInfo(capturedZone.getClaimer()).getColorcode() + capturedZone.getClaimer()));
                InGameUtilities.playPlayerSound(p, "gun.hud.enter_area", SoundCategory.AMBIENT, 1, 1);
            }
            else if (capturedZone != null)
            {
                capturedZone.addBar(p);
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7Vous entrez dans la zone " + capturedZone.getFormattedName()));
                InGameUtilities.playPlayerSound(p, "gun.hud.enter_area", SoundCategory.AMBIENT, 1, 1);
            }
            return;
        }

        FactionInformation info = ff.getFactionInfo(ff.playerFactionName(p));
        if (info == null || !info.hasCapturePerk()) {
            return;
        }
        if (captureZone.isClaimed() && !captureZone.isClaimable())
        {
            return;
        }

        PlayerLevel pl = getPlayerLevel(p.getUniqueId());
        if (pl.getLevel() < 10)
        {
            InGameUtilities.sendPlayerError(p, "Vous devez atteindre le niveau 10 pour capturer une zone.");
            return;
        }

        if (isTimeToCapture() && captureZone.isClaimable())

            if (!data.isCapturing(captureZone.getName(), info.getName()) && captureZone.isClaimable() && isTimeToCapture())
            {
                data.addCapturing(captureZone.getName(), info.getName(), p);
            }
    }

    @EventHandler
    public void zoneLeft(RegionLeftEvent e) {
        ZoneClass captureZone = null;
        ZoneClass capturedZone = null;
        Player p = e.getPlayer();
        for (ZoneClass zone : data.getZones()) {
            if (e.getRegion().getId().contains("zonecapture-" + zone.getName())) {
                captureZone = zone;
                break;
            }
            else if (e.getRegion().getId().contains("zoneenter-" + zone.getName())) {
                capturedZone = zone;
                data.remPlayerToZoneEnter(zone.getName(), p);
                break;
            }
        }
        if (captureZone == null) {
            if (capturedZone != null && capturedZone.isClaimed()) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cVous quittez dans la zone " + capturedZone.getFormattedName() + " contrôlée par " + ff.getFactionInfo(capturedZone.getClaimer()).getColorcode() + capturedZone.getClaimer()));
                InGameUtilities.playPlayerSound(p, "gun.hud.leaving_area", SoundCategory.AMBIENT, 1, 1);
            }
            else if (capturedZone != null)
            {
                capturedZone.removeBar(p);
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7Vous quittez dans la zone " + capturedZone.getFormattedName()));
                InGameUtilities.playPlayerSound(p, "gun.hud.leaving_area", SoundCategory.AMBIENT, 1, 1);
            }
            return;
        }
        FactionInformation info = ff.getFactionInfo(ff.playerFactionName(p));

        if (info == null || !info.hasCapturePerk()) {
            return;
        }
        data.removeCapturing(captureZone.getName(), info.getName(), p);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        FactionInformation info = ff.getFactionInfo(ff.playerFactionName(p));
        if (info == null)
        {
            return;
        }
        for (ZoneClass zone : data.getZones())
        {
            data.removeCapturing(zone.getName(), info.getName(), p);
        }
        data.remPlayerToZoneEnter(p);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e)
    {
        Player p = e.getPlayer();
        FactionInformation info = ff.getFactionInfo(ff.playerFactionName(p));
        if (info == null)
        {
            return;
        }
        for (ZoneClass zone : data.getZones())
        {
            data.removeCapturing(zone.getName(), info.getName(), p);
        }
        data.remPlayerToZoneEnter(p);
    }

    public static boolean isTimeToCapture()
    {
        Date current = new Date();
        return current.getHours() >= 18 && current.getHours() <= 19;
    }

}
