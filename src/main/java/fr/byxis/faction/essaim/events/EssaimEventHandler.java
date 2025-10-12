package fr.byxis.faction.essaim.events;

import fr.byxis.faction.essaim.EssaimManager;
import fr.byxis.faction.essaim.essaimClass.EssaimClass;
import fr.byxis.faction.essaim.essaimClass.EssaimGroup;
import fr.byxis.faction.essaim.managers.GroupManager;
import fr.byxis.faction.essaim.managers.SpawnerManager;
import fr.byxis.faction.essaim.services.EssaimConfigService;
import fr.byxis.faction.essaim.services.EssaimCooldownService;
import fr.byxis.faction.essaim.services.EssaimService;
import fr.byxis.faction.essaim.ui.EssaimMenuBuilder;
import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import fr.byxis.fireland.utilities.TextUtilities;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.*;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Event handler for the Essaim (essaim) functionality.
 * <p>
 * This class handles all events related to essaims, including:
 * <ul>
 *   <li>MythicMobs mob events (spawn, despawn, death)</li>
 *   <li>Player events (death, login, logout, world change)</li>
 *   <li>Entity and block interactions</li>
 *   <li>Inventory interactions (menus)</li>
 *   <li>World save events</li>
 * </ul>
 *
 * @author Byxis
 * @version 1.0
 * @since 5.0
 */
public class EssaimEventHandler implements Listener {

    private static final long INTERACTION_COOLDOWN = 500;
    private final Fireland m_fireland;
    private final EssaimManager m_essaimManager;
    private final GroupManager m_groupManager;
    private final SpawnerManager m_spawnerManager;
    private final EssaimService m_essaimService;
    private final EssaimConfigService m_configService;
    private final EssaimMenuBuilder m_menuBuilder;
    private final FactionFunctions m_factionFunctions;
    private final Map<UUID, Long> m_interactCooldowns = new HashMap<>();


    /**
     * Constructs a new event handler for essaims.
     * <p>
     * Initializes all necessary managers and services from the provided EssaimManager.
     *
     * @param _fireland Main instance of the Fireland plugin
     * @param _essaimManager Main essaim manager
     */
    public EssaimEventHandler(Fireland _fireland, EssaimManager _essaimManager)
    {
        this.m_fireland = _fireland;
        this.m_essaimManager = _essaimManager;
        this.m_groupManager = _essaimManager.getGroupManager();
        this.m_spawnerManager = _essaimManager.getSpawnerManager();
        this.m_essaimService = _essaimManager.getEssaimService();
        this.m_configService = _essaimManager.getConfigService();
        this.m_factionFunctions = new FactionFunctions(_fireland, null);

        // Initialize menu builder
        this.m_menuBuilder = new EssaimMenuBuilder(new InventoryUtilities(), new TextUtilities(), m_configService, m_factionFunctions);
    }

    // Mob events

    /**
     * Handles the death event of a MythicMobs mob.
     * <p>
     * Notifies the SpawnerManager of the mob's disappearance if it's in the "essaim" world.
     *
     * @param _event MythicMobs death event
     */
    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent _event)
    {
        handleMobDisappearing(_event.getMob());
    }

    /**
     * Handles the despawn event of a MythicMobs mob.
     * <p>
     * Notifies the SpawnerManager of the mob's disappearance if it's in the "essaim" world.
     *
     * @param _event MythicMobs despawn event
     */
    @EventHandler
    public void onMythicMobDespawn(MythicMobDespawnEvent _event)
    {
        handleMobDisappearing(_event.getMob());
    }

    /**
     * Handles the canceled spawn event of a MythicMobs mob.
     * <p>
     * If the spawn is canceled, notifies the SpawnerManager of the mob's disappearance.
     *
     * @param _event MythicMobs spawn event
     */
    @EventHandler
    public void onMythicMobSpawnCancelled(MythicMobSpawnEvent _event)
    {
        if (_event.isCancelled())
        {
            handleMobDisappearing(_event.getMob());
        }
    }

    /**
     * Handles the disappearance of a mob (death, despawn, or canceled spawn).
     * <p>
     * Checks that the mob is in the "essaim" world before notifying the SpawnerManager.
     *
     * @param _mob The MythicMobs mob that is disappearing
     */
    private void handleMobDisappearing(io.lumine.mythic.core.mobs.ActiveMob _mob)
    {
        if (!"essaim".equals(_mob.getLocation().getWorld().getName()))
        {
            return;
        }

        m_spawnerManager.onMobRemoved(_mob);
    }

    // Player events

    /**
     * Handles the player death event.
     * <p>
     * If the player is in an essaim group:
     * <ul>
     *   <li>Keeps their inventory if the group has started and this option is enabled</li>
     *   <li>Displays an expedition failure message</li>
     *   <li>Notifies the GroupManager of the player's death</li>
     * </ul>
     *
     * @param _event Player death event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent _event)
    {
        Player player = _event.getPlayer();

        m_groupManager.findPlayerGroup(player).ifPresent(group ->
        {
            if (group.shouldKeepInventoryOnDeath() && group.hasStarted())
            {
                _event.setKeepInventory(true);
                _event.getDrops().clear();
            }

            InGameUtilities.sendPlayerInformation(player, "§cVous avez échoué l'expédition !");
        });

        m_groupManager.handlePlayerDeath(player);
    }

    /**
     * Handles the player quit event.
     * <p>
     * Delegates handling to the leaving handler with the quit flag.
     *
     * @param _event Player quit event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent _event)
    {
        handlePlayerLeaving(_event.getPlayer(), true);
    }

    /**
     * Handles the player join event.
     * <p>
     * Removes the player from any groups they might be in from a previous session.
     *
     * @param _event Player join event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent _event)
    {
        // Remove player from any groups they might be in from previous session
        m_groupManager.findPlayerGroup(_event.getPlayer()).ifPresent(group ->
        {
            String essaimName = findEssaimNameByGroup(group);
            if (essaimName != null)
            {
                m_groupManager.leaveGroup(essaimName, _event.getPlayer());
            }
        });
    }

    /**
     * Handles the player world change event.
     * <p>
     * If the player leaves the "essaim" world, handles their departure from the group.
     *
     * @param _event Player world change event
     */
    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent _event)
    {
        if (!"essaim".equals(_event.getFrom().getName()))
        {
            return;
        }

        handlePlayerLeaving(_event.getPlayer(), false);
    }

    /**
     * Handles a player leaving (disconnect or world change).
     * <p>
     * Behaviors depending on context:
     * <ul>
     *   <li>If disconnecting during an ongoing expedition: kills the player</li>
     *   <li>Otherwise: removes the player from the group normally</li>
     * </ul>
     *
     * @param _player The player who is leaving
     * @param _isQuit True if this is a disconnect, false if it's a world change
     */
    private void handlePlayerLeaving(Player _player, boolean _isQuit)
    {
        m_groupManager.findPlayerGroup(_player).ifPresent(group ->
        {
            GameMode gameMode = _player.getGameMode();
            boolean isNormalPlayer = gameMode != GameMode.CREATIVE && gameMode != GameMode.SPECTATOR;

            if (_isQuit && isNormalPlayer && group.hasStarted())
            {
                // Kill player for quitting during expedition
                _player.setInvulnerable(false);
                _player.setHealth(0);
            }
            else
            {
                // Normal leave
                String essaimName = findEssaimNameByGroup(group);
                if (essaimName != null)
                {
                    m_groupManager.leaveGroup(essaimName, _player);
                }
            }
        });
    }


    /**
     * Handles the player interact with entity event.
     * <p>
     * Detects interaction with ItemFrames containing dead fire coral (essaim portals).
     * Checks cooldowns, faction permissions, and opens the appropriate essaim.
     *
     * @param _event Player interact with entity event
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent _event)
    {

        Player player = _event.getPlayer();
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        Long lastInteraction = m_interactCooldowns.get(playerId);
        if (lastInteraction != null && (currentTime - lastInteraction) < INTERACTION_COOLDOWN)
        {
            _event.setCancelled(true);
            return;
        }

        m_interactCooldowns.put(playerId, currentTime);
        if (!(_event.getRightClicked() instanceof ItemFrame itemFrame))
        {
            return;
        }

        ItemStack item = itemFrame.getItem();
        if (item.getType() != Material.DEAD_FIRE_CORAL || !item.hasItemMeta())
        {
            return;
        }

        _event.setCancelled(true);

        if (!isPlayerInFaction(player))
        {
            InGameUtilities.sendPlayerError(player, "Vous devez être dans une faction pour entrer dans un essaim.");
            return;
        }

        if (!item.getItemMeta().hasDisplayName())
        {
            InGameUtilities.sendPlayerError(player, "Cet essaim est actuellement indisponible.");
            return;
        }

        String essaimName = TextUtilities.convertCleanToStorable(item.getItemMeta().getDisplayName(), " ");
        System.out.println("Entering essaim");

        handleEssaimInteraction(player, essaimName);
    }

    /**
     * Handles the player interact with block event.
     * <p>
     * Detects the use of keys (ECHO_SHARD) on iron doors to open
     * locked doors in essaims.
     *
     * @param _event Player interact event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent _event)
    {
        if (_event.getAction() != Action.RIGHT_CLICK_BLOCK || _event.getClickedBlock() == null)
        {
            return;
        }

        if (_event.getClickedBlock().getType() != Material.IRON_DOOR)
        {
            return;
        }

        ItemStack item = _event.getItem();
        if (item == null || item.getType() != Material.ECHO_SHARD || !item.hasItemMeta())
        {
            return;
        }

        handleKeyUsage(_event);
    }

    // Inventory events

    /**
     * Handles the inventory click event.
     * <p>
     * Detects and handles clicks in different essaim menus:
     * <ul>
     *   <li>Main essaim menu</li>
     *   <li>Invitation menu</li>
     *   <li>Difficulty selection menu</li>
     * </ul>
     *
     * @param _event Inventory click event
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent _event)
    {
        if (!(_event.getWhoClicked() instanceof Player _player))
        {
            return;
        }

        String title = _event.getView().getTitle();
        ItemStack clickedItem = _event.getCurrentItem();

        if (clickedItem == null)
        {
            return;
        }

        if (title.contains("Essaim :"))
        {
            handleEssaimMenuClick(_event, _player, clickedItem);
        }
        else if (title.contains("Invitation :"))
        {
            handleInvitationMenuClick(_event, _player, clickedItem);
        }
        else if (title.contains("Lancement de "))
        {
            handleDifficultyMenuClick(_event, _player, clickedItem);
        }
    }

    // World events

    /**
     * Handles the world save event.
     * <p>
     * Saves the state of all essaims when the main world is saved.
     *
     * @param _event World save event
     */
    @EventHandler
    public void onWorldSave(WorldSaveEvent _event)
    {
        if (!"world".equals(_event.getWorld().getName()))
        {
            return;
        }

        saveAllEssaimStates();
    }

    // Private helper methods

    /**
     * Checks if a player is a member of a faction.
     *
     * @param _player The player to check
     *
     * @return true if the player is in a faction, false otherwise
     */
    private boolean isPlayerInFaction(Player _player)
    {
        return !m_factionFunctions.playerFactionName(_player).isEmpty();
    }

    /**
     * Handles a player's interaction with a essaim portal.
     * <p>
     * Checks that the essaim is active, that the player can join, and opens the appropriate menu
     * or creates a new group.
     *
     * @param _player The player interacting
     * @param _essaimName The essaim name
     */
    private void handleEssaimInteraction(Player _player, String _essaimName)
    {
        if (!m_essaimService.isEssaimActive(_essaimName))
        {
            InGameUtilities.sendPlayerError(_player, "L'essaim n'est pas ouvert !");
            return;
        }

        if (!canPlayerJoinEssaim(_player, _essaimName))
        {
            return;
        }

        if (m_groupManager.hasGroup(_essaimName))
        {
            if (m_groupManager.findPlayerGroup(_player).isPresent())
            {
                EssaimGroup group = m_groupManager.getGroup(_essaimName);
                boolean isFinished = m_essaimService.getActiveEssaim(_essaimName).isFinished();
                _player.openInventory(m_menuBuilder.createGroupMenu(_essaimName, group, isFinished));
            }
            else
            {
                InGameUtilities.sendPlayerError(_player, "Un groupe est déjà entré dans l'essaim !");
            }
        }
        else
        {
            System.out.println("Creating a new group");
            createNewGroupAndTeleport(_player, _essaimName);
        }
    }

    /**
     * Creates a new group and teleports the player to the essaim hub.
     * <p>
     * Checks permissions, handles teleportation with delay (except in creative mode),
     * and creates the group after teleportation.
     *
     * @param _player The player to teleport
     * @param _essaimName The essaim name
     */
    private void createNewGroupAndTeleport(Player _player, String _essaimName)
    {
        if (m_fireland.getHashMapManager().isTeleporting(_player.getUniqueId()))
        {
            return;
        }

        if (!hasEssaimPermission(_player, _essaimName))
        {
            InGameUtilities.sendPlayerError(_player, "Vous n'avez pas complété la quête requise ou n'avez pas l'extension DLC.");
            return;
        }

        if (m_groupManager.hasGroup(_essaimName))
        {
            InGameUtilities.sendPlayerError(_player, "Un groupe est déjà entré dans l'essaim !");
            return;
        }

        Location hubLocation = m_configService.getEssaimLocation(_essaimName, EssaimConfigService.LocationType.HUB);
        int teleportDuration = _player.getGameMode() == GameMode.CREATIVE ? 0 : 10;

        InGameUtilities.setPlayerMoving(_player.getUniqueId(), false);

        if (teleportDuration > 0)
        {
            InGameUtilities.sendPlayerInformation(_player, "Téléportation dans " + teleportDuration + " secondes...");

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    if (_player.isOnline() && !m_fireland.getHashMapManager().isTeleporting(_player.getUniqueId()))
                    {
                        createGroupAfterTeleport(_player, _essaimName);
                    }
                }
            }.runTaskLater(m_fireland, (teleportDuration + 1) * 20L);

            InGameUtilities.teleportPlayer(_player, hubLocation, teleportDuration, "gun.hub.helico");
        }
        else
        {
            _player.teleport(hubLocation);
            createGroupAfterTeleport(_player, _essaimName);
        }
    }

    /**
     * Creates the group after the player's teleportation.
     * <p>
     * Performs a final check that no group already exists and creates the group.
     * In case of error, teleports the player to spawn and restores their state.
     *
     * @param _player The player to create the group for
     * @param _essaimName The essaim name
     */
    private void createGroupAfterTeleport(Player _player, String _essaimName)
    {
        try
        {
            // Vérifier une dernière fois qu'aucun groupe n'existe
            if (m_groupManager.hasGroup(_essaimName))
            {
                InGameUtilities.sendPlayerError(_player, "Un groupe existe déjà !");
                _player.teleport(_player.getWorld().getSpawnLocation());
                return;
            }

            EssaimGroup newGroup = m_groupManager.createGroup(_essaimName, _player);
            System.out.println("Group created for player: " + _player.getName() + " in essaim: " + _essaimName);

            InGameUtilities.playPlayerSound(_player, "gun.hub.helico", SoundCategory.MASTER, 1, 1);
            InGameUtilities.sendPlayerInformation(_player, "§aBienvenue dans l'essaim " + TextUtilities.convertStorableToClean(_essaimName) + " !");
        }
        catch (IllegalStateException e)
        {
            // Un groupe existe déjà
            InGameUtilities.sendPlayerError(_player, "Un groupe est déjà entré dans l'essaim !");
            _player.teleport(_player.getWorld().getSpawnLocation());
        }
        catch (Exception e)
        {
            InGameUtilities.sendPlayerError(_player, "Erreur lors de l'entrée dans l'essaim !");
            m_fireland.getLogger().severe("Error joining essaim " + _essaimName + ": " + e.getMessage());

            // Téléporter le joueur de retour en cas d'erreur
            _player.teleport(_player.getWorld().getSpawnLocation());
        }
        finally
        {
            // S'assurer que l'état de mouvement est restauré
            InGameUtilities.setPlayerMoving(_player.getUniqueId(), true);
        }
    }

    /**
     * Handles clicks in the main essaim menu.
     * <p>
     * Distinguishes between a finished essaim (exit option only) and an active essaim
     * (launch, invitation, exit options).
     *
     * @param _event Inventory click event
     * @param _player The clicking player
     * @param _clickedItem The clicked item
     */
    private void handleEssaimMenuClick(InventoryClickEvent _event, Player _player, ItemStack _clickedItem)
    {
        InventoryUtilities.clickManager(_event);

        String essaimName = extractEssaimNameFromTitle(_event.getView().getTitle());
        EssaimGroup group = m_groupManager.getGroup(essaimName);
        boolean isFinished = m_essaimService.getActiveEssaim(essaimName).isFinished();

        if (isFinished)
        {
            if (_clickedItem.getType() == Material.RED_STAINED_GLASS_PANE)
            {
                InGameUtilities.playPlayerSound(_player, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
                handleFinishedEssaimLeave(essaimName, _player);
            }
        }
        else
        {
            handleActiveEssaimMenuClick(_clickedItem, _player, essaimName, group);
        }
    }

    /**
     * Handles clicks in an active essaim menu.
     * <p>
     * Available options :
     * <ul>
     *   <li>LIME_STAINED_GLASS_PANE: Open difficulty selection menu (leader only)</li>
     *   <li>RED_STAINED_GLASS_PANE: Leave the group</li>
     *   <li>YELLOW_STAINED_GLASS_PANE: Open invitation menu (leader only)</li>
     * </ul>
     *
     * @param _clickedItem The clicked item
     * @param _player The clicking player
     * @param _essaimName The essaim name
     * @param _group The current group
     */
    private void handleActiveEssaimMenuClick(ItemStack _clickedItem, Player _player, String _essaimName, EssaimGroup _group)
    {
        switch (_clickedItem.getType())
        {
            case LIME_STAINED_GLASS_PANE ->
            {
                if (_group.getLeader().equals(_player))
                {
                    _player.openInventory(m_menuBuilder.createDifficultyMenu(_essaimName));
                }
                else
                {
                    InGameUtilities.playPlayerSound(_player, "item.shield.break", SoundCategory.BLOCKS, 1, 0);
                    InGameUtilities.sendPlayerError(_player, "§cVous n'êtes pas le leader du groupe.");
                }
            }
            case RED_STAINED_GLASS_PANE ->
            {
                m_groupManager.leaveGroup(_essaimName, _player);
            }
            case YELLOW_STAINED_GLASS_PANE ->
            {
                if (_group.getLeader().equals(_player))
                {
                    _player.openInventory(m_menuBuilder.createInvitationMenu(_essaimName, _group, _player));
                    InGameUtilities.playPlayerSound(_player, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
                }
                else
                {
                    InGameUtilities.playPlayerSound(_player, "item.shield.break", SoundCategory.BLOCKS, 1, 0);
                    InGameUtilities.sendPlayerError(_player, "§cVous n'êtes pas le leader du groupe.");
                }
            }
        }
    }

    /**
     * Handles clicks in the invitation menu.
     * <p>
     * Allows inviting players by clicking on their head or returning to the main menu.
     *
     * @param _event Inventory click event
     * @param _player The clicking player
     * @param _clickedItem The clicked item
     */
    private void handleInvitationMenuClick(InventoryClickEvent _event, Player _player, ItemStack _clickedItem)
    {
        InventoryUtilities.clickManager(_event);

        String essaimName = extractEssaimNameFromTitle(_event.getView().getTitle());

        switch (_clickedItem.getType())
        {
            case PLAYER_HEAD ->
            {
                if (_clickedItem.hasItemMeta() && _clickedItem.getItemMeta().hasDisplayName())
                {
                    String targetName = ChatColor.stripColor(_clickedItem.getItemMeta().getDisplayName());
                    Player target = Bukkit.getPlayer(targetName);

                    if (target != null)
                    {
                        invitePlayerToGroup(essaimName, target);
                        InGameUtilities.playPlayerSound(_player, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
                    }
                }
            }
            case RED_STAINED_GLASS_PANE ->
            {
                EssaimGroup group = m_groupManager.getGroup(essaimName);
                boolean isFinished = m_essaimService.getActiveEssaim(essaimName).isFinished();
                _player.openInventory(m_menuBuilder.createGroupMenu(essaimName, group, isFinished));
                InGameUtilities.playPlayerSound(_player, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
            }
        }
    }

    /**
     * Handles clicks in the difficulty selection menu.
     * <p>
     * Difficulty 1: PLAYER_HEAD
     * Difficulty 2: SKELETON_SKULL
     * Difficulty 3: WITHER_SKELETON_SKULL (not available)
     *
     * @param _event Inventory click event
     * @param _player The clicking player
     * @param _clickedItem The clicked item
     */
    private void handleDifficultyMenuClick(InventoryClickEvent _event, Player _player, ItemStack _clickedItem)
    {
        InventoryUtilities.clickManager(_event);

        String essaimName = extractEssaimNameFromTitle(_event.getView().getTitle());
        EssaimGroup group = m_groupManager.getGroup(essaimName);

        if (!group.getLeader().equals(_player))
        {
            return;
        }

        int difficulty = switch (_clickedItem.getType())
        {
            case PLAYER_HEAD -> 1;
            case SKELETON_SKULL -> 2;
            case WITHER_SKELETON_SKULL ->
            {
                InGameUtilities.sendPlayerError(_player, "Cette difficulté n'est pas encore disponible.");
                yield -1;
            }
            default -> -1;
        };

        if (difficulty > 0)
        {
            launchEssaim(essaimName, difficulty, _player);
        }
    }

    /**
     * Handles the use of a essaim key on a door.
     * <p>
     * Retrieves the essaim name from the key's CustomModelData
     * and attempts to open the associated door.
     *
     * @param _event Player interact event
     */
    private void handleKeyUsage(PlayerInteractEvent _event)
    {
        ItemStack key = _event.getItem();
        if (!key.getItemMeta().hasCustomModelData())
        {
            return;
        }

        String essaimName = getEssaimNameFromKeyModel(key.getItemMeta().getCustomModelData());
        if (essaimName.isEmpty())
        {
            return;
        }

        try
        {
            Location keyLocation = m_configService.getEssaimLocation(essaimName, EssaimConfigService.LocationType.KEY);
            openDoorWithKey(_event, keyLocation);
        }
        catch (EssaimConfigService.ConfigurationException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Gets the essaim name from a key's CustomModelData.
     * <p>
     * ID mapping:
     * <ul>
     *   <li>1: bunker-de-latus</li>
     *   <li>2: usine-portuaire</li>
     *   <li>3: station-de-traitement-des-eaux</li>
     *   <li>4: entrepot-militaire</li>
     *   <li>5: immeuble-infeste</li>
     *   <li>6: hangar-silencieux</li>
     *   <li>8: centrale-nucleaire</li>
     *   <li>9: epave-du-porte-avion</li>
     *   <li>10: crypte</li>
     *   <li>11: station-petroliere</li>
     *   <li>12: laboratoire</li>
     * </ul>
     *
     * @param _customModelData The key's CustomModelData ID
     *
     * @return The corresponding essaim name, or an empty string if not found
     */
    private String getEssaimNameFromKeyModel(int _customModelData)
    {
        return switch (_customModelData)
        {
            case 1 -> "bunker-de-latus";
            case 2 -> "usine-portuaire";
            case 3 -> "station-de-traitement-des-eaux";
            case 4 -> "entrepot-militaire";
            case 5 -> "immeuble-infeste";
            case 6 -> "hangar-silencieux";
            case 8 -> "centrale-nucleaire";
            case 9 -> "epave-du-porte-avion";
            case 10 -> "crypte";
            case 11 -> "station-petroliere";
            case 12 -> "laboratoire";
            default -> "";
        };
    }

    /**
     * Opens a door with a essaim key.
     * <p>
     * Checks that the door is at the correct position, that it's closed,
     * opens it and consumes the key (except in creative mode).
     *
     * @param _event Player interact event
     * @param _keyLocation The configured door position for this essaim
     */
    private void openDoorWithKey(PlayerInteractEvent _event, Location _keyLocation)
    {
        Location clickedLocation = _event.getClickedBlock().getLocation();
        Location upperLocation = clickedLocation.clone().add(0, 1, 0);

        if (clickedLocation.distance(_keyLocation) > 1 && upperLocation.distance(_keyLocation) > 1)
        {
            return;
        }

        Door door = (Door) _event.getClickedBlock().getBlockData();
        if (door.isOpen())
        {
            return;
        }

        door.setOpen(true);
        _event.getClickedBlock().setBlockData(door);

        InGameUtilities.playWorldSound(_keyLocation, Sound.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1, 1);

        if (_event.getPlayer().getGameMode() != GameMode.CREATIVE)
        {
            InGameUtilities.playWorldSound(_keyLocation, Sound.ITEM_SHIELD_BREAK, SoundCategory.BLOCKS, 1, 1);
            _event.getItem().setAmount(_event.getItem().getAmount() - 1);
        }
    }

    /**
     * Launches a essaim with the selected difficulty.
     * <p>
     * Starts the essaim via the service and notifies all group members.
     *
     * @param _essaimName The essaim name to launch
     * @param _difficulty The selected difficulty (1, 2, or 3)
     * @param _player The player launching the essaim (must be the leader)
     */
    private void launchEssaim(String _essaimName, int _difficulty, Player _player)
    {
        if (m_essaimService.startEssaim(_essaimName, _difficulty))
        {
            InGameUtilities.playPlayerSound(_player, "ui.button.click", SoundCategory.BLOCKS, 1, 2);

            EssaimGroup group = m_groupManager.getGroup(_essaimName);
            for (Player member : group.getMembers())
            {
                member.closeInventory();
                InGameUtilities.sendPlayerInformation(member, "§aL'expédition a démarrée !");
            }
        }
    }

    /**
     * Invites a player to join a essaim group.
     *
     * @param _essaimName The essaim name
     * @param _target The player to invite
     */
    private void invitePlayerToGroup(String _essaimName, Player _target)
    {
        EssaimGroup group = m_groupManager.getGroup(_essaimName);
        if (group != null)
        {
            group.invitePlayer(_target);
        }
    }

    /**
     * Handles a player leaving a finished essaim.
     * <p>
     * If it's the last player, completely cleans up the essaim.
     *
     * @param _essaimName The essaim name
     * @param _player The player leaving
     */
    private void handleFinishedEssaimLeave(String _essaimName, Player _player)
    {
        if (m_groupManager.hasGroup(_essaimName))
        {
            EssaimGroup group = m_groupManager.getGroup(_essaimName);

            // Faire sortir le joueur proprement
            m_essaimManager.getEssaimService().getExitService().exitPlayer(_player, group, true);

            m_groupManager.leaveGroup(_essaimName, _player);

            if (!m_groupManager.hasGroup(_essaimName) || m_groupManager.isGroupEmpty(_essaimName))
            {
                System.out.println("Last player left, cleaning up essaim: " + _essaimName);
                cleanupFinishedEssaim(_essaimName);
            }
            else
            {
                System.out.println("Group still has members, keeping essaim open: " + _essaimName);
            }
        }
    }

    /**
     * Cleans up a finished essaim when all players have left.
     * <p>
     * Disbands the group and resets the essaim state if it's an event.
     *
     * @param _essaimName The essaim name to clean up
     */
    private void cleanupFinishedEssaim(String _essaimName)
    {
        System.out.println("Starting cleanup for essaim: " + _essaimName);

        // Disband any remaining group
        if (m_groupManager.hasGroup(_essaimName))
        {
            System.out.println("Disbanding remaining group for: " + _essaimName);
            m_groupManager.disbandGroup(_essaimName);
        }

        // If the essaim is still marked as active, reset it
        if (m_essaimService.isEssaimActive(_essaimName))
        {
            EssaimClass essaim = m_essaimService.getActiveEssaim(_essaimName);
            if (essaim != null)
            {
                System.out.println("Resetting event essaim: " + _essaimName);
                essaim.unFinish();
                m_fireland.getLogger().info("Reset finished event essaim: " + _essaimName);
            }
        }
        else
        {
            System.out.println("Essaim " + _essaimName + " was already inactive");
        }
    }

    /**
     * Extracts the essaim name from a menu title.
     * <p>
     * Converts the formatted title to a storable essaim name (lowercase with dashes).
     *
     * @param _title The menu title
     *
     * @return The essaim name in storable format
     */
    private String extractEssaimNameFromTitle(String _title)
    {
        String[] parts = ChatColor.stripColor(_title).split(" ");
        StringBuilder sb = new StringBuilder();

        for (int i = 2; i < parts.length - 1; i++)
        {
            sb.append(parts[i].toLowerCase()).append("-");
        }
        if (parts.length > 2)
        {
            sb.append(parts[parts.length - 1].toLowerCase());
        }

        return sb.toString();
    }

    /**
     * Finds the essaim name corresponding to a given group.
     *
     * @param _group The group to search for
     *
     * @return The essaim name, or null if not found
     */
    private String findEssaimNameByGroup(EssaimGroup _group)
    {
        return m_groupManager.getAllGroups().entrySet().stream().filter(entry -> entry.getValue().equals(_group)).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    /**
     * Checks if a player has permission to access a specific essaim.
     * <p>
     * Required permission: {@code fireland.essaim.access.<essaim-name>}
     *
     * @param _player The player to check
     * @param _essaimName The essaim name
     *
     * @return true if the player has permission, false otherwise
     */
    private boolean hasEssaimPermission(Player _player, String _essaimName)
    {
        return fr.byxis.fireland.utilities.PermissionUtilities.hasPermission(_player.getUniqueId(), "fireland.essaim.access." + _essaimName);
    }

    /**
     * Saves the state (open/closed) of all essaims to the configuration.
     */
    private void saveAllEssaimStates()
    {
        for (String essaimName : m_configService.getEssaimNames())
        {
            boolean isActive = m_essaimService.isEssaimActive(essaimName);
            m_configService.setEssaimClosed(essaimName, !isActive);
        }
    }

    /**
     * Checks if a player can join a essaim based on participation cooldowns.
     * <p>
     * Displays an error message to the player if the cooldown hasn't expired.
     *
     * @param _player The player to check
     * @param _essaimName The essaim name
     *
     * @return true if the player can join, false if the cooldown is active
     */
    public boolean canPlayerJoinEssaim(Player _player, String _essaimName)
    {
        EssaimCooldownService cooldownService = m_essaimManager.getEssaimService().getCooldownService();
        EssaimClass essaim = m_essaimService.getActiveEssaim(_essaimName);

        if (!cooldownService.canPlayerEnter(_player, essaim))
        {
            long minutesRemaining = cooldownService.getRemainingParticipationCooldownMinutes(_player, _essaimName);
            InGameUtilities.sendPlayerInformation(_player, "§cVous devez attendre encore " + minutesRemaining + " minutes avant de rejoindre cet essaim.");
            return false;
        }

        return true;
    }
}
