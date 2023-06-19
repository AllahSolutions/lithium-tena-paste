package dev.tenacity.module.impl.movement.longs.impl;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.game.TeleportEvent;
import dev.tenacity.event.impl.player.*;
import dev.tenacity.module.impl.movement.LongJump;
import dev.tenacity.module.impl.movement.longs.LongJumpMode;
import dev.tenacity.utils.player.*;
import dev.tenacity.utils.server.*;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

public class LatestNCPLongJump extends LongJumpMode {

    public float speed;
    public int offGroundTicks, onGroundTicks;

    public boolean started, clipped, under, teleport;

    public LatestNCPLongJump() {
        super("Latest NCP");
    }

    @Override
    public void onEnable() {
        speed = 0;

        under = false;
        started = false;
        clipped = false;
        teleport = false;

        ChatUtil.print("Insane Fly");

        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;

        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;

        super.onDisable();
    }

    @Override
    public void onTeleportEvent(TeleportEvent event) {

        if (teleport) {
            event.cancel();
            teleport = false;
        }

        super.onTeleportEvent(event);
    }

    @Override
    public void onStrafeEvent(StrafeEvent event) {

        if (mc.thePlayer.onGround) {
            this.onGroundTicks += 1;
            this.offGroundTicks = 0;
        } else {
            this.onGroundTicks = 0;
            this.offGroundTicks += 1;
        }

        if (
                !Tenacity.INSTANCE.getModuleCollection().get(LongJump.class)
                        .getModeSetting("Latest NCP Mode").is("Clip")
        ) {
            return;
        }

        AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, 1, 0);

        if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty() || started) {
            switch (offGroundTicks) {
                case 0:
                    if (!under || !clipped)
                        break;

                    MovementUtils.setSpeed(10);
                    mc.thePlayer.motionY = 0.42f;

                    started = true;
                    under = false;

                    break;

                case 1:
                    if (started)
                        MovementUtils.setSpeed(9.6);

                    break;

                default:
                    break;
            }
        } else {
            under = true;

            if (clipped) {
                return;
            }

            clipped = true;

            PacketUtils.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
            PacketUtils.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
            PacketUtils.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));

            teleport = true;
        }

        MovementUtils.strafe(
                MovementUtils.getSpeed()
        );

        mc.timer.timerSpeed = 0.4f;

        super.onStrafeEvent(event);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {

        final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, 1, 0);

        if (
                !Tenacity.INSTANCE.getModuleCollection().get(LongJump.class)
                        .getModeSetting("Latest NCP Mode").is("Normal")
        ) {
            return;
        }

        if (started) {
            mc.thePlayer.motionY += 0.025;
            speed *= 0.935F;

            MovementUtils.strafe(speed);

            if (mc.thePlayer.motionY < -0.5 && !PlayerUtils.isBlockUnder()) {
                Tenacity.INSTANCE.getModuleCollection().getModule(LongJump.class).toggle();
            }
        }

        if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty() && !started) {
            started = true;
            speed = 9;

            mc.thePlayer.jump();
            MovementUtils.strafe(speed);
        }

        super.onMotionEvent(event);
    }

}