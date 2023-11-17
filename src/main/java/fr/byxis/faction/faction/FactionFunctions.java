package fr.byxis.faction.faction;

import fr.byxis.db.DbConnection;
import fr.byxis.faction.faction.events.FactionBuyPerkEvent;
import fr.byxis.faction.faction.events.PlayerJoinFactionEvent;
import fr.byxis.faction.housing.BunkerClass;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.ItemSerializer;
import fr.byxis.fireland.Fireland;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static fr.byxis.fireland.utilities.InGameUtilities.debugp;

public class FactionFunctions {
	
	final private Fireland main;
	private Player sender;

	public FactionFunctions(Fireland main, Player sender)
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
					mod.append("§7").append(player.getName());
				} else {
					mod.append("§8, §7").append(player.getName());
				}
			//Récupération du nom des autres membres de la faction
			} else if (player.getRole() == 0) {
				if (members.toString().equals("")) {
					members.append("§7").append(player.getName());
				} else {
					members.append("§8, §7").append(player.getName());
				}
			}
		}
		//Envoi du message contenant les informations au joueur
		String title = "§8------------- "+infos.getColorcode()+infos.getName()+ "§8 - §dNiv. "+infos.getCurrentUpgrade()+" §8-------------";
		p.sendMessage(title);
		p.sendMessage(" ");
		p.sendMessage("§8Date de création: §7"+infos.getCreatedAt());
		p.sendMessage("§8Nombre de membres: §7"+infos.getCurrentNbrOfPlayers()+"/"+infos.getMaxNbrOfPlayers());
		p.sendMessage("§8Argent: §7$"+ infos.getCurrentMoney()+"/"+infos.getMaxMoney());
		p.sendMessage("");
		p.sendMessage("§8Chef:");
		assert leader != null;
		p.sendMessage("§7"+leader);
		p.sendMessage("§8Modérateurs:");
		p.sendMessage(mod.toString());
		p.sendMessage("§8Membres:");
		p.sendMessage(members.toString());
		p.sendMessage("§8"+("-".repeat(ChatColor.stripColor(title).split("").length-5)));
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
			final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT players.name,player_faction.role, players.uuid, joined_at FROM players INNER JOIN player_faction ON player_faction.player_uuid = players.uuid WHERE player_faction.player_faction = ? ORDER BY player_faction.role DESC, player_faction.joined_at");
			preparedStatement1.setString(1, factionName);
			
			final ResultSet resultSet = preparedStatement1.executeQuery();
			ArrayList<FactionPlayerInformation> ar = new ArrayList<>();
			FactionPlayerInformation player;
			while(resultSet.next())
			{
				player = new FactionPlayerInformation(resultSet.getString(1), factionName, (resultSet.getInt(2)), UUID.fromString(resultSet.getString(3)), resultSet.getTimestamp(4));
				ar.add(player);
				debugp("FBUG01"+player.getName() +" "+ player.getFactionName());
			}
			//On renvoie la liste
			return ar;
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
					sendFactionPlayer(factionName, invited.getName()+" vient d'être invité dans la faction !");
					InGameUtilities.sendInteractivePlayerMessage(invited, "Vous avez été invité dans la faction "+GetColorCode(factionName)+factionName+"§r§7 ! Cliquez ici pour la rejoindre .",
							"/faction join "+factionName, "§aCliquez ici pour rejoindre la faction", ClickEvent.Action.RUN_COMMAND);
				}

			}
			//si il n'y a pas de résultat à la requete, alors le joueur n'a jamais été invité, donc cela ne pose pas de problème, on l'invite
			else
			{
				inviteFaction(connection, factionName, uuid);
				InGameUtilities.sendPlayerInformation(p, invited.getName()+" vient d'être invité dans la faction !");
				InGameUtilities.sendInteractivePlayerMessage(invited, "Vous avez été invité dans la faction "+GetColorCode(factionName)+factionName+"§r§7 ! Cliquez ici pour la rejoindre .",
						"/faction join "+factionName, "§aCliquez ici pour rejoindre la faction", ClickEvent.Action.RUN_COMMAND);
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
				PlayerJoinFactionEvent event = new PlayerJoinFactionEvent(p, factionName);
				Bukkit.getPluginManager().callEvent(event);
				InGameUtilities.sendPlayerInformation(p, "Vous avez rejoint la faction " +GetColorCode(factionName)+ factionName + "§7.");
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

				final PreparedStatement preparedStatement1 = connection.prepareStatement("DELETE FROM player_faction WHERE player_faction.player_faction=? AND player_uuid = ?");
				preparedStatement1.setString(1, infos.getName());
				preparedStatement1.setString(2, p.getUniqueId().toString());
				//On exécute la requete SQL
				preparedStatement1.executeUpdate();
				p.sendMessage("§cVous avez quitté la faction "+infos.getColorcode()+infos.getName()+"§7.");
				if(main.hashMapManager.getFactionMap().containsKey(p.getUniqueId()))
				{
					main.hashMapManager.removeFactionMap(p.getUniqueId());
				}
			}
		} catch (SQLException e) {
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F008");
		}
	}
	
	public boolean creatingFaction(Player p, String name)
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
				return false;
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
					return false;
				}
				else
				{
					//La requête a trouvé que le joueur n'a pas de faction, on crée donc la faction
					return createFaction(connection, uuid, name, p);
				}
				
			}
		} catch (SQLException e) {
			//Une erreur est survenue (Problème de connexion à la BD)
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F009");
		}
		return false;
	}
	
	public boolean createFaction(Connection connection, UUID uuid, String name, Player p)
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
			PlayerJoinFactionEvent event = new PlayerJoinFactionEvent(p, name);
			Bukkit.getPluginManager().callEvent(event);
			InGameUtilities.sendPlayerInformation(p, "Vous avez créé la faction "+GetColorCode(name)+name+" !");
			return true;
		} catch (SQLException e) {
			//Une erreur est survenue (Problème de connexion à la BD)
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F010");
		}
		return false;
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
				String[] queries = {
						"DELETE FROM faction WHERE name = ?",
						"DELETE FROM player_faction WHERE player_faction.player_faction = ?",
						"DELETE FROM faction_zone WHERE faction_zone.faction_name = ?",
						"DELETE FROM capture_zone WHERE faction_name = ?",
						"DELETE FROM faction_storage WHERE faction = ?",
						"DELETE FROM faction_housing WHERE faction = ?",
						"DELETE FROM invite WHERE invite.faction_name = ?"
				};

				for (String query : queries) {
					PreparedStatement statement = connection.prepareStatement(query);
					statement.setString(1, resultFactionName.getString(1));
					statement.executeUpdate();
				}

				if(main.hashMapManager.getFactionMap().containsKey(p.getUniqueId()))
				{
					main.hashMapManager.removeFactionMap(p.getUniqueId());
				}
				BunkerClass bk = new BunkerClass(resultFactionName.getString(1), main);
				bk.Destroy();
				InGameUtilities.sendPlayerError(p, "Vous avez supprimé la faction "+resultFactionName.getString(1)+".");
			}
			else
			{
				//Le joueur n'est soit pas dans une faction, soit pas leader d'une faction
				InGameUtilities.sendPlayerError(p, "Vous ne pouvez pas effectuer cela !");
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
			final PreparedStatement requestInfo = connection.prepareStatement("SELECT upgrade,money,created_at,leader_uuid, friendly_fire, show_nickname, has_skin, show_prefix, capture_perk, color_code, zone_tp FROM faction WHERE name = ?");
			requestInfo.setString(1, factionName);
			final PreparedStatement nbPlayerInfo = connection.prepareStatement("SELECT COUNT(*) FROM player_faction WHERE player_faction.player_faction = ?");
			nbPlayerInfo.setString(1, factionName);

			final ResultSet result = requestInfo.executeQuery();
			final ResultSet result2 = nbPlayerInfo.executeQuery();

			//il y a un résultat, donc on récupère les infos et on return un objet de type faction avec les données de la faction
			if (result.next() && result2.next())
			{
				int currentUpgrade = result.getInt(1);
				int currentNbrOfPlayers = result2.getInt(1);
				int currentMoney = result.getInt(2);
				Timestamp createdAt = result.getTimestamp(3);
				int[] amelioration = GetAmeliorationsUpgrades(currentUpgrade);

				UUID leader = UUID.fromString(result.getString(4));

				return new FactionInformation(factionName, currentNbrOfPlayers, amelioration[0], currentUpgrade, currentMoney, amelioration[1], amelioration[2], createdAt, leader, result.getBoolean(5),  result.getBoolean(6), result.getBoolean(7),  result.getBoolean(8), result.getBoolean(9), result.getString(10), result.getBoolean(11));
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
			final PreparedStatement requestInfo = connection.prepareStatement("SELECT upgrade,money,created_at,leader_uuid, friendly_fire, show_nickname, has_skin, show_prefix, capture_perk, color_code, zone_tp FROM faction WHERE name = ?");
			requestInfo.setString(1, factionName);
			final PreparedStatement nbPlayerInfo = connection.prepareStatement("SELECT COUNT(*) FROM player_faction WHERE player_faction.player_faction = ?");
			nbPlayerInfo.setString(1, factionName);

			final ResultSet result = requestInfo.executeQuery();
			final ResultSet result2 = nbPlayerInfo.executeQuery();

			//il y a un résultat, donc on récupère les infos et on return un objet de type faction avec les données de la faction
			if (result.next() && result2.next())
			{
				int currentUpgrade = result.getInt(1)+1;
				int currentNbrOfPlayers = result2.getInt(1);
				int currentMoney = result.getInt(2);
				Timestamp createdAt = result.getTimestamp(3);
				int[] amelioration = GetAmeliorationsUpgrades(currentUpgrade);

				UUID leader = UUID.fromString(result.getString(4));

				return new FactionInformation(factionName, currentNbrOfPlayers, amelioration[0], currentUpgrade, currentMoney, amelioration[1], amelioration[2], createdAt, leader, result.getBoolean(5),  result.getBoolean(6), result.getBoolean(7),  result.getBoolean(8), result.getBoolean(9), result.getString(10), result.getBoolean(11));
			}
		} catch (SQLException e) {
			//Une erreur est survenue (Problème de connexion à la BD)
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème. Erreur : #F013");
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
		FactionPlayerInformation info = new FactionPlayerInformation("", "", -1, null, null);
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			final Connection connection = firelandConnection.getConnection();
			//Préparation de la requête sql
			final PreparedStatement getInfos = connection.prepareStatement("SELECT player_faction,role, joined_at FROM player_faction WHERE player_uuid=?");
			getInfos.setString(1, playerUuid.toString());

			//Execution de la requête
			final ResultSet rS = getInfos.executeQuery();
			if (rS.next()) {
				//Il y a un résultat, on actualise l'objet avec les bonnes valeurs
				info = new FactionPlayerInformation(playerName, rS.getString(1), rS.getInt(2), playerUuid, rS.getTimestamp(3));
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
				InGameUtilities.sendPlayerInformation(sender,"Votre faction a été amélioré au rang §d"+upgrade+"§7.");
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
				InGameUtilities.sendPlayerInformation(sender, "§7Vous avez déposé " + amount + "$ !");
				return true;
			}
			else
			{
				if(sender != null)sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F018");
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

			final PreparedStatement renameFaction = connection.prepareStatement("""
                    UPDATE faction SET name=? WHERE name = ?;
                    UPDATE player_faction SET player_faction = ? WHERE player_faction = ?;
                    UPDATE faction_zone SET faction_name = ? WHERE faction_zone.faction_name = ?;
                    UPDATE capture_zone SET faction_name = ? WHERE faction_name = ?;
                    UPDATE faction_storage SET faction = ? WHERE faction = ?;
                    UPDATE faction_housing SET faction = ? WHERE faction = ?;
                    UPDATE invite SET faction_name = ? WHERE invite.faction_name = ?;""");

			for(int i =1; i < 8; i+=2)
			{
				renameFaction.setString(i, _newName);
				renameFaction.setString(i+1, _factionName);
			}

			//On exécute la requete SQL
			renameFaction.executeUpdate();

			sender.sendMessage("§cVotre faction a été renommée. Elle s'appelle désormais "+GetColorCode(_newName)+_newName+" §c!");
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
		try
		{
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();

			final PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT "+_perk+" FROM faction WHERE name = ?");
			preparedStatement2.setString(1, _factionName);
			//On exécute la requete SQL
			ResultSet rs = preparedStatement2.executeQuery();
			if(rs.next())
			{
				if(!rs.getBoolean(1))
				{
					final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE faction SET "+_perk+"=1 WHERE name = ?");
					preparedStatement.setString(1, _factionName);
					//On exécute la requete SQL
					preparedStatement.executeUpdate();

					FactionBuyPerkEvent event = new FactionBuyPerkEvent(_factionName, _perk);
					Bukkit.getPluginManager().callEvent(event);
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

			final PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT "+_perk+" FROM faction WHERE name = ? ");
			preparedStatement2.setString(1, _factionName);
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
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F024");
		}
		return false;
	}

	public void SetItemStorage(String faction, int index, ItemStack item)
	{
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();

			final PreparedStatement preparedStatement2 = connection.prepareStatement("INSERT INTO faction_storage(faction,index_item,item) VALUES(?,?,?)");
			preparedStatement2.setString(1, faction);
			preparedStatement2.setInt(2, index);
			preparedStatement2.setString(3, ItemSerializer.serialize(item));
			//On exécute la requete SQL
			preparedStatement2.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F025");
		}
	}
	public void UpdateItemStorage(String faction, int index, ItemStack item)
	{
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();

			final PreparedStatement preparedStatement2 = connection.prepareStatement("UPDATE faction_storage SET item = ? WHERE faction = ? AND index_item = ?");
			preparedStatement2.setString(2, faction);
			preparedStatement2.setInt(3, index);
			preparedStatement2.setString(1, ItemSerializer.serialize(item));
			//On exécute la requete SQL
			preparedStatement2.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F025");
		}
	}

	public boolean IndexStorageUsed(String faction, int index)
	{
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();

			final PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT index_item FROM faction_storage WHERE faction = ? AND index_item = ?");
			preparedStatement2.setString(1, faction);
			preparedStatement2.setInt(2, index);
			//On exécute la requete SQL
			ResultSet rs = preparedStatement2.executeQuery();
			if(rs.next())
			{
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F027");
		}
		return false;
	}

	public void RemoveItemStorage(String faction, int index)
	{
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();

		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();

			final PreparedStatement preparedStatement2 = connection.prepareStatement("DELETE FROM faction_storage WHERE faction_storage.faction = ? AND faction_storage.index_item = ?;");
			preparedStatement2.setString(1, faction);
			preparedStatement2.setInt(2, index);
			//On exécute la requete SQL
			preparedStatement2.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage("§cUne erreur est survenue. Merci de contacter le staff pour résoudre ce problème.  Erreur : #F026");
		}
	}

	public void SaveItemFactionStorage(String faction, int index, ItemStack item)
	{
		if(item != null)
		{
			if(IndexStorageUsed(faction, index))
			{
				UpdateItemStorage(faction, index, item);
			}
			else
			{
				SetItemStorage(faction, index, item);
			}
		}
		else
		{
			RemoveItemStorage(faction, index);
		}
	}

	public void SaveAllItemsFactionStorage(String faction, Inventory inv)
	{
		for(int i=0;i<inv.getSize();i++)
		{
			SaveItemFactionStorage(faction, i, inv.getItem(i));
		}
	}

	public Inventory loadAllItems(String factionName, int rang)
	{
		Inventory inv = Bukkit.createInventory(null, GetAmeliorationsUpgrades(rang)[2], "§8Stockage de: §6"+factionName);
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		inv.setItem(0, null);
		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();

			final PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT index_item, item FROM faction_storage WHERE faction = ?");
			preparedStatement2.setString(1, factionName);
			//On exécute la requete SQL
			ResultSet rs = preparedStatement2.executeQuery();
			while(rs.next())
			{
				inv.setItem(rs.getInt(1), parse(main, rs.getString(2)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return inv;
	}

	public static ItemStack parse(Fireland _main, String input) {
		return ItemSerializer.deserialize(_main, input);
	}

	public Inventory LoadStorage(String factionName, int rang)
	{
		if(!main.hashMapManager.getStorageFactionMap().containsKey(factionName))
		{
			main.hashMapManager.addStorageFactionMap(factionName, loadAllItems(factionName, rang));
		}
		return main.hashMapManager.getStorageFactionMap().get(factionName);
	}

	public String GetColorCode(String factionName)
	{
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();

			final PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT color_code FROM faction WHERE name = ?");
			preparedStatement2.setString(1, factionName);
			//On exécute la requete SQL
			ResultSet rs = preparedStatement2.executeQuery();
			if(rs.next())
			{
				return rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void SetColorCode(String factionName, String colorCode)
	{
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();

			final PreparedStatement preparedStatement2 = connection.prepareStatement("UPDATE faction SET color_code = ? WHERE name = ?");
			preparedStatement2.setString(1, colorCode);
			preparedStatement2.setString(2, factionName);
			//On exécute la requete SQL
			preparedStatement2.execute();
			if(getFactionInfo(factionName).DoShowPrefix())
			{
				for(Player p : Bukkit.getOnlinePlayers())
				{
					String name = playerFactionName(p);
					if(name.equalsIgnoreCase(factionName))
					{
						if(main.hashMapManager.getFactionPrefixMap().containsKey(p.getUniqueId()))
						{
							main.hashMapManager.getFactionPrefixMap().replace(p.getUniqueId(), colorCode+factionName+" > §r");
						}
						else
						{
							main.hashMapManager.addFactionPrefixMap(p.getUniqueId(), colorCode+factionName+" > §r");
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



	public int[] GetAmeliorationsUpgrades(int rang)
	{
		int[] amelioration = new int[3];
		//0 : Nombre de joueurs max
		//1 : Nombre de thunes max
		//2 : Nombre de slots max
		if(rang == 0)
		{
			amelioration[0] = 3;
			amelioration[1] = 2000;
		}
		else if(rang == 1)
		{
			amelioration[0] = 4;
			amelioration[1] = 3000;
		}
		else if(rang == 2)
		{
			amelioration[0] = 6;
			amelioration[1] = 5000;
		}
		else if(rang == 3)
		{
			amelioration[0] = 7;
			amelioration[1] = 8000;
		}
		else if(rang == 4)
		{
			amelioration[0] = 8;
			amelioration[1] = 12000;
		}
		else if(rang == 5)
		{
			amelioration[0] = 10;
			amelioration[1] = 15000;
		}
		else if(rang == 6)
		{
			amelioration[0] = 12;
			amelioration[1] = 20000;
		}
		else if(rang == 7)
		{
			amelioration[0] = 15;
			amelioration[1] = 28000;
		}
		else if(rang == 8)
		{
			amelioration[0] = 20;
			amelioration[1] = 38000;
		}
		else if(rang == 9)
		{
			amelioration[0] = 25;
			amelioration[1] = 50000;
		}
		else if(rang == 10)
		{
			amelioration[0] = 30;
			amelioration[1] = 70000;
		}
		else if(rang >= 11)
		{
			amelioration[0] = 35;
			amelioration[1] = 100000;
		}
		return amelioration;
	}

	public void sendFactionPlayer(String factionName, String msg)
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			String name = playerFactionName(p);
			if(name.equalsIgnoreCase(factionName))
			{
				InGameUtilities.sendPlayerInformation(p, msg);
			}
		}
	}

	public ArrayList<String[]> getFactions()
	{
		ArrayList<String[]> faction = new ArrayList<>();
		final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
		try {
			//On prépare la requête SQL
			final Connection connection = firelandConnection.getConnection();

			final PreparedStatement preparedStatement = connection.prepareStatement("SELECT faction.color_code, faction.name FROM faction ORDER BY faction.upgrade DESC, faction.name, faction.created_at;");
			//On exécute la requete SQL
			ResultSet rs = preparedStatement.executeQuery();
			while(rs.next())
			{
				faction.add(new String[]{rs.getString(1), rs.getString(2)});
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return faction;
	}

	public void sendFactionList(Player p, int page)
	{
		ArrayList<String[]> factions = getFactions();
		int pageSize = 5;
		p.sendMessage("§8------------- §7Factions §8-------------");
		ComponentBuilder message = new ComponentBuilder();
		for(int i = page*pageSize; i < pageSize*(page+1) && i < factions.size(); i++)
		{
			message.append("§8"+(i+1)+". "+factions.get(i)[0]+factions.get(i)[1])
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aVoir les informations de la faction "+factions.get(i)[1]).create()))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction info "+factions.get(i)[1]));
			p.spigot().sendMessage(message.create());
			message = new ComponentBuilder();
		}
		if(page > 0)
		{
			message.append("§2[<]")
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§2Page précédente").create()))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction list "+(page-1)));
		}
		else
		{
			message.append("§7[<]")
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").create()))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
		}
		message.append("§8 ----------------------------- ")
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").create()))
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
		if(page*pageSize + pageSize < factions.size())
		{
			message.append("§4[>]")
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§2Page suivante").create()))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction list "+(page+1)));
		}
		else
		{
			message.append("§7[>]")
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").create()))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
		}
		p.spigot().sendMessage(message.create());
	}

	public void setSender(Player _p)
	{
		sender = _p;
	}
}
