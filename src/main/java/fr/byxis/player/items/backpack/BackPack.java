package fr.byxis.player.items.backpack;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class BackPack implements Listener, CommandExecutor {

    private static Fireland main;

    public BackPack(Fireland _main)
    {
        main = _main;
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
            if (isExceedingLimit(player))
            {
                InGameUtilities.sendPlayerError(player, "Vous ne pouvez avoir qu'un seul sac à dos à la fois !");
                event.setCancelled(true);
                return;
            }
            BackPackClass bp = new BackPackClass(item.getItemMeta().getCustomModelData());
            player.openInventory(bp.loadBackPack(item));
            if (item.getItemMeta().getCustomModelData() == 1)
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
        for (ItemStack item : p.getInventory().getContents())
        {
            if (item != null && item.getType() == Material.LEATHER && item.getItemMeta().hasCustomModelData())
            {
                sacados -= 1;
                if (sacados < 0)
                {
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().contains("Pochette")
                || e.getView().getTitle().contains("Sacoche")
                || e.getView().getTitle().contains("Sac à dos")
                || e.getView().getTitle().contains("Sac de sport")
                || e.getView().getTitle().contains("Sac de randonnée")
                || e.getView().getTitle().contains("Sac à dos militaire")) {
            Player player = (Player) e.getPlayer();
            InGameUtilities.playWorldSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 0.5f, 0f);
            Inventory inventory = e.getInventory();
            ItemStack backPackItem = player.getInventory().getItemInMainHand();
            BackPackClass bp = new BackPackClass(27);
            bp.saveBackPack(backPackItem, inventory);
        }
    }


    @EventHandler
    public void inventoryManager(InventoryClickEvent e)
    {
        if (e.getCurrentItem() != null && (e.getCurrentItem().getType() == Material.LEATHER))
        {
            BackPackClass bp = new BackPackClass(8);
            if (bp.isBackPackEmpty(e.getCurrentItem()) || e.getView().getTitle().contains("Corps de "))
            {
                return;
            }
            else if (e.getClick().isKeyboardClick())
            {
                e.setCancelled(true);
                return;
            }
            if (e.getClickedInventory().getType() != InventoryType.PLAYER ||
                    (
                        e.getView().getTopInventory().getType() != InventoryType.PLAYER
                        && e.getView().getTopInventory().getType() != InventoryType.CRAFTING
                        && e.getView().getTopInventory().getType() != InventoryType.CREATIVE
                    )
            )
            {
                if (!e.getView().getTitle().contains("Coffre") || !(e.getView().getTitle().contains("Stockage") && e.getView().getTitle().contains("de bunker")))
                {
                    e.setCancelled(true);
                    return;
                }
            }
        }
        if (e.getClick().isKeyboardClick() && e.getView().getPlayer().getInventory().getItem(e.getHotbarButton()) != null)
        {
            ItemStack item = e.getView().getPlayer().getInventory().getItem(e.getHotbarButton());
            assert item != null;
            if (item.getType() != Material.LEATHER)
                return;

            BackPackClass bp = new BackPackClass(8);
            if (bp.isBackPackEmpty(item))
            {
                return;
            }
            else if (e.getClick().isKeyboardClick())
            {
                e.setCancelled(true);
                return;
            }
        }
        if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.WHITE_STAINED_GLASS_PANE)
        {
            if (e.getView().getTitle().contains("Pochette")
                    || e.getView().getTitle().contains("Sacoche")
                    || e.getView().getTitle().contains("Sac à dos")
                    || e.getView().getTitle().contains("Sac de sport")
                    || e.getView().getTitle().contains("Sac de randonnée")
                    || e.getView().getTitle().contains("Sac à dos militaire")
            )
            {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void playerDrag(InventoryDragEvent e)
    {
        if (e.getCursor() != null && (e.getCursor().getType() == Material.LEATHER))
        {
            BackPackClass bp = new BackPackClass(8);
            if (bp.isBackPackEmpty(e.getCursor()) || e.getView().getTitle().contains("Corps de "))
            {
                return;
            }
            if (e.getInventory().getType() != InventoryType.PLAYER || (e.getView().getTopInventory().getType() != InventoryType.PLAYER
                    && e.getView().getTopInventory().getType() != InventoryType.CRAFTING
                    && e.getView().getTopInventory().getType() != InventoryType.CREATIVE)
            )
            {
                if (!e.getView().getTitle().contains("Coffre") || !(e.getView().getTitle().contains("Stockage") && e.getView().getTitle().contains("de bunker")))
                {
                    e.setCancelled(true);
                }
            }
        }
    }

    public void giveItem(Player p, int level) {
        BackPackClass bp = new BackPackClass(level);
        ItemStack backpack = new ItemStack(Material.LEATHER, 1);

        ItemMeta meta = backpack.getItemMeta();
        meta.setCustomModelData(level);

        String name = switch (level) {
            case 1 -> "§cPochette";
            case 2 -> "§cSacoche";
            case 3 -> "§cSac à dos";
            case 4 -> "§cSac de sport";
            case 5 -> "§cSac de randonnée";
            case 6 -> "§cSac à dos militaire";
            default -> "§cA venir";
        };
        meta.setDisplayName(name);

        ArrayList<String> lore = new ArrayList<>();
        lore.add("§8Faites clic droit avec pour l'ouvrir");
        lore.add("§8Vous ne pouvez pas le stocker");
        meta.setLore(lore);

        backpack.setItemMeta(meta);

        bp.createBackPack(backpack);
        p.getInventory().addItem(backpack);
    }

}