package fr.byxis.jeton;

import fr.byxis.db.DbConnection;
import fr.byxis.main.Main;
import org.bukkit.entity.Player;

import java.sql.*;

public class jetonSql {

    private Main main;
    private Player sender;

    public jetonSql(Main m, Player p)
    {
        this.main = m;
        this.sender = p;
    }

    public boolean createFacture(String _uuid, int _amount, String _desc)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {

            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement = connection.prepareStatement("" +
                    "SELECT MAX(number)" +
                    " FROM jeton_history");
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next())
            {
                final PreparedStatement preparedStatement1 = connection.prepareStatement("" +
                        "SELECT number, amount" +
                        " FROM jeton_history" +
                        " WHERE description = ?" +
                        " AND player_uuid = ?");
                preparedStatement1.setString(1, _desc);
                preparedStatement1.setString(2, _uuid);
                ResultSet rs1 = preparedStatement1.executeQuery();
                if(rs1.next())
                {
                    return updateFactureToDb(rs1.getInt(1), rs1.getInt(2)+_amount);
                }
                return addFactureToDb(_uuid, rs.getInt(1)+1, _amount, _desc);
            }
        } catch (SQLException e) {
            //Une erreur est survenue (Problème de connexion à la BD)
            sender.sendMessage("§cUne erreur est survenue lors de la création de la facture. Merci de contacter le staff pour résoudre ce problème.");
            e.printStackTrace();
        }
        return false;
    }

    private boolean addFactureToDb(String _uuid, int number, int _amount, String _desc)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement2 = connection.prepareStatement("" +
                    "INSERT INTO jeton_history(number, player_uuid, amount, date, description)" +
                    " VALUES(?,?,?,?,?)");
            preparedStatement2.setInt(1, number);
            preparedStatement2.setString(2, _uuid);
            preparedStatement2.setInt(3, _amount);
            preparedStatement2.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            preparedStatement2.setString(5, _desc);
            preparedStatement2.executeUpdate();
            sender.sendMessage("§aFacture #"+number+" crée !");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean updateFactureToDb(int number, int _amount)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();

            final PreparedStatement preparedStatement2 = connection.prepareStatement("" +
                    "UPDATE jeton_history" +
                    " SET amount = ?" +
                    " WHERE number = ?");
            preparedStatement2.setInt(1, number);
            preparedStatement2.setInt(2, _amount);
            preparedStatement2.executeUpdate();
            sender.sendMessage("§aFacture #"+number+" mise à jour.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
