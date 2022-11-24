package fr.byxis.shop;

import fr.byxis.db.DbConnection;
import fr.byxis.main.Main;
import fr.byxis.event.karmaManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopFunction {

    private Main main;
    private Player sender;

    public ShopFunction(Main main, Player sender)
    {
        this.main = main;
        this.sender = sender;
    }

    public ArrayList<ShopItemClass> getAllItemsOnShop(String _shop) {
        ArrayList<ShopItemClass> items = new ArrayList<>();
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {

            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT items.item_name, items.item, items.durability, items.command, item_shop.price, item_shop.sell" +
                    " FROM item_shop INNER JOIN items" +
                    " ON item_shop.item_name = items.item_name" +
                    " WHERE item_shop.shop = ?;");
            preparedStatement1.setString(1, _shop.replaceAll(" ", "_"));
            sender.sendMessage("|"+_shop+"|");
            final ResultSet rs = preparedStatement1.executeQuery();
            //On vérifie s'il y a un résultat à la requête
            while (rs.next()) {
                ShopItemClass item = new ShopItemClass(rs.getString(1), Material.getMaterial(rs.getString(2)), rs.getShort(3), rs.getString(4), rs.getInt(5), rs.getInt(6));
                sender.sendMessage("item : "+item.itemName);
                items.add(item);
            }
            return items;
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #S001");
            e.printStackTrace();
        }
        return items;
    }

    public ShopItemClass getAnItemOnShop(String _shop, String _itemName) {
        ShopItemClass item = null;
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {

            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT items.item_name, items.item, items.durability, items.command, item_shop.price, item_shop.sell" +
                    " FROM item_shop INNER JOIN items" +
                    " ON item_shop.item_name = items.item_name" +
                    " WHERE item_shop.shop = ?" +
                    " AND items.item_name = ?");
            preparedStatement1.setString(1, _shop);
            preparedStatement1.setString(2, _itemName);
            final ResultSet rs = preparedStatement1.executeQuery();
            //On vérifie s'il y a un résultat à la requête
            if (rs.next()) {
                item = new ShopItemClass(rs.getString(1), Material.getMaterial(rs.getString(2)), rs.getShort(3), rs.getString(4), rs.getInt(5), rs.getInt(6));
            }
            return item;
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #S002");
            e.printStackTrace();
        }
        return null;
    }

    public void setItemsOnShopInv(Inventory _inv, ArrayList<ShopItemClass> _items, int _currentPage, int _pageMax, Player p)
    {
        for(int i=0;i<9;i++)
        {
            _inv.setItem(i, main.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            if(i+45 == 52)
            {
                if(_currentPage == 1)
                {
                    _inv.setItem(i+45, main.setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§a["+_currentPage+"/"+_pageMax+"]", (short) 1));
                }
                else
                {
                    _inv.setItem(i+45, main.setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§a["+(_currentPage-1)+"/"+_pageMax+"]", (short) 1));
                }
            }
            else if(i+45 == 53)
            {
                if(_currentPage == _pageMax)
                {
                    _inv.setItem(i+45, main.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§c["+_currentPage+"/"+_pageMax+"]", (short) 1));
                }
                else
                {
                    _inv.setItem(i+45, main.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§c["+(_currentPage+1)+"/"+_pageMax+"]", (short) 1));
                }
            }
            else
            {
                _inv.setItem(i+45, main.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            }

        }
        ArrayList<String> l = new ArrayList<>();
        l.add("§8Pour acheter un item, faites un");
        l.add("§6clic gauche§8 dessus, pour le");
        l.add("§8vendre, faites un §6clic droit§8.");
        _inv.setItem(45, main.setItemMetaLore(Material.BOOK, "§r- Informations -", (short) 1, l));
        int spot = 19-(_currentPage * 14)+14;
        for (int i = (_currentPage * 14)-14; i < _items.size() && i < _currentPage * 14; i++)
        {
            if(spot+i == 26)
            {
                spot+=2;
            }
            ShopItemClass item = _items.get(i);
            List<String> lore = new ArrayList<>();
            lore.add("§8Achat: §6"+getPriceText(item, p));
            lore.add("§8Vente: §6"+getSellText(item, p));
            _inv.setItem(spot+i, main.setItemMetaLore(item.mat, "§r§7"+item.itemName, item.dura, lore));
        }
    }

    public String getPriceText(ShopItemClass item, Player p)
    {
        double karma = getKarma(p.getUniqueId());
        if(karma >= 75)
        {
            double price = priceKarmaAdapter(p.getUniqueId(), item.price);
            return "§6§m"+item.price+"$§r §d"+price+"$ §8("+getReduction(p.getUniqueId())*100+"%)";
        }
        else if(karma <=25)
        {
            double price = priceKarmaAdapter(p.getUniqueId(), item.price);
            return "§6§m"+item.price+"$§r §c"+price+"$ §8(+"+getReduction(p.getUniqueId())*100+"%)";
        }
        return item.price+"$";
    }

    public String getSellText(ShopItemClass item, Player p)
    {
        return item.sell+"$";
    }

    public String getShopName(InventoryView i)
    {
        String[] title = i.getTitle().split(" ");
        StringBuilder sb = new StringBuilder();
        for (int j = 2; j<title.length;j++)
        {
            if(!(title[j].equalsIgnoreCase("Marchand") || title[j].equalsIgnoreCase("de")) && j+1 != title.length)
            {
                sb.append(title[j]).append(" ");
            }
        }

        return sb.toString().trim();
    }

    public int getInvPageMax(InventoryView i)
    {
        String[] title = i.getTitle().split(" ");
        String pages = title[3];
        boolean equals = String.valueOf(pages.charAt(2)).equals("/");
        if(pages.length() == 5 || (pages.length() == 6 && equals))
        {
            return Integer.parseInt(String.valueOf(pages.charAt(3)));
        }
        else if((pages.length() == 6 && !equals) ||pages.length() == 7)
        {
            return Integer.parseInt(String.valueOf(pages.charAt(4)+pages.charAt(5)));
        }
        return -1;
    }

    public int getItemPage(ItemStack i)
    {
        String pages = i.getItemMeta().getDisplayName();
        boolean equals = String.valueOf(pages.charAt(4)).equals("/");
        if(pages.length() == 7 || (pages.length() == 8 && !equals))
        {
            return Integer.parseInt(String.valueOf(pages.charAt(3)));
        }
        else if((pages.length() == 8 && equals) ||pages.length() == 9)
        {
            return Integer.parseInt(String.valueOf(pages.charAt(3)+pages.charAt(4)));
        }
        return -1;
    }

    public int getInvPageCurrent(InventoryView _title)
    {
        char c= _title.getTitle().charAt(11);
        int i;
        if(String.valueOf(c).equals("/"))
        {
            String s = new StringBuilder().append(_title.getTitle().charAt(9)).append(_title.getTitle().charAt(10)).toString();
            i = Integer.parseInt(s);
        }
        else
        {
            i = Integer.parseInt(String.valueOf(_title.getTitle().charAt(9)));
        }
        return i;
    }

    public void openInv(Player _p, String _shop, int page)
    {
        int maxPage = 1;
        ArrayList<ShopItemClass> items = getAllItemsOnShop(_shop);
        int nbrItems = items.size();
        while(nbrItems > 14)
        {
            nbrItems -= 14;
            maxPage++;
        }
        Inventory craftMenu = Bukkit.createInventory(null, 54, "Marchand de "+_shop+" ("+page+"/"+maxPage+")");
        setItemsOnShopInv(craftMenu, items, page, maxPage, _p);
        _p.openInventory(craftMenu);
    }

    public void buyItem(ItemStack _itemClicked, Player _p, String _shop)
    {
        String name = _itemClicked.getItemMeta().getDisplayName();
        //name = name.replaceAll("[§.{1}]", "");
        name = name.replaceAll("§7", "");
        ShopItemClass item = getAnItemOnShop(_shop.replaceAll(" ", "_"), name);

        if(item != null)
        {
            double balance = main.eco.getBalance(_p);
            double prix;
            if(!item.itemName.contains("Pass"))
            {
                prix = priceKarmaAdapter(_p.getUniqueId(), item.price);
            }
            else
            {
                prix = item.price;
            }

            if(balance >= prix)
            {
                String command = item.command.replaceAll("Player", _p.getName());
                if(command.contains("mcgive"))
                {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:give "+_p.getName()+" minecraft:"+item.mat.name().toLowerCase()+"{display:{Name:'[{\"text\":\"§r"+"§r"+item.itemName+"\"}]'}} 1");
                    main.eco.withdrawPlayer(_p, prix);
                    _p.sendMessage("§aVous avez acheté : "+item.itemName+" pour §c"+prix+"§a!");
                    buyItemKarma(_p.getUniqueId());
                }
                else
                {
                    main.commandExecutor(_p, command, "crackshot.give.all");
                    main.eco.withdrawPlayer(_p, prix);
                    _p.sendMessage("§aVous avez acheté : "+item.itemName+" pour §6"+prix+"$ §a!");
                    _p.playSound(_p.getLocation(), "minecraft:gun.hud.money_drop", (float) 0.1, 1);
                    buyItemKarma(_p.getUniqueId());
                }
            }
            else
            {
                _p.sendMessage("§cVous n'avez pas assez d'argent.");
            }
        }
    }

    public void sellItem(ItemStack _itemClicked, Player _p, String _shop, boolean _isShiftClicked)
    {
        String name = _itemClicked.getItemMeta().getDisplayName();
        name = name.replaceAll("§7", "");
        ShopItemClass item = getAnItemOnShop(_shop, name);

        if(item != null)
        {
            double sell = item.sell;

            if(_isShiftClicked && !item.command.contains("shot"))
            {
                int nbr = 0;
                Inventory inv = _p.getInventory();
                for(ItemStack itemInv : inv)
                {
                    if(itemInv != null)
                    {
                        if(itemInv.getItemMeta().getDisplayName().contains(item.itemName))
                        {
                            if(nbr+itemInv.getAmount() >=64)
                            {
                                itemInv.setAmount(itemInv.getAmount()-nbr);
                                break;
                            }
                            itemInv.setAmount(0);
                            nbr += itemInv.getAmount();
                        }
                    }

                }
                main.eco.depositPlayer(_p, nbr*sell);
                _p.sendMessage("§aVous avez vendu "+nbr+" §7"+item.itemName+" pour un total de §6"+nbr*sell+"$§a !");
            }
            else
            {
                Inventory inv = _p.getInventory();
                boolean founded = false;
                for(ItemStack itemInv : inv)
                {
                    if(itemInv != null)
                    {
                        if(itemInv.getItemMeta().getDisplayName().contains(item.itemName))
                        {
                            itemInv.setAmount(itemInv.getAmount()-1);
                            founded = true;
                            break;
                        }
                    }
                }
                if(founded)
                {
                    main.eco.depositPlayer(_p, sell);
                    _p.sendMessage("§aVous avez vendu un §7"+item.itemName+"§a pour "+sell+"$ !");
                    _p.playSound(_p.getLocation(), "minecraft:gun.hud.money_drop", (float) 0.1, 1);
                }
                else
                {
                    _p.sendMessage("§cVous devez avoir l'item sur vous.");
                }
            }

        }
    }

    private void buyItemKarma(UUID _uuid)
    {
        karmaManager karma = new karmaManager(main);
        karma.goodAction(_uuid, 0.1);
    }

    public double getKarma(UUID _uuid)
    {
        karmaManager karma = new karmaManager(main);
        return karma.getKarma(_uuid);
    }

    private double priceKarmaAdapter(UUID _uuid, double amount)
    {
        double karma = getKarma(_uuid);
        if(karma >= 75)
        {
            return Math.round(amount + Math.round(amount * getReduction(_uuid)));
        }
        else if(karma < 25)
        {
            return Math.round(amount + Math.round(amount * getReduction(_uuid)));
        }
        else
        {
            return amount;
        }
    }

    private double getReduction(UUID _uuid)
    {
        double karma = getKarma(_uuid);

        if(karma >= 75)
        {
            double reduction = karma-75;
            return -(reduction)*0.01;
        }
        if(karma <= 25)
        {
            double reduction = karma -50;
            return -(reduction)*0.02;
        }
        return 0;
    }

    public void addItemOnShop(String _name, Material _item, short _dura, int _price, int _sell, String _shop, String _command)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {

            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT item_name" +
                    " FROM items" +
                    " WHERE items.item_name = ?");
            preparedStatement1.setString(1, _name);
            final ResultSet rs = preparedStatement1.executeQuery();
            //On vérifie s'il y a un résultat à la requête
            if (!rs.next())
            {
                sender.sendMessage("§aNouvel item créé :"+_item.name());
                final PreparedStatement preparedStatement2 = connection.prepareStatement("INSERT INTO items(item_name, item, durability, command)" +
                        " VALUES(?,?,?,?)");
                preparedStatement2.setString(1, _name);
                preparedStatement2.setString(2, _item.toString());
                preparedStatement2.setShort(3, _dura);
                preparedStatement2.setString(4, _command);
                preparedStatement2.executeUpdate();
            }
            sender.sendMessage("§aItem "+_item.name()+" ajouté au shop "+_shop);
            final PreparedStatement preparedStatement3 = connection.prepareStatement("INSERT INTO item_shop(item_name, shop, price, sell)" +
                    " VALUES(?,?,?,?)");
            preparedStatement3.setString(1, _name);
            preparedStatement3.setString(2, _shop);
            preparedStatement3.setInt(3, _price);
            preparedStatement3.setInt(4, _sell);
            preparedStatement3.executeUpdate();

        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #S003");
            e.printStackTrace();
        }
    }

    public ArrayList<String> getAllShop()
    {
        ArrayList<String> l = new ArrayList<>();
        l.add("--Shop");
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement3 = connection.prepareStatement("SELECT DISTINCT item_shop.shop" +
                    " FROM item_shop");
            ResultSet rs = preparedStatement3.executeQuery();
            while(rs.next())
            {
                l.add(rs.getString(1));
            }

        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #S003");
            e.printStackTrace();
        }
        return l;
    }
}
