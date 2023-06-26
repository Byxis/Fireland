package fr.byxis.player.quest.questclass;

import io.lumine.mythic.api.mobs.MythicMob;
import org.bukkit.entity.Entity;

public class KillQuestClass extends QuestClass{
    private String killed;
    private int amount;

    public KillQuestClass(int questId, String title, String description, double reward, double jetons, String objective, String killed, int amount) {
        super(questId, title, description, reward, jetons, objective);
        this.killed = killed;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public String getKilled() {
        return killed;
    }
}
