package fr.byxis.fireland;

import fr.byxis.event.SaveEvent;
import fr.byxis.player.bank.Bank;
import fr.byxis.player.primes.PrimeEvent;
import fr.byxis.player.quest.QuestManager;

public class Save
{

    public static void saveAll(Fireland main)
    {
        if (main.getPlayerAddonsEnabler() != null)
        {
            main.getPlayerAddonsEnabler().saveAll();
        }
        if (main.getEssaimManager() != null)
        {
            main.getEssaimManager().getConfigService().saveConfiguration();
        }

        SaveEvent.saveAllPlayerDatas();
        Bank.saveAllBankStorage();

    }

    public static void reloadAll(Fireland main)
    {
        if (QuestManager.getConfig() != null)
        {
            QuestManager.getConfig().reload();
        }
        if (main.getEssaimManager() != null)
        {
            main.getEssaimManager().getConfigService().reloadConfiguration();
        }
        if (PrimeEvent.getConfig() != null)
        {
            PrimeEvent.getConfig().reload();
        }
    }
}
