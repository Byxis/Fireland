package fr.byxis.player.intendant.menu;

import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.faction.faction.FactionPlayerInformation;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static fr.byxis.player.level.LevelStorage.getPlayerLevel;

public class MenuIntendant {

    public static void OpenIntendant(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory craftMenu = Bukkit.createInventory(null, 45, "§8Intendant");
        SetIntendantItems(craftMenu, p);
        p.openInventory(craftMenu);
    }

    private static void SetIntendantItems(Inventory craftMenu, Player p)
    {
        craftMenu.setItem(28, InventoryUtilities.setItemMeta(Material.NETHER_STAR, "§bSuccès", (short) 0));
        craftMenu.setItem(12, InventoryUtilities.setItemMeta(Material.FIREWORK_ROCKET, "§eBoosters", (short) 0));
        craftMenu.setItem(10, InventoryUtilities.setItemMeta(Material.BELL, "§eMissions quotidiennes", (short) 0));
        craftMenu.setItem(14, InventoryUtilities.setItemMeta(Material.EMERALD, "§aHôtel des ventes", (short) 0));
        craftMenu.setItem(16, InventoryUtilities.setItemMeta(Material.DEAD_FIRE_CORAL, "§cEssaims", (short) 0));
        craftMenu.setItem(30, InventoryUtilities.setItemMeta(Material.PLAYER_HEAD, "§4Primes", (short) 0));
        craftMenu.setItem(32, InventoryUtilities.setItemMeta(Material.NETHERITE_SWORD, "§aFactions", (short) 0));
        switch(getPlayerLevel(p.getUniqueId()).getNation())
        {
            case Etat -> craftMenu.setItem(34, InventoryUtilities.getEtatBanner("§aVotre niveau"));
            case Bannis -> craftMenu.setItem(34, InventoryUtilities.getBannisBanner( "§cVotre niveau"));
            default-> craftMenu.setItem(34, InventoryUtilities.getNeutreBanner( "§fVotre niveau"));
        }
    }

}
