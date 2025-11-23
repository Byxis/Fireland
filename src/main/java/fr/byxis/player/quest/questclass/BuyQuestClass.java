package fr.byxis.player.quest.questclass;

public class BuyQuestClass extends QuestClass
{
    private final int amount;

    public BuyQuestClass(int questId, String title, String description, double reward, double jetons, String objective, int _amount)
    {
        super(questId, title, description, reward, jetons, objective);
        this.amount = _amount;
    }

    public int getAmount()
    {
        return amount;
    }
}
