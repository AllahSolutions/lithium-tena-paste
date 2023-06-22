package dev.tenacity.utils.player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Advancedrots {
    private static final Minecraft mc = Minecraft.getMinecraft();

    

    public static Vec3 getBestHitVec(Entity entity) {
        Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0f);
        float f11 = entity.getCollisionBorderSize();
        AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox().expand((double)f11, (double)f11, (double)f11);
        double ex = MathHelper.clamp_double((double)positionEyes.xCoord, (double)entityBoundingBox.minX, (double)entityBoundingBox.maxX);
        double ey = MathHelper.clamp_double((double)positionEyes.yCoord, (double)entityBoundingBox.minY, (double)entityBoundingBox.maxY);
        double ez = MathHelper.clamp_double((double)positionEyes.zCoord, (double)entityBoundingBox.minZ, (double)entityBoundingBox.maxZ);
        return new Vec3(ex, ey, ez);
    }

    public static float updateRotation(float current, float calc, float maxDelta) {
        float f = MathHelper.wrapAngleTo180_float((float)(calc - current));
        if (f > maxDelta) {
            f = maxDelta;
        }
        if (f < -maxDelta) {
            f = -maxDelta;
        }
        return current + f;
    }

    public static float[] mouseSens(float yaw, float pitch, float lastYaw, float lastPitch) {
        if ((double)Advancedrots.mc.gameSettings.mouseSensitivity == 0.5) {
            Advancedrots.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        if (yaw == lastYaw && pitch == lastPitch) {
            return new float[]{yaw, pitch};
        }
        float f1 = Advancedrots.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float f2 = f1 * f1 * f1 * 8.0f;
        int deltaX = (int)((6.667 * (double)yaw - 6.667 * (double)lastYaw) / (double)f2);
        int deltaY = (int)((6.667 * (double)pitch - 6.667 * (double)lastPitch) / (double)f2) * -1;
        float f5 = (float)deltaX * f2;
        float f3 = (float)deltaY * f2;
        yaw = (float)((double)lastYaw + (double)f5 * 0.15);
        float f4 = (float)((double)lastPitch - (double)f3 * 0.15);
        pitch = MathHelper.clamp_float((float)f4, (float)-90.0f, (float)90.0f);
        return new float[]{yaw, pitch};
    }


    public static float[] basicRotation(Entity entity, float currentYaw, float currentPitch, boolean random) {
        Vec3 ePos = Advancedrots.getBestHitVec(entity);
        double x = ePos.xCoord - mc.thePlayer.posX;
        double y = ePos.yCoord - (mc.thePlayer.posY + (double)Advancedrots.mc.thePlayer.getEyeHeight());
        double z = ePos.zCoord - Advancedrots.mc.thePlayer.posZ;
        float calcYaw = (float)(MathHelper.func_181159_b((double)z, (double)x) * 180.0 / Math.PI - 90.0);
        float calcPitch = (float)(-(MathHelper.func_181159_b((double)y, (double)MathHelper.sqrt_double((double)(x * x + z * z))) * 180.0 / Math.PI));
        float yaw = Advancedrots.updateRotation(currentYaw, calcYaw, 180.0f);
        float pitch = Advancedrots.updateRotation(currentPitch, calcPitch, 180.0f);
        if (random) {
            yaw = (float)((double)yaw + ThreadLocalRandom.current().nextGaussian());
            pitch = (float)((double)pitch + ThreadLocalRandom.current().nextGaussian());
        }
        return Advancedrots.mouseSens(yaw, pitch, currentYaw, currentPitch);
    }

    public static double func_181159_b(double d1, double d2) {
        return Math.atan2(d2, d1);
    }

}
