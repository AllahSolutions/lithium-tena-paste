package dev.tenacity.module.impl.combat;

import de.florianmichael.viamcp.fixes.AttackOrder;
import dev.tenacity.Tenacity;
import dev.tenacity.commands.impl.FriendCommand;
import dev.tenacity.event.impl.player.AttackEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.MultipleBoolSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.player.RotationUtils;
import dev.tenacity.utils.player.rotations.KillauraRotationUtil;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class PlayBot extends Module {


    private final NumberSetting minCPS = new NumberSetting("Min CPS", 10, 20, 1, 1);
    private final NumberSetting maxCPS = new NumberSetting("Max CPS", 10, 20, 1, 1);
    private final NumberSetting reach = new NumberSetting("Reach", 4, 6, 3, 0.1);

    private final NumberSetting strafeDelay = new NumberSetting("Strafe Delay", 1000, 0, 5000, 10);
    private final NumberSetting timeStrafing = new NumberSetting("Time Strafing", 1000, 0, 5000, 10);

    private EntityLivingBase target;

    private final TimerUtil attackTimer = new TimerUtil();
    private final TimerUtil strafeTimer = new TimerUtil();
    private double currentCPS = 10;


    public float[] rotations, lastRotations;

    public PlayBot() {
        super("PlayBot", Category.COMBAT, "Automatically finds the nearest player and attempts to kill them");
        this.addSettings(minCPS, maxCPS, reach, strafeDelay, timeStrafing);
    }


    @Override
    public void onEnable() {

        this.rotations = new float[] {
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch
        };

        this.lastRotations = new float[] {
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch
        };

        super.onEnable();
    }

    @Override
    public void onMotionEvent(MotionEvent event) {

        this.target = mc.theWorld.getLoadedEntityList().stream()
                .filter(entity -> entity instanceof EntityPlayer && entity != mc.thePlayer).map(entity -> (EntityPlayer) entity)
                .min(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity))).orElse(null);

        if (this.target == null) {
            return;
        }

        if (minCPS.getValue() > maxCPS.getValue()) {
            minCPS.setValue(minCPS.getValue() - 1);
        }

        mc.gameSettings.keyBindForward.pressed = mc.thePlayer.getDistanceToEntity(target) > reach.getValue();
        mc.gameSettings.keyBindJump.pressed = mc.thePlayer.isCollidedHorizontally || mc.thePlayer.isInWater();

        double delay = strafeDelay.getValue();
        double strafing = timeStrafing.getValue();

        if (strafeTimer.hasTimeElapsed(delay)) {
            boolean direction = new Random().nextBoolean();

            if (!strafeTimer.hasTimeElapsed(delay + strafing)) {
                if (direction) {
                    mc.gameSettings.keyBindLeft.pressed = false;
                    mc.gameSettings.keyBindRight.pressed = true;
                } else {
                    mc.gameSettings.keyBindLeft.pressed = true;
                    mc.gameSettings.keyBindRight.pressed = false;
                }
            } else {
                strafeTimer.reset();
            }
        }

        lastRotations = rotations;

        final float[] rotations = KillauraRotationUtil.getRotations(
                target,
                event.getYaw(),
                event.getPitch()
        );

        float[] fixedRotations = RotationUtils.getFixedRotations(
                rotations,
                lastRotations
        );

        mc.thePlayer.rotationYaw = fixedRotations[0];
        mc.thePlayer.rotationPitch = fixedRotations[1];

        if (attackTimer.hasTimeElapsed(1000 / currentCPS)) {
            AttackEvent attackEvent = new AttackEvent(target);
            Tenacity.INSTANCE.getEventProtocol().handleEvent(attackEvent);

            if (!attackEvent.isCancelled() && mc.thePlayer.getDistanceToEntity(target) <= reach.getValue()) {
                AttackOrder.sendFixedAttack(mc.thePlayer, target);
            }

            currentCPS = MathUtils.getRandomInRange(minCPS.getValue(), maxCPS.getValue());
            attackTimer.reset();
        }
    }

    @Override
    public void onDisable() {

        this.rotations = new float[] { 0.0F, 0.0F };
        this.lastRotations = new float[]  {0.0F, 0.0F };

        mc.gameSettings.keyBindForward.pressed = false;
        target = null;

        super.onDisable();
    }
}