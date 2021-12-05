package fr.byxis.faction;

import fr.byxis.db.DbConnection;
import fr.byxis.main.Main;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FactionFunctions {
	
	final private Main main;
	final private Player sender;

	public FactionFunctions(Main main, Player sender)
	{
		this.main = main;
		this.sender = sender;
	}

	public void factionInformationSender(Player p, FactionInformation infos, ArrayList<FactionPlayerInformation> players)
	{
		String leader = null;
		StringBuilder mod = new StringBuilder();
		StringBuilder members = new StringBuilder();
		for (FactionPlayerInformation player : players) {
			if (player.getRole() == 2) {
				leader = player.getName();
			} else if (player.getRole() == 1) {
				if (mod.toString().equals("")) {
					mod.append("§r").append(player.getName());
				} else {
					mod.append("§a, §r").append(player.getName());
				}

			} else if (player.getRole() == 0) {
				if (members.toString().equals("")) {
					members.append("§r").append(player.getName());
				} else {
					members.append("§a, §r").append(player.getName());
				}

			}

		}
		p.sendMessage("§a==");
		p.sendMessage("§aNom: §r"+infos.getName());
		p.sendMessage("§aDate de création: §r"+infos.getCreatedAt());
		p.sendMessage("§aNombre de membres: §r"+infos.getCurrentNbrOfPlayers()+"/"+infos.getMaxNbrOfPlayers());
		p.sendMessage("§aAmélioration actuelle: §r"+infos.getCurrentUpgrade());
		p.sendMessage("§aArgent: §r$"+ infos.getCurrentMoney()+"/"+infos.getMaxMoney());
		p.sendMessage("");
		p.sendMessage("§aChef:");
		assert leader != null;
		p.sendMessage(leader);
		p.sendMessage("§aModérateurs:");
		p.sendMessage(mod.toString());
		p.sendMessage("§aMembres:");
		p.sendMessage(members.toString());
		p.sendMessage("§a==");
	}
	
	public ArrayList<FactionPlayerInformation> getPlayersFromFaction(String factionName)
	{
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		
		try {
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT name,faction_role FROM players WHERE faction_name = ?");
			preparedStatement1.setString(1, factionName);
			
			final ResultSet resultSet = preparedStatement1.executeQuery();
			
			if (resultSet.next())
			{
				ArrayList<FactionPlayerInformation> ar = new ArrayList<>();
				FactionPlayerInformation player = new FactionPlayerInformation(resultSet.getString(1), factionName, (resultSet.getInt(2)));
				ar.add(player);
				while(resultSet.next())
				{
					player = new FactionPlayerInformation(resultSet.getString(1), factionName, (resultSet.getInt(2)));
					ar.add(player);
				}
				return ar;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème. Erreur : #F001");
		}
		return null;
	}
	
	public void InitInviteFaction(Player p, Player invited, String factionName) {
		final UUID uuid = invited.getUniqueId();
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		
		try {
			final Connection connection = firelandConnection.getConnection();

			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT available_time,faction_name FROM invite WHERE player_uuid = ? AND faction_name=?");
			preparedStatement1.setString(1, uuid.toString());
			preparedStatement1.setString(2, factionName);

			final ResultSet resultSet = preparedStatement1.executeQuery();
			if (resultSet.next())
			{
				final long time = System.currentTimeMillis();
				Timestamp currentTime = new Timestamp(time);
				if(resultSet.getTimestamp(1).after(currentTime))
				{
					p.sendMessage("§cCe joueur a déjà été récemment invité dans cette faction !");
				}
				else
				{
					final PreparedStatement preparedStatement2 = connection.prepareStatement("DELETE FROM invite WHERE player_uuid = ? AND faction_name=?");
					preparedStatement2.setString(1, uuid.toString());
					preparedStatement2.setString(2, factionName);
					preparedStatement2.executeUpdate();
					inviteFaction(connection, factionName, uuid);
					p.sendMessage("§a"+invited.getName()+" vient d'être invité dans la faction !");
					invited.sendMessage("§aVous avez été invité dans la faction "+factionName+" !");
				}

			}
			else
			{
				inviteFaction(connection, factionName, uuid);
				p.sendMessage("§a"+invited.getName()+" vient d'être invité dans la faction !");
				invited.sendMessage("§aVous avez été invité dans la faction "+factionName+" !");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F003");
		}
	}
	
	public void inviteFaction(Connection connection, String factionName, UUID uuid)
	{
		try {
			final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO invite (faction_name, player_uuid, available_time) VALUES (?, ?, ?)");
			preparedStatement.setString(1, factionName);
			preparedStatement.setString(2, uuid.toString());
			final long time = System.currentTimeMillis()  + TimeUnit.MINUTES.toMillis(20);
			preparedStatement.setTimestamp(3, new Timestamp(time));
			
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F004");
			e.printStackTrace();
		}
		
	}
	
	public boolean isInvitedToFaction(Player p, String name)
	{
		final UUID uuid = p.getUniqueId();
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		
		try {
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT available_time FROM invite WHERE player_uuid  = ? AND faction_name = ?");
			preparedStatement1.setString(1, uuid.toString());
			preparedStatement1.setString(2, name);
			
			final ResultSet resultSet = preparedStatement1.executeQuery();
			
			if (resultSet.next())
			{
				final long time = System.currentTimeMillis();
				return resultSet.getTimestamp(1).after(new Timestamp(time));
			}
			else
			{
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F005");
		}
		return false;
	}
	
	public String getExactFactionNameFromInvite(Player p, String name)
	{
		final UUID uuid = p.getUniqueId();
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		
		try {
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT faction_name FROM invite WHERE player_uuid  = ? AND faction_name = ?");
			preparedStatement1.setString(1, uuid.toString());
			preparedStatement1.setString(2, name);
			
			final ResultSet resultSet = preparedStatement1.executeQuery();
			
			if (resultSet.next())
			{
				return resultSet.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F006");
		}
		return "";
	}
	
	public void joinFaction(Player p, String name) {
		final UUID uuid = p.getUniqueId();
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		
		try {
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT faction_name FROM players WHERE uuid = ?");
			preparedStatement1.setString(1, uuid.toString());
			
			final ResultSet resultSet = preparedStatement1.executeQuery();
			
			if (resultSet.next())
			{
				final PreparedStatement preparedStatement2 = connection.prepareStatement("UPDATE players SET faction_name=?, faction_role=0, faction_joined_at=? WHERE uuid = ?");
				final long time = System.currentTimeMillis();
				preparedStatement2.setString(1, name);
				preparedStatement2.setTimestamp(2, new Timestamp(time));
				preparedStatement2.setString(3, uuid.toString());
				
				preparedStatement2.executeUpdate();
				p.sendMessage("§aVous avez rejoint la faction "+name+".");
				
				final PreparedStatement preparedStatement3 = connection.prepareStatement("SELECT nbr_members FROM faction WHERE name = ?");
				preparedStatement3.setString(1, name);
				final ResultSet resultSet2 = preparedStatement3.executeQuery();
				if(resultSet2.next())
				{
					final PreparedStatement preparedStatement4 = connection.prepareStatement("UPDATE faction SET nbr_members=? WHERE name = ?");
					preparedStatement4.setInt(1, resultSet2.getInt(1)+1);
					preparedStatement4.setString(2, name);
					preparedStatement4.executeQuery();
				}
				
			}
			else
			{
				p.sendMessage("§cVous êtes déjà dans une faction !");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F007");
		}
	}
	
	public void leaveFaction(Player p, UUID leader) {
		final UUID uuid = p.getUniqueId();
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		
		try {
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT name FROM faction WHERE leader_uuid = ?");
			preparedStatement1.setString(1, leader.toString());
			
			final ResultSet resultSet = preparedStatement1.executeQuery();
			
			if (resultSet.next())
			{
				p.sendMessage("§cVous êtes leader donc vous ne pouvez pas quitter votre faction ! Vous pouvez cependant la dissoudre ou donner le rôle a quelqu'un d'autre");
			}
			else
			{
				final PreparedStatement preparedStatement2 = connection.prepareStatement("UPDATE players SET faction_name=NULL, faction_role=NULL, faction_joined_at=NULL WHERE uuid = ?");
				preparedStatement2.setString(1, uuid.toString());
				
				preparedStatement2.executeUpdate();
				p.sendMessage("§cVous avez quitté la faction "+resultSet.getString(1)+".");
			}
		} catch (SQLException e) {
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F008");
		}
	}
	
	public void creatingFaction(Player p, String name)
	{
		
		final UUID uuid = p.getUniqueId();
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		
		try {
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT name FROM faction WHERE name = ?");//AND leader_uuid = ?
			preparedStatement1.setString(1, name);
			//preparedStatement.setString(2, uuid.toString());
			
			final ResultSet resultSet1 = preparedStatement1.executeQuery();
			
			if (resultSet1.next())
			{
				p.sendMessage("§cLe nom de cette faction est déjà pris !");
			}
			else
			{
				final PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT leader_uuid FROM faction WHERE leader_uuid = ?");
				preparedStatement2.setString(1, uuid.toString());
				final ResultSet resultSet2 = preparedStatement2.executeQuery();
				if(resultSet2.next())
				{
					p.sendMessage("§cVous êtes déjà dans une faction !");
				}
				else
				{
					createFaction(connection, uuid, name, p);
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F009");
		}
	}
	
	public void createFaction(Connection connection, UUID uuid, String name, Player p)
	{
		try {
			final PreparedStatement preparatedStatement = connection.prepareStatement("INSERT INTO faction (name, leader_uuid, created_at) VALUES (?, ?, ?)");
			final PreparedStatement preparatedStatementBis = connection.prepareStatement("UPDATE players SET faction_name=?, faction_role=?, faction_joined_at=? WHERE uuid = ?");
			final long time = System.currentTimeMillis();
			
			preparatedStatement.setString(1, name);
			preparatedStatement.setString(2, uuid.toString());
			preparatedStatement.setTimestamp(3, new Timestamp(time));
			preparatedStatementBis.setString(1, name);
			preparatedStatementBis.setInt(2, 2);
			preparatedStatementBis.setTimestamp(3, new Timestamp(time));
			preparatedStatementBis.setString(4, uuid.toString());
			
			preparatedStatement.executeUpdate();
			preparatedStatementBis.executeUpdate();
			main.getFaction().put(name, uuid);
			p.sendMessage("§aVous avez créé la faction "+name+" !");
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F010");
		}
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public void deleteFaction(Player p)
	{
		final UUID uuid = p.getUniqueId();
		
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		Connection connection;
		try {
			connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT name FROM faction WHERE leader_uuid = ?");
			preparedStatement1.setString(1, uuid.toString());
			
			final PreparedStatement preparedStatement2 = connection.prepareStatement("DELETE FROM faction WHERE leader_uuid = ?");
			preparedStatement2.setString(1, uuid.toString());
			
			final ResultSet resultSet = preparedStatement1.executeQuery();
			if(resultSet.next())
			{
				final PreparedStatement preparatedStatement3 = connection.prepareStatement("UPDATE players SET faction_name=NULL, faction_role=NULL, faction_joined_at=NULL WHERE faction_name = ?");
				preparatedStatement3.setString(1, resultSet.getString(1));
				
				preparedStatement2.executeUpdate();
				preparatedStatement3.executeUpdate();
				main.getFaction().remove(main.getFaction().get(uuid));
				p.sendMessage("§cVous avez supprimé la faction "+resultSet.getString(1)+".");
			}
			else
			{
				p.sendMessage("§cVous ne pouvez pas effectuer cela !");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F011");
		}

	}

	public String playerFactionName(Player p)
	{
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			final Connection connection = firelandConnection.getConnection();

			final PreparedStatement preparedStatement = connection.prepareStatement("SELECT faction_name FROM players WHERE uuid=?");
			preparedStatement.setString(1, p.getUniqueId().toString());

			final ResultSet rS = preparedStatement.executeQuery();
			if (!rS.next())
			{
				return "";
			}
			else
			{
				return rS.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F012");
		}
		return "";
	}

	public FactionInformation getFactionInfo(String factionName)
	{
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT upgrade,nbr_members,money,created_at,leader_uuid FROM faction WHERE name = ?");
			preparedStatement1.setString(1, factionName);

			final ResultSet resultSet = preparedStatement1.executeQuery();

			if (resultSet.next())
			{
				int currentUpgrade = resultSet.getInt(1);
				int currentNbrOfPlayers = resultSet.getInt(2);
				int currentMoney = resultSet.getInt(3);
				int maxNbrOfPlayers = 4;
				Timestamp createdAt = resultSet.getTimestamp(4);
				int maxMoney = 10000;
				int chestSize = 0;

				if(currentUpgrade == 2){maxNbrOfPlayers = 6;maxMoney=20000;}
				if(currentUpgrade == 3){maxNbrOfPlayers = 8;maxMoney=40000;chestSize=9;}

				UUID leader = UUID.fromString(resultSet.getString("5"));

				return new FactionInformation(factionName, currentNbrOfPlayers, maxNbrOfPlayers, currentUpgrade, currentMoney, maxMoney, chestSize, createdAt, leader);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème. Erreur : #F013");
		}
		return null;
	}

	public FactionInformation getFactionInfoWithAmeliorations(String factionName)
	{
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT upgrade,nbr_members,money,created_at,leader FROM faction WHERE name = ?");
			preparedStatement1.setString(1, factionName);

			final ResultSet resultSet = preparedStatement1.executeQuery();

			if (resultSet.next())
			{
				int currentUpgrade = resultSet.getInt(1)+1;
				int currentNbrOfPlayers = resultSet.getInt(2);
				int currentMoney = resultSet.getInt(3);
				int maxNbrOfPlayers = 4;
				Timestamp createdAt = resultSet.getTimestamp(4);
				int maxMoney = 10000;
				int chestSize = 0;

				if(currentUpgrade == 2){maxNbrOfPlayers = 6;maxMoney=20000;}
				if(currentUpgrade >= 3){maxNbrOfPlayers = 8;maxMoney=40000;chestSize=9;}

				UUID leader = UUID.fromString(resultSet.getString(5));

				return new FactionInformation(factionName, currentNbrOfPlayers, maxNbrOfPlayers, currentUpgrade, currentMoney, maxMoney, chestSize, createdAt, leader);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème. Erreur : #F014");
		}
		return null;
	}

	public boolean factionHasMaxPlayer(String factionName)
	{
		FactionInformation infos = getFactionInfo(factionName);
		return !(infos.getCurrentNbrOfPlayers() < infos.getMaxNbrOfPlayers());
	}

	public FactionPlayerInformation GetInformationOfPlayerInAFaction(UUID playerUuid, String playerName)
	{
		FactionPlayerInformation info = new FactionPlayerInformation("", "", -1);
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			final Connection connection = firelandConnection.getConnection();

			final PreparedStatement preparedStatement = connection.prepareStatement("SELECT faction_name,faction_role FROM players WHERE uuid=?");
			preparedStatement.setString(1, playerUuid.toString());

			final ResultSet rS = preparedStatement.executeQuery();
			if (!rS.next())
			{
				return info;
			}
			else
			{
				info = new FactionPlayerInformation(playerName, rS.getString(1), rS.getInt(2));
				return info;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F015");
		}
		return info;
	}

	public void upgradeFaction(String factionName)
	{
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT upgrade FROM faction WHERE name = ?");
			preparedStatement1.setString(1, factionName);

			final ResultSet resultSet = preparedStatement1.executeQuery();

			if (resultSet.next())
			{
				final PreparedStatement preparedStatement2 = connection.prepareStatement("UPDATE faction SET upgrade=? WHERE name = ?");
				int upgrade = resultSet.getInt(1)+1;
				preparedStatement2.setString(1, factionName);
				preparedStatement2.setInt(2, upgrade);

				preparedStatement2.executeUpdate();
				sender.sendMessage("§cVotre faction a été amélioré au rang §d"+upgrade);
			}
			else
			{
				sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F015");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F015");
		}
	}

	public void unrankPlayer(String playerName, String factionName)
	{
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT faction_role FROM players WHERE name = ?");
			preparedStatement1.setString(1, playerName);

			final ResultSet resultSet = preparedStatement1.executeQuery();

			if (resultSet.next())
			{
				final PreparedStatement preparedStatement2 = connection.prepareStatement("UPDATE faction_role SET upgrade=? WHERE name = ?");
				int demote = resultSet.getInt(1)-1;
				preparedStatement2.setInt(2, demote);
				preparedStatement2.setString(1, playerName);

				preparedStatement2.executeUpdate();
			}
			else
			{
				sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F016");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F016");
		}
	}
}
