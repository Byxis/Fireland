package fr.byxis.workshop.recycler;

import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.fireland.Fireland;
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

    private Fireland main;

    public RecyclerFunction(Fireland main)
    {
        this.main = main;
    }

    public int GetAmountOfSpaceScrap(Player p)
    {
        int amount = 0;
        for(ItemStack i : p.getInventory().getContents())
        {
            if(i == null )
            {
                amount += 64;
            }
            else if(i.getType() == Material.NETHERITE_SCRAP)
            {
                amount += (64-i.getAmount());
            }
        }
        return amount;
    }

    public int GetAmountOfSpaceGp(Player p)
    {
        int amount = 0;
        for(ItemStack i : p.getInventory().getContents())
        {
            if(i.getType() == Material.GUNPOWDER)
            {
                amount += (64-i.getAmount());
            }
        }
        return amount;
    }

    public int GetItemScrapNumber(ItemStack item)
    {
        int amount = 0;
        for(int i=0;i< item.getAmount();i++)
        {
            switch (item.getType())
            {
                case NETHERITE_SCRAP:
                    amount += 1;
                    break;
                case NETHERITE_HOE:
                    amount += BasicUtilities.generateInt(2, 10);
                    break;
                case NETHERITE_CHESTPLATE, CHAINMAIL_CHESTPLATE, DIAMOND_CHESTPLATE, GOLDEN_CHESTPLATE, IRON_CHESTPLATE, LEATHER_CHESTPLATE:
                    amount += BasicUtilities.generateInt(1, 8);
                    break;
                case NETHERITE_HELMET, CHAINMAIL_HELMET, DIAMOND_HELMET, GOLDEN_HELMET, IRON_HELMET, LEATHER_HELMET:
                    amount += BasicUtilities.generateInt(1, 5);
                    break;
                case NETHERITE_BOOTS, CHAINMAIL_BOOTS, DIAMOND_BOOTS, GOLDEN_BOOTS, IRON_BOOTS, LEATHER_BOOTS:
                    amount += BasicUtilities.generateInt(1, 4);
                    break;
                case NETHERITE_LEGGINGS, CHAINMAIL_LEGGINGS, DIAMOND_LEGGINGS, GOLDEN_LEGGINGS, IRON_LEGGINGS, LEATHER_LEGGINGS:
                    amount += BasicUtilities.generateInt(1, 7);
                    break;
                case PUMPKIN_SEEDS, GRAY_DYE, INK_SAC, MELON_SEEDS, GREEN_DYE, CYAN_DYE, BEETROOT_SEEDS, GOLD_NUGGET, BLUE_DYE, LAPIS_LAZULI, LIME_DYE, LIGHT_BLUE_DYE, YELLOW_DYE, PINK_DYE, BROWN_DYE, IRON_NUGGET, CHARCOAL, ARROW, STICK, PURPLE_DYE, ORANGE_DYE, MAGENTA_DYE, BRICK, LIGHT_GRAY_DYE, NETHER_BRICK:
                    if(item.getItemMeta().getDisplayName().contains("Cartouche"))
                    {
                        amount += BasicUtilities.generateInt(0, 2);
                    }
                    else
                    {
                        amount += BasicUtilities.generateInt(0, 3);
                    }
                    break;
                case WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD, IRON_HOE, STONE_SHOVEL, IRON_PICKAXE, STONE_AXE, STONE_PICKAXE, DIAMOND_SWORD, STONE_HOE:
                    amount += BasicUtilities.generateInt(1, 6);
                    break;
            }
        }
        return amount;
    }
    public int GetItemGunpowderNumber(ItemStack item)
    {
        int amount = 0;
        for(int i=0;i< item.getAmount();i++)
        {
            switch (item.getType()) {
                case PUMPKIN_SEEDS, GRAY_DYE, INK_SAC, MELON_SEEDS, GREEN_DYE, CYAN_DYE, BEETROOT_SEEDS, GOLD_NUGGET, BLUE_DYE, LAPIS_LAZULI, LIME_DYE, LIGHT_BLUE_DYE, YELLOW_DYE, PINK_DYE, BROWN_DYE, IRON_NUGGET, CHARCOAL, ARROW, STICK, PURPLE_DYE, ORANGE_DYE, MAGENTA_DYE, BRICK, LIGHT_GRAY_DYE, NETHER_BRICK:
                    if(item.getItemMeta().getDisplayName().contains("Cartouche"))
                    {
                        int j = BasicUtilities.generateInt(-2, 2);
                        if(j>0)
                        {
                            amount+=j;
                        }
                    }
                    else
                    {
                        amount += BasicUtilities.generateInt(0, 3);
                    }
                    break;
                case GUNPOWDER:
                    amount +=1;
                    break;
            }
        }
        return amount;
    }

    public int GetItemMedsNumber(ItemStack item)
    {
        int amount = 0;
        for(int i=0;i< item.getAmount();i++)
        {
            switch (item.getType()) {
                case QUARTZ:
                    amount += BasicUtilities.generateInt(1, 3);
                    break;
                case AMETHYST_SHARD:
                    amount +=1;
                    break;
                case GHAST_TEAR:
                    amount += BasicUtilities.generateInt(2, 5);
                    break;
            }
        }
        return amount;
    }

    public void OpenRecyclingGui(Player p)
    {
        Inventory RecyclingMenu = Bukkit.createInventory(null, 54, "§2Recycleur");
        setItemsGuiInv(RecyclingMenu);
        p.openInventory(RecyclingMenu);
    }

    public void setItemsGuiInv(Inventory _inv)
    {
        for(int i=46;i<54;i++)
        {
            if(i == 49)
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

    public void Recycle(InventoryView inv, Player p) {
        ItemStack scrap= InventoryUtilities.setItemMeta(Material.NETHERITE_SCRAP, "§7Débris métallique", (short)0);
        ItemStack gp= InventoryUtilities.setItemMeta(Material.GUNPOWDER, "§7Poudre à canon", (short)0);
        ItemStack meds= InventoryUtilities.setItemMeta(Material.AMETHYST_SHARD, "§7Médicaments", (short)0);;

        int space = GetAmountOfSpaceScrap(p);
        for(int i=0;i<45;i++)
        {
            if(inv.getItem(i) != null)
            {
                ItemStack item = inv.getItem(i);
                int scrapNbr = (GetItemScrapNumber(item));
                if (scrapNbr != 0)
                {
                    int gbNbr = (GetItemGunpowderNumber(item));
                    if (gbNbr != 0)
                    {
                        if(gbNbr > space)
                        {
                            p.sendMessage("§cVous n'avez pas assez d'espace pour recycler cet item !");
                            break;
                        }
                        InGameUtilities.playPlayerSound(p, "gun.hud.scraps", SoundCategory.BLOCKS, 1, 0);
                        gp.setAmount(gbNbr);
                        item.setAmount(0);
                        p.getInventory().addItem(gp);
                    }
                    if(scrapNbr > space)
                    {
                        p.sendMessage("§cVous n'avez pas assez d'espace pour recycler cet item !");
                        break;
                    }
                    InGameUtilities.playPlayerSound(p, "gun.hud.scraps", SoundCategory.BLOCKS, 1, 0);
                    scrap.setAmount(scrapNbr);
                    item.setAmount(0);
                    p.getInventory().addItem(scrap);
                }
                int medsNbr = (GetItemMedsNumber(item));
                if (medsNbr != 0)
                {
                    if(medsNbr > space)
                    {
                        p.sendMessage("§cVous n'avez pas assez d'espace pour recycler cet item !");
                        break;
                    }
                    InGameUtilities.playPlayerSound(p, "gun.hud.scraps", SoundCategory.BLOCKS, 1, 0);
                    meds.setAmount(medsNbr);
                    item.setAmount(0);
                    p.getInventory().addItem(meds);
                }
            }
        }
    }

    public void GiveBackItem(InventoryView inv, Player p)
    {
        for(int i=0;i<45;i++)
        {
            if(inv.getItem(i) != null)
            {
                HashMap<Integer,ItemStack> items = p.getInventory().addItem(inv.getItem(i));
                for(int j=0;j<items.size();j++)
                {
                    p.getWorld().dropItem(p.getLocation(), items.get(j));
                }
            }
        }
    }
}
