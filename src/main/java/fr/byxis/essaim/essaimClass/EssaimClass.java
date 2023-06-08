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
    private final int jetons;
    private Timestamp finishDate;

    public EssaimClass(String name, int day, int hour, Location hub,Location start,Location reset,Location entry, int jetons)
    {
        this.name = name;
        this.day = day;
        this.hour = hour;
        this.hub = hub;
        this.start = start;
        this.reset = reset;
        this.entry = entry;
        finishDate = null;
        this.jetons = jetons;
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

    public int getJetons() {
        return jetons;
    }
}
