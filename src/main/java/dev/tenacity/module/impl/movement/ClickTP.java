package dev.tenacity.module.impl.movement;

import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.event.impl.player.UpdateEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.utils.server.PacketUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Mouse;

public class ClickTP extends Module {



    public ClickTP() {
        super("ClickTP", Category.MOVEMENT, "Tp`s your where u click");
    }
    @Override
    public void onUpdateEvent(UpdateEvent e) {

        MovingObjectPosition ray = this.rayTrace(500.0);
        if (ray == null) {
            return;
        }
        if (Mouse.isButtonDown((int)1)) {
            double x_new = (double)ray.getBlockPos().getX() + 0.5;
            double y_new = ray.getBlockPos().getY() + 1;
            double z_new = (double)ray.getBlockPos().getZ() + 0.5;
            double distance = this.mc.thePlayer.getDistance(x_new, y_new, z_new);
            double d = 0.0;
            while (d < distance) {
                this.setPos(this.mc.thePlayer.posX + (x_new - (double)this.mc.thePlayer.getHorizontalFacing().getFrontOffsetX() - this.mc.thePlayer.posX) * d / distance, this.mc.thePlayer.posY + (y_new - this.mc.thePlayer.posY) * d / distance, this.mc.thePlayer.posZ + (z_new - (double)this.mc.thePlayer.getHorizontalFacing().getFrontOffsetZ() - this.mc.thePlayer.posZ) * d / distance);
                d += 2.0;
            }
            this.setPos(x_new, y_new, z_new);
            this.mc.renderGlobal.loadRenderers();
        }
        super.onUpdateEvent(e);
    }

    public MovingObjectPosition rayTrace(double blockReachDistance) {
        Vec3 vec3 = this.mc.thePlayer.getPositionEyes(1.0f);
        Vec3 vec4 = this.mc.thePlayer.getLookVec();
        Vec3 vec5 = vec3.addVector(vec4.xCoord * blockReachDistance, vec4.yCoord * blockReachDistance, vec4.zCoord * blockReachDistance);
        return this.mc.theWorld.rayTraceBlocks(vec3, vec5, !this.mc.thePlayer.isInWater(), false, false);
    }

    public void setPos(double x, double y, double z) {
        this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
        this.mc.thePlayer.setPosition(x, y, z);
    }




}
