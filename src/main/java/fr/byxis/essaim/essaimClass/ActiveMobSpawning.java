package fr.byxis.essaim.essaimClass;

import io.lumine.mythic.core.mobs.ActiveMob;

import java.util.ArrayList;

public class ActiveMobSpawning {

    private ArrayList<ActiveMob> activeMobs;
    private final Integer max;
    private Integer remaining;

    public ActiveMobSpawning(Integer max)
    {
        this.activeMobs = new ArrayList<>();
        this.max = max;
        this.remaining = max-1;
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
        return this.remaining <= 0 && this.activeMobs.size() == 0;
    }
}
