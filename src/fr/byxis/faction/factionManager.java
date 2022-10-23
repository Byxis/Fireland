package fr.byxis.faction;

import fr.byxis.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Objects;

public class factionManager implements Listener, CommandExecutor  {
	
	final private Main main;

	public factionManager(Main main) {
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
				p.sendMessage("§CErreur! Vous n'avez pas de faction.");
				//Renvoyer le classement des plus grandes facs
				return true;
			}
			else
			{
				factionFunctions.factionInformationSender(p, factionFunctions.getFactionInfo(factionName), factionFunctions.getPlayersFromFaction(factionName));
			}

		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("invite"))
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
							p.sendMessage("§cPersonne non trouvée");
						}
						else
						{
							factionFunctions.InitInviteFaction(p, victim, playerInfo.getFactionName());
						}
					}
					else
					{
						p.sendMessage("§cLa faction n'a plus de place pour un nouveau joueur !");
					}
				}
				else
				{
					p.sendMessage("§cVous n'avez pas un rôle assez élevé pour réaliser cela !");
				}
			}
			else
			{
				p.sendMessage("§cVous n'avez pas de faction !");
			}
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("create"))
		{
			if (args[1].length() <= 10)
			{
				factionFunctions.creatingFaction(p, args[1]);
			}
			else
			{
				p.sendMessage("§cLe nom de la faction est trop long !");
			}
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("join"))
		{
			if(factionInfo == null)
			{
				p.sendMessage("§cVous n'avez pas de faction !");
				return true;
			}
			if(factionFunctions.isInvitedToFaction(p, args[1]))
			{
				if(!factionFunctions.factionHasMaxPlayer(args[1]))
				{
					factionFunctions.joinFaction(p, factionFunctions.getExactFactionNameFromInvite(p, args[1]));
				}
				else
				{
					p.sendMessage("§cLa faction n'a plus de place pour un nouveau joueur !");
				}
			}
			else
			{
				p.sendMessage("§cVous n'êtes pas invité dans cette faction.");
			}
		}
		else if (args[0].equalsIgnoreCase("leave"))
		{
			if(factionInfo == null)
			{
				p.sendMessage("§cVous n'avez pas de faction.");
			}
			else
			{
				factionFunctions.leaveFaction(p, factionInfo.getLeader());
			}

		}
		else if (args[0].equalsIgnoreCase("disband"))
		{
			if(factionInfo == null)
			{
				p.sendMessage("§cVous n'avez pas de faction !");
			}
			else if (playerInfo.getRole() < 2)
			{
				p.sendMessage("§cVous ne pouvez pas faire cela.");
			}
			else if(args.length > 1 && args[1].equalsIgnoreCase("yes"))
			{
				factionFunctions.deleteFaction(p);
			}
			else
			{
				p.sendMessage("§cÊtes-vous sûr de vouloir supprimer votre faction ? Vous perdrez tout !");
				p.sendMessage("§cFaites §4/faction disband yes§c pour la supprimer.");
			}

		}
		else if (args[0].equalsIgnoreCase("info"))
		{
			if(args.length == 1)
			{
				String factionName = factionFunctions.playerFactionName(p);
				if(factionInfo == null)
				{
					p.sendMessage("§cVous n'avez pas de faction !");
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
					p.sendMessage("§cFaction inconnue !");
				}
				else
				{
					factionFunctions.factionInformationSender(p, infos, factionFunctions.getPlayersFromFaction(args[1]));
					return true;
				}
			}
		}
		else if(args[0].equalsIgnoreCase("money"))
		{
			if(args.length == 1)
			{
				if(factionInfo == null)
				{
					p.sendMessage("§cVous n'avez pas de faction !");
					return true;
				}
				if(playerInfo != null)
				{
					p.sendMessage("§aVotre faction à §r"+factionInfo.getCurrentMoney()+"§a/§r"+factionInfo.getMaxMoney());
				}
				else
				{
					p.sendMessage("§cMauvaise formulation de la commande !");
				}
			}
			else
			{
				if(factionInfo != null)
				{
					p.sendMessage("§aLa faction §r"+factionInfo.getName()+"§a à §r"+factionInfo.getCurrentMoney()+"§a/§r"+factionInfo.getMaxMoney());
				}
			}
		}
		else if(args[0].equalsIgnoreCase("upgrade"))
		{
			if(factionInfo == null)
			{
				p.sendMessage("§cVous n'avez pas de faction !");
				return true;
			}
			else if(args.length == 2 && args[1].equalsIgnoreCase("yes") && factionInfo.getCurrentUpgrade() < 3)
			{
				if(playerInfo.getRole() == 2)
				{
					if(factionInfo.getCurrentMoney() == factionInfo.getMaxMoney())
					{
						factionFunctions.upgradeFaction(factionInfo.getName());
					}
					else
					{
						p.sendMessage("§cLa faction n'a pas assez d'argent !");
					}
				}
				else
				{
					p.sendMessage("§cSeul le chef peut améliorer la faction !");
				}
			}
			else
			{
				FactionInformation factionUpgradeInfo = factionFunctions.getFactionInfoWithAmeliorations(factionFunctions.playerFactionName(p));
				p.sendMessage("§aIl vous faut §r"+factionUpgradeInfo.getCurrentMoney()+"§a/§6"+factionInfo.getMaxMoney()+"§a$ pour améliorer votre faction !");
				p.sendMessage("§aVous bénéficierez d'un nouveau maximum d'argent de §d"+
						factionUpgradeInfo.getMaxMoney()+"§a$, de §d"+factionUpgradeInfo.getCurrentChestSize()
						+"§a slots de stockage et vous pourrez recruter §d"+factionUpgradeInfo.getMaxNbrOfPlayers()+"§a personnes !");
				p.sendMessage("§aPour l'améliorer, faites §c/faction upgrade yes§a.");
			}
		}
		else if(args[0].equalsIgnoreCase("demote"))
		{
			if(factionInfo == null)
			{
				p.sendMessage("§cVous n'avez pas de faction !");
				return true;
			}
			if (args.length >= 2)
			{
					OfflinePlayer victim = Bukkit.getOfflinePlayer(args[1]);
					if (!(victim.hasPlayedBefore()) || p.getName().equalsIgnoreCase(victim.getName()))
					{
						p.sendMessage("§cPersonne non trouvée.");
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
								p.sendMessage("§cCette personne a déjà le rang minimal !");
							}
							else if(victimInfo.getRole() == 1)
							{
								factionFunctions.unrankPlayer(victim.getUniqueId(), playerInfo.getFactionName());
								p.sendMessage("§aLe joueur §d"+victim.getName()+"§a a été rétrograder.");
								if(victim.isOnline())
								{
									Objects.requireNonNull(victim.getPlayer()).sendMessage("§aVous venez être rétrograder de votre faction.");
								}
							}
							else
							{
								p.sendMessage("§cUne erreur s'est produite, merci de contacter le staff");
							}
						}
						else
						{
							p.sendMessage("§CSeul le chef peut rétrograder une personne !");
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
					p.sendMessage("§cVous n'avez pas de faction !");
				}
				else
				{
					OfflinePlayer victim = Bukkit.getOfflinePlayer(args[1]);
					if (!(victim.hasPlayedBefore()) || p.getName().equalsIgnoreCase(victim.getName()))
					{
						p.sendMessage("§cPersonne non trouvée.");
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
									factionFunctions.promotePlayer(victim.getUniqueId(), playerInfo.getFactionName());
									p.sendMessage("§aLe joueur §d"+victim.getName()+"§a a été promu.");
									if(victim.isOnline())
									{
										Objects.requireNonNull(victim.getPlayer()).sendMessage("§aVous venez d'être promu de votre faction.");
									}
								}
								else if(victimInfo.getRole() == 1)
								{
									if(args.length >= 3 && args[2].equalsIgnoreCase("yes"))
									{
										factionFunctions.promotePlayer(victim.getUniqueId(), playerInfo.getFactionName());
										p.sendMessage("§aLe joueur §d"+victim.getName()+"§a est devenu chef.");
										if(victim.isOnline())
										{
											Objects.requireNonNull(victim.getPlayer()).sendMessage("§aVous venez de devenir chef de votre faction.");
										}

										factionFunctions.unrankPlayer(p.getUniqueId(), playerInfo.getFactionName());
										p.sendMessage("§cVous venez d'être rétrograder.");
									}
									else
									{
										p.sendMessage("§cCette personne est un modérateur de la faction");
										p.sendMessage("§cSi vous voulez lui transférer le pouvoir, faites §4/faction promote "+victim.getName()+" yes§c.");
									}
								}
								else
								{
									p.sendMessage("§cUne erreur s'est produite, merci de contacter le staff");
								}
							}
							else
							{
								p.sendMessage("§CSeul le chef peut rétrograder une personne !");
							}
						}
					}
				}
			}
			else
			{
				p.sendMessage("§cUtilisation : /faction promote <joueur>");
			}
		}
		else if(args[0].equalsIgnoreCase("deposit") && p.hasPermission("fireland.faction.deposit"))
		{
			if (factionInfo == null) {

				p.sendMessage("§cVous n'avez pas de faction !");
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
						p.sendMessage("§cLa faction ne peut pas contenir autant d'argent !");
					}
				} else
				{
					p.sendMessage("§cVous n'avez pas assez d'argent !");
				}
			} else
			{
				p.sendMessage("§cUtilisation : /faction deposit <nombre>");
			}
		}
		else if(args[0].equalsIgnoreCase("withdraw") && p.hasPermission("fireland.faction.withdraw")) {
			if (factionInfo == null) {
				p.sendMessage("§cVous n'avez pas de faction !");
				return true;
			}
			if (args.length == 2) {
				if (playerInfo.getRole() == 2 )
				{
					int money = Integer.parseInt(args[1]);
					if (factionInfo.getCurrentMoney()-money >=0 && money >= 0)
					{
						if (factionFunctions.take(factionInfo.getName(), money))
						{
							main.eco.withdrawPlayer(p, money);
						}
					}
					else
					{
						p.sendMessage("§cLa faction n'a pas assez d'argent !");
					}
				}
				else
				{
					p.sendMessage("§cSeul le leader peut effectuer cette action !");
				}
			} else {
				p.sendMessage("§cUtilisation : /faction withdraw <nombre>");
			}
		}
		else if(args[0].equalsIgnoreCase("upgrade")) {
			if (factionInfo == null) {
				p.sendMessage("§cVous n'avez pas de faction !");
				return true;
			}
			if (args.length == 1) {
				if (playerInfo.getRole() == 2 )
				{

					if (factionInfo.getCurrentMoney() == factionInfo.getMaxMoney())
					{
						if (factionFunctions.deposit(factionInfo.getName(), factionInfo.getMaxMoney()))
						{
							factionFunctions.upgradeFaction(factionInfo.getName());
						}
					}
					else
					{
						p.sendMessage("§cLa faction n'a pas assez d'argent !");
					}
				}
				else
				{
					p.sendMessage("§cSeul le leader peut effectuer cette action !");
				}
			} else {
				p.sendMessage("§cUtilisation : /faction upgrade");
			}
		}
		else
		{
			p.sendMessage("§cMauvaise formulation de la commande !");
		}
		return false;
	}
}
