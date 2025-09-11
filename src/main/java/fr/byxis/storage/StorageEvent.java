package fr.byxis.storage;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.player.bank.Bank;
import fr.byxis.player.bank.BankStorage;
import org.bukkit.ChatColor;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StorageEvent implements Listener {

    private final Fireland main;
    // Track which page each player is currently viewing
    private final Map<UUID, Integer> playerCurrentPage = new HashMap<>();

    public StorageEvent(Fireland main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        String inventoryTitle = e.getView().getTitle();

        // Vérifier si c'est un inventaire de stockage (toutes les pages)
        if (inventoryTitle.contains("Stockage de la banque") ||
                (inventoryTitle.contains("Stockage") && (inventoryTitle.contains("(") || inventoryTitle.contains("/")))) {

            ItemStack itemClicked = e.getCurrentItem();
            if (itemClicked == null) {
                return;
            }

            int clickedSlot = e.getSlot();

            // Handle navigation items (slots 45-53)
            if (clickedSlot >= 45) {
                e.setCancelled(true);
                handleNavigationClick(player, itemClicked, clickedSlot, inventoryTitle);
                return;
            }

            // Handle regular storage interactions (slots 0-44)
            switch (itemClicked.getType()) {
                case WHITE_STAINED_GLASS_PANE:
                    // Prevent interaction with placeholder items
                    e.setCancelled(true);
                    break;
                case BARRIER:
                    // Prevent interaction with barrier blocks
                    e.setCancelled(true);
                    InGameUtilities.playPlayerSound(player, Sound.ITEM_SHIELD_BREAK, SoundCategory.AMBIENT, 1, 0);
                    break;
                default:
                    // Allow normal item interactions in storage area
                    break;
            }
        }
    }

    /**
     * Handle clicks on navigation items
     */
    private void handleNavigationClick(Player player, ItemStack clickedItem, int slot, String inventoryTitle) {
        AbstractStorage storage = getPlayerStorage(player);
        if (storage == null) return;

        int currentPage = storage.getCurrentPage(inventoryTitle);
        int totalPages = storage.getTotalPages();

        switch (slot) {
            case 48: // Previous page button
                if (clickedItem.getType() == Material.ARROW && currentPage > 0) {
                    saveCurrentPageAndOpen(player, storage, currentPage - 1);
                    InGameUtilities.playPlayerSound(player, Sound.UI_BUTTON_CLICK, SoundCategory.AMBIENT, 1, 1.2f);
                } else {
                    InGameUtilities.playPlayerSound(player, Sound.ITEM_SHIELD_BREAK, SoundCategory.AMBIENT, 1, 0.8f);
                }
                break;

            case 50: // Next page button
                if (clickedItem.getType() == Material.ARROW && currentPage < totalPages - 1) {
                    saveCurrentPageAndOpen(player, storage, currentPage + 1);
                    InGameUtilities.playPlayerSound(player, Sound.UI_BUTTON_CLICK, SoundCategory.AMBIENT, 1, 1.2f);
                } else {
                    InGameUtilities.playPlayerSound(player, Sound.ITEM_SHIELD_BREAK, SoundCategory.AMBIENT, 1, 0.8f);
                }
                break;

            case 49: // Page info (optional: quick jump to page 1)
                if (clickedItem.getType() == Material.BOOK && currentPage != 0) {
                    saveCurrentPageAndOpen(player, storage, 0);
                    InGameUtilities.playPlayerSound(player, Sound.UI_BUTTON_CLICK, SoundCategory.AMBIENT, 1, 1.0f);
                }
                break;

            case 53: // Close button
                if (clickedItem.getType() == Material.RED_STAINED_GLASS_PANE) {
                    player.closeInventory();
                    InGameUtilities.playPlayerSound(player, Sound.UI_BUTTON_CLICK, SoundCategory.AMBIENT, 1, 0.8f);
                }
                break;

            default:
                // Other navigation slots - prevent interaction
                InGameUtilities.playPlayerSound(player, Sound.ITEM_SHIELD_BREAK, SoundCategory.AMBIENT, 1, 0.6f);
                break;
        }
    }

    /**
     * Save current page and open a new one
     */
    private void saveCurrentPageAndOpen(Player player, AbstractStorage storage, int newPage) {
        String currentTitle = player.getOpenInventory().getTitle();
        int currentPage = storage.getCurrentPage(currentTitle);

        // Save current page data
        storage.saveAndClosePage(currentPage);

        // Track player's current page
        playerCurrentPage.put(player.getUniqueId(), newPage);

        // Open new page
        storage.openStoragePage(player, newPage);
    }

    /**
     * Get the storage instance for a player
     */
    private AbstractStorage getPlayerStorage(Player player) {
        String inventoryTitle = player.getOpenInventory().getTitle();

        if (inventoryTitle.contains("Stockage de la banque") || inventoryTitle.contains("banque")) {
            // Utiliser la méthode statique de Bank pour obtenir le storage
            return Bank.getBankStorage(player.getUniqueId(), main);
        }

        // Add other storage types here as needed
        return null;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        String inventoryTitle = e.getView().getTitle();

        // Gérer uniquement les inventaires de stockage paginé
        if (inventoryTitle.contains("Stockage de la banque") ||
                (inventoryTitle.contains("Stockage") && (inventoryTitle.contains("(") || inventoryTitle.contains("/")))) {

            AbstractStorage storage = getPlayerStorage(player);
            if (storage != null) {
                // Save the current page when closing
                int currentPage = storage.getCurrentPage(inventoryTitle);
                storage.saveAndClosePage(currentPage);

                // Remove player from tracking
                playerCurrentPage.remove(player.getUniqueId());

                // Play close sound
                InGameUtilities.playPlayerSound(player, "entity.villager.yes", SoundCategory.AMBIENT, 1, 1);
            }
        }
    }

    /**
     * Get the current page a player is viewing
     */
    public int getPlayerCurrentPage(Player player) {
        return playerCurrentPage.getOrDefault(player.getUniqueId(), 0);
    }

    /**
     * Set the current page a player is viewing
     */
    public void setPlayerCurrentPage(Player player, int page) {
        playerCurrentPage.put(player.getUniqueId(), page);
    }

    /**
     * Clean up tracking when player leaves
     */
    public void cleanupPlayer(Player player) {
        playerCurrentPage.remove(player.getUniqueId());
    }
}