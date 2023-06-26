package fr.byxis.player.quest.questclass;

import org.bukkit.entity.Player;

import java.util.UUID;

import static fr.byxis.fireland.utilities.InGameUtilities.debugp;

public class PlayerQuests {

    private final UUID uuid;
    private ProgressQuest first;
    private ProgressQuest second;
    private ProgressQuest third;
    private ProgressQuest fourth;
    private boolean isClaimed;

    public PlayerQuests(UUID uuid)
    {
        this.uuid = uuid;
        isClaimed = false;
        first = null;
        second = null;
        third = null;
        fourth = null;
    }

    public void set(ProgressQuest progress)
    {
        if(first == null)
        {
            first = progress;
        }
        else if(second == null)
        {
            second = progress;
        }
        else if(third == null)
        {
            third = progress;
        }
        else if( fourth == null)
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
        if(first != null)
        {
            i++;
        }
        if(second != null)
        {
            i++;
        }
        if(third != null)
        {
            i++;
        }
        if( fourth != null)
        {
            i++;
        }
        return i;
    }

    public void setFirst(ProgressQuest first)
    {
        this.first = first;
    }

    public ProgressQuest getQuest(int i)
    {
        return switch(i)
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

    public void setSecond(ProgressQuest second)
    {
        this.second = second;
    }
    public ProgressQuest getSecond()
    {
        return second;
    }

    public void setThird(ProgressQuest third)
    {
        this.third = third;
    }
    public ProgressQuest getThird()
    {
        return third;
    }

    public void setFourth(ProgressQuest fourth)
    {
        this.fourth = fourth;
    }
    public ProgressQuest getFourth()
    {
        return fourth;
    }


    public UUID getUuid() {
        return uuid;
    }
    public boolean isQuestFinished(int i )
    {
        return getQuest(i).getProgress() == -1;
    }

    public boolean hasFinished()
    {
        return first.getProgress() == -1 && second.getProgress() == -1 && third.getProgress() == -1 && fourth.getProgress() == -1;
    }

    public boolean isClaimed() {
        return isClaimed;
    }

    public void setClaimed(boolean claimed) {
        isClaimed = claimed;
    }
}
