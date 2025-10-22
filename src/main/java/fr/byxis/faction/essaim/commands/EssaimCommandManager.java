package fr.byxis.faction.essaim.commands;

import fr.byxis.faction.essaim.EssaimManager;
import fr.byxis.faction.essaim.conditions.ConditionScope;
import fr.byxis.faction.essaim.conditions.EssaimCondition;
import fr.byxis.faction.essaim.essaimClass.EssaimClass;
import fr.byxis.faction.essaim.essaimClass.EssaimGroup;
import fr.byxis.faction.essaim.essaimClass.Spawner;
import fr.byxis.faction.essaim.managers.GroupManager;
import fr.byxis.faction.essaim.managers.SpawnerManager;
import fr.byxis.faction.essaim.services.EssaimConfigService;
import fr.byxis.faction.essaim.services.EssaimCooldownService;
import fr.byxis.faction.essaim.services.EssaimService;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.PermissionUtilities;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

import static fr.byxis.fireland.utilities.InGameUtilities.sendError;
import static io.lumine.mythic.bukkit.commands.CommandHelper.sendSuccess;

/**
 * Command manager for "essaim" commands
 */
public class EssaimCommandManager implements CommandExecutor
{

    private final Fireland m_fireland;
    private final EssaimManager m_essaimManager;
    private final EssaimService m_essaimService;
    private final EssaimConfigService m_configService;
    private final GroupManager m_groupManager;
    private final SpawnerManager m_spawnerManager;

    public EssaimCommandManager(Fireland _fireland, EssaimManager _essaimManager)
    {
        this.m_fireland = _fireland;
        this.m_essaimManager = _essaimManager;
        this.m_essaimService = _essaimManager.getEssaimService();
        this.m_configService = _essaimManager.getConfigService();
        this.m_groupManager = _essaimManager.getGroupManager();
        this.m_spawnerManager = _essaimManager.getSpawnerManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender _sender, @NotNull Command _command,
                             @NotNull String _label, String[] _args)
    {
        if (_args.length == 0)
        {
            sendError(_sender, "Mauvaise formulation de la commande !");
            return false;
        }

        try
        {
            return switch (_args[0].toLowerCase())
            {
                case "spawner" -> handleSpawnerCommand(_sender, _args);
                case "create" -> handleCreateCommand(_sender, _args);
                case "open" -> handleOpenCommand(_sender, _args);
                case "close" -> handleCloseCommand(_sender, _args);
                case "remove" -> handleRemoveCommand(_sender, _args);
                case "set" -> handleSetCommand(_sender, _args);
                case "join" -> handleJoinCommand(_sender, _args);
                case "finish" -> handleFinishCommand(_sender, _args);
                case "unfinish" -> handleUnfinishCommand(_sender, _args);
                case "info" -> handleInfoCommand(_sender, _args);
                case "reset" -> handleResetCommand(_sender, _args);
                case "setblock" -> handleSetBlockCommand(_sender, _args);
                case "forcejoin" -> handleForceJoinCommand(_sender, _args);
                case "forcekick" -> handleForceKickCommand(_sender, _args);
                default ->
                {
                    sendError(_sender, "Commande inconnue !");
                    yield false;
                }
            };
        }
        catch (Exception e)
        {
            sendError(_sender, "Erreur lors de l'exécution de la commande : " + e.getMessage());
            m_fireland.getLogger().severe("Command error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Handles the "spawner" subcommand and its actions
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleSpawnerCommand(CommandSender _sender, String[] _args)
    {
        if (_sender instanceof Player p && !PermissionUtilities.hasPermission(p, "fireland.essaim.admin"))
        {
            sendError(_sender, "Permissions insuffisantes !");
            return false;
        }

        if (_args.length < 2)
        {
            sendError(_sender, "Usage: /essaim spawner <action> [args...]");
            return false;
        }

        return switch (_args[1].toLowerCase())
        {
            case "list" -> handleSpawnerList(_sender, _args);
            case "remove" -> handleSpawnerRemove(_sender, _args);
            case "activate" -> handleSpawnerActivate(_sender, _args);
            case "create" -> handleSpawnerCreate(_sender, _args);
            default ->
            {
                sendError(_sender, "Action de spawner inconnue !");
                yield false;
            }
        };
    }

    /**
     * Handles the "spawner list" action
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleSpawnerList(CommandSender _sender, String[] _args)
    {
        if (_args.length != 3)
        {
            sendError(_sender, "Usage: /essaim spawner list <essaim>");
            return false;
        }

        String essaimName = _args[2];
        if (!(_sender instanceof Player player))
        {
            sendError(_sender, "Cette commande nécessite d'être lancée par un joueur !");
            return false;
        }

        try
        {
            m_configService.getEssaimSpawners(essaimName).forEach((name, spawner) ->
            {
                String coords = String.format("%.0f %.0f %.0f",
                        spawner.getLoc().getX(),
                        spawner.getLoc().getY(),
                        spawner.getLoc().getZ()
                );
                InGameUtilities.sendInteractivePlayerMessage(
                        player,
                        name + " : §d§l" + coords,
                        "/tp " + player.getName() + " " + coords,
                        "§aCliquez ici pour vous téléporter",
                        ClickEvent.Action.RUN_COMMAND
                );
            });
            return true;
        }
        catch (Exception e)
        {
            sendError(_sender, "Erreur lors de la liste des spawners : " + e.getMessage());
            return false;
        }
    }

    /**
     * Handles the "spawner remove" action
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleSpawnerRemove(CommandSender _sender, String[] _args)
    {
        if (_args.length != 4)
        {
            sendError(_sender, "Usage: /essaim spawner remove <essaim> <spawner>");
            return false;
        }

        String essaimName = _args[2];
        String spawnerName = _args[3];

        try
        {
            if (!m_configService.getEssaimNames().contains(essaimName))
            {
                sendError(_sender, "L'essaim " + essaimName + " n'existe pas !");
                return false;
            }

            if (!m_configService.getEssaimSpawners(essaimName).containsKey(spawnerName))
            {
                sendError(_sender, "Le spawner " + spawnerName + " n'existe pas !");
                return false;
            }

            m_configService.removeSpawner(essaimName, spawnerName);
            sendSuccess(_sender, "Le spawner " + spawnerName + " a été supprimé avec succès !");
            return true;

        }
        catch (Exception e)
        {
            sendError(_sender, "Erreur lors de la suppression : " + e.getMessage());
            return false;
        }
    }

    /**
     * Handles the "spawner activate" action
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleSpawnerActivate(CommandSender _sender, String[] _args) {
        if (_args.length != 4)
        {
            sendError(_sender, "Usage: /essaim spawner activate <essaim> <spawner>");
            return false;
        }

        String essaimName = _args[2];
        String spawnerName = _args[3];

        try
        {
            Map<String, Spawner> spawners = m_configService.getEssaimSpawners(essaimName);
            if (!spawners.containsKey(spawnerName))
            {
                sendError(_sender, "Le spawner " + spawnerName + " n'existe pas !");
                return false;
            }

            if (m_spawnerManager.enableSpawner(spawners.get(spawnerName)))
            {
                sendSuccess(_sender, "Le spawner " + spawnerName + " activé avec succès !");
            }
            else
            {
                sendError(_sender, "Le spawner " + spawnerName + " est déjà activé !");
            }
            return true;

        } catch (Exception e) {
            sendError(_sender, "Erreur lors de l'activation : " + e.getMessage());
            return false;
        }
    }

    /**
     * Handles the "spawner create" action
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleSpawnerCreate(CommandSender _sender, String[] _args)
    {
        if (!(_sender instanceof Player player))
        {
            sendError(_sender, "Cette commande nécessite d'être un lancée par un joueur !");
            return false;
        }

        if (!PermissionUtilities.hasPermission(player, "fireland.essaim.admin"))
        {
            sendError(_sender, "Permissions insuffisantes !");
            return false;
        }

        if (_args.length < 9)
        {
            sendError(_sender, "Usage: /essaim spawner create <essaim> <name> <mobtype> <amount> " +
                    "<activation-delay> <spawn-delay> <affected-by-difficulty> [command...]");
            return false;
        }

        try
        {
            String essaimName = _args[2];
            String spawnerName = _args[3];
            String mobType = _args[4];
            int amount = Integer.parseInt(_args[5]);
            double activationDelay = Double.parseDouble(_args[6]);
            double spawnDelay = Double.parseDouble(_args[7]);
            boolean affectedByDifficulty = Boolean.parseBoolean(_args[8]);

            // Build command from remaining _args
            StringBuilder commandBuilder = new StringBuilder();
            for (int i = 9; i < _args.length; i++) {
                commandBuilder.append(_args[i]).append(" ");
            }
            String command = commandBuilder.toString().trim();

            m_configService.createSpawner(essaimName, spawnerName, mobType, amount,
                    activationDelay, spawnDelay, command, player.getLocation(), affectedByDifficulty);

            sendSuccess(_sender, "Spawner " + spawnerName + " créé avec succès !");
            return true;
        }
        catch (NumberFormatException e)
        {
            sendError(_sender, "Valeurs numériques invalides !");
            return false;
        }
        catch (Exception e)
        {
            sendError(_sender, "Erreur lors de la création : " + e.getMessage());
            return false;
        }
    }

    /**
     * Handles the "create" subcommand
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleCreateCommand(CommandSender _sender, String[] _args) {
        if (!(_sender instanceof Player player))
        {
            sendError(_sender, "Cette commande nécessite d'être un joueur !");
            return false;
        }

        if (!PermissionUtilities.hasPermission(player, "fireland.essaim.admin"))
        {
            sendError(_sender, "Permissions insuffisantes !");
            return false;
        }

        if (_args.length != 3)
        {
            sendError(_sender, "Usage: /essaim create <name> <region>");
            return false;
        }

        try
        {
            String essaimName = _args[1];
            String region = _args[2];
            Location location = player.getLocation();
            
            m_configService.createEssaim(essaimName, region, location);

            sendSuccess(_sender, "Essaim " + essaimName + " créé avec succès !");
            return true;
        }
        catch (Exception e)
        {
            sendError(_sender, "Erreur lors de la création : " + e.getMessage());
            return false;
        }
    }

    /**
     * Handles the "open" subcommand
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleOpenCommand(CommandSender _sender, String[] _args) {
        if (_sender instanceof Player p && !PermissionUtilities.hasPermission(p, "fireland.essaim.admin"))
        {
            sendError(_sender, "Permissions insuffisantes !");
            return false;
        }

        if (_args.length != 2)
        {
            sendError(_sender, "Usage: /essaim open <essaim>");
            return false;
        }

        String essaimName = _args[1];

        try
        {
            if (m_essaimService.enableEssaim(essaimName))
            {
                sendSuccess(_sender, "L'essaim " + essaimName + " a été ouvert avec succès !");
            }
            else
            {
                sendError(_sender, "L'essaim " + essaimName + " est déjà activé !");
            }
            return true;
        }
        catch (Exception e)
        {
            sendError(_sender, "Erreur lors de l'ouverture : " + e.getMessage());
            return false;
        }
    }

    /**
     * Handles the "close" subcommand
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleCloseCommand(CommandSender _sender, String[] _args)
    {
        if (_sender instanceof Player p && !PermissionUtilities.hasPermission(p, "fireland.essaim.admin"))
        {
            sendError(_sender, "Permissions insuffisantes !");
            return false;
        }

        if (_args.length != 2)
        {
            sendError(_sender, "Usage: /essaim close <essaim>");
            return false;
        }

        String essaimName = _args[1];

        try
        {
            if (m_essaimService.disableEssaim(essaimName))
            {
                sendSuccess(_sender, "Essaim " + essaimName + " a été fermé !");
            }
            else
            {
                m_essaimService.forceDisableEssaim(essaimName);
                sendSuccess(_sender, "Vous avez forcé la fermeture de l'essaim évènementiel " + essaimName + " !");
            }
            return true;
        }
        catch (Exception e)
        {
            sendError(_sender, "Erreur lors de la fermeture : " + e.getMessage());
            return false;
        }
    }

    /**
     * Handles the "close" subcommand
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleResetCommand(CommandSender _sender, String[] _args)
    {
        if (_sender instanceof Player p && !PermissionUtilities.hasPermission(p, "fireland.essaim.admin"))
        {
            sendError(_sender, "Permissions insuffisantes !");
            return false;
        }

        if (_args.length != 2)
        {
            sendError(_sender, "Usage: /essaim reset <essaim>");
            return false;
        }

        String essaimName = _args[1];

        try
        {
            if (m_essaimService.resetEssaim(essaimName))
            {
                sendSuccess(_sender, "Essaim " + essaimName + " a été reset !");
            }
            else
            {
                sendError(_sender, "Impossible de reset l'essaim " + essaimName);
            }
            return true;
        }
        catch (Exception e)
        {
            sendError(_sender, "Erreur lors du reset : " + e.getMessage());
            return false;
        }
    }

    /**
     * Handles the "finish" subcommand
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleFinishCommand(CommandSender _sender, String[] _args)
    {
        if (_sender instanceof Player p && !PermissionUtilities.hasPermission(p, "fireland.essaim.admin"))
        {
            sendError(_sender, "Permissions insuffisantes !");
            return false;
        }

        if (_args.length != 2)
        {
            sendError(_sender, "Usage: /essaim finish <essaim>");
            return false;
        }

        String essaimName = _args[1];

        try
        {
            m_essaimService.finishEssaim(essaimName);
            sendSuccess(_sender, "L'essaim " + essaimName + " a été fini !");
            return true;
        }
        catch (Exception e)
        {
            sendError(_sender, "Erreur lors du finish : " + e.getMessage());
            return false;
        }
    }

    /** Handles the "unfinish" subcommand
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleUnfinishCommand(CommandSender _sender, String[] _args)
    {
        if (!_sender.hasPermission("fireland.essaim.admin"))
        {
            sendError(_sender, "Permissions insuffisantes !");
            return false;
        }

        if (_args.length != 2)
        {
            sendError(_sender, "Usage: /essaim unfinish <essaim>");
            return false;
        }

        String essaimName = _args[1];

        try
        {
            m_essaimService.unfinishEssaim(essaimName);
            sendSuccess(_sender, "L'essaim " + essaimName + " n'est plus fini !");
            return true;
        }
        catch (Exception e)
        {
            sendError(_sender, "Erreur lors du unfinish : " + e.getMessage());
            return false;
        }
    }

    /**
     * Handles the "info" subcommand
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleInfoCommand(CommandSender _sender, String[] _args)
    {
        if (_sender instanceof Player p && !PermissionUtilities.hasPermission(p, "fireland.essaim.admin"))
        {
            sendError(_sender, "Permissions insuffisantes !");
            return false;
        }

        if (_args.length != 2)
        {
            sendError(_sender, "Usage: /essaim info <essaim>");
            return false;
        }

        String essaimName = _args[1];

        try
        {
            if (!m_groupManager.hasGroup(essaimName))
            {
                sendError(_sender, "L'essaim est inconnu ou inactif.");
                return false;
            }

            var group = m_groupManager.getGroup(essaimName);

            _sender.sendMessage("§8Info de l'essaim §f" + essaimName + " §8-");
            _sender.sendMessage("§8Leader: §6" + group.getLeader().getName());
            _sender.sendMessage("§8Joueurs: §f" + group.getMembers().size() + "/4");
            _sender.sendMessage("§8Difficulté: §f" + group.getDifficulty());
            if (group.hasStarted())
            {
                _sender.sendMessage("§8Début: §f" + group.getStartTime());
            }

            return true;
        }
        catch (Exception e)
        {
            sendError(_sender, "Erreur lors de la récupération d'infos : " + e.getMessage());
            return false;
        }
    }

    /**
     * Handles the "set" subcommand
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleSetCommand(CommandSender _sender, String[] _args)
    {
        if (!(_sender instanceof Player player))
        {
            sendError(_sender, "Cette commande nécessite d'être un joueur !");
            return false;
        }

        if (!PermissionUtilities.hasPermission(player, "fireland.essaim.admin"))
        {
            sendError(_sender, "Permissions insuffisantes !");
            return false;
        }

        if (_args.length != 6)
        {
            sendError(_sender, "Usage: /essaim set <point> <x> <y> <z> <essaim>");
            return false;
        }

        try
        {
            String pointType = _args[1].toLowerCase();
            double x = Double.parseDouble(_args[2]);
            double y = Double.parseDouble(_args[3]);
            double z = Double.parseDouble(_args[4]);
            String essaimName = _args[5];

            Location location = new Location(player.getWorld(), x, y, z);

            EssaimConfigService.LocationType locationType = parseLocationType(pointType);
            if (locationType == null)
            {
                sendError(_sender, "Type de point invalide ! Utilisez : hub, start, reset, entry, solo, difficulty.1, difficulty.2, difficulty.3");
                return false;
            }

            m_configService.setEssaimLocation(essaimName, locationType, location);
            sendSuccess(_sender, "Point " + pointType + " créé avec succès pour l'essaim " + essaimName + " !");
            return true;
        }
        catch (NumberFormatException e)
        {
            sendError(_sender, "Coordonnées invalides !");
            return false;
        }
        catch (Exception e)
        {
            sendError(_sender, "Erreur lors de la définition du point : " + e.getMessage());
            return false;
        }
    }

    /**
     * Handles the "join" subcommand
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleJoinCommand(CommandSender _sender, String[] _args) {
        if (!(_sender instanceof Player player))
        {
            sendError(_sender, "Cette commande nécessite d'être un joueur !");
            return false;
        }

        if (_args.length != 3 || !_args[2].equals("Wowowowowowowowowowow1234567890"))
        {
            sendError(_sender, "Commande invalide !");
            return false;
        }

        String essaimName = _args[1];

        try
        {
            if (m_fireland.getHashMapManager().isTeleporting(player.getUniqueId()))
            {
                sendError(player, "Vous êtes déjà en cours de téléportation !");
                return false;
            }

            if (!m_groupManager.hasGroup(essaimName))
            {
                sendError(player, "Ce groupe n'existe plus !");
                return false;
            }

            var group = m_groupManager.getGroup(essaimName);
            if (group.getMembers().contains(player))
            {
                sendError(player, "Vous êtes déjà dans l'essaim !");
                return false;
            }

            if (!EssaimCondition.checkEssaimConditions(m_configService, player, essaimName, ConditionScope.ALL_MEMBERS))
            {
                InGameUtilities.sendPlayerError(player, "Vous ne remplissez pas les conditions pour entrer " +
                        "dans cet essaim : " + EssaimCondition.getUnsatisfiedConditionDescription(m_configService,
                        player, essaimName, ConditionScope.ALL_MEMBERS));
                return false;
            }

            Location hubLocation = m_configService.getEssaimLocation(essaimName, EssaimConfigService.LocationType.HUB);

            InGameUtilities.teleportPlayer(player, hubLocation, 10, "gun.hub.helico", () -> {
                if (!EssaimCondition.checkEssaimConditions(m_configService, player, essaimName, ConditionScope.ALL_MEMBERS))
                {
                    InGameUtilities.sendPlayerError(player, "Vous ne remplissez pas les conditions pour entrer " +
                            "dans cet essaim : " + EssaimCondition.getUnsatisfiedConditionDescription(m_configService,
                            player, essaimName, ConditionScope.ALL_MEMBERS));
                    return false;
                }
                else if (player.isOnline() && m_groupManager.joinGroup(essaimName, player))
                {
                    InGameUtilities.sendPlayerSucces(player, "Vous avez rejoint l'essaim " + essaimName + " !");
                    return true;
                }
                else
                {
                    InGameUtilities.sendPlayerError(player, "Impossible de rejoindre l'essaim !");
                    return false;
                }
            });

            return true;
        }
        catch (Exception e)
        {
            sendError(_sender, "Erreur lors de la jonction : " + e.getMessage());
            return false;
        }
    }

    /**
     * handles the "remove" subcommand
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleRemoveCommand(CommandSender _sender, String[] _args)
    {
        if (!_sender.hasPermission("fireland.essaim.admin"))
        {
            sendError(_sender, "Permissions insuffisantes !");
            return false;
        }

        if (_args.length != 2)
        {
            sendError(_sender, "Usage: /essaim remove <essaim>");
            return false;
        }

        String essaimName = _args[1];

        try
        {
            m_configService.deleteEssaim(essaimName);
            sendSuccess(_sender, "Essaim " + essaimName + " a été supprimé !");
            return true;
        }
        catch (Exception e)
        {
            sendError(_sender, "Erreur lors de la suppression : " + e.getMessage());
            return false;
        }
    }

    /**
     * handle the "setblock" subcommand
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleSetBlockCommand(CommandSender _sender, String[] _args)
    {
        if (_sender instanceof Player p && !PermissionUtilities.hasPermission(p, "fireland.essaim.admin"))
        {
            sendError(_sender, "Permissions insuffisantes !");
            return false;
        }

        if (_args.length != 5)
        {
            sendError(_sender, "Usage: /essaim setblock <x> <y> <z> <material>");
            return false;
        }

        try
        {
            int x = Integer.parseInt(_args[1]);
            int y = Integer.parseInt(_args[2]);
            int z = Integer.parseInt(_args[3]);
            String materialName = _args[4].toUpperCase();

            Material material = Material.getMaterial(materialName);
            if (material == null)
            {
                sendError(_sender, "Matériau invalide : " + materialName);
                return false;
            }

            setBlockInEssaimWorld(x, y, z, material);
            sendSuccess(_sender, "Le block a été mis à jour avec succès !");
            return true;

        }
        catch (NumberFormatException e)
        {
            sendError(_sender, "Coordonnées invalides !");
            return false;
        }
        catch (Exception e)
        {
            sendError(_sender, "Erreur lors de la modification du block : " + e.getMessage());
            return false;
        }
    }

    /**
     * Utility method to parse location type from string
     * @param _pointType the string representation of the location type
     * @return the corresponding LocationType, or null if invalid
     */
    private EssaimConfigService.LocationType parseLocationType(String _pointType)
    {
        return switch (_pointType)
        {
            case "hub" -> EssaimConfigService.LocationType.HUB;
            case "start" -> EssaimConfigService.LocationType.START;
            case "reset" -> EssaimConfigService.LocationType.RESET;
            case "entry" -> EssaimConfigService.LocationType.ENTRY;
            case "solo" -> EssaimConfigService.LocationType.SOLO;
            case "difficulty.1" -> EssaimConfigService.LocationType.DIFFICULTY_1;
            case "difficulty.2" -> EssaimConfigService.LocationType.DIFFICULTY_2;
            case "difficulty.3" -> EssaimConfigService.LocationType.DIFFICULTY_3;
            default -> null;
        };
    }

    /**
     * Utility method to set a block in the "essaim" world
     * @param _x the x coordinate
     * @param _y the y coordinate
     * @param _z the z coordinate
     * @param _material the material to set
     */
    private void setBlockInEssaimWorld(int _x, int _y, int _z, Material _material)
    {
        World essaimWorld = Bukkit.getWorld("essaim");
        if (essaimWorld != null)
        {
            Location blockLocation = new Location(essaimWorld, _x, _y, _z);
            blockLocation.getBlock().setType(_material);
        }
    }

    /**
     * handle "forcejoin" subcommand
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleForceJoinCommand(CommandSender _sender, String[] _args)
    {
        if (_sender instanceof Player p && !PermissionUtilities.hasPermission(p, "fireland.essaim.admin"))
        {
            sendError(_sender, "Permissions insuffisantes !");
            return false;
        }

        if (_args.length != 3)
        {
            sendError(_sender, "Usage: /essaim forcejoin <essaim> <player>");
            return false;
        }

        String essaimName = _args[1];
        String playerName = _args[2];

        try
        {
            Player targetPlayer = m_fireland.getServer().getPlayer(playerName);
            if (targetPlayer == null)
            {
                sendError(_sender, "Le joueur " + playerName + " n'est pas connecté !");
                return false;
            }

            if (!m_essaimService.isEssaimActive(essaimName))
            {
                sendError(_sender, "L'essaim " + essaimName + " n'est pas actif !");
                return false;
            }

            if (!m_groupManager.hasGroup(essaimName))
            {
                sendError(_sender, "Aucun groupe n'existe pour l'essaim " + essaimName + " !");
                return false;
            }

            EssaimGroup group = m_groupManager.getGroup(essaimName);

            if (m_groupManager.isPlayerInAnyGroup(targetPlayer))
            {
                Optional<EssaimGroup> currentGroup = m_groupManager.findPlayerGroup(targetPlayer);
                if (currentGroup.isPresent())
                {
                    String currentEssaim = findEssaimNameByGroup(currentGroup.get());
                    if (currentEssaim != null && !currentEssaim.equals(essaimName))
                    {
                        m_groupManager.leaveGroup(currentEssaim, targetPlayer);
                        InGameUtilities.sendPlayerInformation(targetPlayer,
                                "§eVous avez été retiré de votre groupe précédent par un administrateur.");
                    }
                    else if (currentEssaim != null)
                    {
                        sendError(_sender, "Le joueur " + playerName + " est déjà dans cet essaim !");
                        return false;
                    }
                }
            }


            boolean joinResult = m_groupManager.joinGroup(essaimName, targetPlayer);

            if (joinResult)
            {
                if (group.hasStarted())
                {
                    Location hubLocation = m_configService.getEssaimLocation(essaimName, EssaimConfigService.LocationType.HUB);
                    targetPlayer.teleport(hubLocation);
                }
                else
                {
                    targetPlayer.teleport(m_groupManager.getGroup(essaimName).getLeader().getLocation());
                }

                sendSuccess(_sender, "Le joueur " + playerName + " a été forcé à rejoindre l'essaim " + essaimName + " !");
                InGameUtilities.sendPlayerInformation(targetPlayer,
                        "§aVous avez été ajouté à l'essaim " + essaimName + " par un administrateur !");

                for (Player member : group.getMembers())
                {
                    if (!member.equals(targetPlayer))
                    {
                        InGameUtilities.sendPlayerInformation(member,
                                "§e" + targetPlayer.getName() + " a été ajouté au groupe par un administrateur.");
                    }
                }
            }
            else
            {
                sendError(_sender, "Impossible d'ajouter le joueur au groupe : " + joinResult);
            }

            return true;
        }
        catch (Exception e)
        {
            sendError(_sender, "Erreur lors de l'ajout forcé : " + e.getMessage());
            m_fireland.getLogger().severe("Error in forcejoin command: " + e.getMessage());
            return false;
        }
    }

    /**
     * handle "forcekick" subcommand
     * @param _sender the command sender
     * @param _args command arguments
     * @return true if the command was successful, false otherwise
     */
    private boolean handleForceKickCommand(CommandSender _sender, String[] _args)
    {
        if (_sender instanceof Player p && !PermissionUtilities.hasPermission(p, "fireland.essaim.admin"))
        {
            sendError(_sender, "Permissions insuffisantes !");
            return false;
        }

        if (_args.length != 2)
        {
            sendError(_sender, "Usage: /essaim forcekick <player>");
            return false;
        }

        String playerName = _args[1];

        try
        {
            Player targetPlayer = m_fireland.getServer().getPlayer(playerName);
            if (targetPlayer == null)
            {
                sendError(_sender, "Le joueur " + playerName + " n'est pas connecté !");
                return false;
            }

            Optional<EssaimGroup> playerGroupOpt = m_groupManager.findPlayerGroup(targetPlayer);
            if (playerGroupOpt.isEmpty())
            {
                sendError(_sender, "Le joueur " + playerName + " n'est dans aucun essaim !");
                return false;
            }

            EssaimGroup group = playerGroupOpt.get();
            String essaimName = findEssaimNameByGroup(group);

            if (essaimName == null)
            {
                sendError(_sender, "Impossible de trouver l'essaim du joueur !");
                return false;
            }

            m_essaimManager.getEssaimService().getExitService().exitPlayer(targetPlayer, group, false);

            m_groupManager.leaveGroup(essaimName, targetPlayer);

            sendSuccess(_sender, "Le joueur " + playerName + " a été exclu de l'essaim " + essaimName + " !");
            InGameUtilities.sendPlayerInformation(targetPlayer,
                    "§cVous avez été exclu de l'essaim par un administrateur.");

            return true;
        }
        catch (Exception e)
        {
            sendError(_sender, "Erreur lors de l'exclusion forcée : " + e.getMessage());
            m_fireland.getLogger().severe("Error in forcekick command: " + e.getMessage());
            return false;
        }
    }

    /**
     * Utility method to find the essaim name by its group
     * @param _group the EssaimGroup
     * @return the essaim name, or null if not found
     */
    private String findEssaimNameByGroup(EssaimGroup _group)
    {
        return m_groupManager.getAllGroups().entrySet().stream()
                .filter(entry -> entry.getValue().equals(_group))
                .map(java.util.Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
