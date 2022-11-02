package fr.byxis.workshop;

import fr.byxis.db.DbConnection;
import fr.byxis.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class workshopFunction {

    final private Main main;
    final private Player sender;

    public workshopFunction(Main main, Player sender)
    {
        //Récupération du main, pour pouvoir avoir envoyer des requêtes à la base de données
        this.main = main;
        //Récupération de la personne qui envoie la commande, pour lui envoyer les messages d'erreurs
        this.sender = sender;
    }

    public void createRecipe(String _name, String _command, String _type, Integer _scrap, Integer _gunpowder)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            //On prépare la requête SQL
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO workshop_recipes (name, command, type, scrap, gunpowder) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setString(1, _name);
            preparedStatement.setString(2, _command);
            preparedStatement.setString(3, _type);
            preparedStatement.setInt(4, _scrap);
            preparedStatement.setInt(5, _gunpowder);
            //On execute la requête
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W001");
            e.printStackTrace();
        }
    }

    public int getTimeCrafted(String _recipeName, String _uuid) {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {

            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT player_workshop.crafted_time FROM player_workshop INNER JOIN players ON player_workshop.player_uuid = players.uuid WHERE player_workshop.player_uuid = ? AND player_workshop.recipe_name = ?");
            preparedStatement1.setString(1, _uuid);
            preparedStatement1.setString(2, _recipeName);

            final ResultSet resultSet = preparedStatement1.executeQuery();
            int crafted_time = 0;
            //On vérifie s'il y a un résultat à la requête
            if (resultSet.next()) {
                crafted_time = resultSet.getInt(1);
            }
            return crafted_time;
        } catch (SQLException e) {
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W003");
            e.printStackTrace();

        }
        return 0;
    }

        public void craftItemNbr(String _recipeName, String _uuid, int _amount)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();

            if(getCraftedTimeToLearn(_recipeName) != 0)
            {
                final PreparedStatement preparedStatement4 = connection.prepareStatement("UPDATE player_workshop SET crafted_time =? WHERE player_uuid = ? AND recipe_name = ?");
                preparedStatement4.setInt(1, getTimeCrafted(_recipeName, _uuid)+_amount);
                preparedStatement4.setString(2, _uuid);
                preparedStatement4.setString(3, _recipeName);

                //On execute la requête
                preparedStatement4.executeUpdate();
            }
            else
            {
                final PreparedStatement preparedStatement4 = connection.prepareStatement("INSERT INTO player_workshop (player_uuid, recipe_name, crafted_time, know) VALUES (?,?,?,?)");
                preparedStatement4.setInt(1, _amount);
                preparedStatement4.setString(2, _uuid);
                preparedStatement4.setString(3, _recipeName);
                preparedStatement4.setBoolean(3, false);

                //On execute la requête
                preparedStatement4.executeUpdate();
            }

        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W002");
            e.printStackTrace();
        }
    }

    public void learnRecipe(String _recipeName, String _uuid) {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement4 = connection.prepareStatement("UPDATE player_workshop SET know = 1 WHERE player_uuid = ? AND recipe_name = ?");
            preparedStatement4.setString(1, _uuid);
            preparedStatement4.setString(2, _recipeName);

            //On execute la requête
            preparedStatement4.executeUpdate();
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W004");
            e.printStackTrace();
        }
    }

    public int getCraftedTimeToLearn(String _recipeName) {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT workshop_recipes.type FROM workshop_recipes WHERE name = ?");
            preparedStatement1.setString(1, _recipeName);

            final ResultSet resultSet = preparedStatement1.executeQuery();
            int craftedTimeToLearn = 0;
            //On vérifie s'il y a un résultat à la requête
            if (resultSet.next()) {
                if(resultSet.wasNull())
                {
                    return 0;
                }
                craftedTimeToLearn = resultSet.getInt(1);
            }
            return craftedTimeToLearn;
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W005");
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isLearned(String _recipeName, String _uuid) {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT player_workshop.know FROM player_workshop WHERE player_uuid = ? AND recipe_name = ?");
            preparedStatement1.setString(1, _uuid);
            preparedStatement1.setString(2, _recipeName);

            final ResultSet resultSet = preparedStatement1.executeQuery();
            boolean craftedTimeToLearn = false;
            //On vérifie s'il y a un résultat à la requête
            if (resultSet.next()) {
                int i = resultSet.getInt(0);
                if (i == 0) {
                    return false;
                } else {
                    return true;
                }
            }
            return craftedTimeToLearn;
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W006");
            e.printStackTrace();
        }
        return false;
    }

    private int getNbrOfShowingItems(String _uuid, int _scrapAmount, int _gunpowderAmount)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT workshop_recipes.scrap, workshop_recipes.gunpowder, player_workshop.know FROM workshop_recipes INNER JOIN player_workshop WHERE player_workshop.recipe_name = workshop_recipes.name");

            final ResultSet resultSet = preparedStatement1.executeQuery();
            int nbr = 0;
            //On vérifie s'il y a un résultat à la requête
            while (resultSet.next()) {
                if((resultSet.getInt(1) <= _scrapAmount && resultSet.getInt(2) <= _gunpowderAmount) || resultSet.getBoolean(3))
                {
                    nbr++;
                }
            }
            return nbr;
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W007");
            e.printStackTrace();
        }
        return 0;
    }

    private ArrayList<workshopItemClass> getAllCraftableItems(String _uuid, int _scrapAmount, int _gunpowderAmount)
    {
        ArrayList<workshopItemClass> items = new ArrayList<>();
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT player_workshop.know, workshop_recipes.name, workshop_recipes.scrap, workshop_recipes.gunpowder, workshop_recipes.type, workshop_recipes.command, workshop_craft.item_name, workshop_craft.item, workshop_craft.durability FROM workshop_recipes INNER JOIN player_workshop, workshop_craft WHERE player_workshop.recipe_name = workshop_recipes.name AND workshop_recipes.name = workshop_craft.recipe_name");

            final ResultSet resultSet = preparedStatement1.executeQuery();
            //On vérifie s'il y a un résultat à la requête
            while (resultSet.next()) {
                if((resultSet.getInt(3) <= _scrapAmount && resultSet.getInt(4) <= _gunpowderAmount) || resultSet.getBoolean(1))
                {
                    workshopItemClass item = new workshopItemClass(resultSet.getString(2), resultSet.getString(7), resultSet.getString(5), resultSet.getInt(3), resultSet.getInt(4), Material.getMaterial(resultSet.getString(8)), (short) resultSet.getInt(9), resultSet.getString(6), resultSet.getBoolean(1));
                    items.add(item);
                }
            }
            return items;
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W008");
            e.printStackTrace();
        }
        return items;
    }

    public int[] getCraftItems(Player p)
    {
        int scrap = 0;
        int gunpowder = 0;
        for (ItemStack s : p.getInventory().getContents())
        {
            if(s != null)
            {
                if(s.getType() == Material.NETHERITE_SCRAP)
                {
                    scrap++;
                }
                if(s.getType() == Material.GUNPOWDER)
                {
                    gunpowder++;
                }
            }
        }
        return new int[]{scrap, gunpowder};
    }
    private ItemStack setItemMeta(Material mat, String name, short dura) {
        ItemStack item = new ItemStack(mat);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        item.setDurability(dura);
        return item;
    }
    private ItemStack setItemMetaLore(Material mat, String name, short dura, List<String> lore) {
        ItemStack item = new ItemStack(mat);

        if(mat.equals(Material.GLASS_BOTTLE))
        {
            item = new ItemStack(Material.POTION, 1);
            ItemMeta meta = item.getItemMeta();
            PotionMeta pmeta = (PotionMeta) meta;
            PotionData pdata = new PotionData(PotionType.WATER);
            pmeta.setBasePotionData(pdata);
            item.setItemMeta(meta);
        }

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        itemMeta.setUnbreakable(true);
        item.setItemMeta(itemMeta);
        item.setDurability(dura);
        return item;
    }

    public void setItemsInv(Inventory _inv, int[] _craftableItems, ArrayList<workshopItemClass> _items, int _currentPage, int _pageMax)
    {
        for(int i=0;i<9;i++)
        {
            _inv.setItem(i, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            if(i+45 == 52)
            {
                if(_currentPage == 1)
                {
                    _inv.setItem(i+45, setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§a["+_currentPage+"/"+_pageMax+"]", (short) 1));
                }
                else
                {
                    _inv.setItem(i+45, setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§a["+(_currentPage-1)+"/"+_pageMax+"]", (short) 1));
                }
            }
            else if(i+45 == 53)
            {
                if(_currentPage == _pageMax)
                {
                    _inv.setItem(i+45, setItemMeta(Material.RED_STAINED_GLASS_PANE, "§c["+_currentPage+"/"+_pageMax+"]", (short) 1));
                }
                else
                {
                    _inv.setItem(i+45, setItemMeta(Material.RED_STAINED_GLASS_PANE, "§c["+(_currentPage+1)+"/"+_pageMax+"]", (short) 1));
                }
            }
            else
            {
                _inv.setItem(i+45, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            }

        }
        for (int i = _pageMax * 20 -20; i < _items.size(); i++)
        {
            if(i <= _pageMax * 20 -20)
            {
                if(i >= 26)
                {
                    i+=2;
                }
                workshopItemClass item = _items.get(i);
                List<String> lore = new ArrayList<String>();
                if(item.know)
                {
                    lore.add("§8Type : §d"+item.type);
                    lore.add("§8Nécessite : §6"+item.scrap+"§8/§6"+_craftableItems[0]);
                    lore.add("§8férailles, §6"+item.gunPowder+"§8/§6"+_craftableItems[1]+"§8.");
                }
                else
                {
                    lore.add("§8Type : §d"+item.type+"§8, Nécessite : §a"+item.recipeName);
                    lore.add("§6"+item.scrap+"§8/§6"+_craftableItems[0]+"§8férailles, §6");
                    lore.add("§6"+item.gunPowder+"§8/§6"+_craftableItems[1]+"§8.");
                }
                _inv.setItem(i+19, setItemMetaLore(item.mat, item.itemName, item.dura, lore));
            }
        }


    }

    public void openCraftMenu(Player p)
    {
        int[] craftItems = getCraftItems(p);
        int page = 1;
        int nbrItems = getNbrOfShowingItems(p.getUniqueId().toString(), craftItems[0], craftItems[1]);
        ArrayList<workshopItemClass> items = getAllCraftableItems(p.getUniqueId().toString(), craftItems[0], craftItems[1]);
        while(nbrItems > 20)
        {
            nbrItems -= 20;
            page++;
        }
        Inventory craftMenu = Bukkit.createInventory(null, 54, "Atelier (1/"+page+")");
        setItemsInv(craftMenu, craftItems, getAllCraftableItems(p.getUniqueId().toString(), craftItems[0], craftItems[1]), 1, page);
        p.openInventory(craftMenu);
    }
}
