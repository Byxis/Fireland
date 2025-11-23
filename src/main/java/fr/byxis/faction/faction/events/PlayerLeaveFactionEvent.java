package fr.byxis.faction.faction.events;

import java.util.UUID;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveFactionEvent extends Event
{

    private final UUID player;
    private final String faction;
    private final boolean isKicked;
    private static final HandlerList HANDLERS = new HandlerList();

    public PlayerLeaveFactionEvent(UUID _uuid, String _faction, boolean _isKicked)
    {
        this.player = _uuid;
        this.faction = _faction;
        this.isKicked = _isKicked;
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

    public UUID getPlayerUuid()
    {
        return player;
    }

    public String getFaction()
    {
        return faction;
    }

    public boolean isKicked()
    {
        return isKicked;
    }
}
