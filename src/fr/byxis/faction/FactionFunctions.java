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
		//Récupération du main, pour pouvoir avoir envoyer des requêtes à la base de données
		this.main = main;
		//Récupération de la personne qui envoie la commande, pour lui envoyer les messages d'erreurs
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
		/*
		* Initialise la création d'une faction avec quelques vérifications :
		* 	- Le nom de faction est unique
		* 	- Le joueur qui crée la faction n'est pas dans une faction
		*
		* Parameters:
		* 	- Player p : le joueur qui crée la faction
		* 	- String name : le nom de la faction
		*/
		//Enregistrements des données 
		final UUID uuid = p.getUniqueId();
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        
		try {
			//Initialisation de la connexion a la bd et de la première requete SQL
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement verificationFactionWithName = connection.prepareStatement("SELECT name FROM faction WHERE name = ?");//AND leader_uuid = ?
			verificationFactionWithName.setString(1, name);

			//Réalisation de la requête SQL
			final ResultSet resultSet1 = verificationFactionWithName.executeQuery();
			
			if (resultSet1.next())
			{
				//La requête a trouvé une faction qui a ce nom
				p.sendMessage("§cLe nom de cette faction est déjà pris !");
			}
			else
			{
				//La requête n'a pas trouvé de faction qui a ce nom, on vérifie donc si le joueur a une faction
				final PreparedStatement verificationPlayerInFaction = connection.prepareStatement("SELECT player_uuid FROM player_faction WHERE player_uuid = ?");
				verificationPlayerInFaction.setString(1, uuid.toString());
				final ResultSet resultSet2 = verificationPlayerInFaction.executeQuery();
				if(resultSet2.next())
				{
					//La requête a trouvé que le joueur a une faction
					p.sendMessage("§cVous êtes déjà dans une faction !");
				}
				else
				{
					//La requête a trouvé que le joueur n'a pas de faction, on crée donc la faction
					createFaction(connection, uuid, name, p);
				}
				
			}
		} catch (SQLException e) {
			//Une erreur est survenue (Problème de connexion à la BD)
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F009");
		}
	}
	
	public void createFaction(Connection connection, UUID uuid, String name, Player p)
	{
		/*
		 * Créer une faction
		 *
		 * Parameters:
		 *  - Connection connection : la connection a la DB (on ne la renouvelle pas car cette méthode est précédée
		 * 		de initCreateFaction qui a déjà une ocnnection à la DB)
		 * 	- UUID uuid : l'uuid du leader
		 * 	- String name : le nom de la faction
		 * 	- Player p : le joueur qui crée la faction
		 *
		 */
		try {
			//On prépare les requêtes SQL
			final PreparedStatement insertionFaction = connection.prepareStatement("INSERT INTO faction (name, leader_uuid, created_at) VALUES (?, ?, ?)");
			final PreparedStatement insertionPlayerFaction = connection.prepareStatement("INSERT INTO player_faction VALUES (?,?,?,?)");
			final long time = System.currentTimeMillis();

			//On complète la premiere requête
			insertionFaction.setString(1, name);
			insertionFaction.setString(2, uuid.toString());
			insertionFaction.setTimestamp(3, new Timestamp(time));
			//On complète la deuxieme requête
			insertionPlayerFaction.setString(1, p.getUniqueId().toString());
			insertionPlayerFaction.setString(2, name);
			insertionPlayerFaction.setTimestamp(3, new Timestamp(time));
			insertionPlayerFaction.setInt(4, 2);

			//On executes les requetes
			insertionFaction.executeUpdate();
			insertionPlayerFaction.executeUpdate();
			p.sendMessage("§aVous avez créé la faction "+name+" !");
		} catch (SQLException e) {
			//Une erreur est survenue (Problème de connexion à la BD)
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F010");
		}
	}

	public void deleteFaction(Player p)
	{
		/*
		 * Supprime une faction, le joueur p doit etre le chef de la faction
		 *
		 * Parameters:
		 *  - Player p : la personne qui envoie la commande
		 *
		 */
		final UUID uuid = p.getUniqueId();

		//Connection a la bd
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		try {
			Connection connection = firelandConnection.getConnection();
			//Préparation de la premiere requete
			final PreparedStatement getFactionName = connection.prepareStatement("SELECT name FROM faction WHERE leader_uuid = ?");
			getFactionName.setString(1, uuid.toString());

			final ResultSet resultFactionName = getFactionName.executeQuery();
			//Check si le joueur est chef d'une faction
			if(resultFactionName.next())
			{
				//Preparations des requetes pour supprimer
				final PreparedStatement removeFaction = connection.prepareStatement("DELETE FROM faction WHERE leader_uuid = ?");
				removeFaction.setString(1, uuid.toString());
				final PreparedStatement removePlayerInFaction = connection.prepareStatement("DELETE FROM player_faction WHERE player_faction = ?");
				removePlayerInFaction.setString(1, resultFactionName.getString(1));

				//Executions des requetes pour supprimer
				removeFaction.executeUpdate();
				removePlayerInFaction.executeUpdate();
				main.getFaction().remove(main.getFaction().get(uuid));
				p.sendMessage("§cVous avez supprimé la faction "+resultFactionName.getString(1)+".");
			}
			else
			{
				//Le joueur n'est soit pas dans une faction, soit pas leader d'une faction
				p.sendMessage("§cVous ne pouvez pas effectuer cela !");
			}
			
		} catch (SQLException e) {
			//Une erreur est survenue (Problème de connexion à la BD)
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
		/*
		 * Renvoie un objet de type FactionInformation avec les données de la faciton nommée factionName
		 *
		 * Parameters:
		 * 	- String name : le nom de la faction
		 */
		//Connection a la base de données
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			final Connection connection = firelandConnection.getConnection();
			//Préparation de la commande
			final PreparedStatement requestInfo = connection.prepareStatement("SELECT upgrade,nbr_members,money,created_at,leader_uuid FROM faction WHERE name = ?");
			requestInfo.setString(1, factionName);

			final ResultSet result = requestInfo.executeQuery();

			//il y a un résultat, donc on récupère les infos et on return un objet de type faction avec les données de la faction
			if (result.next())
			{
				int currentUpgrade = result.getInt(1);
				int currentNbrOfPlayers = result.getInt(2);
				int currentMoney = result.getInt(3);
				int maxNbrOfPlayers = 4;
				Timestamp createdAt = result.getTimestamp(4);
				int maxMoney = 10000;
				int chestSize = 0;

				if(currentUpgrade == 2){maxNbrOfPlayers = 6;maxMoney=20000;}
				if(currentUpgrade >= 3){maxNbrOfPlayers = 8;maxMoney=40000;chestSize=9;}

				UUID leader = UUID.fromString(result.getString("5"));

				return new FactionInformation(factionName, currentNbrOfPlayers, maxNbrOfPlayers, currentUpgrade, currentMoney, maxMoney, chestSize, createdAt, leader);
			}
		} catch (SQLException e) {
			//Une erreur est survenue (Problème de connexion à la BD)
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème. Erreur : #F013");
		}
		//Il n'y avait pas de résultat donc on return Null
		return null;
	}

	public FactionInformation getFactionInfoWithAmeliorations(String factionName)
	{
		/*
		 * Renvoie un objet de type FactionInformation avec les données de la faction nommée factionName a la prochaine amélioration
		 *
		 * Parameters:
		 * 	- String name : le nom de la faction
		 */
		//Connection a la base de données
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			final Connection connection = firelandConnection.getConnection();
			//Préparation de la commande
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT upgrade,nbr_members,money,created_at,leader FROM faction WHERE name = ?");
			preparedStatement1.setString(1, factionName);

			final ResultSet resultSet = preparedStatement1.executeQuery();
			//il y a un résultat, donc on récupère les infos et on return un objet de type faction avec les données de la faction
			if (resultSet.next())
			{
				//On rajoute 1 a l'upgrade actuelle pour avoir les informations de la faction au rang suivant
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
			//Une erreur est survenue (Problème de connexion à la BD)
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème. Erreur : #F014");
		}
		//Il n'y avait pas de résultat donc on return Null
		return null;
	}

	public boolean factionHasMaxPlayer(String factionName)
	{
		//on récupère les informations de la faction
		FactionInformation infos = getFactionInfo(factionName);
		//On vérifie que la faction existe, que les données ont été trouvées
		if (infos == null)
		{
			return false;
		}
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
