package fr.byxis.player.booster;

import java.sql.Date;
import java.util.UUID;

public class BoosterClass {

    private final Date started;
    private final Date finished;
    private final UUID uuid;
    private final double moneyMin;
    private final double moneyMax;
    private final double boosterLootPercent;
    private final int level;

    public BoosterClass(Date _started, Date _finished, UUID _uuid, int _level)
    {
        this.started = _started;
        this.finished = _finished;
        this.uuid = _uuid;
        this.level = _level;
        switch (_level)
        {
            default:
                this.moneyMin = -4;
                this.moneyMax = 1;
                this.boosterLootPercent = 5;
                break;
            case 2:
                this.moneyMin = -9;
                this.moneyMax = 2;
                this.boosterLootPercent = 10;
                break;
            case 3:
                this.moneyMin = -14;
                this.moneyMax = 3;
                this.boosterLootPercent = 15;
                break;
        }
    }

    public Date getStarted() {
        return started;
    }

    public Date getFinished() {
        return finished;
    }

    public double getMoneyMin() {
        return moneyMin;
    }

    public double getMoneyMax() {
        return moneyMax;
    }

    public double getBoosterLootPercent() {
        return boosterLootPercent;
    }

    public int getLevel() {
        return level;
    }

    public UUID getUuid() {
        return uuid;
    }
}
