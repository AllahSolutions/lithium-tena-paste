package dev.tenacity.module.impl.movement.longs.impl;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.impl.movement.LongJump;
import dev.tenacity.module.impl.movement.longs.LongJumpMode;
import dev.tenacity.utils.player.ChatUtil;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.server.PacketUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class VulcanLongJump extends LongJumpMode {

    private boolean ignore;
    private int ticks;

    public VulcanLongJump() {
        super("Vulcan");
    }

    @Override
    public void onDisable() {
        mc.thePlayer.posX = 0;
        mc.thePlayer.posZ = 0;
        super.onDisable();
    }

    @Override
    public void onEnable() {

        if (!mc.thePlayer.onGround) {
           // if(Tenacity.INSTANCE.isEnabled(LongJump.class)) {
              //  Tenacity.INSTANCE.getModuleCollection().getModule(LongJump.class).setEnabled(false);

           // }
        }

        ignore = false;
        ticks = 0;
        super.onEnable();
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        if(e.isPre()) {
            ticks++;

            if (mc.thePlayer.fallDistance > 0 && ticks % 2 == 0 && mc.thePlayer.fallDistance < 2.2) {
                mc.thePlayer.motionY += 0.14F;
            }

            switch (ticks) {
                case 1:


                    PacketUtils.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
                    PacketUtils.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0784000015258789, mc.thePlayer.posZ, mc.thePlayer.onGround));
                    PacketUtils.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));

                    ignore = true;
                    MovementUtils.strafe(7.9f);
                    mc.thePlayer.motionY = 0.42F;
                    break;

                case 2:
                    //orginal 0.1
                    mc.thePlayer.motionY += 0.1F;
                    MovementUtils.strafe(2.79f);
                    break;

                case 3:
                    MovementUtils.strafe(2.56f);
                    break;

                case 4:
                    e.setOnGround(true);
                    mc.thePlayer.onGround = true;
                    MovementUtils.strafe(0.49f);
                    break;

                case 5:

                    MovementUtils.strafe(0.59f);
                    break;

                case 6:

                    MovementUtils.strafe(0.3f);
                    break;


            }
        }
        super.onMotionEvent(e);
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof S08PacketPlayerPosLook && ignore) {
            event.cancel();
            ignore = false;
        }
        super.onPacketReceiveEvent(event);
    }



}