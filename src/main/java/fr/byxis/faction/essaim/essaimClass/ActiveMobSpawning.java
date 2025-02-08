package fr.byxis.faction.essaim.essaimClass;

import io.lumine.mythic.core.mobs.ActiveMob;

import java.util.ArrayList;

public class ActiveMobSpawning {

    private final ArrayList<ActiveMob> activeMobs;
    private final Integer max;
    private Integer remaining;
    private final String essaim;

    public ActiveMobSpawning(String _essaim, Integer _max)
    {
        this.activeMobs = new ArrayList<>();
        this.max = _max;
        this.remaining = _max - 1;
        this.essaim = _essaim;
    }

    public ArrayList<ActiveMob> getActiveMobs() {
        return activeMobs;
    }

    public void addActiveMob(ActiveMob activeMob) {
        this.activeMobs.add(activeMob);
    }

    public void removeActiveMob(ActiveMob activeMob) {
        this.activeMobs.remove(activeMob);
        this.remaining--;
    }

    public int getRemaining()
    {
        return this.remaining;
    }

    public boolean isSpawnerFinished()
    {
        return this.remaining <= 0 && this.activeMobs.isEmpty();
    }

    public Integer getMax() {
        return max;
    }

    public String getEssaim() {
        return essaim;
    }
}
