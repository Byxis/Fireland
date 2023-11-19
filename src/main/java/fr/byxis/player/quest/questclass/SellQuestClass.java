package fr.byxis.player.quest.questclass;

public class SellQuestClass extends QuestClass{
    private int amount;

    public SellQuestClass(int questId, String title, String description, double reward, double jetons, String objective, int amount) {
        super(questId, title, description, reward, jetons, objective);
        this.amount = amount;
    }


    public int getAmount() {
        return amount;
    }
}
