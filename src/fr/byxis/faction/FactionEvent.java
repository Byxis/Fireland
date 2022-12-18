package fr.byxis.faction;

import fr.byxis.main.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FactionEvent implements Listener {

    private final Main main;
    public FactionEvent(Main main) {
        this.main = main;
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e)
    {
        FactionFunctions ff = new FactionFunctions(main, e.getPlayer());
        String fname = ff.playerFactionName(e.getPlayer());
        FactionInformation infos = ff.getFactionInfo(fname);
        if(!fname.equals(""))
        {
            if(infos.hasFriendlyFirePerk())
            {
                main.hashMapManager.addFactionMap(e.getPlayer().getUniqueId(), fname);
            }

            if(infos.hasSkinPerk())
            {
                main.addPermission(e.getPlayer(), "csp.skin.Faction");
            }
            if(infos.DoShowPrefix())
            {
                main.hashMapManager.addFactionPrefixMap(e.getPlayer().getUniqueId(), infos.getColorcode()+infos.getName()+" > §r");
            }
        }
    }

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        if(main.hashMapManager.getFactionMap().containsKey(p.getUniqueId()))
        {
            main.hashMapManager.removeFactionMap(p.getUniqueId());
        }
        if(main.hashMapManager.getFactionPrefixMap().containsKey(p.getUniqueId()))
        {
            main.hashMapManager.removeFactionMap(p.getUniqueId());
        }
    }

    @EventHandler
    public void PlayerChat(PlayerChatEvent e)
    {
        if(main.hashMapManager.getFactionPrefixMap().containsKey(e.getPlayer().getUniqueId()))
        {
            e.setFormat(main.hashMapManager.getFactionPrefixMap().get(e.getPlayer().getUniqueId())+e.getFormat());
        }
    }

    /*@EventHandler
    public void PlayerInteraction(InventoryClickEvent e)
    {
        if(e.getView().getTitle().contains("Stockage de la faction"))
        {
            Player p = (Player) e.getView().getPlayer();
            FactionFunctions ff = new FactionFunctions(main, p);
            String name = ff.playerFactionName(p);
            main.hashMapManager.replaceStorageFactionMap(name, e.getInventory());
        }
    }

    @EventHandler
    public void PlayerCloseInv(InventoryCloseEvent e)
    {
        if(e.getView().getTitle().contains("Stockage de la faction"))
        {
            Player p = (Player) e.getView().getPlayer();
            FactionFunctions ff = new FactionFunctions(main, p);
            String name = ff.playerFactionName(p);
            main.hashMapManager.replaceStorageFactionMap(name, e.getInventory());
        }
    }*/
}
