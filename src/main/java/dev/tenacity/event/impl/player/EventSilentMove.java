package dev.tenacity.event.impl.player;

import dev.tenacity.event.Event;

public class EventSilentMove extends Event {

    public boolean silent, advanced;
    public float yaw;

    public EventSilentMove(float yaw) {
        this.yaw = yaw;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    public void setAdvanced(boolean advanced) {
        this.advanced = advanced;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
