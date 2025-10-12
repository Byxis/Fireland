package fr.byxis.faction.essaim.ui;

import fr.byxis.faction.essaim.essaimClass.EssaimGroup;
import fr.byxis.faction.essaim.services.EssaimConfigService;
import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.faction.faction.FactionPlayerInformation;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.fireland.utilities.TextUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for creating Essaim-related user interfaces
 * Centralizes all UI logic and provides consistent menu creation
 */
public class EssaimMenuBuilder {

    private final InventoryUtilities inventoryUtils;
    private final TextUtilities textUtils;
    private final EssaimConfigService configService;
    private final FactionFunctions factionFunctions;

    public EssaimMenuBuilder(InventoryUtilities inventoryUtils, TextUtilities textUtils,
                             EssaimConfigService configService, FactionFunctions factionFunctions) {
        this.inventoryUtils = inventoryUtils;
        this.textUtils = textUtils;
        this.configService = configService;
        this.factionFunctions = factionFunctions;
    }

    /**
     * Creates the main essaim group menu
     */
    public Inventory createGroupMenu(String essaimName, EssaimGroup group, boolean isFinished) {
        String cleanName = textUtils.convertStorableToClean(essaimName);
        String title = "§8Essaim : §c" + cleanName;
        Inventory inventory = Bukkit.createInventory(null, 27, title);

        if (isFinished) {
            setFinishedMenuItems(inventory);
        } else {
            setActiveGroupMenuItems(inventory, group, essaimName);
        }

        return inventory;
    }

    /**
     * Creates the invitation menu for adding players to group
     */
    public Inventory createInvitationMenu(String essaimName, EssaimGroup group, Player viewer) {
        String cleanName = textUtils.convertStorableToClean(essaimName);
        String title = "§8Invitation : §c" + cleanName;
        Inventory inventory = Bukkit.createInventory(null, 54, title);

        setInvitationMenuItems(inventory, group, viewer);
        return inventory;
    }

    /**
     * Creates the difficulty selection menu
     */
    public Inventory createDifficultyMenu(String essaimName) {
        String cleanName = textUtils.convertStorableToClean(essaimName);
        String title = "§8Lancement de §c" + cleanName;
        Inventory inventory = Bukkit.createInventory(null, 27, title);

        setDifficultyMenuItems(inventory);
        return inventory;
    }

    // Private menu building methods

    private void setFinishedMenuItems(Inventory inventory) {
        ItemStack leaveButton = inventoryUtils.setItemMeta(
                Material.RED_STAINED_GLASS_PANE,
                "§cQuitter l'essaim",
                (short) 1
        );
        inventory.setItem(13, leaveButton);
    }

    private void setActiveGroupMenuItems(Inventory inventory, EssaimGroup group, String essaimName) {
        // Member display with leader's head
        ItemStack memberHead = createMemberDisplay(group);
        inventory.setItem(4, memberHead);

        // Action buttons
        inventory.setItem(10, createLaunchButton(essaimName));
        inventory.setItem(13, createInviteButton());
        inventory.setItem(16, createLeaveButton());
    }

    private ItemStack createMemberDisplay(EssaimGroup group) {
        ItemStack head = inventoryUtils.getHead(
                group.getLeader().getUniqueId(),
                "§eMembres - §7(" + group.getMembers().size() + "/4)"
        );

        List<String> memberLore = new ArrayList<>();
        for (Player member : group.getMembers()) {
            String prefix = member.equals(group.getLeader()) ? "§6" : "§8";
            memberLore.add(prefix + member.getName());
        }
        head.setLore(memberLore);

        return head;
    }

    private ItemStack createLaunchButton(String essaimName) {
        List<String> lore = new ArrayList<>();

        // Add recommendations from config if they exist
        EssaimConfigService.EssaimInfo info = configService.getEssaimInfo(essaimName);
        if (info != null) {
            for (int i = 1; i <= 3; i++) {
                String recommendation = configService.getRawConfig().getString(essaimName + ".recommendations." + i);
                if (recommendation != null) {
                    lore.add(recommendation);
                }
            }
        }

        return inventoryUtils.setItemMetaLore(
                Material.LIME_STAINED_GLASS_PANE,
                "§aLancer l'expédition",
                (short) 1,
                lore
        );
    }

    private ItemStack createInviteButton() {
        return inventoryUtils.setItemMeta(
                Material.YELLOW_STAINED_GLASS_PANE,
                "§eInviter des membres de votre faction",
                (short) 1
        );
    }

    private ItemStack createLeaveButton() {
        return inventoryUtils.setItemMeta(
                Material.RED_STAINED_GLASS_PANE,
                "§cQuitter l'expédition",
                (short) 1
        );
    }

    private void setInvitationMenuItems(Inventory inventory, EssaimGroup group, Player viewer) {
        // Header
        ItemStack header = inventoryUtils.setItemMeta(
                Material.BOOK,
                "§eMembres - §7(" + group.getMembers().size() + "/4)",
                (short) 1
        );
        inventory.setItem(0, header);

        // Glass panes for decoration
        fillGlassPanes(inventory);

        // Current members (top row)
        displayCurrentMembers(inventory, group);

        // Available faction members (main area)
        displayAvailableFactionMembers(inventory, group, viewer);

        // Back button
        ItemStack backButton = inventoryUtils.setItemMeta(
                Material.RED_STAINED_GLASS_PANE,
                "§cRetour",
                (short) 0
        );
        inventory.setItem(53, backButton);
    }

    private void fillGlassPanes(Inventory inventory) {
        ItemStack glassPane = inventoryUtils.setItemMeta(
                Material.WHITE_STAINED_GLASS_PANE,
                "§rCliquez sur un joueur pour l'inviter",
                (short) 1
        );

        // Top separator
        for (int i = 9; i < 18; i++) {
            inventory.setItem(i, glassPane);
        }

        // Bottom separator
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, glassPane);
        }
    }

    private void displayCurrentMembers(Inventory inventory, EssaimGroup group) {
        int slot = 1;
        for (Player member : group.getMembers()) {
            if (slot > 4) break; // Max 4 members

            ItemStack memberHead = inventoryUtils.getHead(
                    member.getUniqueId(),
                    "§8" + member.getName()
            );
            inventory.setItem(slot, memberHead);
            slot++;
        }
    }

    private void displayAvailableFactionMembers(Inventory inventory, EssaimGroup group, Player viewer) {
        String factionName = factionFunctions.playerFactionName(viewer);
        List<FactionPlayerInformation> factionMembers = factionFunctions.getPlayersFromFaction(factionName);

        int slot = 18;
        for (FactionPlayerInformation member : factionMembers) {
            if (slot >= 45) break; // Don't overflow into bottom separator

            // Skip if already in group
            boolean isInGroup = group.getMembers().stream()
                    .anyMatch(p -> p.getName().equalsIgnoreCase(member.getName()));

            if (!isInGroup) {
                ItemStack memberHead = inventoryUtils.getHead(
                        member.getUuid(),
                        "§8" + member.getName()
                );
                inventory.setItem(slot, memberHead);
                slot++;
            }
        }
    }

    private void setDifficultyMenuItems(Inventory inventory) {
        // Easy difficulty
        List<String> easyLore = List.of(
                "§8Une issue s'offre à vous. Vous garderez votre inventaire",
                "§8en cas de mort mais la récompense finale sera amoindrie."
        );
        ItemStack easyButton = inventoryUtils.setItemMetaLore(
                Material.PLAYER_HEAD,
                "§eÉchappatoire",
                (short) 1,
                easyLore
        );
        inventory.setItem(10, easyButton);

        // Normal difficulty
        List<String> normalLore = List.of(
                "§8Aucun retour possible. Vous perdrez votre inventaire",
                "§8en cas de mort mais la récompense finale sera augmentée."
        );
        ItemStack normalButton = inventoryUtils.setItemMetaLore(
                Material.SKELETON_SKULL,
                "§cÉtreinte Mortelle",
                (short) 1,
                normalLore
        );
        inventory.setItem(13, normalButton);

        // Hard difficulty (disabled for now)
        List<String> hardLore = List.of(
                "§8Personne ne peut vous retenir de choisir cette voie, mais",
                "§8restez sur vos gardes. La récompense finale sera maximale et",
                "§8suivie d'un bonus de 5 jetons.",
                "§cÀ venir bientôt..."
        );
        ItemStack hardButton = inventoryUtils.setItemMetaLore(
                Material.WITHER_SKELETON_SKULL,
                "§4§lLune de Sang",
                (short) 1,
                hardLore
        );
        inventory.setItem(16, hardButton);
    }
}