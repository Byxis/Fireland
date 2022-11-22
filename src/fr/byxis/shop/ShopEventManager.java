package fr.byxis.shop;

import fr.byxis.main.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ShopEventManager implements Listener {

    private final Main main;
    public ShopEventManager(Main main) {
        this.main=main;
    }

    @EventHandler
    public void clicEventOnInv(InventoryClickEvent e)
    {
        if(e.getView().getTitle().contains("Marchand"))
        {
            e.setCancelled(true);
            ItemStack itemclicked = e.getCurrentItem();
            if (itemclicked == null) {
                return;
            }
            Player p = (Player) e.getView().getPlayer();
            ShopFunction sf = new ShopFunction(main, p);

            if(itemclicked.getType() == Material.RED_STAINED_GLASS_PANE || itemclicked.getType() == Material.LIME_STAINED_GLASS_PANE)
            {
                int next = sf.getItemPage(itemclicked);
                int max = sf.getInvPageMax(e.getView());
                if(next != (max+1))
                {
                    sf.openInv(p, sf.getShopName(e.getView()), next);
                }
            }
            else if(itemclicked.getType() != Material.WHITE_STAINED_GLASS_PANE ||itemclicked.getType() != Material.BOOK)
            {
                if(e.isLeftClick())
                {
                    sf.buyItem(itemclicked, p, sf.getShopName(e.getView()));
                }
                else if(e.isRightClick())
                {
                    sf.sellItem(itemclicked, p, sf.getShopName(e.getView()).replaceAll(" ", "_"), e.isShiftClick());
                }
            }

        }
    }
}
