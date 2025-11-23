package fr.byxis.player.bank;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.PermissionUtilities;
import java.util.UUID;
import org.bukkit.entity.Player;

public class BankAccount
{
    private final Fireland m_fireland;
    private final String m_ownerId;

    public BankAccount(Fireland _fireland, String _ownerId)
    {
        this.m_fireland = _fireland;
        this.m_ownerId = _ownerId;
    }

    public int getMoney()
    {
        return m_fireland.getCfgm().getEnderchest().getInt("bank." + m_ownerId + ".money", 0);
    }

    public void setMoney(int amount)
    {
        m_fireland.getCfgm().getEnderchest().set("bank." + m_ownerId + ".money", amount);
        m_fireland.getCfgm().saveEnderchest();
    }

    public int getUpgradeLevel()
    {
        return m_fireland.getCfgm().getEnderchest().getInt("bank." + m_ownerId + ".upgrade", 0);
    }

    public void setUpgradeLevel(int level)
    {
        m_fireland.getCfgm().getEnderchest().set("bank." + m_ownerId + ".upgrade", level);
        m_fireland.getCfgm().saveEnderchest();
    }

    public int getMaxMoney()
    {
        int upgrade = getUpgradeLevel();
        return switch (upgrade)
        {
            case 1 -> 2500;
            case 2 -> 5000;
            case 3 -> 10000;
            case 4 -> 25000;
            case 5 -> 50000;
            case 6, 7 -> 100000;
            default -> 1000;
        };
    }

    public int getMaxSlots()
    {
        int upgrade = getUpgradeLevel();
        int baseMax = switch (upgrade)
        {
            case 0 -> 9;
            case 1 -> 18;
            case 2 -> 27;
            case 3 -> 36;
            case 4 -> 45;
            case 5, 6, 7 -> 54;
            default -> 0;
        };

        if (baseMax == 54)
        {
            Player player = m_fireland.getServer().getPlayer(UUID.fromString(m_ownerId));
            if (player != null)
            {
                if (PermissionUtilities.hasPermission(player, "fireland.bank.bonus.1"))
                {
                    baseMax += 54;
                }
                if (PermissionUtilities.hasPermission(player, "fireland.bank.bonus.2"))
                {
                    baseMax += 54;
                }
                if (PermissionUtilities.hasPermission(player, "fireland.bank.bonus.3"))
                {
                    baseMax += 54;
                }
            }
        }
        return baseMax;
    }
}
