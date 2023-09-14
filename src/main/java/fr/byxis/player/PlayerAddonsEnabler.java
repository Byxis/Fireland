package fr.byxis.player;

import fr.byxis.fireland.Fireland;
import fr.byxis.player.items.itemEnabler;
import fr.byxis.player.items.toxic.infectedPlayer;
import fr.byxis.player.primes.PrimeEvent;
import fr.byxis.player.primes.PrimesConfig;
import fr.byxis.player.pvpmanager.PvPManager;
import fr.byxis.player.quest.QuestManager;
import fr.byxis.player.rank.RankCustomMessage;

public class PlayerAddonsEnabler {

    private Fireland main;
    public itemEnabler item;
    public QuestManager questManager;

    public PlayerAddonsEnabler(Fireland main)
    {
        this.main = main;
        item = new itemEnabler(main);
        questManager = new QuestManager(main);
        main.getServer().getPluginManager().registerEvents(new PrimeEvent(main), main);
        main.getServer().getPluginManager().registerEvents(new RankCustomMessage(main), main);
        main.getCommand("rank").setExecutor(new RankCustomMessage(main));
        main.getCommand("rank").setTabCompleter(new RankCustomMessage(main));
        main.getServer().getPluginManager().registerEvents(new PvPManager(main), main);
    }
}
