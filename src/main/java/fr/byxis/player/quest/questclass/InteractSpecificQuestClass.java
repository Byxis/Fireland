package fr.byxis.player.quest.questclass;

import org.bukkit.Location;

public class InteractSpecificQuestClass extends QuestClass
{
    private final Location loc;

    public InteractSpecificQuestClass(int questId, String title, String description, double reward, double jetons, String objective,
            Location _loc)
    {
        super(questId, title, description, reward, jetons, objective);
        this.loc = _loc;
    }

    public Location getLocation()
    {
        return loc;
    }
}
