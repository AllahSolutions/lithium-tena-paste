package dev.tenacity.module.impl.combat;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.player.ChatUtil;
import dev.tenacity.utils.player.MovementUtils;

import java.awt.*;
import java.util.Random;


public class Reach extends Module {
    public static final NumberSetting minreach = new NumberSetting("Min Reach", 3, 6, 3, 0.1);
    public static final NumberSetting chance = new NumberSetting("reach chance", 60, 100, 1, 1);
    public static final NumberSetting maxreach = new NumberSetting("Max Reach", 3, 6, 3f, 0.1);
    public static final BooleanSetting move = new BooleanSetting("Only Move", false);
    public static final BooleanSetting render = new BooleanSetting("Render", false);
    public Reach() {
        super("Reach", Category.COMBAT, "mhh i wonder");
        addSettings(maxreach,minreach,move,chance,render);
    }
    @Override
    public void onRender2DEvent(Render2DEvent e) {
        if(render.isEnabled())
        mc.fontRendererObj.drawStringWithShadow("Reach: " + getReachAmount(), 4, 15, Color.white);
        super.onRender2DEvent(e);
    }
    @Override
    public void onMotionEvent(MotionEvent e) {
        this.setSuffix(minreach.getValue().floatValue() + " - " + maxreach.getValue().floatValue());
        super.onMotionEvent(e);
    }

    public static float getReachAmount() {
        Random random = new Random();
        if (random.nextInt(100) >= (Integer)chance.getValue().intValue()) return 3.0f;
        if (!Tenacity.INSTANCE.isEnabled(Reach.class)) return 3.0f;
        if (Reach.mc.thePlayer == null) return 3.0f;
        if (!MovementUtils.isMoving()) {
            if (move.isEnabled() != false) return 3.0f;
        }
        float actualreach = maxreach.getValue().floatValue() - minreach.getValue().floatValue();
        actualreach = (float)((double)actualreach * Math.abs(random.nextGaussian()));
        if(mc.thePlayer.isSwingInProgress) {
            if (mc.thePlayer.ticksExisted % 10 == 0) {
               // ChatUtil.print(actualreach + minreach.getValue().floatValue());
            }
        }
        return actualreach + minreach.getValue().floatValue();
    }


}