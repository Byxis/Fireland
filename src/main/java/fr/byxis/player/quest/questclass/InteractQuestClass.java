package fr.byxis.player.quest.questclass;

import org.bukkit.Material;

public class InteractQuestClass extends QuestClass{
    private Material mat;
    private int amount;

    public InteractQuestClass(int questId, String title, String description, double reward, double jetons, String objective, Material mat, int amount) {
        super(questId, title, description, reward, jetons, objective);
        this.mat = mat;
        this.amount = amount;
    }

    public Material getMaterial() {
        return mat;
    }

    public int getAmount() {
        return amount;
    }
}
