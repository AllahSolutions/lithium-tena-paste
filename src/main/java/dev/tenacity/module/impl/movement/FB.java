package dev.tenacity.module.impl.movement;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.server.PacketUtils;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class FB extends Module {



    public FB() {
        super("FB", Category.MOVEMENT, "FB LONGJUMP FOR HYPIXCKEL");
    }
    @Override
    public void onMotionEvent(MotionEvent e) {

            PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
        super.onMotionEvent(e);
    }
    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
        if (e.getPacket() instanceof S12PacketEntityVelocity) {



            S12PacketEntityVelocity velocityPacket = (S12PacketEntityVelocity) e.getPacket();

            velocityPacket.motionX = velocityPacket.motionX * 7;

            velocityPacket.motionZ = velocityPacket.motionZ * 7;
        }
        super.onPacketReceiveEvent(e);




    }



}
