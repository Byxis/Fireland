package fr.byxis.player.quest.questclass;

import org.bukkit.Material;

public class InteractQuestClass extends QuestClass
{
    private final Material mat;
    private final int amount;

    public InteractQuestClass(int questId, String title, String description, double reward, double jetons, String objective, Material _mat,
            int _amount)
    {
        super(questId, title, description, reward, jetons, objective);
        this.mat = _mat;
        this.amount = _amount;
    }

    public Material getMaterial()
    {
        return mat;
    }

    public int getAmount()
    {
        return amount;
    }
}
