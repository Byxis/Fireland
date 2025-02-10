package fr.byxis.player.workshop.recycler;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerFunction {

    private final Fireland main;

    public RecyclerFunction(Fireland _main)
    {
        this.main = _main;
    }

    public int getAmountOfSpaceScrap(Player p)
    {
        int amount = 0;
        for (ItemStack i : p.getInventory().getContents())
        {
            if (i == null)
            {
                amount += 64;
            }
            else if (i.getType() == Material.NETHERITE_SCRAP)
            {
                amount += (64 - i.getAmount());
            }
        }
        return amount;
    }

    public int getAmountOfSpaceGp(Player p)
    {
        int amount = 0;
        for (ItemStack i : p.getInventory().getContents())
        {
            if (i.getType() == Material.GUNPOWDER)
            {
                amount += (64 - i.getAmount());
            }
        }
        return amount;
    }

    public int getItemScrapNumber(ItemStack item) {
        int amount = 0;
        for (int i = 0; i < item.getAmount(); i++) {
            int increment = switch (item.getType()) {
                case NETHERITE_SCRAP -> 1;
                case NETHERITE_HOE -> BasicUtilities.generateInt(2, 10);
                case ARROW -> BasicUtilities.generateInt(1, 2);
                case NETHERITE_CHESTPLATE, CHAINMAIL_CHESTPLATE, DIAMOND_CHESTPLATE, GOLDEN_CHESTPLATE, IRON_CHESTPLATE, LEATHER_CHESTPLATE
                        -> BasicUtilities.generateInt(1, 8);
                case NETHERITE_HELMET, CHAINMAIL_HELMET, DIAMOND_HELMET, GOLDEN_HELMET, IRON_HELMET, LEATHER_HELMET
                        -> BasicUtilities.generateInt(1, 5);
                case NETHERITE_BOOTS, CHAINMAIL_BOOTS, DIAMOND_BOOTS, GOLDEN_BOOTS, IRON_BOOTS, LEATHER_BOOTS
                        -> BasicUtilities.generateInt(1, 4);
                case NETHERITE_LEGGINGS, CHAINMAIL_LEGGINGS, DIAMOND_LEGGINGS, GOLDEN_LEGGINGS, IRON_LEGGINGS, LEATHER_LEGGINGS
                        -> BasicUtilities.generateInt(1, 7);
                case WHEAT_SEEDS -> BasicUtilities.generateInt(0, 3);
                case IRON_NUGGET -> BasicUtilities.generateInt(0, 2);
                case IRON_INGOT -> BasicUtilities.generateInt(0, 5);
                case WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD, IRON_HOE, STONE_SHOVEL, IRON_PICKAXE, STONE_AXE, STONE_PICKAXE, DIAMOND_SWORD, STONE_HOE
                        -> BasicUtilities.generateInt(1, 6);
                default -> 0;
            };
            amount += increment;
        }
        return amount;
    }

    public int getItemGunpowderNumber(ItemStack item) {
        int amount = 0;
        for (int i = 0; i < item.getAmount(); i++) {
            int increment = switch (item.getType()) {
                case GUNPOWDER -> 1;
                case WHEAT_SEEDS -> BasicUtilities.generateInt(0, 2);
                case IRON_NUGGET -> BasicUtilities.generateInt(0, 1);
                default -> 0;
            };
            amount += increment;
        }
        return amount;
    }


    public int getItemMedsNumber(ItemStack item)
    {
        int amount = 0;
        for (int i = 0; i < item.getAmount(); i++)
        {
            switch (item.getType()) {
                case QUARTZ:
                    amount += BasicUtilities.generateInt(1, 3);
                    break;
                case AMETHYST_SHARD:
                    amount += 1;
                    break;
                case GHAST_TEAR:
                    amount += BasicUtilities.generateInt(2, 5);
                    break;
            }
        }
        return amount;
    }

    public void openRecyclingGui(Player p)
    {
        Inventory recyclingMenu = Bukkit.createInventory(null, 54, "§2Recycleur");
        setItemsGuiInv(recyclingMenu);
        p.openInventory(recyclingMenu);
    }

    public void setItemsGuiInv(Inventory _inv)
    {
        for (int i = 46; i < 54; i++)
        {
            if (i == 49)
            {
                _inv.setItem(i, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRecycler les items", (short) 1));
            }
            else
            {
                _inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            }

        }
        ArrayList<String> l = new ArrayList<>();
        l.add("§8Pour recycler des items, faites un");
        l.add("§6clic gauche§8 sur le bouton");
        l.add("§8recyclage.§4§lLes items seront supprimés§r§8.");
        _inv.setItem(45, InventoryUtilities.setItemMetaLore(Material.BOOK, "§r- Informations -", (short) 1, l));
    }

    public void recycle(InventoryView inv, Player p) {
        ItemStack scrap = new ItemStack(Material.NETHERITE_SCRAP);
        ItemStack gp = new ItemStack(Material.GUNPOWDER);

        InGameUtilities.playPlayerSound(p, "gun.hud.scraps", SoundCategory.BLOCKS, 1, 2);
        int space = getAmountOfSpaceScrap(p);
        for (int i = 0; i < 45; i++)
        {
            if (inv.getItem(i) != null)
            {
                ItemStack item = inv.getItem(i);
                if (item == null) continue;
                int scrapNbr = (getItemScrapNumber(item));
                int gbNbr = (getItemGunpowderNumber(item));
                if (scrapNbr != 0)
                {
                    if (gbNbr != 0)
                    {
                        if (hasGived(p, gp, space, item, gbNbr)) break;
                    }
                    if (hasGived(p, scrap, space, item, scrapNbr)) break;
                }
                else if (gbNbr > 0)
                {
                    if (hasGived(p, gp, space, item, gbNbr)) break;
                }
            }
        }
    }

    private boolean hasGived(Player p, ItemStack component, int space, ItemStack item, int scrapNbr) {
        if (scrapNbr > space)
        {
            p.sendMessage("§cVous n'avez pas assez d'espace pour recycler cet item !");
            return true;
        }
        InGameUtilities.playPlayerSound(p, "gun.hud.scraps", SoundCategory.BLOCKS, 1, 0);
        component.setAmount(scrapNbr);
        item.setAmount(0);
        p.getInventory().addItem(component);
        return false;
    }

    public void giveBackItem(InventoryView inv, Player p)
    {
        for (int i = 0; i < 45; i++)
        {
            if (inv.getItem(i) != null)
            {
                HashMap<Integer, ItemStack> items = p.getInventory().addItem(inv.getItem(i));
                for (int j = 0; j < items.size(); j++)
                {
                    p.getWorld().dropItem(p.getLocation(), items.get(j));
                }
            }
        }
    }
}
