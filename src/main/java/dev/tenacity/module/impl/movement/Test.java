package dev.tenacity.module.impl.movement;

import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class Test extends Module {



    public Test() {
        super("Test", Category.MOVEMENT, "Test Module");

    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {

        if (e.getPacket() instanceof S12PacketEntityVelocity) {
         //   mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));


            S12PacketEntityVelocity velocityPacket = (S12PacketEntityVelocity) e.getPacket();
            velocityPacket.motionX = velocityPacket.motionX * 5;
          //  velocityPacket.motionY = velocityPacket.motionY * 5;
            velocityPacket.motionZ = velocityPacket.motionZ * 5;


        }

    }



}
