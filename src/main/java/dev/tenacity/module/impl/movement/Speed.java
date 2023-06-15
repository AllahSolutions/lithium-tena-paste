package dev.tenacity.module.impl.movement;

import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.time.TimerUtil;

import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unused")
public final class Speed extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "BlocksMC", "BlocksMC");

    private final TimerUtil timerUtil = new TimerUtil();
    private final float r = ThreadLocalRandom.current().nextFloat();
    private double speed, lastDist;
    private float speedChangingDirection;
    private int stage;
    private boolean strafe, wasOnGround;
    private double moveSpeed;
    private int inAirTicks;

    public Speed() {
        super("Speed", Category.MOVEMENT, "Makes you go faster");
        this.addSettings(mode);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        this.setSuffix(mode.getMode());

        if (event.isPost()) {
            return;
        }

        if (!MovementUtils.isMoving() || mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) {
            return;
        }

        switch (mode.getMode()) {
            case "BlocksMC":

                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }

                MovementUtils.strafe(MovementUtils.getSpeed() - (float) (Math.random() - 0.5F) / 750F);

                break;
        }

    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;

        mc.thePlayer.speedInAir = 0.02F;
        mc.thePlayer.jumpMovementFactor = 0.02F;

        super.onDisable();
    }
}