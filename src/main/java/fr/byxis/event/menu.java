package fr.byxis.event;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
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

import java.util.ArrayList;

public class menu implements Listener,CommandExecutor {

	boolean mouvement = false;
    private Fireland main;
    
    public menu(Fireland main) {
        this.main = main;
    }

    private void setItems(Inventory inv) {
        ItemStack centreVille = new ItemStack(Material.WHEAT_SEEDS);
        ItemMeta modifiedCentreVille = centreVille.getItemMeta();
        modifiedCentreVille.setCustomModelData(200);
        modifiedCentreVille.setDisplayName("§lCentre-Ville");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("§eCoût de la téléportation : "+main.getConfig().getInt("heliport.centreville.price")+"$");
        modifiedCentreVille.setLore(lore);
        centreVille.setItemMeta(modifiedCentreVille);
        inv.setItem(11, centreVille);
        inv.setItem(12, centreVille);
        
        ItemStack zoneNordEst = new ItemStack(Material.WHEAT_SEEDS);
		ItemMeta modifiedzoneNordEst = zoneNordEst.getItemMeta();
        modifiedzoneNordEst.setCustomModelData(200);
		modifiedzoneNordEst.setDisplayName("§lZone Nord-Est");
		lore = new ArrayList<String>();
		lore.add("§eCoût de la téléportation : "+main.getConfig().getInt("heliport.zonene.price")+"$");
		modifiedzoneNordEst.setLore(lore);
		zoneNordEst.setItemMeta(modifiedzoneNordEst);
        inv.setItem(14, zoneNordEst);
        inv.setItem(15, zoneNordEst);
		
		ItemStack zoneSud = new ItemStack(Material.WHEAT_SEEDS);
		ItemMeta modifiedzoneSud = zoneNordEst.getItemMeta();
        modifiedzoneSud.setCustomModelData(200);
		modifiedzoneSud.setDisplayName("§lZone Sud");
		lore = new ArrayList<String>();
		lore.add("§eCoût de la téléportation : "+main.getConfig().getInt("heliport.zones.price")+"$");
		modifiedzoneSud.setLore(lore);
		zoneSud.setItemMeta(modifiedzoneSud);
        inv.setItem(48, zoneSud);
        inv.setItem(49, zoneSud);
		
		ItemStack zoneMili = new ItemStack(Material.WHEAT_SEEDS);
		ItemMeta modifiedzoneMili = zoneNordEst.getItemMeta();
        modifiedzoneMili.setCustomModelData(200);
		modifiedzoneMili.setDisplayName("§lBase Militaire");
		lore = new ArrayList<String>();
		lore.add("§eCoût de la téléportation : "+main.getConfig().getInt("heliport.zonemilli.price")+"$");
		modifiedzoneMili.setLore(lore);
		zoneMili.setItemMeta(modifiedzoneMili);
        inv.setItem(51, zoneMili);
        inv.setItem(52, zoneMili);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("menu") || cmd.getName().equalsIgnoreCase("heliport")) {
                //"\uD83E\uDED4"
                Inventory inv = Bukkit.createInventory(null, 54, "§f\uD83C\uDFA0");
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
        				TeleportPlayer(player,current,centreville, main.getConfig().getInt("heliport.centreville.price"));
        				break;
        			case "§lZone Nord-Est":
        				TeleportPlayer(player,current,zoneNE, main.getConfig().getInt("heliport.zonene.price"));
        				break;
        			case "§lZone Sud":
        				TeleportPlayer(player,current,zoneS, main.getConfig().getInt("heliport.zones.price"));
        				break;
        			case "§lBase Militaire":
        				TeleportPlayer(player,current,zoneMilli, main.getConfig().getInt("heliport.zonemilli.price"));
        				break;
        				
        		}
        		e.setCancelled(true);
        	}
        }
    }
    
    private void TeleportPlayer(Player player, ItemStack current, Location loc, int price)
    {
    	if((player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) && !main.hashMapManager.isTeleporting(player.getUniqueId()))
    	{
            InGameUtilities.teleportPlayer(player, loc, 0, "");
    	}
    	else if(main.eco.hasAccount(player) && !main.hashMapManager.isTeleporting(player.getUniqueId())) {
        	if(main.eco.getBalance(player) >= price) {
        		player.closeInventory();
        		player.sendMessage("§8La téléportation commence, veuillez ne pas bougez.");
                teleportPlayer(player, loc, 15, "gun.hub.helico", main, price);

        	}else{
        		player.sendMessage("§8Vous n'avez pas assez d'argent !");
        		player.closeInventory();
        	}
        }
    }

    public void teleportPlayer(Player player, Location loc, int duration, String sound, Fireland main, int price)
    {
        player.playSound(player.getLocation(), "minecraft:"+sound, (float) 0.1, (float) 1);
        main.hashMapManager.addTeleporting(player.getUniqueId());

        new BukkitRunnable() {
            private int i = -1;

            @Override
            public void run() {
                i++;
                if(InGameUtilities.getPlayerMoving(player)){
                    InGameUtilities.sendPlayerError(player,"Téléportation annulée !");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stopsound "+player.getName()+" * minecraft:"+sound);
                    main.hashMapManager.removeTeleporting(player.getUniqueId());
                    cancel();
                }
                else
                {
                    if((i%5 == 0 && i != duration) || i == duration-3 ||i  == duration-2 || i  == duration-1)
                    {
                        InGameUtilities.sendPlayerInformation(player,"Téléportation dans " +(duration-i)+" secondes");
                    }
                    if(i == duration)
                    {
                        InGameUtilities.sendPlayerInformation(player,"Téléportation...");
                        player.teleport(loc);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a times 20 100 20");
                        player.sendMessage("§7Vous avez payé "+price+"$");
                        main.eco.withdrawPlayer(player, price);
                        main.hashMapManager.removeTeleporting(player.getUniqueId());
                        cancel();
                    }
                }

            }
        }.runTaskTimer(main, 0L, 20L);
    }
}