package fr.byxis.workshop;

import fr.byxis.db.DbConnection;
import fr.byxis.main.Main;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F004");
            e.printStackTrace();
        }
    }

    public void learnRecipe(String _recipeName, String _uuid, int _amount)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {

            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT player_workshop.crafted_time FROM player_workshop INNER JOIN player ON player_workshop.player_uuid = players.uuid WHERE player_workshop.player_uuid = ? AND player_workshop.recipe_name = ?");
            preparedStatement1.setString(1, _uuid);
            preparedStatement1.setString(2, _recipeName);

            final ResultSet resultSet = preparedStatement1.executeQuery();
            int crafted_time = 0;
            //On vérifie s'il y a un résultat à la requête
            if (resultSet.next())
            {
                crafted_time = resultSet.getInt(0);
            }
            final PreparedStatement preparedStatement4 = connection.prepareStatement("UPDATE player_workshop SET crafted_time =? WHERE player_uuid = ? AND recipe_name = ?");
            preparedStatement4.setInt(1, crafted_time+_amount);
            preparedStatement4.setString(2, _uuid);
            preparedStatement4.setString(3, _recipeName);

            //On execute la requête
            preparedStatement4.executeUpdate();
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F004");
            e.printStackTrace();
        }
    }


}
