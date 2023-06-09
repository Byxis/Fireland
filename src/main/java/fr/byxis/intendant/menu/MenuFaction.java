package fr.byxis.intendant.menu;

import fr.byxis.faction.FactionFunctions;
import fr.byxis.faction.FactionInformation;
import fr.byxis.faction.FactionPlayerInformation;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuFaction {

    public static void OpenFaction(Fireland main, Player p, boolean canReturn)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 0);
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation infos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        Inventory faction = Bukkit.createInventory(null, 54, "§8Votre faction: "+ff.GetColorCode(infos.getFactionName())+infos.getFactionName());
        SetFactionItems(main, faction, p, canReturn);
        p.openInventory(faction);
    }

    private static void SetFactionItems(Fireland main, Inventory inventory, Player p, boolean canReturn)
    {
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation pInfos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        FactionInformation finfos = ff.getFactionInfo(pInfos.getFactionName());
        if(pInfos != null && finfos != null)
        {
            for(int i=0;i<9;i++) {
                inventory.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
                inventory.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            }

            String role = "§aMembre";
            if(pInfos.getRole() == 1)
            {
                role = "§eModérateur";
            }
            else if(pInfos.getRole() == 2)
            {
                role = "§cLeader";
            }
            inventory.setItem(8, InventoryUtilities.setItemMeta(Material.BARRIER, "§4§lQuitter la faction", (short) 0));
            inventory.setItem(22, InventoryUtilities.setItemMetaLore(Material.ENDER_CHEST, "§aStockage - "+finfos.getCurrentChestSize()+" slots", (short) 0, BasicUtilities.listMaker("§8- Faites un §dclic gauche§8 pour ouvrir votre stockage","","","")));
            inventory.setItem(26, InventoryUtilities.GetHead(finfos.getLeader(), "§7Leader: "+Bukkit.getOfflinePlayer(finfos.getLeader()).getName()));
            inventory.setItem(30, InventoryUtilities.setItemMetaLore(Material.GOLD_INGOT, "§aArgent - §6"+finfos.getCurrentMoney()+"/"+finfos.getMaxMoney(), (short) 0, BasicUtilities.listMaker("§8- Faites un §dclic gauche §8pour ajouter §6100$","§8à la faction (shift pour 1000$)", "§8- §c(Leader)§8 Faites un §dclic droit §8pour retirer §6100$","§8de la faction (shift pour 1000$)")));
            inventory.setItem(32, InventoryUtilities.setItemMetaLore(Material.GRASS_BLOCK, "§aTerritoires claims -", (short) 0, BasicUtilities.listMaker("§cNon disponible pour le moment", "","","")));
            inventory.setItem(35, InventoryUtilities.setItemMetaLore(Material.ANVIL, "§aAméliorations -", (short) 0, BasicUtilities.listMaker("§8Accédez aux améliorations de la faction !","§cSeul le leader peut acheter des améliorations !","","")));
            inventory.setItem(45, InventoryUtilities.setItemMetaLore(Material.BOOK, "§7Vous êtes "+role+"§7.", (short) 0, BasicUtilities.listMaker("§8Date de création: "+finfos.getCreatedAt(),"","","")));
            if(canReturn)
            {
                inventory.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour à l'intendant", (short) 0));
            }
        }

    }
}
