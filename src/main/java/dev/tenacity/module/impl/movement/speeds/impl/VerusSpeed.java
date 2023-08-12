package dev.tenacity.module.impl.movement.speeds.impl;

import dev.tenacity.event.impl.player.BoundingBoxEvent;
import dev.tenacity.event.impl.player.movement.correction.StrafeEvent;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.utils.player.MovementUtils;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;

public class VerusSpeed extends SpeedMode {
    int jumps;
    private int offGroundTicks;
    public VerusSpeed() {
        super("Verus");
    }



    @Override
    public void onStrafeEvent(StrafeEvent event) {


        if (mc.thePlayer.onGround) {
            offGroundTicks = 0;

        } else {
            offGroundTicks++;
        }
        if(!MovementUtils.hasAppliedSpeedII(mc.thePlayer)) {
            if (!mc.thePlayer.onGround) {
                if (!mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    MovementUtils.strafe(0.37f);
                    //0.36 might be the max value
                } else {
                    MovementUtils.strafe(0.43f);
                }
            }
        } else{
            MovementUtils.strafe(0.49f);
            //0.48

        }
        if(!MovementUtils.hasAppliedSpeedII(mc.thePlayer)) {
            if (mc.thePlayer.onGround) {
                if (!mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    MovementUtils.strafe(0.55f);
                    //0.50
                } else {
                    if (mc.gameSettings.keyBindForward.isPressed()) {
                        MovementUtils.strafe(0.63f);
                    } else {
                        //  MovementUtils.strafe(MovementUtils.getBaseMoveSpeed() - 0.2f);
                    }
                }
            }
        } else{
            if(mc.thePlayer.onGround) {
                MovementUtils.strafe(0.79f);
                //0.80 kindo flags
                //0.79
                //0.78
                //0.75
                //0.72
                //0.70
                //might be able to go a bit more
            }

        }
      //  if(mc.thePlayer.onGround) {
        if (mc.thePlayer.onGround) {
          mc.thePlayer.motionY = 0.42f;
        }


       
      //  }

    










        super.onStrafeEvent(event);
    }

    @Override
    public void onBoundingBoxEvent(BoundingBoxEvent event) {



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
