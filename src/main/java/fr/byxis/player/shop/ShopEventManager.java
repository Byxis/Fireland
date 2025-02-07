package fr.byxis.player.shop;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InventoryUtilities;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ShopEventManager implements Listener {

    private final Fireland main;
    public ShopEventManager(Fireland main) {
        this.main = main;
    }

    @EventHandler
    public void clicEventOnInv(InventoryClickEvent e)
    {
        if (e.getView().getTitle().contains("Marchand"))
        {
            Player p = (Player) e.getView().getPlayer();
            InventoryUtilities.clickManager(e);

            ItemStack itemclicked = e.getCurrentItem();
            if (itemclicked == null) {
                return;
            }
            ShopFunction sf = new ShopFunction(main, p);
            if (itemclicked.getType() == Material.RED_STAINED_GLASS_PANE || itemclicked.getType() == Material.LIME_STAINED_GLASS_PANE)
            {
                e.setCancelled(true);
                int next = sf.getItemPage(itemclicked);
                int max = sf.getInvPageMax(e.getView());
                if (next != (max + 1))
                {
                    sf.openInv(p, sf.getShopName(e.getView()), next);
                }
            }
            else if (itemclicked.getType() != Material.WHITE_STAINED_GLASS_PANE || itemclicked.getType() != Material.BOOK)
            {
                if (e.getClickedInventory() == e.getView().getTopInventory()) {
                    if (e.isLeftClick())
                    {
                        sf.buyItem(itemclicked, p, sf.getShopName(e.getView()).replaceAll(" ", "_"), e.getView().getTitle().contains("skin"));
                    }
                    else if (e.isRightClick())
                    {
                        if (!e.getView().getTitle().contains("skin"))
                        {
                            sf.sellItem(itemclicked, p, sf.getShopName(e.getView()).replaceAll(" ", "_"), e.isShiftClick());
                        }

                    }
                    e.setResult(Event.Result.DENY);
                }
                if (e.isShiftClick() || e.getClick().isKeyboardClick())
                {
                    e.setResult(Event.Result.DENY);

                }
            }
            p.updateInventory();
        }
    }
}
