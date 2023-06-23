package dev.tenacity.module.impl.render.targethud;

import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.utils.animations.ContinualAnimation;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.GLUtil;
import dev.tenacity.utils.render.GradientUtil;
import dev.tenacity.utils.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.text.DecimalFormat;

public class ZeroDayTargetHUD extends TargetHUD {

    private final ContinualAnimation animation = new ContinualAnimation();
    private final DecimalFormat DF_1O = new DecimalFormat("0.#");

    public ZeroDayTargetHUD() {
        super("ZeroDay");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        IFontRenderer fr = mc.fontRendererObj;

        float width = 145;
        double healthPercentage = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);
        setWidth(width);
        setHeight(60);

        Color c1 = ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), alpha);
        Color c2 = ColorUtil.applyOpacity(HUDMod.getClientColors().getSecond(), alpha);
        int textColor = ColorUtil.applyOpacity(-1, alpha);

        // Draw background
        Gui.drawRect2(x, y, width, getHeight(), new Color(0, 0, 0, (0.6F * alpha)).getRGB());

        // damage anim
        float endWidth = (float) Math.max(0, width * healthPercentage);
        animation.animate(endWidth, 20);

        float healthWidth = animation.getOutput();

        Gui.drawGradientRectSideways2(
                x, y + getHeight() - 2,
                healthWidth, 2,
                new Color(255, 0, 0).getRGB(),
                new Color(255, 255, 0).getRGB()
        );

        // Draw player
        RenderUtil.resetColor();
        RenderUtil.color(-1, alpha);
        GuiInventory.drawEntityOnScreen((int) x + 17, (int) y + 50, 20, target.rotationYaw, target.rotationPitch, target);

        // Draw name
        RenderUtil.resetColor();
        GLUtil.startBlend();
        fr.drawStringWithShadow(target.getName(), x + 34, y + 4, ColorUtil.applyOpacity(-1, (float) Math.max(.1, alpha)));

        // Draw Health Circle
        RenderUtil.drawCircleNotSmoothHollow(
                x, y,
                5, 2,
                new Color(57, 57, 57).getRGB()
        );
    }


    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        Gui.drawRect2(x, y, getWidth(), getHeight(), ColorUtil.applyOpacity(Color.BLACK.getRGB(), alpha));
    }

}
