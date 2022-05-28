package fr.byxis.event;

import fr.byxis.db.DbConnection;
import fr.byxis.main.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.*;
import java.util.UUID;

public class join implements Listener {
	
	private final Main main;
	
	public join(Main main) {
		this.main = main;
	}

	@EventHandler
	public void PlayerJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();
		main.getConfig().set("door."+player.getName(), false);
		addOnDb(player);
		updateName(player);
	}
	
	private void addOnDb(Player p) 
	{
		final UUID uuid = p.getUniqueId();
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		
		try {
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT uuid FROM players WHERE uuid = ?");
			preparedStatement1.setString(1, uuid.toString());
			
			final ResultSet resultSet1 = preparedStatement1.executeQuery();
			
			if (resultSet1.next())
			{
				return;
			}
			else
			{
				final long time = System.currentTimeMillis(); 
				final PreparedStatement preparatedStatement = connection.prepareStatement("INSERT INTO players (uuid, name, first_joined) VALUES (?,?,?)");
				preparatedStatement.setString(1, uuid.toString());
				preparatedStatement.setString(2, p.getName());
				preparatedStatement.setTimestamp(3, new Timestamp(time));
				
				preparatedStatement.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void updateName(Player p)
	{
		final UUID uuid = p.getUniqueId();
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		
		try {
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET name = ? WHERE uuid = ?");
			preparedStatement.setString(1, p.getName());
			preparedStatement.setString(2, uuid.toString());
			
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
