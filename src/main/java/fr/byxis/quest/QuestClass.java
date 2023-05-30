package fr.byxis.quest;

public class QuestClass {

    private int questId;
    private String title;
    private String desc;
    private double reward;

    public QuestClass(int questId, String title, String description, double reward) {
        this.questId = questId;
        this.title = title;
        this.desc = description;
        this.reward = reward;
    }
}
