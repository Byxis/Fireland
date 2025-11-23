package fr.byxis.faction.essaim.essaimClass;

import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.ArrayList;

/**
 * Class to manage the spawning of a group of mobs in an essaim
 */
public class ActiveMobSpawning
{

    private final ArrayList<ActiveMob> m_activeMobs;
    private final Integer m_maxMobs;
    private Integer m_remainingMobs;
    private final String m_essaim;

    public ActiveMobSpawning(String _essaim, Integer _max)
    {
        this.m_activeMobs = new ArrayList<>();
        this.m_maxMobs = _max;
        this.m_remainingMobs = _max - 1;
        this.m_essaim = _essaim;
    }

    /**
     * Get the list of active mobs
     * 
     * @return ArrayList of ActiveMob
     */
    public ArrayList<ActiveMob> getActiveMobs()
    {
        return m_activeMobs;
    }

    /**
     * Add an active mob to the list
     * 
     * @param activeMob
     *            ActiveMob to add
     */
    public void addActiveMob(ActiveMob activeMob)
    {
        this.m_activeMobs.add(activeMob);
    }

    /**
     * Remove an active mob from the list
     * 
     * @param activeMob
     *            ActiveMob to remove
     */
    public void removeActiveMob(ActiveMob activeMob)
    {
        this.m_activeMobs.remove(activeMob);
        this.m_remainingMobs--;
    }

    /**
     * Get the number of remaining mobs to spawn
     * 
     * @return int number of remaining mobs
     */
    public int getRemaining()
    {
        return this.m_remainingMobs;
    }

    /**
     * Check if the spawner is finished (no more mobs to spawn and no active mobs)
     * 
     * @return boolean true if finished, false otherwise
     */
    public boolean isSpawnerFinished()
    {
        return this.m_remainingMobs <= 0 && this.m_activeMobs.isEmpty();
    }

    /**
     * Get the maximum number of mobs to spawn
     * 
     * @return int maximum number of mobs
     */
    public Integer getMax()
    {
        return m_maxMobs;
    }

    /**
     * Get the name of the essaim associated with this spawner
     * 
     * @return String name of the essaim
     */
    public String getEssaim()
    {
        return m_essaim;
    }

    /**
     * Clear all active mobs from the list and remove them from the world
     */
    public void clear()
    {
        for (ActiveMob mob : m_activeMobs)
        {
            mob.remove();
            this.m_remainingMobs--;
        }
    }
}
