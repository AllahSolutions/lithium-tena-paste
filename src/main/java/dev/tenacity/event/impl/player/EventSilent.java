package dev.tenacity.event.impl.player;

import dev.tenacity.event.Event;

public class EventSilent extends Event {
    private int slotID;
    
    public EventSilent(final int slotID) {
        this.slotID = slotID;
    }
    
    public int getSlotID() {
        return this.slotID;
    }
    
    public void setSlotID(final int slotID) {
        this.slotID = slotID;
    }
}
