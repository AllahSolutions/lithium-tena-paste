package dev.tenacity.module.impl.movement;

import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.event.impl.player.MoveEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.combat.TargetStrafe;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.utils.misc.Random;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unused")
public final class Speed extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "BlocksMC","Vanilla","NoRule","Watchdog","Strafe", "BlocksMC");

    private final TimerUtil timerUtil = new TimerUtil();
    private final float r = ThreadLocalRandom.current().nextFloat();
    private double speed, lastDist;
    private float movementSpeed = 0;
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
    public void onMoveEvent(MoveEvent e) {
        switch (mode.getMode()) {
            case "Vanilla":


                TargetStrafe.strafe(e, 1);
                MovementUtils.strafe(1);

                break;


            case"Strafe":

                final boolean haspeed = mc.thePlayer.isPotionActive(Potion.moveSpeed);
                if(haspeed) {

                    

                    TargetStrafe.strafe(e, MovementUtils.getSpeed() * 1.04);
                    MovementUtils.strafe(MovementUtils.getSpeed() * 1.04f);
                } else{
                    TargetStrafe.strafe(e, MovementUtils.getSpeed() * 1.02f);
                    MovementUtils.strafe(MovementUtils.getSpeed() * 1.02f);
                }


                break;

        }

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
                if (mc.thePlayer.hurtTime > 2) {
                     MovementUtils.strafe(0.35f);
                }

                if (mc.thePlayer.onGround) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
                } else {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
                }

                MovementUtils.strafe(MovementUtils.getSpeed() - (float) (Math.random() - 0.5F) / 2500);

                break;

            case "NoRule":
                 mc.timer.timerSpeed = 1.3f;

                if (mc.thePlayer.onGround) {
                    MovementUtils.strafe(1f);
                    mc.thePlayer.jump();
                }

                MovementUtils.strafe(0.46f);
                break;

            case"Strafe":
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }



                break;

            case "Watchdog":
                if (mc.thePlayer.onGround) {
                    MovementUtils.strafe(MovementUtils.getSpeed());
                    //30
                    mc.thePlayer.jump();
                }

                break;

            case "Vanilla":
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }




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