package fr.byxis.event;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.byxis.main.ConfigManager;
import fr.byxis.main.Main;

public class menu implements Listener,CommandExecutor {

	boolean mouvement = false;
    private Main main;
    
    public menu(Main main) {
        this.main = main;
    }

    private void setItems(Inventory inv) {
        ItemStack blueGlass = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta modifiedBlueGlass = blueGlass.getItemMeta();
        modifiedBlueGlass.setDisplayName(" ");
        blueGlass.setItemMeta(modifiedBlueGlass);
        
        ItemStack limeGlass = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta modifiedLimeGlass = limeGlass.getItemMeta();
        modifiedLimeGlass.setDisplayName(" ");
        limeGlass.setItemMeta(modifiedLimeGlass);
        
        ItemStack greenGlass = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta modifiedGreenGlass = greenGlass.getItemMeta();
        modifiedGreenGlass.setDisplayName("Héliport");
        greenGlass.setItemMeta(modifiedGreenGlass);
        
        ItemStack centreVille = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta modifiedCentreVille = centreVille.getItemMeta();
        modifiedCentreVille.setDisplayName("§lCentre-Ville");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("§eCoût de la téléportation : 250$");
        modifiedCentreVille.setLore(lore);
        centreVille.setItemMeta(modifiedCentreVille);
        
        ItemStack zoneNordEst = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		ItemMeta modifiedzoneNordEst = zoneNordEst.getItemMeta();
		modifiedzoneNordEst.setDisplayName("§lZone Nord-Est");
		lore = new ArrayList<String>();
		lore.add("§eCoût de la téléportation : 500$");
		modifiedzoneNordEst.setLore(lore);
		zoneNordEst.setItemMeta(modifiedzoneNordEst);
		
		ItemStack zoneSud = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		ItemMeta modifiedzoneSud = zoneNordEst.getItemMeta();
		modifiedzoneSud.setDisplayName("§lZone Sud");
		lore = new ArrayList<String>();
		lore.add("§eCoût de la téléportation : 500$");
		modifiedzoneSud.setLore(lore);
		zoneSud.setItemMeta(modifiedzoneSud);
		
		ItemStack zoneMili = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		ItemMeta modifiedzoneMili = zoneNordEst.getItemMeta();
		modifiedzoneMili.setDisplayName("§lBase Militaire");
		lore = new ArrayList<String>();
		lore.add("§eCoût de la téléportation : 750$");
		modifiedzoneMili.setLore(lore);
		zoneMili.setItemMeta(modifiedzoneMili);
        
        inv.setItem(0, blueGlass);
        inv.setItem(1, blueGlass);
        inv.setItem(2, limeGlass);
        inv.setItem(3, limeGlass);
        inv.setItem(4, blueGlass);
        inv.setItem(5, blueGlass);
        inv.setItem(6, limeGlass);
        inv.setItem(7, limeGlass);
        inv.setItem(8, blueGlass);
        inv.setItem(9, blueGlass);
        inv.setItem(10, limeGlass);
        inv.setItem(11, centreVille);
        inv.setItem(12, limeGlass);
        inv.setItem(13, limeGlass);
        inv.setItem(14, limeGlass);
        inv.setItem(15, zoneNordEst);
        inv.setItem(16, blueGlass);
        inv.setItem(17, blueGlass);
        inv.setItem(18, blueGlass);
        inv.setItem(19, limeGlass);
        inv.setItem(20, limeGlass);
        inv.setItem(21, limeGlass);
        inv.setItem(22, limeGlass);
        inv.setItem(23, limeGlass);
        inv.setItem(24, limeGlass);
        inv.setItem(25, blueGlass);
        inv.setItem(26, blueGlass);
        inv.setItem(27, blueGlass);
        inv.setItem(28, blueGlass);
        inv.setItem(29, limeGlass);
        inv.setItem(30, limeGlass);
        inv.setItem(31, limeGlass);
        inv.setItem(32, limeGlass);
        inv.setItem(33, limeGlass);
        inv.setItem(34, blueGlass);
        inv.setItem(35, blueGlass);
        inv.setItem(36, limeGlass);
        inv.setItem(37, blueGlass);
        inv.setItem(38, limeGlass);
        inv.setItem(39, limeGlass);
        inv.setItem(40, zoneSud);
        inv.setItem(41, limeGlass);
        inv.setItem(42, limeGlass);
        inv.setItem(43, blueGlass);
        inv.setItem(44, zoneMili);
        inv.setItem(45, blueGlass);
        inv.setItem(46, blueGlass);
        inv.setItem(47, limeGlass);
        inv.setItem(48, limeGlass);
        inv.setItem(49, limeGlass);
        inv.setItem(50, blueGlass);
        inv.setItem(51, blueGlass);
        inv.setItem(52, blueGlass);
        inv.setItem(53, blueGlass);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("menu") || cmd.getName().equalsIgnoreCase("heliport")) {
                Inventory inv = Bukkit.createInventory(null, 54, "Héliport");
                setItems(inv);
                player.openInventory(inv);
                return true;
            }
        }

        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player)e.getWhoClicked();
        ItemStack current = e.getCurrentItem();
        Location centreville = new Location(Bukkit.getWorld("world"), main.getConfig().getDouble("heliport.centreville.x"), main.getConfig().getDouble("heliport.centreville.y"), main.getConfig().getDouble("heliport.centreville.z"));
        //-444.5D, 81.0D, -434.5D
        Location zoneNE = new Location(Bukkit.getWorld("world"), main.getConfig().getDouble("heliport.zonene.x"), main.getConfig().getDouble("heliport.zonene.y"), main.getConfig().getDouble("heliport.zonene.z"));
        //349.5D, 84.0D, -393.5D
        Location zoneS = new Location(Bukkit.getWorld("world"), main.getConfig().getDouble("heliport.zones.x"), main.getConfig().getDouble("heliport.zones.y"), main.getConfig().getDouble("heliport.zones.z"));
        //-179.5D, 97.0D, 605.5D
        Location zoneMilli = new Location(Bukkit.getWorld("world"), main.getConfig().getDouble("heliport.zonemilli.x"), main.getConfig().getDouble("heliport.zonemilli.y"), main.getConfig().getDouble("heliport.zonemilli.z"));
        //553.5D, 76.0D, 704.5D
        if (current != null) {
        	if(e.getView().getTitle().equalsIgnoreCase("Héliport"))
        	{
        		switch(current.getItemMeta().getDisplayName())
        		{
        			case "§lCentre-Ville":
        				TeleportPlayer(player,current,centreville, 250);
        				break;
        			case "§lZone Nord-Est":
        				TeleportPlayer(player,current,zoneNE, 500);
        				break;
        			case "§lZone Sud":
        				TeleportPlayer(player,current,zoneS, 500);
        				break;
        			case "§lBase Militaire":
        				TeleportPlayer(player,current,zoneMilli, 750);
        				break;
        				
        		}
        		e.setCancelled(true);
        	}
        }
    }
    
    private void TeleportPlayer(Player player, ItemStack current, Location loc, int price)
    {
    	if(player.getGameMode() == GameMode.CREATIVE)
    	{
    		player.sendMessage("§8Téléportation...");
			player.teleport(loc);
    	}
    	else if(main.eco.hasAccount(player)) {
        	if(main.eco.getBalance(player) >= price) {
        		ConfigManager config = main.cfgm;
        		player.closeInventory();
        		player.sendMessage("§8La téléportation commence, veuillez ne pas bougez.");
        		config.getPlayerDB().set("mouvement."+player.getName()+".time", 10);
        		config.savePlayerDB();
        		player.playSound(player.getLocation(), "minecraft:gun.hud.helico", (float) 0.1, (float) 1);
                new BukkitRunnable() {

                	public void run() {
                			
                		if (config.getPlayerDB().getBoolean("mouvement."+player.getName())) {
                			player.sendMessage("§cTéléportation annulée !");
                			config.getPlayerDB().set("mouvement."+player.getName()+".time", 50);;
                			config.getPlayerDB().set("mouvement."+player.getName()+".boulean", false);
                			config.savePlayerDB();
                			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stopsound "+player.getName()+" * minecraft:gun.hud.helico");
                			cancel();
                		}

                		if (config.getPlayerDB().getInt("mouvement."+player.getName()+".time") == 10 || config.getPlayerDB().getInt("mouvement."+player.getName()+".time") == 5) {
                			player.sendMessage("§8Téléportation dans " + config.getPlayerDB().getInt("mouvement."+player.getName()+".time") + " secondes !");
                		}

                		if (config.getPlayerDB().getInt("mouvement."+player.getName()+".time") == 0) {
                			player.sendMessage("§8Téléportation...");
                			player.teleport(loc);
                			player.sendMessage("§7Vous avez payé "+price+"$");
                			main.eco.withdrawPlayer(player, price);
                			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a times 20 100 20");
                				
                			cancel();
                		}

                		config.getPlayerDB().set("mouvement."+player.getName()+".time",config.getPlayerDB().getInt("mouvement."+player.getName()+".time") -1);
                		config.savePlayerDB();
                	}
                }.runTaskTimer(main, 0L, 20L);
                return;

        	}else{
        		player.sendMessage("§8Vous n'avez pas assez d'argent !");
        		player.closeInventory();
        	}
        }
    }
}