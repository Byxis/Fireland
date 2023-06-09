package fr.byxis.event;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class bank implements Listener, CommandExecutor {
	
	final private Fireland main;
	
	public bank(Fireland main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("bank")) 
			{
				int length = args.length;
				if(!player.hasPermission("fireland.command.bank.set") || length == 0)
				{
					openBankMenu(player);
					return true;
				}
				else if(player.hasPermission("fireland.command.bank.set"))
				{
					if(length == 3) 
					{
						if(args[1].equalsIgnoreCase("money"))
						{
							player.sendMessage("§aVous avez maintenant "+args[2]+" dans votre banque !");
							main.cfgm.getEnderchest().set("bank."+player.getUniqueId()+".money", Integer.parseInt(args[2]));
							main.cfgm.saveEnderchest();
							return true;
						}
						else if(args[1].equalsIgnoreCase("upgrade"))
						{
							player.sendMessage("§aVous avez maintenant l'amélioration "+args[2]+" !");
							main.cfgm.getEnderchest().set("bank."+player.getUniqueId()+".upgrade", Integer.parseInt(args[2]));
							main.cfgm.saveEnderchest();
							return true;
						}
					}
					else if(length == 4) 
					{
						if(args[1].equalsIgnoreCase("money"))
						{
							Player victim =  (Player) Bukkit.getOfflinePlayer(BasicUtilities.getUuid(args[3]));
							player.sendMessage("§aLe joueur "+args[3]+" à maintenant "+args[2]+" dans sa banque !");
							main.cfgm.getEnderchest().set("bank."+victim.getUniqueId()+".money", Integer.parseInt(args[2]));
							main.cfgm.saveEnderchest();
							return true;
						}
						else if(args[1].equalsIgnoreCase("upgrade"))
						{
							Player victim =  (Player) Bukkit.getOfflinePlayer(BasicUtilities.getUuid(args[3]));
							player.sendMessage("§aLe joueur "+args[3]+" à maintenant l'amélioration "+args[2]+" !");
							main.cfgm.getEnderchest().set("bank."+victim.getUniqueId()+".upgrade", Integer.parseInt(args[2]));
							main.cfgm.saveEnderchest();
							return true;
						}
					}
					else
					{
						player.sendMessage("§cUsage : /bank set <upgrade/money> [player]");
					}
				}
				
				/*if(!msg.contains("set")) return false;
				if((args[0].equalsIgnoreCase("set") && !args[1].equalsIgnoreCase(""))) 
				{
					if(args[1].equalsIgnoreCase("money"))
					{
						if(!args[2].equalsIgnoreCase("") && !args[3].equalsIgnoreCase(""))
						{//bank set money int player
							player.sendMessage("§aLe joueur "+args[3]+" à maintenant "+args[2]+" dans sa banque !");
							main.getConfig().set("bank."+args[3]+".money", args[2]);
							main.saveConfig();
							return true;
						}
						else if (!args[3].equalsIgnoreCase(""))
						{
							player.sendMessage("§aVous avez maintenant "+args[2]+" dans votre banque !");
							main.getConfig().set("bank."+player.getUniqueId()+".money", args[2]);
							main.saveConfig();
							return true;
						}
					}
					else if(args[1].equalsIgnoreCase("upgrade"))
					{
						if(!args[2].equalsIgnoreCase("") && !(args[3] != ""))
						{//bank set upgrade int player
							player.sendMessage("§aLe joueur "+args[3]+" à maintenant l'amélioration "+args[2]+" !");
							main.getConfig().set("bank."+args[3]+".upgrade", args[2]);
							main.saveConfig();
							return true;
						}
						else if (!args[3].equalsIgnoreCase(""))
						{
							player.sendMessage("§aVous avez maintenant l'amélioration "+args[2]+" !");
							main.getConfig().set("bank."+player.getUniqueId()+".upgrade", args[2]);
							main.saveConfig();
							return true;
						}
						
					}
				}*/
				return true;
				
			}
		}
		return false;
	}
	
	@EventHandler
	public void itemClickEvent(InventoryClickEvent e) 
	{
		Player player = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();
		
		if(current == null) return;
		
		if(e.getView().getTitle().contains("Votre argent :")) 
		{
			e.setCancelled(true);
			
			int playerBankMoney = main.cfgm.getEnderchest().getInt("bank."+player.getUniqueId()+".money");
			double playerMoney = main.eco.getBalance(player);
			
			if(current.getType().equals(Material.GOLD_INGOT))
			{
				if(e.getClick().isLeftClick())
				{
					if(e.isShiftClick())
					{
						int max = getMaxMoney(main.cfgm.getEnderchest().getInt("bank."+player.getUniqueId()+".upgrade"));

						if(playerBankMoney >= max)
						{
							return;
						}
						
						if(playerMoney+playerBankMoney < max)
						{
							main.eco.withdrawPlayer(player, playerMoney);
							InGameUtilities.playPlayerSound(player, "gun.hud.money_drop", SoundCategory.AMBIENT, 1, 1);
							main.cfgm.getEnderchest().set("bank."+player.getUniqueId()+".money", playerBankMoney+playerMoney);
							main.cfgm.saveEnderchest();
							openBankMenu(player);
						}
						else
						{
							main.eco.withdrawPlayer(player, (max - playerBankMoney));
							InGameUtilities.playPlayerSound(player, "gun.hud.money_drop", SoundCategory.AMBIENT, 1, 1);
							main.cfgm.getEnderchest().set("bank."+player.getUniqueId()+".money", max);
							main.cfgm.saveEnderchest();
							openBankMenu(player);
						}
					}
					else if (playerMoney >= 100 && playerBankMoney+100 <= getMaxMoney(main.cfgm.getEnderchest().getInt("bank."+player.getUniqueId()+".upgrade")))
					{
						main.eco.withdrawPlayer(player, 100);
						InGameUtilities.playPlayerSound(player, "gun.hud.money_drop", SoundCategory.AMBIENT, 1, 1);
						main.cfgm.getEnderchest().set("bank."+player.getUniqueId()+".money", playerBankMoney+100);
						main.cfgm.saveEnderchest();
						openBankMenu(player);
					}
				}
				else if (e.getClick().isRightClick())
				{
					if(e.isShiftClick())
					{
						main.eco.depositPlayer(player, playerBankMoney);
						InGameUtilities.playPlayerSound(player, "gun.hud.money_pickup", SoundCategory.AMBIENT, 1, 1);
						main.cfgm.getEnderchest().set("bank."+player.getUniqueId()+".money", 0);
						main.cfgm.saveEnderchest();
						openBankMenu(player);
					}
					else if(playerBankMoney >= 100)
					{
						main.eco.depositPlayer(player, 100);
						InGameUtilities.playPlayerSound(player, "gun.hud.money_pickup", SoundCategory.AMBIENT, 1, 1);
						main.cfgm.getEnderchest().set("bank."+player.getUniqueId()+".money", playerBankMoney-100);
						main.cfgm.saveEnderchest();
						openBankMenu(player);
					}
				}
				
			}
			else if(current.getType().equals(Material.ENDER_CHEST))
			{
				int slot = getMaxSlots(main.cfgm.getEnderchest().getInt("bank."+player.getUniqueId()+".upgrade"));
				if(!main.hashMapManager.getStorageMap().containsKey(e.getView().getPlayer().getUniqueId()))
				{
					Inventory ec = Bukkit.createInventory(null, slot, "§8Stockage de "+player.getName());
					int i = 0;
					for (ItemStack item : loadEnderchest(player))
					{
						if(i < slot)
						{
							ec.setItem(i, item);
						}
						i++;
					}
					player.openInventory(ec);
					main.hashMapManager.addStorageMap(player.getUniqueId(), ec);
				}
				else
				{
					if(slot != main.hashMapManager.getStorageMap().get(e.getView().getPlayer().getUniqueId()).getSize())
					{
						Inventory ec = Bukkit.createInventory(null, slot, "§8Stockage de "+player.getName());
						int i = 0;
						for (ItemStack item : main.hashMapManager.getStorageMap().get(e.getView().getPlayer().getUniqueId()))
						{
							if(i < slot)
							{
								ec.setItem(i, item);
							}
							i++;
						}
						player.openInventory(ec);
						main.hashMapManager.addStorageMap(player.getUniqueId(), ec);
					}
					else
					{
						player.openInventory(main.hashMapManager.getStorageMap().get(e.getView().getPlayer().getUniqueId()));
					}
				}
			}
			
			if(current.getType().equals(Material.ANVIL))
			{
				int price = getMaxMoney(main.cfgm.getEnderchest().getInt("bank."+player.getUniqueId()+".upgrade"));
				if(playerMoney >= price)
				{
					main.eco.withdrawPlayer(player, price);
					main.cfgm.getEnderchest().set("bank."+player.getUniqueId()+".upgrade", main.cfgm.getEnderchest().getInt("bank."+player.getUniqueId()+".upgrade")+1);
					player.sendMessage("§aVous avez payé §6"+price+"$ §a pour améliorer votre banque au niveau §d"+(main.cfgm.getEnderchest().getInt("bank."+player.getUniqueId()+".upgrade")+1)+"§a !");
					InGameUtilities.playPlayerSound(player, "block.anvil.use", SoundCategory.AMBIENT, 1, 1);
					InGameUtilities.playPlayerSound(player, "entity.player.levelup", SoundCategory.AMBIENT, 1, 1);
					main.cfgm.saveEnderchest();
					openBankMenu(player);
				}
				else
				{
					player.sendMessage("§cVous n'avez pas assez d'argent.");
				}
			}
		}/*
		else if(e.getView().getTitle().contains("§8Stockage de "))
		{
			main.hashMapManager.replaceStorageMap(player.getUniqueId(), e.getInventory());
			//saveEnderchest(e.getInventory().getContents(),player);
		}*/
	}
	
	@EventHandler
	public void closeInventory(InventoryCloseEvent e)
	{
		Player player = (Player) e.getPlayer();
		if(e.getView().getTitle().equalsIgnoreCase("§8Stockage de "+player.getName())) 
		{
			InGameUtilities.playPlayerSound(player, "entity.villager.yes", SoundCategory.AMBIENT, 1, 1);
			//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound minecraft:entity.villager.yes ambient "+player.getName()+" ~ ~ ~ 1");
			//saveEnderchest(e.getInventory().getContents(),player);
		}
	}
	
	private void openBankMenu(Player player)
	{
		int money = main.cfgm.getEnderchest().getInt("bank."+player.getUniqueId()+".money");
		int maxMoney = getMaxMoney(main.cfgm.getEnderchest().getInt("bank."+player.getUniqueId()+".upgrade"));
		
		Inventory bank = Bukkit.createInventory(null, 27, "§8Votre argent : §6"+money+"$ / "+maxMoney+"$");
		setItemsMenuBank(bank, player);
		player.openInventory(bank);
	}

	private int getMaxMoney(int upgrade) 
	{
		int max = switch (upgrade) {
			case 1 -> 2500;
			case 2 -> 5000;
			case 3 -> 10000;
			case 4 -> 25000;
			case 5 -> 50000;
			case 6, 7 -> 100000;
			default -> 1000;
		};

		return max;
	}
	
	private int getMaxSlots(int upgrade) 
	{
		int max = switch (upgrade) {
			case 0 -> 9;
			case 1 -> 18;
			case 2 -> 27;
			case 3 -> 36;
			case 4 -> 45;
			case 5, 6, 7 -> 54;
			default -> 0;
		};

		return max;
	}
	
	private void setItemsMenuBank(Inventory inv, Player player)
	{
		inv.setItem(11, setItemMetaLore(Material.GOLD_INGOT, "§aArgent -", (short) 0, listMaker("§8Faites un §dclic gauche §8pour ajouter §6100$","§8à votre compte en banque", "§8Faites un §dclic droit §8pour retirer §6100$","§8de votre compte en banque")));
		if(main.cfgm.getEnderchest().getInt("bank."+player.getUniqueId()+".upgrade") < 7)
		{
			inv.setItem(13, setItemMetaLore(Material.ANVIL, "§aAmélioration - Prix : §6"+getMaxMoney(main.cfgm.getEnderchest().getInt("bank."+player.getUniqueId()+".upgrade"))+"$", (short) 0, listMaker("§8Vous avez actuellement l'amélioration n°§d"+main.cfgm.getEnderchest().getInt("bank."+player.getUniqueId()+".upgrade")+" ","§8Pour l'améliorer au niveau suivant :","§8- Maximum de la banque : §6"+getMaxMoney((main.cfgm.getEnderchest().getInt("bank."+player.getUniqueId()+".upgrade")+1))+"$", "§8- Maximum du stockage : §6"+getMaxSlots((main.cfgm.getEnderchest().getInt("bank."+player.getUniqueId()+".upgrade")+1))+" slots")));
		}
		else
		{

			inv.setItem(13, setItemMetaLore(Material.BOOK, "§aAmélioration -", (short) 0, listMaker("§8Vous avez atteint le maximum d'amélioration !","","","")));
		}
		int slots = getMaxSlots(main.cfgm.getEnderchest().getInt("bank."+player.getUniqueId()+".upgrade"));
		inv.setItem(15, setItemMetaLore(Material.ENDER_CHEST, "§aStockage personnel - ", (short) 0, listMaker("§8Faites un §dclic gauche§8 pour ouvrir votre stockage","§8Vous disposez actuellement de §6"+slots+"§8 slots de stockage !","§8L'amélioration suivant vous permettra de passer §6","§8à §6"+(slots+9)+"§8 slots !")));
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
	
	private List<String> listMaker(String str1, String str2,String str3, String str4){
		List<String> lore = new ArrayList<String>();
		if(str1 != "") lore.add(str1);
		if(str2 != "") lore.add(str2);
		if(str3 != "")lore.add(str3);
		if(str4 != "")lore.add(str4);
		return lore;
	}
	
	public ItemStack[] loadEnderchest(Player player) {
		FileConfiguration config = main.cfgm.getEnderchest();
        List<ItemStack> itemstackList = new ArrayList<ItemStack>();
        
        /*for(int i = 0; i < main.getConfig().getInt("bank."+player.getUniqueId()+".upgrade")*9; i++)
        {
        	if (config.contains(player.getUniqueId()+"."+i))
       	 	{
       		 	itemstackList.add(config.getItemStack(player.getUniqueId()+"."+i));
       	 	} 
       	 	else
       	 	{
       	 		itemstackList.add(null);
       	 	}
        }*/
        
        //boolean done = false;
		int i = -1;
		int maxSlots = getMaxSlots(main.cfgm.getEnderchest().getInt("bank."+player.getUniqueId()+".upgrade"));
		while (i < maxSlots) {
			i++;
			if (config.contains("stockage."+player.getUniqueId()+"."+i))
			{
				itemstackList.add(config.getItemStack("stockage."+player.getUniqueId()+"."+i));
			}
			else
			{
				itemstackList.add(null);
			}

		}
		//Some converting and returning
		ItemStack[] toReturn = itemstackList.toArray(new ItemStack[itemstackList.size()]);
		return toReturn;
	}
}