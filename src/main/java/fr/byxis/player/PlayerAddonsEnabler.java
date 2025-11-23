package fr.byxis.player;

import fr.byxis.fireland.Fireland;
import fr.byxis.player.items.ItemEnabler;
import fr.byxis.player.level.LevelStorage;
import fr.byxis.player.primes.PrimeEvent;
import fr.byxis.player.protection.ProtectionManager;
import fr.byxis.player.pvpmanager.PvPManager;
import fr.byxis.player.quest.QuestManager;
import fr.byxis.player.rank.RankCustomMessage;
import fr.byxis.player.scoreboard.PlayerScoreboardManager;

public class PlayerAddonsEnabler
{

    private final Fireland m_fireland;
    private final ItemEnabler m_itemEnabler;
    private final QuestManager m_questManager;
    private final LevelStorage m_levelStorage;

    public PlayerAddonsEnabler(Fireland _fireland)
    {
        this.m_fireland = _fireland;
        m_itemEnabler = new ItemEnabler(_fireland);
        m_questManager = new QuestManager(_fireland);
        m_levelStorage = new LevelStorage(_fireland);
        RankCustomMessage rankCustomMessage = new RankCustomMessage(_fireland);
        _fireland.getServer().getPluginManager().registerEvents(new PrimeEvent(_fireland), _fireland);
        _fireland.getServer().getPluginManager().registerEvents(rankCustomMessage, _fireland);
        _fireland.getCommand("rank").setExecutor(rankCustomMessage);
        _fireland.getCommand("rank").setTabCompleter(rankCustomMessage);
        _fireland.getServer().getPluginManager().registerEvents(new PvPManager(_fireland), _fireland);
        _fireland.getServer().getPluginManager().registerEvents(new ProtectionManager(), _fireland);
        _fireland.getServer().getPluginManager().registerEvents(new PlayerScoreboardManager(_fireland, m_itemEnabler.getInfectionManager()),
                _fireland);
    }

    public void saveAll()
    {
        m_itemEnabler.saveAll();
        QuestManager.saveProgress();
        PrimeEvent.savePrime();
    }

    public ItemEnabler getItemEnabler()
    {
        return m_itemEnabler;
    }
}
