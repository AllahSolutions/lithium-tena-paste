package dev.tenacity.module.impl.combat;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.player.UpdateEvent;
import dev.tenacity.event.impl.player.movement.MoveEvent;
import dev.tenacity.event.impl.player.movement.correction.JumpEvent;
import dev.tenacity.event.impl.player.movement.correction.StrafeEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.movement.Flight;
import dev.tenacity.module.impl.movement.Scaffold;
import dev.tenacity.module.impl.movement.Speed;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.player.rotations.KillauraRotationUtil;
import net.minecraft.entity.EntityLivingBase;



public final class TargetStrafe extends Module {
    private KillAura aura;
    private int direction = -1;

    private boolean collidedLeft = false;
    private boolean collidedRight = false;

    public NumberSetting range = new NumberSetting("Radius", 0.5, 10, 0.5, 0.1);
    private final BooleanSetting holdjump = new BooleanSetting("Hold Jump", false);
    public TargetStrafe() {
        super("Target Strafe", Category.MOVEMENT, "Strafes around your target :skull:");
        addSettings(range,holdjump);
    }

    @Override
    public void onEnable() {
        if (this.aura == null) {
            this.aura = ((KillAura) Tenacity.INSTANCE.getModuleCollection().get(KillAura.class));
        }
        super.onEnable();
    }
    @Override
    public void onDisable() {
        if (this.aura == null) {
            this.aura = ((KillAura) Tenacity.INSTANCE.getModuleCollection().get(KillAura.class));
        }
        super.onDisable();
    }
    public boolean canStrafe() {
        return KillAura.target != null && this.isEnabled() || mc.gameSettings.keyBindJump.isPressed();
    }

    public void strafe(MoveEvent event, double moveSpeed) {
        if (canStrafe() && KillAura.target != null && MovementUtils.isMoving()) {
       //     if (mc.gameSettings.keyBindForward.isKeyDown()) {
                EntityLivingBase target = KillAura.target;
                float[] rotations = KillauraRotationUtil.getRotations(KillAura.target,mc.thePlayer.rotationYaw,mc.thePlayer.rotationPitch);
                if ((double) mc.thePlayer.getDistanceToEntity(target) <= this.range.getValue().floatValue()) {
                    MovementUtils.setSpeed(event, moveSpeed, rotations[0], this.direction, 0.0);
                } else {
                    MovementUtils.setSpeed(event, moveSpeed, rotations[0], this.direction, 1.0);
                }


           // }

        }
    }


    public double speed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    private void switchDirection() {
        if (this.direction == 1) {
            this.direction = -1;
        } else {
            this.direction = 1;
        }

    }

    @Override
    public void onMoveEvent(MoveEvent event) {
        Module flight = Tenacity.INSTANCE.getModuleCollection().get(Flight.class);
        Module speed = Tenacity.INSTANCE.getModuleCollection().get(Speed.class);
        if(KillAura.target != null) {
            if (speed.isEnabled()) {
                // ChatUtil.print("asd");
                strafe(event, 1);
            }

            if (flight.isEnabled()) {
                strafe(event, 1);
            }
        }




        super.onMoveEvent(event);
    }




    @Override
    public void onUpdateEvent(UpdateEvent event) {
        if (mc.gameSettings.keyBindLeft.isPressed()) {
            this.direction = 1;
        }

        if (mc.gameSettings.keyBindRight.isPressed()) {
            this.direction = -1;
        }
        if (mc.thePlayer.isCollidedHorizontally) {
            this.switchDirection();
        }
        if(KillAura.target == null) {
            if (this.aura == null) {
                this.aura = ((KillAura) Tenacity.INSTANCE.getModuleCollection().get(KillAura.class));
            }
        }


        super.onUpdateEvent(event);
    }











}
