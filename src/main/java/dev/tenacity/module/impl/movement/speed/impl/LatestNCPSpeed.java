package dev.tenacity.module.impl.movement.speed.impl;

import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.module.impl.movement.speed.SpeedMode;

public class LatestNCPSpeed extends SpeedMode {


    public int onGroundTicks, offGroundTicks;

    public LatestNCPSpeed() {
        super("Latest NCP");
    }

    @Override
    public void onTickEvent(TickEvent event) {

        if (mc.thePlayer.onGround) {
            ++onGroundTicks;
            offGroundTicks = 0;
        } else {
            ++offGroundTicks;
            onGroundTicks = 0;
        }

        super.onTickEvent(event);
    }

}
