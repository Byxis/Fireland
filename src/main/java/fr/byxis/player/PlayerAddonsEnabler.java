package fr.byxis.player;

import fr.byxis.fireland.Fireland;
import fr.byxis.player.items.ItemEnabler;
import fr.byxis.player.level.LevelStorage;
import fr.byxis.player.primes.PrimeEvent;
import fr.byxis.player.pvpmanager.PvPManager;
import fr.byxis.player.quest.QuestManager;
import fr.byxis.player.rank.RankCustomMessage;
import fr.byxis.player.scoreboard.PlayerScoreboardManager;

public class PlayerAddonsEnabler {

    private final Fireland main;
    private final ItemEnabler item;
    private final QuestManager questManager;
    private final LevelStorage levelStorage;

    public PlayerAddonsEnabler(Fireland _main)
    {
        this.main = _main;
        item = new ItemEnabler(_main);
        questManager = new QuestManager(_main);
        levelStorage = new LevelStorage(_main);
        RankCustomMessage rankCustomMessage = new RankCustomMessage(_main);
        _main.getServer().getPluginManager().registerEvents(new PrimeEvent(_main), _main);
        _main.getServer().getPluginManager().registerEvents(rankCustomMessage, _main);
        _main.getCommand("rank").setExecutor(rankCustomMessage);
        _main.getCommand("rank").setTabCompleter(rankCustomMessage);
        _main.getServer().getPluginManager().registerEvents(new PvPManager(_main), _main);
        _main.getServer().getPluginManager().registerEvents(new PlayerScoreboardManager(_main, item.getInfectionManager()), _main);
    }

    public void SaveAll()
    {
        item.SaveAll();
        QuestManager.saveProgress();
        PrimeEvent.savePrime();
    }
}
