package dev.tenacity.module.impl.movement.speeds.impl;

import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.event.impl.player.MoveEvent;
import dev.tenacity.module.impl.combat.TargetStrafe;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.player.PlayerUtils;
import net.minecraft.potion.Potion;

public class InvadedSpeed extends SpeedMode {


    public int onGroundTicks, offGroundTicks;
    private TargetStrafe targetStrafe;

    public InvadedSpeed() {
        super("Invaded");
        targetStrafe = new TargetStrafe();
    }


    @Override
    public void onMoveEvent(MoveEvent event) {


        if (mc.thePlayer.hurtTime > 1) {
            targetStrafe.strafe(event, 1f);
        } else {
            targetStrafe.strafe(event, MovementUtils.getBaseMoveSpeed());
        }


    }


    @Override
    public void onMotionEvent(MotionEvent event) {
        if (event.isPost() || !MovementUtils.isMoving() || MovementUtils.isInLiquid()) {
            return;
        }



        if(mc.thePlayer.hurtTime >1) {

            MovementUtils.strafe(1f);
        } else {
            MovementUtils.strafe(MovementUtils.getBaseMoveSpeed());
        }


        if (mc.thePlayer.onGround) {

            mc.thePlayer.jump();

        }
        super.onMotionEvent(event);
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;

       // mc.thePlayer.motionX = 0;
     //   mc.thePlayer.motionZ = 0;

        super.onDisable();
    }
}
