package fr.byxis.faction.faction;

import fr.byxis.faction.faction.events.FactionBuyPerkEvent;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.PermissionUtilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FactionEvent implements Listener {

    private final Fireland main;
    private final FactionFunctions ff;
    public FactionEvent(Fireland _main) {
        this.main = _main;
        ff = new FactionFunctions(_main, null);
        if (Bukkit.getOnlinePlayers().isEmpty())
            return;
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e)
    {
        ff.setSender(e.getPlayer());
        String fname = ff.playerFactionName(e.getPlayer());
        FactionInformation infos = ff.getFactionInfo(fname);
        if (!fname.equals(""))
        {
            if (infos.hasFriendlyFirePerk())
            {
                main.getHashMapManager().addFactionMap(e.getPlayer().getUniqueId(), fname);
            }

            if (infos.hasSkinPerk())
            {
                PermissionUtilities.addPermission(e.getPlayer(), "csp.skin.Faction");
            }
            if (infos.doShowPrefix())
            {
                main.getHashMapManager().addFactionPrefixMap(e.getPlayer().getUniqueId(), infos.getColorcode() + infos.getName() + " > §r");
            }
        }
    }

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        if (main.getHashMapManager().getFactionMap().containsKey(p.getUniqueId()))
        {
            main.getHashMapManager().removeFactionMap(p.getUniqueId());
        }
        if (main.getHashMapManager().getFactionPrefixMap().containsKey(p.getUniqueId()))
        {
            main.getHashMapManager().removeFactionMap(p.getUniqueId());
        }
    }

    @EventHandler
    public void playerChat(PlayerChatEvent e)
    {
        if (main.getHashMapManager().getFactionPrefixMap().containsKey(e.getPlayer().getUniqueId()))
        {
            e.setFormat(main.getHashMapManager().getFactionPrefixMap().get(e.getPlayer().getUniqueId()) + e.getFormat());
        }
    }

    @EventHandler
    public void factionBuyPerk(FactionBuyPerkEvent e)
    {
        if (e.getPerk().equalsIgnoreCase("friendly_fire"))
        {
            for (Player p : Bukkit.getOnlinePlayers())
            {
                String name = ff.playerFactionName(p);
                if (name.equalsIgnoreCase(e.getFaction()))
                {
                    main.getHashMapManager().addFactionMap(p.getUniqueId(), e.getFaction());
                }
            }
        }
    }

    /*@EventHandler
    public void PlayerInteraction(InventoryClickEvent e)
    {
        if (e.getView().getTitle().contains("Stockage de la faction"))
        {
            Player p = (Player) e.getView().getPlayer();
            FactionFunctions ff = new FactionFunctions(main, p);
            String name = ff.playerFactionName(p);
            main.getHashMapManager().replaceStorageFactionMap(name, e.getInventory());
        }
    }

    @EventHandler
    public void PlayerCloseInv(InventoryCloseEvent e)
    {
        if (e.getView().getTitle().contains("Stockage de la faction"))
        {
            Player p = (Player) e.getView().getPlayer();
            FactionFunctions ff = new FactionFunctions(main, p);
            String name = ff.playerFactionName(p);
            main.getHashMapManager().replaceStorageFactionMap(name, e.getInventory());
        }
    }*/
}
