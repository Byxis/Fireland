package fr.byxis.faction.faction.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FactionBuyPerkEvent  extends Event {

    private String faction;
    private String perk;

    private static final HandlerList handlers = new HandlerList();

    public FactionBuyPerkEvent(String _faction, String _perk)
    {
        faction = _faction;
        perk = _perk;
    }
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getFaction()
    {
        return faction;
    }

    public String getPerk()
    {
        return perk;
    }


}
