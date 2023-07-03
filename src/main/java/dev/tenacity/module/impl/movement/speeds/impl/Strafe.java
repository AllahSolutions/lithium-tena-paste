package dev.tenacity.module.impl.movement.speeds.impl;

import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.event.impl.player.MoveEvent;
import dev.tenacity.module.impl.combat.TargetStrafe;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.utils.player.MovementUtils;
import net.minecraft.potion.Potion;

public class Strafe extends SpeedMode {





    public Strafe() {
        super("Strafe");
    }


    @Override
    public void onMoveEvent(MoveEvent event) {


        if (mc.thePlayer.hurtTime > 1) {
            TargetStrafe.strafe(event, MovementUtils.getBaseMoveSpeed() * 1.04f);
        } else {
            if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                TargetStrafe.strafe(event, MovementUtils.getBaseMoveSpeed() * 1.02f);
            } else {

                TargetStrafe.strafe(event, MovementUtils.getBaseMoveSpeed() * 1.01f);
            }
        }


    }


    @Override
    public void onMotionEvent(MotionEvent event) {
        if (event.isPost() || !MovementUtils.isMoving() || MovementUtils.isInLiquid()) {
            return;
        }

        if(mc.thePlayer.onGround) {
            MovementUtils.strafe(MovementUtils.getBaseMoveSpeed() * 1.04f);
        }





        if(mc.thePlayer.hurtTime >1) {

            MovementUtils.strafe(MovementUtils.getBaseMoveSpeed() * 1.04f);
        } else {
            if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                MovementUtils.strafe(MovementUtils.getBaseMoveSpeed() * 1.02f);

            } else{

                MovementUtils.strafe(MovementUtils.getBaseMoveSpeed() * 1.01f);
            }

        }


        if (mc.thePlayer.onGround) {

            mc.thePlayer.jump();

        }
        super.onMotionEvent(event);
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;

        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;

        super.onDisable();
    }
}
