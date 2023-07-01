package dev.tenacity.module.impl.movement.speeds.impl;

import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.event.impl.player.MoveEvent;
import dev.tenacity.module.impl.combat.TargetStrafe;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.utils.player.MovementUtils;

public class VanillaSpeed extends SpeedMode {
    private TargetStrafe targetStrafe;

    public VanillaSpeed() {
        super("Vanilla");
        targetStrafe = new TargetStrafe();
    }

    @Override
    public void onMoveEvent(MoveEvent event) {
        MovementUtils.strafe(1.0F);
        targetStrafe.strafe(event,1);


    }

    @Override
    public void onMotionEvent(MotionEvent event) {

        if (mc.thePlayer.onGround)
            mc.thePlayer.jump();



        super.onMotionEvent(event);
    }
}
