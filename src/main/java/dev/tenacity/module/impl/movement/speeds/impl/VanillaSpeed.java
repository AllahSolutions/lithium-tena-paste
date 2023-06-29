package dev.tenacity.module.impl.movement.speeds.impl;

import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.utils.player.MovementUtils;

public class VanillaSpeed extends SpeedMode {

    public VanillaSpeed() {
        super("Vanilla");
    }

    @Override
    public void onMotionEvent(MotionEvent event) {

        if (mc.thePlayer.onGround)
            mc.thePlayer.jump();

        MovementUtils.strafe(1.0F);

        super.onMotionEvent(event);
    }
}
