package fr.byxis.faction.faction;

import java.sql.Timestamp;
import java.util.UUID;

public class FactionInformation
{

    private final String name;
    private final int currentNbrOfPlayers;
    private final int maxNbrOfPlayers;
    private final int currentUpgrade;
    private final int currentMoney;
    private final int maxMoney;
    private final int currentChestSize;
    private final Timestamp createdAt;
    private final UUID leader;

    private final boolean hasNicknameVisibilityPerk;
    private final boolean hasFriendlyFirePerk;
    private final boolean hasSkinPerk;
    private final boolean showPrefix;
    private final boolean hasCapturePerk;

    private final String colorcode;
    private final boolean hasZoneTpPerk;

    public FactionInformation(String _name, int _nbrOfPlayers, int _maxNbrOfPlayers, int _currentUpgrade, int _currentMoney, int _maxMoney,
            int _currentChestSize, Timestamp _createdAt, UUID _leader, boolean _hasFriendlyFirePerk, boolean _hasNicknameVisibilityPerk,
            boolean _hasSkinPerk, boolean _showPrefix, boolean _hasCapturePerk, String _colorcode, boolean _hasZoneTpPerk)
    {
        this.name = _name;
        this.currentNbrOfPlayers = _nbrOfPlayers;
        this.maxNbrOfPlayers = _maxNbrOfPlayers;
        this.currentUpgrade = _currentUpgrade;
        this.currentMoney = _currentMoney;
        this.maxMoney = _maxMoney;
        this.currentChestSize = _currentChestSize;
        this.createdAt = _createdAt;
        this.leader = _leader;
        this.hasNicknameVisibilityPerk = _hasNicknameVisibilityPerk;
        this.hasFriendlyFirePerk = _hasFriendlyFirePerk;
        this.hasSkinPerk = _hasSkinPerk;
        this.showPrefix = _showPrefix;
        this.hasCapturePerk = _hasCapturePerk;
        this.colorcode = _colorcode;
        this.hasZoneTpPerk = _hasZoneTpPerk;
    }

    public String getName()
    {
        return name;
    }

    public int getCurrentNbrOfPlayers()
    {
        return currentNbrOfPlayers;
    }

    public int getCurrentUpgrade()
    {
        return currentUpgrade;
    }

    public int getCurrentMoney()
    {
        return currentMoney;
    }

    public int getCurrentChestSize()
    {
        return currentChestSize;
    }

    public int getMaxMoney()
    {
        return maxMoney;
    }

    public int getMaxNbrOfPlayers()
    {
        return maxNbrOfPlayers;
    }

    public Timestamp getCreatedAt()
    {
        return createdAt;
    }

    public UUID getLeader()
    {
        return leader;
    }

    public boolean hasNicknameVisibilityPerk()
    {
        return hasNicknameVisibilityPerk;
    }

    public boolean hasFriendlyFirePerk()
    {
        return hasFriendlyFirePerk;
    }

    public String getColorcode()
    {
        return colorcode;
    }

    public boolean hasSkinPerk()
    {
        return hasSkinPerk;
    }

    public boolean doShowPrefix()
    {
        return showPrefix;
    }

    public boolean hasCapturePerk()
    {
        return hasCapturePerk;
    }

    public boolean hasZoneTpPerk()
    {
        return hasZoneTpPerk;
    }
}
