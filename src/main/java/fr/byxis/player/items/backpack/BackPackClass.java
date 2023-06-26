package fr.byxis.player.items.backpack;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
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
        if(level <= 1 || level >6)
        {
            this.maxInventorySize = 9;
        }
        else
        {
            this.maxInventorySize = level*9;
        }
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
            default -> name = "§cCeinture de munitions";
            case 2,4,5 -> name = "§cA venir";
            case 3 -> name = "§cSac à dos léger";
            case 6 -> name = "§cSac à dos militaire";
        }
        return Bukkit.createInventory(new BackpackInventoryHolder(), level*9, name);
    }

    private static class BackpackInventoryHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }


}
