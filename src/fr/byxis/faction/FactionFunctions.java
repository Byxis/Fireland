package fr.byxis.faction;

import fr.byxis.db.DbConnection;
import fr.byxis.main.Main;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
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
		/*
		 * Donne les informations d'une faction au joueur ayant rentré une commande:
		 * Parameters:
		 *     - Player p : le joueur qui envoie la requête
		 *     - Faction Information infos : les informations à propos de la faction
		 * 	   - ArrayList<FactionPlayerInformation> players : les informations des joueurs qui sont dans la faction.
		 */

		String leader = null;
		StringBuilder mod = new StringBuilder();
		StringBuilder members = new StringBuilder();
		//Récupération du nom des leaders de la faction
		for (FactionPlayerInformation player : players) {
			//Récupération du nom des leaders de la faction
			if (player.getRole() == 2) {
				leader = player.getName();
			//Récupération du nom des modérateurs de la faction
			} else if (player.getRole() == 1) {
				if (mod.toString().equals("")) {
					mod.append("§r").append(player.getName());
				} else {
					mod.append("§a, §r").append(player.getName());
				}
			//Récupération du nom des autres membres de la faction
			} else if (player.getRole() == 0) {
				if (members.toString().equals("")) {
					members.append("§r").append(player.getName());
				} else {
					members.append("§a, §r").append(player.getName());
				}
			}
		}
		//Envoi du message contenant les informations au joueur
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
		/*
		 * Crée une liste avec les pseudos et rangs de tout les joueurs appartenant à la faction
		 * Parameters:
		 *     - String factionName : le nom de la faction
		 */
	{
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		//On prépare une requete sql
		try {
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT players.name,player_faction.role FROM players INNER JOIN player_faction ON player_faction.player_uuid = players.uuid WHERE player_faction.player_faction = ?");
			preparedStatement1.setString(1, factionName);
			
			final ResultSet resultSet = preparedStatement1.executeQuery();
			//On vérifie s'il y a un résultat à la requête
			if (resultSet.next())
			{
				//On initialise les variables
				ArrayList<FactionPlayerInformation> ar = new ArrayList<>();
				FactionPlayerInformation player = new FactionPlayerInformation(resultSet.getString(1), factionName, (resultSet.getInt(2)));
				//On ajoute les joueurs de la faction dans la liste
				ar.add(player);
				while(resultSet.next())
				{
					player = new FactionPlayerInformation(resultSet.getString(1), factionName, (resultSet.getInt(2)));
					ar.add(player);
				}
				//On renvoie la liste
				return ar;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème. Erreur : #F001");
		}
		return null;
	}
	
	public void InitInviteFaction(Player p, Player invited, String factionName) {
		/*
		 * Initialise l'invitation à une faction avec quelques vérifications :
		 * 	- le joueur invité n'a pas deja été invité précédemment
		 * On précise que l'on vérifie si le joueur qui invite appartient bien à une faction et à le droit d'inviter et que le joueuer invité n'a pas de faction dans factionManager
		 * Parameters:
		 * 	- Player p : le joueur qui invite
		 *  - Player invited : le joueur que l'on invite
		 * 	- String factionName : le nom de la faction
		 */
		final UUID uuid = invited.getUniqueId();
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		
		try {
			final Connection connection = firelandConnection.getConnection();
			//On prépare la requête sql
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT available_time,faction_name FROM invite WHERE player_uuid = ? AND faction_name=?");
			preparedStatement1.setString(1, uuid.toString());
			preparedStatement1.setString(2, factionName);

			final ResultSet resultSet = preparedStatement1.executeQuery();
			//On vérifie si il y a des résultats à la requête
			if (resultSet.next())
			{
				//On regarde si la date obtenue dans la base de donnée est après la date actuelle
				final long time = System.currentTimeMillis();
				Timestamp currentTime = new Timestamp(time);
				if(resultSet.getTimestamp(1).after(currentTime))
				{
					//si oui, alors le joueur ne peut pas etre inviter à nouveau
					p.sendMessage("§cCe joueur a déjà été récemment invité dans cette faction !");
				}
				else
				{
					//Sinon, on invite le joueur
					final PreparedStatement preparedStatement2 = connection.prepareStatement("DELETE FROM invite WHERE player_uuid = ? AND faction_name=?");
					preparedStatement2.setString(1, uuid.toString());
					preparedStatement2.setString(2, factionName);
					preparedStatement2.executeUpdate();
					inviteFaction(connection, factionName, uuid);
					p.sendMessage("§a"+invited.getName()+" vient d'être invité dans la faction !");
					invited.sendMessage("§aVous avez été invité dans la faction "+factionName+" !");
				}

			}
			//si il n'y a pas de résultat à la requete, alors le joueur n'a jamais été invité, donc cela ne pose pas de problème, on l'invite
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
		/*
		 * Permet à un joueur d'en inviter un autre dans sa faction
		 * Parameters:
		 * 	- Connection connection : la connection a la DB.
		 * 	- UUID uuid : l'uuid de la personne invitée
		 * 	- String factionName : le nom de la faction
		 */
	{
		try {
			//On prépare la requête SQL
			final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO invite (faction_name, player_uuid, available_time) VALUES (?, ?, ?)");
			preparedStatement.setString(1, factionName);
			preparedStatement.setString(2, uuid.toString());
			final long time = System.currentTimeMillis()  + TimeUnit.MINUTES.toMillis(20);
			preparedStatement.setTimestamp(3, new Timestamp(time));
			//On execute la requête
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			//Une erreur est survenue (Problème de connexion à la BD)
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F004");
			e.printStackTrace();
		}
		
	}
	
	public boolean isInvitedToFaction(Player p, String name)
	{
		/*
		 * Cherche si un joueur a reçu une invitation.
		 * Parameters:
		 * 	- Player p : le joueur dont on gère les invitations
		 * 	- String name : le nom de la faction
		 */

		final UUID uuid = p.getUniqueId();
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		
		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT available_time FROM invite WHERE player_uuid  = ? AND faction_name = ?");
			preparedStatement1.setString(1, uuid.toString());
			preparedStatement1.setString(2, name);
			//Réalisation de la requête SQL
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
			//Une erreur est survenue (Problème de connexion à la BD)
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F005");
		}
		return false;
	}
	
	public String getExactFactionNameFromInvite(Player p, String name)
	{
		/*
		 * Donne le nom exact d'une fonction, avec les bonnes majuscules, c'est plus esthétique quand il faut envoyer des messages.
		 * Parameters:
		 * 	- Player p : le joueur envoyant l'invitation
		 * 	- String name : le nom de la faction
		 */
		final UUID uuid = p.getUniqueId();
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		
		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT faction_name FROM invite WHERE player_uuid  = ? AND faction_name = ?");
			preparedStatement1.setString(1, uuid.toString());
			preparedStatement1.setString(2, name);
			//Réalisation de la requête SQL
			final ResultSet resultSet = preparedStatement1.executeQuery();
			//S'il y a un résultat à la requête, on renvoie le nom de la faction, qui est le premier attribut de la table.
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
	
	public void joinFaction(Player p, String factionName) {
		/*
		 * Intègre un joueur à une faction.
		 * Parameters:
		 * 	- Player p : le joueur rejoignant la faction
		 * 	- String factionName : le nom de la faction que le joueur rejoint
		 */
		final UUID uuid = p.getUniqueId();
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		
		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT player_uuid FROM player_faction WHERE player_uuid = ?");
			preparedStatement1.setString(1, uuid.toString());
			//Réalisation de la requête SQL
			final ResultSet resultSet = preparedStatement1.executeQuery();
			//S'il n'y a aucun résultat, le joueur n'est pas dans une fonction, dans ce cas là, on l'ajoute à la table player_faction
			if (!resultSet.next())
			{
				final PreparedStatement preparedStatement2 = connection.prepareStatement("INSERT INTO player_faction VALUES (?,?,?,?)");
				final long time = System.currentTimeMillis();
				preparedStatement2.setString(1, uuid.toString());
				preparedStatement2.setString(2, factionName);
				preparedStatement2.setTimestamp(3, new Timestamp(time));
				preparedStatement2.setInt(4, 0);
				preparedStatement2.executeUpdate();
				p.sendMessage("§aVous avez rejoint la faction " + factionName + ".");

				//Puis on ajoute 1 au nombre de membres actuels
				final PreparedStatement preparedStatement3 = connection.prepareStatement("SELECT nbr_members FROM faction WHERE name = ?");
				preparedStatement3.setString(1, factionName);
				final ResultSet resultSet2 = preparedStatement3.executeQuery();
				if(resultSet2.next())
				{
					final PreparedStatement preparedStatement4 = connection.prepareStatement("UPDATE faction SET nbr_members=? WHERE name = ?");
					preparedStatement4.setInt(1, resultSet2.getInt(1)+1);
					preparedStatement4.setString(2, factionName);
					preparedStatement4.executeUpdate();
				}

			}
			//S'il y a un résultat dans la table, le joueur appartient donc déjà à une faction.
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
		/*
		 * Retire un joueur d'une faction.
		 * Parameters:
		 * 	- Player p : le joueur rejoignant la faction
		 * 	- UUID leader : l'identifiant du chef de la faction du joueur
		 */
		final UUID uuid = p.getUniqueId();
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		
		try {
			if (p.getUniqueId() == leader)
			{
				p.sendMessage("§cVous êtes leader donc vous ne pouvez pas quitter votre faction ! Vous pouvez cependant la dissoudre ou donner le rôle a quelqu'un d'autre");
			}
			//Si ce n'est pas le leader, il peut quitter, dans ce cas on change la table faction_name et on le prévient
			else
			{

				final Connection connection = firelandConnection.getConnection();
				FactionInformation infos = getFactionInfo(GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName()).getFactionName());
				final PreparedStatement preparedStatement2 = connection.prepareStatement("UPDATE faction SET nbr_members=? WHERE name = ?");
				preparedStatement2.setInt(1, infos.getCurrentNbrOfPlayers()-1);
				preparedStatement2.setString(2, infos.getName());
				//On exécute la requete SQL
				preparedStatement2.executeUpdate();

				final PreparedStatement preparedStatement1 = connection.prepareStatement("DELETE FROM player_faction WHERE player_faction=? AND player_uuid = ?");
				preparedStatement1.setString(1, infos.getName());
				preparedStatement1.setString(2, p.getUniqueId().toString());
				//On exécute la requete SQL
				preparedStatement1.executeUpdate();
				p.sendMessage("§cVous avez quitté la faction "+infos.getName()+".");
				if(main.hashMapManager.getFactionMap().containsKey(p.getUniqueId()))
				{
					main.hashMapManager.removeFactionMap(p.getUniqueId());
				}
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
				HashMap<UUID, String> fmap = main.hashMapManager.getFactionMap();
				if(main.hashMapManager.getFactionMap().containsKey(p.getUniqueId()))
				{
					main.hashMapManager.removeFactionMap(p.getUniqueId());
				}
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
		/*
		 * Renvoie un objet de type String, qui est le nom de la faction à laquelle appartient le joueur Player p
		 *
		 * Parameters:
		 * 	- Player p : le joueur dont on veut le nom de la faction
		 */
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			final Connection connection = firelandConnection.getConnection();
			//On prépare la requete SQL
			final PreparedStatement preparedStatement = connection.prepareStatement("SELECT player_faction FROM player_faction WHERE player_uuid=?");
			preparedStatement.setString(1, p.getUniqueId().toString());
			//Réalisation de la requete SQL
			final ResultSet rS = preparedStatement.executeQuery();
			//Si la requete n'a pas de résultats, cela signifie que le joueur n'a pas de faction, dans ce cas, on ne renvoie rien
			if (!rS.next())
			{
				return "";
			}
			//S'il y a un résultat, on renvoie le nom de la faction que l'on trouve dans la table player_faction
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
				int maxNbrOfPlayers = 2;
				Timestamp createdAt = result.getTimestamp(4);
				int maxMoney = 10000;
				int chestSize = 0;

				if(currentUpgrade == 2){maxNbrOfPlayers = 4;maxMoney=20000;}
				if(currentUpgrade >= 3){maxNbrOfPlayers = 6;maxMoney=40000;chestSize=9;}

				UUID leader = UUID.fromString(result.getString(5));

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
				int maxNbrOfPlayers = 2;
				Timestamp createdAt = resultSet.getTimestamp(4);
				int maxMoney = 10000;
				int chestSize = 0;

				if(currentUpgrade == 2){maxNbrOfPlayers = 4;maxMoney=20000;}
				if(currentUpgrade >= 3){maxNbrOfPlayers = 6;maxMoney=40000;chestSize=9;}

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
		/*
		 * Renvoie l'objet de type FactionPlayerInformation, qui permet d'obtenir facilement le nom, le nom de faction et le role dans la faction
		 * du joueur
		 *
		 * Parameters:
		 * 	- UUID playerUuid : uuid du joueur
		 *  - String playerName : nom du joueur
		 */
		//Création de l'objet
		//Il est par défaut vide puis sera actualisé
		FactionPlayerInformation info = new FactionPlayerInformation("", "", -1);
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			final Connection connection = firelandConnection.getConnection();
			//Préparation de la requête sql
			final PreparedStatement getInfos = connection.prepareStatement("SELECT player_faction,role FROM player_faction WHERE player_uuid=?");
			getInfos.setString(1, playerUuid.toString());

			//Execution de la requête
			final ResultSet rS = getInfos.executeQuery();
			if (rS.next()) {
				//Il y a un résultat, on actualise l'objet avec les bonnes valeurs
				info = new FactionPlayerInformation(playerName, rS.getString(1), rS.getInt(2));
			}
			//Sinon, on return les infos vides
			return info;
			//Une erreur est survenue (Problème de connexion à la BD)

		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F015");
		}
		return info;
	}

	public void upgradeFaction(String factionName)
	{
		/*
		 * Améliore le rang d'une faction.
		 *
		 * Parameters:
		 * 	- String factionName : le nom de la faction que l'on améliore.
		 */
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT upgrade FROM faction WHERE name = ?");
			preparedStatement1.setString(1, factionName);
			//Réalisation de la requête SQL
			final ResultSet resultSet = preparedStatement1.executeQuery();
			//Si la faction est trouvée dans la table upgrade, on améliore son rang
			if (resultSet.next())
			{
				//On prépare la requete de modification :
				final PreparedStatement preparedStatement2 = connection.prepareStatement("UPDATE faction SET upgrade=? WHERE name = ?");
				//On modifie l'attribut upgrade qui correspond au rang de la faction, on ajoute 1
				int upgrade = resultSet.getInt(1)+1;
				preparedStatement2.setString(2, factionName);
				preparedStatement2.setInt(1, upgrade);
				//On exécute la requete SQL
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

	public void ChangePlayerRank(UUID playerUuid, String factionName, int amount)
		/*
		 * Augmente le rang d'un joueur dans une faction
		 *
		 * Parameters:
		 *  - UUID playerUuid : l'uuid du joueur dont on veut modifier le rang
		 * 	- String factionName : le nom de la faction que l'on améliore.
		 */
	{
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT role FROM player_faction WHERE player_uuid = ?");
			preparedStatement1.setString(1, playerUuid.toString());
			//Réalisation de la requête SQL
			final ResultSet resultSet = preparedStatement1.executeQuery();
			//S'il y a un résultat à la requête, on change le rang du joueur comme prévu :
			if (resultSet.next())
			{
				//On prépare la requete de modification :
				final PreparedStatement preparedStatement2 = connection.prepareStatement("UPDATE player_faction SET role = ? WHERE player_uuid = ?");
				//On crée la variable promote que l'on va insérer dans l'attribut upgrade. La variable correspond à l'identifiant du rang du joueur, ici + 1 car on augmente son rang
				double promote = resultSet.getDouble(1)+amount;
				preparedStatement2.setDouble(1, promote);
				preparedStatement2.setString(2, playerUuid.toString());
				//Réalisation de la requête SQL
				preparedStatement2.executeUpdate();
			}
			else
			{
				sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F017");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F017");
		}
	}

	public boolean deposit(String factionName, int amount)
	{
		/*
		 * Améliore le rang d'une faction.
		 *
		 * Parameters:
		 * 	- String factionName : le nom de la faction que l'on améliore.
		 */
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT money FROM faction WHERE name = ?");
			preparedStatement1.setString(1, factionName);
			//Réalisation de la requête SQL
			final ResultSet resultSet = preparedStatement1.executeQuery();
			//Si la faction est trouvée dans la table upgrade, on améliore son rang
			if (resultSet.next())
			{
				//On prépare la requete de modification :
				final PreparedStatement preparedStatement2 = connection.prepareStatement("UPDATE faction SET money=? WHERE name = ?");
				//On modifie l'attribut money qui correspond au rang de la faction, on ajoute 1
				int money = resultSet.getInt(1)+amount;
				preparedStatement2.setString(2, factionName);
				preparedStatement2.setInt(1, money);
				//On exécute la requete SQL
				preparedStatement2.executeUpdate();
				sender.sendMessage("§aVous avez déposé " + amount + "$ !");
				return true;
			}
			else
			{
				sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F018");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F018");
			return false;
		}
	}

	public boolean take(String factionName, double amount)
	{
		/*
		 * Améliore le rang d'une faction.
		 *
		 * Parameters:
		 * 	- String factionName : le nom de la faction que l'on améliore.
		 */
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();
			//On prépare la requete de modification :
			final PreparedStatement preparedStatement2 = connection.prepareStatement("UPDATE faction SET money=? WHERE name = ?");
			//On modifie l'attribut money qui correspond au rang de la faction, on ajoute 1
			double money = GetFactionMoney(factionName);
			if(money != -1)
			{
				preparedStatement2.setString(2, factionName);
				preparedStatement2.setDouble(1, money-amount);
				//On exécute la requete SQL
				preparedStatement2.executeUpdate();
				return true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F019");
		}
		return false;
	}

	public void renameFaction(String _factionName, String _newName)
	{
		/*
		 * Renomme une faction.
		 *
		 * Parameters :
		 * 	- String _factionName : le nom de la faction que l'on améliore.
		 */
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();
			//Si la faction est trouvée dans la table upgrade, on améliore son rang

			//On prépare la requete de modification :
			final PreparedStatement preparedStatement2 = connection.prepareStatement("UPDATE faction SET name=? WHERE name = ?");
			preparedStatement2.setString(2, _factionName);
			preparedStatement2.setString(1, _newName);
			//On exécute la requete SQL
			preparedStatement2.executeUpdate();

			final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE invite SET faction_name=? WHERE faction_name = ?");
			preparedStatement.setString(2, _factionName);
			preparedStatement.setString(1, _newName);
			//On exécute la requete SQL
			preparedStatement.executeUpdate();

			final PreparedStatement preparedStatement1 = connection.prepareStatement("UPDATE player_faction SET player_faction=? WHERE player_faction = ?");
			preparedStatement1.setString(2, _factionName);
			preparedStatement1.setString(1, _newName);
			//On exécute la requete SQL
			preparedStatement1.executeUpdate();
			sender.sendMessage("§cVotre faction a été renommée. Elle s'appelle désormais §d"+_newName+" §c!");
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F015");
		}
	}

	public double GetFactionMoney(String factionName) {
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT money FROM faction WHERE name = ?");
			preparedStatement1.setString(1, factionName);
			//Réalisation de la requête SQL
			final ResultSet resultSet = preparedStatement1.executeQuery();
			if(resultSet.next())
			{
				return resultSet.getDouble(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F021");
		}
		return -1;
	}

	public void kickPlayer(FactionInformation infos, Player victim)
	{//TODO
		/*
		 * Renomme une faction.
		 *
		 * Parameters :
		 * 	- String _factionName : le nom de la faction que l'on améliore.
		 */
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();
			//Si la faction est trouvée dans la table upgrade, on améliore son rang

			//On prépare la requete de modification :
			final PreparedStatement preparedStatement2 = connection.prepareStatement("UPDATE faction SET nbr_members=? WHERE name = ?");
			preparedStatement2.setInt(1, infos.getCurrentNbrOfPlayers()-1);
			preparedStatement2.setString(2, infos.getName());
			//On exécute la requete SQL
			preparedStatement2.executeUpdate();

			final PreparedStatement preparedStatement1 = connection.prepareStatement("DELETE FROM player_faction WHERE player_faction=? AND player_uuid = ?");
			preparedStatement1.setString(1, infos.getName());
			preparedStatement1.setString(2, victim.getUniqueId().toString());
			//On exécute la requete SQL
			preparedStatement1.executeUpdate();
			if(main.hashMapManager.getFactionMap().containsKey(victim.getUniqueId()))
			{
				main.hashMapManager.removeFactionMap(victim.getUniqueId());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F022");
		}
	}

	public void AddPerk(String _factionName, String _perk)
	{
		/*
		 * Renomme une faction.
		 *
		 * Parameters :
		 * 	- String _factionName : le nom de la faction que l'on améliore.
		 */
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();

			final PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT ? FROM faction WHERE ");
			preparedStatement2.setString(1, _perk);
			preparedStatement2.setString(2, _factionName);
			//On exécute la requete SQL
			ResultSet rs = preparedStatement2.executeQuery();
			if(rs.next())
			{
				if(!rs.getBoolean(1))
				{
					final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE faction SET ?=1 WHERE name = ?");
					preparedStatement.setString(1, _perk);
					preparedStatement.setString(2, _factionName);
					//On exécute la requete SQL
					preparedStatement.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F023");
		}
	}

	public boolean HasPerk(String _factionName, String _perk)
	{
		/*
		 * Renomme une faction.
		 *
		 * Parameters :
		 * 	- String _factionName : le nom de la faction que l'on améliore.
		 */
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();

			final PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT ? FROM faction WHERE name = ? ");
			preparedStatement2.setString(1, _perk);
			preparedStatement2.setString(2, _factionName);
			//On exécute la requete SQL
			ResultSet rs = preparedStatement2.executeQuery();
			if(rs.next())
			{
				if(rs.getBoolean(1))
				{
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F023");
		}
		return false;
	}
}
