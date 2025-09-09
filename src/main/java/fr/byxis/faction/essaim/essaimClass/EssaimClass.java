package fr.byxis.faction.essaim.essaimClass;

import fr.byxis.faction.essaim.EssaimConfigManager;
import fr.byxis.fireland.utilities.TextUtilities;
import org.bukkit.Bukkit;
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
    private final Location difficulty1;
    private final Location difficulty2;
    private final Location difficulty3;
    private final int jetons;
    private Timestamp finishDate;
    private boolean isClosed;
    private boolean isEvent;
    private final int delayBetwenEvent;



    public EssaimClass(String _name, EssaimConfigManager configManager)
    {
        this.name = _name;
        this.day = configManager.getConfig().getInt(_name + ".day");
        this.hour = configManager.getConfig().getInt(_name + ".hour");
        this.hub = new Location(Bukkit.getWorld(configManager.getConfig().getString(_name + ".hub.position.world")), //hub
                configManager.getConfig().getInt(_name + ".hub.position.x"),
                configManager.getConfig().getInt(_name + ".hub.position.y"),
                configManager.getConfig().getInt(_name + ".hub.position.z")
        );
        this.start = new Location(Bukkit.getWorld(configManager.getConfig().getString(_name + ".start.position.world")), //start
                configManager.getConfig().getInt(_name + ".start.position.x"),
                configManager.getConfig().getInt(_name + ".start.position.y"),
                configManager.getConfig().getInt(_name + ".start.position.z")
        );
        this.reset = new Location(Bukkit.getWorld(configManager.getConfig().getString(_name + ".reset.position.world")), //reset
                configManager.getConfig().getInt(_name + ".reset.position.x"),
                configManager.getConfig().getInt(_name + ".reset.position.y"),
                configManager.getConfig().getInt(_name + ".reset.position.z")
        );
        this.entry = new Location(Bukkit.getWorld(configManager.getConfig().getString(_name + ".entry.position.world")), //entry
                configManager.getConfig().getInt(_name + ".entry.position.x"),
                configManager.getConfig().getInt(_name + ".entry.position.y"),
                configManager.getConfig().getInt(_name + ".entry.position.z")
        );
        this.solo = new Location(Bukkit.getWorld(configManager.getConfig().getString(_name + ".solo.position.world")), //entry
                configManager.getConfig().getInt(_name + ".solo.position.x"),
                configManager.getConfig().getInt(_name + ".solo.position.y"),
                configManager.getConfig().getInt(_name + ".solo.position.z")
        );
        if (configManager.getConfig().contains(_name + ".difficulty.1"))
        {
            this.difficulty1 = new Location(Bukkit.getWorld(configManager.getConfig().getString(_name + ".solo.position.world")), //entry
                    configManager.getConfig().getInt(_name + ".difficulty.1.position.x"),
                    configManager.getConfig().getInt(_name + ".difficulty.1.position.y"),
                    configManager.getConfig().getInt(_name + ".difficulty.1.position.z")
            );
        }
        else
        {
            this.difficulty1 = null;
        }
        if (configManager.getConfig().contains(_name + ".difficulty.1"))
        {
            this.difficulty2 = new Location(Bukkit.getWorld(configManager.getConfig().getString(_name + ".solo.position.world")), //entry
                    configManager.getConfig().getInt(_name + ".difficulty.2.position.x"),
                    configManager.getConfig().getInt(_name + ".difficulty.2.position.y"),
                    configManager.getConfig().getInt(_name + ".difficulty.2.position.z")
            );
        }
        else
        {
            this.difficulty2 = null;
        }

        if (configManager.getConfig().contains(_name + ".difficulty.1"))
        {
            this.difficulty3 = new Location(Bukkit.getWorld(configManager.getConfig().getString(_name + ".solo.position.world")), //entry
                    configManager.getConfig().getInt(_name + ".difficulty.3.position.x"),
                    configManager.getConfig().getInt(_name + ".difficulty.3.position.y"),
                    configManager.getConfig().getInt(_name + ".difficulty.3.position.z")
            );
        }
        else
        {
            this.difficulty3 = null;
        }

        if (configManager.getConfig().contains(_name + ".event.isevent"))
        {
            this.isEvent = configManager.getConfig().getBoolean(_name + ".event.isevent");
        }
        else
        {
            this.isEvent = false;
        }
        if (configManager.getConfig().contains(_name + ".event.delay"))
        {
            this.delayBetwenEvent = configManager.getConfig().getInt(_name + ".event.delay");
        }
        else
        {
            this.delayBetwenEvent = 6 * 30;
        }

        finishDate = null;
        this.jetons = configManager.getConfig().getInt(_name + ".jetons");
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
        if (isFinished())
        {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp close = new Timestamp(finishDate.getTime() + 8 * 60 * 1000);
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

    public Location getDifficulty1() {
        return difficulty1;
    }

    public Location getDifficulty2() {
        return difficulty2;
    }

    public Location getDifficulty3() {
        return difficulty3;
    }

    public boolean isEvenBased()
    {
        return isEvent;
    }

    public int getDelayBetwenEvent()
    {
        return delayBetwenEvent;
    }
}
