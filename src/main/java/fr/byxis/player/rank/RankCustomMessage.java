package fr.byxis.player.rank;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static fr.byxis.fireland.utilities.InGameUtilities.sendEveryoneCustomText;
import static fr.byxis.fireland.utilities.ListUtilities.tabList;

public class RankCustomMessage implements Listener, CommandExecutor, TabCompleter {

    private final Fireland main;
    private final RankConfig config;

    public RankCustomMessage(Fireland _main) {
        this.main = _main;
        config = new RankConfig(_main);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerDeath(PlayerDeathEvent e)
    {
        Entity ent = e.getEntity();
        EntityDamageEvent ede = ent.getLastDamageCause();
        if (ede == null)
        {
            return;
        }
        EntityDamageEvent.DamageCause dc = ede.getCause();
        Player p = (Player) ent;
        ArrayList<String> grades = new ArrayList<String>();
        grades.add("admin");
        grades.add("veteran");
        grades.add("stratege");
        grades.add("mercenaire");
        for (String grade : grades)
        {
            if (p.hasPermission("fireland.message." + grade)) {
                if (e.getEntity().getLastDamageCause().getEntity() != null) {
                    e.setDeathMessage(config.getConfig().getString(grade + ".PLAYERKILL").replace("player", e.getPlayer().getName()).replace("killer", p.getLastDamageCause().getEntity().getName()));
                } else if (main.getCfgm().getPlayerDB().getBoolean("infected." + p.getUniqueId() + ".state") && e.getEntity().getKiller() == null) {
                    e.setDeathMessage(config.getConfig().getString(grade + ".INFECTION").replace("player", e.getPlayer().getName()));
                }
                e.setDeathMessage(config.getConfig().getString(grade + "." + dc).replace("player", e.getPlayer().getName()));
                break;
            }
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        sendEveryoneCustomText("§8[§a+§8] " + p.getName());
        if (p.hasPermission("fireland.message.admin") && config.getConfig().getString("admin.JOIN") != null && !config.getConfig().getString("admin.JOIN").equals(""))
        {
            sendEveryoneCustomText(config.getConfig().getString("admin.JOIN").replace("player", e.getPlayer().getName()));
        }
        else if (p.hasPermission("fireland.message.veteran") && config.getConfig().getString("veteran.JOIN") != null && !config.getConfig().getString("veteran.JOIN").equals(""))
        {
            sendEveryoneCustomText(config.getConfig().getString("veteran.JOIN").replace("player", e.getPlayer().getName()));
        }
        else if (p.hasPermission("fireland.message.stratege") && config.getConfig().getString("stratege.JOIN") != null && !config.getConfig().getString("stratege.JOIN").equals(""))
        {
            sendEveryoneCustomText(config.getConfig().getString("stratege.JOIN").replace("player", e.getPlayer().getName()));
        }
        else if (p.hasPermission("fireland.message.mercenaire") && config.getConfig().getString("mercenaire.JOIN") != null && !config.getConfig().getString("mercenaire.JOIN").equals(""))
        {
            sendEveryoneCustomText(config.getConfig().getString("mercenaire.JOIN").replace("player", e.getPlayer().getName()));
        }
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        sendEveryoneCustomText("§8[§c-§8] " + p.getName());
        if (p.hasPermission("fireland.message.admin") && config.getConfig().getString("admin.LEAVE") != null && !config.getConfig().getString("admin.LEAVE").equals(""))
        {
            sendEveryoneCustomText(config.getConfig().getString("admin.LEAVE").replace("player", e.getPlayer().getName()));
        }
        else if (p.hasPermission("fireland.message.veteran") && config.getConfig().getString("veteran.LEAVE") != null && !config.getConfig().getString("veteran.LEAVE").equals(""))
        {
            sendEveryoneCustomText(config.getConfig().getString("veteran.LEAVE").replace("player", e.getPlayer().getName()));
        }
        else if (p.hasPermission("fireland.message.stratege") && config.getConfig().getString("stratege.LEAVE") != null && !config.getConfig().getString("stratege.LEAVE").equals(""))
        {
            sendEveryoneCustomText(config.getConfig().getString("stratege.LEAVE").replace("player", e.getPlayer().getName()));
        }
        else if (p.hasPermission("fireland.message.mercenaire") && config.getConfig().getString("mercenaire.LEAVE") != null && !config.getConfig().getString("mercenaire.LEAVE").equals(""))
        {
            sendEveryoneCustomText(config.getConfig().getString("mercenaire.LEAVE").replace("player", e.getPlayer().getName()));
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player p && p.hasPermission("fireland.admin") && strings.length > 3)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(strings[2].replace("&", "§"));
            for (int i = 3; i < strings.length; i++)
            {
                sb.append(" " + strings[i].replace("&", "§"));
            }
            config.getConfig().set(strings[0] + "." + strings[1], sb.toString());
            config.save();
            InGameUtilities.sendPlayerSucces(p, "Le message a été mis à jour avec succès.");
        }
        if (commandSender instanceof Player p && p.hasPermission("fireland.admin") && strings.length == 1)
        {
            Collection<String> l = tabList("", "", EntityDamageEvent.DamageCause.values());
            ArrayList<String> more = new ArrayList<String>();
            more.addAll(l);
            more.add("INFECTION");
            more.add("PLAYERKILL");
            more.add("JOIN");
            more.add("LEAVE");
            for (String str : more)
            {

                if (!config.getConfig().contains(strings[0] + "." + str))
                {
                    config.getConfig().set(strings[0] + "." + str, "");
                }
            }
            config.save();
            InGameUtilities.sendPlayerSucces(p, "Les messages ont été initialisés.");
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Collection<String> l = new ArrayList<String>();
        if (commandSender instanceof Player p && p.hasPermission("fireland.admin"))
        {
            if (strings.length == 1)
            {
                ArrayList<String> more = new ArrayList<String>();
                more.add("admin");
                more.add("veteran");
                more.add("stratege");
                more.add("mercenaire");
                l = tabList(strings[0], "-Group", more);
            }
            else if (strings.length == 2)
            {
                l = tabList(strings[1], "", EntityDamageEvent.DamageCause.values());
                ArrayList<String> more = new ArrayList<String>();
                more.addAll(l);
                more.add("INFECTION");
                more.add("PLAYERKILL");
                more.add("JOIN");
                more.add("LEAVE");
                l = tabList(strings[1], "-Type", more);
            }
            else if (strings.length >= 2)
            {
                l.add("-Text");
            }
        }
        List<String> list = new ArrayList<String>();
        list.addAll(l);
        return list;
    }
}
