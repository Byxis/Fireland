package fr.byxis.player.intendant.menu;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.player.quest.QuestManager;
import fr.byxis.player.quest.questclass.PlayerQuests;
import fr.byxis.player.quest.questclass.QuestClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

import static fr.byxis.player.items.notes.OpenNotes.usingSubstringMethod;
import static fr.byxis.player.quest.QuestManager.getAvailableQuests;
import static fr.byxis.player.quest.QuestManager.getPlayerQuest;

public class MenuQuest {

    public static void openQuestMenu(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory playerList = Bukkit.createInventory(null, 54, "§8Quêtes quotidiennes");
        setQuestMenuItems(main, playerList, p);
        p.openInventory(playerList);
    }

    private static void setQuestMenuItems(Fireland main, Inventory inv, Player p)
    {
        PlayerQuests pq = getPlayerQuest().get(p.getUniqueId());
        for (int i = 0; i < 9; i++)
        {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour à l'intendant", (short) 0));
        for (int i = 1; i < 5; i++)
        {
            QuestClass quest = getAvailableQuests().get(pq.getQuest(i).getId());
            if (quest == null)
            {
                QuestManager.fillQuests(p);
                quest = getAvailableQuests().get(pq.getQuest(i).getId());
            }
            if (quest == null)
            {
                MenuIntendant.openIntendant(main, p);
                return;
            }

            if (pq.getQuest(i).getProgress() == -1)
            {
                List<String> desc = usingSubstringMethod(quest.getDesc(), 52, "§7");
                desc.add("§7Progrès : §aFini.");
                inv.setItem(17 + 2 * i, InventoryUtilities.setItemMetaLore(Material.GLOW_ITEM_FRAME, "§a" + quest.getTitle(), (short) 0, desc));
            }
            else
            {
                List<String> desc = usingSubstringMethod(quest.getDesc(), 53, "§r§7");
                desc.add("§7Progrès : §8" + pq.getQuest(i).getProgress());
                inv.setItem(17 + 2 * i, InventoryUtilities.setItemMetaLore(Material.ITEM_FRAME, "§e" + quest.getTitle(), (short) 0, desc));
            }
        }

        if (pq.hasFinished())
        {
            if (pq.isClaimed())
            {
                inv.setItem(45, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§aVous avez déjà récupérer les récompenses", (short) 0, BasicUtilities.listMaker("§8Après avoir effectué les 4 missions", "§8quotidiennes, vous pouvez récupérer", "§6200$ §8et §b1§8 jetons.", "")));
            }
            else
            {
                inv.setItem(45, InventoryUtilities.setItemMetaLore(Material.STRUCTURE_VOID, "§aRécupérer les récompenses", (short) 0, BasicUtilities.listMaker("§8Après avoir effectué les 4 missions", "§8quotidiennes, vous pouvez récupérer", "§6200$ §8et §b1§8 jetons.", "")));
            }
        }
        else
        {
            inv.setItem(45, InventoryUtilities.setItemMetaLore(Material.BARRIER, "§cRécupérer les récompenses", (short) 0, BasicUtilities.listMaker("§8Après avoir effectué les 4 missions", "§8quotidiennes, vous pouvez récupérer", "§6200$ §8et §b1§8 jetons.", "")));
        }

    }
}
