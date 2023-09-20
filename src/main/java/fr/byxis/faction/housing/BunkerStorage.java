package fr.byxis.faction.housing;

import fr.byxis.db.DbConnection;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.fireland.utilities.ItemSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BunkerStorage {

    HashMap<Integer, ItemStack> m_items;

    private String m_name;

    private Inventory m_storage0;
    private Inventory m_storage1;
    private Inventory m_storage2;
    private Inventory m_storage3;
    private Inventory m_storage4;
    private Inventory m_storage5;
    private Inventory m_storage6;
    private Inventory m_storage7;
    private Inventory m_storage8;
    private Inventory m_storage9;

    private final Fireland m_main;

    public BunkerStorage(Fireland _main, String _name) {
        m_main = _main;
        m_name = _name;
        m_items = new HashMap<Integer, ItemStack>();
        LoadAllItems();
    }

    public void UpdateItemStorage(int index, ItemStack item)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();

        try {
            //On prépare la requête SQL
            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement2 = connection.prepareStatement("UPDATE faction_storage SET item = ? WHERE faction = ? AND index_item = ?");
            preparedStatement2.setString(2, m_name);
            preparedStatement2.setInt(3, index);
            preparedStatement2.setString(1, ItemSerializer.serialize(item));
            //On exécute la requete SQL
            preparedStatement2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void SetItemStorage(int index, ItemStack item)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();

        try {
            //On prépare la requête SQL
            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement2 = connection.prepareStatement("INSERT INTO faction_storage(faction,index_item,item) VALUES(?,?,?)");
            preparedStatement2.setString(1, m_name);
            preparedStatement2.setInt(2, index);
            preparedStatement2.setString(3, ItemSerializer.serialize(item));
            //On exécute la requete SQL
            preparedStatement2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean IndexStorageUsed(int index)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();

        try {
            //On prépare la requête SQL
            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT index_item FROM faction_storage WHERE faction = ? AND index_item = ?");
            preparedStatement2.setString(1, m_name);
            preparedStatement2.setInt(2, index);
            //On exécute la requete SQL
            ResultSet rs = preparedStatement2.executeQuery();
            if(rs.next())
            {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void RemoveItemStorage(int index)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();

        try {
            //On prépare la requête SQL
            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement2 = connection.prepareStatement("DELETE FROM faction_storage WHERE faction_storage.faction = ? AND faction_storage.index_item = ?;");
            preparedStatement2.setString(1, m_name);
            preparedStatement2.setInt(2, index);
            //On exécute la requete SQL
            preparedStatement2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void SaveItemFactionStorage(int index, ItemStack item)
    {
        if(item != null)
        {
            if(IndexStorageUsed(index))
            {
                UpdateItemStorage(index, item);
            }
            else
            {
                SetItemStorage(index, item);
            }
        }
        else
        {
            RemoveItemStorage(index);
        }
    }

    public void SaveAllItems()
    {
        ActualiseItems();
        for(int i = 0; i < 540; i++)
        {
            SaveItemFactionStorage(i, m_items.get(i));
        }
    }

    public void ActualiseItems()
    {
        int number = 0;
        if(m_storage0 != null)
        {
            for(int i = 0; i < 27; i++)
            {
                if(m_storage0.getItem(i) != null)
                {
                    m_items.put(i+27*number, m_storage0.getItem(i));
                }
                else
                {
                    m_items.remove(i+27*number);
                }
            }
        }
        number++;
        if(m_storage1 != null)
        {
            for(int i = 0; i < 27; i++)
            {
                if(m_storage1.getItem(i) != null)
                {
                    m_items.put(i+27*number, m_storage1.getItem(i));
                }
                else
                {
                    m_items.remove(i+27*number);
                }
            }
        }
        number++;
        if(m_storage2 != null)
        {
            for(int i = 0; i < 27; i++)
            {
                if(m_storage2.getItem(i) != null)
                {
                    m_items.put(i+27*number, m_storage2.getItem(i));
                }
                else
                {
                    m_items.remove(i+27*number);
                }
            }
        }
        number++;
        if(m_storage3 != null)
        {
            for(int i = 0; i < 27; i++)
            {
                if(m_storage3.getItem(i) != null)
                {
                    m_items.put(i+27*number, m_storage3.getItem(i));
                }
                else
                {
                    m_items.remove(i+27*number);
                }
            }
        }
        number++;
        if(m_storage4 != null)
        {
            for(int i = 0; i < 27; i++)
            {
                if(m_storage4.getItem(i) != null)
                {
                    m_items.put(i+27*number, m_storage4.getItem(i));
                }
                else
                {
                    m_items.remove(i+27*number);
                }
            }
        }
        number++;
        if(m_storage5 != null)
        {
            for(int i = 0; i < 27; i++)
            {
                if(m_storage5.getItem(i) != null)
                {
                    m_items.put(i+27*number, m_storage5.getItem(i));
                }
                else
                {
                    m_items.remove(i+27*number);
                }
            }
        }
        number++;
        if(m_storage6 != null)
        {
            for(int i = 0; i < 27; i++)
            {
                if(m_storage6.getItem(i) != null)
                {
                    m_items.put(i+27*number, m_storage6.getItem(i));
                }
                else
                {
                    m_items.remove(i+27*number);
                }
            }
        }
        number++;
        if(m_storage7 != null)
        {
            for(int i = 0; i < 27; i++)
            {
                if(m_storage7.getItem(i) != null)
                {
                    m_items.put(i+27*number, m_storage7.getItem(i));
                }
                else
                {
                    m_items.remove(i+27*number);
                }
            }
        }
        number++;
        if(m_storage8 != null)
        {
            for(int i = 0; i < 27; i++)
            {
                if(m_storage8.getItem(i) != null)
                {
                    m_items.put(i+27*number, m_storage8.getItem(i));
                }
                else
                {
                    m_items.remove(i+27*number);
                }
            }
        }
        number++;
        if(m_storage9 != null)
        {
            for(int i = 0; i < 27; i++)
            {
                if(m_storage9.getItem(i) != null)
                {
                    m_items.put(i+27*number, m_storage9.getItem(i));
                }
                else
                {
                    m_items.remove(i+27*number);
                }
            }
        }

    }

    public void LoadAllItems()
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            //On prépare la requête SQL
            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT index_item, item FROM faction_storage WHERE faction = ?");
            preparedStatement2.setString(1, m_name);
            //On exécute la requete SQL
            ResultSet rs = preparedStatement2.executeQuery();
            while(rs.next())
            {
                m_items.put(rs.getInt(1), ItemSerializer.deserialize(rs.getString(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ItemStack> GetItemsOfStorageNumber(int _number)
    {
        List<ItemStack> items = new ArrayList<>();
        for(int i = (27 * _number); i<27+(27*_number); i++)
        {
            items.add(m_items.getOrDefault(i, null));
        }
        return items;
    }

    public void SetItemsOnInventoryStorage(Inventory inv, int _number)
    {
        int i = 0;
        for(ItemStack item : GetItemsOfStorageNumber(_number))
        {
            if(item != null)
                inv.setItem(i, item);
            i++;
        }

        for(int j = 0; j < 9; j++)
        {
            inv.setItem(j+27, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(8+27, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cQuitter", (short) 1));
    }

    public void OpenStorage(Player _p, int _storage)
    {
        InGameUtilities.playPlayerSound(_p, Sound.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 1, 0);
        switch(_storage)
        {
            case 0 ->
            {
                if(m_storage0 == null)
                {
                    m_storage0 = Bukkit.createInventory(null, 36, "§8Stockage 1 du bunker");
                    SetItemsOnInventoryStorage(m_storage0, 0);
                }
                _p.openInventory(m_storage0);
            }
            case 1 ->
            {
                if(m_storage1 == null)
                {
                    m_storage1 = Bukkit.createInventory(null, 36, "§8Stockage 2 du bunker");
                    SetItemsOnInventoryStorage(m_storage1, 1);
                }
                _p.openInventory(m_storage1);
            }
            case 2 ->
            {
                if(m_storage2 == null)
                {
                    m_storage2 = Bukkit.createInventory(null, 36, "§8Stockage 3 du bunker");
                    SetItemsOnInventoryStorage(m_storage2, 2);
                }
                _p.openInventory(m_storage2);
            }
            case 3 ->
            {
                if(m_storage3 == null)
                {
                    m_storage3 = Bukkit.createInventory(null, 36, "§8Stockage 4 du bunker");
                    SetItemsOnInventoryStorage(m_storage3, 3);
                }
                _p.openInventory(m_storage3);
            }
            case 4 ->
            {
                if(m_storage4 == null)
                {
                    m_storage4 = Bukkit.createInventory(null, 36, "§8Stockage 5 du bunker");
                    SetItemsOnInventoryStorage(m_storage4, 4);
                }
                _p.openInventory(m_storage4);
            }
            case 5 ->
            {
                if(m_storage5 == null)
                {
                    m_storage5 = Bukkit.createInventory(null, 36, "§8Stockage 6 du bunker");
                    SetItemsOnInventoryStorage(m_storage5, 5);
                }
                _p.openInventory(m_storage5);
            }
            case 6 ->
            {
                if(m_storage6 == null)
                {
                    m_storage6 = Bukkit.createInventory(null, 36, "§8Stockage 7 du bunker");
                    SetItemsOnInventoryStorage(m_storage6, 6);
                }
                _p.openInventory(m_storage6);
            }
            case 7 ->
            {
                if(m_storage7 == null)
                {
                    m_storage7 = Bukkit.createInventory(null, 36, "§8Stockage 8 du bunker");
                    SetItemsOnInventoryStorage(m_storage7, 7);
                }
                _p.openInventory(m_storage7);
            }
            case 8 ->
            {
                if(m_storage8 == null)
                {
                    m_storage8 = Bukkit.createInventory(null, 36, "§8Stockage 9 du bunker");
                    SetItemsOnInventoryStorage(m_storage8, 8);
                }
                _p.openInventory(m_storage8);
            }
            case 9 ->
            {
                if(m_storage9 == null)
                {
                    m_storage9 = Bukkit.createInventory(null, 36, "§8Stockage 10 du bunker");
                    SetItemsOnInventoryStorage(m_storage9, 9);
                }
                _p.openInventory(m_storage9);
            }
        }
    }
}
