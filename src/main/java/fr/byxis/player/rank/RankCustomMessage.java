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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static fr.byxis.fireland.utilities.InGameUtilities.sendEveryoneCustomText;
import static fr.byxis.fireland.utilities.ListUtilities.filterSuggestions;
import static fr.byxis.fireland.utilities.ListUtilities.tabList;

public class RankCustomMessage implements Listener, CommandExecutor, TabCompleter {

    private final Fireland main;
    private final RankConfig config;
    private final List<String> ranks = Arrays.asList("admin", "veteran", "stratege", "mercenaire");

    public RankCustomMessage(Fireland _main) {
        this.main = _main;
        config = new RankConfig(_main);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerDeath(PlayerDeathEvent e) {
        Player victim = e.getPlayer();
        EntityDamageEvent lastDamage = victim.getLastDamageCause();

        if (lastDamage == null) return;

        String rank = getPlayerRank(victim);
        if (rank == null) return;

        String deathMessage = getDeathMessage(victim, lastDamage, rank);
        if (deathMessage != null && !deathMessage.isEmpty()) {
            e.setDeathMessage(deathMessage);
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        sendEveryoneCustomText("§8[§a+§8] " + player.getName());

        String rank = getPlayerRank(player);
        if (rank != null) {
            sendRankMessage(rank, "JOIN", player.getName());
        }
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        sendEveryoneCustomText("§8[§c-§8] " + player.getName());

        String rank = getPlayerRank(player);
        if (rank != null) {
            sendRankMessage(rank, "LEAVE", player.getName());
        }
    }

    private String getPlayerRank(Player player)
    {
        for (String rank : ranks) {
            if (player.hasPermission("fireland.message." + rank)) {
                return rank;
            }
        }
        return null;
    }

    private String getDeathMessage(Player victim, EntityDamageEvent lastDamage, String rank) {
        Entity killer = lastDamage.getEntity();
        EntityDamageEvent.DamageCause cause = lastDamage.getCause();

        if (killer instanceof Player) {
            return formatMessage(rank, "PLAYERKILL", victim.getName(), killer.getName());
        }

        if (main.getCfgm().getPlayerDB().getBoolean("infected." + victim.getUniqueId() + ".state") && killer == null) {
            return formatMessage(rank, "INFECTION", victim.getName(), null);
        }

        return formatMessage(rank, cause.name(), victim.getName(), null);
    }

    private String formatMessage(String rank, String messageType, String playerName, String killerName) {
        String message = config.getConfig().getString(rank + "." + messageType);

        if (message == null || message.isEmpty()) {
            return null;
        }

        message = message.replace("player", playerName);
        if (killerName != null) {
            message = message.replace("killer", killerName);
        }

        return message;
    }

    private void sendRankMessage(String rank, String messageType, String playerName) {
        String message = formatMessage(rank, messageType, playerName, null);
        if (message != null && !message.isEmpty()) {
            sendEveryoneCustomText(message);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player) || !player.hasPermission("fireland.admin")) {
            return false;
        }

        if (strings.length == 1) {
            initializeRankMessages(strings[0]);
            InGameUtilities.sendPlayerSucces(player, "Les messages ont été initialisés pour le rang " + strings[0] + ".");
            return true;
        }

        // Mise à jour d'un message spécifique
        if (strings.length > 3) {
            updateMessage(strings, player);
            return true;
        }

        return false;
    }

    private void initializeRankMessages(String rank) {
        List<String> messageTypes = new ArrayList<>();

        for (EntityDamageEvent.DamageCause cause : EntityDamageEvent.DamageCause.values()) {
            messageTypes.add(cause.name());
        }

        messageTypes.addAll(Arrays.asList("INFECTION", "PLAYERKILL", "JOIN", "LEAVE"));

        for (String messageType : messageTypes) {
            String path = rank + "." + messageType;
            if (!config.getConfig().contains(path)) {
                config.getConfig().set(path, "");
            }
        }

        config.save();
    }

    private void updateMessage(String[] args, Player player) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(args[2].replace("&", "§"));

        for (int i = 3; i < args.length; i++) {
            messageBuilder.append(" ").append(args[i].replace("&", "§"));
        }

        String path = args[0] + "." + args[1];
        config.getConfig().set(path, messageBuilder.toString());
        config.save();

        InGameUtilities.sendPlayerSucces(player, "Le message " + path + " a été mis à jour avec succès.");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player) || !player.hasPermission("fireland.admin")) {
            return new ArrayList<>();
        }

        List<String> suggestions = new ArrayList<>();

        switch (strings.length) {
            case 1:
                suggestions.addAll(filterSuggestions(strings[0], ranks));
                break;
            case 2:
                List<String> messageTypes = new ArrayList<>();
                for (EntityDamageEvent.DamageCause cause : EntityDamageEvent.DamageCause.values()) {
                    messageTypes.add(cause.name());
                }
                messageTypes.addAll(Arrays.asList("INFECTION", "PLAYERKILL", "JOIN", "LEAVE"));
                suggestions.addAll(filterSuggestions(strings[1], messageTypes));
                break;
            default:
                suggestions.add("<message>");
                break;
        }

        return suggestions;
    }
}