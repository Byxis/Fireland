package fr.byxis.essaim.essaimClass;

import io.lumine.mythic.api.mobs.MythicMob;
import org.bukkit.Location;

public class Spawner {

    private final String name;
    private final String essaim;
    private final Location loc;
    private final String mob;
    private final int amount;
    private final String command;

    public Spawner(String name, String essaim, Location loc, String mob, int amount, String command)
    {
        this.name = name;
        this.essaim = essaim;
        this.loc = loc;
        this.mob = mob;
        this.amount = amount;
        this.command = command;
    }

    public int getAmount() {
        return amount;
    }

    public String getMob() {
        return mob;
    }

    public Location getLoc() {
        return loc;
    }

    public String getEssaim() {
        return essaim;
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }
}
