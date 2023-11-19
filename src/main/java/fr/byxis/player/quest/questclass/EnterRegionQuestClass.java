package fr.byxis.player.quest.questclass;

public class EnterRegionQuestClass extends QuestClass{
    private String region;

    public EnterRegionQuestClass(int questId, String title, String description, double reward, double jetons, String objective, String region) {
        super(questId, title, description, reward, jetons, objective);
        this.region = region;
    }

    public String getRegion() {
        return region;
    }
}
