package dev.tenacity.module.impl.combat;

import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;


public class NoClickDelay extends Module {


    public NoClickDelay() {
        super("No Click Delay", Category.COMBAT, "Remove the click delay from 1.8");
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            mc.leftClickCounter = 0;
        }
        super.onMotionEvent(e);
    }


}
