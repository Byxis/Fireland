package fr.byxis.player.bank;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static fr.byxis.fireland.Fireland.getEco;
import static fr.byxis.fireland.utilities.InventoryUtilities.setItemMetaLore;

public class Bank implements Listener, CommandExecutor {
    private final Fireland main;

    public Bank(Fireland main) {
        this.main = main;
    }
    private static final Map<UUID, BankStorage> bankStorages = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if (!cmd.getName().equalsIgnoreCase("bank")) return false;

        int length = args.length;

        if (player.hasPermission("fireland.command.bank.see") && length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(BasicUtilities.getUuid(args[0]));
            if (target != null && target.hasPlayedBefore()) {
                openBank(target, player);
            }
        } else if (!player.hasPermission("fireland.command.bank.set") || length == 0) {
            openBankMenu(player);
            return true;
        } else if (player.hasPermission("fireland.command.bank.set")) {
            if (length == 3) {
                if (args[1].equalsIgnoreCase("money")) {
                    BankAccount account = new BankAccount(main, player.getUniqueId().toString());
                    account.setMoney(Integer.parseInt(args[2]));
                    player.sendMessage("§aVous avez maintenant " + args[2] + " dans votre banque !");
                    return true;
                } else if (args[1].equalsIgnoreCase("upgrade")) {
                    BankAccount account = new BankAccount(main, player.getUniqueId().toString());
                    account.setUpgradeLevel(Integer.parseInt(args[2]));
                    player.sendMessage("§aVous avez maintenant l'amélioration " + args[2] + " !");
                    return true;
                }
            } else if (length == 4) {
                if (args[1].equalsIgnoreCase("money")) {
                    Player target = Bukkit.getPlayer(BasicUtilities.getUuid(args[3]));
                    if (target != null) {
                        BankAccount account = new BankAccount(main, target.getUniqueId().toString());
                        account.setMoney(Integer.parseInt(args[2]));
                        player.sendMessage("§aLe joueur " + args[3] + " a maintenant " + args[2] + " dans sa banque !");
                    }
                    return true;
                } else if (args[1].equalsIgnoreCase("upgrade")) {
                    Player target = Bukkit.getPlayer(BasicUtilities.getUuid(args[3]));
                    if (target != null) {
                        BankAccount account = new BankAccount(main, target.getUniqueId().toString());
                        account.setUpgradeLevel(Integer.parseInt(args[2]));
                        player.sendMessage("§aLe joueur " + args[3] + " a maintenant l'amélioration " + args[2] + " !");
                    }
                    return true;
                }
            } else {
                player.sendMessage("§cUsage : /bank set <upgrade/money> [player]");
            }
        }

        return true;
    }

    @EventHandler
    public void itemClickEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack current = e.getCurrentItem();
        String inventoryTitle = e.getView().getTitle();

        if (current == null) return;

        // Seule la gestion du menu principal de la banque (pas les pages de stockage)
        if (inventoryTitle.contains("Votre argent :") && !inventoryTitle.contains("Stockage")) {
            e.setCancelled(true);

            BankAccount account = new BankAccount(main, player.getUniqueId().toString());
            int playerBankMoney = account.getMoney();
            double playerMoney = getEco().getBalance(player);

            if (current.getType().equals(Material.GOLD_INGOT)) {
                if (e.getClick().isLeftClick()) {
                    if (e.isShiftClick()) {
                        int max = account.getMaxMoney();
                        if (playerBankMoney >= max) {
                            return;
                        }

                        if (playerMoney + playerBankMoney < max) {
                            getEco().withdrawPlayer(player, playerMoney);
                            InGameUtilities.playPlayerSound(player, "gun.hud.money_drop", SoundCategory.AMBIENT, 1, 1);
                            account.setMoney(playerBankMoney + (int) playerMoney);
                            openBankMenu(player);
                        } else {
                            getEco().withdrawPlayer(player, (max - playerBankMoney));
                            InGameUtilities.playPlayerSound(player, "gun.hud.money_drop", SoundCategory.AMBIENT, 1, 1);
                            account.setMoney(max);
                            openBankMenu(player);
                        }
                    } else if (playerMoney >= 100 && playerBankMoney + 100 <= account.getMaxMoney()) {
                        getEco().withdrawPlayer(player, 100);
                        InGameUtilities.playPlayerSound(player, "gun.hud.money_drop", SoundCategory.AMBIENT, 1, 1);
                        account.setMoney(playerBankMoney + 100);
                        openBankMenu(player);
                    }
                } else if (e.getClick().isRightClick()) {
                    if (e.isShiftClick()) {
                        getEco().depositPlayer(player, playerBankMoney);
                        InGameUtilities.playPlayerSound(player, "gun.hud.money_pickup", SoundCategory.AMBIENT, 1, 1);
                        account.setMoney(0);
                        openBankMenu(player);
                    } else if (playerBankMoney >= 100) {
                        getEco().depositPlayer(player, 100);
                        InGameUtilities.playPlayerSound(player, "gun.hud.money_pickup", SoundCategory.AMBIENT, 1, 1);
                        account.setMoney(playerBankMoney - 100);
                        openBankMenu(player);
                    }
                }
            } else if (current.getType().equals(Material.ENDER_CHEST)) {
                openBank(player, player);
            } else if (current.getType().equals(Material.ANVIL)) {
                BankAccount bankAccount = new BankAccount(main, player.getUniqueId().toString());
                int price = bankAccount.getMaxMoney();
                if (playerMoney >= price) {
                    getEco().withdrawPlayer(player, price);
                    bankAccount.setUpgradeLevel(bankAccount.getUpgradeLevel() + 1);
                    player.sendMessage("§aVous avez payé §6" + price + "$§a pour améliorer votre banque au niveau §d" + account.getUpgradeLevel() + "§a !");
                    InGameUtilities.playPlayerSound(player, "block.anvil.use", SoundCategory.AMBIENT, 1, 1);
                    InGameUtilities.playPlayerSound(player, "entity.player.levelup", SoundCategory.AMBIENT, 1, 1);
                    openBankMenu(player);
                } else {
                    player.sendMessage("§cVous n'avez pas assez d'argent.");
                }
            }
        }
        // Les événements de stockage sont maintenant gérés par StorageEvent
    }

    @EventHandler
    public void closeInventory(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        String inventoryTitle = e.getView().getTitle();

        // Seule la gestion de fermeture pour les anciens stockages (compatibilité)
        if (inventoryTitle.equalsIgnoreCase("§8Stockage de " + player.getName())) {
            BankStorage storage = new BankStorage(main, player.getUniqueId().toString());
            storage.saveAllItems();
            InGameUtilities.playPlayerSound(player, "entity.villager.yes", SoundCategory.AMBIENT, 1, 1);
        }
        // Les fermetures de stockage paginé sont gérées par StorageEvent
    }

    private void openBankMenu(Player player) {
        BankAccount account = new BankAccount(main, player.getUniqueId().toString());
        int money = account.getMoney();
        int maxMoney = account.getMaxMoney();

        Inventory bank = Bukkit.createInventory(null, 27, "§8Votre argent : §6" + money + "$ / " + maxMoney + "$");
        setItemsMenuBank(bank, player);
        player.openInventory(bank);
    }

    private void openBank(Player owner, Player consulter) {
        UUID ownerId = owner.getUniqueId();
        BankStorage storage = bankStorages.computeIfAbsent(ownerId, id -> new BankStorage(main, id.toString()));

        // Check if storage needs multiple pages
        if (storage.getTotalPages() > 1) {
            storage.openStoragePage(consulter, 0); // Start with page 0
        } else {
            storage.openStorage(consulter, 0); // Legacy single-page method
        }
    }

    private void openBank(OfflinePlayer owner, Player consulter) {
        UUID ownerId = owner.getUniqueId();
        BankStorage storage = bankStorages.computeIfAbsent(ownerId, id -> new BankStorage(main, id.toString()));

        // Check if storage needs multiple pages
        if (storage.getTotalPages() > 1) {
            storage.openStoragePage(consulter, 0); // Start with page 0
        } else {
            storage.openStorage(consulter, 0); // Legacy single-page method
        }
    }

    private void setItemsMenuBank(Inventory inv, Player player) {
        BankAccount account = new BankAccount(main, player.getUniqueId().toString());

        inv.setItem(11, setItemMetaLore(
                Material.GOLD_INGOT, "§aArgent -",
                (short) 0,
                listMaker("§8Faites un §dclic gauche §8pour ajouter §6100$",
                        "§8à votre compte en banque",
                        "§8Faites un §dclic droit §8pour retirer §6100$",
                        "§8de votre compte en banque"
                )
        ));

        if (account.getUpgradeLevel() < 7) {
            inv.setItem(13, setItemMetaLore(
                    Material.ANVIL,
                    "§aAmélioration - Prix : §6" + account.getMaxMoney() + "$",
                    (short) 0,
                    listMaker(
                            "§8Vous avez actuellement l'amélioration n°§d " + account.getUpgradeLevel() + " ",
                            "§8Pour l'améliorer au niveau suivant:",
                            "§8- Maximum de la banque: §6" + account.getMaxMoney() + "$",
                            "§8- Maximum du stockage: §6" + account.getMaxSlots() + " slots"
                    )
            ));
        } else {
            inv.setItem(13, setItemMetaLore(
                    Material.BOOK,
                    "§aAmélioration -",
                    (short) 0,
                    listMaker("§8Vous avez atteint le maximum d'amélioration !", "", "", "")));
        }

        int slots = account.getMaxSlots();
        inv.setItem(15, setItemMetaLore(
                Material.ENDER_CHEST,
                "§aStockage personnel - ",
                (short) 0,
                listMaker(
                        "§8Faites un §dclic gauche§8 pour ouvrir votre stockage",
                        "§8Vous disposez actuellement de §6" + slots + "§8 slots de stockage !",
                        "§8L'amélioration suivante vous permettra de passer §6",
                        "§8à §6" + (slots + 9) + "§8 slots !"
                )
        ));
    }

    // Méthodes utilitaires pour la gestion du stockage
    public static BankStorage getBankStorage(UUID playerId, Fireland main) {
        return bankStorages.computeIfAbsent(playerId, id -> new BankStorage(main, id.toString()));
    }

    public static void saveAllBankStorage() {
        for (UUID uuid : bankStorages.keySet()) {
            BankStorage storage = bankStorages.get(uuid);
            storage.saveAllItems();
        }
    }

    private List<String> listMaker(String str1, String str2, String str3, String str4) {
        List<String> lore = new ArrayList<>();
        if (!str1.isEmpty()) lore.add(str1);
        if (!str2.isEmpty()) lore.add(str2);
        if (!str3.isEmpty()) lore.add(str3);
        if (!str4.isEmpty()) lore.add(str4);
        return lore;
    }
}