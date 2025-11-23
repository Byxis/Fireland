package fr.byxis.player.discretion;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DiscretionClass
{

    private double m_score;
    private boolean m_eating;
    private int m_shooting;
    private boolean m_moving;
    private boolean m_isListeningMovements;
    private boolean m_isUsingLights;
    private int m_camoReduction;

    public DiscretionClass()
    {
        this.m_score = 100;
        this.m_eating = false;
        this.m_shooting = 0;
        this.m_moving = false;
        this.m_isListeningMovements = false;
        this.m_isUsingLights = false;
        this.m_camoReduction = 0;

    }

    public double getScore()
    {
        return m_score;
    }

    public void setScore(double _score)
    {
        this.m_score = _score;
    }

    public boolean isEating()
    {
        return m_eating;
    }

    public void setEating(boolean _eating)
    {
        this.m_eating = _eating;
    }

    public boolean isShooting()
    {
        return m_shooting > 0;
    }
    public void reduceTimeShooting()
    {
        if (!isShooting())
            return;
        m_shooting -= 1;
        if (m_shooting < 0)
            m_shooting = 0;
    }

    public void setShooting(int _shooting)
    {
        this.m_shooting = _shooting;
    }

    public void setMoving(boolean _moving)
    {
        this.m_moving = _moving;
    }

    public boolean isMoving()
    {
        return m_moving;
    }

    public boolean isListeningMovements()
    {
        return m_isListeningMovements;
    }

    public void setListeningMovements(boolean listeningMovements)
    {
        m_isListeningMovements = listeningMovements;
    }

    public boolean isUsingLights()
    {
        return m_isUsingLights;
    }

    public void setUsingLights(boolean usingLights)
    {
        m_isUsingLights = usingLights;
    }

    public float getCamoReduction()
    {
        return m_camoReduction;
    }

    public boolean isUsingCamo()
    {
        return m_camoReduction > 0;
    }

    public void setCamoReduction(int _camoReduction)
    {
        m_camoReduction = _camoReduction;
    }

    public void actualizeDiscretion(Player player)
    {
        double discretion = DiscretionManager.MAX_SCORE;

        if (isMoving())
        {
            discretion -= DiscretionManager.MOVEMENT_PENALTY;
            if (player.isSneaking())
            {
                discretion += DiscretionManager.SNEAKING_REDUCTION;
            }
        }
        if (isUsingCamo())
        {
            discretion += getCamoReduction();
        }

        if (player.isSprinting() || player.isSwimming() || player.isClimbing())
        {
            discretion -= DiscretionManager.ACTIVE_MOVEMENT_PENALTY;
        }
        if (!player.isFlying() && !((Entity) player).isOnGround() && !player.isClimbing())
        {
            discretion -= DiscretionManager.AIRBORNE_PENALTY;
        }
        if (isEating())
        {
            discretion -= DiscretionManager.EAT_PENALTY;
        }

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();

        if (itemInMainHand.getType() == Material.END_ROD || itemInOffHand.getType() == Material.END_ROD)
        {
            discretion -= DiscretionManager.LIGHT_PENALTY;
            setUsingLights(true);
        }
        else
        {
            setUsingLights(false);
        }

        if (discretion > DiscretionManager.MAX_SCORE)
        {
            discretion = DiscretionManager.MAX_SCORE;
        }
        else if (discretion < 0)
        {
            discretion = 0;
        }

        setScore(discretion);
    }
}
