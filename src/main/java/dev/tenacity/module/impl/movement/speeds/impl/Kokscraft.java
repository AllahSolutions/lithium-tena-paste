package dev.tenacity.module.impl.movement.speeds.impl;

import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.player.movement.correction.StrafeEvent;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.utils.player.ChatUtil;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.player.PlayerUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

import java.util.Random;

public class Kokscraft extends SpeedMode {
    int jumps;
    private int offGroundTicks;
    public Kokscraft() {
        super("Kokscraft");
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
