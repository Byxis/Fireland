package fr.byxis.player;

import fr.byxis.fireland.Fireland;
import fr.byxis.player.items.itemEnabler;
import fr.byxis.player.items.toxic.infectedPlayer;
import fr.byxis.player.level.LevelStorage;
import fr.byxis.player.primes.PrimeEvent;
import fr.byxis.player.primes.PrimesConfig;
import fr.byxis.player.pvpmanager.PvPManager;
import fr.byxis.player.quest.QuestManager;
import fr.byxis.player.rank.RankCustomMessage;

public class PlayerAddonsEnabler {

    private Fireland main;
    public itemEnabler item;
    public QuestManager questManager;
    public LevelStorage levelStorage;

    public PlayerAddonsEnabler(Fireland main)
    {
        this.main = main;
        item = new itemEnabler(main);
        questManager = new QuestManager(main);
        levelStorage = new LevelStorage(main);
        RankCustomMessage rankCustomMessage = new RankCustomMessage(main);
        main.getServer().getPluginManager().registerEvents(new PrimeEvent(main), main);
        main.getServer().getPluginManager().registerEvents(rankCustomMessage, main);
        main.getCommand("rank").setExecutor(rankCustomMessage);
        main.getCommand("rank").setTabCompleter(rankCustomMessage);
        main.getServer().getPluginManager().registerEvents(new PvPManager(main), main);
    }
}
