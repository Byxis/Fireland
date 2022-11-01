package fr.byxis.event;

import fr.byxis.main.Main;
import fr.byxis.main.karmaManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class shop implements Listener, CommandExecutor {
	
	Inventory revolver1 = Bukkit.createInventory(null, 54, "Marchand de Revolver (1/2)");
	Inventory revolver2 = Bukkit.createInventory(null, 54, "Marchand de Revolver (2/2)");
	Inventory smg1 = Bukkit.createInventory(null, 54, "Marchand de SMG (1/2)");
	Inventory smg2 = Bukkit.createInventory(null, 54, "Marchand de SMG (2/2)");
	Inventory fusil1 = Bukkit.createInventory(null, 54, "Marchand de Fusil (1/2)");
	Inventory fusil2 = Bukkit.createInventory(null, 54, "Marchand de Fusil (2/2)");
	Inventory assaut1 = Bukkit.createInventory(null, 54, "Marchand de Fusil d'Assaut (1/3)");
	Inventory assaut2 = Bukkit.createInventory(null, 54, "Marchand de Fusil d'Assaut (2/3)");
	Inventory assaut3 = Bukkit.createInventory(null, 54, "Marchand de Fusil d'Assaut (3/3)");
	Inventory lourd1 = Bukkit.createInventory(null, 54, "Marchand d'armes lourdes (1/2)");
	Inventory lourd2 = Bukkit.createInventory(null, 54, "Marchand d'armes lourdes (2/2)");
	Inventory utilitaire = Bukkit.createInventory(null, 54, "Marchand Utilitaire");
	Inventory passv = Bukkit.createInventory(null, 45, "Marchand de Pass Vert");
	Inventory passb = Bukkit.createInventory(null, 45, "Marchand de Pass Bleu");
	Inventory passj = Bukkit.createInventory(null, 45, "Marchand de Pass Jaune");
	Inventory passr = Bukkit.createInventory(null, 45, "Marchand de Pass Rouge");
	
	private Main main;
	
	public shop(Main main) {
		this.main = main;
	}

	private void sellItemKarma(UUID _uuid)
	{
		karmaManager karma = new karmaManager(main);
		karma.goodAction(_uuid, 0.1);
	}

	private double getKarma(UUID _uuid)
	{
		karmaManager karma = new karmaManager(main);
		return karma.getKarma(_uuid);
	}

	private double priceKarmaAdapter(UUID _uuid, double amount)
	{
		double karma = getKarma(_uuid);
		if(karma >= 75)
		{
			double reduction = karma -75;
			return amount + getReduction(_uuid);
		}
		if(karma >= 25 && karma < 50)
		{
			double reduction = karma -50;
			return amount + getReduction(_uuid);
		}
		return amount;
	}

	private double getReduction(UUID _uuid)
	{
		double karma = getKarma(_uuid);

		if(karma >= 75)
		{
			double reduction = karma -75;
			return Math.round(-(reduction)*1/100);
		}
		if(karma >= 25 && karma < 50)
		{
			double reduction = karma -50;
			return Math.round(-(reduction)*2/100);
		}
		return 0;
	}

	private String reductionShow(UUID _uuid)
	{
		double reduction = getReduction(_uuid);
		if(reduction != 0)
		{
			return "  §d(§n"+(reduction*100)+"%§r§d)";
		}
		return "";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if(sender instanceof Player player) {
			if(cmd.getName().equalsIgnoreCase("shop")) {
				if(args.length == 1) {
					if(getKarma(player.getUniqueId()) <= 25)
					{
						player.sendMessage("§cJe ne vends pas ma marchandise à des criminels comme vous !");
					}
					else if(args[0].equalsIgnoreCase("revolver")) {
						setItems(revolver1, "Marchand de Revolver (1/2)", player.getUniqueId());
						player.openInventory(revolver1);
					}else if(args[0].equalsIgnoreCase("smg")) {
						setItems(smg1, "Marchand de SMG (1/2)", player.getUniqueId());
						player.openInventory(smg1);
					}else if(args[0].equalsIgnoreCase("fusil")) {
						setItems(fusil1, "Marchand de Fusil (1/2)", player.getUniqueId());
						player.openInventory(fusil1);
					}else if(args[0].equalsIgnoreCase("assaut")) {
						setItems(assaut1, "Marchand de Fusil d'Assaut (1/3)", player.getUniqueId());
						player.openInventory(assaut1);
					}else if(args[0].equalsIgnoreCase("lourd")) {
						setItems(lourd1, "Marchand d'armes lourdes (1/2)", player.getUniqueId());
						player.openInventory(lourd1);
					}else if(args[0].equalsIgnoreCase("utilitaire")) {
						setItems(utilitaire, "Marchand Utilitaire", player.getUniqueId());
						player.openInventory(utilitaire);
					}
					else if(main.cfgm.getKarmaDB().getDouble(player.getUniqueId().toString()) <= 75 && args[0].contains("pass"))
					{
						player.sendMessage("§cJe ne vous fais pas encore assez confiance pour vous vendre des pass !");
					}
					else if(args[0].contains("pass"))
					{
						if(args[0].equalsIgnoreCase("passv")) {
							setItemsFromSmallInv(passv, "Marchand de Pass Vert", player.getUniqueId());
							player.openInventory(passv);
						}else if(args[0].equalsIgnoreCase("passb")) {
							setItemsFromSmallInv(passb, "Marchand de Pass Bleu", player.getUniqueId());
							player.openInventory(passb);
						}else if(args[0].equalsIgnoreCase("passj")) {
							setItemsFromSmallInv(passj, "Marchand de Pass Jaune", player.getUniqueId());
							player.openInventory(passj);
						}else if(args[0].equalsIgnoreCase("passr")) {
							setItemsFromSmallInv(passr, "Marchand de Pass Rouge", player.getUniqueId());
							player.openInventory(passr);
						}
					}
					
				}else {
					player.sendMessage("§cUsage : /shop <revolver/smg/fusil/assaut/lourd/utilitaire/passv/passb/passj/passr>");
				}
				return true;
			}
		}
		return false;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();
		if(current == null) return;
		if(e.getView().getTitle().equalsIgnoreCase("Marchand de Revolver (1/2)")) {
			BuyItem(player, current, e.getClick());
			e.setCancelled(true);
			if(current.getType().equals(Material.RED_STAINED_GLASS_PANE) && current.getItemMeta().getDisplayName().contains("[2/2]")) {
				setItems(revolver2, "Marchand de Revolver (2/2)", player.getUniqueId());
				player.openInventory(revolver2);
				return;
			}
		}else if (e.getView().getTitle().equalsIgnoreCase("Marchand de Revolver (2/2)")){
			BuyItem(player, current, e.getClick());
			e.setCancelled(true);
			if(current.getType().equals(Material.LIME_STAINED_GLASS_PANE) && current.getItemMeta().getDisplayName().contains("[1/2]")) {
				setItems(revolver1, "Marchand de Revolver (1/2)", player.getUniqueId());
				player.openInventory(revolver1);
				return;
			}
		}else if (e.getView().getTitle().equalsIgnoreCase("Marchand de SMG (1/2)")){
			BuyItem(player, current, e.getClick());
			e.setCancelled(true);
			if(current.getType().equals(Material.RED_STAINED_GLASS_PANE) && current.getItemMeta().getDisplayName().contains("[2/2]")) {
				setItems(smg2, "Marchand de SMG (2/2)", player.getUniqueId());
				player.openInventory(smg2);
				return;
			}
		}else if (e.getView().getTitle().equalsIgnoreCase("Marchand de SMG (2/2)")){
			BuyItem(player, current, e.getClick());
			e.setCancelled(true);
			if(current.getType().equals(Material.LIME_STAINED_GLASS_PANE) && current.getItemMeta().getDisplayName().contains("[1/2]")) {
				setItems(smg1, "Marchand de SMG (1/2)", player.getUniqueId());
				player.openInventory(smg1);
				return;
			}
		}else if (e.getView().getTitle().equalsIgnoreCase("Marchand de Fusil (1/2)")){
			BuyItem(player, current, e.getClick());
			e.setCancelled(true);
			if(current.getType().equals(Material.RED_STAINED_GLASS_PANE) && current.getItemMeta().getDisplayName().contains("[2/2]")) {
				setItems(fusil2, "Marchand de Fusil (2/2)", player.getUniqueId());
				player.openInventory(fusil2);
				return;
			}
		}else if (e.getView().getTitle().equalsIgnoreCase("Marchand de Fusil (2/2)")){
			BuyItem(player, current, e.getClick());
			e.setCancelled(true);
			if(current.getType().equals(Material.LIME_STAINED_GLASS_PANE) && current.getItemMeta().getDisplayName().contains("[1/2]")) {
				setItems(fusil1, "Marchand de Fusil (1/2)", player.getUniqueId());
				player.openInventory(fusil1);
				return;
			}
		}else if (e.getView().getTitle().equalsIgnoreCase("Marchand de Fusil d'Assaut (1/3)")){
			BuyItem(player, current, e.getClick());
			e.setCancelled(true);
			if(current.getType().equals(Material.RED_STAINED_GLASS_PANE) && current.getItemMeta().getDisplayName().contains("[2/3]")) {
				setItems(assaut2, "Marchand de Fusil d'Assaut (2/3)", player.getUniqueId());
				player.openInventory(assaut2);
				return;
			}
		}else if (e.getView().getTitle().equalsIgnoreCase("Marchand de Fusil d'Assaut (2/3)")){
			BuyItem(player, current, e.getClick());
			e.setCancelled(true);
			if(current.getType().equals(Material.LIME_STAINED_GLASS_PANE) && current.getItemMeta().getDisplayName().contains("[1/3]")) {
				setItems(assaut1, "Marchand de Fusil d'Assaut (1/3)", player.getUniqueId());
				player.openInventory(assaut1);
				return;
			}
			if(current.getType().equals(Material.RED_STAINED_GLASS_PANE) && current.getItemMeta().getDisplayName().contains("[3/3]")) {
				setItems(assaut3, "Marchand de Fusil d'Assaut (3/3)", player.getUniqueId());
				player.openInventory(assaut3);
				return;
			}
		}else if (e.getView().getTitle().equalsIgnoreCase("Marchand de Fusil d'Assaut (3/3)")){
			BuyItem(player, current, e.getClick());
			e.setCancelled(true);
			if(current.getType().equals(Material.LIME_STAINED_GLASS_PANE) && current.getItemMeta().getDisplayName().contains("[2/3]")) {
				setItems(assaut2, "Marchand de Fusil d'Assaut (2/3)", player.getUniqueId());
				player.openInventory(assaut2);
				return;
			}
		}else if (e.getView().getTitle().equalsIgnoreCase("Marchand d'armes lourdes (1/2)")){
			BuyItem(player, current, e.getClick());
			e.setCancelled(true);
			if(current.getType().equals(Material.RED_STAINED_GLASS_PANE) && current.getItemMeta().getDisplayName().contains("[2/2]")) {
				setItems(lourd2, "Marchand d'armes lourdes (2/2)", player.getUniqueId());
				player.openInventory(lourd2);
				return;
			}
		}else if (e.getView().getTitle().equalsIgnoreCase("Marchand d'armes lourdes (2/2)")){
			BuyItem(player, current, e.getClick());
			e.setCancelled(true);
			if(current.getType().equals(Material.LIME_STAINED_GLASS_PANE) && current.getItemMeta().getDisplayName().contains("[1/2]")) {
				setItems(lourd1, "Marchand d'armes lourdes (1/2)", player.getUniqueId());
				player.openInventory(lourd1);
				return;
			}
		}else if(e.getView().getTitle().equalsIgnoreCase("Marchand Utilitaire")) {
			BuyItem(player, current, e.getClick());
			e.setCancelled(true);
		}else if(e.getView().getTitle().equalsIgnoreCase("Marchand de Pass Vert")) {
			BuyItem(player, current, e.getClick());
			e.setCancelled(true);
		}else if(e.getView().getTitle().equalsIgnoreCase("Marchand de Pass Bleu")) {
			BuyItem(player, current, e.getClick());
			e.setCancelled(true);
		}else if(e.getView().getTitle().equalsIgnoreCase("Marchand de Pass Jaune")) {
			BuyItem(player, current, e.getClick());
			e.setCancelled(true);
		}else if(e.getView().getTitle().equalsIgnoreCase("Marchand de Pass Rouge")) {
			BuyItem(player, current, e.getClick());
			e.setCancelled(true);
		}
	}
	
	private void BuyItem(Player p, ItemStack item, ClickType type) 
	{
		double money = 0;
		double price = 0;
		double sell = 0;
		String name = "";
		String command = "";
		boolean food = false;
		
		if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Pomme")) {food = true;price = 20;command = "minecraft:give "+p.getName()+" apple";name = "une pomme";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Veste de commando")) {price = 1000;command = "minecraft:give "+p.getName()+" diamond_chestplate";name = "une veste de commando";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Gilet pare-balle de commando")) {price = 1000;command = "minecraft:give "+p.getName()+" diamond_leggings";name = "un gilet pare-balle de commando";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Casque de commando")) {price = 800;command = "minecraft:give "+p.getName()+" diamond_helmet";name = "un casque de commando";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Bottes de commando")) {price = 800;command = "minecraft:give "+p.getName()+" diamond_boots";name = "des bottes de commando";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Veste de CRS")) {price = 700;command = "minecraft:give "+p.getName()+" iron_chestplate";name = "une veste de CRS";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Gilet pare-balle de CRS")) {price = 700;command = "minecraft:give "+p.getName()+" iron_leggings";name = "un gilet pare-balle CRS";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Casque de CRS")) {price = 500;command = "minecraft:give "+p.getName()+" iron_helmet";name = "un casque de CRS";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Bottes de CRS")) {price = 500;command = "minecraft:give "+p.getName()+" iron_boots";name = "des bottes de CRS";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Grenade ")) {price = 350;command = "shot give "+p.getName()+" grenade";name = "une grenade";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Parachute")) {price = 2000;command = "shot give "+p.getName()+" para";name = "un parachute";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Hache")) {price = 700;command = "shot give "+p.getName()+" hache";name = "une hache";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Machette")) {price = 900;command = "shot give "+p.getName()+" machet";name = "une machette";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7TAC Crossbow")) {price = 2500;command = "shot give "+p.getName()+" tac";name = "un TAC Crossbow";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Carreau")) {price = 20;command = "shot give "+p.getName()+" carrea";name = "un carreau";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Cookie")) {food = true;price = 10;command = "minecraft:give "+p.getName()+" cookie";name = "un cookie";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Tarte")) {food = true;price = 25;command = "minecraft:give "+p.getName()+" pumpkin_pie";name = "une tarte";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Pain")) {food = true;price = 20;command = "minecraft:give "+p.getName()+" bread";name = "du pain";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Carotte")) {food = true;price = 17;command = "minecraft:give "+p.getName()+" carrot";name = "une carotte";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Seringue Antidote")) {price = 150;command = "shot give "+p.getName()+" serin";name = "une seringue antidote";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Kit de soin")) {food = true;price = 150;command = "shot give "+p.getName()+" kit";name = "un kit de soin";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Lunettes de vision nocturne")) {price = 2000;command = "shot give "+p.getName()+" nvgo";name = "des lunettes de vision nocturne";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Bouteille d'eau")) {food = true;price = 80;command = "minecraft:give "+p.getName()+" potion{Potion:\"minecraft:water\"}";name = "une bouteille d'eau";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Walther PPK")) {price = 200;command = "shot give "+p.getName()+" walth";name = "un WaltherPPK";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Colt 1911")) {price = 230;command = "shot give "+p.getName()+" colt";name = "un colt 1911";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Makarov")) {price = 200;command = "shot give "+p.getName()+" maka";name = "un Makarov";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Beretta M9")) {price = 350;command = "shot give "+p.getName()+" berettam9";name = "un BerettaM9";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Glock 17")) {price = 370;command = "shot give "+p.getName()+" glock";name = "un Glock 17";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Hk USP")) {price = 430;command = "shot give "+p.getName()+" hkusp";name = "un Hk USP";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Five-seveN")) {price = 500;command = "shot give "+p.getName()+" fnfive";name = "un Five SeveN";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7r8")) {price = 300;command = "shot give "+p.getName()+" r8";name = "un r8";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Magnum 44")) {price = 350;command = "shot give "+p.getName()+" Magn";name = "un Magnum 44";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Taurus Raging Bull")) {price = 550;command = "shot give "+p.getName()+" taurus";name = "un Taurus Raging Bull";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7IMI Desert-Eagle")) {price = 1000;command = "shot give "+p.getName()+" imide";name = "un IMI Desert Eagle";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Beretta 92r")) {price = 700;command = "shot give "+p.getName()+" beret";name = "un Beretta 92r";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7CZ75a")) {price = 600;command = "shot give "+p.getName()+" CZ75";name = "un CZ75a";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de Revolver")) {price = 20;command = "shot give "+p.getName()+" chargeur_rev";name = "un Chargeur de Revolver";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de Haute Capacité")) {price = 25;command = "shot give "+p.getName()+" chargeur_revolver_haute";name = "un Chargeur de Haute Capacité";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de Desert Eagle")) {price = 35;command = "shot give "+p.getName()+" chargeur_des";name = "un Chargeur de Desert Eagle";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Cartouche de .347 Magnum")) {price = 4;command = "shot give "+p.getName()+" cartouche_347";name = "une Cartouche de .347 Magnum";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Uzi")) {price = 750;command = "shot give "+p.getName()+" uzi";name = "un Uzi";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7PP 2000")) {price = 780;command = "shot give "+p.getName()+" pp";name = "un PP 2000";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Kriss Vector")) {price = 780;command = "shot give "+p.getName()+" kriss";name = "un Kriss Vector";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7HK MP5")) {price = 800;command = "shot give "+p.getName()+" hkmp5";name = "un HK MP5";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7MAC 10")) {price = 750;command = "shot give "+p.getName()+" mac10";name = "un MAC 10";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7TEC-9")) {price = 880;command = "shot give "+p.getName()+" tec";name = "un TEC-9";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7UMP45")) {price = 850;command = "shot give "+p.getName()+" ump";name = "un UMP45";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7HK MP7")) {price = 850;command = "shot give "+p.getName()+" hkmp7";name = "un HK MP7";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7MP40")) {price = 900;command = "shot give "+p.getName()+" MP40";name = "un MP40";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7FN P90")) {price = 1300;command = "shot give "+p.getName()+" FNP90";name = "un FN P90";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7MP9")) {price = 900;command = "shot give "+p.getName()+" MP9";name = "un MP9";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Thompson")) {price = 1500;command = "shot give "+p.getName()+" tho";name = "une Thompson";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de UZI")) {price = 40;command = "shot give "+p.getName()+" chargeur_uzi";name = "un Chargeur de Uzi";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de HKMP7")) {price = 40;command = "shot give "+p.getName()+" chargeur_hkmp7";name = "un Chargeur de HK MP7";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de Kriss Vector")) {price = 45;command = "shot give "+p.getName()+" chargeur_kriss";name = "un Chargeur de Kriss Vector";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de MP40")) {price = 40;command = "shot give "+p.getName()+" chargeur_mp40";name = "un Chargeur de MP40";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de UMP45")) {price = 40;command = "shot give "+p.getName()+" chargeur_ump";name = "un Chargeur de UMP45";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de MP9")) {price = 40;command = "shot give "+p.getName()+" chargeur_mp9";name = "un Chargeur de MP9";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de HKMP5")) {price = 40;command = "shot give "+p.getName()+" chargeur_hkmp5";name = "un Chargeur de HK MP5";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de PP2000")) {price = 40;command = "shot give "+p.getName()+" chargeur_pp2";name = "un Chargeur de PP2000";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de TEC9")) {price = 40;command = "shot give "+p.getName()+" chargeur_tec";name = "un Chargeur de TEC9";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de MAC10")) {price = 40;command = "shot give "+p.getName()+" chargeur_mac";name = "un Chargeur de MAC10";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de FNP90")) {price = 45;command = "shot give "+p.getName()+" chargeur_fnp";name = "un Chargeur de FNP90";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de Thompson")) {price = 50;command = "shot give "+p.getName()+" chargeur_thompson";name = "un Chargeur de Thompson";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Barrett M107")) {price = 3000;command = "shot give "+p.getName()+" barret";name = "un Barett M107";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7L96A1")) {price = 2300;command = "shot give "+p.getName()+" l96";name = "un L96A1";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7SVD Dragunov")) {price = 2600;command = "shot give "+p.getName()+" svdd";name = "un SVD Dragunov";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7VSS Vintorez")) {price = 3500;command = "shot give "+p.getName()+" vssvin";name = "un VSS Vintorez";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7M40A1")) {price = 2000;command = "shot give "+p.getName()+" m40a";name = "un M40A1";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Mosin Nagant")) {price = 2100;command = "shot give "+p.getName()+" mosin";name = "un Mosin Nagant";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Karabiner 98k")) {price = 2150;command = "shot give "+p.getName()+" karabiner";name = "un Karabiner 98k";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7SVT 40")) {price = 2100;command = "shot give "+p.getName()+" SVT";name = "un SVT40";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7CheyTacM200")) {price = 2700;command = "shot give "+p.getName()+" cheyt";name = "un Cheytac M200";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7SPAS 12")) {price = 2600;command = "shot give "+p.getName()+" spas";name = "un SPAS 12";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7XM 1014")) {price = 2300;command = "shot give "+p.getName()+" xm";name = "un XM 1014";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Benelli Nova")) {price = 2200;command = "shot give "+p.getName()+" benell";name = "un Benelli Nova";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Remington 870")) {price = 2100;command = "shot give "+p.getName()+" remin";name = "un Remington 870";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7AA12")) {price = 3000;command = "shot give "+p.getName()+" aa";name = "un AA12";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Cartouche de Calibre 12")) {price = 10;command = "shot give "+p.getName()+" cartouche_cal";name = "une Cartouche de Calibre 12";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de Barrett")) {price = 50;command = "shot give "+p.getName()+" chargeur_barr";name = "un Chargeur de Barrett";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de L96")) {price = 35;command = "shot give "+p.getName()+" chargeur_l96";name = "un Chargeur de L96";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de SVD Dragunov")) {price = 35;command = "shot give "+p.getName()+" chargeur_svd";name = "un Chargeur de SVD Dragunov";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Cartouche de 7.62 NATO")) {price = 10;command = "shot give "+p.getName()+" cartouche_762";name = "une Cartouche de 7.62 NATO";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de ASVAL")) {price = 50;command = "shot give "+p.getName()+" chargeur_ASVAL";name = "un Chargeur de ASVAL";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de SVT-40")) {price = 30;command = "shot give "+p.getName()+" chargeur_svt";name = "un Chargeur de SVT-40";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de AA12")) {price = 50;command = "shot give "+p.getName()+" chargeur_aa";name = "un Chargeur de AA12";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de CheytacM200")) {price = 30;command = "shot give "+p.getName()+" chargeur_chey";name = "un Chargeur de CheytacM200";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7RPK")) {price = 1400;command = "shot give "+p.getName()+" rpk";name = "un RPK";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7AK12")) {price = 1450;command = "shot give "+p.getName()+" AK12";name = "un AK12";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7AK74")) {price = 1400;command = "shot give "+p.getName()+" ak74";name = "un AK74";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7FN Scar-H")) {price = 2100;command = "shot give "+p.getName()+" fnscar-h";name = "une FN Scar-H";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7ACR")) {price = 1650;command = "shot give "+p.getName()+" acr";name = "une ACR";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7M16 A4")) {price = 1600;command = "shot give "+p.getName()+" m16a4";name = "une M16 A4";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7M14")) {price = 1500;command = "shot give "+p.getName()+" M14";name = "une M14";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7IWI ACE53")) {price = 1900;command = "shot give "+p.getName()+" iwiace";name = "une IWI ACE53";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Steyr AUG")) {price = 1900;command = "shot give "+p.getName()+" steyraug";name = "une Steyr AUG";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7HK 416")) {price = 1650;command = "shot give "+p.getName()+" HK416";name = "une HK 416";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7TAR 21")) {price = 1600;command = "shot give "+p.getName()+" tar";name = "une TAR 21";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Famas")) {price = 1850;command = "shot give "+p.getName()+" famas";name = "un Famas";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7FN FAL")) {price = 1800;command = "shot give "+p.getName()+" FNfal";name = "un FN FAL";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7AS VAL")) {price = 3200;command = "shot give "+p.getName()+" asval";name = "un AS VAL";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de M14")) {price = 40;command = "shot give "+p.getName()+" chargeur_m14";name = "un Chargeur de M14";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de ASVAL")) {price = 50;command = "shot give "+p.getName()+" chargeur_asval";name = "un Chargeur de ASVAL";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de SteyrAUG")) {price = 45;command = "shot give "+p.getName()+" chargeur_steyrau";name = "un Chargeur de SteyrAUG";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de HK416")) {price = 40;command = "shot give "+p.getName()+" chargeur_HK416";name = "un Chargeur de HK416";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de M16")) {price = 40;command = "shot give "+p.getName()+" chargeur_m16";name = "un Chargeur de M16";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de Famas")) {price = 45;command = "shot give "+p.getName()+" chargeur_famas";name = "un Chargeur de Famas";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de FNFAL")) {price = 45;command = "shot give "+p.getName()+" chargeur_fnfal";name = "un Chargeur de FNFAL";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de AK")) {price = 45;command = "shot give "+p.getName()+" chargeur_ak";name = "un Chargeur de AK";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de Scar")) {price = 45;command = "shot give "+p.getName()+" chargeur_scar";name = "un Chargeur de Scar";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de TAR21")) {price = 40;command = "shot give "+p.getName()+" chargeur_tar21";name = "un Chargeur de TAR21";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de IWI ACE53")) {price = 45;command = "shot give "+p.getName()+" chargeur_iwiace";name = "un Chargeur de IWI ACE53";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de M2 Browning")) {price = 55;command = "shot give "+p.getName()+" chargeur_m2_br";name = "un Chargeur de M2 Browning";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de MK48")) {price = 50;command = "shot give "+p.getName()+" chargeur_mk48";name = "un Chargeur de MK48";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de M60")) {price = 55;command = "shot give "+p.getName()+" chargeur_m60";name = "un Chargeur de M60";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de M249")) {price = 45;command = "shot give "+p.getName()+" chargeur_m249";name = "un Chargeur de M249";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de Minigun")) {price = 55;command = "shot give "+p.getName()+" chargeur_minigun";name = "un Chargeur de Minigun";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Grenade 40mm")) {price = 60;command = "shot give "+p.getName()+" grenade40";name = "une grenade 40mm";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Roquette")) {price = 65;command = "shot give "+p.getName()+" roquette";name = "une Roquette";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7M2 Browning")) {price = 3000;command = "shot give "+p.getName()+" M2_browning";name = "une M2 Browning";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7MK48")) {price = 2600;command = "shot give "+p.getName()+" MK48";name = "une MK48";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7M60")) {price = 3400;command = "shot give "+p.getName()+" M60";name = "une M60";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7M249")) {price = 2500;command = "shot give "+p.getName()+" M249";name = "une M249";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Minigun")) {price = 3200;command = "shot give "+p.getName()+" Minigun";name = "une Minigun";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7M32 MGL")) {price = 4000;command = "shot give "+p.getName()+" M32MGL";name = "une M32 MGL";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7M79")) {price = 3000;command = "shot give "+p.getName()+" M79";name = "une M79";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7RPG-7")) {price = 3500;command = "shot give "+p.getName()+" RPG";name = "un RPG-7";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Lampe de Poche")) {price = 80;command = "shot give "+p.getName()+" lampe";name = "une lampe de poche";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7G36C")) {price = 1700;command = "shot give "+p.getName()+" g36c";name = "un G36C";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7M4A1")) {price = 1600;command = "shot give "+p.getName()+" m4A1";name = "une M4A4";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de G36C")) {price = 40;command = "shot give "+p.getName()+" chargeur_g36";name = "un chargeur de G36C";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7AKM")) {price = 1450;command = "shot give "+p.getName()+" akm";name = "une AKM";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7L86")) {price = 1850;command = "shot give "+p.getName()+" l86";name = "une L86";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de L86")) {price = 40;command = "shot give "+p.getName()+" chargeur_L86";name = "un chargeur de L86";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7r8")) {price = 300;command = "shot give "+p.getName()+" r8";name = "un r8";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Chargeur de KP31 Suomi")) {price = 40;command = "shot give "+p.getName()+" chargeur_kp31";name = "un chargeur de KP31 Suomi";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7KP31 Suomi")) {price = 1500;command = "shot give "+p.getName()+" kp31";name = "une KP31 Suomi";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7CZ52")) {price = 210;command = "shot give "+p.getName()+" cz52";name = "un CZ52";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§8Pass - §aVert")) {price = 1200;command = "minecraft:give "+p.getName()+" minecraft:music_disc_cat";name = "un pass vert";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§8Pass - §9Bleu")) {price = 3000;command = "minecraft:give "+p.getName()+" minecraft:music_disc_wait";name = "un pass bleu";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§8Pass - §eJaune")) {price = 7000;command = "minecraft:give "+p.getName()+" minecraft:music_disc_13";name = "un pass jaune";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§8Pass - §cRouge")) {price = 18000;command = "minecraft:give "+p.getName()+" minecraft:music_disc_blocks";name = "un pass rouge";}
		else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Cartouche de Calibre 12")) {price = 10;command = "shot give "+p.getName()+" cartouche_calibre12";name = "des cartouches de calibre 12";}
		
		if (price != 0) 
		{
			sell = price/4;
		}
		else 
		{
			return;
		}
		sell = sell+0;
		if(type.isLeftClick()) 
		{
			if(main.eco.hasAccount(p)) 
			{
				money = main.eco.getBalance(p);
			}

			if(!command.equals("")) 
			{
				long newprice = Math.round(priceKarmaAdapter(p.getUniqueId(), price));
				if(money >= newprice)
				{
					if(command.contains("minecraft:give ") && food == false) 
					{
						if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§8Pass - §aVert"))
						{
							command += "{display:{Name:\"{\\\"text\\\":\\\""+"§aPass Vert"+"\\\"}\"}} 1";
						}
						else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§8Pass - §9Bleu"))
						{
							command += "{display:{Name:\"{\\\"text\\\":\\\""+"§9Pass Bleu"+"\\\"}\"}} 1";
						}
						else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§8Pass - §eJaune"))
						{
							command += "{display:{Name:\"{\\\"text\\\":\\\""+"§ePass Jaune"+"\\\"}\"}} 1";
						}
						else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§8Pass - §cRouge"))
						{
							command += "{display:{Name:\"{\\\"text\\\":\\\""+"§cPass Rouge"+"\\\"}\"}} 1";
						}
						else
						{
							command += "{display:{Name:\"{\\\"text\\\":\\\""+item.getItemMeta().getDisplayName()+"\\\"}\"}} 1";
						}
					}


					p.playSound(p.getLocation(), "minecraft:gun.hud.money_drop", (float) 0.1, 1);
					p.sendMessage("§7Vous avez acheté "+name+" pour "+newprice+"$ !");
					sellItemKarma(p.getUniqueId());
					main.eco.withdrawPlayer(p, newprice);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
				}
				else 
				{
					p.sendMessage("§CVous n'avez pas assez d'argent !");
				}
			}
		}
		else if(type.isRightClick()) 
		{
			boolean hasSelled = false;
			if(type.isShiftClick())
			{
				int num = 0;
				for (ItemStack s : p.getInventory().getContents()) 
				{
					if(s != null)
					{
						if(s.getItemMeta().getDisplayName().toLowerCase().contains(item.getItemMeta().getDisplayName().toLowerCase())) 
						{
							hasSelled =  true;
							if(s.getAmount() > 1)
							{
								num += s.getAmount();
								s.setAmount(0);
							}
							else
							{
								s.setAmount(s.getAmount() -1);
								num +=1;
							}
							
						}
						else if(s.getType().equals(item.getType()) && food) 
						{
							hasSelled =  true;
							if(s.getAmount() > 1)
							{
								num += s.getAmount();
								s.setAmount(0);
							}
							else
							{
								s.setAmount(s.getAmount() -1);
								num +=1;
							}
						}
					}
				}
				if(!hasSelled) 
				{
					p.sendMessage("§CVous n'avez pas l'item...");
				}
				else
				{
					float totalSell = (float) (num*sell);
					main.eco.depositPlayer(p, num);
					p.sendMessage("§7Vous avez vendu "+name+"§ax"+num+"§7 pour "+totalSell+"$ !");
					p.playSound(p.getLocation(), "minecraft:gun.hud.money_pickup", (float) 0.1, 1);

					sellItemKarma(p.getUniqueId());

				}
			}
			else
			{
				for (ItemStack s : p.getInventory().getContents()) 
				{
					if(s != null)
					{
						if(s.getItemMeta().getDisplayName().toLowerCase().contains(item.getItemMeta().getDisplayName().toLowerCase())) 
						{
							hasSelled =  true;
							s.setAmount(s.getAmount() -1);
							main.eco.depositPlayer(p, sell);
							p.sendMessage("§7Vous avez vendu "+name+" pour "+sell+"$ !");
							p.playSound(p.getLocation(), "minecraft:gun.hud.money_pickup", (float) 0.1, 1);

							sellItemKarma(p.getUniqueId());

							break;
						}
						else if(s.getType().equals(item.getType()) && food) 
						{
							hasSelled =  true;
							s.setAmount(s.getAmount() -1);
							main.eco.depositPlayer(p, sell);
							p.sendMessage("§7Vous avez vendu "+name+" pour "+sell+"$ !");
							p.playSound(p.getLocation(), "minecraft:gun.hud.money_pickup", (float) 0.1, 1);

							sellItemKarma(p.getUniqueId());

							break;
						}
					}
				}
				if(!hasSelled) 
				{
					p.sendMessage("§CVous n'avez pas l'item...");
				}
			}
			
		}
	}
	
	private void setItemsFromSmallInv(Inventory inv, String name, UUID _uuid)
	{
		inv.setItem(0, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(1, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(2, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(3, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(4, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(5, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(6, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(7, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(8, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(36, setItemMetaLore(Material.BOOK, "§r- Informations -", (short) 1, listMaker3("§8Pour acheter un item, faites un","§6clic gauche§8 dessus, pour le","§8vendre, faites un §6clic droit§8.", _uuid)));
		inv.setItem(37, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(38, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(39, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(40, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(41, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(42, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		
		if(name.equalsIgnoreCase("Marchand de Pass Vert")) 
		{
			inv.setItem(22, setItemMetaLore(Material.MUSIC_DISC_CAT, "§8Pass - §aVert", (short) 0, listMaker3("§7Permet l'accès à certaines zones","§8Coût: §61200$","§8Vente: §6300$", _uuid)));
			inv.setItem(43, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
			inv.setItem(44, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		}
		else if(name.equalsIgnoreCase("Marchand de Pass Bleu")) 
		{
			inv.setItem(22, setItemMetaLore(Material.MUSIC_DISC_WAIT, "§8Pass - §9Bleu", (short) 0, listMaker3("§7Permet l'accès à certaines zones","§8Coût: §63000$","§8Vente: §6750$", _uuid)));
			inv.setItem(43, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
			inv.setItem(44, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		}
		else if(name.equalsIgnoreCase("Marchand de Pass Jaune")) 
		{
			inv.setItem(22, setItemMetaLore(Material.MUSIC_DISC_13, "§8Pass - §eJaune", (short) 0, listMaker3("§7Permet l'accès à certaines zones","§8Coût: §67000$","§8Vente: §61750$", _uuid)));
			inv.setItem(43, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
			inv.setItem(44, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		}
		else if(name.equalsIgnoreCase("Marchand de Pass Rouge")) 
		{
			inv.setItem(22, setItemMetaLore(Material.MUSIC_DISC_BLOCKS, "§8Pass - §cRouge", (short) 0, listMaker3("§7Permet l'accès à certaines zones","§8Coût: §618000$","§8Vente: §64500$", _uuid)));
			inv.setItem(43, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
			inv.setItem(44, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		}
			
	}
	
	private void setItems(Inventory inv, String name, UUID _uuid) {
		inv.setItem(0, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(1, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(2, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(3, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(4, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(5, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(6, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(7, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(8, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(45, setItemMetaLore(Material.BOOK, "§r- Informations -", (short) 1, listMaker3("§8Pour acheter un item, faites un","§6clic gauche§8 dessus, pour le","§8vendre, faites un §6clic droit§8.", _uuid)));
		inv.setItem(46, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(47, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(48, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(49, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(50, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		inv.setItem(51, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		
		if(name.equalsIgnoreCase("Marchand de Revolver (1/2)")) {

			inv.setItem(19, setItemMetaLore(Material.STONE_AXE, "§7Walther PPK", (short) 18, listMaker3("§8Munitions: §7Revolver","§8Coût: §6200$","§8Vente: §650$", _uuid)));
			inv.setItem(20, setItemMetaLore(Material.IRON_SHOVEL, "§7Makarov", (short) 4, listMaker3("§8Munitions: §7Revolver","§8Coût: §6200$","§8Vente: §650$", _uuid)));
			inv.setItem(21, setItemMetaLore(Material.STONE_AXE, "§7Colt 1911", (short) 6, listMaker3("§8Munitions: §7Revolver","§8Coût: §6230$","§8Vente: §657.5$", _uuid)));
			inv.setItem(22, setItemMetaLore(Material.STONE_AXE, "§7Glock 17", (short) 0, listMaker3("§8Munitions: §7Haute Capacité","§8Coût: §6370$","§8Vente: §692.5$", _uuid)));
			inv.setItem(23, setItemMetaLore(Material.IRON_SHOVEL, "§7Beretta 92r", (short) 31, listMaker3("§8Munitions: §7Haute Capacité","§8Coût: §6700$","§8Vente: §6175$", _uuid)));
			inv.setItem(24, setItemMetaLore(Material.IRON_SHOVEL, "§7CZ75a", (short) 37, listMaker3("§8Munitions: §7Haute Capacité","§8Coût: §6600$","§8Vente: §6150$", _uuid)));
			inv.setItem(25, setItemMetaLore(Material.WOODEN_SHOVEL, "§7Five-seveN", (short) 0, listMaker3("§8Munitions: §7Haute Capacité","§8Coût: §6500$","§8Vente: §6125$", _uuid)));
			inv.setItem(28, setItemMetaLore(Material.STONE_AXE, "§7Beretta M9", (short) 49, listMaker3("§8Munitions: §7Haute Capacité","§8Coût: §6350$","§8Vente: §687.5", _uuid)));
			inv.setItem(29, setItemMetaLore(Material.STONE_AXE, "§7HK Usp", (short) 24, listMaker3("§8Munitions: §7Haute Capacité","§8Coût: §6430$","§8Vente: §6107.5$", _uuid)));
			inv.setItem(30, setItemMetaLore(Material.STONE_AXE, "§7Taurus Raging Bull", (short) 33, listMaker3("§8Munitions: §7.347 Magnum","§8Coût: §6550$","§8Vente: §6137.5$", _uuid)));
			inv.setItem(31, setItemMetaLore(Material.STONE_AXE, "§7r8", (short) 45, listMaker3("§8Munitions: §7.347 Magnum","§8Coût: §6300$","§8Vente: §675$", _uuid)));
			inv.setItem(32, setItemMetaLore(Material.IRON_SHOVEL, "§7Magnum 44", (short) 10, listMaker3("§8Munitions: §7.347 Magnum","§8Coût: §6350$","§8Vente: §687.5$", _uuid)));
			inv.setItem(33, setItemMetaLore(Material.STONE_AXE, "§7IMI Desert-Eagle", (short) 29, listMaker3("§8Munitions: §7Desert Eagle","§8Coût: §61000$","§8Vente: §6250$", _uuid)));
			inv.setItem(34, setItemMetaLore(Material.DIAMOND_AXE, "§7CZ52", (short) 9, listMaker3("§8Munitions: §7Revolver","§8Coût: §6210$","§8Vente: §652.5$", _uuid)));
			inv.setItem(52, setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§r[1/2]", (short) 1));
			inv.setItem(53, setItemMeta(Material.RED_STAINED_GLASS_PANE, "§r[2/2]", (short) 1));
			return;
		}else if(name.equalsIgnoreCase("Marchand de Revolver (2/2)")){
			inv.setItem(19, setItemMetaLore(Material.GRAY_DYE, "§7Chargeur de Desert Eagle", (short) 0, listMaker2("§8Coût: §635$","§8Vente: §68.75$", _uuid)));
			inv.setItem(20, setItemMetaLore(Material.MELON_SEEDS, "§7Chargeur de Haute Capacité", (short) 0, listMaker2("§8Coût: §625$","§8Vente: §66.25$", _uuid)));
			inv.setItem(21, setItemMetaLore(Material.PUMPKIN_SEEDS, "§7Chargeur de Revolver", (short) 0, listMaker2("§8Coût: §620$","§8Vente: §65$", _uuid)));
			inv.setItem(22, setItemMetaLore(Material.CYAN_DYE, "§7Cartouche de .347 Magnum", (short) 0, listMaker2("§8Coût: §64$","§8Vente: §61$", _uuid)));
			inv.setItem(52, setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§r[1/2]", (short) 1));
			inv.setItem(53, setItemMeta(Material.RED_STAINED_GLASS_PANE, "§r[2/2]", (short) 1));
			return;
		}else if(name.equalsIgnoreCase("Marchand de SMG (1/2)")) {
			inv.setItem(19, setItemMetaLore(Material.GOLDEN_PICKAXE, "§7Uzi", (short) 6, listMaker3("§8Munitions: §7Uzi","§8Coût: §6750$","§8Vente: §6187.5$", _uuid)));
			inv.setItem(20, setItemMetaLore(Material.STONE_AXE, "§7PP 2000", (short) 41, listMaker3("§8Munitions: §7PP 2000","§8Coût: §6780$","§8Vente: §6195$", _uuid)));
			inv.setItem(21, setItemMetaLore(Material.GOLDEN_SHOVEL, "§7Kriss Vector", (short) 0, listMaker3("§8Munitions: §7Kriss Vector","§8Coût: §6780$","§8Vente: §6195$", _uuid)));
			inv.setItem(22, setItemMetaLore(Material.GOLDEN_HOE, "§7HK MP5", (short) 0, listMaker3("§8Munitions: §7HK MP5","§8Coût: §6800$","§8Vente: §6200$", _uuid)));
			inv.setItem(23, setItemMetaLore(Material.STONE_AXE, "§7MAC 10", (short) 12, listMaker3("§8Munitions: §7MAC10","§8Coût: §6750$","§8Vente: §6187.5$", _uuid)));
			inv.setItem(24, setItemMetaLore(Material.IRON_AXE, "§7TEC-9", (short) 4, listMaker3("§8Munitions: §7TEC-9","§8Coût: §6880$","§8Vente: §6220$", _uuid)));
			inv.setItem(25, setItemMetaLore(Material.STONE_AXE, "§7UMP45", (short) 37, listMaker3("§8Munitions: §7UMP45","§8Coût: §6850$","§8Vente: §6212.5$", _uuid)));
			inv.setItem(28, setItemMetaLore(Material.IRON_HOE, "§7HK MP7", (short) 100, listMaker3("§8Munitions: §7HK MP7","§8Coût: §6850$","§8Vente: §6212.5$", _uuid)));
			inv.setItem(29, setItemMetaLore(Material.DIAMOND_AXE, "§7MP40", (short) 21, listMaker3("§8Munitions: §7MP40","§8Coût: §6900$","§8Vente: §6225$", _uuid)));
			inv.setItem(30, setItemMetaLore(Material.IRON_HOE, "§7FN P90", (short) 0, listMaker3("§8Munitions: §7FN P90","§8Coût: §61300$","§8Vente: §6325$", _uuid)));
			inv.setItem(31, setItemMetaLore(Material.IRON_HOE, "§7MP9", (short) 170, listMaker3("§8Munitions: §7MP9","§8Coût: §6900$","§8Vente: §6225$", _uuid)));
			inv.setItem(32, setItemMetaLore(Material.GOLDEN_SHOVEL, "§7Thompson", (short) 4, listMaker3("§8Munitions: §7Thompson","§8Coût: §61500$","§8Vente: §6375$", _uuid)));
			inv.setItem(33, setItemMetaLore(Material.DIAMOND_AXE, "§7KP31 Suomi", (short) 18, listMaker3("§8Munitions: §7KP31 Suomi","§8Coût: §61500$","§8Vente: §6375$", _uuid)));
			inv.setItem(52, setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§r[1/2]", (short) 1));
			inv.setItem(53, setItemMeta(Material.RED_STAINED_GLASS_PANE, "§r[2/2]", (short) 1));
		}else if(name.equalsIgnoreCase("Marchand de SMG (2/2)")) {
			inv.setItem(19, setItemMetaLore(Material.ORANGE_DYE, "§7Chargeur de UZI", (short) 0, listMaker2("§8Coût: §640$","§8Vente: §610$", _uuid)));
			inv.setItem(20, setItemMetaLore(Material.ORANGE_DYE, "§7Chargeur de HKMP7", (short) 0, listMaker2("§8Coût: §640$","§8Vente: §610$", _uuid)));
			inv.setItem(21, setItemMetaLore(Material.ORANGE_DYE, "§7Chargeur de Kriss Vector", (short) 0, listMaker2("§8Coût: §645$","§8Vente: §611.25$", _uuid)));
			inv.setItem(22, setItemMetaLore(Material.ORANGE_DYE, "§7Chargeur de MP40", (short) 0, listMaker2("§8Coût: §640$","§8Vente: §610$", _uuid)));
			inv.setItem(23, setItemMetaLore(Material.ORANGE_DYE, "§7Chargeur de UMP45", (short) 0, listMaker2("§8Coût: §640$","§8Vente: §610$", _uuid)));
			inv.setItem(24, setItemMetaLore(Material.ORANGE_DYE, "§7Chargeur de MP9", (short) 0, listMaker2("§8Coût: §640$","§8Vente: §610$", _uuid)));
			inv.setItem(25, setItemMetaLore(Material.MAGENTA_DYE, "§7Chargeur de HKMP5", (short) 0, listMaker2("§8Coût: §640$","§8Vente: §610$", _uuid)));
			inv.setItem(28, setItemMetaLore(Material.PURPLE_DYE, "§7Chargeur de PP2000", (short) 0, listMaker2("§8Coût: §640$","§8Vente: §610$", _uuid)));
			inv.setItem(29, setItemMetaLore(Material.PURPLE_DYE, "§7Chargeur de TEC9", (short) 0, listMaker2("§8Coût: §640$","§8Vente: §610$", _uuid)));
			inv.setItem(30, setItemMetaLore(Material.PURPLE_DYE, "§7Chargeur de MAC10", (short) 0, listMaker2("§8Coût: §640$","§8Vente: §610$", _uuid)));
			inv.setItem(31, setItemMetaLore(Material.STICK, "§7Chargeur de FNP90", (short) 0, listMaker2("§8Coût: §645$","§8Vente: §611.25$", _uuid)));
			inv.setItem(32, setItemMetaLore(Material.YELLOW_DYE, "§7Chargeur de Thompson", (short) 0, listMaker2("§8Coût: §650$","§8Vente: §612.5$", _uuid)));
			inv.setItem(33, setItemMetaLore(Material.ORANGE_DYE, "§7Chargeur de KP31 Suomi", (short) 0, listMaker2("§8Coût: §640$","§8Vente: §610$", _uuid)));
			inv.setItem(52, setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§r[1/2]", (short) 1));
			inv.setItem(53, setItemMeta(Material.RED_STAINED_GLASS_PANE, "§r[2/2]", (short) 1));
		}else if(name.equalsIgnoreCase("Marchand de Fusil (1/2)")) {
			inv.setItem(19, setItemMetaLore(Material.DIAMOND_AXE, "§7L96A1", (short) 3, listMaker3("§8Munitions: §7L96A1","§8Coût: §62300$","§8Vente: §6575$", _uuid)));
			inv.setItem(20, setItemMetaLore(Material.DIAMOND_AXE, "§7SVD Dragunov", (short) 0, listMaker3("§8Munitions: §7SVD Dragunov","§8Coût: §62600$","§8Vente: §6650$", _uuid)));
			inv.setItem(21, setItemMetaLore(Material.DIAMOND_SHOVEL, "§7VSS Vintorez", (short) 3, listMaker3("§8Munitions: §7AS VAL","§8Coût: §63500$","§8Vente: §6875$", _uuid)));
			inv.setItem(22, setItemMetaLore(Material.WOODEN_HOE, "§7M40A1", (short) 5, listMaker3("§8Munitions: §77.62 NATO","§8Coût: §62000$","§8Vente: §6500$", _uuid)));
			inv.setItem(23, setItemMetaLore(Material.IRON_SHOVEL, "§7Mosin Nagant", (short) 7, listMaker3("§8Munitions: §77.62 NATO","§8Coût: §62100$","§8Vente: §6525$", _uuid)));
			inv.setItem(24, setItemMetaLore(Material.DIAMOND_AXE, "§7Karabiner 98k", (short) 15, listMaker3("§8Munitions: §77.62 NATO","§8Coût: §62150$","§8Vente: §6537.5$", _uuid)));
			inv.setItem(25, setItemMetaLore(Material.IRON_HOE, "§7SVT 40", (short) 120, listMaker3("§8Munitions: §7SVT 40","§8Coût: §62100$","§8Vente: §6525$", _uuid)));
			inv.setItem(28, setItemMetaLore(Material.DIAMOND_AXE, "§7CheyTacM200", (short) 12, listMaker3("§8Munitions: §7Cheytac M200","§8Coût: §62700$","§8Vente: §6375$", _uuid)));
			inv.setItem(29, setItemMetaLore(Material.WOODEN_HOE, "§7SPAS 12", (short) 8, listMaker3("§8Munitions: §7Calibre 12","§8Coût: §62600$","§8Vente: §6650$", _uuid)));
			inv.setItem(30, setItemMetaLore(Material.WOODEN_HOE, "§7XM 1014", (short) 2, listMaker3("§8Munitions: §7Calibre 12","§8Coût: §62300$","§8Vente: §6575$", _uuid)));
			inv.setItem(31, setItemMetaLore(Material.IRON_AXE, "§7Benelli Nova", (short) 34, listMaker3("§8Munitions: §7Calibre 12","§8Coût: §62200$","§8Vente: §6550$", _uuid)));
			inv.setItem(32, setItemMetaLore(Material.WOODEN_HOE, "§7Remington 870", (short) 14, listMaker3("§8Munitions: §7Calibre 12","§8Coût: §62100$","§8Vente: §6525$", _uuid)));
			inv.setItem(52, setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§r[1/2]", (short) 1));
			inv.setItem(53, setItemMeta(Material.RED_STAINED_GLASS_PANE, "§r[2/2]", (short) 1));
		}else if(name.equalsIgnoreCase("Marchand de Fusil (2/2)")) {
			inv.setItem(19, setItemMetaLore(Material.PINK_DYE, "§7Chargeur de L96", (short) 0, listMaker2("§8Coût: §635$","§8Vente: §68.75$", _uuid)));
			inv.setItem(20, setItemMetaLore(Material.PINK_DYE, "§7Chargeur de SVD Dragunov", (short) 0, listMaker2("§8Coût: §635$","§8Vente: §68.75$", _uuid)));
			inv.setItem(21, setItemMetaLore(Material.CYAN_DYE, "§7Cartouche de 7.62 NATO", (short) 0, listMaker2("§8Coût: §610$","§8Vente: §62.5$", _uuid)));
			inv.setItem(22, setItemMetaLore(Material.LAPIS_LAZULI, "§7Chargeur de ASVAL", (short) 0, listMaker2("§8Coût: §650$","§8Vente: §612.5$", _uuid)));
			inv.setItem(23, setItemMetaLore(Material.PINK_DYE, "§7Chargeur de SVT-40", (short) 0, listMaker2("§8Coût: §630$","§8Vente: §67.5$", _uuid)));
			inv.setItem(24, setItemMetaLore(Material.BROWN_DYE, "§7Chargeur de CheytacM200", (short) 0, listMaker2("§8Coût: §630$","§8Vente: §67.5$", _uuid)));
			inv.setItem(25, setItemMetaLore(Material.BEETROOT_SEEDS, "§7Cartouche de Calibre 12", (short) 0, listMaker2("§8Coût: §610$","§8Vente: §62.5$", _uuid)));
			inv.setItem(52, setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§r[1/2]", (short) 1));
			inv.setItem(53, setItemMeta(Material.RED_STAINED_GLASS_PANE, "§r[2/2]", (short) 1));
		}else if(name.equalsIgnoreCase("Marchand de Fusil d'Assaut (1/3)")) {
			inv.setItem(19, setItemMetaLore(Material.DIAMOND_SHOVEL, "§7RPK", (short) 11, listMaker3("§8Munitions: §7Fusil d'Assaut","§8Coût: §61400$","§8Vente: §6350$", _uuid)));
			inv.setItem(20, setItemMetaLore(Material.GOLDEN_PICKAXE, "§7AK12", (short) 5, listMaker3("§8Munitions: §7AK","§8Coût: §61450$","§8Vente: §6362.5$", _uuid)));
			inv.setItem(21, setItemMetaLore(Material.GOLDEN_PICKAXE, "§7AK74", (short) 11, listMaker3("§8Munitions: §7AK","§8Coût: §61400$","§8Vente: §6350$", _uuid)));
			inv.setItem(22, setItemMetaLore(Material.GOLDEN_AXE, "§7G36C", (short) 0, listMaker3("§8Munitions: §7G36C","§8Coût: §61700$","§8Vente: §6425$", _uuid)));
			inv.setItem(23, setItemMetaLore(Material.GOLDEN_SHOVEL, "§7ACR", (short) 19, listMaker3("§8Munitions: §7M16","§8Coût: §61650$","§8Vente: §6412.5$", _uuid)));
			inv.setItem(24, setItemMetaLore(Material.DIAMOND_SHOVEL, "§7M16 A4", (short) 0, listMaker3("§8Munitions: §7M16","§8Coût: §61600$","§8Vente: §6400$", _uuid)));
			inv.setItem(25, setItemMetaLore(Material.IRON_HOE, "§7M14", (short) 75, listMaker3("§8Munitions: §7M14","§8Coût: §61500$","§8Vente: §6375$", _uuid)));
			inv.setItem(28, setItemMetaLore(Material.DIAMOND_SHOVEL, "§7IWI ACE53", (short) 6, listMaker3("§8Munitions: §7IWI ACE53","§8Coût: §61900$","§8Vente: §6475$", _uuid)));
			inv.setItem(29, setItemMetaLore(Material.WOODEN_PICKAXE, "§7Steyr AUG", (short) 0, listMaker3("§8Munitions: §7STEYR AUG","§8Coût: §61900$","§8Vente: §6475$", _uuid)));
			inv.setItem(30, setItemMetaLore(Material.STONE_HOE, "§7HK 416", (short) 1, listMaker3("§8Munitions: §7HK 416","§8Coût: §61650$","§8Vente: §6412.5$", _uuid)));
			inv.setItem(31, setItemMetaLore(Material.GOLDEN_SHOVEL, "§7TAR 21", (short) 7, listMaker3("§8Munitions: §7TAR21","§8Coût: §61600$","§8Vente: §6400$", _uuid)));
			inv.setItem(32, setItemMetaLore(Material.IRON_HOE, "§7Famas", (short) 25, listMaker3("§8Munitions: §7FAMAS","§8Coût: §61850$","§8Vente: §6462.5$", _uuid)));
			inv.setItem(33, setItemMetaLore(Material.GOLDEN_SHOVEL, "§7FN FAL", (short) 10, listMaker3("§8Munitions: §7FNFAL","§8Coût: §61800$","§8Vente: §6450$", _uuid)));
			inv.setItem(34, setItemMetaLore(Material.IRON_HOE, "§7AS VAL", (short) 50, listMaker3("§8Munitions: §7AS VAL","§8Coût: §63200$","§8Vente: §6800$", _uuid)));
			inv.setItem(52, setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§r[1/3]", (short) 1));
			inv.setItem(53, setItemMeta(Material.RED_STAINED_GLASS_PANE, "§r[2/3]", (short) 1));
		}else if(name.equalsIgnoreCase("Marchand de Fusil d'Assaut (2/3)")) {
			inv.setItem(19, setItemMetaLore(Material.GOLDEN_SHOVEL, "§7M4A1", (short) 13, listMaker3("§8Munitions: §7M16","§8Coût: §61600$","§8Vente: §6400$", _uuid)));
			inv.setItem(20, setItemMetaLore(Material.GOLDEN_PICKAXE, "§7AKM", (short) 0, listMaker3("§8Munitions: §7AK","§8Coût: §61450$","§8Vente: §6362.5$", _uuid)));
			inv.setItem(21, setItemMetaLore(Material.DIAMOND_AXE, "§7L86", (short) 16, listMaker3("§8Munitions: §7L86","§8Coût: §61400$","§8Vente: §6350$", _uuid)));
			inv.setItem(52, setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§r[1/3]", (short) 1));
			inv.setItem(53, setItemMeta(Material.RED_STAINED_GLASS_PANE, "§r[3/3]", (short) 1));
		}else if(name.equalsIgnoreCase("Marchand de Fusil d'Assaut (3/3)")) {
			inv.setItem(19, setItemMetaLore(Material.LIGHT_BLUE_DYE, "§7Chargeur de M14", (short) 0, listMaker2("§8Coût: §640$","§8Vente: §610$", _uuid)));
			inv.setItem(20, setItemMetaLore(Material.LAPIS_LAZULI, "§7Chargeur de ASVAL", (short) 0, listMaker2("§8Coût: §650$","§8Vente: §612.5$", _uuid)));
			inv.setItem(21, setItemMetaLore(Material.BLUE_DYE, "§7Chargeur de SteyrAUG", (short) 0, listMaker2("§8Coût: §645$","§8Vente: §611.25$", _uuid)));
			inv.setItem(22, setItemMetaLore(Material.GOLD_NUGGET, "§7Chargeur de HK416", (short) 0, listMaker2("§8Coût: §640$","§8Vente: §610$", _uuid)));
			inv.setItem(23, setItemMetaLore(Material.INK_SAC, "§7Chargeur de M16", (short) 0, listMaker2("§8Coût: §640$","§8Vente: §610$", _uuid)));
			inv.setItem(24, setItemMetaLore(Material.GOLD_NUGGET, "§7Chargeur de FAMAS", (short) 0, listMaker2("§8Coût: §645$","§8Vente: §611.25$", _uuid)));
			inv.setItem(25, setItemMetaLore(Material.LIME_DYE, "§7Chargeur de FNFAL", (short) 0, listMaker2("§8Coût: §645$","§8Vente: §611.25$", _uuid)));
			inv.setItem(28, setItemMetaLore(Material.GOLD_NUGGET, "§7Chargeur de AK", (short) 0, listMaker2("§8Coût: §645$","§8Vente: §611.25$", _uuid)));
			inv.setItem(29, setItemMetaLore(Material.LIGHT_BLUE_DYE, "§7Chargeur de TAR21", (short) 0, listMaker2("§8Coût: §640$","§8Vente: §610$", _uuid)));
			inv.setItem(30, setItemMetaLore(Material.GOLD_NUGGET, "§7Chargeur de IWI ACE53", (short) 0, listMaker2("§8Coût: §645$","§8Vente: §611.25$", _uuid)));
			inv.setItem(31, setItemMetaLore(Material.INK_SAC, "§7Chargeur de L86", (short) 0, listMaker2("§8Coût: §640$","§8Vente: §610$", _uuid)));
			inv.setItem(32, setItemMetaLore(Material.LAPIS_LAZULI, "§7Chargeur de G36C", (short) 0, listMaker2("§8Coût: §645$","§8Vente: §611.25$", _uuid)));
			inv.setItem(52, setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§r[2/3]", (short) 1));
			inv.setItem(53, setItemMeta(Material.RED_STAINED_GLASS_PANE, "§r[3/3]", (short) 1));
		}else if(name.equalsIgnoreCase("Marchand d'armes lourdes (1/2)")) {
			inv.setItem(19, setItemMetaLore(Material.STONE_PICKAXE, "§7FN Scar-H", (short) 2, listMaker3("§8Munitions: §7SCAR","§8Coût: §62100$","§8Vente: §6525$", _uuid)));
			inv.setItem(20, setItemMetaLore(Material.IRON_SHOVEL, "§7M2 Browning", (short) 19, listMaker3("§8Munitions: §7M2 Browning","§8Coût: §63000$","§8Vente: §6750$", _uuid)));
			inv.setItem(21, setItemMetaLore(Material.IRON_SHOVEL, "§7MK48", (short) 13, listMaker3("§8Munitions: §7MK48","§8Coût: §62600$","§8Vente: §6650$", _uuid)));
			inv.setItem(22, setItemMetaLore(Material.WOODEN_AXE, "§7M60", (short) 0, listMaker3("§8Munitions: §7M60","§8Coût: §62800$","§8Vente: §6700$", _uuid)));
			inv.setItem(23, setItemMetaLore(Material.IRON_SHOVEL, "§7M249", (short) 16, listMaker3("§8Munitions: §7M249","§8Coût: §62500$","§8Vente: §6625$", _uuid)));
			inv.setItem(24, setItemMetaLore(Material.IRON_SHOVEL, "§7Minigun", (short) 34, listMaker3("§8Munitions: §7Minigun","§8Coût: §63200$","§8Vente: §6805$", _uuid)));
			inv.setItem(25, setItemMetaLore(Material.IRON_SHOVEL, "§7M32 MGL", (short) 22, listMaker3("§8Munitions: §7Grenade 40mm","§8Coût: §64000$","§8Vente: §61000$", _uuid)));
			inv.setItem(28, setItemMetaLore(Material.GOLDEN_HOE, "§7M79", (short) 4, listMaker3("§8Munitions: §7Grenade 40mm","§8Coût: §63000$","§8Vente: §6750$", _uuid)));
			inv.setItem(29, setItemMetaLore(Material.IRON_SHOVEL, "§7RPG-7", (short) 0, listMaker3("§8Munitions: §7Roquette","§8Coût: §63500$","§8Vente: §6875$", _uuid)));
			inv.setItem(30, setItemMetaLore(Material.WOODEN_HOE, "§7Barrett M107", (short) 11, listMaker3("§8Munitions: §7Barrett M107","§8Coût: §63000$","§8Vente: §6750$", _uuid)));
			inv.setItem(31, setItemMetaLore(Material.IRON_AXE, "§7AA12", (short) 1, listMaker3("§8Munitions: §7Calibre 12","§8Coût: §63000$","§8Vente: §6750$", _uuid)));
			inv.setItem(52, setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§r[1/2]", (short) 1));
			inv.setItem(53, setItemMeta(Material.RED_STAINED_GLASS_PANE, "§r[2/2]", (short) 1));
		}else if(name.equalsIgnoreCase("Marchand d'armes lourdes (2/2)")) {
			inv.setItem(19, setItemMetaLore(Material.BLUE_DYE, "§7Chargeur de SCAR", (short) 0, listMaker2("§8Coût: §645$","§8Vente: §611.25$", _uuid)));
			inv.setItem(20, setItemMetaLore(Material.BRICK, "§7Chargeur de M2 Browning", (short) 0, listMaker2("§8Coût: §655$","§8Vente: §613.75$", _uuid)));
			inv.setItem(21, setItemMetaLore(Material.BRICK, "§7Chargeur de MK48", (short) 0, listMaker2("§8Coût: §650$","§8Vente: §612.5$", _uuid)));
			inv.setItem(22, setItemMetaLore(Material.LIGHT_GRAY_DYE, "§7Chargeur de M60", (short) 0, listMaker2("§8Coût: §655$","§8Vente: §613.75$", _uuid)));
			inv.setItem(23, setItemMetaLore(Material.LIGHT_GRAY_DYE, "§7Chargeur de M249", (short) 0, listMaker2("§8Coût: §645$","§8Vente: §611.25$", _uuid)));
			inv.setItem(24, setItemMetaLore(Material.BRICK, "§7Chargeur de Minigun", (short) 0, listMaker2("§8Coût: §655$","§8Vente: §613.75$", _uuid)));
			inv.setItem(25, setItemMetaLore(Material.CHARCOAL, "§7Grenade 40mm", (short) 0, listMaker2("§8Coût: §660$","§8Vente: §615$", _uuid)));
			inv.setItem(28, setItemMetaLore(Material.IRON_NUGGET, "§7Roquette", (short) 0, listMaker2("§8Coût: §665$","§8Vente: §616.25$", _uuid)));
			inv.setItem(29, setItemMetaLore(Material.BEETROOT_SEEDS, "§7Cartouche de Calibre 12", (short) 0, listMaker2("§8Coût: §610$","§8Vente: §62.5$", _uuid)));
			inv.setItem(30, setItemMetaLore(Material.GREEN_DYE, "§7Chargeur de AA12", (short) 0, listMaker2("§8Coût: §650$","§8Vente: §612.5$", _uuid)));
			inv.setItem(31, setItemMetaLore(Material.BROWN_DYE, "§7Chargeur de Barrett", (short) 0, listMaker2("§8Coût: §650$","§8Vente: §612.5$", _uuid)));
			inv.setItem(52, setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§r[1/2]", (short) 1));
			inv.setItem(53, setItemMeta(Material.RED_STAINED_GLASS_PANE, "§r[2/2]", (short) 1));
		}else if(name.equalsIgnoreCase("Marchand Utilitaire")) {
			inv.setItem(52, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
			inv.setItem(53, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
			
			inv.setItem(10, setItemMetaLore(Material.IRON_HELMET, "§7Casque de CRS", (short) 0, listMaker2("§8Coût: §6500$","§8Vente: §6125$", _uuid)));
			inv.setItem(19, setItemMetaLore(Material.IRON_CHESTPLATE, "§7Veste de CRS", (short) 0, listMaker2("§8Coût: §6700$","§8Vente: §6175$", _uuid)));
			inv.setItem(28, setItemMetaLore(Material.IRON_LEGGINGS, "§7Gilet pare-balle de CRS", (short) 0, listMaker2("§8Coût: §6700$","§8Vente: §6175$", _uuid)));
			inv.setItem(37, setItemMetaLore(Material.IRON_BOOTS, "§7Bottes de CRS", (short) 0, listMaker2("§8Coût: §6500$","§8Vente: §6125$", _uuid)));
			
			inv.setItem(11, setItemMetaLore(Material.DIAMOND_HELMET, "§7Casque de Commando", (short) 0, listMaker2("§8Coût: §6800$","§8Vente: §6200$", _uuid)));
			inv.setItem(20, setItemMetaLore(Material.DIAMOND_CHESTPLATE, "§7Veste de Commando", (short) 0, listMaker2("§8Coût: §61000$","§8Vente: §6250$", _uuid)));
			inv.setItem(29, setItemMetaLore(Material.DIAMOND_LEGGINGS, "§7Gilet pare-balle de Commando", (short) 0, listMaker2("§8Coût: §61000$","§8Vente: §6250$", _uuid)));
			inv.setItem(38, setItemMetaLore(Material.DIAMOND_BOOTS, "§7Bottes de Commando", (short) 0, listMaker2("§8Coût: §6800$","§8Vente: §6200$", _uuid)));
			
			inv.setItem(13, setItemMetaLore(Material.IRON_SWORD, "§7Hache", (short) 0, listMaker2("§8Coût: §6700$","§8Vente: §6175$", _uuid)));
			inv.setItem(22, setItemMetaLore(Material.DIAMOND_SWORD, "§7Machette", (short) 0, listMaker2("§8Coût: §6900$","§8Vente: §6225$", _uuid)));
			inv.setItem(31, setItemMetaLore(Material.STONE_SHOVEL, "§7TAC Crossbow", (short) 0, listMaker3("§8Munitions: §7Carreau 40mm","§8Coût: §62500$","§8Vente: §6625$", _uuid)));
			inv.setItem(40, setItemMetaLore(Material.ARROW, "§7Carreau", (short) 0, listMaker2("§8Coût: §620$","§8Vente: §65$", _uuid)));
			
			inv.setItem(15, setItemMetaLore(Material.APPLE, "§7Pomme", (short) 0, listMaker2("§8Coût: §620$","§8Vente: §65$", _uuid)));
			inv.setItem(16, setItemMetaLore(Material.SLIME_BALL, "§7Grenade", (short) 0, listMaker2("§8Coût: §6350$","§8Vente: §687.5$", _uuid)));
			inv.setItem(17, setItemMetaLore(Material.DANDELION, "§7Parachute", (short) 0, listMaker2("§8Coût: §62000$","§8Vente: §6500$", _uuid)));
			inv.setItem(24, setItemMetaLore(Material.BREAD, "§7Pain", (short) 0, listMaker2("§8Coût: §620$","§8Vente: §65$", _uuid)));
			inv.setItem(25, setItemMetaLore(Material.QUARTZ, "§7Kit de soin", (short) 0, listMaker2("§8Coût: §6150$","§8Vente: §637.5$", _uuid)));
			inv.setItem(26, setItemMetaLore(Material.RED_DYE, "§7Lunettes de vision nocturne", (short) 0, listMaker2("§8Coût: §62000$","§8Vente: §6500$", _uuid)));
			inv.setItem(33, setItemMetaLore(Material.PUMPKIN_PIE, "§7Tarte", (short) 0, listMaker2("§8Coût: §625$","§8Vente: §66.25$", _uuid)));
			inv.setItem(34, setItemMetaLore(Material.GHAST_TEAR, "§7Seringue Antidote", (short) 0, listMaker2("§8Coût: §6150$","§8Vente: §637,5$", _uuid)));
			inv.setItem(35, setItemMetaLore(Material.GLASS_BOTTLE, "§7Bouteille d'Eau", (short) 0, listMaker2("§8Coût: §680$","§8Vente: §620$", _uuid)));
			inv.setItem(42, setItemMetaLore(Material.COOKIE, "§7Cookie", (short) 0, listMaker2("§8Coût: §610$","§8Vente: §62.5$", _uuid)));
			inv.setItem(43, setItemMetaLore(Material.CARROT, "§7Carotte", (short) 0, listMaker2("§8Coût: §617$","§8Vente: §64.25$", _uuid)));
			inv.setItem(44, setItemMetaLore(Material.END_ROD, "§7Lampe de Poche", (short) 0, listMaker2("§8Coût: §680$","§8Vente: §620$", _uuid)));
		}else if(name.equalsIgnoreCase("Marchand Utilitaire")) {
			inv.setItem(50, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
			inv.setItem(51, setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
		}
		/*
		 * 
			inv.setItem(19, setItemMetaLore(Material.WOODEN_HOE, "§7", (short) 11, listMaker3("§8Munitions: §7","§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(20, setItemMetaLore(Material.STONE_AXE, "§7", (short) 41, listMaker3("§8Munitions: §7","§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(21, setItemMetaLore(Material.GOLDEN_SHOVEL, "§7", (short) 0, listMaker3("§8Munitions: §7","§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(22, setItemMetaLore(Material.GOLDEN_HOE, "§7", (short) 0, listMaker3("§8Munitions: §7","§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(23, setItemMetaLore(Material.STONE_AXE, "§7", (short) 12, listMaker3("§8Munitions: §7","§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(24, setItemMetaLore(Material.IRON_AXE, "§7", (short) 4, listMaker3("§8Munitions: §7","§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(25, setItemMetaLore(Material.STONE_AXE, "§", (short) 37, listMaker3("§8Munitions: §7","§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(28, setItemMetaLore(Material.IRON_HOE, "§7", (short) 100, listMaker3("§8Munitions: §7","§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(29, setItemMetaLore(Material.DIAMOND_AXE, "§7", (short) 21, listMaker3("§8Munitions: §7","§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(30, setItemMetaLore(Material.IRON_HOE, "§7", (short) 0, listMaker3("§8Munitions: §7","§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(31, setItemMetaLore(Material.IRON_HOE, "§7", (short) 170, listMaker3("§8Munitions: §7","§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(32, setItemMetaLore(Material.GOLDEN_SHOVEL, "§7", (short) 4, listMaker3("§8Munitions: §7","§8Coût: §6$","§8Vente: §6$")));
			
			inv.setItem(19, setItemMetaLore(Material.ORANGE_DYE, "§7Chargeur de ", (short) 0, listMaker2("§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(20, setItemMetaLore(Material.ORANGE_DYE, "§7Chargeur de ", (short) 0, listMaker2("§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(21, setItemMetaLore(Material.ORANGE_DYE, "§7Chargeur de ", (short) 0, listMaker2("§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(22, setItemMetaLore(Material.ORANGE_DYE, "§7Chargeur de ", (short) 0, listMaker2("§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(23, setItemMetaLore(Material.ORANGE_DYE, "§7Chargeur de ", (short) 0, listMaker2("§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(24, setItemMetaLore(Material.ORANGE_DYE, "§7Chargeur de ", (short) 0, listMaker2("§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(25, setItemMetaLore(Material.MAGENTA_DYE, "§7Chargeur de ", (short) 0, listMaker2("§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(28, setItemMetaLore(Material.PURPLE_DYE, "§7Chargeur de ", (short) 0, listMaker2("§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(29, setItemMetaLore(Material.PURPLE_DYE, "§7Chargeur de ", (short) 0, listMaker2("§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(30, setItemMetaLore(Material.PURPLE_DYE, "§7Chargeur de ", (short) 0, listMaker2("§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(31, setItemMetaLore(Material.STICK, "§7Chargeur de ", (short) 0, listMaker2("§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(32, setItemMetaLore(Material.YELLOW_DYE, "§7Chargeur de ", (short) 0, listMaker2("§8Coût: §6$","§8Vente: §6$")));
			inv.setItem(52, setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§r[1/2]", (short) 1));
			inv.setItem(53, setItemMeta(Material.RED_STAINED_GLASS_PANE, "§r[2/2]", (short) 1));
		*/
	}
	
	@SuppressWarnings({ "deprecation" })
	private ItemStack setItemMeta(Material mat, String name, short dura) {
		ItemStack item = new ItemStack(mat);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name);
		item.setItemMeta(itemMeta);
		item.setDurability(dura);
		return item;
	}
	
	@SuppressWarnings({ "deprecation" })
	private ItemStack setItemMetaLore(Material mat, String name, short dura, List<String> lore) {
		ItemStack item = new ItemStack(mat);
		
		if(mat.equals(Material.GLASS_BOTTLE))
		{
			item = new ItemStack(Material.POTION, 1);
			ItemMeta meta = item.getItemMeta();
			PotionMeta pmeta = (PotionMeta) meta;
			PotionData pdata = new PotionData(PotionType.WATER);
			pmeta.setBasePotionData(pdata);
			item.setItemMeta(meta);
		}

		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		itemMeta.setDisplayName(name);
		itemMeta.setLore(lore);
		itemMeta.setUnbreakable(true);
		item.setItemMeta(itemMeta);
		item.setDurability(dura);
		return item;
	}
	
	private List<String> listMaker3(String str1, String str2,String str3, UUID _uuid){
		List<String> lore = new ArrayList<String>();
		lore.add(str1);
		lore.add(str2+reductionShow(_uuid));
		lore.add(str3);
		return lore;
	}
	private List<String> listMaker2(String str1, String str2, UUID _uuid){
		List<String> lore = new ArrayList<String>();
		lore.add(str1+reductionShow(_uuid));
		lore.add(str2);
		return lore;
	}
}
