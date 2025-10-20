package fr.byxis.player.intendant.menu;

import fr.byxis.faction.essaim.EssaimManager;
import fr.byxis.faction.essaim.enums.CooldownType;
import fr.byxis.faction.essaim.essaimClass.EssaimClass;
import fr.byxis.faction.essaim.repository.EssaimRepository;
import fr.byxis.faction.essaim.services.EssaimConfigService;
import fr.byxis.faction.essaim.services.EssaimCooldownService;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.InventoryUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MenuEssaim {

    public static void openEssaimMenu(Fireland main, Player p)
    {
        InGameUtilities.playPlayerSound(p, "ui.button.click", SoundCategory.BLOCKS, 1, 2);
        Inventory inv = Bukkit.createInventory(null, 54, "Calendrier des Essaims");
        setItemEssaimMenu(main, inv, p);
        p.openInventory(inv);
    }

    private static void setItemEssaimMenu(Fireland main, Inventory inv, Player p) {
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            inv.setItem(i + 45, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }
        for (int i = 9; i < 45; i += 9) {
            inv.setItem(i, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
            inv.setItem(i + 8, InventoryUtilities.setItemMeta(Material.WHITE_STAINED_GLASS_PANE, " ", (short) 1));
        }

        // Immeuble Infesté
        setEssaimItem(main, inv, p, "immeuble-infeste", Material.CYAN_TERRACOTTA, "§7Immeuble Infesté", 10, 1);

        // Usine Portuaire
        setEssaimItem(main, inv, p, "usine-portuaire", Material.BRICKS, "§7Usine", 11, 3);

        // Station de traitement des eaux
        setEssaimItem(main, inv, p, "station-de-traitement-des-eaux", Material.MOSSY_STONE_BRICKS, "§7Station de traitement des eaux", 12, 4);

        // Entrepôt Militaire
        setEssaimItem(main, inv, p, "entrepot-militaire", Material.END_STONE_BRICKS, "§7Entrepôt Militaire", 13, 5);

        // Épave du porte-avion
        setEssaimItem(main, inv, p, "soute-du-porte-avion", Material.PRISMARINE_BRICKS, "§7Soute du porte-avion", 14, 9);

        // Hangar Silencieux
        setEssaimItem(main, inv, p, "hangar-silencieux", Material.COAL_BLOCK, "§7Hangar Silencieux", 15, 6);

        // Crypte
        setEssaimItem(main, inv, p, "crypte", Material.SKELETON_SKULL, "§7Crypte", 16, 6);

        // Centrale Nucléaire
        setEssaimItem(main, inv, p, "centrale-nucleaire", Material.WAXED_WEATHERED_COPPER, "§7Centrale Nucléaire", 19, 7);

        // Station Pétrolière
        setEssaimItem(main, inv, p, "station-petroliere", Material.BLACKSTONE, "§7Station Pétrolière", 20, 7);

        // Laboratoire
        setEssaimItem(main, inv, p, "laboratoire", Material.SMOOTH_QUARTZ, "§7Laboratoire", 21, 9);

        // Bunker de Latus
        setEssaimItem(main, inv, p, "bunker-de-latus", Material.DEEPSLATE_BRICKS, "§7Bunker de Latus", 22, 9);

        // Antre de la Pondeuse
        setEssaimItem(main, inv, p, "antre-de-la-pondeuse", Material.SCULK, "§7Antre de la Pondeuse", 23, 12);

        // Les Abysses
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§4§lAbsence de données");
        inv.setItem(24, InventoryUtilities.setItemMetaLore(Material.SEA_LANTERN, "§7Les Abysses", (short) 1, lore));

        // Retour à l'intendant
        inv.setItem(53, InventoryUtilities.setItemMeta(Material.RED_STAINED_GLASS_PANE, "§cRetour à l'intendant", (short) 0));
    }

    private static void setEssaimItem(Fireland main, Inventory inv, Player p, String essaimName, Material material, String displayName, int slot, int difficulty) {
        ArrayList<String> lore = new ArrayList<>();
        EssaimManager essaimManager = main.getEssaimManager();
        boolean isActive = essaimManager.isEssaimActive(essaimName);

        if (isActive) {
            EssaimClass essaimClass = essaimManager.getEssaimService().getActiveEssaim(essaimName);

            if (essaimClass != null) {
                boolean isEventBased = essaimClass.isEventBased();

                if (isEventBased) {
                    if (hasPlayerFinishedEvent(p, essaimClass, essaimManager)) {
                        lore.add("§8Disponibilité : §aÉvènement déjà réalisé");
                    } else {
                        lore.add("§8Disponibilité : §aOuvert §d(Événementiel)");
                    }
                } else {
                    lore.add("§8Disponibilité : §aOuvert");
                }
            } else {
                lore.add("§8Disponibilité : §7Erreur de chargement");
            }
        }

        lore.add("§8Difficulté : " + getDifficulty(difficulty));

        if (!essaimName.equals("none")) {
            addRewardInformation(main, lore, p, essaimName);
        }

        inv.setItem(slot, InventoryUtilities.setItemMetaLore(material, displayName, (short) 1, lore));
    }

    private static void addRewardInformation(Fireland main, ArrayList<String> lore, Player p, String essaimName) {
        EssaimConfigService configService = new EssaimConfigService(main);

        // Récupérer les informations de récompenses depuis EssaimConfigService
        EssaimConfigService.EssaimInfo essaimInfo = configService.getEssaimInfo(essaimName);
        if (essaimInfo == null || essaimInfo.rewards() == null) return;

        EssaimConfigService.RewardConfiguration rewards = essaimInfo.rewards();

        lore.add(""); // Ligne vide

        // Afficher les jetons
        if (rewards.hasJetons()) {
            String jetonsStatus = getRewardStatus(main, p, essaimName, "jetons", "default", rewards.getJetonsCooldown());
            String jetonsLabel = getRewardLabel(rewards.getJetonsCooldown());
            lore.add("§8" + jetonsLabel + " (" + rewards.getJetonsAmount() + " jetons) : " + jetonsStatus);
        }

        // Afficher les commandes
        for (int i = 0; i < rewards.getCommandRewards().size(); i++) {
            EssaimConfigService.CommandReward commandReward = rewards.getCommandRewards().get(i);
            String commandStatus = getRewardStatus(main, p, essaimName, "command", "command_" + i, commandReward.getCooldown());
            String commandLabel = getRewardLabel(commandReward.getCooldown());
            lore.add("§8" + commandLabel + " : " + commandStatus);
        }
    }

    private static String getRewardLabel(String cooldownValue) {
        return switch (cooldownValue.toLowerCase()) {
            case "first_time" -> "Première récompense";
            case "1d", "24h", "dynamic_day" -> "Récompenses journalières";
            case "1w", "7d", "dynamic_week" -> "Récompenses hebdomadaires";
            case "1m", "30d", "dynamic_month" -> "Récompenses mensuelles";
            case "none" -> "Récompenses permanentes";
            default -> {
                if (cooldownValue.contains("d")) {
                    yield "Récompenses (" + cooldownValue + ")";
                } else if (cooldownValue.contains("h")) {
                    yield "Récompenses (" + cooldownValue + ")";
                }
                yield "Récompenses spéciales";
            }
        };
    }

    private static String getRewardStatus(Fireland main, Player p, String essaimName, String rewardType, String rewardId, String cooldownValue) {
        EssaimManager essaimManager = main.getEssaimManager();
        EssaimCooldownService cooldownService = essaimManager.getEssaimService().getCooldownService();
        EssaimRepository repository = essaimManager.getEssaimService().getRepository();

        if (cooldownService.canReceiveReward(p.getUniqueId(), essaimName, rewardType, rewardId, cooldownValue)) {
            return "§adisponible";
        }

        CooldownType cooldownType = CooldownType.fromString(cooldownValue);

        switch (cooldownType) {
            case FIRST_TIME:
                return "§cobtenue";

            case NONE:
                return "§atoujours disponible";

            default:
                // Calculer le temps restant pour tous les autres types
                LocalDateTime lastReward = repository.getLastRewardDate(p.getUniqueId(), essaimName, rewardType, rewardId);
                if (lastReward != null) {
                    try {
                        var duration = CooldownType.getDuration(cooldownValue);
                        var nextAvailable = lastReward.plus(duration);
                        var now = LocalDateTime.now();

                        if (now.isBefore(nextAvailable)) {
                            long secondsRemaining = java.time.Duration.between(now, nextAvailable).getSeconds();
                            String timeString = BasicUtilities.getStringTime(secondsRemaining);
                            return "§cdans " + timeString;
                        }
                    } catch (Exception e) {
                        return "§7erreur cooldown";
                    }
                }
                return "§adisponible";
        }
    }

    private static boolean hasPlayerFinishedEvent(Player player, EssaimClass essaimClass, EssaimManager essaimManager) {
        return essaimManager.getConfigService().getRawConfig()
                .getBoolean("player_completion." + player.getUniqueId() + "." + essaimClass.getName(), false);
    }

    private static String getDifficulty(int i)
    {
        return switch (i)
        {
            case 1 -> "§a☠";
            case 2 -> "§a☠☠";
            case 3 -> "§a☠☠☠";
            case 4 -> "§e☠";
            case 5 -> "§e☠☠";
            case 6 -> "§e☠☠☠";
            case 7 -> "§c☠";
            case 8 -> "§c☠☠";
            case 9 -> "§c☠☠☠";
            case 10 -> "§4☠";
            case 11 -> "§4☠☠";
            case 12 -> "§4☠☠☠";
            default -> "";
        };
    }
}
