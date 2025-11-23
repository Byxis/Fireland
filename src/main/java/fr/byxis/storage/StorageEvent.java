package fr.byxis.storage;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.player.bank.Bank;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class StorageEvent implements Listener
{

    private final Fireland m_fireland;
    private final Map<UUID, Integer> m_playerCurrentPage = new HashMap<>();

    public StorageEvent(Fireland _fireland)
    {
        this.m_fireland = _fireland;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent _e)
    {
        Player player = (Player) _e.getWhoClicked();
        String inventoryTitle = _e.getView().getTitle();

        if (inventoryTitle.contains("Stockage de la banque")
                || (inventoryTitle.contains("Stockage") && (inventoryTitle.contains("(") || inventoryTitle.contains("/"))))
        {

            ItemStack itemClicked = _e.getCurrentItem();
            if (itemClicked == null)
            {
                return;
            }

            int clickedSlot = _e.getSlot();

            if (clickedSlot >= 45)
            {
                _e.setCancelled(true);
                handleNavigationClick(player, itemClicked, clickedSlot, inventoryTitle);
                return;
            }

            switch (itemClicked.getType())
            {
                case WHITE_STAINED_GLASS_PANE :
                    _e.setCancelled(true);
                    break;
                case BARRIER :
                    _e.setCancelled(true);
                    InGameUtilities.playPlayerSound(player, Sound.ITEM_SHIELD_BREAK, SoundCategory.AMBIENT, 1, 0);
                    break;
                default :
                    break;
            }
        }
    }

    /**
     * Handle clicks on navigation items
     *
     * @param _player
     *            The player who clicked
     * @param _clickedItem
     *            The item that was clicked
     * @param _slot
     *            The slot that was clicked
     * @param _inventoryTitle
     *            The title of the inventory
     */
    private void handleNavigationClick(Player _player, ItemStack _clickedItem, int _slot, String _inventoryTitle)
    {
        AbstractStorage storage = getPlayerStorage(_player);
        if (storage == null)
            return;

        int currentPage = storage.getCurrentPage(_inventoryTitle);
        int totalPages = storage.getTotalPages();

        switch (_slot)
        {
            case 48 : // Previous page button
                if (_clickedItem.getType() == Material.ARROW && currentPage > 0)
                {
                    saveCurrentPageAndOpen(_player, storage, currentPage - 1);
                    InGameUtilities.playPlayerSound(_player, Sound.UI_BUTTON_CLICK, SoundCategory.AMBIENT, 1, 1.2f);
                }
                else
                {
                    InGameUtilities.playPlayerSound(_player, Sound.ITEM_SHIELD_BREAK, SoundCategory.AMBIENT, 1, 0.8f);
                }
                break;

            case 50 : // Next page button
                if (_clickedItem.getType() == Material.ARROW && currentPage < totalPages - 1)
                {
                    saveCurrentPageAndOpen(_player, storage, currentPage + 1);
                    InGameUtilities.playPlayerSound(_player, Sound.UI_BUTTON_CLICK, SoundCategory.AMBIENT, 1, 1.2f);
                }
                else
                {
                    InGameUtilities.playPlayerSound(_player, Sound.ITEM_SHIELD_BREAK, SoundCategory.AMBIENT, 1, 0.8f);
                }
                break;

            case 49 : // Page info (optional: quick jump to page 1)
                if (_clickedItem.getType() == Material.BOOK && currentPage != 0)
                {
                    saveCurrentPageAndOpen(_player, storage, 0);
                    InGameUtilities.playPlayerSound(_player, Sound.UI_BUTTON_CLICK, SoundCategory.AMBIENT, 1, 1.0f);
                }
                break;

            case 53 : // Close button
                if (_clickedItem.getType() == Material.RED_STAINED_GLASS_PANE)
                {
                    _player.closeInventory();
                    InGameUtilities.playPlayerSound(_player, Sound.UI_BUTTON_CLICK, SoundCategory.AMBIENT, 1, 0.8f);
                }
                break;

            default :
                // Other navigation slots - prevent interaction
                InGameUtilities.playPlayerSound(_player, Sound.ITEM_SHIELD_BREAK, SoundCategory.AMBIENT, 1, 0.6f);
                break;
        }
    }

    /**
     * Save curent page and open a new one
     *
     * @param _player
     *            The player
     * @param _storage
     *            The storage instance
     * @param _newPage
     *            The new page to open
     */
    private void saveCurrentPageAndOpen(Player _player, AbstractStorage _storage, int _newPage)
    {
        String currentTitle = _player.getOpenInventory().getTitle();
        int currentPage = _storage.getCurrentPage(currentTitle);

        _storage.saveAndClosePage(currentPage);
        m_playerCurrentPage.put(_player.getUniqueId(), _newPage);
        _storage.openStoragePage(_player, _newPage);
    }

    /**
     * Get the storage instance for a player
     *
     * @param _player
     *            The player
     *
     * @return The storage instance or null if none
     */
    private AbstractStorage getPlayerStorage(Player _player)
    {
        String inventoryTitle = _player.getOpenInventory().getTitle();

        if (inventoryTitle.contains("Stockage de la banque") || inventoryTitle.contains("banque"))
        {
            return Bank.getBankStorage(_player.getUniqueId(), m_fireland);
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent e)
    {
        Player player = (Player) e.getPlayer();
        String inventoryTitle = e.getView().getTitle();

        if (inventoryTitle.contains("Stockage de la banque")
                || (inventoryTitle.contains("Stockage") && (inventoryTitle.contains("(") || inventoryTitle.contains("/"))))
        {

            AbstractStorage storage = getPlayerStorage(player);
            if (storage != null)
            {
                int currentPage = storage.getCurrentPage(inventoryTitle);
                storage.saveAndClosePage(currentPage);

                m_playerCurrentPage.remove(player.getUniqueId());
                InGameUtilities.playPlayerSound(player, "entity.villager.yes", SoundCategory.AMBIENT, 1, 1);
            }
        }
    }

    /**
     * Get the current page a player is viewing
     *
     * @param _player
     *            The player
     *
     * @return The page number
     */
    public int getPlayerCurrentPage(Player _player)
    {
        return m_playerCurrentPage.getOrDefault(_player.getUniqueId(), 0);
    }

    /**
     * Set the current page a player is viewing
     *
     * @param _player
     *            The player
     * @param _page
     *            The page number
     */
    public void setPlayerCurrentPage(Player _player, int _page)
    {
        m_playerCurrentPage.put(_player.getUniqueId(), _page);
    }

    /**
     * Clean up tracking when player leaves
     *
     * @param _player
     *            The player
     */
    public void cleanupPlayer(Player _player)
    {
        m_playerCurrentPage.remove(_player.getUniqueId());
    }
}