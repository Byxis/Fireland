package fr.byxis.player.quest.questclass;

import java.util.UUID;

public class PlayerQuests {

    private final UUID uuid;
    private ProgressQuest first;
    private ProgressQuest second;
    private ProgressQuest third;
    private ProgressQuest fourth;
    private boolean isClaimed;

    public PlayerQuests(UUID _uuid)
    {
        this.uuid = _uuid;
        isClaimed = false;
        first = null;
        second = null;
        third = null;
        fourth = null;
    }

    public void set(ProgressQuest progress)
    {
        if (first == null)
        {
            first = progress;
        }
        else if (second == null)
        {
            second = progress;
        }
        else if (third == null)
        {
            third = progress;
        }
        else if (fourth == null)
        {
            fourth = progress;
        }
    }

    public boolean isFull()
    {
        return first != null && second != null && third != null && fourth != null;
    }
    public int numberOfQuests()
    {
        int i = 0;
        if (first != null)
        {
            i++;
        }
        if (second != null)
        {
            i++;
        }
        if (third != null)
        {
            i++;
        }
        if (fourth != null)
        {
            i++;
        }
        return i;
    }

    public void setFirst(ProgressQuest _first)
    {
        this.first = _first;
    }

    public ProgressQuest getQuest(int i)
    {
        return switch (i)
        {
            default -> first;
            case 2 -> second;
            case 3 -> third;
            case 4 -> fourth;
        };
    }
    public ProgressQuest getFirst()
    {
        return first;
    }

    public void setSecond(ProgressQuest _second)
    {
        this.second = _second;
    }
    public ProgressQuest getSecond()
    {
        return second;
    }

    public void setThird(ProgressQuest _third)
    {
        this.third = _third;
    }
    public ProgressQuest getThird()
    {
        return third;
    }

    public void setFourth(ProgressQuest _fourth)
    {
        this.fourth = _fourth;
    }
    public ProgressQuest getFourth()
    {
        return fourth;
    }


    public UUID getUuid() {
        return uuid;
    }
    public boolean isQuestFinished(int i)
    {
        return getQuest(i).getProgress() == -1;
    }

    public boolean hasFinished()
    {
        if (first == null || second == null || third == null || fourth == null)
        {
            return false;
        }
        return first.getProgress() == -1 && second.getProgress() == -1 && third.getProgress() == -1 && fourth.getProgress() == -1;
    }

    public boolean isClaimed() {
        return isClaimed;
    }

    public void setClaimed(boolean claimed) {
        isClaimed = claimed;
    }
}
