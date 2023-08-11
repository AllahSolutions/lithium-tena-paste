package dev.tenacity.module.impl.movement.speeds.impl;

import dev.tenacity.event.impl.player.BoundingBoxEvent;
import dev.tenacity.event.impl.player.movement.correction.StrafeEvent;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.utils.player.MovementUtils;
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
        if(mc.thePlayer.onGround) {
            MovementUtils.strafe(MovementUtils.getBaseMoveSpeed() * 1.01f);
        }

        switch (offGroundTicks) {
            case 0:
                mc.thePlayer.jump();


                break;
            case 5:
                mc.thePlayer.motionY = mc.thePlayer.motionY - 0.3;
                break;
        }










        super.onStrafeEvent(event);
    }

    @Override
    public void onBoundingBoxEvent(BoundingBoxEvent event) {

            final AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(-5, 2, -5, 5, 2, 5).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ());
            event.setBoundingBox(axisAlignedBB);

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
