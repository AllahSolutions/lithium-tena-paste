package dev.tenacity.event.impl.player;

import dev.tenacity.event.Event;

public class RaycastEvent extends Event {
    private float partialTicks;

    public RaycastEvent(final float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void setPartialTicks(final float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
