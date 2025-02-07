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

import java.time.LocalDate;
import java.util.ArrayList;

public class MenuPlayerList {

    public static void OpenPlayerList(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation infos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        Inventory PlayerList = Bukkit.createInventory(null, 54, "§8Membres de " + ff.GetColorCode(infos.getFactionName()) + infos.getFactionName());
        SetPlayerListItems(main, PlayerList, p);
        p.openInventory(PlayerList);
    }

    private static void SetPlayerListItems(Fireland main, Inventory inventory, Player p)
    {
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation pInfos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        if (pInfos != null)
        {
            ArrayList<FactionPlayerInformation> infos = ff.getPlayersFromFaction(pInfos.getFactionName());

            for (int i = 0; i < 9; i++)
            {
                inventory.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            }
            inventory.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour à l'intendant", (short) 0));
            int member = 9;
            String you = "";
            String nouveau = "";
            for (FactionPlayerInformation info : infos)
            {
                LocalDate lDate = info.getJoinDate().toLocalDateTime().toLocalDate();

                java.time.Period prd = java.time.Period.between(lDate, java.time.LocalDate.now());
                String connectionInformation = "";
                if (Bukkit.getOfflinePlayer(info.getUuid()).isOnline())
                {
                    connectionInformation = "§b\u23FA §r";
                }
                else
                {
                    connectionInformation = "§8\u2B58 §r";
                }
                if (info.getName().equalsIgnoreCase(p.getName()))
                {
                    you = " §d(Vous)";
                }
                if (prd.getDays() <= 7)
                {
                    nouveau = " §1(Nouveau)";
                }
                if (info.getRole() == 2)
                {
                    inventory.setItem(0, InventoryUtilities.GetHead(info.getUuid(), connectionInformation + "§cLeader: " + Bukkit.getOfflinePlayer(info.getUuid()).getName() + you));
                }
                if (info.getRole() == 1)
                {

                    inventory.setItem(member, InventoryUtilities.GetHead(info.getUuid(), connectionInformation + "§eModérateur: " + Bukkit.getOfflinePlayer(info.getUuid()).getName() + you + nouveau));
                    member++;
                }
                if (info.getRole() == 0)
                {
                    inventory.setItem(member, InventoryUtilities.GetHead(info.getUuid(), connectionInformation + "§aMembre: " + Bukkit.getOfflinePlayer(info.getUuid()).getName() + you + nouveau));
                    member++;
                }
                you = "";
                nouveau = "";
            }
        }
    }

}
