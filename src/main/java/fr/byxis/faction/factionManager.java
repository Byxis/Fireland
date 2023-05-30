package fr.byxis.faction;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.PermissionUtilities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

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
				BasicUtilities.sendPlayerError(p, "Vous n'avez pas de faction.");
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
							BasicUtilities.sendPlayerError(p, "Le joueur n'a pas été trouvé.");
						}
						else
						{
							factionFunctions.InitInviteFaction(p, victim, playerInfo.getFactionName());
						}
					}
					else
					{
						BasicUtilities.sendPlayerError(p, "La faction n'a pas assez de place pour un nouveau joueur.");
					}
				}
				else
				{
					BasicUtilities.sendPlayerError(p, "Vous n'avez pas un rôle assez élevé pour effectuer cette action.");
				}
			}
			else
			{
				BasicUtilities.sendPlayerError(p, "Vous n'avez pas de faction.");
			}
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("create") && p.hasPermission("fireland.command.faction.create"))
		{
			if (args[1].length() <= 36)
			{
				double money = main.eco.getBalance(p);
				if(money >= 10000) {
					main.eco.withdrawPlayer(p, 10000);
					factionFunctions.creatingFaction(p, args[1]);
					for(Player op : Bukkit.getOnlinePlayers())
					{
						if(op != p)
						{
							BasicUtilities.sendPlayerInformation(p, "La faction §d"+args[1]+"§7 a été créé par "+p.getName()+" !");
						}
					}
				}
				else
				{
					BasicUtilities.sendPlayerError(p, "Vous n'avez pas d'argent.");
				}
			}
			else
			{
				BasicUtilities.sendPlayerError(p, "Le nom de la faction est trop long.");
			}
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("join") && p.hasPermission("fireland.command.faction.join"))
		{
			if(factionInfo != null)
			{
				BasicUtilities.sendPlayerError(p, "Vous avez déjà une faction.");
				return true;
			}
			if(factionFunctions.isInvitedToFaction(p, args[1]))
			{
				if(!factionFunctions.factionHasMaxPlayer(args[1]))
				{
					factionFunctions.joinFaction(p, factionFunctions.getExactFactionNameFromInvite(p, args[1]));
					factionInfo = factionFunctions.getFactionInfo(args[1]);
					ThingsToDoWhileJoining(p, args[1], factionInfo);
					factionFunctions.sendFactionPlayer(factionInfo.getName(), p.getName()+" a rejoins la faction !");
				}
				else
				{
					BasicUtilities.sendPlayerError(p, "La faction n'a pas assez de place pour un nouveau joueur.");
				}
			}
			else
			{
				BasicUtilities.sendPlayerError(p, "Vous n'avez pas été invité dans cette faction.");
			}
		}
		else if (args[0].equalsIgnoreCase("leave") && p.hasPermission("fireland.command.faction.leave"))
		{
			if(factionInfo == null)
			{
				BasicUtilities.sendPlayerError(p, "Vous n'avez pas de faction.");
			}
			else
			{
				if(playerInfo.getRole() == 2)
				{
					BasicUtilities.sendPlayerError(p, "Vous êtes leader et donc vous ne pouvez pas quitter la faction. Pour supprimer la faction, faites /faction disband. Pour désigner un nouveau leader, faites /faction promote <nom>.");
				}
				else
				{
					ThingsToDoWhileLeaving(p);
					factionFunctions.leaveFaction(p, factionInfo.getLeader());
					factionFunctions.sendFactionPlayer(factionInfo.getName(), "§c"+p.getName()+" a quitté la faction.");
				}
			}

		}
		else if (args[0].equalsIgnoreCase("disband") && p.hasPermission("fireland.command.faction.disband"))
		{
			if(factionInfo == null)
			{
				BasicUtilities.sendPlayerError(p, "Vous n'avez pas de faction.");
			}
			else if (playerInfo.getRole() < 2)
			{
				BasicUtilities.sendPlayerError(p, "Vous n'avez pas un rôle assez élevé pour effectuer cette action.");
			}
			else if(args.length > 1 && args[1].equalsIgnoreCase("yes"))
			{
				factionFunctions.deleteFaction(p);
				ThingsToDoWhileLeaving(p);
				for(Player op : Bukkit.getOnlinePlayers())
				{
					if(op != p)
					{
						BasicUtilities.sendPlayerError(op, "La faction "+args[1]+" a été supprimée par "+p.getName()+".");
					}
				}
			}
			else
			{
				BasicUtilities.sendPlayerError(p, "Êtes-vous sûr de vouloir supprimer votre faction ? Vous perdrez tout !");
				BasicUtilities.sendPlayerError(p, "Faites §4/faction disband yes§c pour la supprimer.");
			}

		}
		else if (args[0].equalsIgnoreCase("info") && p.hasPermission("fireland.command.faction.info"))
		{
			if(args.length == 1)
			{
				String factionName = factionFunctions.playerFactionName(p);
				if(factionInfo == null)
				{
					BasicUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
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
					BasicUtilities.sendPlayerError(p, "Faction inconnue !");
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
					BasicUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
					return true;
				}
				if(playerInfo != null)
				{
					BasicUtilities.sendPlayerInformation(p, "Votre faction a §6"+factionInfo.getCurrentMoney()+"$§7/§6"+factionInfo.getMaxMoney()+"$§7.");
				}
				else
				{
					BasicUtilities.sendPlayerError(p, "Mauvaise formulation de la commande !");
				}
			}
			else
			{
				if(factionInfo != null)
				{
					BasicUtilities.sendPlayerInformation(p, "La faction §r"+factionInfo.getName()+"§7 à §r"+factionInfo.getCurrentMoney()+"§7/§r"+factionInfo.getMaxMoney()+"§7$");
				}
			}
		}
		else if(args[0].equalsIgnoreCase("upgrade") && p.hasPermission("fireland.command.faction.upgrade"))
		{
			if(factionInfo == null)
			{
				BasicUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
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
						BasicUtilities.sendPlayerError(p, "La faction n'a pas assez d'argent !");
					}
				}
				else
				{
					BasicUtilities.sendPlayerError(p, "Seul le chef peut améliorer la faction !");
				}
			}
			else if(args.length == 2 && args[1].equalsIgnoreCase("yes") && factionInfo.getCurrentUpgrade() == 11)
			{
				BasicUtilities.sendPlayerInformation(p, "Votre faction est déjà au niveau maximum !");
			}
			else
			{
				FactionInformation factionUpgradeInfo = factionFunctions.getFactionInfoWithAmeliorations(factionFunctions.playerFactionName(p));
				BasicUtilities.sendPlayerInformation(p, "Il vous faut §6"+factionUpgradeInfo.getCurrentMoney()+"§7/§6"+factionInfo.getMaxMoney()+"$§7 pour améliorer votre faction !");
				BasicUtilities.sendPlayerInformation(p, "Vous bénéficierez d'un nouveau maximum d'argent de §d"+
						factionUpgradeInfo.getMaxMoney()+"$§7, de §d"+factionUpgradeInfo.getCurrentChestSize()
						+"§7 slots de stockage et vous pourrez recruter §d"+factionUpgradeInfo.getMaxNbrOfPlayers()+"§7 personnes !");
				BasicUtilities.sendPlayerInformation(p, "Pour l'améliorer, faites §c/faction upgrade yes§7.");
			}
		}
		else if(args[0].equalsIgnoreCase("demote") && p.hasPermission("fireland.command.faction.demote"))
		{
			if(factionInfo == null)
			{
				BasicUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
				return true;
			}
			if (args.length >= 2)
			{
					OfflinePlayer victim = Bukkit.getOfflinePlayer(BasicUtilities.getUuid(args[1]));
					if (!(victim.hasPlayedBefore()) || p.getName().equalsIgnoreCase(victim.getName()))
					{
						BasicUtilities.sendPlayerError(p, "Personne non trouvée.");
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
									BasicUtilities.sendPlayerError(p, "Cette personne a déjà le rang minimal !");
								}
								else if(victimInfo.getRole() == 1)
								{
									factionFunctions.ChangePlayerRank(victim.getUniqueId(), playerInfo.getFactionName(), -1);
									BasicUtilities.sendPlayerInformation(p, "Le joueur §d"+victim.getName()+"§a a été rétrograder.");
									factionFunctions.sendFactionPlayer(factionInfo.getName(), "§cLe joueur "+victim.getName()+" a été rétrogradé.");
								}
								else
								{
									BasicUtilities.sendPlayerError(p, "Une erreur s'est produite, merci de contacter le staff");
								}
							}
							else
							{
								BasicUtilities.sendPlayerError(p, "Seul le chef peut rétrograder une personne !");
							}
						}

				}

			}

		}
		else if(args[0].equalsIgnoreCase("promote") && p.hasPermission("fireland.command.faction.promote"))
		{
			if (args.length >= 2)
			{
				if(factionInfo == null)
				{
					BasicUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
				}
				else
				{
					OfflinePlayer victim =  Bukkit.getOfflinePlayer(BasicUtilities.getUuid(args[1]));
					if (!(victim.hasPlayedBefore()) || p.getName().equalsIgnoreCase(victim.getName()))
					{
						BasicUtilities.sendPlayerError(p, "Personne non trouvée.");
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
									BasicUtilities.sendPlayerInformation(p, "Le joueur §d"+victim.getName()+"§7 a été promu.");
									factionFunctions.sendFactionPlayer(factionInfo.getName(), "Le joueur "+victim.getName()+" a été promu !");

								}
								else if(victimInfo.getRole() == 1)
								{
									if(args.length >= 3 && args[2].equalsIgnoreCase("yes"))
									{
										factionFunctions.ChangePlayerRank(victim.getUniqueId(), playerInfo.getFactionName(), 1);
										BasicUtilities.sendPlayerInformation(p, "Le joueur §d"+victim.getName()+"§7 est devenu chef.");
										if(victim.isOnline())
										{
											BasicUtilities.sendPlayerError(p, "Vous venez de devenir chef de votre faction.");
										}
										factionFunctions.ChangePlayerRank(p.getUniqueId(), playerInfo.getFactionName(), -1);
										BasicUtilities.sendPlayerError(p, "Vous venez d'être rétrogradé.");
									}
									else
									{
										BasicUtilities.sendPlayerError(p, "Cette personne est un modérateur de la faction");
										BasicUtilities.sendPlayerError(p, "Si vous voulez lui transférer le pouvoir, faites §4/faction promote "+victim.getName()+" yes§c.");
									}
								}
								else
								{
									BasicUtilities.sendPlayerError(p, "Une erreur s'est produite, merci de contacter le staff");
								}
							}
							else
							{
								BasicUtilities.sendPlayerError(p, "Seul le chef peut rétrograder une personne !");
							}
						}
					}
				}
			}
			else
			{
				BasicUtilities.sendPlayerError(p, "Utilisation : /faction promote <joueur>");
			}
		}
		else if(args[0].equalsIgnoreCase("rename") && playerInfo.getRole() == 2  && p.hasPermission("fireland.command.faction.rename"))
		{
			if(args.length >= 2)
			{
				double money = factionFunctions.GetFactionMoney(playerInfo.getFactionName());
				if(money >= 5000)
				{
					if (factionFunctions.take(factionInfo.getName(), 5000)) {
						factionFunctions.sendFactionPlayer(factionInfo.getName(), "La faction a été renommée "+args[1]+" !");
						factionFunctions.renameFaction(playerInfo.getFactionName(), args[1]);
					}
				}
				else
				{
					BasicUtilities.sendPlayerError(p, "La faction n'a pas assez d'argent ! Renommer sa faction coûte 5000$ !");
				}
			}
		}
		else if(args[0].equalsIgnoreCase("deposit") && p.hasPermission("fireland.command.faction.deposit"))
		{
			if (factionInfo == null) {

				BasicUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
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
						BasicUtilities.sendPlayerError(p, "La faction ne peut pas contenir autant d'argent !");
					}
				} else
				{
					BasicUtilities.sendPlayerError(p, "Vous n'avez pas assez d'argent !");
				}
			} else
			{
				BasicUtilities.sendPlayerError(p, "Utilisation : /faction deposit <nombre>");
			}
		}
		else if(args[0].equalsIgnoreCase("withdraw") && p.hasPermission("fireland.command.faction.withdraw")) {
			if (factionInfo == null) {
				BasicUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
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
							BasicUtilities.sendPlayerInformation(p, "Vous avez retiré " + amount + "$ !");
							main.eco.depositPlayer(p, amount);
						}
					}
					else
					{
						BasicUtilities.sendPlayerError(p, "La faction n'a pas assez d'argent !");
					}
				}
				else
				{
					BasicUtilities.sendPlayerError(p, "Seul le leader peut effectuer cette action !");
				}
			} else {
				BasicUtilities.sendPlayerError(p, "Utilisation : /faction withdraw <nombre>");
			}
		}
		else if(args[0].equalsIgnoreCase("kick") && p.hasPermission("fireland.command.faction.kick"))
		{
			if (factionInfo == null) {
				BasicUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
				return true;
			}
			if (args.length == 2) {
				if (playerInfo.getRole() == 2)
				{
					OfflinePlayer victim =  Bukkit.getOfflinePlayer(BasicUtilities.getUuid(args[1]));
					if (!(victim.hasPlayedBefore()) || p.getName().equalsIgnoreCase(victim.getName()))
					{
						BasicUtilities.sendPlayerError(p, "Personne non trouvée.");
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
							BasicUtilities.sendPlayerError(p, "Vous avez exclu §4"+victim.getName()+" §c !");
							factionFunctions.kickPlayer(factionInfo, (Player) victim);
							factionFunctions.sendFactionPlayer(factionInfo.getName(), "§cLe joueur "+victim.getName()+" a été exclu de la faction.");

						}
					}
				}
				else
				{
					BasicUtilities.sendPlayerError(p, "Seul le leader peut effectuer cette action !");
				}
			}
			else
			{
				BasicUtilities.sendPlayerError(p, "Utilisation : /faction kick joueur");
			}
		}
		else if(args[0].equalsIgnoreCase("perk") && p.hasPermission("fireland.command.faction.perk"))
		{
			if (factionInfo == null) {
				BasicUtilities.sendPlayerError(p, "Vous n'avez pas de faction !");
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
								BasicUtilities.sendPlayerInformation(p, "Vous avez acheté une nouvelle amélioration pour votre faction !");
								factionFunctions.sendFactionPlayer(factionInfo.getName(), "La faction a débloqué une nouvelle amélioration !");
							}
							else
							{
								BasicUtilities.sendPlayerError(p, "La faction n'a pas assez d'argent !");
							}
						}
						else
						{
							BasicUtilities.sendPlayerError(p, "La faction a déjà cette amélioration !");
						}
					}
				}
				else
				{
					BasicUtilities.sendPlayerError(p, "Seul le leader peut effectuer cette action !");
				}
			}
			else
			{
				BasicUtilities.sendPlayerError(p, "Utilisation : /faction perk <perk>");
			}
		}
		else
		{
			BasicUtilities.sendPlayerError(p, "Mauvaise formulation de la commande !");
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
		return result;
	}

	private double getCostPerk(String perk)
	{
		double amount = 0;
		if(perk.equalsIgnoreCase("friendly_fire"))
		{
			amount = 8000;
		}
		else if(perk.equalsIgnoreCase("show_nickname"))
		{
			amount = 9000;
		}
		else if(perk.equalsIgnoreCase("has_skin"))
		{
			amount = 12000;
		}
		else if(perk.equalsIgnoreCase("show_prefix"))
		{
			amount = 6000;
		}
		else if(perk.equalsIgnoreCase("capture_perk"))
		{
			amount = 5000;
		}
		return amount;
	}

	private void ThingsToDoWhileLeaving(Player p)
	{
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
