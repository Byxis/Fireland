package fr.byxis.player.shop;

import fr.byxis.db.DbConnection;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.fireland.utilities.PermissionUtilities;
import fr.byxis.jeton.JetonManager;
import fr.byxis.player.level.PlayerLevel;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static fr.byxis.fireland.Fireland.getEco;
import static fr.byxis.player.level.LevelStorage.getPlayerLevel;
import static fr.byxis.player.quest.QuestManager.actualiseBuyProgress;
import static fr.byxis.player.quest.QuestManager.actualiseSellProgress;

public class ShopFunction {

    private final Fireland main;
    private final Player sender;

    public ShopFunction(Fireland _main, Player _sender)
    {
        this.main = _main;
        this.sender = _sender;
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
                if (item.isShow())
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

    public void setItemsOnShopInv(Inventory _inv, ArrayList<ShopItemClass> _items, int _currentPage, int _pageMax, Player p, boolean isSkinShop, String _shop)
    {
        for (int i = 0; i < 9; i++)
        {
            _inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            if (i + 45 == 52)
            {
                if (_currentPage == 1)
                {
                    _inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§a[" + _currentPage + "/" + _pageMax + "]", (short) 1));
                }
                else
                {
                    _inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§a[" + (_currentPage - 1) + "/" + _pageMax + "]", (short) 1));
                }
            }
            else if (i + 45 == 53)
            {
                if (_currentPage == _pageMax)
                {
                    _inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§c[" + _currentPage + "/" + _pageMax + "]", (short) 1));
                }
                else
                {
                    _inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§c[" + (_currentPage + 1) + "/" + _pageMax + "]", (short) 1));
                }
            }
            else
            {
                _inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            }

        }
        ArrayList<String> l = new ArrayList<>();
        l.add("§8Pour acheter un item, faites un");
        l.add("§6clic gauche§8 dessus, pour le");
        l.add("§8vendre, faites un §6clic droit§8.");
        if (isSkinShop)
        {
            l.add("§c§lPlus d'infos sur discord §6(/discord)");
        }
        _inv.setItem(45, InventoryUtilities.setItemMetaLore(Material.BOOK, "§r- Informations -", (short) 1, l));
        int spot = 19 - (_currentPage * 14) + 14;
        for (int i = (_currentPage * 14) - 14; i < _items.size() && i < _currentPage * 14; i++)
        {
            if (spot + i == 26)
            {
                spot += 2;
            }
            ShopItemClass item = _items.get(i);
            List<String> lore = new ArrayList<>();
            if (!isSkinShop)
            {
                lore.add("§8Achat: §6 " + getPriceText(item, p, false, _shop));
                lore.add("§8Vente: §6 " + getSellText(item, p, false));
            }
            else
            {
                if (PermissionUtilities.hasPermission(p, item.getCommand()))
                {
                    lore.add("§aPossédé");
                }
                else
                {
                    lore.add("§8Achat: §6 " + getPriceText(item, p, true, _shop));
                }
            }
            _inv.setItem(spot + i, InventoryUtilities.setItemCustomModelData(
                    InventoryUtilities.setItemMetaLore(item.getMat(), "§r§7 " + item.getItemName(),
                            item.getDura(), lore), item.getCustomModelData()));
        }
    }

    public String getPriceText(ShopItemClass item, Player p, boolean isSkinShop, String _shop)
    {
        if (isSkinShop)
        {
            return "§b " + item.getPrice() + " \u26c1";
        }
        else
        {
            PlayerLevel pl = getPlayerLevel(p.getUniqueId());
            if (pl.getReduction() > 0 && pl.hasAccessToReductions(_shop))
            {
                double price = priceReduction(p.getUniqueId(), item.getPrice(), _shop);
                return "§6§m " + item.getPrice() + "$§r §d " + price + "$ §8(-" + Math.round(pl.getReduction() * 100) + "%)";
            }
            else if (pl.getReduction() > 0 && pl.hasAccessToAugmentation(_shop))
            {
                double price = priceReduction(p.getUniqueId(), item.getPrice(), _shop);
                return "§6§m " + item.getPrice() + "$§r §c " + price + "$ §8(+" + Math.round(pl.getReduction() * 100) + "%)";
            }
            return item.getPrice() + "$";
        }

    }

    public String getSellText(ShopItemClass item, Player p, boolean isSkinShop)
    {
        if (isSkinShop)
        {
            return "";
        }
        return item.getSell() + "$";
    }

    public String getShopName(InventoryView i)
    {
        String[] title = i.getTitle().split(" ");
        StringBuilder sb = new StringBuilder();
        for (int j = 2; j < title.length; j++)
        {
            if (!(title[j].equalsIgnoreCase("Marchand") || title[j].equalsIgnoreCase("de")) && j + 1 != title.length)
            {
                sb.append(title[j]).append(" ");
            }
        }

        return sb.toString().trim();
    }

    public String getShopName(String str)
    {
        String[] title = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int j = 2; j < title.length; j++)
        {
            if (!(title[j].equalsIgnoreCase("Marchand") || title[j].equalsIgnoreCase("de")) && j + 1 != title.length)
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
        if (pages.length() == 5 || (pages.length() == 6 && equals))
        {
            return Integer.parseInt(String.valueOf(pages.charAt(3)));
        }
        else if ((pages.length() == 6 && !equals) || pages.length() == 7)
        {
            return Integer.parseInt(String.valueOf(pages.charAt(4) + pages.charAt(5)));
        }
        return -1;
    }

    public int getItemPage(ItemStack i)
    {
        String pages = i.getItemMeta().getDisplayName();
        boolean equals = String.valueOf(pages.charAt(4)).equals("/");
        if (pages.length() == 7 || (pages.length() == 8 && !equals))
        {
            return Integer.parseInt(String.valueOf(pages.charAt(3)));
        }
        else if ((pages.length() == 8 && equals) || pages.length() == 9)
        {
            return Integer.parseInt(String.valueOf(pages.charAt(3) + pages.charAt(4)));
        }
        return -1;
    }

    public int getInvPageCurrent(InventoryView _title)
    {
        char c = _title.getTitle().charAt(11);
        int i;
        if (String.valueOf(c).equals("/"))
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
        while (nbrItems > 14)
        {
            nbrItems -= 14;
            maxPage++;
        }
        boolean isASkinShop = _shop.equalsIgnoreCase("skin");
        Inventory craftMenu = Bukkit.createInventory(null, 54, "Marchand de " + _shop.replaceAll("_", " ") + " (" + page + "/" + maxPage + ")");
        setItemsOnShopInv(craftMenu, items, page, maxPage, _p, isASkinShop, _shop);
        _p.openInventory(craftMenu);
    }

    public void buyItem(ItemStack _itemClicked, Player _p, String _shop, boolean isSkinShop)
    {
        String name = _itemClicked.getItemMeta().getDisplayName();
        //name = name.replaceAll("[§.{1}]", "");
        name = name.replaceAll("§7", "");
        ShopItemClass item = getAnItemOnShop(_shop.replaceAll(" ", "_"), name);
        if (item != null)
        {
            if (isSkinShop)
            {
                if (_p.hasPermission(item.getCommand()))
                {
                    InGameUtilities.sendPlayerError(_p, "Vous avez déjà ce skin !");
                }
                else if (JetonManager.payJetons(_p, item.getPrice(),
                        "Achat du skin " + item.getItemName(), false, true))
                {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + _p.getName() + " permission set " + item.getCommand() + " true");
                    InGameUtilities.sendPlayerInformation(_p, "Vous avez acheté le skin " + item.getItemName() + " ! Merci pour votre achat !");
                    InGameUtilities.playPlayerSound(_p, "gun.hud.money_drop", SoundCategory.AMBIENT, 1, 1);
                }
            }
            else
            {
                double balance = getEco().getBalance(_p);
                double prix;
                if (!item.getItemName().contains("Pass"))
                {
                    prix = priceReduction(_p.getUniqueId(), item.getPrice(), _shop);
                }
                else
                {
                    prix = item.getPrice();
                }

                if (balance >= prix)
                {
                    actualiseBuyProgress(_p, (int) prix);
                    String command = item.getCommand().replaceAll("Player", _p.getName());
                    if (command.contains("mcgive"))
                    {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:give " + _p.getName() + " minecraft:" + item.getMat().name().toLowerCase() + "{display:{Name:'[{\"text\":\"§r" + "§r " + item.getItemName() + "\"}]'}} 1");
                        getEco().withdrawPlayer(_p, prix);
                        _p.sendMessage("§aVous avez acheté : " + item.getItemName() + "§r§a pour §c " + prix + "$ §a!");
                    }
                    else if (command.contains("minecraft:give"))
                    {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), item.getCommand().replaceAll("Player", _p.getName()));
                        getEco().withdrawPlayer(_p, prix);
                        _p.sendMessage("§aVous avez acheté : " + item.getItemName() + "§r§a pour §c " + prix + "$ §a!");
                    }
                    else
                    {
                        if (command.contains("wm give") || command.contains("wmp give"))
                        {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                        }
                        else
                        {
                            PermissionUtilities.commandExecutor(_p, command, "frere_c_quoi_la_perm");
                        }
                        getEco().withdrawPlayer(_p, prix);
                        _p.sendMessage("§aVous avez acheté : " + item.getItemName() + "§r§a pour §6 " + prix + "$ §a!");
                        InGameUtilities.playPlayerSound(_p, "gun.hud.money_drop", SoundCategory.AMBIENT, 1, 1);
                    }
                }
                else if (_p.getGameMode() == GameMode.CREATIVE)
                {
                    String command = item.getCommand().replaceAll("Player", _p.getName());
                    if (command.contains("mcgive"))
                    {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:give " + _p.getName() + " minecraft:" + item.getMat().name().toLowerCase() + "{display:{Name:'[{\"text\":\"§r" + "§r " + item.getItemName() + "\"}]'}} 1");
                    }
                    else
                    {
                        PermissionUtilities.commandExecutor(_p, command, "crackshot.give.all");
                    }
                    _p.sendMessage("§aVous avez acheté : " + item.getItemName() + "§r§a pour §c " + prix + "$ §a!");
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
        name = name.replaceAll("§7", "").replaceAll("\\u25ab", "").replaceAll("\\u25aa", "").replaceAll("\\u02D7", "");

        String[] words = name.split(" ");
        StringBuilder sbb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i + 1 != words.length)
            {

                if (words[i + 1].contains("«") || words[i + 1].contains("»")) {
                    sbb.append(words[i]);
                    break;
                }
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

        if (item != null)
        {
            double sell = item.getSell();

            if (_isShiftClicked && !(item.getCommand().contains("wm give") || item.getCommand().contains("wmp give")))
            {
                int nbr = 0;
                for (ItemStack itemInv : getPlayerContent(_p))
                {
                    if (itemInv != null)
                    {
                        words = itemInv.getItemMeta().getDisplayName().replaceAll("§7", "").replaceAll("\\u25ab", "").replaceAll("\\u25aa", "").replaceAll("\\u02D7", "").split(" ");
                        sbb = new StringBuilder();
                        for (int i = 0; i < words.length; i++) {
                            if (i + 1 != words.length)
                            {
                                if (words[i + 1].contains("«") || words[i + 1].contains("»")) {
                                    sbb.append(words[i]);
                                    break;
                                }
                                else {
                                    sbb.append(words[i]).append(" ");
                                }
                            }
                            else {
                                sbb.append(words[i]);
                            }
                        }
                        String itemName = sbb.toString().trim();
                        if (ChatColor.stripColor(itemName).equalsIgnoreCase(ChatColor.stripColor(item.getItemName())) || ChatColor.stripColor(itemName).equalsIgnoreCase(ChatColor.stripColor(item.getItemName()) + " B"))
                        {
                            if (nbr + itemInv.getAmount() >= 64)
                            {
                                itemInv.setAmount(itemInv.getAmount() - nbr);
                                nbr += 64 - itemInv.getAmount();
                                break;
                            }
                            else
                            {
                                nbr += itemInv.getAmount();
                                itemInv.setAmount(0);
                            }
                        }
                    }

                }
                actualiseSellProgress(_p, (int) (nbr * sell));
                getEco().depositPlayer(_p, nbr * sell);
                _p.sendMessage("§aVous avez vendu " + nbr + " §7 " + item.getItemName() + " pour un total de §6 " + nbr * sell + "$§a !");
            }
            else
            {
                boolean founded = false;
                for (ItemStack itemInv : getPlayerContent(_p))
                {
                    if (itemInv != null)
                    {
                        String itemName;
                        if (itemInv.hasItemMeta())
                        {
                            words = itemInv.getItemMeta().getDisplayName().replaceAll("§7", "").replaceAll("\\u25ab", "").replaceAll("\\u25aa", "").replaceAll("\\u02D7", "").split(" ");
                            sbb = new StringBuilder();
                            for (int i = 0; i < words.length; i++) {
                                if (i + 1 != words.length)
                                {

                                    if (words[i + 1].contains("«") || words[i + 1].contains("»")) {
                                        sbb.append(words[i]);
                                        break;
                                    }
                                    else {
                                        sbb.append(words[i]).append(" ");
                                    }
                                }
                                else {
                                    sbb.append(words[i]);
                                }
                            }
                            itemName = sbb.toString().trim();
                            if (ChatColor.stripColor(itemName).equalsIgnoreCase(ChatColor.stripColor(item.getItemName())) || ChatColor.stripColor(itemName).equalsIgnoreCase(ChatColor.stripColor(item.getItemName()) + " B"))
                            {
                                itemInv.setAmount(itemInv.getAmount() - 1);
                                founded = true;
                                break;
                            }
                        }
                    }
                }

                if (founded)
                {
                    getEco().depositPlayer(_p, sell);
                    actualiseSellProgress(_p, (int) (sell));
                    _p.sendMessage("§aVous avez vendu un §7 " + item.getItemName() + "§a pour " + sell + "$ !");
                    InGameUtilities.playPlayerSound(_p, "gun.hud.money_pickup", SoundCategory.AMBIENT, 1, 1);
                }
                else
                {
                    _p.sendMessage("§cVous devez avoir l'item sur vous.");
                }
            }

        }
    }

    private double priceReduction(UUID _uuid, double amount, String _shop)
    {
        PlayerLevel pl = getPlayerLevel(_uuid);
        if (pl.hasAccessToReductions(_shop))
        {
            return amount - amount * pl.getReduction();
        }
        else if (pl.hasAccessToAugmentation(_shop))
        {
            return amount + amount * pl.getReduction();
        }
        return amount;
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
                sender.sendMessage("§aNouvel item créé :" + _item.name());
                final PreparedStatement preparedStatement2 = connection.prepareStatement("INSERT INTO items(item_name, item, durability, command, custom_model_data)" +
                        " VALUES(?,?,?,?,?)");
                preparedStatement2.setString(1, _name);
                preparedStatement2.setString(2, _item.toString());
                preparedStatement2.setShort(3, _dura);
                preparedStatement2.setString(4, _command);
                preparedStatement2.setInt(5, _custommodeldata);
                preparedStatement2.executeUpdate();
            }
            sender.sendMessage("§aItem " + _item.name() + " ajouté au shop " + _shop);
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
            while (rs.next())
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

    private List<ItemStack> getPlayerContent(Player _p)
    {
        PlayerInventory inventory = _p.getInventory();

        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < 36; i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                items.add(item);
            }
        }

        ItemStack offHandItem = inventory.getItemInOffHand();
        if (offHandItem != null) {
            items.add(offHandItem);
        }
        return items;
    }


}
