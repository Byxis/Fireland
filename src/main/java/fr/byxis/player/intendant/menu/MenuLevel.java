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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fr.byxis.player.level.LevelStorage.getPlayerLevel;
import static fr.byxis.player.level.LevelStorage.getPlayerLevelSavings;
import static fr.byxis.player.scoreboard.ScoreboardPlayer.getTimeString;

public class MenuLevel {

    public static void openLevelMenu(Fireland main, Player p, int page)
    {
        PlayerLevel pl = getPlayerLevel(p.getUniqueId());
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory inv = Bukkit.createInventory(null, 54, "§8Votre niveau : " + pl.getLevel());
        setLevelItems(main, inv, pl, page);
        p.openInventory(inv);
    }

    private static void setLevelItems(Fireland main, Inventory inv, PlayerLevel pl, int page)
    {
        ArrayList<String> lore = new ArrayList<>();
        LevelSavings ls = getPlayerLevelSavings(pl.getUuid());
        for (int i = 1; i < 46 && (page * 45 + i) <= 100; i++)
        {
            int pos = page * 45 + i;
            if (pos % 25 == 0 || pos == 1)
            {
                if (pos <= pl.getLevel())
                {
                    if (pl.hasClaimedReward(main, pos))
                    {
                        lore.clear();
                        lore.add("§9Récompense déjà récupérée");
                        inv.setItem(pos - 1 - page * 45, InventoryUtilities.setItemMetaLore(Material.DIAMOND_BLOCK, "§dNiveau " + pos, lore));
                    }
                    else
                    {
                        getLore(pl, lore, pos, "§a", main);
                        inv.setItem(pos - 1 - page * 45, InventoryUtilities.setItemMetaLore(Material.DIAMOND_BLOCK, "§dNiveau " + pos, lore));
                    }
                }
                else
                {
                    getLore(pl, lore, pos, "§8", main);
                    inv.setItem(pos - 1 - page * 45, InventoryUtilities.setItemMetaLore(Material.COAL_BLOCK, "§7Niveau " + pos, lore));
                }
            }
            else
            {
                if (pos <= pl.getLevel())
                {
                    if (pl.hasClaimedReward(main, pos))
                    {
                        lore.clear();
                        lore.add("§9Récompense déjà récupérée");
                        inv.setItem(pos - 1 - page * 45, InventoryUtilities.setItemMetaLore(Material.DIAMOND, "§bNiveau " + pos, lore));
                    }
                    else
                    {
                        getLore(pl, lore, pos, "§a", main);
                        inv.setItem(pos - 1 - page * 45, InventoryUtilities.setItemMetaLore(Material.DIAMOND, "§bNiveau " + pos, lore));
                    }
                }
                else
                {
                    getLore(pl, lore, pos, "§8", main);
                    inv.setItem(pos - 1 - page * 45, InventoryUtilities.setItemMetaLore(Material.COAL, "§8Niveau " + pos, lore));
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
        switch (pl.getNation())
        {

            case Etat -> {
                if (pl.getRang() < 4)
                {
                    lore.add("§8Votre rang : §7" + (pl.getRang() + 1));
                    lore.add("");
                    lore.add("§8Mission : Tuer des zombies");
                    lore.add("§8Progression : §7" + ls.getZombieKills() + "/" + ls.getCurrentMaxZombieKills());
                }
                else
                {
                    lore.add("§8Votre rang : §7" + (pl.getRang() + 1));
                    lore.add("");
                    lore.add("§8Aucune tâche à accomplir.");
                }
                lore.add("");
                lore.add("§c§lCliquez ici pour changer de nation");
                lore.add("§8Prix de changement : §d" + pl.getJetonPriceNationChange() + "§f⛁ §8et §7" + pl.getMoneyPriceNationChange() + "§f$");
                inv.setItem(46, InventoryUtilities.getEtatBanner("§7Nation: Etat", lore));
            }
            case Bannis -> {
                if (pl.getRang() < 4)
                {
                    lore.add("§8Votre rang : §7" + (pl.getRang() + 1));
                    lore.add("");
                    lore.add("§8Mission : Tuer des joueurs");
                    lore.add("§8Progression : §7" + ls.getKills() + "/" + ls.getCurrentMaxKills());
                }
                else
                {
                    lore.add("§8Votre rang : §7" + (pl.getRang() + 1));
                    lore.add("");
                    lore.add("§8Aucune tâche à accomplir.");
                }

                lore.add("");
                lore.add("§c§lCliquez ici pour changer de nation");
                lore.add("§8Prix de changement : §d" + pl.getJetonPriceNationChange() + "§f⛁ §8et §7" + pl.getMoneyPriceNationChange() + "§f$");
                inv.setItem(46, InventoryUtilities.getBannisBanner("§cNation: Bannis", lore));
            }
            case Neutre -> {
                lore.add("§8Aucune tâche à accomplir.");
                lore.add("");
                lore.add("§c§lCliquez ici pour changer de nation");
                lore.add("§8Prix de changement : §d" + pl.getJetonPriceNationChange() + "§f⛁ §8et §7" + pl.getMoneyPriceNationChange() + "§f$");
                inv.setItem(46, InventoryUtilities.getNeutreBanner("§fNation: Neutre", lore));
            }
            default -> {
                inv.setItem(46, InventoryUtilities.getWhiteGlassPane());
            }
        }
        lore.clear();
        lore.add("§8Expérience : §7" + pl.getXp() + "/" + pl.getRemainingXp());
        lore.add("§8Temps survécu : §7" + getTimeString(main, pl.getUuid()));
        inv.setItem(49, InventoryUtilities.getHead(pl.getUuid(), "§aNiveau : " + pl.getLevel(), lore));
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

    /**
     * Generates the lore for a level item in the menu.
     *
     * @param _playerLevel The player's level data
     * @param _lore The list to populate with lore lines
     * @param _level The level number
     * @param _color The color code to use for the text
     * @param _main The main plugin instance
     */
    private static void getLore(PlayerLevel _playerLevel, ArrayList<String> _lore, int _level, String _color, Fireland _main)
    {
        _lore.clear();

        addClaimStatusLore(_playerLevel, _lore, _level, _color, _main);
        addRewardsLore(_playerLevel, _lore, _level, _color);
        addPassAccessLore(_lore, _level);
    }

    /**
     * Adds the claim status message to the lore.
     *
     * @param _playerLevel The player's level data
     * @param _lore The list to populate with lore lines
     * @param _level The level number
     * @param _color The color code to use for the text
     * @param _main The main plugin instance
     */
    private static void addClaimStatusLore(PlayerLevel _playerLevel, ArrayList<String> _lore, int _level, String _color, Fireland _main)
    {
        if (_color.equals("§a"))
        {
            if (_playerLevel.hasClaimedReward(_main, _level))
            {
                _lore.add("§9Vous avez déjà récupéré cette récompense");
            }
            else
            {
                _lore.add("§a§lCliquez pour récupérer votre récompense !");
            }
        }
    }

    /**
     * Adds the rewards information to the lore.
     *
     * @param _playerLevel The player's level data
     * @param _lore The list to populate with lore lines
     * @param _level The level number
     * @param _color The color code to use for the text
     */
    private static void addRewardsLore(PlayerLevel _playerLevel, ArrayList<String> _lore, int _level, String _color)
    {
        int jetons = _playerLevel.getRewardsJetons(_level);
        int money = _playerLevel.getRewardsMoney(_level);
        boolean hasItems = !_playerLevel.getRewardsItems(_level).isEmpty();
        String hasPet = _playerLevel.getRewardsPetsFormatted(_level);

        List<String> rewardParts = new ArrayList<>();

        if (jetons > 0)
        {
            rewardParts.add("§b" + jetons + "§f⛁");
        }

        if (hasItems)
        {
            rewardParts.add("§7un item");
        }

        if (money > 0)
        {
            rewardParts.add("§7" + money + "§f$");
        }

        if (!hasPet.isEmpty())
        {
            rewardParts.add(hasPet);
        }

        if (!rewardParts.isEmpty())
        {
            StringBuilder rewards = new StringBuilder();
            for (int i = 0; i < rewardParts.size(); i++)
            {
                if (i > 0)
                {
                    if (i == rewardParts.size() - 1)
                    {
                        rewards.append(_color).append(" et ");
                    }
                    else
                    {
                        rewards.append(_color).append(", ");
                    }
                }
                rewards.append(rewardParts.get(i));
            }
            _lore.add(_color + "Récompense : " + rewards);
        }
    }

    /**
     * Adds the pass access information to the lore for milestone levels.
     *
     * @param _lore The list to populate with lore lines
     * @param _level The level number
     */
    private static void addPassAccessLore(ArrayList<String> _lore, int _level)
    {
        String passAccess = switch (_level)
        {
            case 0 -> "§aAccès au pass vert";
            case 25 -> "§9Accès au pass bleu";
            case 50 -> "§eAccès au pass jaune";
            case 75 -> "§4Accès au pass rouge";
            default -> null;
        };

        if (passAccess != null)
        {
            _lore.add(passAccess);
        }
    }

}
