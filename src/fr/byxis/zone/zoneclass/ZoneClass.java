package fr.byxis.zone.zoneclass;

import org.bukkit.Location;

import java.sql.Timestamp;

public class ZoneClass {
    private final String name;

    private final Location location;

    private final int price;
    private final int daily_gain;
    private final int finalDollarsGain;
    private final int finalJetonsGain;
    private final int captureTime;


    private final int privationDuration;
    private final int autoRelease;
    private boolean claimable;

    private boolean claimed;
    private String claimer;
    private Timestamp claimedAt;

    public ZoneClass(String name, Location loc, int price, int daily_gain, int finalDollarsGain, int finalJetonsGain, int privationDuration, int autoRelease, int captureTime, boolean claimable)
    {
        this.name = name;
        this.location = loc;
        this.price = price;
        this.daily_gain = daily_gain;
        this.finalDollarsGain = finalDollarsGain;
        this.finalJetonsGain = finalJetonsGain;
        this.privationDuration = privationDuration;
        this.autoRelease = autoRelease;
        this.captureTime = captureTime;
        this.claimable = claimable;
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

    public int getDaily_gain() {
        return daily_gain;
    }

    public int getFinalDollarsGain() {
        return finalDollarsGain;
    }

    public int getFinalJetonsGain() {
        return finalJetonsGain;
    }

    public int getPrivationDuration() {
        return privationDuration;
    }

    public int getAutoRelease() {
        return autoRelease;
    }

    public void setClaimed(String name, Timestamp date)
    {
        this.claimed = true;
        this.claimer = name;
        this.claimedAt = date;
    }

    public void setClaimer(String name)
    {
        this.claimer = name;
    }

    public void unclaim()
    {
        this.claimed = false;
        this.claimer = null;
        this.claimedAt = null;
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

    public int getCaptureTime() {
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
}
