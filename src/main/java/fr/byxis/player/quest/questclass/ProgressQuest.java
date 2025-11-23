package fr.byxis.player.quest.questclass;

import static fr.byxis.fireland.Fireland.getEco;
import static fr.byxis.fireland.utilities.InGameUtilities.sendPlayerSucces;
import static fr.byxis.player.level.LevelStorage.addPlayerXp;

import fr.byxis.jeton.JetonManager;
import fr.byxis.player.level.LevelStorage;
import fr.byxis.player.quest.QuestManager;
import org.bukkit.entity.Player;

public class ProgressQuest
{

    private final int id;
    private int progress;

    public ProgressQuest(int _id, int _progress)
    {
        this.id = _id;
        this.progress = _progress;
    }

    public int getId()
    {
        return id;
    }

    public int getProgress()
    {
        return progress;
    }

    public void addProgress(int amount)
    {
        this.progress += amount;
    }
    public void finish(Player p)
    {
        this.progress = -1;
        addPlayerXp(p.getUniqueId(), 50, LevelStorage.Nation.Null);
        QuestClass quest = QuestManager.getAvailableQuests().get(this.id);
        sendPlayerSucces(p, "Vous avez fini une quête quotidienne. Vous avez reçu §6" + (int) quest.getReward() + "$§a et §b"
                + (int) quest.getJetons() + "§a jetons.");
        JetonManager.addJetonsPlayer(p.getUniqueId(), (int) quest.getJetons());
        getEco().depositPlayer(p, quest.getReward());

    }

}
