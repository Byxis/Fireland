package fr.byxis.fireland;

import fr.byxis.event.SaveEvent;
import fr.byxis.faction.essaim.EssaimFunctions;
import fr.byxis.player.primes.PrimeEvent;
import fr.byxis.player.quest.QuestManager;
import org.bukkit.event.EventHandler;

public class Save {

    public static void SaveAll(Fireland main)
    {
        QuestManager.saveProgress();
        SaveEvent.SaveAllPlayerDatas();
        EssaimFunctions.SaveEssaim();
        PrimeEvent.SavePrime();

    }

    public static void ReloadAll(Fireland main)
    {
        if(QuestManager.config != null)
        {
            QuestManager.config.reload();
        }
        if(EssaimFunctions.configManager != null)
        {
            EssaimFunctions.configManager.reload();
        }
        if(PrimeEvent.config != null)
        {
            PrimeEvent.config.reload();
        }
    }
}
