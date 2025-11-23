package fr.byxis.faction.faction.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerJoinFactionEvent extends Event
{

    private final Player player;
    private final String faction;
    private static final HandlerList HANDLERS = new HandlerList();

    public PlayerJoinFactionEvent(Player _player, String _faction)
    {
        this.player = _player;
        this.faction = _faction;
    }
    @Override
    public HandlerList getHandlers()
    {
        return HANDLERS;
    }

    public static HandlerList getHandlerList()
    {
        return HANDLERS;
    }

    public Player getPlayer()
    {
        return player;
    }

    public String getFaction()
    {
        return faction;
    }
}
