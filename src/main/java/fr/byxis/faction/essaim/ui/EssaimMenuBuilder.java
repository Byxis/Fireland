package fr.byxis.faction.essaim.ui;

import fr.byxis.faction.essaim.essaimClass.EssaimGroup;
import fr.byxis.faction.essaim.services.EssaimConfigService;
import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.faction.faction.FactionPlayerInformation;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.fireland.utilities.TextUtilities;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Builder for creating Essaim-related user interfaces Centralizes all UI logic
 * and provides consistent menu creation
 */
public class EssaimMenuBuilder
{

    private final InventoryUtilities m_inventoryUtilities;
    private final TextUtilities m_textUtils;
    private final EssaimConfigService m_configService;
    private final FactionFunctions m_factionFunctions;

    public EssaimMenuBuilder(InventoryUtilities _inventoryUtilities, TextUtilities _textUtilities, EssaimConfigService _essaimConfigService,
            FactionFunctions _factionFunctions)
    {
        this.m_inventoryUtilities = _inventoryUtilities;
        this.m_textUtils = _textUtilities;
        this.m_configService = _essaimConfigService;
        this.m_factionFunctions = _factionFunctions;
    }

    /**
     * Creates the main essaim group menu
     *
     * @param _essaimName
     *            The name of the essaim
     * @param _group
     *            The essaim group
     * @param _isFinished
     *            Whether the essaim is finished
     *
     * @return The constructed Inventory menu
     */
    public Inventory createGroupMenu(String _essaimName, EssaimGroup _group, boolean _isFinished)
    {
        String cleanName = m_textUtils.convertStorableToClean(_essaimName);
        String title = "§8Essaim : §c" + cleanName;
        Inventory inventory = Bukkit.createInventory(null, 27, title);

        if (_isFinished)
        {
            setFinishedMenuItems(inventory);
        }
        else
        {
            setActiveGroupMenuItems(inventory, _group, _essaimName);
        }

        return inventory;
    }

    /**
     * Creates the invitation menu for adding players to group
     *
     * @param _essaimName
     *            The name of the essaim
     * @param _group
     *            The essaim group
     * @param _viewer
     *            The player viewing the menu
     *
     * @return The constructed Inventory menu
     */
    public Inventory createInvitationMenu(String _essaimName, EssaimGroup _group, Player _viewer)
    {
        String cleanName = m_textUtils.convertStorableToClean(_essaimName);
        String title = "§8Invitation : §c" + cleanName;
        Inventory inventory = Bukkit.createInventory(null, 54, title);

        setInvitationMenuItems(inventory, _group, _viewer);
        return inventory;
    }

    /**
     * Creates the difficulty selection menu
     *
     * @param _essaimName
     *            The name of the essaim
     *
     * @return The constructed Inventory menu
     */
    public Inventory createDifficultyMenu(String _essaimName)
    {
        String cleanName = m_textUtils.convertStorableToClean(_essaimName);
        String title = "§8Lancement de §c" + cleanName;
        Inventory inventory = Bukkit.createInventory(null, 27, title);

        setDifficultyMenuItems(inventory);
        return inventory;
    }

    // Private menu building methods

    private void setFinishedMenuItems(Inventory _inventory)
    {
        ItemStack leaveButton = m_inventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cQuitter l'essaim", (short) 1);
        _inventory.setItem(13, leaveButton);
    }

    private void setActiveGroupMenuItems(Inventory _inventory, EssaimGroup _group, String _essaimName)
    {
        // Member display with leader's head
        ItemStack memberHead = createMemberDisplay(_group);
        _inventory.setItem(4, memberHead);

        // Action buttons
        _inventory.setItem(10, createLaunchButton(_essaimName));
        _inventory.setItem(13, createInviteButton());
        _inventory.setItem(16, createLeaveButton());
    }

    private ItemStack createMemberDisplay(EssaimGroup _group)
    {
        ItemStack head = m_inventoryUtilities.getHead(_group.getLeader().getUniqueId(),
                "§eMembres - §7(" + _group.getMembers().size() + "/4)");

        List<String> memberLore = new ArrayList<>();
        for (Player member : _group.getMembers())
        {
            String prefix = member.equals(_group.getLeader()) ? "§6" : "§8";
            memberLore.add(prefix + member.getName());
        }
        head.setLore(memberLore);

        return head;
    }

    private ItemStack createLaunchButton(String _essaimName)
    {
        List<String> lore = new ArrayList<>();

        // Add recommendations from config if they exist
        EssaimConfigService.EssaimInfo info = m_configService.getEssaimInfo(_essaimName);
        if (info != null)
        {
            for (int i = 1; i <= 3; i++)
            {
                String recommendation = m_configService.getRawConfig().getString(_essaimName + ".recommendations." + i);
                if (recommendation != null)
                {
                    lore.add(recommendation);
                }
            }
        }

        return m_inventoryUtilities.setItemMetaLore(Material.LIME_STAINED_GLASS_PANE, "§aLancer l'expédition", (short) 1, lore);
    }

    private ItemStack createInviteButton()
    {
        return m_inventoryUtilities.setItemMeta(Material.YELLOW_STAINED_GLASS_PANE, "§eInviter des membres de votre faction", (short) 1);
    }

    private ItemStack createLeaveButton()
    {
        return m_inventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cQuitter l'expédition", (short) 1);
    }

    private void setInvitationMenuItems(Inventory _inventory, EssaimGroup _group, Player _viewer)
    {
        // Header
        ItemStack header = m_inventoryUtilities.setItemMeta(Material.BOOK, "§eMembres - §7(" + _group.getMembers().size() + "/4)",
                (short) 1);
        _inventory.setItem(0, header);

        // Glass panes for decoration
        fillGlassPanes(_inventory);

        // Current members (top row)
        displayCurrentMembers(_inventory, _group);

        // Available faction members (main area)
        displayAvailableFactionMembers(_inventory, _group, _viewer);

        // Back button
        ItemStack backButton = m_inventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour", (short) 0);
        _inventory.setItem(53, backButton);
    }

    private void fillGlassPanes(Inventory _inventory)
    {
        ItemStack glassPane = m_inventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, "§rCliquez sur un joueur pour l'inviter",
                (short) 1);

        // Top separator
        for (int i = 9; i < 18; i++)
        {
            _inventory.setItem(i, glassPane);
        }

        // Bottom separator
        for (int i = 45; i < 54; i++)
        {
            _inventory.setItem(i, glassPane);
        }
    }

    private void displayCurrentMembers(Inventory _inventory, EssaimGroup _group)
    {
        int slot = 1;
        for (Player member : _group.getMembers())
        {
            if (slot > 4)
                break; // Max 4 members

            ItemStack memberHead = m_inventoryUtilities.getHead(member.getUniqueId(), "§8" + member.getName());
            _inventory.setItem(slot, memberHead);
            slot++;
        }
    }

    private void displayAvailableFactionMembers(Inventory _inventory, EssaimGroup _group, Player _viewer)
    {
        String factionName = m_factionFunctions.playerFactionName(_viewer);
        List<FactionPlayerInformation> factionMembers = m_factionFunctions.getPlayersFromFaction(factionName);

        int slot = 18;
        for (FactionPlayerInformation member : factionMembers)
        {
            if (slot >= 45)
                break; // Don't overflow into bottom separator

            // Skip if already in group
            boolean isInGroup = _group.getMembers().stream().anyMatch(p -> p.getName().equalsIgnoreCase(member.getName()));

            if (!isInGroup)
            {
                ItemStack memberHead = m_inventoryUtilities.getHead(member.getUuid(), "§8" + member.getName());
                _inventory.setItem(slot, memberHead);
                slot++;
            }
        }
    }

    private void setDifficultyMenuItems(Inventory _inventory)
    {
        // Easy difficulty
        List<String> easyLore = List.of("§8Une issue s'offre à vous. Vous garderez votre inventaire",
                "§8en cas de mort mais la récompense finale sera amoindrie.");
        ItemStack easyButton = m_inventoryUtilities.setItemMetaLore(Material.PLAYER_HEAD, "§eÉchappatoire", (short) 1, easyLore);
        _inventory.setItem(10, easyButton);

        // Normal difficulty
        List<String> normalLore = List.of("§8Aucun retour possible. Vous perdrez votre inventaire",
                "§8en cas de mort mais la récompense finale sera augmentée.");
        ItemStack normalButton = m_inventoryUtilities.setItemMetaLore(Material.SKELETON_SKULL, "§cÉtreinte Mortelle", (short) 1,
                normalLore);
        _inventory.setItem(13, normalButton);

        // Hard difficulty (disabled for now)
        List<String> hardLore = List.of("§8Personne ne peut vous retenir de choisir cette voie, mais",
                "§8restez sur vos gardes. La récompense finale sera maximale et", "§8suivie d'un bonus de 5 jetons.",
                "§cÀ venir bientôt...");
        ItemStack hardButton = m_inventoryUtilities.setItemMetaLore(Material.WITHER_SKELETON_SKULL, "§4§lLune de Sang", (short) 1,
                hardLore);
        _inventory.setItem(16, hardButton);
    }
}