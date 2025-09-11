package fr.byxis.storage;

import fr.byxis.db.DbConnection;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.fireland.utilities.ItemSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractStorage {
    protected final String m_name;
    protected final Fireland m_main;
    protected final String m_tableName;
    protected final Map<Integer, Inventory> m_activePages;

    protected static final int SLOTS_PER_PAGE = 45;
    protected static final int NAV_ROW_START = 45;
    protected static final int PREV_PAGE_SLOT = 48;
    protected static final int PAGE_INFO_SLOT = 49;
    protected static final int NEXT_PAGE_SLOT = 50;
    protected static final int CLOSE_SLOT = 53;

    public AbstractStorage(Fireland main, String name, String tableName, int maxStorages) {
        m_main = main;
        m_name = name;
        m_tableName = tableName;
        m_activePages = new HashMap<>();
    }

    public abstract int getMaxSpaceStorage();
    public abstract int getStorageSize(int storageNumber);
    public abstract String getStorageTitle(int storageNumber);

    public int getTotalPages() {
        int totalSlots = getMaxSpaceStorage();
        return Math.max(1, (int) Math.ceil((double) totalSlots / SLOTS_PER_PAGE));
    }

    public int getPageSize(int pageNumber) {
        int totalSlots = getMaxSpaceStorage();
        int startIndex = pageNumber * SLOTS_PER_PAGE;
        if (startIndex >= totalSlots) return 0;
        return Math.min(SLOTS_PER_PAGE, totalSlots - startIndex);
    }

    public String getPageTitle(int pageNumber) {
        int totalPages = getTotalPages();
        if (totalPages <= 1) {
            return getStorageTitle(0);
        }
        return getStorageTitle(0) + " §7(" + (pageNumber + 1) + "/" + totalPages + ")";
    }

    private void setNavigationItems(Inventory inv, int currentPage) {
        int totalPages = getTotalPages();

        for (int i = NAV_ROW_START; i < NAV_ROW_START + 9; i++) {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.GRAY_STAINED_GLASS_PANE, " ", (short) 0));
        }

        if (currentPage > 0) {
            inv.setItem(PREV_PAGE_SLOT, InventoryUtilities.setItemMetaLore(
                    Material.ARROW,
                    "§a← Page précédente",
                    (short) 0,
                    List.of("§7Cliquez pour aller à la page " + currentPage)
            ));
        } else {
            inv.setItem(PREV_PAGE_SLOT, InventoryUtilities.setItemMeta(Material.BARRIER, "§c← Page précédente", (short) 0));
        }

        if (totalPages > 1) {
            inv.setItem(PAGE_INFO_SLOT, InventoryUtilities.setItemMetaLore(
                    Material.BOOK,
                    "§6Page " + (currentPage + 1) + "/" + totalPages,
                    (short) 0,
                    List.of(
                            "§7Vous consultez la page " + (currentPage + 1),
                            "§7Total d'emplacements: §e" + getMaxSpaceStorage(),
                            "§7Emplacements sur cette page: §e" + getPageSize(currentPage)
                    )
            ));
        }

        if (currentPage < totalPages - 1) {
            inv.setItem(NEXT_PAGE_SLOT, InventoryUtilities.setItemMetaLore(
                    Material.ARROW,
                    "§aPage suivante →",
                    (short) 0,
                    List.of("§7Cliquez pour aller à la page " + (currentPage + 2))
            ));
        } else {
            inv.setItem(NEXT_PAGE_SLOT, InventoryUtilities.setItemMeta(Material.BARRIER, "§cPage suivante →", (short) 0));
        }

        inv.setItem(CLOSE_SLOT, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cFermer", (short) 0));
    }

    public void openStoragePage(Player player, int pageNumber) {
        if (pageNumber < 0 || pageNumber >= getTotalPages()) {
            pageNumber = 0;
        }

        Inventory pageInventory = loadOrCreatePage(pageNumber);
        setNavigationItems(pageInventory, pageNumber);

        m_activePages.put(pageNumber, pageInventory);
        player.openInventory(pageInventory);
    }

    public void openStorage(Player player, int storageNumber) {
        openStoragePage(player, 0);
    }

    public int getCurrentPage(String inventoryTitle) {
        if (inventoryTitle.contains("(") && inventoryTitle.contains("/")) {
            try {
                String pageInfo = inventoryTitle.substring(
                        inventoryTitle.lastIndexOf("(") + 1,
                        inventoryTitle.lastIndexOf(")")
                );
                String currentPageStr = pageInfo.split("/")[0];
                return Integer.parseInt(currentPageStr) - 1;
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    private Inventory loadOrCreatePage(int pageNumber) {
        Inventory pageInventory = Bukkit.createInventory(null, 54, getPageTitle(pageNumber));

        int startIndex = pageNumber * SLOTS_PER_PAGE;
        int pageSize = getPageSize(pageNumber);

        for (int i = 0; i < pageSize; i++) {
            ItemStack item = loadItemFromDatabase(startIndex + i);
            pageInventory.setItem(i, item);
        }

        for (int i = pageSize; i < SLOTS_PER_PAGE; i++) {
            pageInventory.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 0));
        }

        return pageInventory;
    }

    public void saveAndClosePage(int pageNumber) {
        Inventory pageInventory = m_activePages.get(pageNumber);
        if (pageInventory == null) return;

        int startIndex = pageNumber * SLOTS_PER_PAGE;
        int pageSize = getPageSize(pageNumber);

        for (int i = 0; i < pageSize; i++) {
            ItemStack item = pageInventory.getItem(i);
            if (item != null && !isNavigationItem(item)) {
                saveItemToDatabase(startIndex + i, item);
            } else {
                removeItemFromDatabase(startIndex + i);
            }
        }

        m_activePages.remove(pageNumber);
    }

    private boolean isNavigationItem(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        return type == Material.GRAY_STAINED_GLASS_PANE ||
                type == Material.WHITE_STAINED_GLASS_PANE ||
                type == Material.RED_STAINED_GLASS_PANE ||
                type == Material.ARROW ||
                type == Material.BARRIER ||
                type == Material.BOOK;
    }

    private ItemStack loadItemFromDatabase(int index) {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT item FROM " + m_tableName + " WHERE owner = ? AND index_item = ?"
            );
            preparedStatement.setString(1, m_name);
            preparedStatement.setInt(2, index);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return ItemSerializer.deserialize(m_main, rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveItemToDatabase(int index, ItemStack item) {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement checkStatement = connection.prepareStatement(
                    "SELECT 1 FROM " + m_tableName + " WHERE owner = ? AND index_item = ?"
            );
            checkStatement.setString(1, m_name);
            checkStatement.setInt(2, index);

            boolean exists = checkStatement.executeQuery().next();

            if (exists) {
                final PreparedStatement updateStatement = connection.prepareStatement(
                        "UPDATE " + m_tableName + " SET item = ? WHERE owner = ? AND index_item = ?"
                );
                updateStatement.setString(1, ItemSerializer.serialize(item));
                updateStatement.setString(2, m_name);
                updateStatement.setInt(3, index);
                updateStatement.executeUpdate();
            } else {
                final PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO " + m_tableName + "(owner, index_item, item) VALUES(?, ?, ?)"
                );
                insertStatement.setString(1, m_name);
                insertStatement.setInt(2, index);
                insertStatement.setString(3, ItemSerializer.serialize(item));
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeItemFromDatabase(int index) {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM " + m_tableName + " WHERE owner = ? AND index_item = ?"
            );
            preparedStatement.setString(1, m_name);
            preparedStatement.setInt(2, index);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveAllItems() {
        for (Map.Entry<Integer, Inventory> entry : m_activePages.entrySet()) {
            saveAndClosePage(entry.getKey());
        }
    }

    public void actualiseItems() {
        saveAllItems();
    }
}