package fr.byxis.player.items.backpack;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class BackPack implements Listener, CommandExecutor {

    private static Fireland main;

    public BackPack(Fireland main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player p) {
            if (command.getName().equalsIgnoreCase("backpack") && p.hasPermission("fireland.command.backpack")) {
                if (strings.length == 0) {
                    giveItem(p, 1);
                } else {
                    giveItem(p, Integer.parseInt(strings[0]));
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        // Vérifie si le joueur tient une peau de cuir en main
        if (item.getType() == Material.LEATHER && item.getAmount() == 1 && item.getItemMeta().hasCustomModelData() && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
            // Si le joueur n'a pas encore de sac à dos, donne-lui-en un nouveau.
            if(isExceedingLimit(player))
            {
                InGameUtilities.sendPlayerError(player, "Vous ne pouvez avoir qu'un seul sac à dos et une seule ceinture de munition à la fois  ou deux ceintures de munitions à la fois !");
                event.setCancelled(true);
                return;
            }
            BackPackClass bp = new BackPackClass(item.getItemMeta().getCustomModelData());
            player.openInventory(bp.loadBackPack(item));
            if(item.getItemMeta().getCustomModelData() == 1)
            {
                InGameUtilities.playWorldSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.PLAYERS, 0.5f, 0f);
            }
            else
            {
                InGameUtilities.playWorldSound(player.getLocation(), "gun.hud.bag_open", SoundCategory.PLAYERS, 0.5f, 1f);
            }
            InGameUtilities.playWorldSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 0.5f, 0f);
            event.setCancelled(true); // Empêche la peau de cuir d'être utilisée comme autre chose que pour ouvrir le sac à dos.
        }
    }

    public boolean isExceedingLimit(Player p)
    {
        int sacados = 1;
        int ceinture = 2;
        for(ItemStack item : p.getInventory().getContents())
        {
            if(item != null && item.getType() == Material.LEATHER && item.getItemMeta().hasCustomModelData())
            {
                ceinture -=1;
                if(item.getItemMeta().getCustomModelData() != 1)
                {
                    sacados -=1;
                }
                if(sacados < 0 ||ceinture < 0)
                {
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().contains("Sac à dos") || event.getView().getTitle().contains("Ceinture de munitions")) {
            Player player = (Player) event.getPlayer();
            InGameUtilities.playWorldSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 0.5f, 0f);
            Inventory inventory = event.getInventory();
            ItemStack backPackItem = player.getInventory().getItemInMainHand();
            BackPackClass bp = new BackPackClass(27);
            bp.saveBackPack(backPackItem, inventory);
        }
    }


    @EventHandler
    public void inventoryManager(InventoryClickEvent e)
    {
        if(e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.LEATHER)
        {
            if(e.getClickedInventory().getType() != InventoryType.PLAYER || (e.getView().getTopInventory().getType() != InventoryType.PLAYER
                    &&e.getView().getTopInventory().getType() != InventoryType.CRAFTING
                    &&e.getView().getTopInventory().getType() != InventoryType.CREATIVE))
            {
                e.setCancelled(true);
            }
        }
    }

    public void giveItem(Player p, int level)
    {
        BackPackClass bp = new BackPackClass(level);
        ItemStack backpack = new ItemStack(Material.LEATHER, 1);

        ItemMeta meta = backpack.getItemMeta();
        meta.setCustomModelData(level);
        switch(level)
        {
            default -> meta.setDisplayName("§cCeinture de munitions");
            case 2,4,5 -> meta.setDisplayName("§cA venir");
            case 3 -> meta.setDisplayName("§cSac à dos léger");
            case 6 -> meta.setDisplayName("§cSac à dos militaire");
        }

        ArrayList<String> lore = new ArrayList<String>();
        lore.add("§8Faites clic droit avec pour l'ouvrir");
        lore.add("§8Vous ne pouvez pas le stocker");
        meta.setLore(lore);

        backpack.setItemMeta(meta);

        bp.createBackPack(backpack);
        p.getInventory().addItem(backpack);
    }
}