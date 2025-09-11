package fr.byxis.player.bank;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.PermissionUtilities;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BankAccount {
    private final Fireland main;
    private final String ownerId;

    public BankAccount(Fireland main, String ownerId) {
        this.main = main;
        this.ownerId = ownerId;
    }

    public int getMoney() {
        return main.getCfgm().getEnderchest().getInt("bank." + ownerId + ".money", 0);
    }

    public void setMoney(int amount) {
        main.getCfgm().getEnderchest().set("bank." + ownerId + ".money", amount);
        main.getCfgm().saveEnderchest();
    }

    public int getUpgradeLevel() {
        return main.getCfgm().getEnderchest().getInt("bank." + ownerId + ".upgrade", 0);
    }

    public void setUpgradeLevel(int level) {
        main.getCfgm().getEnderchest().set("bank." + ownerId + ".upgrade", level);
        main.getCfgm().saveEnderchest();
    }

    public int getMaxMoney() {
        int upgrade = getUpgradeLevel();
        return switch (upgrade) {
            case 1 -> 2500;
            case 2 -> 5000;
            case 3 -> 10000;
            case 4 -> 25000;
            case 5 -> 50000;
            case 6, 7 -> 100000;
            default -> 1000;
        };
    }

    public int getMaxSlots() {
        int upgrade = getUpgradeLevel();
        int baseMax = switch (upgrade) {
            case 0 -> 9;
            case 1 -> 18;
            case 2 -> 27;
            case 3 -> 36;
            case 4 -> 45;
            case 5, 6, 7 -> 54;
            default -> 0;
        };

        if (baseMax == 54) {
            Player player = main.getServer().getPlayer(UUID.fromString(ownerId));
            if (player != null) {
                if (PermissionUtilities.hasPermission(player, "fireland.bank.bonus.1")) {
                    baseMax += 54;
                }
                if (PermissionUtilities.hasPermission(player, "fireland.bank.bonus.2")) {
                    baseMax += 54;
                }
                if (PermissionUtilities.hasPermission(player, "fireland.bank.bonus.3")) {
                    baseMax += 54;
                }
            }
        }
        return baseMax;
    }
}
