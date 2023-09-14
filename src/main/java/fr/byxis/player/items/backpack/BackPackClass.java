package fr.byxis.player.items.backpack;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BackPackClass {
    private int maxInventorySize;
    private NamespacedKey backpackKey;
    private Gson gson = new Gson();
    private Type itemListType = new TypeToken<List<String>>(){}.getType();

    public BackPackClass(int level) {
        this.backpackKey = new NamespacedKey("fireland", "backpack");



        if(level == 1 || level >8)
        {
            this.maxInventorySize = 5;
        }
        else if(level == 2)
        {
            this.maxInventorySize = 7;
        }
        else if(level <= 3)
        {
            this.maxInventorySize = 9;
        }
        else
        {
            this.maxInventorySize = (level-2)*9;
        }
    }
    public boolean isBackPackEmpty(ItemStack item)
    {
        Inventory inventory = loadBackPack(item);
        return inventory.isEmpty();

    }
    public Inventory loadBackPack(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        Inventory inventory = createEmptyInventory(item.getItemMeta().getCustomModelData());

        if (container.has(backpackKey, PersistentDataType.STRING)) {
            try {
                String data = container.get(backpackKey, PersistentDataType.STRING);
                List<String> itemList = gson.fromJson(data, itemListType);
                ItemStack[] items = new ItemStack[itemList.size()];
                for (int i = 0; i < itemList.size(); i++) {
                    if (itemList.get(i) != null) {
                        YamlConfiguration config = new YamlConfiguration();
                        config.loadFromString(itemList.get(i));
                        items[i] = config.getItemStack("item");
                    }
                }
                inventory.setContents(items);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ItemStack glass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        if(this.maxInventorySize == 5)
        {
            inventory.setItem(0, glass);
            inventory.setItem(1, glass);
            inventory.setItem(7, glass);
            inventory.setItem(8, glass);
        }
        if(this.maxInventorySize == 7)
        {
            inventory.setItem(0, glass);
            inventory.setItem(8, glass);
        }
        return inventory;
    }

    public void saveBackPack(ItemStack item, Inventory inventory) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        try {
            ItemStack[] items = inventory.getContents();
            List<String> itemList = new ArrayList<>();
            for (ItemStack itemStack : items) {
                if (itemStack != null) {
                    YamlConfiguration config = new YamlConfiguration();
                    config.set("item", itemStack);
                    itemList.add(config.saveToString());
                } else {
                    itemList.add(null);
                }
            }
            String data = gson.toJson(itemList);
            container.set(backpackKey, PersistentDataType.STRING, data);
            item.setItemMeta(meta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void createBackPack(ItemStack item) {
        Inventory inventory = createEmptyInventory(item.getItemMeta().getCustomModelData());
        saveBackPack(item, inventory);
    }

    private Inventory createEmptyInventory(int level) {
        String name = null;
        switch(level)
        {
            case 1 -> name = "§cPochette";
            case 2 -> name = "§cSacoche";
            case 3 -> name = "§cSac à dos";
            case 4 -> name = "§cSac de sport";
            case 5 -> name = "§cSac de randonnée";
            case 6 -> name = "§cSac à dos militaire";
            default -> name = "§cA venir";
        }
        if(level > 3)
        {
            return Bukkit.createInventory(new BackpackInventoryHolder(), (level-2)*9, name);
        }
        return Bukkit.createInventory(new BackpackInventoryHolder(), 9, name);
    }

    private static class BackpackInventoryHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }


}
