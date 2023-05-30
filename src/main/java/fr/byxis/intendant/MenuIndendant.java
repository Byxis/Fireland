package fr.byxis.intendant;

import fr.byxis.jeton.jetonsCommandManager;
import fr.byxis.booster.BoosterClass;
import fr.byxis.faction.FactionFunctions;
import fr.byxis.faction.FactionInformation;
import fr.byxis.faction.FactionPlayerInformation;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.BlockUtilities;
import fr.byxis.fireland.utilities.ItemUtilities;
import fr.byxis.zone.zoneclass.FactionZoneInformation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MenuIndendant implements Listener {

    private final Fireland main;
    public MenuIndendant(Fireland main) {
        this.main = main;
    }

    @EventHandler
    public void ClickInventoryEvent(InventoryClickEvent e)
    {
        InventoryView inv = e.getView();
        if(e.getView().getPlayer() instanceof Player p)
        {
            if(inv.getTitle().contains("Intendant"))
            {
                /**       Click check        **/
                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }
                if (e.getClickedInventory() == e.getView().getTopInventory() || e.getClick().isKeyboardClick()) {
                    e.setCancelled(true);
                }
                else
                {
                    if(e.isShiftClick())
                    {
                        e.setCancelled(true);
                    }
                }
                /**       Click check        **/


                switch(itemclicked.getType())
                {
                    case EMERALD -> main.commandExecutor(p, "ah", "crazyauctions.access");
                    case DIAMOND_SWORD -> OpenFaction(p, true);
                    case FIREWORK_ROCKET -> OpenBoosters(p);
                }
            }
            else if(inv.getTitle().contains("Votre faction"))
            {
                /**       Click check        **/
                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }
                if (e.getClickedInventory() == e.getView().getTopInventory() || e.getClick().isKeyboardClick()) {
                    e.setCancelled(true);
                }
                else
                {
                    if(e.isShiftClick())
                    {
                        e.setCancelled(true);
                    }
                }
                /**       Click check        **/
                FactionFunctions ff = new FactionFunctions(main, p);
                FactionPlayerInformation infos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
                FactionInformation finfos = ff.getFactionInfo(infos.getFactionName());
                switch(itemclicked.getType())
                {
                    case RED_STAINED_GLASS_PANE :
                        OpenIntendant(p);
                        break;
                    case ANVIL:
                        OpenPerks(p);
                        break;
                    case PLAYER_HEAD:
                        OpenPlayerList(p);
                        break;
                    case BARRIER :
                        main.commandExecutor(p, "faction leave", "fireland.command.faction.leave");
                        p.closeInventory();
                        break;
                    case ENDER_CHEST:
                        if(ff.GetAmeliorationsUpgrades(finfos.getCurrentUpgrade())[2] != 0)
                        {
                            p.openInventory(ff.LoadStorage(infos.getFactionName(), finfos.getCurrentUpgrade()));
                        }
                        break;
                    case GOLD_INGOT :
                        if(e.isRightClick() && infos.getRole() == 2)
                        {
                            if(e.isShiftClick())
                            {
                                main.commandExecutor(p, "faction withdraw 1000", "fireland.command.faction.withdraw");
                            }
                            else
                            {
                                main.commandExecutor(p, "faction withdraw 100", "fireland.command.faction.withdraw");
                            }
                        }
                        else if(e.isLeftClick())
                        {
                            if(e.isShiftClick())
                            {
                                main.commandExecutor(p, "faction deposit 1000", "fireland.command.faction.deposit");
                            }
                            else
                            {
                                main.commandExecutor(p, "faction deposit 100", "fireland.command.faction.deposit");
                            }
                        }
                        OpenFaction(p, true);
                        break;
                }
            }
            else if(inv.getTitle().contains("Membres de "))
            {
                /**       Click check        **/
                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }
                if (e.getClickedInventory() == e.getView().getTopInventory() || e.getClick().isKeyboardClick()) {
                    e.setCancelled(true);
                }
                else
                {
                    if(e.isShiftClick())
                    {
                        e.setCancelled(true);
                    }
                }
                /**       Click check        **/


                switch(itemclicked.getType())
                {
                    case RED_STAINED_GLASS_PANE -> OpenFaction(p, true);
                }
            }
            else if(inv.getTitle().contains("Améliorations pour"))
            {
                /**       Click check        **/
                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }
                if (e.getClickedInventory() == e.getView().getTopInventory() || e.getClick().isKeyboardClick()) {
                    e.setCancelled(true);
                }
                else
                {
                    if(e.isShiftClick())
                    {
                        e.setCancelled(true);
                    }
                }
                /**       Click check        **/
                FactionFunctions ff = new FactionFunctions(main, p);
                FactionPlayerInformation pInfos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
                FactionInformation finfos = ff.getFactionInfo(pInfos.getFactionName());
                final Material color = BlockUtilities.getGlassPaneColor(finfos.getColorcode());

                switch(itemclicked.getType())
                {
                    case RED_STAINED_GLASS_PANE -> OpenFaction(p, true);
                    case LIME_STAINED_GLASS_PANE -> main.commandExecutor(p, "faction upgrade", "fireland.command.faction.upgrade");
                    case SHIELD -> {
                        if(!finfos.hasFriendlyFirePerk())
                        {
                            if(finfos.getCurrentUpgrade() < 2)
                            {
                                BasicUtilities.sendPlayerError(p, "Vous devez être niveau de faction 2 pour débloquer cette amélioration.");
                            }
                            else
                            {
                                main.commandExecutor(p, "faction perk friendly_fire", "fireland.command.faction.perk");
                                OpenPerks(p);
                            }

                        }
                        return;
                    }
                    case NAME_TAG -> {
                        if(!finfos.hasNicknameVisibilityPerk())
                        {
                            if(finfos.getCurrentUpgrade() < 5)
                            {
                                BasicUtilities.sendPlayerError(p, "Vous devez être niveau de faction 5 pour débloquer cette amélioration.");
                            }
                            else
                            {
                                main.commandExecutor(p, "faction perk show_nickname", "fireland.command.faction.perk");
                                OpenPerks(p);
                            }

                        }
                        return;
                    }
                    case LEATHER -> {
                        if(!finfos.hasSkinPerk())
                        {
                            if(finfos.getCurrentUpgrade() < 4)
                            {
                                BasicUtilities.sendPlayerError(p, "Vous devez être niveau de faction 4 pour débloquer cette amélioration.");
                            }
                            else
                            {
                                main.commandExecutor(p, "faction perk has_skin", "fireland.command.faction.perk");
                                OpenPerks(p);
                            }

                        }
                        return;
                    }
                    case FLOWER_BANNER_PATTERN -> {
                        if(!finfos.DoShowPrefix())
                        {
                            if(finfos.getCurrentUpgrade() < 3)
                            {
                                BasicUtilities.sendPlayerError(p, "Vous devez être niveau de faction 3 pour débloquer cette amélioration.");
                            }
                            else
                            {
                                main.commandExecutor(p, "faction perk show_prefix", "fireland.command.faction.perk");
                                OpenPerks(p);
                            }

                        }
                        return;
                    }
                    case GRASS_BLOCK -> {
                        if(!finfos.hasCapturePerk())
                        {
                            if(finfos.getCurrentUpgrade() < 2)
                            {
                                BasicUtilities.sendPlayerError(p, "Vous devez être niveau de faction 3 pour débloquer cette amélioration.");
                            }
                            else
                            {
                                main.commandExecutor(p, "faction perk capture_perk", "fireland.command.faction.perk");
                                OpenPerks(p);
                            }

                        }
                        return;
                    }
                }
                if(itemclicked.getType() == color && p.hasPermission("fireland.command.faction.color") && !itemclicked.getItemMeta().getDisplayName().contains("Améliorer la faction au rang") && pInfos.getRole() == 2)
                {
                    OpenColorMenu(p, finfos);
                }
            }
            else if(inv.getTitle().contains("Changement de couleur"))
            {
                /**       Click check        **/
                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }
                if (e.getClickedInventory() == e.getView().getTopInventory() || e.getClick().isKeyboardClick()) {
                    e.setCancelled(true);
                }
                else
                {
                    if(e.isShiftClick())
                    {
                        e.setCancelled(true);
                    }
                }
                /**       Click check        **/


                if(itemclicked.getType() == Material.RED_STAINED_GLASS_PANE && itemclicked.getItemMeta().getDisplayName().contains("Retour à l'intendant"))
                {
                    OpenPerks(p);
                }
                else if(itemclicked.getType() != Material.WHITE_STAINED_GLASS_PANE)
                {
                    FactionFunctions ff = new FactionFunctions(main, p);
                    FactionPlayerInformation pInfos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
                    FactionInformation finfos = ff.getFactionInfo(pInfos.getFactionName());
                    ff.SetColorCode(pInfos.getFactionName(), GetStringColor(itemclicked.getType()));
                    OpenColorMenu(p, finfos);
                }
            }
            else if(inv.getTitle().contains("Boosters"))
            {
                /**       Click check        **/
                ItemStack itemclicked = e.getCurrentItem();
                if (itemclicked == null) {
                    return;
                }
                if (e.getClickedInventory() == e.getView().getTopInventory() || e.getClick().isKeyboardClick()) {
                    e.setCancelled(true);
                }
                else
                {
                    if(e.isShiftClick())
                    {
                        e.setCancelled(true);
                    }
                }
                /**       Click check        **/


                if(itemclicked.getType() == Material.RED_STAINED_GLASS_PANE && itemclicked.getItemMeta().getDisplayName().contains("Retour à l'intendant"))
                {
                    OpenIntendant(p);
                }
                else if(itemclicked.getType() == Material.FIREWORK_ROCKET)
                {
                    ItemMeta meta = itemclicked.getItemMeta();
                    int duration = 1;
                    if(meta.getDisplayName().contains("3h"))
                    {
                        duration = 3;
                    }
                    else if(meta.getDisplayName().contains("5h"))
                    {
                        duration = 5;
                    }
                    int level = 1;
                    if(meta.getDisplayName().contains("Lvl. 2"))
                    {
                        level = 2;
                    }
                    else if(meta.getDisplayName().contains("Lvl. 3"))
                    {
                        level = 3;
                    }
                    main.commandExecutor(p, "booster create "+level+" "+duration, "fireland.command.booster");
                    OpenBoosters(p);
                }
            }
        }
    }

    public void OpenIntendant(Player p)
    {
        Inventory craftMenu = Bukkit.createInventory(null, 27, "§8Intendant");
        SetIntendantItems(craftMenu, p);
        p.openInventory(craftMenu);
    }

    private void SetIntendantItems(Inventory craftMenu, Player p)
    {
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation infos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        if(infos == null)
        {
            craftMenu.setItem(12, ItemUtilities.setItemMeta(Material.FIREWORK_ROCKET, "§eBoosters", (short) 0));
            craftMenu.setItem(14, ItemUtilities.setItemMeta(Material.EMERALD, "§aHôtel des ventes", (short) 0));
        }
        else
        {
            craftMenu.setItem(11, ItemUtilities.setItemMeta(Material.FIREWORK_ROCKET, "§eBoosters", (short) 0));
            craftMenu.setItem(13, ItemUtilities.setItemMeta(Material.EMERALD, "§aHôtel des ventes", (short) 0));
            craftMenu.setItem(15, ItemUtilities.setItemMeta(Material.DIAMOND_SWORD, "§aFactions", (short) 0));
        }

    }

    public void OpenFaction(Player p, boolean canReturn)
    {
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation infos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        Inventory faction = Bukkit.createInventory(null, 54, "§8Votre faction: "+ff.GetColorCode(infos.getFactionName())+infos.getFactionName());
        SetFactionItems(faction, p, canReturn);
        p.openInventory(faction);
    }

    private void SetFactionItems(Inventory inventory, Player p, boolean canReturn)
    {
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation pInfos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        FactionInformation finfos = ff.getFactionInfo(pInfos.getFactionName());
        if(pInfos != null && finfos != null)
        {
            for(int i=0;i<9;i++) {
                inventory.setItem(i, ItemUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
                inventory.setItem(i + 45, ItemUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
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
            inventory.setItem(8, ItemUtilities.setItemMeta(Material.BARRIER, "§4§lQuitter la faction", (short) 0));
            inventory.setItem(22, ItemUtilities.setItemMetaLore(Material.ENDER_CHEST, "§aStockage - "+finfos.getCurrentChestSize()+" slots", (short) 0, listMaker("§8- Faites un §dclic gauche§8 pour ouvrir votre stockage","","","")));
            inventory.setItem(26, GetHead(finfos.getLeader(), "§7Leader: "+Bukkit.getOfflinePlayer(finfos.getLeader()).getName()));
            inventory.setItem(30, ItemUtilities.setItemMetaLore(Material.GOLD_INGOT, "§aArgent - §6"+finfos.getCurrentMoney()+"/"+finfos.getMaxMoney(), (short) 0, listMaker("§8- Faites un §dclic gauche §8pour ajouter §6100$","§8à la faction (shift pour 1000$)", "§8- §c(Leader)§8 Faites un §dclic droit §8pour retirer §6100$","§8de la faction (shift pour 1000$)")));
            inventory.setItem(32, ItemUtilities.setItemMetaLore(Material.GRASS_BLOCK, "§aTerritoires claims -", (short) 0, listMaker("§cNon disponible pour le moment", "","","")));
            inventory.setItem(35, ItemUtilities.setItemMetaLore(Material.ANVIL, "§aAméliorations -", (short) 0, listMaker("§8Accédez aux améliorations de la faction !","§cSeul le leader peut acheter des améliorations !","","")));
            inventory.setItem(45, ItemUtilities.setItemMetaLore(Material.BOOK, "§7Vous êtes "+role+"§7.", (short) 0, listMaker("§8Date de création: "+finfos.getCreatedAt(),"","","")));
            if(canReturn)
            {
                inventory.setItem(53, ItemUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour à l'intendant", (short) 0));
            }
        }

    }

    private ItemStack GetHead(UUID uuid, String name)
    {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        headMeta.setDisplayName(name);
        head.setItemMeta(headMeta);
        return head;
    }
    private List<String> listMaker(String str1, String str2, String str3, String str4){
        List<String> lore = new ArrayList<String>();
        if(str1 != "") lore.add(str1);
        if(str2 != "") lore.add(str2);
        if(str3 != "")lore.add(str3);
        if(str4 != "")lore.add(str4);
        return lore;
    }

    private void OpenPlayerList(Player p)
    {
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation infos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        Inventory PlayerList = Bukkit.createInventory(null, 54, "§8Membres de "+ff.GetColorCode(infos.getFactionName())+infos.getFactionName());
        SetPlayerListItems(PlayerList, p);
        p.openInventory(PlayerList);
    }

    private void SetPlayerListItems(Inventory inventory, Player p)
    {
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation pInfos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        if(pInfos != null)
        {
            ArrayList<FactionPlayerInformation> infos = ff.getPlayersFromFaction(pInfos.getFactionName());

            for(int i=0;i<9;i++) {
                inventory.setItem(i + 45, ItemUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            }
            inventory.setItem(53, ItemUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour à l'intendant", (short) 0));
            int member = 9;
            String you = "";
            String nouveau = "";
            for(FactionPlayerInformation info :infos)
            {
                LocalDate lDate = info.getJoinDate().toLocalDateTime().toLocalDate();

                java.time.Period prd = java.time.Period.between(lDate, java.time.LocalDate.now());
                String connectionInformation = "";
                if(Bukkit.getOfflinePlayer(info.getUuid()).isOnline())
                {
                    connectionInformation = "§b\u23FA §r";
                }
                else
                {
                    connectionInformation = "§8\u2B58 §r";
                }
                if(info.getName().equalsIgnoreCase(p.getName()))
                {
                    you = " §d(Vous)";
                }
                if(prd.getDays() <=7)
                {
                    nouveau = " §1(Nouveau)";
                }
                if(info.getRole() == 2)
                {
                    inventory.setItem(0, GetHead(info.getUuid(), connectionInformation+"§cLeader: "+Bukkit.getOfflinePlayer(info.getUuid()).getName()+you));
                }
                if(info.getRole() == 1)
                {

                    inventory.setItem(member, GetHead(info.getUuid(), connectionInformation+"§eModérateur: "+Bukkit.getOfflinePlayer(info.getUuid()).getName()+you+nouveau));
                    member++;
                }
                if(info.getRole() == 0)
                {
                    inventory.setItem(member, GetHead(info.getUuid(), connectionInformation+"§aMembre: "+Bukkit.getOfflinePlayer(info.getUuid()).getName()+you+nouveau));
                    member++;
                }
                you = "";
                nouveau = "";
            }
        }
    }

    private void OpenPerks(Player p)
    {
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation infos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        Inventory perksInv = Bukkit.createInventory(null, 54, "§8Améliorations pour "+ff.GetColorCode(infos.getFactionName())+infos.getFactionName());
        SetPerksItems(perksInv, p);
        p.openInventory(perksInv);
    }

    private void SetPerksItems(Inventory inventory, Player p)
    {
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation pInfos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        if(pInfos != null)
        {
            FactionInformation finfos = ff.getFactionInfo(pInfos.getFactionName());
            FactionInformation nextFinfos = ff.getFactionInfoWithAmeliorations(finfos.getName());
            for(int i=0;i<9;i++) {
                inventory.setItem(i + 45, ItemUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            }
            inventory.setItem(45, ItemUtilities.setItemMetaLore(Material.LIME_STAINED_GLASS_PANE, "§aAméliorer la faction au rang §d§l"+nextFinfos.getCurrentUpgrade(), (short) 0, listMaker("§8Coût: "+finfos.getCurrentMoney()+"/§6"+finfos.getMaxMoney()+"$", "§8Maximum d'argent: §6"+nextFinfos.getMaxMoney()+"$", "§8Maximum dans la banque: "+nextFinfos.getCurrentChestSize(), "§8Maximum de joueurs: "+nextFinfos.getMaxNbrOfPlayers())));
            inventory.setItem(49, ItemUtilities.setItemMetaLore(BlockUtilities.getGlassPaneColor(finfos.getColorcode()), finfos.getColorcode()+"Changer la couleur d'affichage de la faction", (short) 0, listMaker("§8Disponible seulement pour les personnes disposant ", "§8du grade Vétérant ou Stratège.", "§8Utilisable uniquement par le Leader.", "")));
            inventory.setItem(53, ItemUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour au menu Faction", (short) 0));

            if(finfos.hasFriendlyFirePerk())
            {
                inventory.setItem(10, ItemUtilities.setItemMetaLore(Material.SHIELD, "§aSupprimer le Friendly Fire... §d- Lvl. 2", (short)0, listMaker("§8Empêche les joueurs de cette faction", "§8de se faire des dégâts", "§aDébloqué", "")));
            }
            else
            {
                inventory.setItem(10, ItemUtilities.setItemMetaLore(Material.SHIELD, "§cSupprimer le Friendly Fire... §d- Lvl. 2", (short)0, listMaker("§8Empêche les joueurs de cette faction", "§8de se faire des dégâts", "§8Coût: §68000$", "")));
            }

            if(finfos.hasCapturePerk())
            {
                inventory.setItem(13, ItemUtilities.setItemMetaLore(Material.GRASS_BLOCK, "§aDébloquer la capture de zone... §d- Lvl. 2", (short)0, listMaker("§8Permet de capturer des zones", "§8dans la map.", "§aDébloqué", "")));
            }
            else
            {
                inventory.setItem(13, ItemUtilities.setItemMetaLore(Material.GRASS_BLOCK, "§cDébloquer la capture de zone... §d- Lvl. 2", (short)0, listMaker("§8Permet de capturer des zones", "§8dans la map.", "§8Coût: §65000$", "")));
            }

            if(finfos.DoShowPrefix())
            {
                inventory.setItem(16, ItemUtilities.setItemMetaLore(Material.FLOWER_BANNER_PATTERN, "§aDébloquer le préfixe de faction... §d- Lvl. 3", (short)0, listMaker("§8Affiche votre nom de faction", "§8dans le chat général.", "§aDébloqué", "")));
            }
            else
            {
                inventory.setItem(16, ItemUtilities.setItemMetaLore(Material.FLOWER_BANNER_PATTERN, "§cDébloquer le préfixe de faction... §d- Lvl. 3", (short)0, listMaker("§8Affiche votre nom de faction", "§8dans le chat général.", "§8Coût: §66000$", "")));
            }

            if(finfos.hasSkinPerk())
            {
                inventory.setItem(28, ItemUtilities.setItemMetaLore(Material.LEATHER, "§aDébloquer les skins de faction... §d- Lvl. 4", (short)0, listMaker("§8Donne l'accès aux membres de la faction", "§8aux skins de faction.", "§aDébloqué", "")));
            }
            else
            {
                inventory.setItem(28, ItemUtilities.setItemMetaLore(Material.LEATHER, "§cDébloquer les skins de faction... §d- Lvl. 4", (short)0, listMaker("§8Donne l'accès aux membres de la faction", "§8aux skins de faction.", "§8Coût: §612000$", "")));
            }

            if(finfos.hasNicknameVisibilityPerk())
            {
                inventory.setItem(31, ItemUtilities.setItemMetaLore(Material.NAME_TAG, "§aAfficher les pseudos... §d- Lvl. 5", (short)0, listMaker("§8Affiche les pseudos des joueurs", "§8qui sont dans cette faction.", "§aDébloqué", "")));
            }
            else
            {
                inventory.setItem(31, ItemUtilities.setItemMetaLore(Material.NAME_TAG, "§cAfficher les pseudos... §d- Lvl. 5", (short)0, listMaker("§8Affiche les pseudos des joueurs", "§8qui sont dans cette faction.", "§8Coût: §69000$", "§cEn développement...")));
            }
            inventory.setItem(34, ItemUtilities.setItemMetaLore(Material.PAPER, "§cFonctionalité à venir...", (short)0, listMaker("", "", "", "")));

        }
    }

    private void OpenColorMenu(Player p, FactionInformation infos)
    {
        FactionFunctions ff = new FactionFunctions(main, p);
        Inventory faction = Bukkit.createInventory(null, 54, ff.GetColorCode(infos.getName())+"Changement de couleur");
        SetItemColorMenu(faction, p, infos);
        p.openInventory(faction);
    }

    private void SetItemColorMenu(Inventory inv, Player p, FactionInformation infos)
    {
        for(int i=0;i<9;i++) {
            inv.setItem(i, ItemUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            inv.setItem(i + 45, ItemUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        inv.setItem(19, ItemUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§4"+infos.getName(), (short) 0));
        inv.setItem(20, ItemUtilities.setItemMeta(Material.ORANGE_STAINED_GLASS_PANE, "§6"+infos.getName(), (short) 0));
        inv.setItem(21, ItemUtilities.setItemMeta(Material.YELLOW_STAINED_GLASS_PANE, "§e"+infos.getName(), (short) 0));
        inv.setItem(22, ItemUtilities.setItemMeta(Material.LIME_STAINED_GLASS_PANE, "§a"+infos.getName(), (short) 0));
        inv.setItem(23, ItemUtilities.setItemMeta(Material.GREEN_STAINED_GLASS_PANE, "§2"+infos.getName(), (short) 0));
        inv.setItem(24, ItemUtilities.setItemMeta(Material.CYAN_STAINED_GLASS_PANE, "§3"+infos.getName(), (short) 0));
        inv.setItem(25, ItemUtilities.setItemMeta(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "§b"+infos.getName(), (short) 0));
        inv.setItem(34, ItemUtilities.setItemMeta(Material.BLUE_STAINED_GLASS_PANE, "§1"+infos.getName(), (short) 0));
        inv.setItem(33, ItemUtilities.setItemMeta(Material.PURPLE_STAINED_GLASS_PANE, "§5"+infos.getName(), (short) 0));
        inv.setItem(32, ItemUtilities.setItemMeta(Material.MAGENTA_STAINED_GLASS_PANE, "§d"+infos.getName(), (short) 0));
        inv.setItem(31, ItemUtilities.setItemMeta(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "§7"+infos.getName(), (short) 0));
        inv.setItem(30, ItemUtilities.setItemMeta(Material.GRAY_STAINED_GLASS_PANE, "§8"+infos.getName(), (short) 0));
        inv.setItem(29, ItemUtilities.setItemMeta(Material.BLACK_STAINED_GLASS_PANE, "§0"+infos.getName(), (short) 0));
        inv.setItem(53, ItemUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour à l'intendant", (short) 0));
    }

    private String GetStringColor(Material mat)
    {
        switch (mat)
        {
            case BLACK_STAINED_GLASS_PANE -> {
                return "§0";
            }
            case BLUE_STAINED_GLASS_PANE -> {
                return "§1";
            }
            case GREEN_STAINED_GLASS_PANE -> {
                return "§2";
            }
            case CYAN_STAINED_GLASS_PANE -> {
                return "§3";
            }
            case RED_STAINED_GLASS_PANE -> {
                return "§4";
            }
            case PURPLE_STAINED_GLASS_PANE -> {
                return "§5";
            }
            case ORANGE_STAINED_GLASS_PANE -> {
                return "§6";
            }
            case LIGHT_GRAY_STAINED_GLASS_PANE -> {
                return "§7";
            }
            case GRAY_STAINED_GLASS_PANE -> {
                return "§8";
            }
            case LIME_STAINED_GLASS_PANE -> {
                return "§a";
            }
            case LIGHT_BLUE_STAINED_GLASS_PANE -> {
                return "§b";
            }
            case MAGENTA_STAINED_GLASS_PANE -> {
                return "§d";
            }
            case YELLOW_STAINED_GLASS_PANE -> {
                return "§e";
            }
        }
        return "§7";

    }

    private void OpenBoosters(Player p)
    {
        Inventory boosterInv = Bukkit.createInventory(null, 54, "§8Boosters");
        SetBoostersItem(boosterInv, p);
        p.openInventory(boosterInv);
    }

    private void SetBoostersItem(Inventory inv, Player p)
    {
        for(int i=0;i<9;i++) {
            inv.setItem(i + 45, ItemUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        jetonsCommandManager jeton = new jetonsCommandManager(main);

        if(main.hashMapManager.getBooster() != null)
        {
            BoosterClass booster = main.hashMapManager.getBooster();
            inv.setItem(0, ItemUtilities.setItemMetaLore(Material.LIME_WOOL, "§a§lUn Booster est actif !", (short) 0, listMaker("§8Créé par "+((Player)Bukkit.getOfflinePlayer(booster.getUuid())).getName(), "§8Expiration dans "+main.getStringTime(booster.getFinished().getTime()-System.currentTimeMillis()), "", "")));
        }
        else
        {
            inv.setItem(0, ItemUtilities.setItemMetaLore(Material.RED_WOOL, "§cAucun Booster n'est actif.", (short) 0, listMaker("", "", "", "")));
        }
        ItemStack head = GetHead(p.getUniqueId(), "§d"+p.getName());
        ItemMeta meta = head.getItemMeta();
        meta.setLore(listMaker("§8Jetons : §b"+jeton.getJetonsPlayer(p.getUniqueId())+ " \u26c1" , "", "", ""));
        head.setItemMeta(meta);
        inv.setItem(8, head);

        inv.setItem(11, ItemUtilities.setItemMetaLore(Material.PAPER, "§eBooster - Lvl. 1", (short) 0, listMaker("§8Permet de gagner 0 à 1$ sur les zombies", "§8Donne 5% plus d'argent lors du kill", "§8Donne 5% plus de loot dans les coffres.", "")));
        inv.setItem(13, ItemUtilities.setItemMetaLore(Material.PAPER, "§eBooster - Lvl. 2", (short) 0, listMaker("§8Permet de gagner 0 à 2$ sur les zombies", "§8Donne 7.5% plus d'argent lors du kill", "§8Donne 7.5% plus de loot dans les coffres.", "")));
        inv.setItem(15, ItemUtilities.setItemMetaLore(Material.PAPER, "§eBooster - Lvl. 3", (short) 0, listMaker("§8Permet de gagner 0 à 3$ sur les zombies", "§8Donne 10% plus d'argent lors du kill", "§8Donne 10% plus de loot dans les coffres.", "")));

        inv.setItem(20, ItemUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 1 - 1h", (short) 0, listMaker("§8Avantages: voir ci-dessus","§8Coût : §b100 \u26c1","", "")));
        inv.setItem(29, ItemUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 1 - 3h", (short) 0, listMaker("§8Avantages: voir ci-dessus","§8Coût : §b300 \u26c1","", "")));
        inv.setItem(38, ItemUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 1 - 5h", (short) 0, listMaker("§8Avantages: voir ci-dessus","§8Coût : §b500 \u26c1","", "")));

        inv.setItem(22, ItemUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 2 - 1h", (short) 0, listMaker("§8Avantages: voir ci-dessus","§8Coût : §b200 \u26c1","", "")));
        inv.setItem(31, ItemUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 2 - 3h", (short) 0, listMaker("§8Avantages: voir ci-dessus","§8Coût : §b600 \u26c1","", "")));
        inv.setItem(40, ItemUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 2 - 5h", (short) 0, listMaker("§8Avantages: voir ci-dessus","§8Coût : §b1000 \u26c1","", "")));

        inv.setItem(24, ItemUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 3 - 1h", (short) 0, listMaker("§8Avantages: voir ci-dessus","§8Coût : §b300 \u26c1","", "")));
        inv.setItem(33, ItemUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 3 - 3h", (short) 0, listMaker("§8Avantages: voir ci-dessus","§8Coût : §b900 \u26c1","", "")));
        inv.setItem(42, ItemUtilities.setItemMetaLore(Material.FIREWORK_ROCKET, "§eBooster - Lvl. 3 - 5h", (short) 0, listMaker("§8Avantages: voir ci-dessus","§8Coût : §b1500 \u26c1","", "")));

        inv.setItem(53, ItemUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour à l'intendant", (short) 0));
    }

    private void OpenZone(Player p)
    {
        Inventory zoneInv = Bukkit.createInventory(null, 54, "§8Zones");
        SetZoneItem(zoneInv, p);
        p.openInventory(zoneInv);
    }

    private void SetZoneItem(Inventory inv, Player p)
    {
        for(int i=0;i<9;i++)
        {
            inv.setItem(i + 45, ItemUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        FactionFunctions ff = new FactionFunctions(main, p);
        FactionPlayerInformation pInfos = ff.GetInformationOfPlayerInAFaction(p.getUniqueId(), p.getName());
        List<FactionZoneInformation> list = main.zoneManager.GetFactionData(pInfos.getFactionName());
        int i = 10;
        for(FactionZoneInformation factionZoneInformation : list)
        {
            if(i == 17 ||i == 17+9 || i == 17+18)
            {
                List<String> lore = new ArrayList<>();
                lore.add("§8Date de capture: §a"+factionZoneInformation.claimedAt.toString());
                lore.add("§8Total cumulé: §a"+factionZoneInformation.totalDuration);
                inv.setItem(53, ItemUtilities.setItemMetaLore(Material.WHITE_BANNER, "§a"+factionZoneInformation.zoneName, (short) 0, lore));
            }
            i++;
        }

        inv.setItem(53, ItemUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour au menu Faction", (short) 0));
    }

}
