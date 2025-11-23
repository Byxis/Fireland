package fr.byxis.player.quest.questclass;

public class KillQuestClass extends QuestClass
{
    private final String killed;
    private final int amount;

    public KillQuestClass(int questId, String title, String description, double reward, double jetons, String objective, String _killed,
            int _amount)
    {
        super(questId, title, description, reward, jetons, objective);
        this.killed = _killed;
        this.amount = _amount;
    }

    public int getAmount()
    {
        return amount;
    }

    public String getKilled()
    {
        return killed;
    }
}
