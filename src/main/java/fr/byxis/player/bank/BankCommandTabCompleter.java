package fr.byxis.player.bank;

import fr.byxis.fireland.Fireland;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BankCommandTabCompleter implements TabCompleter {

    private final Fireland main;

    public BankCommandTabCompleter(Fireland _main) {
        this.main = _main;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String arg, @NotNull String[] args) {
        List<String> l = new ArrayList<>();

        if (cmd.getName().equalsIgnoreCase("bank")) {
            if (args.length == 1) {
                if (sender.hasPermission("fireland.command.bank.set")) {
                    l.add("set");
                }

                if (sender.hasPermission("fireland.command.bank.see")) {
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        l.add(player.getName());
                    }
                    l.add("--Nom du joueur à consulter");
                }
            }
            else if (args.length >= 2) {
                if (args[0].equalsIgnoreCase("set") && sender.hasPermission("fireland.command.bank.set")) {
                    if (args.length == 2) {
                        l.add("money");
                        l.add("upgrade");
                        l.add("--Type de modification");
                    }
                    else if (args.length == 3) {
                        if (args[1].equalsIgnoreCase("money")) {
                            l.add("1000");
                            l.add("5000");
                            l.add("10000");
                            l.add("25000");
                            l.add("50000");
                            l.add("100000");
                            l.add("--Montant d'argent");
                        }
                        else if (args[1].equalsIgnoreCase("upgrade")) {
                            l.add("0");
                            l.add("1");
                            l.add("2");
                            l.add("3");
                            l.add("4");
                            l.add("5");
                            l.add("6");
                            l.add("7");
                            l.add("--Niveau d'amélioration");
                        }
                    }
                    else if (args.length == 4) {
                        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                            l.add(player.getName());
                        }
                        l.add("--Joueur cible");
                    }
                }
            }
        }

        return l;
    }
}