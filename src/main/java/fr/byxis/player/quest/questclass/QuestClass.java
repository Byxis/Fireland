package fr.byxis.player.quest.questclass;

public class QuestClass {

    private final int questId;
    private final String title;
    private final String desc;
    private final double reward;
    private final double jetons;
    private final String objective;

    public QuestClass(int questId, String title, String description, double reward, double jetons, String objective) {
        this.questId = questId;
        this.title = title;
        this.desc = description;
        this.reward = reward;
        this.jetons = jetons;
        this.objective = objective;
    }

    public double getReward() {
        return reward;
    }

    public String getDesc() {
        return desc;
    }

    public String getTitle() {
        return title;
    }

    public int getQuestId() {
        return questId;
    }

    public String getObjective() {
        return objective;
    }

    public double getJetons() {
        return jetons;
    }
}
