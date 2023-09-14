package fr.byxis.player.workshop;

import fr.byxis.db.DbConnection;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.fireland.utilities.PermissionUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static fr.byxis.player.quest.QuestManager.actualiseCraftProgress;

public class workshopFunction {

    final private Fireland main;
    public Player sender;

    public workshopFunction(Fireland main, Player sender)
    {
        //Récupération du main, pour pouvoir avoir envoyer des requêtes à la base de données
        this.main = main;
        //Récupération de la personne qui envoie la commande, pour lui envoyer les messages d'erreurs
        this.sender = sender;
    }

    public int getInvPageMax(InventoryView _title)
    {
        char c= _title.getTitle().charAt(11);
        int i;
        if(String.valueOf(c).equals("/"))
        {
            String s = new StringBuilder().append(_title.getTitle().charAt(12)).append(_title.getTitle().charAt(13)).toString();
            i = Integer.parseInt(s);
        }
        else
        {
            i = Integer.parseInt(String.valueOf(_title.getTitle().charAt(11)));
        }
        return i;
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

    public int getItemCurrentPage(ItemStack _is)
    {
        char c= _is.getItemMeta().getDisplayName().charAt(4);
        int i;
        if(!String.valueOf(c).equals("/"))
        {
            String s = new StringBuilder().append(_is.getItemMeta().getDisplayName().charAt(3)).append(_is.getItemMeta().getDisplayName().charAt(4)).toString();
            i = Integer.parseInt(s);
        }
        else
        {
            i = Integer.parseInt(String.valueOf(_is.getItemMeta().getDisplayName().charAt(3)));
        }
        return i;
    }

    public int getItemMaxPage(ItemStack _is)
    {
        char c= _is.getItemMeta().getDisplayName().charAt(4);
        int i;
        if(!String.valueOf(c).equals("/"))
        {
            String s = new StringBuilder().append(_is.getItemMeta().getDisplayName().charAt(3)).append(_is.getItemMeta().getDisplayName().charAt(4)).toString();
            i = Integer.parseInt(s);
        }
        else
        {
            i = Integer.parseInt(String.valueOf(_is.getItemMeta().getDisplayName().charAt(3)));
        }
        return i;
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
        actualiseCraftProgress(Bukkit.getPlayer(UUID.fromString(_uuid)), _amount);
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
                    craftedTimeToLearn = 5;
                }
                else if(i.equalsIgnoreCase("D"))
                {
                    craftedTimeToLearn = 3;
                }
                else if(i.equalsIgnoreCase("E"))
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

            final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT player_workshop.know, workshop_recipes.name, workshop_recipes.scrap, workshop_recipes.gunpowder, workshop_recipes.type, items.command, items.item_name, items.item, items.durability, items.custom_model_data FROM workshop_recipes INNER JOIN player_workshop, items WHERE player_workshop.recipe_name = workshop_recipes.name AND workshop_recipes.name = items.recipe_name AND player_workshop.player_uuid = ? ORDER BY type, scrap DESC, gunpowder ASC");
            preparedStatement1.setString(1, _uuid);
            final ResultSet resultSet = preparedStatement1.executeQuery();
            //On vérifie s'il y a un résultat à la requête
            while (resultSet.next()) {
                if(hasPlan(p, resultSet.getString(2)) || resultSet.getBoolean(1))
                {
                    workshopItemClass item = new workshopItemClass(resultSet.getString(2), resultSet.getString(7), resultSet.getString(5), resultSet.getInt(3), resultSet.getInt(4), Material.getMaterial(resultSet.getString(8)), (short) resultSet.getInt(9), resultSet.getString(6), resultSet.getBoolean(1), resultSet.getInt(10));
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
            final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT player_workshop.know, workshop_recipes.name, workshop_recipes.scrap, workshop_recipes.gunpowder, workshop_recipes.type, items.command, items.item_name, items.item, items.durability, items.custom_model_data FROM workshop_recipes INNER JOIN player_workshop, items WHERE player_workshop.recipe_name = workshop_recipes.name AND workshop_recipes.name = items.recipe_name AND items.item_name = ? AND player_workshop.player_uuid = ?");
            preparedStatement1.setString(1, _recipeName);
            preparedStatement1.setString(2, _uuid);
            final ResultSet resultSet = preparedStatement1.executeQuery();
            //On vérifie s'il y a un résultat à la requête

            if(resultSet.next()) {
                if((resultSet.getInt(3) <= _scrapAmount && resultSet.getInt(4) <= _gunpowderAmount && hasPlan(p, resultSet.getString(2))) || resultSet.getBoolean(1))
                {
                    item = new workshopItemClass(resultSet.getString(2), resultSet.getString(7), resultSet.getString(5), resultSet.getInt(3), resultSet.getInt(4), Material.getMaterial(resultSet.getString(8)), (short) resultSet.getInt(9), resultSet.getString(6), resultSet.getBoolean(1), resultSet.getInt(10));
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
            if(item.getItemMeta().getDisplayName().contains(recipe_name))
            {
                return true;
            }
        }
        return false;
    }


    public void setItemsGuiInv(Inventory _inv, int[] _craftableItems, ArrayList<workshopItemClass> _items, int _currentPage, int _pageMax)
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
            _inv.setItem(spot+i, InventoryUtilities.setItemCustomModelData(InventoryUtilities.setItemMetaLore(item.mat, "§r§7"+item.itemName, item.dura, lore),item.customModelData));
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
        ArrayList<workshopItemClass> items = getAllCraftableItems(p, p.getUniqueId().toString());
        int nbrItems = items.size();
        while(nbrItems > 14)
        {
            nbrItems -= 14;
            maxPage++;
        }
        Inventory craftMenu = Bukkit.createInventory(null, 54, "Atelier ("+page+"/"+maxPage+")");
        setItemsGuiInv(craftMenu, craftItems, getAllCraftableItems(p, p.getUniqueId().toString()), page, maxPage);
        p.openInventory(craftMenu);
    }

    public void craftItem(Player p, workshopItemClass item)
    {
        int[] craftItems = getCraftItems(p);
        if(craftItems[0] >= item.scrap && craftItems[1] >= item.gunPowder)
        {
            if(hasPlan(p, item.recipeName) || item.know)
            {
                double reduction = 0;
                if(p.hasPermission("fireland.workshop.reduction.15"))
                {
                    reduction = 0.15;
                }
                else if(p.hasPermission("fireland.workshop.reduction.10"))
                {
                    reduction = 0.10;
                }
                else if(p.hasPermission("fireland.workshop.reduction.5"))
                {
                    reduction = 0.05;
                }
                if(addItemToCraft(p.getUniqueId().toString(), item, reduction))
                {
                    InGameUtilities.playPlayerSound(p, "block.anvil.use", SoundCategory.BLOCKS, 1, 0);
                    InGameUtilities.playPlayerSound(p, "gun.hud.scraps", SoundCategory.BLOCKS, 1, 2);
                    removeItemsOnInventoryOfPlayer(p, Material.NETHERITE_SCRAP, item.scrap);
                    removeItemsOnInventoryOfPlayer(p, Material.GUNPOWDER, item.gunPowder);
                    p.sendMessage("§aVous avez craft §6"+item.itemName+"§a !");
                /*
                PermissionUtilities.commandExecutor(p, item.command, "crackshot.give.all");
                craftItemNbr(item.recipeName, p.getUniqueId().toString(), 1);*/
                }


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
                temp = temp.replaceAll("[^a-zA-Z0-9-]", " ");
                if(!temp.equals(words[i]))
                {
                    break;
                }
                if(i+1 < words.length)
                {
                    String temp2 = words[i+1];
                    temp2 = temp2.replaceAll("[^a-zA-Z0-9-]", " ");
                    if(temp2.equals(words[i+1]))
                    {
                        sb.append(words[i]).append("_");
                    }
                    else
                    {
                        sb.append(words[i]);
                    }
                }
                else
                {
                    sb.append(words[i]);
                }

            }
            name = sb.toString().trim();
//ws newrecipe nom type scrap gp nomitem mat dura    cmd
            PermissionUtilities.commandExecutor(p, "ws newrecipe Plan_de_fabrication_de_"+name+" "+_type+" "+_scrap+" "+poudre_canon+" "+name+" "+item.getType()+" "+item.getItemMeta().getCustomModelData()+" "+_command, "fireland.workshop.a:newrecipe");
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


    public boolean addItemToCraft(String _uuid, workshopItemClass item, double reduction)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement1 = connection.prepareStatement("INSERT INTO player_crafting(player_uuid, item, creation_date, finish_date, is_breakable) VALUES(?,?,?,?,?)");
            final long time = System.currentTimeMillis();
            Timestamp currentTime = new Timestamp(time);
            long timeAdded = GetTimeFromType(item.type);

            timeAdded *= (1-reduction);

            Timestamp finishTime = new Timestamp(time+timeAdded);
            preparedStatement1.setString(1, _uuid);
            preparedStatement1.setString(2, item.itemName);
            preparedStatement1.setTimestamp(3, currentTime);
            preparedStatement1.setTimestamp(4, finishTime);
            preparedStatement1.setBoolean(5, true);
            preparedStatement1.executeUpdate();
            return true;
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cVous êtes en cooldown ! Veuillez espacer vos crafts d'au moins 1 seconde.");
            e.printStackTrace();
        }
        return false;
    }

    public int GetTimeFromType(String _type)
    {
        int time = 0;
        if(_type.equals("A"))
        {
            time = 24*60*60*1000;
        }
        else if(_type.equals("B"))
        {
            time = 3*60*60*1000;
        }
        else if(_type.equals("C"))
        {
            time = 60*60*1000;
        }
        else if(_type.equals("D"))
        {
            time = 30*60*1000;
        }
        if(_type.equals("E"))
        {
            time = 5*60*1000;
        }
        return time;
    }

    public void setItemsCraftingInv(Inventory _inv, ArrayList<workshopCraftingItemClass> _items, int _currentPage, int _pageMax)
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
        int spot = 19-(_currentPage * 14)+14;
        for (int i = (_currentPage * 14)-14; i < _items.size() && i < _currentPage * 14; i++)
        {
            if(spot+i == 26)
            {
                spot+=2;
            }
            workshopCraftingItemClass item = _items.get(i);
            List<String> lore = new ArrayList<>();
            if(item.finishDate.before(new Timestamp(System.currentTimeMillis())))
            {
                lore.add("§8Type : §d"+item.type +"§8, §a§lvotre item est prêt §r§8!");
            }
            else
            {
                lore.add("§8Type : §d"+item.type +"§8, reste §c"+ BasicUtilities.getStringTimeHour(item.finishDate.getTime() - System.currentTimeMillis()));
            }

            lore.add("§8Date de fin de création : "+item.finishDate);
            lore.add("§8Date de création : "+item.creationDate);
            _inv.setItem(spot+i, InventoryUtilities.setItemCustomModelData(InventoryUtilities.setItemMetaLore(item.mat, "§r§7"+item.itemName, item.dura, lore), item.customModelData));
        }
    }


    public void openCraftingMenu(Player p, int page)
    {
        int maxPage = 1;
        int nbrItems = getNbrOfCraftingItem(p.getUniqueId().toString());
        ArrayList<workshopItemClass> items = getAllCraftableItems(p, p.getUniqueId().toString());
        while(nbrItems > 14)
        {
            nbrItems -= 14;
            maxPage++;
        }
        Inventory craftMenu = Bukkit.createInventory(null, 54, "Attente ("+page+"/"+maxPage+")");
        setItemsCraftingInv(craftMenu, getAllCraftingItems(p, p.getUniqueId().toString()), page, maxPage);
        p.openInventory(craftMenu);
    }

    public int getNbrOfCraftingItem(String _uuid)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

        int nbr = 0;
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM player_crafting WHERE player_uuid = ?");
            preparedStatement.setString(1, _uuid);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next())
            {
                nbr = rs.getInt(1);
            }
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W012");
            e.printStackTrace();
        }
        return nbr;
    }

    public ArrayList<workshopCraftingItemClass> getAllCraftingItems(Player p, String _uuid)
    {
        ArrayList<workshopCraftingItemClass> items = new ArrayList<>();
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {

            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT player_crafting.item, workshop_recipes.type, items.item, items.durability, items.command, player_crafting.creation_date, player_crafting.finish_date, items.custom_model_data, items.recipe_name \n" +
                    "FROM player_crafting INNER JOIN items, workshop_recipes \n" +
                    "WHERE items.recipe_name = workshop_recipes.name \n" +
                    "AND player_crafting.item = items.item_name\n" +
                    "AND player_crafting.player_uuid = ? ORDER BY workshop_recipes.type, timestamp(player_crafting.creation_date);");
            preparedStatement1.setString(1, _uuid);
            final ResultSet resultSet = preparedStatement1.executeQuery();
            //On vérifie s'il y a un résultat à la requête
            while (resultSet.next()) {
                workshopCraftingItemClass item = new workshopCraftingItemClass(resultSet.getString(1), resultSet.getString(2), Material.getMaterial(resultSet.getString(3)), (short) resultSet.getInt(4), resultSet.getString(5), resultSet.getTimestamp(6), resultSet.getTimestamp(7), resultSet.getInt(8), resultSet.getString(9));
                items.add(item);
            }
            return items;
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W013");
            e.printStackTrace();
        }
        return items;
    }

    public void removeFromQueue(workshopCraftingItemClass _itm, String _uuid)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {

            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement1 = connection.prepareStatement("" +
                    "DELETE FROM player_crafting" +
                    " WHERE player_uuid = ?" +
                    " AND creation_date = '"+_itm.creationDate+"';");
            preparedStatement1.setString(1, _uuid);
            preparedStatement1.executeUpdate();
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W014");
            e.printStackTrace();
        }
    }

    public void setUnbreakable(workshopCraftingItemClass _itm, String _uuid)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {

            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement1 = connection.prepareStatement("" +
                    "UPDATE player_crafting" +
                    " SET is_breakable = ?" +
                    " WHERE player_uuid = ?" +
                    " AND creation_date = '"+_itm.creationDate+"';");
            preparedStatement1.setBoolean(1, false);
            preparedStatement1.setString(2, _uuid);
            preparedStatement1.executeUpdate();
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W015");
            e.printStackTrace();
        }
    }

    public void removeTime(workshopCraftingItemClass _itm, String _uuid)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {

            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement = connection.prepareStatement("" +
                    "SELECT finish_date" +
                    " FROM player_crafting" +
                    " WHERE player_uuid = ?" +
                    " AND creation_date = ?;");
            preparedStatement.setString(1, _uuid);
            preparedStatement.setTimestamp(2, _itm.creationDate);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next())
            {
                final PreparedStatement init = connection.prepareStatement("" +
                        "UPDATE player_crafting" +
                        " SET finish_date = ?" +
                        " WHERE player_uuid = ?" +
                        " AND creation_date = ?;");
                Timestamp t = new Timestamp(rs.getTimestamp(1).getTime());
                init.setTimestamp(1, t);
                init.setString(2, _uuid);
                init.setTimestamp(3, _itm.creationDate);
                init.executeUpdate();

                final PreparedStatement preparedStatement1 = connection.prepareStatement("" +
                        "UPDATE player_crafting" +
                        " SET finish_date = ?" +
                        " WHERE player_uuid = ?" +
                        " AND creation_date = ?;");
                Timestamp t1 = new Timestamp(rs.getTimestamp(1).getTime() - 1000*60*30);
                preparedStatement1.setTimestamp(1, t1);
                preparedStatement1.setString(2, _uuid);
                preparedStatement1.setTimestamp(3, _itm.creationDate);
                preparedStatement1.executeUpdate();
            }

        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W016");
            e.printStackTrace();
        }
    }

    public boolean isBreakable(workshopCraftingItemClass _itm, String _uuid)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {

            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement1 = connection.prepareStatement("" +
                    "SELECT is_breakable" +
                    " FROM player_crafting" +
                    " WHERE player_uuid = ?" +
                    " AND creation_date = ?;");
            preparedStatement1.setString(1, _uuid);
            preparedStatement1.setTimestamp(2, _itm.creationDate);
            ResultSet rs = preparedStatement1.executeQuery();
            if(rs.next())
            {
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W016");
            e.printStackTrace();
        }
        return false;
    }

    public int getNbrOfItemCrafting(String _uuid)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {

            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement1 = connection.prepareStatement("" +
                    "SELECT COUNT(*)" +
                    " FROM player_crafting" +
                    " WHERE player_uuid = ?;");
            preparedStatement1.setString(1, _uuid);
            ResultSet rs = preparedStatement1.executeQuery();
            if(rs.next())
            {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W016");
            e.printStackTrace();
        }
        return 0;
    }

    public void openWorkshop(Player p)
    {
        InGameUtilities.playPlayerSound(p, "gun.hud.scraps", SoundCategory.AMBIENT, 1, 1);
        Inventory craftMenu = Bukkit.createInventory(null, 9*3, "Plan de travail");
        craftMenu.setItem(11, InventoryUtilities.setItemMeta(Material.ANVIL, "§6Atelier", (short) 1));
        craftMenu.setItem(13, InventoryUtilities.setItemMeta(Material.NETHERITE_SCRAP, "§aRecyclage", (short) 1));
        craftMenu.setItem(15, InventoryUtilities.setItemMeta(Material.CHEST, "§6Création", (short) 1));
        p.openInventory(craftMenu);
    }

    public void getPlan(Player p, String itemName)
    {
        if(p.getInventory().firstEmpty() == -1)
        {
            p.sendMessage("§cVous n'avez pas assez de place.");
            return;
        }
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT recipe_name, workshop_recipes.type " +
                    "FROM items INNER JOIN workshop_recipes" +
                    " ON items.recipe_name = workshop_recipes.name WHERE items.item_name LIKE ?");
            preparedStatement.setString(1, itemName+"%");
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next())
            {
                ItemStack i;
                if(rs.getString(2).equals("E"))
                {
                    i = InventoryUtilities.setItemMeta(Material.PAPER, "§r§a"+rs.getString(1), (short) 1);
                    ItemMeta itemMeta=i.getItemMeta();
                    itemMeta.setCustomModelData(1);
                    i.setItemMeta(itemMeta);
                }
                else if(rs.getString(2).equals("D"))
                {
                    i = InventoryUtilities.setItemMeta(Material.PAPER, "§r§9"+rs.getString(1), (short) 1);
                    ItemMeta itemMeta=i.getItemMeta();
                    itemMeta.setCustomModelData(2);
                    i.setItemMeta(itemMeta);
                }
                else if(rs.getString(2).equals("C"))
                {
                    i = InventoryUtilities.setItemMeta(Material.PAPER, "§r§c"+rs.getString(1), (short) 1);
                    ItemMeta itemMeta=i.getItemMeta();
                    itemMeta.setCustomModelData(3);
                    i.setItemMeta(itemMeta);
                }
                else if(rs.getString(2).equals("B"))
                {
                    i = InventoryUtilities.setItemMeta(Material.PAPER, "§r§e"+rs.getString(1), (short) 1);
                    ItemMeta itemMeta=i.getItemMeta();
                    itemMeta.setCustomModelData(4);
                    i.setItemMeta(itemMeta);
                }
                else
                {
                    i = InventoryUtilities.setItemMeta(Material.PAPER, "§r§6§l"+rs.getString(1), (short) 1);
                    ItemMeta itemMeta=i.getItemMeta();
                    itemMeta.setCustomModelData(5);
                    i.setItemMeta(itemMeta);
                }

                ItemMeta im = i.getItemMeta();
                List<String> lore = new ArrayList<>();
                lore.add("§8Ce plan vous permet d'apprendre à craft un §6item§8.");
                lore.add("§8Gardez le pour §6craft§8, Faites clic droit avec le plan ");
                lore.add("§8pour l'§6apprendre§8 !");

                im.setLore(lore);
                i.setItemMeta(im);
                p.sendMessage("§aVous avez obtenu le plan de fabrication : "+rs.getString(1));
                p.getInventory().addItem(i);
                return;
            }
            else
            {
                p.sendMessage("§cAucun plan n'a été trouvé avec comme nom d'item "+itemName);
            }
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W012");
            e.printStackTrace();
        }
    }
    public void forgetAllPlans(String _uuid)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {

            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement1 = connection.prepareStatement("" +
                    "DELETE FROM player_workshop" +
                    " WHERE player_uuid = ?");
            preparedStatement1.setString(1, _uuid);
            preparedStatement1.executeUpdate();
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #W019");
            e.printStackTrace();
        }
    }
}
