package fr.byxis.faction.essaim.services;

import static fr.byxis.player.level.LevelStorage.addPlayerXp;

import fr.byxis.faction.essaim.essaimClass.EssaimClass;
import fr.byxis.faction.essaim.essaimClass.EssaimGroup;
import fr.byxis.faction.essaim.repository.EssaimRepository;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.jeton.JetonManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Gère les récompenses des essaims (jetons, expérience, etc.)
 */
public class EssaimRewardService
{

    private final Fireland m_fireland;
    private final EssaimRepository m_repository;

    public EssaimRewardService(Fireland _fireland, EssaimRepository _repository)
    {
        this.m_fireland = _fireland;
        this.m_repository = _repository;
    }

    /**
     * Distribute rewards to all members of the essaim group upon completion.
     *
     * @param _group
     *            The essaim group that has completed the essaim.
     * @param _essaim
     *            The essaim class that was completed.
     */
    public void distributeRewards(EssaimGroup _group, EssaimClass _essaim)
    {
        if (!_group.isCompleted())
        {
            return;
        }

        EssaimConfigService configService = new EssaimConfigService(m_fireland);
        EssaimConfigService.EssaimInfo essaimInfo = configService.getEssaimInfo(_essaim.getName());

        if (essaimInfo == null)
        {
            m_fireland.getLogger().warning("No configuration found for essaim: " + _essaim.getName());
            return;
        }

        EssaimConfigService.RewardConfiguration rewards = essaimInfo.rewards();

        for (Player member : _group.getMembers())
        {
            m_repository.recordPlayerCompletion(member.getUniqueId(), _essaim.getName());

            if (rewards.hasJetons())
            {
                giveRewardToPlayer(member, _essaim.getName(), "jetons", "default", rewards.getJetonsAmount(), rewards.getJetonsCooldown(),
                        _group);
            }

            if (rewards.hasXp())
            {
                addPlayerXp(member.getUniqueId(), rewards.getXpAmount());
            }

            for (int i = 0; i < rewards.getCommandRewards().size(); i++)
            {
                EssaimConfigService.CommandReward cmdReward = rewards.getCommandRewards().get(i);
                executeCommandReward(member, _essaim.getName(), "command_" + i, cmdReward.getCommand(), cmdReward.getCooldown());
            }
        }

        m_fireland.getLogger()
                .info("Distributed rewards to " + _group.getMembers().size() + " players for completing " + _essaim.getName());
    }

    private void giveRewardToPlayer(Player player, String essaimName, String rewardType, String rewardId, int amount, String cooldownValue,
            EssaimGroup group)
    {
        EssaimCooldownService cooldownService = new EssaimCooldownService(m_repository);

        if (!cooldownService.canReceiveReward(player.getUniqueId(), essaimName, rewardType, rewardId, cooldownValue))
        {
            return;
        }

        int finalReward = group.calculateJetonReward(amount);

        JetonManager.addJetonsPlayer(player.getUniqueId(), finalReward);

        m_repository.recordPlayerReward(player.getUniqueId(), essaimName, rewardType, rewardId);

        InGameUtilities.sendPlayerInformation(player, "§a+ " + finalReward + " §r⛁§a reçus pour avoir terminé l'essaim !");
    }

    private void executeCommandReward(Player player, String essaimName, String commandId, String command, String cooldownValue)
    {
        EssaimCooldownService cooldownService = new EssaimCooldownService(m_repository);

        if (!cooldownService.canReceiveReward(player.getUniqueId(), essaimName, "command", commandId, cooldownValue))
        {
            return;
        }

        String replacedCommand = command.toLowerCase().replace("player", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replacedCommand);

        m_repository.recordPlayerReward(player.getUniqueId(), essaimName, "command", commandId);
    }

}