package fr.byxis.player.quest.questclass;

import org.bukkit.Location;

public class InteractSpecificQuestClass extends QuestClass{
    private Location loc;

    public InteractSpecificQuestClass(int questId, String title, String description, double reward, double jetons, String objective, Location loc) {
        super(questId, title, description, reward, jetons, objective);
        this.loc = loc;
    }

    public Location getLocation() {
        return loc;
    }
}
