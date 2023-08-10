package dev.tenacity.module.impl.combat;

import dev.tenacity.Tenacity;

import dev.tenacity.event.impl.player.UpdateEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.render.Render3DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.Packet;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class BackTrack extends Module {
    public static EntityLivingBase target;
    public static List<Vec3> pastPositions = new ArrayList<>();
    public static List<Vec3> forwardPositions = new ArrayList<>();
    public static List<Vec3> positions = new ArrayList<>();
    private final Deque<Packet<?>> packets = new ArrayDeque<>();

    private final NumberSetting amount = new NumberSetting("Amount", 0, 100, 1, 1);
    private final NumberSetting forward = new NumberSetting("Forward", 0, 100, 1, 1);

    private int ticks;




    public BackTrack() {
        super("Back track", Category.COMBAT, "Fucks players in their last possition");
        addSettings(amount,forward);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        //if (mc.thePlayer.ticksExisted < 5) {
        //    onDisable();
       //     return;
      //  }

        if (target == null) return;

        pastPositions.add(new Vec3(target.posX, target.posY, target.posZ));

        final double deltaX = (target.posX - target.lastTickPosX) * 2;
        final double deltaZ = (target.posZ - target.lastTickPosZ) * 2;

        forwardPositions.clear();
        int i = 0;
        while (forward.getValue() > forwardPositions.size()) {
            i++;
            forwardPositions.add(new Vec3(target.posX + deltaX * i, target.posY, target.posZ + deltaZ * i));
        }

        while (pastPositions.size() > amount.getValue().intValue()) {
            pastPositions.remove(0);
        }

        positions.clear();
        positions.addAll(forwardPositions);
        positions.addAll(pastPositions);

        ticks++;
        super.onMotionEvent(event);
    }
    @Override
    public void onUpdateEvent(UpdateEvent event) {
        mc.theWorld.getLoadedEntityList();
        if(!Tenacity.INSTANCE.isEnabled(KillAura.class)) {
            target = null;
        }
        if (KillAura.target instanceof EntityPlayer) target = (EntityLivingBase) KillAura.target;
        ticks = 0;
        super.onUpdateEvent(event);
    }
    @Override
    public void onRender3DEvent(Render3DEvent event) {
        if (target != null && !positions.isEmpty()) {
            double expand = 0.14;

            double avgX = 0;
            double avgY = 0;
            double avgZ = 0;


            for (Vec3 position : positions) {
                avgX += position.xCoord;
                avgY += position.yCoord;
                avgZ += position.zCoord;
            }
            avgX /= positions.size();
            avgY /= positions.size();
            avgZ /= positions.size();


            AxisAlignedBB boundingBox = new AxisAlignedBB(
                    avgX - expand,
                    avgY - expand,
                    avgZ - expand,
                    avgX + expand,
                    avgY + expand,
                    avgZ + expand
            );

            Color color = HUDMod.getClientColors().getFirst();
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GL11.glDepthMask(false);

            GlStateManager.color(
                color.getRed() / 255.0F,
                    color.getGreen() / 255.0F,
                    color.getBlue() / 255.0F,
                    0.5F
            );

            RenderUtil.drawBoundingBox(mc.thePlayer.getEntityBoundingBox().offset(-mc.thePlayer.posX, -mc.thePlayer.posY, -mc.thePlayer.posZ)
                    .offset(avgX, avgY, avgZ));


            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GL11.glDepthMask(true);
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
            GlStateManager.resetColor();


           // Breadcrumbs.renderLine(positions, colors);
        }
        super.onRender3DEvent(event);
    }





    @Override
    public void onDisable() {
        target = null;
        positions.clear();
        pastPositions.clear();
        forwardPositions.clear();
        packets.clear();
        super.onDisable();
    }











}