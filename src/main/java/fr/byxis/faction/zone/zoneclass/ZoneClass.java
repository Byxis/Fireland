package fr.byxis.faction.zone.zoneclass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.sql.Timestamp;

import static fr.byxis.faction.zone.WorldGuardEnterZoneEvent.isTimeToCapture;
import static fr.byxis.fireland.utilities.BlockUtilities.getBossBarColor;

public class ZoneClass {
    private final String name;

    private final Location location;

    private final int price;
    private final int dailyGain;
    private final int finalDollarsGain;
    private final int finalJetonsGain;
    private final double captureTime;


    private final double privationDuration;
    private final double autoRelease;
    private boolean claimable;

    private boolean claimed;
    private String claimer;
    private Timestamp claimedAt;
    private final BossBar bar;
    private String barColor;

    public ZoneClass(String _name, Location _loc, int _price, int _dailyGain, int _finalDollarsGain, int _finalJetonsGain, double _privationDuration, double _autoRelease, double _captureTime, boolean _claimable)
    {
        this.name = _name;
        this.location = _loc;
        this.price = _price;
        this.dailyGain = _dailyGain;
        this.finalDollarsGain = _finalDollarsGain;
        this.finalJetonsGain = _finalJetonsGain;
        this.privationDuration = _privationDuration;
        this.autoRelease = _autoRelease;
        this.captureTime = _captureTime;
        this.claimable = _claimable;
        bar = Bukkit.createBossBar("Capture de la zone " + getFormattedName() + " disponible", BarColor.WHITE, BarStyle.SEGMENTED_10);
        bar.setProgress(0);
        setColor("§f");
        if (!isTimeToCapture())
        {
            bar.setTitle("Zone " + getFormattedName());
            setColor("§7");
        }
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public int getPrice() {
        return price;
    }

    public int getDailyGain() {
        return dailyGain;
    }

    public int getFinalDollarsGain() {
        return finalDollarsGain;
    }

    public int getFinalJetonsGain() {
        return finalJetonsGain;
    }

    public double getPrivationDuration() {
        return privationDuration;
    }

    public double getAutoRelease() {
        return autoRelease;
    }

    public void setClaimed(String _name, Timestamp date)
    {
        this.claimed = true;
        this.claimer = _name;
        this.claimedAt = date;
        this.claimable = false;
    }

    public void setClaimer(String _name)
    {
        this.claimer = _name;
    }

    public void unclaim()
    {
        this.claimed = false;
        this.claimer = null;
        this.claimedAt = null;
        this.claimable = true;

    }
    public String getClaimer()
    {
        return this.claimer;
    }
    public boolean isClaimed()
    {
        return this.claimed;
    }
    public Timestamp getClaimedAt()
    {
        return this.claimedAt;
    }

    public double getCaptureTime() {
        return captureTime;
    }

    public boolean isClaimable()
    {
        return this.claimable;
    }

    public void setClaimable()
    {
        this.claimable = true;
    }

    public void unsetClaimable()
    {
        this.claimable = false;
    }

    public String getFormattedName()
    {
        return (name.substring(0, 1).toUpperCase() + name.substring(1)).replaceAll("-", " ");
    }

    public void addBar(Player p)
    {
        if (!bar.getPlayers().contains(p))
            bar.addPlayer(p);
    }

    public void removeBar(Player p)
    {
        if (bar.getPlayers().contains(p))
            bar.removePlayer(p);
    }

    public void removeAllBar()
    {
        bar.removeAll();
    }

    public void setProgressBar(double prog, String faction)
    {
        if (prog != 0)
        {
            bar.setProgress(prog / 100);
            bar.setTitle(barColor + "Capture par " + faction + " - " + prog + "%");
        }
        else
        {
            bar.setProgress(0);
            bar.setTitle("Capture de la zone " + getFormattedName() + " disponible");
            bar.setColor(BarColor.WHITE);
        }
    }

    public void setColor(String color)
    {
        barColor = color;
        bar.setColor(getBossBarColor(color));
    }
}
