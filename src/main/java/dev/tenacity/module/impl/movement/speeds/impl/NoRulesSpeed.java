package dev.tenacity.module.impl.movement.speeds.impl;

import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.utils.player.ChatUtil;
import dev.tenacity.utils.player.MovementUtils;

public class NoRulesSpeed extends SpeedMode {

    public NoRulesSpeed() {
        super("Cum");
    }

    @Override
    public void onMotionEvent(MotionEvent event) {

        if (event.isPost() || !MovementUtils.isMoving() || MovementUtils.isInLiquid()) {
            return;
        }

        if(mc.thePlayer.isCollidedHorizontally) {
            return;
        }


        if (mc.thePlayer.onGround) {
            MovementUtils.strafe(0.46f);
            //0.40 , 0.45
            mc.thePlayer.jump();
          // mc.thePlayer.motionY = 0.42f;
        } else {
              mc.thePlayer.motionY = mc.thePlayer.motionY - 5;
          //  event.setY(-mc.thePlayer.fallDistance);
        }






        super.onMotionEvent(event);
    }

    @Override
    public void onDisable() {

        mc.timer.timerSpeed = 1.0F;

        super.onDisable();
    }
}
