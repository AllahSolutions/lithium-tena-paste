package dev.tenacity.module.impl.movement.speeds.impl;

import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.player.movement.correction.StrafeEvent;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.utils.player.MovementUtils;

public class Karhu extends SpeedMode {
    int jumps;
    private int offGroundTicks;
    public Karhu() {
        super("Karhu");
    }



    @Override
    public void onMotionEvent(MotionEvent event) {

        if(event.isPre()) {
            if (mc.thePlayer.onGround) {
                offGroundTicks = 0;

            } else {
                offGroundTicks++;
            }

            if (mc.thePlayer.onGround) {
                //if (mc.thePlayer.hurtTime == 0) MoveUtil.strafe(MoveUtil.getAllowedHorizontalDistance() * 0.99);
               // MovementUtils.strafe(MovementUtils.getBaseMoveSpeed());
                mc.thePlayer.jump();

                jumps++;
            }
            
            //maybe make it auto space
            //&& mc.thePlayer.hurtTime == 0
            if (offGroundTicks == 1) {

                //4 works perfectly and 5 seems to work decently
                //or 2 instead of 3
                mc.thePlayer.motionY = MovementUtils.predictedMotion(mc.thePlayer.motionY, jumps % 2 == 0 ? 3 : 5);
            }
        }

        super.onMotionEvent(event);
    }



    @Override
    public void onDisable() {
        offGroundTicks = 0;
        mc.timer.timerSpeed = 1.0F;

        super.onDisable();
    }

    @Override
    public void onEnable() {
        jumps = 0;
        mc.timer.timerSpeed = 1.0F;

        super.onEnable();
    }
}
