package fr.byxis.faction.bunker;

import fr.byxis.fireland.Fireland;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class BunkerManager {

    private static Fireland m_main;
    private final HashMap<String, BunkerClass> m_loadedBunker;
    private final HashMap<Material, String[]> m_skinList;

    public BunkerManager(Fireland _main)
    {
        if (m_main == null)
        {
            BunkerManager.m_main = _main;
        }
        m_loadedBunker = new HashMap<>();
        m_skinList = new HashMap<>();
        m_skinList.put(Material.STONE, new String[]{"§7Par défaut", "default"});
        m_skinList.put(Material.JUNGLE_LOG, new String[]{"§2Tropical", "tropical"});
        m_skinList.put(Material.SEA_LANTERN, new String[]{"§9Aquatique", "aquatique"});
        m_skinList.put(Material.GOLD_BLOCK, new String[]{"§6Luxueux", "luxueux"});
        m_skinList.put(Material.MOSSY_STONE_BRICKS, new String[]{"§aCatacombes", "catacombes"});
        m_skinList.put(Material.SCULK_VEIN, new String[]{"§8Mycélien", "mycelien"});
        m_skinList.put(Material.MAGMA_BLOCK, new String[]{"§6Magmatique", "magmatique"});
        m_skinList.put(Material.TRAPPED_CHEST, new String[]{"§2Militaire", "militaire"});
        m_skinList.put(Material.SMOOTH_QUARTZ, new String[]{"§bFuturiste", "futuriste"});
        m_skinList.put(Material.DEEPSLATE, new String[]{"§7Caverne", "caverne"});
        m_skinList.put(Material.PACKED_ICE, new String[]{"§bGlacé", "glace"});

        m_main.getServer().getPluginManager().registerEvents(new BunkerEvent(m_main), m_main);
        m_main.getCommand("bunker").setExecutor(new BunkerCommand(m_main));

    }

    public BunkerClass findBunkerEnteredByPlayer(String player)
    {
        for (BunkerClass bunker : m_loadedBunker.values())
        {
            for (Player p : bunker.getPlayerInside())
            {
                if (p.getName().equals(player))
                {
                    return bunker;
                }
            }
        }
        return null;
    }

    public HashMap<String, BunkerClass> getLoadedBunker()
    {
        return m_loadedBunker;
    }

    public BunkerClass getBunker(String name)
    {
        loadBunker(name);
        return m_loadedBunker.get(name);
    }

    public void loadBunker(String name)
    {
        if (!m_loadedBunker.containsKey(name))
        {
            BunkerClass bunker = new BunkerClass(name, m_main);
            addLoadedBunker(bunker);
        }
    }

    public void addLoadedBunker(BunkerClass bk)
    {
        if (!m_loadedBunker.containsKey(bk.getName()))
            m_loadedBunker.put(bk.getName(), bk);
    }

    public HashMap<Material, String[]> getBunkerSkins()
    {
        return m_skinList;
    }
}
