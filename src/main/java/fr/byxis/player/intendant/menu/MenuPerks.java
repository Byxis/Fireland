package fr.byxis.player.intendant.menu;

import static fr.byxis.faction.faction.FactionManager.getCostPerk;

import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.faction.faction.FactionInformation;
import fr.byxis.faction.faction.FactionPlayerInformation;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.BlockUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuPerks
{

    public static void openPerks(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation infos = ff.getInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        Inventory perksInv = Bukkit.createInventory(null, 54,
                "§8Améliorations pour " + ff.getColorCode(infos.getFactionName()) + infos.getFactionName());
        setPerksItems(main, perksInv, p);
        p.openInventory(perksInv);
    }

    private static void setPerksItems(Fireland main, Inventory inventory, Player p)
    {
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation pInfos = ff.getInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        if (pInfos != null)
        {
            FactionInformation finfos = ff.getFactionInfo(pInfos.getFactionName());
            FactionInformation nextFinfos = ff.getFactionInfoWithAmeliorations(finfos.getName());
            for (int i = 0; i < 9; i++)
            {
                inventory.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            }
            if (finfos.getCurrentUpgrade() < 11)
                inventory.setItem(45,
                        InventoryUtilities.setItemMetaLore(Material.LIME_STAINED_GLASS_PANE,
                                "§aAméliorer la faction au rang §d§l " + nextFinfos.getCurrentUpgrade(), (short) 0,
                                BasicUtilities.listMaker("§8Coût: " + finfos.getCurrentMoney() + "/§6 " + finfos.getMaxMoney() + "$",
                                        "§8Maximum d'argent: §6" + nextFinfos.getMaxMoney() + "$",
                                        "§8Maximum de joueurs: " + nextFinfos.getMaxNbrOfPlayers(), "")));
            inventory.setItem(49,
                    InventoryUtilities.setItemMetaLore(BlockUtilities.getGlassPaneColor(finfos.getColorcode()),
                            finfos.getColorcode() + "Changer la couleur d'affichage de la faction", (short) 0,
                            BasicUtilities.listMaker("§8Disponible seulement pour les personnes disposant ",
                                    "§8du grade Vétéran ou Stratège.", "§8Utilisable uniquement par le Leader.", "")));
            inventory.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour au menu Faction", (short) 0));

            if (finfos.hasFriendlyFirePerk())
            {
                inventory.setItem(10, InventoryUtilities.setItemMetaLore(Material.SHIELD, "§aSupprimer le Friendly Fire... §d- Lvl. 2",
                        (short) 0,
                        BasicUtilities.listMaker("§8Empêche les joueurs de cette faction", "§8de se faire des dégâts", "§aDébloqué", "")));
            }
            else
            {
                inventory.setItem(10,
                        InventoryUtilities.setItemMetaLore(Material.SHIELD, "§cSupprimer le Friendly Fire... §d- Lvl. 2", (short) 0,
                                BasicUtilities.listMaker("§8Empêche les joueurs de cette faction", "§8de se faire des dégâts",
                                        "§8Coût: §6" + getCostPerk("friendly_fire") + "$", "")));
            }

            if (finfos.hasCapturePerk())
            {
                inventory.setItem(13,
                        InventoryUtilities.setItemMetaLore(Material.GRASS_BLOCK, "§aDébloquer la capture de zone... §d- Lvl. 2", (short) 0,
                                BasicUtilities.listMaker("§8Permet de capturer des zones", "§8dans la map.", "§aDébloqué", "")));
            }
            else
            {
                inventory.setItem(13,
                        InventoryUtilities.setItemMetaLore(Material.GRASS_BLOCK, "§cDébloquer la capture de zone... §d- Lvl. 2", (short) 0,
                                BasicUtilities.listMaker("§8Permet de capturer des zones", "§8dans la map.",
                                        "§8Coût: §6" + getCostPerk("capture_perk") + "$", "")));
            }

            if (finfos.doShowPrefix())
            {
                inventory.setItem(16,
                        InventoryUtilities.setItemMetaLore(Material.FLOWER_BANNER_PATTERN,
                                "§aDébloquer le préfixe de faction... §d- Lvl. 3", (short) 0,
                                BasicUtilities.listMaker("§8Affiche votre nom de faction", "§8dans le chat général.", "§aDébloqué", "")));
            }
            else
            {
                inventory.setItem(16,
                        InventoryUtilities.setItemMetaLore(Material.FLOWER_BANNER_PATTERN,
                                "§cDébloquer le préfixe de faction... §d- Lvl. 3", (short) 0,
                                BasicUtilities.listMaker("§8Affiche votre nom de faction", "§8dans le chat général.",
                                        "§8Coût: §6" + getCostPerk("show_prefix") + "$", "")));
            }

            if (finfos.hasSkinPerk())
            {
                inventory.setItem(28,
                        InventoryUtilities.setItemMetaLore(Material.LEATHER, "§aDébloquer les skins de faction... §d- Lvl. 4", (short) 0,
                                BasicUtilities.listMaker("§8Donne l'accès aux membres de la faction", "§8aux skins de faction.",
                                        "§aDébloqué", "")));
            }
            else
            {
                inventory.setItem(28,
                        InventoryUtilities.setItemMetaLore(Material.LEATHER, "§cDébloquer les skins de faction... §d- Lvl. 4", (short) 0,
                                BasicUtilities.listMaker("§8Donne l'accès aux membres de la faction", "§8aux skins de faction.",
                                        "§8Coût: §6" + getCostPerk("has_skin") + "$", "")));
            }

            if (finfos.hasNicknameVisibilityPerk())
            {
                inventory.setItem(31, InventoryUtilities.setItemMetaLore(Material.NAME_TAG, "§aAfficher les pseudos... §d- Lvl. 5",
                        (short) 0,
                        BasicUtilities.listMaker("§8Affiche les pseudos des joueurs", "§8qui sont dans cette faction.", "§aDébloqué", "")));
            }
            else
            {
                inventory.setItem(31,
                        InventoryUtilities.setItemMetaLore(Material.NAME_TAG, "§cAfficher les pseudos... §d- Lvl. 5", (short) 0,
                                BasicUtilities.listMaker("§8Affiche les pseudos des joueurs", "§8qui sont dans cette faction.",
                                        "§8Coût: §6" + getCostPerk("show_nickname") + "$", "")));
            }
            if (finfos.hasZoneTpPerk())
            {
                inventory.setItem(34,
                        InventoryUtilities.setItemMetaLore(Material.BEACON, "§aTP de zones... §d- Lvl. 6", (short) 0,
                                BasicUtilities.listMaker("§8Permet de se téléporter aux", "§8zones claims par la faction toutes",
                                        "§8les heures", "§aDébloqué")));
            }
            else
            {
                inventory.setItem(34,
                        InventoryUtilities.setItemMetaLore(Material.BEACON, "§cTP de zones... §d- Lvl. 6", (short) 0,
                                BasicUtilities.listMaker("§8Permet de se téléporter aux", "§8zones claims par la faction toutes",
                                        "§8les heures", "§8Coût: §620000$")));
            }
            inventory.setItem(40, InventoryUtilities.setItemMetaLore(Material.PAPER, "§cD'autres fonctionalité sont à venir...", (short) 0,
                    BasicUtilities.listMaker("", "", "", "")));

        }
    }

}
