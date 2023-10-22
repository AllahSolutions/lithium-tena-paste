package dev.tenacity.module.impl.movement;

import dev.tenacity.event.impl.game.world.TickEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.MathHelper;

import java.util.Random;

public class Eagle extends Module {

    private final TimerUtil timerend = new TimerUtil();
    private final TimerUtil timestart = new TimerUtil();

    private final BooleanSetting smart = new BooleanSetting("Smart", false);
    private final NumberSetting startmin = new NumberSetting("Start Min", 50, 500, 10, 10);
    private final NumberSetting startmax = new NumberSetting("Start Max", 50, 500, 10, 10);

    private final NumberSetting endtmin = new NumberSetting("End Min", 50, 500, 10, 10);
    private final NumberSetting endmax = new NumberSetting("End Max", 50, 500, 10, 10);
    private boolean sneaking;


    public Eagle() {
        super("Eagle", Category.MISC, "FUCKING NIGGER");
        addSettings(smart,startmin,startmax,endtmin,endmax);
    }

    @Override
    public void onDisable() {
        timerend.reset();
        timestart.reset();
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            mc.gameSettings.keyBindSneak.pressed = false;
        }
        super.onDisable();
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        if (mc.theWorld.getBlockState(new BlockPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock() instanceof BlockAir
                && mc.thePlayer.onGround) {
           // if(mc.thePlayer.ticksExisted % 2 == 0) {
            if(timestart.hasTimeElapsed(MathHelper.randomFloatClamp(new Random(),startmin.getValue().floatValue(),startmax.getValue().floatValue()))) {
                sneaking = true;
                timestart.reset();
                mc.gameSettings.keyBindSneak.pressed = true;
            }
          //  }
        } else {
            if (sneaking) {
                if(timestart.hasTimeElapsed(MathHelper.randomFloatClamp(new Random(),endtmin.getValue().floatValue(),endmax.getValue().floatValue()))) {
                    // if(mc.thePlayer.ticksExisted % 2 == 0) {
                    mc.gameSettings.keyBindSneak.pressed = false;
                    timerend.reset();
                    sneaking = false;
                }
             //   }
            } //else{
              //  timerend.reset();
          //  }
        }

        super.onMotionEvent(e);
    }



}
