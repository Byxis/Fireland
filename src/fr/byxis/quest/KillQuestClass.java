package fr.byxis.quest;

import org.bukkit.entity.Entity;

public class KillQuestClass extends QuestClass{
    private Entity killed;
    private int amount;

    public KillQuestClass(int questId, String title, String description, double reward, Entity killed, int amount) {
        super(questId, title, description, reward);
        this.killed = killed;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public Entity getKilled() {
        return killed;
    }
}
