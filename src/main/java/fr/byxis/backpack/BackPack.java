package fr.byxis.backpack;

import fr.byxis.fireland.Fireland;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;

public class BackPack implements Listener, CommandExecutor {

    private static Fireland main;

    public BackPack(Fireland main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player p)
        {
            if(command.getName().equalsIgnoreCase("backpack") && p.hasPermission("fireland.command.backpack"))
            {
                giveBackpack(p);
            }
        }
        return false;
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        // Vérifie si le joueur tient une peau de cuir en main
        if (item.getType() == Material.LEATHER && item.getAmount() == 1) {
            Inventory backpackInv = getBackpack(player);
            // Si le joueur n'a pas encore de sac à dos, donne-lui-en un nouveau.
            player.openInventory(backpackInv);
            event.setCancelled(true); // Empêche la peau de cuir d'être utilisée comme autre chose que pour ouvrir le sac à dos.
        }
    }
    private static final String BACKPACK_KEY_PREFIX = "backpack_";
    /**
     * Récupère l'inventaire du sac à dos actuel du joueur.
     * Si le joueur n'a pas de sac à dos, cette méthode renverra null.
     */
    public static Inventory getBackpack(Player player) {
        String uuid = player.getUniqueId().toString();
        if (player.hasMetadata(BACKPACK_KEY_PREFIX + uuid)) {
            return (Inventory) player.getMetadata(BACKPACK_KEY_PREFIX + uuid).get(0).value();
        }
        else
        {

        }
        return null;
    }
    /**
     * Définit l'inventaire du sac à dos actuel pour le joueur donné.
     */
    public static void setBackpack(Player player, Inventory backpackInv) {
        String uuid = player.getUniqueId().toString();
        player.setMetadata(BACKPACK_KEY_PREFIX + uuid, new FixedMetadataValue(main, backpackInv));
    }

    public static void giveBackpack(Player player) {
        Inventory backpackInv = Bukkit.createInventory(null, 9, "Sac a Dos");
        ItemStack backpackItem = new ItemStack(Material.LEATHER);
        backpackItem.getItemMeta().setDisplayName("Sac a Dos");
        // Ajoute l'item du sac à dos dans l'inventaire principal du joueur.
        player.getInventory().addItem(backpackItem);
        // Enregistre l'inventaire du sac à dos pour ce joueur.
        setBackpack(player, backpackInv);
    }
}