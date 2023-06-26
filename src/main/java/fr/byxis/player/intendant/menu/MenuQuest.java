package fr.byxis.player.intendant.menu;

import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.player.quest.questclass.PlayerQuests;
import fr.byxis.player.quest.questclass.QuestClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static fr.byxis.fireland.utilities.InGameUtilities.debugp;
import static fr.byxis.player.quest.QuestManager.getAvailableQuests;
import static fr.byxis.player.quest.QuestManager.getPlayerQuest;

public class MenuQuest {

    public static void OpenQuestMenu(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        FactionFunctions ff = new FactionFunctions(main, p);
        Inventory PlayerList = Bukkit.createInventory(null, 54, "§8Quêtes quotidiennes");
        SetQuestMenuItems(main, PlayerList, p);
        p.openInventory(PlayerList);
    }

    private static void SetQuestMenuItems(Fireland main, Inventory inv, Player p)
    {
        PlayerQuests pq = getPlayerQuest().get(p.getUniqueId());
        for(int i = 0; i < 9; i++)
        {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour à l'intendant", (short) 0));
        for(int i = 1; i < 5; i++) {
            QuestClass quest = getAvailableQuests().get(pq.getQuest(i).getId());
            if (pq.getQuest(i).getProgress() == -1)
            {
                inv.setItem(17+2*i, InventoryUtilities.setItemMetaLore(Material.GLOW_ITEM_FRAME, "§a"+quest.getTitle(), (short) 0, BasicUtilities.listMaker("§7"+quest.getDesc(),"§7Progrès : §aFini.","","")));
            }
            else
            {
                inv.setItem(17+2*i, InventoryUtilities.setItemMetaLore(Material.ITEM_FRAME, "§e"+quest.getTitle(), (short) 0, BasicUtilities.listMaker("§7"+quest.getDesc(),"§7Progrès : §8"+pq.getQuest(i).getProgress(),"","")));
            }
        }

        if(pq.hasFinished())
        {
            if(pq.isClaimed())
            {
                inv.setItem(45, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§aVous avez déjà récupérer les récompenses", (short) 0, BasicUtilities.listMaker("§8Après avoir effectué les 4 missions","§8quotidiennes, vous pouvez récupérer","§6200$ §8et §b1§8 jetons.","")));
            }
            else
            {
                inv.setItem(45, InventoryUtilities.setItemMetaLore(Material.STRUCTURE_VOID, "§aRécupérer les récompenses", (short) 0, BasicUtilities.listMaker("§8Après avoir effectué les 4 missions","§8quotidiennes, vous pouvez récupérer","§6200$ §8et §b1§8 jetons.","")));
            }
        }
        else
        {
            inv.setItem(45, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§cRécupérer les récompenses", (short) 0, BasicUtilities.listMaker("§8Après avoir effectué les 4 missions","§8quotidiennes, vous pouvez récupérer","§6200$ §8et §b1§8 jetons.","")));
        }

    }
}
