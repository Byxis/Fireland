package fr.byxis.player.quest.questclass;

import fr.byxis.fireland.Fireland;
import fr.byxis.jeton.JetonManager;
import fr.byxis.player.level.LevelStorage;
import fr.byxis.player.quest.QuestManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static fr.byxis.fireland.Fireland.eco;
import static fr.byxis.fireland.utilities.InGameUtilities.sendPlayerSucces;
import static fr.byxis.player.level.LevelStorage.addPlayerXp;

public class ProgressQuest {

    private int id;
    private int progress;

    public ProgressQuest(int id, int progress)
    {
        this.id = id;
        this.progress = progress;
    }

    public int getId() {
        return id;
    }

    public int getProgress() {
        return progress;
    }

    public void addProgress(int amount) {
        this.progress += amount;
    }
    public void finish(Player p)
    {
        this.progress = -1;
        addPlayerXp(p.getUniqueId(), 50, LevelStorage.Nation.Null);
        QuestClass quest = QuestManager.getAvailableQuests().get(this.id);
        sendPlayerSucces(p, "Vous avez fini une quête quotidienne. Vous avez reçu §6"+(int)quest.getReward()+"$§a et §b"+(int)quest.getJetons()+"§a jetons.");
        JetonManager.addJetonsPlayer(p.getUniqueId(), (int) quest.getJetons());
        eco.depositPlayer(p, quest.getReward());

    }

}
