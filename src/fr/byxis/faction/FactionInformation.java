package fr.byxis.faction;

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

    public FactionInformation(String name, int nbrOfPlayers, int maxNbrOfPlayers, int currentUpgrade, int currentMoney, int maxMoney, int currentChestSize, Timestamp createdAt, UUID leader)
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
}
