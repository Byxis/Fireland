package fr.byxis.player.intendant.menu;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.player.level.LevelSavings;
import fr.byxis.player.level.PlayerLevel;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

import static fr.byxis.player.level.LevelStorage.GetPlayerLevelSavings;
import static fr.byxis.player.level.LevelStorage.getPlayerLevel;
import static fr.byxis.player.scoreboard.ScoreboardPlayer.getTimeString;

public class MenuLevel {

    public static void OpenLevelMenu(Fireland main, Player p, int page)
    {
        PlayerLevel pl = getPlayerLevel(p.getUniqueId());
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory inv = Bukkit.createInventory(null, 54, "§8Votre niveau : " + pl.getLevel());
        SetLevelItems(main, inv, pl, page);
        p.openInventory(inv);
    }

    private static void SetLevelItems(Fireland main, Inventory inv, PlayerLevel pl, int page)
    {
        ArrayList<String> lore = new ArrayList<>();
        LevelSavings ls = GetPlayerLevelSavings(pl.getUuid());
        for (int i = 1; i < 46 && (page * 45 + i) <= 100; i++)
        {
            int pos = page *45 + i;
            if (pos % 25 == 0 || pos == 1)
            {
                if (pos <= pl.getLevel())
                {
                    if (pl.HasClaimedReward(main, pos))
                    {
                        lore.clear();
                        lore.add("§9Récompense déjà récupérée");
                        inv.setItem(pos-1 -page *45, InventoryUtilities.setItemMetaLore(Material.DIAMOND_BLOCK, "§dNiveau " + pos, lore));
                    }
                    else
                    {
                        GetLore(pl, lore, pos, "§a", main);
                        inv.setItem(pos-1 -page *45, InventoryUtilities.setItemMetaLore(Material.DIAMOND_BLOCK, "§dNiveau " + pos, lore));
                    }
                }
                else
                {
                    GetLore(pl, lore, pos, "§8", main);
                    inv.setItem(pos-1 -page *45, InventoryUtilities.setItemMetaLore(Material.COAL_BLOCK, "§7Niveau " + pos, lore));
                }
            }
            else
            {
                if (pos <= pl.getLevel())
                {
                    if (pl.HasClaimedReward(main, pos))
                    {
                        lore.clear();
                        lore.add("§9Récompense déjà récupérée");
                        inv.setItem(pos-1 -page *45, InventoryUtilities.setItemMetaLore(Material.DIAMOND, "§bNiveau " + pos, lore));
                    }
                    else
                    {
                        GetLore(pl, lore, pos, "§a", main);
                        inv.setItem(pos-1 -page *45, InventoryUtilities.setItemMetaLore(Material.DIAMOND, "§bNiveau " + pos, lore));
                    }
                }
                else
                {
                    GetLore(pl, lore, pos, "§8", main);
                    inv.setItem(pos-1 -page *45, InventoryUtilities.setItemMetaLore(Material.COAL, "§8Niveau " + pos, lore));
                }
            }
        }
        for (int i = 45; i < 54; i++)
            inv.setItem(i, InventoryUtilities.getWhiteGlassPane());
        lore.clear();
        lore.add("§8Ici pour pouvez voir l'avancée de vos niveaux.");
        lore.add("§8Cliquez sur les niveaux complétés pour recevoir votre récompense.");
        inv.setItem(45, InventoryUtilities.setItemMetaLore(Material.BOOK, "§aInformations -", lore));

        lore.clear();
        switch(pl.getNation())
        {

            case Etat -> {
                if (pl.getRang() < 4)
                {
                    lore.add("§8Votre rang : §7" + (pl.getRang() + 1));
                    lore.add("");
                    lore.add("§8Mission : Tuer des zombies");
                    lore.add("§8Progression : §7 " + ls.getZombieKills() + "/" + ls.getCurrentMaxZombieKills());
                }
                else
                {
                    lore.add("§8Votre rang : §7" + (pl.getRang() + 1));
                    lore.add("");
                    lore.add("§8Aucune tâche à accomplir.");
                }
                lore.add("");
                lore.add("§c§lCliquez ici pour changer de nation");
                lore.add("§8Prix de changement : §d " + pl.GetJetonPriceNationChange() + "§f\u26c1 §8et §7 " + pl.GetMoneyPriceNationChange() + "§f$");
                inv.setItem(46, InventoryUtilities.getEtatBanner("§7Nation: Etat", lore));
            }
            case Bannis -> {
                if (pl.getRang() < 4)
                {
                    lore.add("§8Votre rang : §7" + (pl.getRang() + 1));
                    lore.add("");
                    lore.add("§8Mission : Tuer des joueurs");
                    lore.add("§8Progression : §7 " + ls.getKills() + "/" + ls.getCurrentMaxKills());
                }
                else
                {
                    lore.add("§8Votre rang : §7" + (pl.getRang() + 1));
                    lore.add("");
                    lore.add("§8Aucune tâche à accomplir.");
                }

                lore.add("");
                lore.add("§c§lCliquez ici pour changer de nation");
                lore.add("§8Prix de changement : §d " + pl.GetJetonPriceNationChange() + "§f\u26c1 §8et §7 " + pl.GetMoneyPriceNationChange() + "§f$");
                inv.setItem(46, InventoryUtilities.getBannisBanner("§cNation: Bannis", lore));
            }
            case Neutre -> {
                lore.add("§8Aucune tâche à accomplir.");
                lore.add("");
                lore.add("§c§lCliquez ici pour changer de nation");
                lore.add("§8Prix de changement : §d " + pl.GetJetonPriceNationChange() + "§f\u26c1 §8et §7 " + pl.GetMoneyPriceNationChange() + "§f$");
                inv.setItem(46, InventoryUtilities.getNeutreBanner("§fNation: Neutre", lore));
            }
            default -> {
                inv.setItem(46, InventoryUtilities.getWhiteGlassPane());
            }
        }
        lore.clear();
        lore.add("§8Expérience : §7 " + pl.getXp() + "/" + pl.getRemainingXp());
        lore.add("§8Temps survécu : §7 " + getTimeString(main, pl.getUuid()));
        inv.setItem(49, InventoryUtilities.GetHead(pl.getUuid(), "§aNiveau : " + pl.getLevel(), lore));
        if (page <= 0)
        {
            inv.setItem(52, InventoryUtilities.setItemMeta(Material.GRAY_STAINED_GLASS_PANE, "§7[1/3]"));
        }
        else
        {
            inv.setItem(52, InventoryUtilities.setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§7[" + (page) + "/3]"));

        }

        if (page >= 2)
        {
            inv.setItem(53, InventoryUtilities.setItemMeta(Material.GRAY_STAINED_GLASS_PANE, "§7[3/3]"));
        }
        else
        {
            inv.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§7[" + (page + 2) + "/3]"));

        }
    }

    private static void GetLore(PlayerLevel pl, ArrayList<String> lore, int pos, String color, Fireland main) {
        lore.clear();
        if (color.equals("§a"))
        {
            if (pl.HasClaimedReward(main, pos))
            {
                lore.add("§9Vous avez déjà récupéré cette récompense");
            }
            else
            {
                lore.add("§a§lCliquez pour récupérer votre récompense !");
            }
        }
        if (pl.GetRewardsJetons(pos) > 0)
        {
            if (!pl.GetRewardsItems(pos).isEmpty())
            {
                lore.add(color + "Récompense : §b " + pl.GetRewardsJetons(pos) + "§f\u26c1 " + color + ", §7un item " + color + " et §7 " + pl.GetRewardsMoney(pos) + "§f$");
            }
            else
            {
                lore.add(color + "Récompense : §b " + pl.GetRewardsJetons(pos) + "§f\u26c1 " + color + " et §7 " + pl.GetRewardsMoney(pos) + "§f$");
            }
        }
        else
        {
            if (!pl.GetRewardsItems(pos).isEmpty())
            {
                lore.add(color + "Récompense : §7Un item " + color + " et §7 " + pl.GetRewardsMoney(pos) + "§f$");
            }
            else
            {
                lore.add(color + "Récompense : §7 " + pl.GetRewardsMoney(pos) + "§f$");
            }
        }
        switch (pos)
        {
            case 0:
                lore.add("§aAccès au pass vert");
                break;
            case 25:
                lore.add("§9Accès au pass bleu");
                break;
            case 50:
                lore.add("§eAccès au pass jaune");
                break;
            case 75:
                lore.add("§4Accès au pass rouge");
                break;
            default:
                break;
        }

    }

}
