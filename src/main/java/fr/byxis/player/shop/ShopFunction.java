package fr.byxis.player.shop;

import fr.byxis.db.DbConnection;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.PermissionUtilities;
import fr.byxis.jeton.jetonsCommandManager;
import fr.byxis.jeton.jetonSql;
import fr.byxis.player.karma.karmaManager;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InventoryUtilities;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static fr.byxis.player.quest.QuestManager.actualiseBuyProgress;
import static fr.byxis.player.quest.QuestManager.actualiseSellProgress;

public class ShopFunction {

    private Fireland main;
    private Player sender;

    public ShopFunction(Fireland main, Player sender)
    {
        this.main = main;
        this.sender = sender;
    }

    public ArrayList<ShopItemClass> getAllItemsOnShop(String _shop) {
        ArrayList<ShopItemClass> items = new ArrayList<>();
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {

            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT items.item_name, items.item, items.durability, items.command, item_shop.price, item_shop.sell, items.custom_model_data, item_shop.do_show" +
                    " FROM item_shop INNER JOIN items" +
                    " ON item_shop.item_name = items.item_name" +
                    " WHERE item_shop.shop = ?;");
            preparedStatement1.setString(1, _shop.replaceAll(" ", "_"));
            final ResultSet rs = preparedStatement1.executeQuery();
            //On vérifie s'il y a un résultat à la requête
            while (rs.next()) {
                ShopItemClass item = new ShopItemClass(rs.getString(1), Material.getMaterial(rs.getString(2)), rs.getShort(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getBoolean(8));
                if(item.show)
                {
                    items.add(item);
                }
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

            final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT items.item_name, items.item, items.durability, items.command, item_shop.price, item_shop.sell, items.custom_model_data, item_shop.do_show" +
                    " FROM item_shop INNER JOIN items" +
                    " ON item_shop.item_name = items.item_name" +
                    " WHERE item_shop.shop = ?" +
                    " AND items.item_name = ?");
            preparedStatement1.setString(1, _shop);
            preparedStatement1.setString(2, _itemName);
            final ResultSet rs = preparedStatement1.executeQuery();
            //On vérifie s'il y a un résultat à la requête
            if (rs.next()) {
                item = new ShopItemClass(rs.getString(1), Material.getMaterial(rs.getString(2)), rs.getShort(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getBoolean(8));
            }
            return item;
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #S002");
            e.printStackTrace();
        }
        return null;
    }

    public void setItemsOnShopInv(Inventory _inv, ArrayList<ShopItemClass> _items, int _currentPage, int _pageMax, Player p, boolean isSkinShop)
    {
        for(int i=0;i<9;i++)
        {
            _inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            if(i+45 == 52)
            {
                if(_currentPage == 1)
                {
                    _inv.setItem(i+45, InventoryUtilities.setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§a["+_currentPage+"/"+_pageMax+"]", (short) 1));
                }
                else
                {
                    _inv.setItem(i+45, InventoryUtilities.setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§a["+(_currentPage-1)+"/"+_pageMax+"]", (short) 1));
                }
            }
            else if(i+45 == 53)
            {
                if(_currentPage == _pageMax)
                {
                    _inv.setItem(i+45, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§c["+_currentPage+"/"+_pageMax+"]", (short) 1));
                }
                else
                {
                    _inv.setItem(i+45, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§c["+(_currentPage+1)+"/"+_pageMax+"]", (short) 1));
                }
            }
            else
            {
                _inv.setItem(i+45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            }

        }
        ArrayList<String> l = new ArrayList<>();
        l.add("§8Pour acheter un item, faites un");
        l.add("§6clic gauche§8 dessus, pour le");
        l.add("§8vendre, faites un §6clic droit§8.");
        if(isSkinShop)
        {
            l.add("§c§lPlus d'infos sur discord §6(/discord)");
        }
        _inv.setItem(45, InventoryUtilities.setItemMetaLore(Material.BOOK, "§r- Informations -", (short) 1, l));
        int spot = 19-(_currentPage * 14)+14;
        for (int i = (_currentPage * 14)-14; i < _items.size() && i < _currentPage * 14; i++)
        {
            if(spot+i == 26)
            {
                spot+=2;
            }
            ShopItemClass item = _items.get(i);
            List<String> lore = new ArrayList<>();
            if(!isSkinShop)
            {
                lore.add("§8Achat: §6"+getPriceText(item, p, false));
                lore.add("§8Vente: §6"+getSellText(item, p, false));
            }
            else
            {
                Permission perm = new Permission(item.command);
                Permission all = new Permission("csp.skin.all");
                if(p.hasPermission(perm) || p.hasPermission(all))
                {
                    lore.add("§aPossédé");
                }
                else
                {
                    lore.add("§8Achat: §6"+getPriceText(item, p, true));
                }
            }
            _inv.setItem(spot+i, InventoryUtilities.setItemCustomModelData(InventoryUtilities.setItemMetaLore(item.mat, "§r§7"+item.itemName, item.dura, lore),item.customModelData));
        }
    }

    public String getPriceText(ShopItemClass item, Player p, boolean isSkinShop)
    {
        if(isSkinShop)
        {
            return "§b "+item.price+" \u26c1";
        }
        else
        {
            double karma = getKarma(p.getUniqueId());
            if(karma >= 75 && !item.itemName.contains("Pass"))
            {
                double price = priceKarmaAdapter(p.getUniqueId(), item.price);
                return "§6§m"+item.price+"$§r §d"+price+"$ §8("+Math.round(getReduction(p.getUniqueId())*100)+"%)";
            }
            else if(karma <=25 && !item.itemName.contains("Pass"))
            {
                double price = priceKarmaAdapter(p.getUniqueId(), item.price);
                return "§6§m"+item.price+"$§r §c"+price+"$ §8(+"+Math.round(getReduction(p.getUniqueId())*100)+"%)";
            }
            return item.price+"$";
        }

    }

    public String getSellText(ShopItemClass item, Player p, boolean isSkinShop)
    {
        if(isSkinShop)
        {
            return "";
        }
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
        boolean isASkinShop = false;
        if(_shop.equalsIgnoreCase("skin"))
        {
            isASkinShop = true;
        }
        Inventory craftMenu = Bukkit.createInventory(null, 54, "Marchand de "+_shop+" ("+page+"/"+maxPage+")");
        setItemsOnShopInv(craftMenu, items, page, maxPage, _p, isASkinShop);
        _p.openInventory(craftMenu);
    }

    public void buyItem(ItemStack _itemClicked, Player _p, String _shop, boolean isSkinShop)
    {
        String name = _itemClicked.getItemMeta().getDisplayName();
        //name = name.replaceAll("[§.{1}]", "");
        name = name.replaceAll("§7", "");
        ShopItemClass item = getAnItemOnShop(_shop.replaceAll(" ", "_"), name);
        if(item != null)
        {
            if(isSkinShop)
            {
                jetonsCommandManager jeton = new jetonsCommandManager(main);
                if(_p.hasPermission(item.command) || _p.hasPermission("csp.skin.all"))
                {
                    InGameUtilities.sendPlayerError(_p, "Vous avez déjà ce skin !");
                }
                else if(jeton.getJetonsPlayer(_p.getUniqueId()) >= item.price)
                {
                    jetonSql facture = new jetonSql(main, _p);
                    if(facture.createFacture(_p.getUniqueId().toString(), item.price, "Achat du skin "+item.itemName+" le "+(new Date(System.currentTimeMillis())).getTime()))
                    {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user "+ _p.getName()+" permission set "+item.command+" true");
                        main.getLogger().info("[ACHAT] Achat de "+item.itemName+" par "+_p.getName()+" pour "+item.price+" jetons.");
                        jeton.removeJetonsPlayer(_p.getUniqueId(), item.price);
                        InGameUtilities.sendPlayerInformation(_p, "Vous avez acheté le skin: "+item.itemName+" ! Merci pour votre achat !");
                        InGameUtilities.playPlayerSound(_p, "gun.hud.money_drop", SoundCategory.AMBIENT, 1, 1);
                    }
                }
                else
                {
                    InGameUtilities.sendPlayerError(_p, "Vous n'avez pas assez de jetons !");
                }
            }
            else
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
                    actualiseBuyProgress(_p, (int) prix);
                    String command = item.command.replaceAll("Player", _p.getName());
                    if(command.contains("mcgive") )
                    {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:give "+_p.getName()+" minecraft:"+item.mat.name().toLowerCase()+"{display:{Name:'[{\"text\":\"§r"+"§r"+item.itemName+"\"}]'}} 1");
                        main.eco.withdrawPlayer(_p, prix);
                        _p.sendMessage("§aVous avez acheté : "+item.itemName+"§r§a pour §c"+prix+"$ §a!");
                        buyItemKarma(_p.getUniqueId());
                    }
                    else if (command.contains("minecraft:give"))
                    {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), item.command.replaceAll("Player", _p.getName()));
                        main.eco.withdrawPlayer(_p, prix);
                        _p.sendMessage("§aVous avez acheté : "+item.itemName+"§r§a pour §c"+prix+"$ §a!");
                        buyItemKarma(_p.getUniqueId());
                    }
                    else
                    {
                        if(command.contains("wm give"))
                        {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                        }
                        else
                        {
                            PermissionUtilities.commandExecutor(_p, command, "frere_c_quoi_la_perm");
                        }
                        main.eco.withdrawPlayer(_p, prix);
                        _p.sendMessage("§aVous avez acheté : "+item.itemName+"§r§a pour §6"+prix+"$ §a!");
                        InGameUtilities.playPlayerSound(_p, "gun.hud.money_drop", SoundCategory.AMBIENT, 1, 1);
                        buyItemKarma(_p.getUniqueId());
                    }
                }
                else if(_p.getGameMode() == GameMode.CREATIVE)
                {
                    String command = item.command.replaceAll("Player", _p.getName());
                    if(command.contains("mcgive"))
                    {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:give "+_p.getName()+" minecraft:"+item.mat.name().toLowerCase()+"{display:{Name:'[{\"text\":\"§r"+"§r"+item.itemName+"\"}]'}} 1");
                    }
                    else
                    {
                        PermissionUtilities.commandExecutor(_p, command, "crackshot.give.all");
                    }
                    _p.sendMessage("§aVous avez acheté : "+item.itemName+"§r§a pour §c"+prix+"$ §a!");
                    InGameUtilities.playPlayerSound(_p, "gun.hud.money_drop", SoundCategory.AMBIENT, 1, 1);
                }
                else
                {
                    _p.sendMessage("§cVous n'avez pas assez d'argent.");
                }
            }
        }
    }

    public void sellItem(ItemStack _itemClicked, Player _p, String _shop, boolean _isShiftClicked)
    {
        String name = _itemClicked.getItemMeta().getDisplayName();
        name = name.replaceAll("§7", "").replaceAll("\\u25ab", "").replaceAll("\\u25aa", "").replaceAll("\\u02D7","");

        String[] words = name.split(" ");
        StringBuilder sbb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if(i+1 != words.length)
            {

                if (words[i + 1].contains("«") || words[i + 1].contains("»")) {
                    sbb.append(words[i]);
                    break;
                }//«
                else {
                    sbb.append(words[i]).append(" ");
                }
            }
            else {
                sbb.append(words[i]);
            }
        }
        name = sbb.toString().trim();
        ShopItemClass item = getAnItemOnShop(_shop, name);

        if(item != null)
        {
            double sell = item.sell;

            if(_isShiftClicked && !item.command.contains("wm give"))
            {
                int nbr = 0;
                Inventory inv = _p.getInventory();
                for(ItemStack itemInv : inv)
                {
                    if(itemInv != null)
                    {
                        words = itemInv.getItemMeta().getDisplayName().replaceAll("§7", "").replaceAll("\\u25ab", "").replaceAll("\\u25aa", "").replaceAll("\\u02D7","").split(" ");
                        sbb = new StringBuilder();
                        for (int i = 0; i < words.length; i++) {
                            if(i+1 != words.length)
                            {
                                if (words[i + 1].contains("«") || words[i + 1].contains("»")) {
                                    sbb.append(words[i]);
                                    break;
                                }//«
                                else {
                                    sbb.append(words[i]).append(" ");
                                }
                            }
                            else {
                                sbb.append(words[i]);
                            }
                        }
                        String itemName = sbb.toString().trim();
                        if(ChatColor.stripColor(itemName).equalsIgnoreCase(ChatColor.stripColor(item.itemName)) ||ChatColor.stripColor(itemName).equalsIgnoreCase(ChatColor.stripColor(item.itemName)+" B"))
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
                actualiseSellProgress(_p, (int) (nbr*sell));
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
                        words = itemInv.getItemMeta().getDisplayName().replaceAll("§7", "").replaceAll("\\u25ab", "").replaceAll("\\u25aa", "").replaceAll("\\u02D7","").split(" ");
                        sbb = new StringBuilder();
                        for (int i = 0; i < words.length; i++) {
                            if(i+1 != words.length)
                            {

                                if (words[i + 1].contains("«") || words[i + 1].contains("»")) {
                                    sbb.append(words[i]);
                                    break;
                                }//«
                                else {
                                    sbb.append(words[i]).append(" ");
                                }
                            }
                            else {
                                sbb.append(words[i]);
                            }
                        }
                        String itemName = sbb.toString().trim();
                        if(ChatColor.stripColor(itemName).equalsIgnoreCase(ChatColor.stripColor(item.itemName)) ||ChatColor.stripColor(itemName).equalsIgnoreCase(ChatColor.stripColor(item.itemName)+" B"))
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
                    actualiseSellProgress(_p, (int) (sell));
                    _p.sendMessage("§aVous avez vendu un §7"+item.itemName+"§a pour "+sell+"$ !");
                    InGameUtilities.playPlayerSound(_p, "gun.hud.money_pickup", SoundCategory.AMBIENT, 1, 1);
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

    public void addItemOnShop(String _name, Material _item, short _dura, int _price, int _sell, String _shop, String _command, int _custommodeldata)
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
                final PreparedStatement preparedStatement2 = connection.prepareStatement("INSERT INTO items(item_name, item, durability, command, custom_model_data)" +
                        " VALUES(?,?,?,?,?)");
                preparedStatement2.setString(1, _name);
                preparedStatement2.setString(2, _item.toString());
                preparedStatement2.setShort(3, _dura);
                preparedStatement2.setString(4, _command);
                preparedStatement2.setInt(5, _custommodeldata);
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
