package fr.byxis.player.items.water;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
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

public class Thirst implements Listener, CommandExecutor
{
    
    private final Fireland main;

    public Thirst(Fireland _main)
    {
        this.main = _main;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if (sender instanceof Player p && p.hasPermission("fireland.thirst.admin")) {
            if (cmd.getName().equalsIgnoreCase("thirst")) {
                FileConfiguration config = main.getCfgm().getPlayerDB();
                float thirst;
                
                if (args.length == 0) {
                    if (config.getString("thirst." + p.getUniqueId()) == null) {
                        config.set("thirst." + p.getUniqueId(), 100);
                        main.getCfgm().savePlayerDB();
                        return true;
                    } else {
                        thirst = 100f;
                        config.set("thirst." + p.getUniqueId(), thirst);
                        main.getCfgm().savePlayerDB();
                        p.setExp(thirst * 0.01f);
                        return true;
                    }
                }
                else if (args.length == 1)
                {
                    if (Bukkit.getOfflinePlayer(BasicUtilities.getUuid(args[0])) != null)
                    {
                        config.set("thirst." + BasicUtilities.getUuid(args[0]), 100);
                        main.getCfgm().savePlayerDB();
                        return true;
                    }
                    if (Float.parseFloat(args[0]) < 0 || Float.parseFloat(args[0]) > 100) {
                        p.sendMessage("§cLa valeur entrée doit être comprise entre 0 et 100 !");
                        return true;
                    }
                    else
                    {
                        if (config.getString("thirst." + p.getUniqueId()) == null) {
                            config.set("thirst." + p.getUniqueId(), args[0]);
                            main.getCfgm().savePlayerDB();
                            return true;
                        } else {
                            thirst = Float.parseFloat(args[0]);
                            config.set("thirst." + p.getUniqueId(), thirst);
                            main.getCfgm().savePlayerDB();
                            p.setExp(thirst * 0.01f);
                            return true;
                        }
                    }
                }
                else if (args.length == 2)
                {
                    Player victim =  (Player) Bukkit.getOfflinePlayer(BasicUtilities.getUuid(args[1]));
                    String playerData = "thirst." + victim.getUniqueId().toString();
                    config.set(playerData, Integer.valueOf(args[1]));
                    main.getCfgm().savePlayerDB();
                    p.setExp(Integer.parseInt(args[1]) * 0.01f);
                    return true;
                    
                }
                else
                {
                    p.sendMessage("§cMauvaise formulation de la commande ! (/thirst [player] [nombre]");
                }
            }
        }
        return false;
    }
    
    @EventHandler
    public void onPlayerfirstJoin(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        if (!main.getCfgm().getPlayerDB().contains("thirst." + p.getUniqueId().toString()))
        {
            main.getCfgm().getPlayerDB().set("thirst." + p.getUniqueId().toString(), 100);
            main.getCfgm().savePlayerDB();
        }
    }
    
    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (event.getRegainReason() == RegainReason.SATIATED && event.getEntity() instanceof Player p)
        {
            FileConfiguration config = main.getCfgm().getPlayerDB();
            
            Float thirst = (float) config.getDouble("thirst." + p.getUniqueId());
            
            if (config.getString("thirst." + p.getUniqueId()) != null) {
                if (thirst <= 10) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @SuppressWarnings({ "deprecation" })
    @EventHandler
    public void playerDrink(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        FileConfiguration config = main.getCfgm().getPlayerDB();
        ItemStack i = e.getItem();
        
        Float thirst = (float) config.getDouble("thirst." + p.getUniqueId());
        if ((i.getType() == Material.POTION && e.getItem().getDurability() == 0)) {
            if (((PotionMeta) i.getItemMeta()).getColor() == null || ((PotionMeta) i.getItemMeta()).getColor().getBlue() >= 100)
            {
                if (config.getString("thirst." + p.getUniqueId()) != null && config.getInt("thirst." + p.getUniqueId()) <= 75) {
                    config.set("thirst." + p.getUniqueId(), config.getDouble("thirst." + p.getUniqueId()) + 35);
                    main.getCfgm().savePlayerDB();
                } else {
                    thirst = 100f;
                    config.set("thirst." + p.getUniqueId(), thirst);
                    main.getCfgm().savePlayerDB();
                    p.setExp(thirst * 0.01f);
                }
            }
            else
            {
                if (config.getString("thirst." + p.getUniqueId()) != null && config.getInt("thirst." + p.getUniqueId()) <= 75) {
                    config.set("thirst." + p.getUniqueId(), config.getDouble("thirst." + p.getUniqueId()) + 10);
                    main.getCfgm().savePlayerDB();
                }
                else
                {
                    thirst = 100f;
                    config.set("thirst." + p.getUniqueId(), thirst);
                    main.getCfgm().savePlayerDB();
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
    
    @EventHandler
    public void playerDeath(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        FileConfiguration config = main.getCfgm().getPlayerDB();
        
        config.set("thirst." + p.getUniqueId(), 100);
        main.getCfgm().savePlayerDB();
        p.setExp(100 * 0.01f);
    }

    public void updateThirst(Player p)
    {
        FileConfiguration config = main.getCfgm().getPlayerDB();
        Float thirst = (float) config.getDouble("thirst." + p.getUniqueId());
        if (config.getString("thirst." + p.getUniqueId()) == null) {
            config.set("thirst." + p.getUniqueId(), 100);
            main.getCfgm().savePlayerDB();
            thirst = 100f;
            p.setExp(thirst * 0.01f);

        } else if (thirst <= 0f) {
            p.setExp(0);
        } else {
            thirst = thirst - 0.005f;
            config.set("thirst." + p.getUniqueId(), thirst);
            main.getCfgm().savePlayerDB();
            if (thirst * 0.01f > 1) {
                p.setExp(1);
            } else if (thirst * 0.01f < 0) {
                p.setExp(0);
            } else {
                p.setExp(thirst * 0.01f);
            }
        }
    }
}
