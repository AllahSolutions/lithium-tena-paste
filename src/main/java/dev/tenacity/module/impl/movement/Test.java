package dev.tenacity.module.impl.movement;

import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.player.DamageUtils;
import dev.tenacity.utils.player.MovementUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class Test extends Module {
    private boolean damaged = false;



    public Test() {
        super("Test", Category.MOVEMENT, "Test Module");

    }










    @Override
    public void onMotionEvent(MotionEvent e) {
        if(mc.thePlayer.hurtTime >1) {
            damaged = true;
        }

        if(mc.thePlayer.hurtTime >1) {
            MovementUtils.strafe(8);

        } else{
            MovementUtils.strafe(MovementUtils.getSpeed() * (float) 1.01);
        }
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {

        if (e.getPacket() instanceof S12PacketEntityVelocity) {
         //   mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));


            S12PacketEntityVelocity velocityPacket = (S12PacketEntityVelocity) e.getPacket();
            //FIREBALL SHIT
            //high
            //velocityPacket.motionX = velocityPacket.motionX * 6;
         //  velocityPacket.motionY = velocityPacket.motionY * 5;
          //  velocityPacket.motionZ = velocityPacket.motionZ * 6;

            velocityPacket.motionX = velocityPacket.motionX * 7;
            velocityPacket.motionY = velocityPacket.motionY * 4;
            velocityPacket.motionZ = velocityPacket.motionZ * 7;

         //   velocityPacket.motionX = velocityPacket.motionX * 20;
        //    velocityPacket.motionY = velocityPacket.motionY * 3;
           // velocityPacket.motionZ = velocityPacket.motionZ * 20;


        }

    }

    @Override
    public void onEnable() {
        damaged = false;
        super.onEnable();

    }



}
