package fr.byxis.fireland;

import fr.byxis.event.SaveEvent;
import fr.byxis.faction.essaim.EssaimFunctions;
import fr.byxis.player.bank.Bank;
import fr.byxis.player.bank.BankStorage;
import fr.byxis.player.primes.PrimeEvent;
import fr.byxis.player.quest.QuestManager;

public class Save {

    public static void saveAll(Fireland main)
    {
        QuestManager.saveProgress();
        SaveEvent.saveAllPlayerDatas();
        EssaimFunctions.saveEssaim();
        PrimeEvent.savePrime();
        Bank.saveAllBankStorage();

    }

    public static void reloadAll(Fireland main)
    {
        if (QuestManager.getConfig() != null)
        {
            QuestManager.getConfig().reload();
        }
        if (EssaimFunctions.getConfigManager() != null)
        {
            EssaimFunctions.getConfigManager().reload();
        }
        if (PrimeEvent.getConfig() != null)
        {
            PrimeEvent.getConfig().reload();
        }
    }
}
