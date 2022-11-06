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


    public void createRecipe(String _name, String _command, String _type, Integer _scrap, Integer _gunpowder, String _itemName, String _mat, int _durability)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            //On prépare la requête SQL
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO workshop_recipes (name, type, scrap, gunpowder) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, _name);
            preparedStatement.setString(2, _type);
            preparedStatement.setInt(3, _scrap);
            preparedStatement.setInt(4, _gunpowder);


            //On execute la requête
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W001");
            e.printStackTrace();
        }

        try
        {

            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatementBis = connection.prepareStatement("INSERT INTO items (item_name, recipe_name, item, durability, command) VALUES (?, ?, ?, ?, ?)");
            preparedStatementBis.setString(1, _itemName);
            preparedStatementBis.setString(2, _name);
            preparedStatementBis.setString(3, _mat);
            preparedStatementBis.setInt(4, _durability);
            preparedStatementBis.setString(5, _command);

            preparedStatementBis.executeUpdate();
        } catch(SQLException e)
        {
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème. Il peut s'agir du fait qu'un item à déjà été créé.  Erreur : #W009");
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
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT player_workshop.crafted_time FROM player_workshop WHERE player_uuid = ? AND recipe_name = ?");
            preparedStatement.setString(1, _uuid);
            preparedStatement.setString(2, _recipeName);

            //On execute la requête
            ResultSet rs = preparedStatement.executeQuery();

            if(rs.next())
            {
                final PreparedStatement preparedStatement4 = connection.prepareStatement("UPDATE player_workshop SET crafted_time =? WHERE player_uuid = ? AND recipe_name = ?");
                preparedStatement4.setInt(1, rs.getInt(1)+_amount);
                preparedStatement4.setString(2, _uuid);
                preparedStatement4.setString(3, _recipeName);

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
                String i = resultSet.getString(1);
                if(i.equalsIgnoreCase("A"))
                {
                    craftedTimeToLearn = 20;
                }
                else if(i.equalsIgnoreCase("B"))
                {
                    craftedTimeToLearn = 10;
                }
                else if(i.equalsIgnoreCase("C"))
                {
                    craftedTimeToLearn = 3;
                }
                else if(i.equalsIgnoreCase("D"))
                {
                    craftedTimeToLearn = 1;
                }
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
                int i = resultSet.getInt(1);
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

    public ArrayList<workshopItemClass> getAllCraftableItems(Player p, String _uuid)
    {
        ArrayList<workshopItemClass> items = new ArrayList<>();
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        initPlayerRecipe(p.getUniqueId().toString());
        try {

            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT player_workshop.know, workshop_recipes.name, workshop_recipes.scrap, workshop_recipes.gunpowder, workshop_recipes.type, items.command, items.item_name, items.item, items.durability FROM workshop_recipes INNER JOIN player_workshop, items WHERE player_workshop.recipe_name = workshop_recipes.name AND workshop_recipes.name = items.recipe_name AND player_workshop.player_uuid = ? ORDER BY type, scrap DESC, gunpowder ASC");
            preparedStatement1.setString(1, _uuid);
            final ResultSet resultSet = preparedStatement1.executeQuery();
            //On vérifie s'il y a un résultat à la requête
            while (resultSet.next()) {
                if(hasPlan(p, resultSet.getString(2)) || resultSet.getBoolean(1))
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

    public workshopItemClass getACraftableItem(Player p, String _uuid, int _scrapAmount, int _gunpowderAmount, String _recipeName)
    {
        workshopItemClass item = null;
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT player_workshop.know, workshop_recipes.name, workshop_recipes.scrap, workshop_recipes.gunpowder, workshop_recipes.type, items.command, items.item_name, items.item, items.durability FROM workshop_recipes INNER JOIN player_workshop, items WHERE player_workshop.recipe_name = workshop_recipes.name AND workshop_recipes.name = items.recipe_name AND items.item_name = ? AND player_workshop.player_uuid = ?");
            preparedStatement1.setString(1, _recipeName);
            preparedStatement1.setString(2, _uuid);
            final ResultSet resultSet = preparedStatement1.executeQuery();
            //On vérifie s'il y a un résultat à la requête

            while(resultSet.next()) {
                if((resultSet.getInt(3) <= _scrapAmount && resultSet.getInt(4) <= _gunpowderAmount && hasPlan(p, resultSet.getString(2))) || resultSet.getBoolean(1))
                {
                    item = new workshopItemClass(resultSet.getString(2), resultSet.getString(7), resultSet.getString(5), resultSet.getInt(3), resultSet.getInt(4), Material.getMaterial(resultSet.getString(8)), (short) resultSet.getInt(9), resultSet.getString(6), resultSet.getBoolean(1));
                    return item;
                }
            }
            return item;
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W008");
            e.printStackTrace();
        }
        return item;
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
                    scrap+=s.getAmount();
                }
                if(s.getType() == Material.GUNPOWDER)
                {
                    gunpowder+=s.getAmount();
                }
            }
        }
        return new int[]{scrap, gunpowder};
    }

    public ArrayList<ItemStack> getPlans(Player p)
    {
        ArrayList<ItemStack> items = new ArrayList<>();
        for (ItemStack s : p.getInventory().getContents())
        {
            if(s != null)
            {
                if(s.getType() == Material.PAPER && s.getItemMeta().getDisplayName().contains("Plan de"))
                {
                    items.add(s);
                }
            }
        }
        return items;
    }

    public boolean hasPlan(Player p, String recipe_name)
    {
        ArrayList<ItemStack> plans = getPlans(p);
        for (ItemStack item : plans)
        {
            if(item.getItemMeta().getDisplayName().equalsIgnoreCase(recipe_name))
            {
                return true;
            }
        }
        return false;
    }

    public ItemStack setItemMeta(Material mat, String name, short dura) {
        ItemStack item = new ItemStack(mat);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        item.setDurability(dura);
        return item;
    }
    public ItemStack setItemMetaLore(Material mat, String name, short dura, List<String> lore) {
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
        int spot = 19-(_currentPage * 14)+14;
        for (int i = (_currentPage * 14)-14; i < _items.size() && i < _currentPage * 14; i++)
        {
            if(spot+i == 26)
            {
                spot+=2;
            }
            workshopItemClass item = _items.get(i);
            List<String> lore = new ArrayList<>();
            if(item.know)
            {
                lore.add("§8Type : §d"+item.type +" §a(Plan connu)");
                lore.add("§8Nécessite : §6"+_craftableItems[0]+"§8/§6"+item.scrap+"§8férailles,");
                lore.add("§6"+_craftableItems[1]+"§8/§6"+item.gunPowder+"§8 poudre à canon.");
            }
            else
            {
                lore.add("§8Type : §d"+item.type+"§8, Nécessite : §c"+item.recipeName);
                lore.add("§6"+_craftableItems[0]+"§8/§6"+item.scrap+"§8férailles, §6");
                lore.add("§6"+_craftableItems[1]+"§8/§6"+item.gunPowder+"§8 poudre à canon.");
            }
            _inv.setItem(spot+i, setItemMetaLore(item.mat, "§r"+item.itemName, item.dura, lore));
        }


    }

    public void removeItemsOnInventoryOfPlayer(Player p, Material mat, int amount)
    {
        int toRemove = amount;
        for(ItemStack item : p.getInventory().getContents())
        {
            if(item != null)
            {
                if(item.getType() == mat)
                {
                    if(item.getAmount() > toRemove)
                    {
                        item.setAmount(item.getAmount()-toRemove);
                        return;
                    }
                    else
                    {
                        toRemove -= item.getAmount();
                        item.setAmount(0);
                    }
                }
            }
        }
    }

    public void openCraftMenu(Player p, int page)
    {
        int[] craftItems = getCraftItems(p);
        int maxPage = 1;
        int nbrItems = getNbrOfShowingItems(p.getUniqueId().toString(), craftItems[0], craftItems[1]);
        ArrayList<workshopItemClass> items = getAllCraftableItems(p, p.getUniqueId().toString());
        while(nbrItems > 14)
        {
            nbrItems -= 14;
            maxPage++;
        }
        Inventory craftMenu = Bukkit.createInventory(null, 54, "Atelier ("+page+"/"+maxPage+")");
        setItemsInv(craftMenu, craftItems, getAllCraftableItems(p, p.getUniqueId().toString()), page, maxPage);
        p.openInventory(craftMenu);
    }

    public void craftItem(Player p, workshopItemClass item)
    {
        int[] craftItems = getCraftItems(p);
        if(craftItems[0] >= item.scrap && craftItems[1] >= item.gunPowder)
        {
            if(hasPlan(p, item.recipeName) || item.know)
            {
                p.playSound(p.getLocation(), "minecraft:block.anvil.use", 1, 1);
                removeItemsOnInventoryOfPlayer(p, Material.NETHERITE_SCRAP, item.scrap);
                removeItemsOnInventoryOfPlayer(p, Material.GUNPOWDER, item.gunPowder);
                p.sendMessage("§aVous avez craft §6"+item.itemName+"§a !");
                main.commandExecutor(p, item.command, "crackshot.give.all");
                craftItemNbr(item.recipeName, p.getUniqueId().toString(), 1);
                return;
            }
            p.sendMessage("§cVous n'avez pas le plan.");
        }
        else
        {
            p.sendMessage("§cVous n'avez pas assez de ferraille/poudre à canon !");
        }
    }

    public void saveNewItem(Player p, String _type, int _scrap, int poudre_canon, String _command)
    {//ws newrecipe a:NomRecette Type scrap canon a:Itemname a:materiel a:durability commande
        if(p.getItemInHand().getType() != Material.AIR)
        {
            ItemStack item = p.getItemInHand();
            String name = item.getItemMeta().getDisplayName();
            name = name.replaceAll("§7", "");
            String[] words = name.split(" ");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < words.length; i++){
                String temp = words[i];
                temp = temp.replaceAll("[^a-zA-Z0-9]", " ");
                if(!temp.equals(words[i]))
                {
                    break;
                }
                String temp2 = words[i+1];
                temp2 = temp2.replaceAll("[^a-zA-Z0-9]", " ");
                if(temp2.equals(words[i+1]))
                {
                    sb.append(words[i]).append("_");
                }
                else
                {
                    sb.append(words[i]);
                }
            }
            name = sb.toString().trim();
//ws newrecipe nom type scrap gp nomitem mat dura    cmd
            main.commandExecutor(p, "ws newrecipe Plan_de_fabrication_de_"+name+" "+_type+" "+_scrap+" "+poudre_canon+" "+name+" "+item.getType()+" "+item.getDurability()+" "+_command, "fireland.workshop.a:newrecipe");
        }
    }

    public void initPlayerRecipe(String _uuid)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT workshop_recipes.name FROM workshop_recipes\n" +
                    "WHERE workshop_recipes.name NOT IN (SELECT player_workshop.recipe_name FROM player_workshop WHERE player_workshop.player_uuid = ?);");
            preparedStatement1.setString(1, _uuid);
            final ResultSet resultSet = preparedStatement1.executeQuery();
            //On vérifie s'il y a un résultat à la requête

            while(resultSet.next()) {
                final PreparedStatement preparedStatementbis = connection.prepareStatement("INSERT INTO player_workshop(player_uuid, recipe_name, crafted_time, know)\n" +
                        "VALUES (?,?,0,0)");
                preparedStatementbis.setString(1, _uuid);
                preparedStatementbis.setString(2, resultSet.getString(1));
                preparedStatementbis.executeUpdate();
            }
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W010");
            e.printStackTrace();
        }
    }
}
