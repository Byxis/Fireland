package fr.byxis.faction.essaim.essaimClass;

import org.bukkit.Location;

/**
 * Class representing a spawner in the game.
 */
public class Spawner
{

    private final String m_name;
    private final String m_essaim;
    private final Location m_loc;
    private final String m_mob;
    private final int m_amount;
    private final String m_command;
    private final double m_activationDelay;
    private final double m_spawnDelay;
    private final boolean m_isAffectedByDifficulty;

    public Spawner(String _name, String _essaim, Location _loc, String _mob, int _amount, double _activationDelay, double _spawnDelay,
            String _command, Boolean _isAffecteByDifficulty)
    {
        this.m_name = _name;
        this.m_essaim = _essaim;
        this.m_loc = _loc;
        this.m_mob = _mob;
        this.m_activationDelay = _activationDelay;
        this.m_spawnDelay = _spawnDelay;
        this.m_command = _command;
        this.m_amount = _amount;
        this.m_isAffectedByDifficulty = _isAffecteByDifficulty;
    }

    public int getAmount()
    {
        return m_amount;
    }

    public String getMob()
    {
        return m_mob;
    }

    public Location getLoc()
    {
        return m_loc;
    }

    public String getEssaim()
    {
        return m_essaim;
    }

    public String getName()
    {
        return m_name;
    }

    public String getCommand()
    {
        return m_command;
    }

    public double getActivationDelay()
    {
        return m_activationDelay;
    }

    public double getSpawnDelay()
    {
        return m_spawnDelay;
    }

    public boolean isAffectedByDifficulty()
    {
        return m_isAffectedByDifficulty;
    }
}
