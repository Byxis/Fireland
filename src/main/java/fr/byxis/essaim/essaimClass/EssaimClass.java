package fr.byxis.essaim.essaimClass;

import fr.byxis.fireland.utilities.TextUtilities;
import org.bukkit.Location;

import java.sql.Timestamp;

public class EssaimClass {

    private final String name;
    private final int day;
    private final int hour;
    private final Location hub;
    private final Location entry;
    private final Location reset;
    private final Location start;
    private final Location solo;
    private final int jetons;
    private Timestamp finishDate;
    private boolean isClosed;


    public EssaimClass(String name, int day, int hour, Location hub,Location start,Location reset,Location entry, Location solo, int jetons)
    {
        this.name = name;
        this.day = day;
        this.hour = hour;
        this.hub = hub;
        this.start = start;
        this.reset = reset;
        this.entry = entry;
        this.solo = solo;
        finishDate = null;
        this.jetons = jetons;
        this.isClosed = false;
    }

    public Location getHub() {
        return hub;
    }

    public int getHour() {
        return hour;
    }

    public int getDay() {
        return day;
    }

    public String getName() {
        return name;
    }

    public String getFormattedName() {
        return TextUtilities.convertStorableToClean(name);
    }

    public Location getEntry() {
        return entry;
    }

    public Location getReset() {
        return reset;
    }

    public Location getSolo() {
        return solo;
    }

    public Location getStart() {
        return start;
    }

    public boolean isFinished() {
        return finishDate != null;
    }

    public boolean shouldClose() {
        if(isFinished())
        {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp close = new Timestamp(finishDate.getTime() + 8*60*1000);
            return now.after(close);
        }
        return false;
    }

    public Timestamp getFinishDate() {
        return finishDate;
    }

    public void setFinish() {
        this.finishDate = new Timestamp(System.currentTimeMillis());
    }

    public void unFinish() {
        this.finishDate = null;
    }

    public int getJetons() {
        return jetons;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}
