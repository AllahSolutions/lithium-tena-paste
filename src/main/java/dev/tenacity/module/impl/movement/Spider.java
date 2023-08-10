package dev.tenacity.module.impl.movement;

import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import net.minecraft.util.MathHelper;

import java.util.Random;

public final class Spider extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla","Vulcan", "Verus");

    public Spider() {
        super("Spider", Category.MOVEMENT, "Climbs you up walls like a spider");
        addSettings(mode);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        setSuffix(mode.getMode());
        if(mc.thePlayer.isCollidedHorizontally) {
            if(!mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) return;
            switch(mode.getMode()) {
                case "Vanilla":
                    mc.thePlayer.jump();
                    break;
                case "Verus":
                    if(mc.thePlayer.ticksExisted % 3 == 0)
                        mc.thePlayer.motionY = 0.42f;
                    break;
                case"Vulcan":
                 //   if(mc.thePlayer.ticksExisted % 2 == 0) {
                        mc.thePlayer.motionY = MathHelper.randomFloatClamp(new Random(),0.42f,0.60f);
                   // }
                    break;
            }
        }
    }
}
