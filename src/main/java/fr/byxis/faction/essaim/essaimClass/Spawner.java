package fr.byxis.faction.essaim.essaimClass;

import org.bukkit.Location;

public class Spawner {

    private final String name;
    private final String essaim;
    private final Location loc;
    private final String mob;
    private final int amount;
    private final String command;
    private final double activationDelay;
    private final double spawnDelay;
    private final boolean isAffectedByDifficulty;

    public Spawner(String name, String essaim, Location loc, String mob, int amount, double activationDelay, double spawnDelay, String command, Boolean isAffecteByDifficulty)
    {
        this.name = name;
        this.essaim = essaim;
        this.loc = loc;
        this.mob = mob;
        this.activationDelay = activationDelay;
        this.spawnDelay = spawnDelay;
        this.command = command;
        this.amount = amount;
        this.isAffectedByDifficulty = isAffecteByDifficulty;
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

    public double getActivationDelay() {
        return activationDelay;
    }

    public double getSpawnDelay() {
        return spawnDelay;
    }

    public boolean isAffectedByDifficulty() {
        return isAffectedByDifficulty;
    }
}
