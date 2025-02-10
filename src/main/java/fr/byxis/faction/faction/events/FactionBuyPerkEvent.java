package fr.byxis.faction.faction.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FactionBuyPerkEvent  extends Event {

    private final String faction;
    private final String perk;

    private static final HandlerList HANDLERS = new HandlerList();

    public FactionBuyPerkEvent(String _faction, String _perk)
    {
        faction = _faction;
        perk = _perk;
    }
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
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
