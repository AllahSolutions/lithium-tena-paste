package dev.tenacity.module.impl.render.targethud;

import dev.tenacity.module.impl.combat.KillAura;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.utils.animations.ContinualAnimation;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.render.*;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.text.DecimalFormat;

public class AstolfoTargetHUD extends TargetHUD {

    private final ContinualAnimation animation = new ContinualAnimation();
    private final DecimalFormat DF_1O = new DecimalFormat("0.#");

    public AstolfoTargetHUD() {
        super("Astolfo");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        IFontRenderer fr = mc.fontRendererObj;
        //or 90
        float width = Math.max(50, fr.getStringWidth(target.getName()) + 100);
        double healthPercentage = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);
        setWidth(width);
        setHeight(55);

        Color c1 = ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), alpha);
        Color c2 = ColorUtil.applyOpacity(HUDMod.getClientColors().getSecond(), alpha);

        // Draw background
        RoundedUtil.drawRoundOutline(x, y, width, 50,2,0.2f, new Color(0, 0, 0, (0.6F * alpha)),HUDMod.getClientColors().getFirst());

        // Draw health bar (high quality code)
        RenderUtil.drawGradientRect(x + 34, y + 45, x + width - 4, y + 40, c1.darker().darker().darker().darker().getRGB(), c2.darker().darker().darker().darker().getRGB());
        if (target instanceof AbstractClientPlayer) {
            StencilUtil.initStencilToWrite();
            RenderUtil.renderRoundedRect(x + 3, y + 3, 36, 36, 3, -1);
            StencilUtil.readStencilBuffer(1);
            RenderUtil.color(-1, alpha);
            renderPlayer2D(x + 3, y + 3, 36, 36, (AbstractClientPlayer) target);
            StencilUtil.uninitStencilBuffer();
            GlStateManager.disableBlend();
        } else {
            FontUtil.lithiumBoldFont32.drawCenteredStringWithShadow("?", x + 20, y + 17 - FontUtil.lithiumBoldFont32.getHeight() / 2f, Color.white.getRGB());
        }
        // damage anim
        float endWidth = (float) Math.max(0, (width - 34) * healthPercentage);
        animation.animate(endWidth, 18);
        float healthWidth = animation.getOutput();

       // RenderUtil.drawGradientRect(x + 34, y + 45, x + 30 + healthWidth, y + 53, c1.darker().darker().getRGB(), c2.darker().darker().getRGB());
        RenderUtil.drawGradientRect(x + 3, y + 40, x + 32 + Math.min(endWidth, healthWidth), y + 48, c1.getRGB(), c2.getRGB());

        // Draw player


        // Draw name
        RenderUtil.resetColor();
        GLUtil.startBlend();
        if(KillAura.target != null) {
            if (mc.thePlayer.getHealth() > KillAura.target.getHealth()) {
                fr.drawStringWithShadow(EnumChatFormatting.GREEN + "Winning", x + 42, y + 15, ColorUtil.applyOpacity(-1, (float) Math.max(.1, alpha)));
            } else {
                fr.drawStringWithShadow(EnumChatFormatting.RED + "Losing", x + 42, y + 15, ColorUtil.applyOpacity(-1, (float) Math.max(.1, alpha)));
            }
        } else{
            fr.drawStringWithShadow("Neutral", x + 42, y + 15, ColorUtil.applyOpacity(-1, (float) Math.max(.1, alpha)));
        }
        fr.drawStringWithShadow(target.getName(), x + 42, y + 4, ColorUtil.applyOpacity(-1, (float) Math.max(.1, alpha)));
        //fr.drawStringWithShadow("nigger", x + 42, y + 23, ColorUtil.applyOpacity(-1, (float) Math.max(.1, alpha)));

        // Draw health
        float scale = 2.2F;
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        RenderUtil.setAlphaLimit(0);
        RenderUtil.resetColor();
        GlStateManager.popMatrix();
    }


    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        Gui.drawRect2(x, y, getWidth(), 45, ColorUtil.applyOpacity(Color.BLACK.getRGB(), alpha));
    }

}
