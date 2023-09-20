package fr.byxis.player.intendant.menu;

import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.faction.faction.FactionInformation;
import fr.byxis.faction.faction.FactionPlayerInformation;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.BlockUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.faction.zone.zoneclass.FactionZoneInformation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MenuZone {

    public static void OpenZone(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory zoneInv = Bukkit.createInventory(null, 54, "§8Zones");
        SetZoneItem(main, zoneInv, p);
        p.openInventory(zoneInv);
    }

    private static void SetZoneItem(Fireland main, Inventory inv, Player p)
    {
        for(int i=0;i<9;i++)
        {
            inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation pInfos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        List<FactionZoneInformation> list = main.zoneManager.GetFactionData(pInfos.getFactionName());
        int i = 10;
        for(FactionZoneInformation factionZoneInformation : list)
        {
            List<String> lore = new ArrayList<>();
            String color = ff.getFactionInfo(factionZoneInformation.getFactionName()).getColorcode();
            if(factionZoneInformation.getClaimedAt()!= null)
            {
                FactionInformation finfos = ff.getFactionInfo(pInfos.getFactionName());
                if(finfos.hasZoneTpPerk())
                {
                    lore.add("§6Cliquez ici pour vous téléporter !");
                }
                lore.add("§8La zone sera contestable le : §a"+ new Timestamp((long) (factionZoneInformation.getClaimedAt().getTime() + main.zoneManager.data.configManager.config.getDouble("zone."+factionZoneInformation.getZoneName()+".privation-duration")*1000*3600)));
                lore.add("§8Génère §6"+main.zoneManager.data.configManager.config.getInt("zone."+factionZoneInformation.getZoneName()+".daily-gain")+"§r§f$§8 par jour.");

                inv.setItem(i, InventoryUtilities.setItemMetaLore(BlockUtilities.getBannerColor(color), "§a"+ factionZoneInformation.getFormattedName(), (short) 0, lore));

            }
            else
            {
                lore.add("§8Zone non capturée");
                lore.add("§8Total cumulé: §7"+BasicUtilities.getStringTime(factionZoneInformation.getTotalDuration()));
                inv.setItem(i, InventoryUtilities.setItemMetaLore(Material.LIGHT_GRAY_BANNER, "§7"+ factionZoneInformation.getFormattedName(), (short) 0, lore));
            }

            if(i == 17 ||i == 17+9 || i == 17+18)
            {
                i++;
            }
            i++;
        }

        inv.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour au menu Faction", (short) 0));
    }

}
