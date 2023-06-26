package fr.byxis.faction.faction;

import java.sql.Timestamp;
import java.util.UUID;

public class FactionInformation {

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
    private final boolean show_prefix;
    private final boolean hasCapturePerk;

    private final String colorcode;
    private final boolean hasZoneTpPerk;

    public FactionInformation(String name, int nbrOfPlayers, int maxNbrOfPlayers, int currentUpgrade,
                              int currentMoney, int maxMoney, int currentChestSize, Timestamp createdAt,
                              UUID leader, boolean hasFriendlyFirePerk, boolean hasNicknameVisibilityPerk,
                              boolean hasSkinPerk, boolean show_prefix, boolean hasCapturePerk, String colorcode,
                              boolean hasZoneTpPerk)
    {
        this.name = name;
        this.currentNbrOfPlayers = nbrOfPlayers;
        this.maxNbrOfPlayers = maxNbrOfPlayers;
        this.currentUpgrade = currentUpgrade;
        this.currentMoney = currentMoney;
        this.maxMoney = maxMoney;
        this.currentChestSize = currentChestSize;
        this.createdAt = createdAt;
        this.leader = leader;
        this.hasNicknameVisibilityPerk = hasNicknameVisibilityPerk;
        this.hasFriendlyFirePerk = hasFriendlyFirePerk;
        this.hasSkinPerk = hasSkinPerk;
        this.show_prefix = show_prefix;
        this.hasCapturePerk = hasCapturePerk;
        this.colorcode = colorcode;
        this.hasZoneTpPerk = hasZoneTpPerk;
    }

    public String getName()
    {
        return name;
    }

    public int getCurrentNbrOfPlayers() {
        return currentNbrOfPlayers;
    }

    public int getCurrentUpgrade() {
        return currentUpgrade;
    }

    public int getCurrentMoney() {
        return currentMoney;
    }

    public int getCurrentChestSize() {
        return currentChestSize;
    }

    public int getMaxMoney() {
        return maxMoney;
    }

    public int getMaxNbrOfPlayers() {
        return maxNbrOfPlayers;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public UUID getLeader() {
        return leader;
    }

    public boolean hasNicknameVisibilityPerk() {
        return hasNicknameVisibilityPerk;
    }

    public boolean hasFriendlyFirePerk() {
        return hasFriendlyFirePerk;
    }

    public String getColorcode() {
        return colorcode;
    }

    public boolean hasSkinPerk() {
        return hasSkinPerk;
    }

    public boolean DoShowPrefix() {
        return show_prefix;
    }

    public boolean hasCapturePerk() {
        return hasCapturePerk;
    }

    public boolean hasZoneTpPerk() {
        return hasZoneTpPerk;
    }
}
