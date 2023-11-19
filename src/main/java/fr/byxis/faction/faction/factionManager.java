package fr.byxis.faction.faction;

import fr.byxis.faction.faction.events.PlayerJoinFactionEvent;
import fr.byxis.faction.faction.events.PlayerLeaveFactionEvent;
import fr.byxis.faction.housing.BunkerClass;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.PermissionUtilities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static fr.byxis.fireland.utilities.BasicUtilities.getUuid;
import static fr.byxis.jeton.JetonManager.sendPlayerFacture;

public class factionManager implements Listener, CommandExecutor  {
	
	final private Fireland main;

	public factionManager(Fireland main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if(!(sender instanceof Player p))
		{
			return false;
		}


		FactionFunctions factionFunctions = new FactionFunctions(main, p);

		FactionInformation factionInfo = factionFunctions.getFactionInfo(factionFunctions.playerFactionName(p));
		FactionPlayerInformation playerInfo = factionFunctions.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
		if (args.length == 0)
		{
			String factionName = factionFunctions.playerFactionName(p);
			if(factionName.equals(""))
			{
				InGameUtilities.sendPlayerError(p, "Vous n'avez pas de faction.");
				//Renvoyer le classement des plus grandes facs
				return true;
			}
			else
			{
				factionFunctions.factionInformationSender(p, factionFunctions.getFactionInfo(factionName), factionFunctions.getPlayersFromFaction(factionName));
			}

		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("invite") && p.hasPermission("fireland.command.faction.invite"))
		{

			if(factionInfo != null)
			{
				if(!(playerInfo.getRole() < 1))
				{
					if(!factionFunctions.factionHasMaxPlayer(factionFunctions.playerFactionName(p)))
					{
						Player victim = Bukkit.getPlayer(args[1]);
						if(victim == null)
						{
							InGameUtilities.sendPlayerError(p, "Le joueur n'a pas été trouvé.");
						}
						else
						{

							if(!victim.getName().equalsIgnoreCase(p.getName()))
							{
								if(factionFunctions.playerFactionName(victim).equalsIgnoreCase(factionInfo.getName()))
								{
									InGameUtilities.sendPlayerError(p, "Ce joueur est déjà dans votre faction !");
								}
								else
								{
									factionFunctions.InitInviteFaction(p, victim, playerInfo.getFactionName());
								}
							}
							else
							{
								InGameUtilities.sendPlayerError(p, "Vous ne pouvez pas vous inviter vous même !");
							}
						}
					}
					else
					{
						InGameUtilities.sendPlayerError(p, "La faction n'a pas assez de place pour un nouveau joueur.");
					}
				}
				else
				{
					InGameUtilities.sendPlayerError(p, "Vous n'avez pas un rôle assez élevé pour effectuer cette action.");
				}
			}
			else
			{
				InGameUtilities.sendPlayerError(p, "Vous n'avez pas de faction.");
			}
		}
		else if (args[0].equalsIgnoreCase("create") && p.hasPermission("fireland.command.faction.create"))
		{
			if(args.length != 2)
			{
				InGameUtilities.sendPlayerError(p, "Vous ne pouvez pas mettre d'espace dans le nom de votre faction !");
				return false;
			}
			if (args[1].length() <= 16)
			{
				double money = main.eco.getBalance(p);
				if(money >= 1000 && factionFunctions.creatingFaction(p, args[1])) {
					main.eco.withdrawPlayer(p, 1000);
					InGameUtilities.sendPlayerSucces(p, "Vous avez créé la faction "+args[1]);
					for(Player op : Bukkit.getOnlinePlayers())
					{
						if(op != p)
						{
							InGameUtilities.sendPlayerInformation(op, "La faction §d"+args[1]+"§7 a été créée par "+p.getName()+" !");
						}
					}
					ThingsToDoWhileJoining(p, args[1], factionFunctions.getFactionInfo(args[1]));
				}
				else
				{
					InGameUtilities.sendPlayerError(p, "Vous n'avez pas d'argent, créer une faction coûte 1000$.");
				}
			}
			else
			{
				InGameUtilities.sendPlayerError(p, "Le nom de la faction est trop long.");
			}
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("join") && p.hasPermission("fireland.command.faction.join"))
		{
			if(factionInfo != null)
			{
				InGameUtilities.sendPlayerError(p, "Vous avez déjà une faction.");
				return true;
			}
			if(factionFunctions.isInvitedToFaction(p, args[1]))
			{
				if(!factionFunctions.factionHasMaxPlayer(args[1]))
				{
					factionFunctions.joinFaction(p, factionFunctions.getExactFactionNameFromInvite(p, args[1]));
					factionInfo = factionFunctions.getFactionInfo(args[1]);
					ThingsToDoWhileJoining(p, args[1], factionInfo);
					factionFunctions.sendFactionPlayer(factionInfo.getName(), p.getName()+" a rejoint la faction !");
				}
				else
				{
					InGameUtilities.sendPlayerError(p, "La faction n'a pas assez de place pour un nouveau joueur.");
				}
			}
			else
			{
				InGameUtilities.sendPlayerError(p, "Vous n'avez pas été invité dans cette faction.");
			}
		}
		else if (args[0].equalsIgnoreCase("leave") && p.hasPermission("fireland.command.faction.leave"))
		{
			if(factionInfo == null)
			{
				InGameUtilities.sendPlayerError(p, "Vous n'avez pas de faction.");
			}
			else
			{
				if(playerInfo.getRole() == 2)
				{
					InGameUtilities.sendPlayerError(p, "Vous êtes leader et donc vous ne pouvez pas quitter la faction. Pour supprimer la faction, faites /faction disband. Pour désigner un nouveau leader, faites /faction promote <nom>.");
				}
				else
				{
					PlayerLeaveFactionEvent event = new PlayerLeaveFactionEvent(p, factionInfo.getName(), false);
					Bukkit.getPluginManager().callEvent(event);
					factionFunctions.leaveFaction(p, factionInfo.getLeader());
					factionFunctions.sendFactionPlayer(factionInfo.getName(), "§c"+p.getName()+" a quitté la faction.");
				}
			}

		}
		else if (args[0].equalsIgnoreCase("disband") && p.hasPermission("fireland.command.faction.disband"))
		{
			if(factionInfo == null)
			{
				InGameUtilities.sendPlayerError(p, "Vous n'avez pas de faction.");
			}
			else if (playerInfo.getRole() < 2)
			{
				InGameUtilities.sendPlayerError(p, "Vous n'avez pas un rôle assez élevé pour effectuer cette action.");
			}
			else if(args.length > 1 && args[1].equalsIgnoreCase("yes"))
			{
				factionFunctions.deleteFaction(p);
				for(Player op : Bukkit.getOnlinePlayers())
				{
					PlayerLeaveFactionEvent event = new PlayerLeaveFactionEvent(op, factionInfo.getName(), true);
					Bukkit.getPluginManager().callEvent(event);
					if(op != p)
					{
						InGameUtilities.sendPlayerError(op, "La faction "+args[1]+" a été supprimée par "+p.getName()+".");
					}
				}
			}
			else
			{
				InGameUtilities.sendPlayerError(p, "Êtes-vous sûr de vouloir supprimer votre faction ? Vous perdrez tout !");
				InGameUtilities.sendPlayerError(p, "Faites §4/faction disband yes§c pour la supprimer.");
			}

		}
		else if (args[0].equalsIgnoreCase("info") && p.hasPermission("fireland.command.faction.info"))
		{
			if(args.length == 1)
			{
				String factionName = factionFunctions.playerFactionName(p);
				if(factionInfo == null)
				{
					InGameUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
					return true;
				} else {
					factionFunctions.factionInformationSender(p, factionFunctions.getFactionInfo(factionName), factionFunctions.getPlayersFromFaction(factionName));
				}
			}
			else
			{
				FactionInformation infos = factionFunctions.getFactionInfo(args[1]);
				if (infos == null)
				{
					InGameUtilities.sendPlayerError(p, "Faction inconnue !");
				}
				else
				{
					factionFunctions.factionInformationSender(p, infos, factionFunctions.getPlayersFromFaction(args[1]));
					return true;
				}
			}
		}
		else if(args[0].equalsIgnoreCase("money") && p.hasPermission("fireland.command.faction.money"))
		{
			if(args.length == 1)
			{
				if(factionInfo == null)
				{
					InGameUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
					return true;
				}
				if(playerInfo != null)
				{
					InGameUtilities.sendPlayerInformation(p, "Votre faction a §6"+factionInfo.getCurrentMoney()+"$§7/§6"+factionInfo.getMaxMoney()+"$§7.");
				}
				else
				{
					InGameUtilities.sendPlayerError(p, "Mauvaise formulation de la commande !");
				}
			}
			else
			{
				if(factionInfo != null)
				{
					InGameUtilities.sendPlayerInformation(p, "La faction §r"+factionInfo.getName()+"§7 à §r"+factionInfo.getCurrentMoney()+"§7/§r"+factionInfo.getMaxMoney()+"§7$");
				}
			}
		}
		else if(args[0].equalsIgnoreCase("upgrade") && p.hasPermission("fireland.command.faction.upgrade"))
		{
			if(factionInfo == null)
			{
				InGameUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
				return true;
			}
			else if(args.length == 2 && args[1].equalsIgnoreCase("yes") && factionInfo.getCurrentUpgrade() < 11)
			{
				if(playerInfo.getRole() == 2)
				{
					if(factionInfo.getCurrentMoney() >= factionInfo.getMaxMoney())
					{
						factionFunctions.take(factionInfo.getName(), factionInfo.getMaxMoney());
						factionFunctions.upgradeFaction(factionInfo.getName());
						factionFunctions.sendFactionPlayer(factionInfo.getName(), "La faction passe au niveau §d§l"+(factionInfo.getCurrentUpgrade()+1)+" §r§7!");
					}
					else
					{
						InGameUtilities.sendPlayerError(p, "La faction n'a pas assez d'argent !");
					}
				}
				else
				{
					InGameUtilities.sendPlayerError(p, "Seul le chef peut améliorer la faction !");
				}
			}
			else if(args.length == 2 && args[1].equalsIgnoreCase("yes") && factionInfo.getCurrentUpgrade() == 11)
			{
				InGameUtilities.sendPlayerInformation(p, "Votre faction est déjà au niveau maximum !");
			}
			else
			{
				FactionInformation factionUpgradeInfo = factionFunctions.getFactionInfoWithAmeliorations(factionFunctions.playerFactionName(p));
				InGameUtilities.sendPlayerInformation(p, "Il vous faut §6"+factionUpgradeInfo.getCurrentMoney()+"§7/§6"+factionInfo.getMaxMoney()+"$§7 pour améliorer votre faction !");
				InGameUtilities.sendPlayerInformation(p, "Vous bénéficierez d'un nouveau maximum d'argent de §d"+
						factionUpgradeInfo.getMaxMoney()+"$§7, de §d"+factionUpgradeInfo.getCurrentChestSize()
						+"§7 slots de stockage et vous pourrez recruter §d"+factionUpgradeInfo.getMaxNbrOfPlayers()+"§7 personnes !");
				InGameUtilities.sendPlayerInformation(p, "Pour l'améliorer, faites §c/faction upgrade yes§7.");
				p.closeInventory();
			}
		}
		else if(args[0].equalsIgnoreCase("demote") && p.hasPermission("fireland.command.faction.demote"))
		{
			if(factionInfo == null)
			{
				InGameUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
				return true;
			}
			if (args.length >= 2)
			{
					OfflinePlayer victim = Bukkit.getOfflinePlayer(BasicUtilities.getUuid(args[1]));
					if (!(victim.hasPlayedBefore()) || p.getName().equalsIgnoreCase(victim.getName()))
					{
						InGameUtilities.sendPlayerError(p, "Personne non trouvée.");
					}
					else
					{
						FactionPlayerInformation victimInfo = factionFunctions.GetInformationOfPlayerInAFaction(victim.getUniqueId(), victim.getName());
						if(!victimInfo.getFactionName().equalsIgnoreCase(playerInfo.getFactionName()))
						{
							p.sendMessage("§d"+victimInfo.getName()+" §cne fait pas partie de votre faction !");
						}
						else
						{
							if(playerInfo.getRole() == 2)
							{
								if(victimInfo.getRole() == 0 )
								{
									InGameUtilities.sendPlayerError(p, "Cette personne a déjà le rang minimal !");
								}
								else if(victimInfo.getRole() == 1)
								{
									factionFunctions.ChangePlayerRank(victim.getUniqueId(), playerInfo.getFactionName(), -1);
									InGameUtilities.sendPlayerInformation(p, "Le joueur §d"+victim.getName()+"§a a été rétrograder.");
									factionFunctions.sendFactionPlayer(factionInfo.getName(), "§cLe joueur "+victim.getName()+" a été rétrogradé.");
								}
								else
								{
									InGameUtilities.sendPlayerError(p, "Une erreur s'est produite, merci de contacter le staff");
								}
							}
							else
							{
								InGameUtilities.sendPlayerError(p, "Seul le chef peut rétrograder une personne !");
							}
						}

				}

			}

		}
		else if(args[0].equalsIgnoreCase("promote"))
		{
			if (args.length >= 2)
			{
				if(factionInfo == null)
				{
					InGameUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
				}
				else
				{
					OfflinePlayer victim =  Bukkit.getOfflinePlayer(BasicUtilities.getUuid(args[1]));
					if (!(victim.hasPlayedBefore()) || p.getName().equalsIgnoreCase(victim.getName()))
					{
						InGameUtilities.sendPlayerError(p, "Personne non trouvée.");
					}
					else
					{
						FactionPlayerInformation victimInfo = factionFunctions.GetInformationOfPlayerInAFaction(victim.getUniqueId(), victim.getName());
						if(!victimInfo.getFactionName().equalsIgnoreCase(playerInfo.getFactionName()))
						{
							p.sendMessage("§d"+victimInfo.getName()+" §cne fait pas partie de votre faction !");
						}
						else
						{
							if(playerInfo.getRole() == 2)
							{
								if(victimInfo.getRole() == 0 )
								{
									factionFunctions.ChangePlayerRank(victim.getUniqueId(), playerInfo.getFactionName(), 1);
									factionFunctions.sendFactionPlayer(factionInfo.getName(), "Le joueur "+victim.getName()+" a été promu !");

								}
								else if(victimInfo.getRole() == 1)
								{
									if(args.length >= 3 && args[2].equalsIgnoreCase("yes"))
									{
										factionFunctions.ChangePlayerRank(victim.getUniqueId(), playerInfo.getFactionName(), 1);
										InGameUtilities.sendPlayerInformation(p, "Le joueur §d"+victim.getName()+"§7 est devenu chef.");
										if(victim.isOnline())
										{
											InGameUtilities.sendPlayerError(p, "Vous venez de devenir chef de votre faction.");
										}
										factionFunctions.ChangePlayerRank(p.getUniqueId(), playerInfo.getFactionName(), -1);
										InGameUtilities.sendPlayerError(p, "Vous venez d'être rétrogradé.");
									}
									else
									{
										InGameUtilities.sendPlayerError(p, "Cette personne est un modérateur de la faction");
										InGameUtilities.sendPlayerError(p, "Si vous voulez lui transférer le pouvoir, faites §4/faction promote "+victim.getName()+" yes§c.");
									}
								}
								else
								{
									InGameUtilities.sendPlayerError(p, "Une erreur s'est produite, merci de contacter le staff");
								}
							}
							else
							{
								InGameUtilities.sendPlayerError(p, "Seul le chef peut rétrograder une personne !");
							}
						}
					}
				}
			}
			else
			{
				InGameUtilities.sendPlayerError(p, "Utilisation : /faction promote <joueur>");
			}
		}
		else if(args[0].equalsIgnoreCase("rename") && playerInfo.getRole() == 2  && p.hasPermission("fireland.command.faction.rename"))
		{
			if(args.length >= 2)
			{
				double money = factionFunctions.GetFactionMoney(playerInfo.getFactionName());
				if(money >= 4000*factionInfo.getCurrentUpgrade())
				{
					if (factionFunctions.take(factionInfo.getName(), 4000*factionInfo.getCurrentUpgrade())) {
						factionFunctions.sendFactionPlayer(factionInfo.getName(), "La faction a été renommée "+args[1]+" !");
						factionFunctions.renameFaction(playerInfo.getFactionName(), args[1]);
					}
				}
				else
				{
					InGameUtilities.sendPlayerError(p, "La faction n'a pas assez d'argent ! Renommer sa faction coûte "+(4000*factionInfo.getCurrentUpgrade())+"$ !");
				}
			}
		}
		else if(args[0].equalsIgnoreCase("deposit") && p.hasPermission("fireland.command.faction.deposit"))
		{
			if (factionInfo == null) {

				InGameUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
				return true;
			}
			if (args.length == 2)
			{
				int money = Integer.parseInt(args[1]);
				if (main.eco.getBalance(p) >= money)
				{
					if (money + factionInfo.getCurrentMoney() <= factionInfo.getMaxMoney() && money >= 0)
					{
						if (factionFunctions.deposit(factionInfo.getName(), money))
						{
							main.eco.withdrawPlayer(p, money);
						}
					} else
					{
						InGameUtilities.sendPlayerError(p, "La faction ne peut pas contenir autant d'argent !");
					}
				} else
				{
					InGameUtilities.sendPlayerError(p, "Vous n'avez pas assez d'argent !");
				}
			} else
			{
				InGameUtilities.sendPlayerError(p, "Utilisation : /faction deposit <nombre>");
			}
		}
		else if(args[0].equalsIgnoreCase("withdraw") && p.hasPermission("fireland.command.faction.withdraw")) {
			if (factionInfo == null) {
				InGameUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
				return true;
			}
			if (args.length == 2) {
				if (playerInfo.getRole() == 2 )
				{
					int amount = Integer.parseInt(args[1]);
					if (factionInfo.getCurrentMoney()-amount >=0 && amount >= 0)
					{
						if (factionFunctions.take(factionInfo.getName(), amount))
						{
							InGameUtilities.sendPlayerInformation(p, "Vous avez retiré " + amount + "$ !");
							main.eco.depositPlayer(p, amount);
						}
					}
					else
					{
						InGameUtilities.sendPlayerError(p, "La faction n'a pas assez d'argent !");
					}
				}
				else
				{
					InGameUtilities.sendPlayerError(p, "Seul le leader peut effectuer cette action !");
				}
			} else {
				InGameUtilities.sendPlayerError(p, "Utilisation : /faction withdraw <nombre>");
			}
		}
		else if(args[0].equalsIgnoreCase("kick"))
		{
			if (factionInfo == null) {
				InGameUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
				return true;
			}
			if (args.length == 2) {
				if (playerInfo.getRole() >= 1)
				{
					OfflinePlayer victim =  Bukkit.getOfflinePlayer(BasicUtilities.getUuid(args[1]));
					if (p.getName().equalsIgnoreCase(victim.getName()))
					{
						InGameUtilities.sendPlayerError(p, "Vous ne pouvez pas vous exclure vous-même !");
					}
					else
					{
						FactionPlayerInformation victimInfo = factionFunctions.GetInformationOfPlayerInAFaction(victim.getUniqueId(), victim.getName());
						if(!victimInfo.getFactionName().equalsIgnoreCase(playerInfo.getFactionName()))
						{
							p.sendMessage("§d"+victimInfo.getName()+" §cne fait pas partie de votre faction !");
						}
						else
						{
							if(playerInfo.getRole() == 1 && victimInfo.getRole() == 1)
							{
								p.sendMessage("§cVous ne pouvez pas exclure quelqu'un de même rang que vous !");
							}
							PlayerLeaveFactionEvent event = new PlayerLeaveFactionEvent((Player) victim, factionInfo.getName(), true);
							Bukkit.getPluginManager().callEvent(event);
							InGameUtilities.sendPlayerError(p, "Vous avez exclu §4"+victim.getName()+" §c !");
							factionFunctions.kickPlayer(factionInfo, (Player) victim);
							factionFunctions.sendFactionPlayer(factionInfo.getName(), "§cLe joueur "+victim.getName()+" a été exclu de la faction.");

						}
					}
				}
				else
				{
					InGameUtilities.sendPlayerError(p, "Seul le leader et les modérateurs peuvent effectuer cette action !");
				}
			}
			else
			{
				InGameUtilities.sendPlayerError(p, "Utilisation : /faction kick joueur");
			}
		}
		else if(args[0].equalsIgnoreCase("perk") && p.hasPermission("fireland.command.faction.perk"))
		{
			if (factionInfo == null) {
				InGameUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
				return true;
			}
			if (args.length == 2) {
				if (playerInfo.getRole() == 2)
				{
					if(isPerk(args[1]))
					{
						if(!factionFunctions.HasPerk(factionInfo.getName(), args[1]))
						{
							double price = getCostPerk(args[1]);
							if(factionInfo.getCurrentMoney() >= price)
							{
								factionFunctions.take(factionInfo.getName(), price);
								factionFunctions.AddPerk(factionInfo.getName(), args[1]);
								InGameUtilities.sendPlayerInformation(p, "Vous avez acheté une nouvelle amélioration pour votre faction !");
								factionFunctions.sendFactionPlayer(factionInfo.getName(), "La faction a débloqué une nouvelle amélioration !");
							}
							else
							{
								InGameUtilities.sendPlayerError(p, "La faction n'a pas assez d'argent !");
							}
						}
						else
						{
							InGameUtilities.sendPlayerError(p, "La faction a déjà cette amélioration !");
						}
					}
				}
				else
				{
					InGameUtilities.sendPlayerError(p, "Seul le leader peut effectuer cette action !");
				}
			}
			else
			{
				InGameUtilities.sendPlayerError(p, "Utilisation : /faction perk <perk>");
			}
		}
		else if(args[0].equalsIgnoreCase("list"))
		{
			if(args.length == 1)
			{
				factionFunctions.sendFactionList(p, 0);
			}
			else if(args.length == 2)
			{
				try
				{
					int amount = Integer.parseInt(args[1]);
					factionFunctions.sendFactionList(p, amount);
				}
				catch (NumberFormatException e)
				{
					InGameUtilities.sendPlayerError(p, "Utilisation: /faction list [page]");
					return false;
				}
			}
		}
		else
		{
			InGameUtilities.sendPlayerError(p, "Mauvaise formulation de la commande !");
		}
		return false;
	}


	private boolean isPerk(String perk)
	{
		boolean result = false;
		if(perk.equalsIgnoreCase("friendly_fire"))
		{
			result = true;
		}
		else if(perk.equalsIgnoreCase("show_nickname"))
		{
			result = true;
		}
		else if(perk.equalsIgnoreCase("has_skin"))
		{
			result = true;
		}
		else if(perk.equalsIgnoreCase("show_prefix"))
		{
			result = true;
		}
		else if(perk.equalsIgnoreCase("capture_perk"))
		{
			result = true;
		}
		else if(perk.equalsIgnoreCase("zone_tp"))
		{
			result = true;
		}
		return result;
	}

	public static double getCostPerk(String perk)
	{
		double amount = 0;
		if(perk.equalsIgnoreCase("friendly_fire"))
		{
			amount = 8000;
		}
		else if(perk.equalsIgnoreCase("show_nickname"))
		{
			amount = 6000;
		}
		else if(perk.equalsIgnoreCase("has_skin"))
		{
			amount = 16000;
		}
		else if(perk.equalsIgnoreCase("show_prefix"))
		{
			amount = 2000;
		}
		else if(perk.equalsIgnoreCase("capture_perk"))
		{
			amount = 4000;
		}
		return amount;
	}

	@EventHandler
	private void ThingsToDoWhileLeaving(PlayerLeaveFactionEvent e)
	{
		Player p = e.getPlayer();
		BunkerClass bk = main.bunkerManager.FindBunkerEnteredByPlayer(p.getName());
		if(bk != null)
		{
			bk.Leave(p);
		}
		if(main.hashMapManager.getFactionMap().containsKey(p.getUniqueId()))
		{
			main.hashMapManager.removeFactionMap(p.getUniqueId());
		}
		if(main.hashMapManager.getFactionPrefixMap().containsKey(p.getUniqueId()))
		{
			main.hashMapManager.removeFactionMap(p.getUniqueId());
		}
		PermissionUtilities.removePermission(p, "csp.skin.Faction");
	}

	private void ThingsToDoWhileJoining(Player p, String name, FactionInformation factionInfo)
	{
		FactionFunctions ff = new FactionFunctions(main, p);
		if(factionInfo == null)
		{
			return;
		}
		if(factionInfo.hasFriendlyFirePerk())
		{
			main.hashMapManager.addFactionMap(p.getUniqueId(), name);
		}
		if(factionInfo.DoShowPrefix())
		{
			main.hashMapManager.addFactionPrefixMap(p.getUniqueId(), factionInfo.getColorcode()+factionInfo.getName()+" > §r");
		}
		if(factionInfo.hasSkinPerk())
		{
			PermissionUtilities.addPermission(p, "csp.skin.Faction");
		}
	}
}
