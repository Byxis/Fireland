package fr.byxis.player.items.water;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Manages the thirst system for players.
 * <p>
 * This system tracks player hydration levels using the experience bar, allowing
 * players to drink water bottles to restore thirst. Clean water (blue potions)
 * provide more hydration than dirty water, which can also apply negative
 * effects.
 */
public class Thirst implements Listener, CommandExecutor
{

    private final Fireland m_fireland;

    /**
     * Constructs a new Thirst handler.
     *
     * @param _main
     *            Main instance of the Fireland plugin
     */
    public Thirst(Fireland _main)
    {
        this.m_fireland = _main;
    }

    /**
     * Handles the /thirst command for managing player thirst levels.
     * <p>
     * Usage:
     * <ul>
     * <li>/thirst - Reset your thirst to 100</li>
     * <li>/thirst [value] - Set your thirst to a specific value (0-100)</li>
     * <li>/thirst [player] - Reset target player's thirst to 100</li>
     * <li>/thirst [player] [value] - Set target player's thirst to a specific
     * value</li>
     * </ul>
     *
     * @param _sender
     *            The command sender
     * @param _cmd
     *            The command
     * @param _msg
     *            The command label
     * @param _args
     *            The command arguments
     *
     * @return true if command was handled, false otherwise
     */
    @Override
    public boolean onCommand(@NotNull CommandSender _sender, @NotNull Command _cmd, @NotNull String _msg, String @NotNull [] _args)
    {
        if (_sender instanceof Player p && p.hasPermission("fireland.thirst.admin"))
        {
            if (_cmd.getName().equalsIgnoreCase("thirst"))
            {
                FileConfiguration config = m_fireland.getCfgm().getPlayerDB();
                float thirst;

                if (_args.length == 0)
                {
                    thirst = 100f;
                    config.set("thirst." + p.getUniqueId(), thirst);
                    m_fireland.getCfgm().savePlayerDB();
                    p.setExp(thirst * 0.01f);
                    return true;
                }
                else if (_args.length == 1)
                {
                    try
                    {
                        float value = Float.parseFloat(_args[0]);
                        if (value < 0 || value > 100)
                        {
                            p.sendMessage("§cLa valeur entrée doit être comprise entre 0 et 100 !");
                            return true;
                        }
                        thirst = value;
                        config.set("thirst." + p.getUniqueId(), thirst);
                        m_fireland.getCfgm().savePlayerDB();
                        p.setExp(thirst * 0.01f);
                        return true;
                    }
                    catch (NumberFormatException e)
                    {
                        UUID targetUuid = BasicUtilities.getUuid(_args[0]);
                        if (targetUuid != null)
                        {
                            config.set("thirst." + targetUuid, 100);
                            m_fireland.getCfgm().savePlayerDB();
                            p.sendMessage("§aThirst reset to 100 for " + _args[0]);
                            return true;
                        }
                        else
                        {
                            p.sendMessage("§cPlayer not found!");
                            return true;
                        }
                    }
                }
                else if (_args.length == 2)
                {
                    UUID targetUuid = BasicUtilities.getUuid(_args[0]);
                    if (targetUuid == null)
                    {
                        p.sendMessage("§cPlayer not found!");
                        return true;
                    }

                    try
                    {
                        float value = Float.parseFloat(_args[1]);
                        if (value < 0 || value > 100)
                        {
                            p.sendMessage("§cLa valeur entrée doit être comprise entre 0 et 100 !");
                            return true;
                        }

                        config.set("thirst." + targetUuid, value);
                        m_fireland.getCfgm().savePlayerDB();

                        Player target = Bukkit.getPlayer(targetUuid);
                        if (target != null)
                        {
                            target.setExp(value * 0.01f);
                        }

                        p.sendMessage("§aThirst set to " + value + " for " + _args[0]);
                        return true;
                    }
                    catch (NumberFormatException e)
                    {
                        p.sendMessage("§cInvalid number: " + _args[1]);
                        return true;
                    }
                }
                else
                {
                    p.sendMessage("§cMauvaise formulation de la commande ! (/thirst [player] [nombre])");
                }
            }
        }
        return false;
    }

    /**
     * Initializes thirst level for new players on first join.
     * <p>
     * Sets thirst to 100 if the player doesn't have a thirst value stored.
     *
     * @param _e
     *            The player join event
     */
    @EventHandler
    public void onPlayerfirstJoin(PlayerJoinEvent _e)
    {
        Player p = _e.getPlayer();
        if (!m_fireland.getCfgm().getPlayerDB().contains("thirst." + p.getUniqueId().toString()))
        {
            m_fireland.getCfgm().getPlayerDB().set("thirst." + p.getUniqueId().toString(), 100);
            m_fireland.getCfgm().savePlayerDB();
        }
    }

    /**
     * Prevents natural health regeneration when thirst is too low.
     * <p>
     * Cancels the SATIATED health regeneration if thirst is at or below 10.
     *
     * @param _event
     *            The entity regain health event
     */
    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent _event)
    {
        if (_event.getRegainReason() == RegainReason.SATIATED && _event.getEntity() instanceof Player p)
        {
            FileConfiguration config = m_fireland.getCfgm().getPlayerDB();

            Float thirst = (float) config.getDouble("thirst." + p.getUniqueId());

            if (config.getString("thirst." + p.getUniqueId()) != null)
            {
                if (thirst <= 10)
                {
                    _event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Handles player drinking water bottles.
     * <p>
     * Drinking behavior:
     * <ul>
     * <li>Clean water (blue/null color): +35 thirst, no side effects</li>
     * <li>Dirty water (other colors): +10 thirst, applies slowness, hunger, and
     * potentially wither</li>
     * </ul>
     *
     * @param _e
     *            The player item consume event
     */
    @EventHandler
    public void playerDrink(PlayerItemConsumeEvent _e)
    {
        Player p = _e.getPlayer();
        FileConfiguration config = m_fireland.getCfgm().getPlayerDB();
        ItemStack i = _e.getItem();

        config.getDouble("thirst." + p.getUniqueId());
        float thirst;
        if ((i.getType() == Material.POTION && _e.getItem().getDurability() == 0))
        {
            if (((PotionMeta) i.getItemMeta()).getColor() == null || ((PotionMeta) i.getItemMeta()).getColor().getBlue() >= 100)
            {
                // Clean water
                if (config.getString("thirst." + p.getUniqueId()) != null && config.getInt("thirst." + p.getUniqueId()) <= 75)
                {
                    config.set("thirst." + p.getUniqueId(), config.getDouble("thirst." + p.getUniqueId()) + 35);
                    m_fireland.getCfgm().savePlayerDB();
                }
                else
                {
                    thirst = 100f;
                    config.set("thirst." + p.getUniqueId(), thirst);
                    m_fireland.getCfgm().savePlayerDB();
                    p.setExp(thirst * 0.01f);
                }
            }
            else
            {
                // Dirty water
                if (config.getString("thirst." + p.getUniqueId()) != null && config.getInt("thirst." + p.getUniqueId()) <= 75)
                {
                    config.set("thirst." + p.getUniqueId(), config.getDouble("thirst." + p.getUniqueId()) + 10);
                    m_fireland.getCfgm().savePlayerDB();
                }
                else
                {
                    thirst = 100f;
                    config.set("thirst." + p.getUniqueId(), thirst);
                    m_fireland.getCfgm().savePlayerDB();
                    p.setExp(thirst * 0.01f);
                }
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 60, 0, true, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 60, 1, true, false));
                if (Math.random() > 0.5)
                {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 20, 1, true, false));
                }
            }

        }
    }

    /**
     * Resets player thirst to 100 on respawn.
     *
     * @param _e
     *            The player respawn event
     */
    @EventHandler
    public void playerDeath(PlayerRespawnEvent _e)
    {
        Player p = _e.getPlayer();
        FileConfiguration config = m_fireland.getCfgm().getPlayerDB();

        config.set("thirst." + p.getUniqueId(), 100);
        m_fireland.getCfgm().savePlayerDB();
        p.setExp(100 * 0.01f);
    }

}
