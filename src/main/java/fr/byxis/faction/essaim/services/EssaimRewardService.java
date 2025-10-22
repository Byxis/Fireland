package fr.byxis.faction.essaim.services;

import fr.byxis.faction.essaim.essaimClass.EssaimClass;
import fr.byxis.faction.essaim.essaimClass.EssaimGroup;
import fr.byxis.faction.essaim.repository.EssaimRepository;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.jeton.JetonManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static fr.byxis.player.level.LevelStorage.addPlayerXp;

/**
 * Gère les récompenses des essaims (jetons, expérience, etc.)
 */
public class EssaimRewardService
{

    private final Fireland plugin;
    private final EssaimRepository repository;
    private final JetonManager jetonManager;

    public EssaimRewardService(Fireland plugin, EssaimRepository repository)
    {
        this.plugin = plugin;
        this.repository = repository;
        this.jetonManager = new JetonManager(plugin);
    }

    /**
     * Distribue les récompenses à un groupe qui a terminé l'essaim
     */
    public void distributeRewards(EssaimGroup group, EssaimClass essaim)
    {
        if (!group.isCompleted())
        {
            return;
        }

        EssaimConfigService configService = new EssaimConfigService(plugin);
        EssaimConfigService.EssaimInfo essaimInfo = configService.getEssaimInfo(essaim.getName());

        if (essaimInfo == null)
        {
            plugin.getLogger().warning("No configuration found for essaim: " + essaim.getName());
            return;
        }

        EssaimConfigService.RewardConfiguration rewards = essaimInfo.rewards();

        for (Player member : group.getMembers())
        {
            repository.recordPlayerCompletion(member.getUniqueId(), essaim.getName());

            if (rewards.hasJetons())
            {
                giveRewardToPlayer(member, essaim.getName(), "jetons", "default",
                        rewards.getJetonsAmount(), rewards.getJetonsCooldown(), group);
            }

            if (rewards.hasXp())
            {
                addPlayerXp(member.getUniqueId(), rewards.getXpAmount());
            }

            for (int i = 0; i < rewards.getCommandRewards().size(); i++)
            {
                EssaimConfigService.CommandReward cmdReward = rewards.getCommandRewards().get(i);
                executeCommandReward(member, essaim.getName(), "command_" + i,
                        cmdReward.getCommand(), cmdReward.getCooldown());
            }
        }

        plugin.getLogger().info("Distributed rewards to " + group.getMembers().size() +
                " players for completing " + essaim.getName());
    }

    private void giveRewardToPlayer(Player player, String essaimName, String rewardType, String rewardId,
                                    int amount, String cooldownValue, EssaimGroup group) {
        EssaimCooldownService cooldownService = new EssaimCooldownService(repository);

        if (!cooldownService.canReceiveReward(player.getUniqueId(), essaimName, rewardType, rewardId, cooldownValue)) {
            return;
        }

        int finalReward = group.calculateJetonReward(amount);

        JetonManager.addJetonsPlayer(player.getUniqueId(), finalReward);

        repository.recordPlayerReward(player.getUniqueId(), essaimName, rewardType, rewardId);

        InGameUtilities.sendPlayerInformation(player,
                "§a+ " + finalReward + " §r⛁§a reçus pour avoir terminé l'essaim !");
    }

    private void executeCommandReward(Player player, String essaimName, String commandId,
                                      String command, String cooldownValue) {
        EssaimCooldownService cooldownService = new EssaimCooldownService(repository);

        if (!cooldownService.canReceiveReward(player.getUniqueId(), essaimName, "command", commandId, cooldownValue)) {
            return;
        }

        String replacedCommand = command.toLowerCase().replace("player", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replacedCommand);

        repository.recordPlayerReward(player.getUniqueId(), essaimName, "command", commandId);
    }


}