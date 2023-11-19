package fr.byxis.player.quest.questclass;

public class CraftQuestClass extends QuestClass{
    private int amount;

    public CraftQuestClass(int questId, String title, String description, double reward, double jetons, String objective, int amount) {
        super(questId, title, description, reward, jetons, objective);
        this.amount = amount;
    }


    public int getAmount() {
        return amount;
    }
}
