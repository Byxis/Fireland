package fr.byxis.player.quest.questclass;

public class EnterRegionQuestClass extends QuestClass {
    private final String region;

    public EnterRegionQuestClass(int questId, String title, String description, double reward, double jetons, String objective, String _region) {
        super(questId, title, description, reward, jetons, objective);
        this.region = _region;
    }

    public String getRegion() {
        return region;
    }
}
