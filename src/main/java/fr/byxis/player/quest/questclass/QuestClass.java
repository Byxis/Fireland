package fr.byxis.player.quest.questclass;

public class QuestClass {

    private final int questId;
    private final String title;
    private final String desc;
    private final double reward;
    private final double jetons;
    private final String objective;

    public QuestClass(int _questId, String _title, String _description, double _reward, double _jetons, String _objective) {
        this.questId = _questId;
        this.title = _title;
        this.desc = _description;
        this.reward = _reward;
        this.jetons = _jetons;
        this.objective = _objective;
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
