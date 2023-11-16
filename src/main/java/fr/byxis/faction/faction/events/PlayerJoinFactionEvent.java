package fr.byxis.faction.faction.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinFactionEvent extends Event {

    private final Player player;
    private final String faction;
    private static final HandlerList handlers = new HandlerList();

    public PlayerJoinFactionEvent(Player p, String faction)
    {
        this.player = p;
        this.faction = faction;
    }
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public String getFaction() {
        return faction;
    }
}
