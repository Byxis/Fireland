package fr.byxis.event;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.byxis.fireland.Fireland;

public class heliport implements Listener,CommandExecutor {
	
	boolean mouvement = false;
	
	private Fireland main;
	
	public heliport(Fireland main) {
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
		ItemMeta modifiedzoneNordEst = centreVille.getItemMeta();
		modifiedCentreVille.setDisplayName("§lZone Nord-Est");
		lore = new ArrayList<String>();
		lore.add("§eCoût de la téléportation : 500$");
		modifiedzoneNordEst.setLore(lore);
		zoneNordEst.setItemMeta(modifiedzoneNordEst);
		
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
		inv.setItem(40, greenGlass);
		inv.setItem(41, limeGlass);
		inv.setItem(42, limeGlass);
		inv.setItem(43, blueGlass);
		inv.setItem(44, greenGlass);
		inv.setItem(45, blueGlass);
		inv.setItem(46, blueGlass);
		inv.setItem(47, limeGlass);
		inv.setItem(48, limeGlass);
		inv.setItem(49, limeGlass);
		inv.setItem(50, blueGlass);
		inv.setItem(51, blueGlass);
		inv.setItem(52, blueGlass);
		inv.setItem(53, blueGlass);
		return;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("map")) {
				
				Inventory inv = Bukkit.createInventory(null, 54,"Héliport");
				
				setItems(inv);
				
				player.openInventory(inv);
				return true;
			}
		}
		
		return false;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();
		Location loc = new Location(Bukkit.getWorld("world"), -444.5, 81, -439.5);
		
		if(current == null) return;
		
		if(e.getView().getTitle().equalsIgnoreCase("Heliport")) {
			e.setCancelled(true);
			if(current.getType().equals(Material.GREEN_STAINED_GLASS_PANE) && current.getItemMeta().getDisplayName().equalsIgnoreCase("§lCentre-Ville")) {
				player.closeInventory();
				player.sendMessage("§8La téléportation commence, veuillez ne pas bougez.");
				new BukkitRunnable() {
					
					Integer i = 10;
					
					@Override
					public void run() {
						if (mouvement) {
							player.sendMessage("§cTéléportation annulée !");
							i = 50;
							mouvement = false;
							cancel();
							}
						if(i == 10 || i == 5 ) {
							player.sendMessage("§8Téléportation dans "+i+" secondes !");
						}
						
						if(i == 0) {
							player.sendMessage("§8Téléportation...");
							player.teleport(loc);
							cancel();
						}
						
						i -= 1;
					}
				}.runTaskTimer(main, 0, 20);
				return;
			}
		}
	}
	
	@EventHandler
	public void playerMouvement(PlayerMoveEvent e) {
		
		Location from = new Location(e.getFrom().getWorld(), e.getFrom().getX(), e.getFrom().getY(), e.getFrom().getZ());
		Location to = new Location(e.getTo().getWorld(), e.getTo().getX(), e.getTo().getY(), e.getTo().getZ());
		
		if (!to.equals(from)) {
			mouvement = true;
			new BukkitRunnable() {

				@Override
				public void run() {
					mouvement = false;
					cancel();
				}
				
			}.runTaskLater(main, 20);
			return;
		}
	}

}
