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

    public Spawner(String _name, String _essaim, Location _loc, String _mob, int _amount, double _activationDelay, double _spawnDelay, String _command, Boolean _isAffecteByDifficulty)
    {
        this.name = _name;
        this.essaim = _essaim;
        this.loc = _loc;
        this.mob = _mob;
        this.activationDelay = _activationDelay;
        this.spawnDelay = _spawnDelay;
        this.command = _command;
        this.amount = _amount;
        this.isAffectedByDifficulty = _isAffecteByDifficulty;
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
