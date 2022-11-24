package fr.byxis.workshop.recycler;

import fr.byxis.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RecyclerFunction {

    int genererInt(int borneInf, int borneSup){
        Random random = new Random();
        int nb;
        nb = borneInf+random.nextInt(borneSup-borneInf);
        return nb;
    }

    private Main main;

    public RecyclerFunction(Main main)
    {
        this.main = main;
    }

    public int GetAmountOfSpace(Player p)
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

    public int GetItemScrapNumber(ItemStack item)
    {
        int amount = -1;
        switch (item.getType()) {
            case NETHERITE_HOE:
                amount = genererInt(5, 20);
                break;
            case NETHERITE_CHESTPLATE, CHAINMAIL_CHESTPLATE,DIAMOND_CHESTPLATE, GOLDEN_CHESTPLATE, IRON_CHESTPLATE, LEATHER_CHESTPLATE:
                amount = genererInt(2, 8);
                break;
            case NETHERITE_HELMET, CHAINMAIL_HELMET,DIAMOND_HELMET, GOLDEN_HELMET, IRON_HELMET, LEATHER_HELMET:
                amount = genererInt(1, 5);
                break;
            case NETHERITE_BOOTS, CHAINMAIL_BOOTS,DIAMOND_BOOTS, GOLDEN_BOOTS, IRON_BOOTS, LEATHER_BOOTS:
                amount = genererInt(0, 4);
                break;
            case NETHERITE_LEGGINGS, CHAINMAIL_LEGGINGS,DIAMOND_LEGGINGS, GOLDEN_LEGGINGS, IRON_LEGGINGS, LEATHER_LEGGINGS:
                amount = genererInt(2, 7);
                break;
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
                _inv.setItem(i, main.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRecycler les items", (short) 1));
            }
            else
            {
                _inv.setItem(i, main.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            }

        }
        ArrayList<String> l = new ArrayList<>();
        l.add("§8Pour recycler des items, faites un");
        l.add("§6clic gauche§8 sur le bouton");
        l.add("§8recyclage.§4§lLes items seront supprimés§r§8.");
        _inv.setItem(45, main.setItemMetaLore(Material.BOOK, "§r- Informations -", (short) 1, l));
    }

    public void Recycle(InventoryView inv, Player p) {
        int space = GetAmountOfSpace(p);
        ItemStack scrap = main.setItemMeta(Material.NETHERITE_SCRAP, "", (short)0);
        for(int i=0;i<45;i++)
        {
            if(inv.getItem(i) != null)
            {
                ItemStack item = inv.getItem(i);
                int scrapNbr = (GetItemScrapNumber(item));
                if (scrapNbr != -1)
                {
                    if(scrapNbr*item.getAmount() > space)
                    {
                        p.sendMessage("§cVous n'avez pas assez d'espace !");
                        break;
                    }
                    scrap.setAmount(scrapNbr*item.getAmount());
                    item.setAmount(0);
                    p.getInventory().addItem(scrap);
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
