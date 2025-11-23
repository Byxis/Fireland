package fr.byxis.faction.essaim.managers;

import fr.byxis.faction.essaim.enums.LeaveResult;
import fr.byxis.faction.essaim.essaimClass.EssaimGroup;
import fr.byxis.fireland.utilities.InGameUtilities;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.bukkit.entity.Player;

/**
 * Manages Essaim groups and player membership Handles group creation, joining,
 * leaving, and lifecycle
 */
public class GroupManager
{

    private final Map<String, EssaimGroup> m_activeGroups;

    public GroupManager()
    {
        this.m_activeGroups = new HashMap<>();
    }

    /**
     * Creates a new group for the specified essaim
     *
     * @param _essaimName
     *            The essaim name
     * @param _leader
     *            The group leader
     *
     * @return The created group
     *
     * @throws IllegalStateException
     *             if a group already exists for this essaim
     */
    public synchronized EssaimGroup createGroup(String _essaimName, Player _leader)
    {
        if (hasGroup(_essaimName))
        {
            throw new IllegalStateException("Group already exists for essaim: " + _essaimName);
        }

        EssaimGroup group = new EssaimGroup(_essaimName, _leader);
        m_activeGroups.put(_essaimName, group);

        InGameUtilities.sendPlayerInformation(_leader, "Vous avez créé un groupe dans l'essaim !");
        return group;
    }

    /**
     * Adds a player to an existing group
     *
     * @param _essaimName
     *            The essaim name
     * @param _player
     *            The player to add
     *
     * @return true if the player was added, false otherwise
     */
    public boolean joinGroup(String _essaimName, Player _player)
    {
        EssaimGroup group = m_activeGroups.get(_essaimName);
        if (group == null)
        {
            return false;
        }

        if (group.getMembers().contains(_player))
        {
            return false;
        }

        group.joinGroup(_player);
        return true;
    }

    /**
     * Removes a player from their group
     *
     * @param _essaimName
     *            The essaim name
     * @param _player
     *            The player to remove
     *
     * @return true if the player was removed, false otherwise
     */
    public boolean leaveGroup(String _essaimName, Player _player)
    {
        EssaimGroup group = m_activeGroups.get(_essaimName);
        if (group == null || !group.getMembers().contains(_player))
        {
            return false;
        }

        boolean wasLeader = group.getLeader().equals(_player);
        LeaveResult result = group.leaveGroup(_player);

        switch (result)
        {
            case SUCCESS -> {
                if (wasLeader && !group.isEmpty())
                {
                    notifyGroupMembers(group, "§e" + group.getLeader().getName() + " est maintenant le leader du groupe.");
                }
                else
                {
                    notifyGroupMembers(group, _player.getName() + " a quitté l'expédition.");
                }
                return true;
            }
            case GROUP_DISBANDED -> {
                m_activeGroups.remove(_essaimName);
                return true;
            }
            case NOT_IN_GROUP -> {
                return false;
            }
        }

        return false;
    }

    public boolean isGroupEmpty(String _essaimName)
    {
        EssaimGroup group = m_activeGroups.get(_essaimName);
        return group == null || group.isEmpty();
    }

    /**
     * Disbands the group completely
     *
     * @param _essaimName
     *            The essaim name
     */
    public void disbandGroup(String _essaimName)
    {
        EssaimGroup group = m_activeGroups.remove(_essaimName);
        if (group != null)
        {
            group.disband();
        }
    }

    /**
     * Handles player death in a group
     *
     * @param _player
     *            The player who died
     */
    public void handlePlayerDeath(Player _player)
    {
        Optional<EssaimGroup> playerGroup = findPlayerGroup(_player);
        if (playerGroup.isPresent())
        {
            EssaimGroup group = playerGroup.get();
            group.handlePlayerDeath(_player);

            if (group.isEmpty())
            {
                String essaimName = findEssaimNameByGroup(group);
                if (essaimName != null)
                {
                    disbandGroup(essaimName);
                }
            }
        }
    }

    /**
     * Starts an essaim expedition for a group
     *
     * @param _essaimName
     *            The essaim name
     * @param _difficulty
     *            The expedition difficulty
     *
     * @return true if the expedition was started, false otherwise
     */
    public boolean startExpedition(String _essaimName, int _difficulty)
    {
        EssaimGroup group = m_activeGroups.get(_essaimName);
        if (group == null || group.hasStarted())
        {
            return false;
        }

        group.startExpedition(_difficulty);
        return true;
    }

    // Getter methods

    public boolean hasGroup(String _essaimName)
    {
        return m_activeGroups.containsKey(_essaimName);
    }

    public EssaimGroup getGroup(String _essaimName)
    {
        return m_activeGroups.get(_essaimName);
    }

    public Map<String, EssaimGroup> getAllGroups()
    {
        return new HashMap<>(m_activeGroups);
    }

    public boolean isPlayerInAnyGroup(Player _player)
    {
        return findPlayerGroup(_player).isPresent();
    }

    public Optional<EssaimGroup> findPlayerGroup(Player _player)
    {
        return m_activeGroups.values().stream().filter(group -> group.getMembers().contains(_player)).findFirst();
    }

    // Private helper methods

    /**
     * Notifies all group members with a message
     *
     * @param _group
     *            The group to notify
     * @param _string
     *            The message to send
     */
    private void notifyGroupMembers(EssaimGroup _group, String _string)
    {
        _group.getMembers().forEach(member -> InGameUtilities.sendPlayerInformation(member, _string));
    }

    /**
     * Finds the essaim name associated with a given group
     *
     * @param _targetGroup
     *            The target group
     *
     * @return The essaim name, or null if not found
     */
    private String findEssaimNameByGroup(EssaimGroup _targetGroup)
    {
        return m_activeGroups.entrySet().stream().filter(entry -> entry.getValue().equals(_targetGroup)).map(Map.Entry::getKey).findFirst()
                .orElse(null);
    }
}
