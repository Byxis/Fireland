package fr.byxis.faction.essaim.essaimClass;

import fr.byxis.faction.essaim.enums.ExpeditionState;
import fr.byxis.faction.essaim.enums.InvitationResult;
import fr.byxis.faction.essaim.enums.JoinResult;
import fr.byxis.faction.essaim.enums.LeaveResult;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.TextUtilities;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a group (essaim) of players undertaking an expedition together.
 * <p>
 * The EssaimGroup class manages the core functionalities of a player group,
 * including inviting players, joining/leaving the group, starting/finishing expeditions,
 * handling player deaths, and calculating rewards based on difficulty and group size.
 * </p>
 * <p>
 * Key Features:
 * <ul>
 *     <li>Group Management: Invite, join, leave, and disband the group.</li>
 *     <li>Expedition Lifecycle: Start, finish, and track the state of the expedition.</li>
 *     <li>Adaptive Difficulty: Adjust difficulty multipliers based on group size.</li>
 *     <li>Reward Calculation: Determine jeton rewards based on expedition completion and difficulty.</li>
 *     <li>Player Death Handling: Manage player deaths and their impact on the group.</li>
 * </ul>
 * </p>
 * <p>
 * Example Usage:
 * <pre>
 * Player leader = ...; // Obtain a Player object for the leader
 * EssaimGroup group = new EssaimGroup("MyEssaim", leader);
 * group.invitePlayer(otherPlayer);
 * group.startExpedition(2); // Start expedition with medium difficulty
 * </pre>
 * </p>
 */
public class EssaimGroup {

    // Constants
    private static final int MAX_GROUP_SIZE = 4;
    private static final String INVITATION_TOKEN = "Wowowowowowowowowowow1234567890";
    private static final float SOLO_MULTIPLIER = 0.8f;
    private static final float DUO_MULTIPLIER = 1.2f;
    private static final float TRIO_MULTIPLIER = 1.6f;
    private static final float FULL_GROUP_MULTIPLIER = 2.0f;

    // Core data
    private final String m_essaimName;
    private Player m_leader;
    private final List<Player> m_members;

    // Expedition state
    private ExpeditionState m_state;
    private int m_difficultyLevel;
    private Timestamp m_expeditionStartTime;


    public EssaimGroup(String _essaimName, Player _initialLeader) {
        this.m_essaimName = validateEssaimName(_essaimName);
        this.m_leader = validatePlayer(_initialLeader, "Initial leader");
        this.m_members = new ArrayList<>();
        this.m_state = ExpeditionState.PREPARING;
        this.m_difficultyLevel = 0;
        this.m_expeditionStartTime = null;

        // Add leader as first member
        this.m_members.add(_initialLeader);
    }

    /**
     * Invites a player to join this group
     *
     * @param _invitee The player to invite
     * @return InvitationResult indicating success or failure reason
     */
    public InvitationResult invitePlayer(Player _invitee) {
        if (_invitee == null) {
            return InvitationResult.INVALID_PLAYER;
        }

        if (isGroupFull()) {
            return InvitationResult.GROUP_FULL;
        }

        if (m_members.contains(_invitee)) {
            return InvitationResult.ALREADY_IN_GROUP;
        }

        if (m_state != ExpeditionState.PREPARING) {
            return InvitationResult.EXPEDITION_STARTED;
        }

        sendInvitationMessage(_invitee);
        return InvitationResult.SUCCESS;
    }

    /**
     * Adds a player to the group (typically after accepting invitation)
     *
     * @param _joiner The player who wants to join
     */
    public JoinResult joinGroup(Player _joiner)
    {
        if (_joiner == null)
        {
            return JoinResult.INVALID_PLAYER;
        }

        if (isGroupFull())
        {
            return JoinResult.GROUP_FULL;
        }

        if (m_members.contains(_joiner))
        {
            return JoinResult.ALREADY_IN_GROUP;
        }

        if (m_state != ExpeditionState.PREPARING)
        {
            return JoinResult.EXPEDITION_STARTED;
        }

        m_members.add(_joiner);

        // If group was empty, make this player the leader
        if (m_members.size() == 1)
        {
            m_leader = _joiner;
        }

        notifyGroupJoin(_joiner);
        return JoinResult.SUCCESS;
    }

    /**
     * Removes a player from the group
     *
     * @param _leaver The player who wants to leave
     * @return LeaveResult indicating success or failure reason
     */
    public LeaveResult leaveGroup(Player _leaver)
    {
        if (_leaver == null || !m_members.contains(_leaver))
        {
            return LeaveResult.NOT_IN_GROUP;
        }

        boolean wasLeader = _leaver.equals(m_leader);
        m_members.remove(_leaver);

        if (m_members.isEmpty())
        {
            // Group is now empty
            m_state = ExpeditionState.DISBANDED;
            return LeaveResult.GROUP_DISBANDED;
        }

        if (wasLeader)
        {
            // Promote new leader
            m_leader = m_members.get(0);
            notifyNewLeader();
        }

        notifyGroupLeave(_leaver);
        return LeaveResult.SUCCESS;
    }

    /**
     * Handles a player death during expedition
     *
     * @param _player The player who died
     */
    public void handlePlayerDeath(Player _player)
    {
        if (!m_members.contains(_player))
        {
            return;
        }

        m_members.remove(_player);

        for (Player member : m_members)
        {
            InGameUtilities.playPlayerSound(member, Sound.ENTITY_WITHER_HURT, SoundCategory.PLAYERS, 1, 0);
            InGameUtilities.sendPlayerInformation(member, "§cLe joueur " + _player.getName() + " a quitté l'expédition.");
        }

        if (_player.equals(m_leader))
        {
            if (!m_members.isEmpty())
            {
                m_leader = m_members.get(0);
                notifyNewLeader();
            }
        }

        if (m_members.isEmpty()) {
            m_state = ExpeditionState.FAILED;
        }
    }

    /**
     * Starts the expedition with specified difficulty
     *
     * @param _difficulty Difficulty level (1-3)
     * @return true if the expedition started successfully, false otherwise
     */
    public boolean startExpedition(int _difficulty)
    {
        if (m_state != ExpeditionState.PREPARING)
        {
            return false;
        }

        if (_difficulty < 1 || _difficulty > 3)
        {
            throw new IllegalArgumentException("Difficulty must be between 1 and 3");
        }

        this.m_difficultyLevel = _difficulty;
        this.m_expeditionStartTime = new Timestamp(System.currentTimeMillis());
        this.m_state = ExpeditionState.IN_PROGRESS;

        return true;
    }

    /**
     * Finishes the expedition successfully
     */
    public void finishExpedition()
    {
        if (m_state == ExpeditionState.IN_PROGRESS)
        {
            m_state = ExpeditionState.COMPLETED;
        }
    }

    /**
     * Calculates adaptive difficulty multiplier based on group size
     *
     * @return float multiplier (e.g., 0.8 for solo, 1.2 for duo, etc.)
     */
    public float getAdaptiveDifficultyMultiplier()
    {
        return switch (m_members.size()) {
            case 1 -> SOLO_MULTIPLIER;
            case 2 -> DUO_MULTIPLIER;
            case 3 -> TRIO_MULTIPLIER;
            case 4 -> FULL_GROUP_MULTIPLIER;
            default -> 1.0f; // Fallback
        };
    }

    /**
     * Calculates jeton reward based on difficulty and completion
     *
     * @param _baseJetons Base jeton reward
     * @return int total jeton reward
     */
    public int calculateJetonReward(int _baseJetons)
    {
        if (m_state != ExpeditionState.COMPLETED)
        {
            return 0;
        }

        int reward = _baseJetons;

        if (m_difficultyLevel == 3)
            reward += 5;

        return reward;
    }

    /**
     * Checks if inventory should be kept on death
     *
     * @return true if inventory should be kept, false otherwise
     */
    public boolean shouldKeepInventoryOnDeath()
    {
        return m_difficultyLevel == 1 && hasStarted();
    }

    /**
     * Disbands the group completely
     */
    public void disband()
    {
        m_members.clear();
        m_state = ExpeditionState.DISBANDED;
    }

    // Getters and state checks

    public String getEssaimName()
    {
        return m_essaimName;
    }

    public Player getLeader()
    {
        return m_leader;
    }

    public List<Player> getMembers()
    {
        return Collections.unmodifiableList(m_members);
    }

    public List<String> getMemberNames()
    {
        return m_members.stream()
                .map(Player::getName)
                .toList();
    }

    public int getSize()
    {
        return m_members.size();
    }

    public boolean isEmpty()
    {
        return m_members.isEmpty();
    }

    public boolean isGroupFull()
    {
        return m_members.size() >= MAX_GROUP_SIZE;
    }

    public boolean hasStarted()
    {
        return m_expeditionStartTime != null && m_state == ExpeditionState.IN_PROGRESS;
    }

    public boolean isCompleted()
    {
        return m_state == ExpeditionState.COMPLETED;
    }

    public boolean isFailed()
    {
        return m_state == ExpeditionState.FAILED;
    }

    public boolean isDisbanded()
    {
        return m_state == ExpeditionState.DISBANDED;
    }

    public int getDifficulty()
    {
        return m_difficultyLevel;
    }

    public Timestamp getStartTime()
    {
        return m_expeditionStartTime != null ? new Timestamp(m_expeditionStartTime.getTime()) : null;
    }

    public ExpeditionState getState()
    {
        return m_state;
    }

    // Private helper methods

    /**
     * Validates and cleans the essaim name
     *
     * @param name The essaim name to validate
     * @return Cleaned essaim name
     * @throws IllegalArgumentException if name is null or empty
     */
    private String validateEssaimName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Essaim name cannot be null or empty");
        }
        return name.trim();
    }

    /**
     * Validates a player object
     *
     * @param player The player to validate
     * @param context Contextual information for error messages
     * @return The validated player
     * @throws IllegalArgumentException if player is null
     */
    private Player validatePlayer(Player player, String context) {
        if (player == null) {
            throw new IllegalArgumentException(context + " cannot be null");
        }
        return player;
    }

    /**
     * Sends an interactive invitation message to the invitee
     *
     * @param invitee The player to invite
     */
    private void sendInvitationMessage(Player invitee) {
        String cleanEssaimName = TextUtilities.convertStorableToClean(m_essaimName);
        String message = String.format(
                "Vous avez été invité dans l'essaim %s par %s. Cliquez sur ce message pour rejoindre.",
                cleanEssaimName,
                m_leader.getName()
        );

        String command = String.format("/essaim join %s %s", m_essaimName, INVITATION_TOKEN);

        InGameUtilities.sendInteractivePlayerMessage(
                invitee,
                message,
                command,
                "§aCliquez ici pour rejoindre l'essaim",
                ClickEvent.Action.RUN_COMMAND
        );
    }

    /**
     * Notifies all group members that a new player has joined
     *
     * @param joiner The player who joined
     */
    private void notifyGroupJoin(Player joiner) {
        for (Player member : m_members) {
            InGameUtilities.sendPlayerInformation(member,
                    joiner.getName() + " a rejoint l'expédition.");
        }
    }

    /**
     * Notifies all group members that a player has left
     *
     * @param leaver The player who left
     */
    private void notifyGroupLeave(Player leaver) {
        for (Player member : m_members) {
            InGameUtilities.sendPlayerInformation(member,
                    leaver.getName() + " a quitté l'expédition.");
        }
    }

    /**
     * Notifies all group members about the new leader
     */
    private void notifyNewLeader() {
        for (Player member : m_members) {
            InGameUtilities.sendPlayerInformation(member,
                    "§e" + m_leader.getName() + " est maintenant le leader du groupe.");
        }
    }

    @Override
    public String toString() {
        return "EssaimGroup{" +
                "essaimName='" + m_essaimName + '\'' +
                ", leader=" + (m_leader != null ? m_leader.getName() : "null") +
                ", memberCount=" + m_members.size() +
                ", state=" + m_state +
                ", difficulty=" + m_difficultyLevel +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EssaimGroup that = (EssaimGroup) obj;
        return Objects.equals(m_essaimName, that.m_essaimName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_essaimName);
    }
}