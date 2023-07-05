package dev.tenacity.event.impl.player;

import dev.tenacity.event.Event;
import net.minecraft.client.Minecraft;

import java.sql.Wrapper;

public class LegitClick extends Event  {
    private final Minecraft mc = Minecraft.getMinecraft();

    public static double x;
    public static double z;
    private float yaw;
    private float pitch;
    public static double y;
    private boolean ground;

    public LegitClick() {

    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        mc.thePlayer.renderYawOffset = yaw;
        mc.thePlayer.rotationYawHead = yaw;
        this.yaw = yaw;
    }

    public void setYawhead(float yaw) {
        mc.thePlayer.rotationYawHead = yaw;
        this.yaw = yaw;
    }




    public void setYawhead2(float yaw) {
        if(mc.thePlayer.rotationPitchHead > 90) {
            mc.thePlayer.renderYawOffset = yaw;


        }
        mc.thePlayer.rotationYawHead = yaw;
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        mc.thePlayer.rotationPitchHead = pitch;
        this.pitch = pitch;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isOnground() {
        return this.ground;
    }

    public void setOnground(boolean ground) {
        this.ground = ground;
    }

    public void setXspeed(double x) {
        LegitClick.x = x;
    }



    public void setYsped(double y) {
        LegitClick.y = y;
    }


    public void setZ(double z) {
        LegitClick.z = z;
    }

    public void setX(double x) {
        LegitClick.x = x;
    }
}
