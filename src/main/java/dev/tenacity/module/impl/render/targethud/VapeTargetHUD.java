package dev.tenacity.module.impl.render.targethud;

import dev.tenacity.utils.animations.ContinualAnimation;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.render.StencilUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class VapeTargetHUD extends TargetHUD {

    public VapeTargetHUD() {
        super("Vape");
    }

    private final ContinualAnimation healthAnim = new ContinualAnimation();
    private final ContinualAnimation absAnim = new ContinualAnimation();

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        setWidth(110);
        setHeight(40);

        if (target instanceof AbstractClientPlayer) {
            GL11.glPushMatrix();
            GlStateManager.disableBlend();
            StencilUtil.initStencilToWrite();
            RenderUtil.renderRoundedRect(x + 3, y + 4.5f, 31, 31, 1, Color.BLACK.getRGB());
            StencilUtil.readStencilBuffer(1);

            renderPlayer2D(x + 3, y + 4.5f, 31f, 31f, (AbstractClientPlayer) target);

            StencilUtil.uninitStencilBuffer();
            GlStateManager.enableBlend();
            GL11.glPopMatrix();
        } else {
            GL11.glPushMatrix();
            GL11.glTranslated(x + 3, y + 4.5f, 0);
            GlStateManager.disableBlend();
            RenderUtil.scissorStart(x + 3, y + 4.5f, 31, 31);

            float pitch = target.rotationPitchHead;
            target.rotationPitchHead = 0;
            GuiInventory.drawEntityOnScreen(0, 0, 14, -100.0f, 0f, target);
            target.rotationPitchHead = pitch;

            RenderUtil.scissorEnd();
            GlStateManager.enableBlend();
            GL11.glPopMatrix();
        }

        tenacityBoldFont12.drawString(target.getName(), x + 36.5f, y + (12.6f - tenacityBoldFont12.getHeight()) / 2f, -1);

        float targetHealth = target.getHealth();
        float targetMaxHealth = target.getMaxHealth();
        float targetAbsorptionAmount = target.getAbsorptionAmount();
        float targetHealthDWithAbs = targetHealth / Math.max(targetMaxHealth + targetAbsorptionAmount, 1.0f);
        float targetHealthD = targetHealth / Math.max(targetMaxHealth, 1.0f);

        healthAnim.animate(targetHealthDWithAbs, 18);

        Color color = ColorUtil.blendColors(new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { new Color(250, 50, 56), new Color(236, 129, 44), new Color(5, 134, 105) }, targetHealthD);

        RoundedUtil.drawRound(x + 37f, y + 12.6f, 68, 2f, 1, new Color(43, 42, 43));
        RoundedUtil.drawRound(x + 37f, y + 12.6f, 68f * healthAnim.getOutput(), 2f, 1, color);

        if (targetAbsorptionAmount > 0) {
            float absLength = 68f * (targetAbsorptionAmount / (targetMaxHealth + targetAbsorptionAmount));
            absAnim.animate(absLength, 18);
            RoundedUtil.drawRound(x + 37f + 68f * healthAnim.getOutput() - absAnim.getOutput(), y + 12.6f, absAnim.getOutput(), 2f, 1, new Color(0xFFAA00));
        }

        String hp = String.format("%.1f", targetHealth + targetAbsorptionAmount) + " hp";
        tenacityFont12.drawString(hp, x + 105 - tenacityFont12.getStringWidth(hp), y + (12.6f - tenacityFont12.getHeight())/ 2f, -1);

        GL11.glPushMatrix();
        GL11.glTranslatef(x + 36.5f, y + 18.5f, 0);
        GL11.glScaled(0.8, 0.8, 0.8);

        if (target instanceof EntityPlayer) {
            ArrayList<ItemStack> arrayList = new ArrayList<>(Arrays.asList(((EntityPlayer) target).inventory.armorInventory));
            if (((EntityPlayer) target).inventory.getCurrentItem() != null)
                arrayList.add(((EntityPlayer) target).inventory.getCurrentItem());

            if (arrayList.size() >= 1) {
                int n = 0;
                Collections.reverse(arrayList);
                for (ItemStack item : arrayList) {
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(item, n, 0);
                    RenderHelper.disableStandardItemLighting();
                    n += 17;
                }
            }
        }
        GL11.glScalef(1, 1, 1);
        GL11.glPopMatrix();
    }

    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        RoundedUtil.drawRound(x + 3, y + 4.5f, 31, 31, 4, Color.BLACK);
        RoundedUtil.drawRound(x + 37f, y + 12.6f, 68, 2f, 1, Color.BLACK);
    }
}
