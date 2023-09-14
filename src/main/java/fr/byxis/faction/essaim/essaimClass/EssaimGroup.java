package fr.byxis.faction.essaim.essaimClass;

import fr.byxis.faction.essaim.EssaimFunctions;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.fireland.utilities.TextUtilities;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EssaimGroup {
    private String name;
    private Player leader;
    private List<Player> members;
    private int difficulty;
    private Timestamp startTime;

    public EssaimGroup(String name, Player firstPlayer) {
        this.name = name;
        this.leader = firstPlayer;
        this.members = new ArrayList<Player>();
        this.startTime = null;

        // Add the first player as a member (and leader)
        members.add(firstPlayer);
    }

    public boolean invitePlayer(Player invitee) {

        //Check if group is full or if player is already in group
        if (members.size() >= 4 || members.contains(invitee)) {
            return false;
        }

        // Invite player to group
        InGameUtilities.sendInteractivePlayerMessage(invitee, "Vous avez été invité dans l'essaim " + TextUtilities.convertStorableToClean(name) + " par "+leader.getName()+". Cliquez sur ce message pour rejoindre.", "/essaim join "+name+" Wowowowowowowowowowow1234567890", "§aCliquez ici pour rejoindre l'essaim", ClickEvent.Action.RUN_COMMAND);
        return true;
    }

    public void joinGroup(Player joiner)
    {
        if(members.isEmpty())
        {
            leader = joiner;
            members.add(joiner);
        }
        else
        {

            members.add(joiner);
        }
    }

    public boolean kickPlayer(String kicker, String kicked) {
        // Only allow the leader to kick players
        if (!kicker.equals(leader)) {
            return false;
        }

        // Remove player from group and return success/failure flag based on whether or not they were in the group.
        return members.remove(kicked);
    }

    public void leaveGroup(Player leaver) {
        // If it's not the leader leaving then remove them from list of players.
        if(leaver.getName().equalsIgnoreCase(leader.getName())){
            disband();
        }
        else
        {
            members.remove(leaver);
        }
    }

    public void loose(Player leaver) {
        members.remove(leaver);
        if(leaver.getName().equalsIgnoreCase(leader.getName())){
            if(!members.isEmpty())
            {
                leader = getMembers().get(0);
            }
            else
            {
                EssaimFunctions.looseEssaim(name, leader);
            }
        }
        for(Player member : members)
        {
            InGameUtilities.playPlayerSound(member, Sound.ENTITY_WITHER_HURT, SoundCategory.PLAYERS, 1, 0);
            InGameUtilities.sendPlayerInformation(member, "§cLe joueur " + leaver.getName() + " a quitter l'expédition.");
        }
    }

    public void finish(Player leaver) {
        members.remove(leaver);
        if(leaver.getName().equalsIgnoreCase(leader.getName())){
            disband();
        }
        else if(members.size() == 1)
        {
            leader = getMembers().get(0);
        }
    }

    public void disband() {
        // Clear all players from list
        for(int i=members.size()-1;i>=0;i--){
            members.remove(i);
        }
    }
    public boolean isEmpty()
    {
        if(members.size() == 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public List<Player> getMembers()
    {
        return members;
    }

    public Player getLeader()
    {
        return leader;
    }

    public void setDifficulty()
    {
        difficulty = members.size();
    }

    public float getMultiplicatorDifficulty() {
        return switch (difficulty)
        {
            case 1 -> 0.8f;
            case 3 -> 1.6f;
            case 4 -> 2f;
            default -> 1.2f;
        };
    }

    public void startEssaim()
    {
        setDifficulty();
        this.startTime = new Timestamp(System.currentTimeMillis());
    }

    public boolean hasStarted()
    {
        return startTime != null;
    }
}