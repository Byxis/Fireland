package fr.byxis.faction.faction.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PlayerLeaveFactionEvent extends Event {

    private final UUID player;
    private final String faction;
    private final boolean isKicked;
    private static final HandlerList handlers = new HandlerList();

    public PlayerLeaveFactionEvent(UUID p, String faction, boolean isKicked)
    {
        this.player = p;
        this.faction = faction;
        this.isKicked = isKicked;
    }
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public UUID getPlayerUuid() {
        return player;
    }

    public String getFaction() {
        return faction;
    }

    public boolean isKicked() {
        return isKicked;
    }
}
