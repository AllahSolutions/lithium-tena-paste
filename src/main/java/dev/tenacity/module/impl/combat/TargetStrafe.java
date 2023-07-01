package dev.tenacity.module.impl.combat;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.event.impl.player.MoveEvent;
import dev.tenacity.event.impl.render.Render3DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.combat.KillAura;
import dev.tenacity.module.impl.movement.Flight;
import dev.tenacity.module.impl.movement.Speed;
import dev.tenacity.module.impl.movement.Test;
import dev.tenacity.module.settings.ParentAttribute;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ColorSetting;
import dev.tenacity.module.settings.impl.MultipleBoolSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.Validator.EntityValidator;
import dev.tenacity.utils.Validator.impl.VoidCheck;
import dev.tenacity.utils.Validator.impl.WallCheck;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.player.RotationUtils;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.server.ServerUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public final class TargetStrafe extends Module {

    private final NumberSetting radius = new NumberSetting(("Radius"), 2.0, 0.1, 4.0, 0.1);
    private final BooleanSetting render = new BooleanSetting("Render", true);

    private final EntityValidator targetValidator;
    private KillAura aura;
    private int direction = -1;

    private static int strafe = 1;
    private static int position;

    private final DecelerateAnimation animation = new DecelerateAnimation(250, radius.getValue(), Direction.FORWARDS);
    private boolean returnState;

    public TargetStrafe() {
        super("TargetStrafe", Category.COMBAT, "strafe around targets");
        addSettings(radius, render);
        this.targetValidator = new EntityValidator();
        this.targetValidator.add(new VoidCheck());
        this.targetValidator.add(new WallCheck());
    }

    @Override
    public void onEnable() {
        if (this.aura == null) {
            this.aura = ((KillAura) Tenacity.INSTANCE.getModuleCollection().getModule(KillAura.class));
        }
    }

    @Override
    public void onMoveEvent(MoveEvent event) {
        if (mc.thePlayer.isCollidedHorizontally || (!new VoidCheck().validate(mc.thePlayer))) {
            if (mc.gameSettings.keyBindLeft.isPressed()) {
                this.direction = 1;
            }
            if (mc.gameSettings.keyBindRight.isPressed()) {
                this.direction = -1;
            }
        }
    }

    private void switchDirection() {
        this.direction = this.direction == 1 ? -1 : 1;
    }

    public void strafe(MoveEvent event, double moveSpeed) {
        if (canStrafe() && KillAura.target != null) {
            if (mc.gameSettings.keyBindForward.isKeyDown()) {
                EntityLivingBase target = KillAura.target;
                float[] rotations = RotationUtils.getRotations(target.posX, target.posY, target.posZ);
                if ((double) mc.thePlayer.getDistanceToEntity(target) <= this.radius.getValue().floatValue()) {
                    MovementUtils.setSpeed(event, moveSpeed, rotations[0], this.direction, 0.0);
                } else {
                    MovementUtils.setSpeed(event, moveSpeed, rotations[0], this.direction, 1.0);
                }
            }
        }
    }

    @Override
    public void onRender3DEvent(Render3DEvent event) {
        if (canStrafe() && ((boolean) this.render.isEnabled() && KillAura.target != null)) {
            this.drawCircle(KillAura.target, this.radius.getValue().floatValue() + 1f, 0xFF000000);
            this.drawCircle(KillAura.target, this.radius.getValue().floatValue(), 0xffffff);
        }
    }

    private void drawCircle(Entity entity, float lineWidth, int color) {
        if (entity == null) return;

        GL11.glPushMatrix();
        RenderUtil.color(color, (float) (radius.getValue().doubleValue()));
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        GL11.glBegin(GL11.GL_LINE_STRIP);
        EntityLivingBase target = KillAura.target;
        float partialTicks = mc.timer.elapsedPartialTicks;
        double rad = radius.getValue().doubleValue();
        double d = (Math.PI * 2.0) / 26;

        double posX = target.posX, posY = target.posY, posZ = target.posZ;
        double lastTickX = target.lastTickPosX, lastTickY = target.lastTickPosY, lastTickZ = target.lastTickPosZ;
        double renderPosX = mc.getRenderManager().renderPosX, renderPosY = mc.getRenderManager().renderPosY, renderPosZ = mc.getRenderManager().renderPosZ;

        double y = lastTickY + (posY - lastTickY) * partialTicks - renderPosY;
        for (double i = 0; i < Math.PI * 2.0; i += d) {
            double x = lastTickX + (posX - lastTickX) * partialTicks + StrictMath.sin(i) * rad - renderPosX;
            double z = lastTickZ + (posZ - lastTickZ) * partialTicks + StrictMath.cos(i) * rad - renderPosZ;
            GL11.glVertex3d(x, y, z);
        }
        double x = lastTickX + (posX - lastTickX) * partialTicks - renderPosX;
        double z = lastTickZ + (posZ - lastTickZ) * partialTicks + rad - renderPosZ;
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }

    public boolean canStrafe() {
        return KillAura.target != null && this.isEnabled() || mc.gameSettings.keyBindJump.isPressed();
    }
}
