package dev.tenacity.module.impl.combat;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.game.world.TickEvent;
import dev.tenacity.event.impl.player.input.AttackEvent;
import dev.tenacity.event.impl.player.input.LegitClickEvent;
import dev.tenacity.event.impl.player.input.MoveInputEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.player.movement.SlowdownEvent;
import dev.tenacity.event.impl.player.movement.correction.JumpEvent;
import dev.tenacity.event.impl.player.movement.correction.StrafeEvent;
import dev.tenacity.event.impl.render.Render3DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.movement.Scaffold;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.MultipleBoolSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.misc.Random;
import dev.tenacity.utils.player.rotations.KillauraRotationUtil;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.player.RotationUtils;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.EnumFacing;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class KillAura extends Module {

    public static ModeSetting attackMode = new ModeSetting("Attack Mode", "Single", "Single", "Switch", "Multi"),
            blockMode = new ModeSetting("Blocking Mode", "Vanilla", "None", "Vanilla", "Watchdog", "PostAttack", "BlocksMC"),
            rotationMode = new ModeSetting("Rotation Mode", "Normal", "None", "Normal"),
            sortingMode = new ModeSetting("Sorting Mode", "Health", "Health", "Range", "HurtTime", "Armor"),
            attackTiming = new ModeSetting("Attack Timing", "Pre", "Pre", "Post", "Legit", "All"),
            randomMode = new ModeSetting("Random Mode", "None", "None", "Normal", "Doubled", "Gaussian");

    public BooleanSetting blockInteract = new BooleanSetting("Block Interact", false);
    public static BooleanSetting fakeAutoblock = new BooleanSetting("Fake AutoBlock", false);
                
    public NumberSetting maxTargets = new NumberSetting("Max Targets", 2, 10, 2, 1);
    public NumberSetting minAPS = new NumberSetting("Min APS", 9, 20, 1, 0.1),
            maxAPS = new NumberSetting("Max APS", 12, 20, 1, 0.1);

    public NumberSetting minTurnSpeed = new NumberSetting("Min Turn Speed", 120, 180, 10, 10);
    public NumberSetting maxTurnSpeed = new NumberSetting("Max Turn Speed", 160, 180, 10, 10);
    public NumberSetting randomization = new NumberSetting("Randomization", 0, 3, 0.1, 0.1);

    public NumberSetting swingRange = new NumberSetting("Swing Range", 3, 10, 3, 0.1),
            attackRange = new NumberSetting("Attack Range", 3, 10, 3, 0.1),
            wallsRange = new NumberSetting("Walls Range", 0.5, 10, 0.5, 0.1),
            blockRange = new NumberSetting("Block Range", 3, 10, 3, 0.1),
            rotationRange = new NumberSetting("Rotation Range", 3, 10, 3, 0.1);

    public NumberSetting blockChance = new NumberSetting("Block Chance", 100, 100, 0, 1);
    public NumberSetting switchDelay = new NumberSetting("Switch Delay", 350, 5000, 50, 50);

    public BooleanSetting silentRotations = new BooleanSetting("Silent Rotations", true),
            showRotations = new BooleanSetting("Show Rotations", true);

    public MultipleBoolSetting targets = new MultipleBoolSetting(
            "Targets",
            new BooleanSetting("Players", true),
            new BooleanSetting("Animals", false),
            new BooleanSetting("Monsters", false),
            new BooleanSetting("Invisible", false)

    );

    public MultipleBoolSetting bypass = new MultipleBoolSetting(
            "Bypass",
            new BooleanSetting("Movement Correction", true),
            new BooleanSetting("Through Walls", true),
            new BooleanSetting("Ray Tracing", false)

    );

    public MultipleBoolSetting features = new MultipleBoolSetting(
            "Features",
            new BooleanSetting("Auto Disable", true),
            new BooleanSetting("Ignore UIs", true)
    );

    public MultipleBoolSetting renders = new MultipleBoolSetting(
            "Renders",
            new BooleanSetting("Circle", true),
            new BooleanSetting("Box", false),
            new BooleanSetting("Tracer", false)
    );

    public BooleanSetting reverseSorting = new BooleanSetting("Reverse Sorting", false);

    public static EntityLivingBase target, renderTarget;
    public List<EntityLivingBase> list;

    private int targetIndex = 0;

    private float yaw, pitch, lastYaw, lastPitch;

    public static boolean fake, blocking, attacking;

    private double currentAPS = 2;

    private final TimerUtil attackTimer = new TimerUtil();
    private final TimerUtil switchTimer = new TimerUtil();


    public KillAura() {
        super("KillAura", Category.COMBAT, "Automatically hits entities for you.");

        this.blockInteract.addParent(blockMode, a -> !blockMode.is("None") && !blockMode.is("Fake"));
        this.maxTargets.addParent(attackMode, a -> attackMode.is("Single"));

        this.blockChance.addParent(blockMode, a -> !blockMode.is("None") && !blockMode.is("Fake"));
        this.switchDelay.addParent(rotationMode, a -> rotationMode.is("Switch"));

        this.silentRotations.addParent(rotationMode, a -> !rotationMode.is("None"));
        this.showRotations.addParent(rotationMode, a -> !rotationMode.is("None"));

        this.addSettings(
                attackMode, blockMode, rotationMode, sortingMode, attackTiming, randomMode,
                blockInteract, maxTargets, blockChance, switchDelay,

                minAPS, maxAPS,
                swingRange, attackRange, wallsRange, blockRange, rotationRange,

                silentRotations, showRotations,
                minTurnSpeed, maxTurnSpeed,
                randomization,

                targets, bypass, features, fakeAutoblock, renders,
                reverseSorting
        );

        this.list = new ArrayList<>();
    }

    @Override
    public void onEnable() {

        attackTimer.reset();

        yaw = mc.thePlayer.rotationYaw;
        pitch = mc.thePlayer.rotationPitch;

        lastYaw = mc.thePlayer.rotationYaw;
        lastPitch = mc.thePlayer.rotationPitch;

        fake = blockMode.is("Fake");

        super.onEnable();
    }

    @Override
    public void onDisable() {

        target = null;
        renderTarget = null;

        list.clear();

        unblock();

        blocking = false;
        attacking = false;

        super.onDisable();
    }

    @Override
    public void onTickEvent(TickEvent event) {

        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        updateTargets();

        target = (this.list.size() > 0) ? this.list.get(0) : null;

        if (target == null)
            return;

        calculateRotations(target);

        super.onTickEvent(event);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        this.setSuffix(attackMode.getMode());

        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        attacking = target != null && !Tenacity.INSTANCE.isEnabled(Scaffold.class);

        if (mc.thePlayer.ticksExisted == 0 && features.getSetting("Auto Disable").isEnabled()) {
            this.toggle();
            return;
        }

        if (mc.currentScreen != null && features.getSetting("Ignore UIs").isEnabled()) {
            return;
        }

        target = (list.size() > 0) ? list.get(0) : null;

        if (target != null)
            runRotations(event);

        if (
                (event.isPost() && attackTiming.is("Pre")) ||
                (event.isPre() && attackTiming.is("Post")) ||
                attackTiming.is("Legit") ||
                target == null
        ) {
            return;
        }

        runAttackLoop();

        super.onMotionEvent(event);
    }

    @Override
    public void onLegitClickEvent(LegitClickEvent event) {

        if (target == null) {
            return;
        }

        if (
                attackTiming.is("Pre") ||
                attackTiming.is("Post")
        ) {
            return;
        }

        runAttackLoop();

        super.onLegitClickEvent(event);
    }

    @Override
    public void onMoveInputEvent(MoveInputEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        if (bypass.getSetting("Movement Correction").isEnabled() && target != null) {
            MovementUtils.fixMovement(event, yaw);
        }
    }

    @Override
    public void onStrafeEvent(StrafeEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        if (bypass.getSetting("Movement Correction").isEnabled() && target != null) {
            event.setYaw(yaw);
        }
    }

    @Override
    public void onJumpFixEvent(JumpEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        if (bypass.getSetting("Movement Correction").isEnabled() && target != null) {
            event.setYaw(yaw);
        }
    }

    private void calculateRotations(EntityLivingBase target) {
        lastYaw = yaw;
        lastPitch = pitch;

        float[] rotations = new float[] {0, 0};

        switch (rotationMode.getMode()) {
            case "None": {
                rotations = new float[] { mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch };
                break;
            }
            case "Normal": {
                rotations = KillauraRotationUtil.getRotations(target, lastYaw, lastPitch);
                break;
            }
            default: {
                break;
            }
        }

        yaw = rotations[0];
        pitch = rotations[1];

        switch (randomMode.getMode()) {
            case "Normal": {
                yaw += Math.random() * randomization.getValue();
                pitch += Math.random() * randomization.getValue();
                break;
            }
            case "Doubled": {
                yaw += Math.random() * randomization.getValue();
                pitch += Math.random() * randomization.getValue();

                if (mc.thePlayer.ticksExisted % 3 == 0) {
                    yaw += Math.random() * randomization.getValue();
                    pitch += Math.random() * randomization.getValue();
                }

                break;
            }
            case "Gaussian": {
                yaw += ThreadLocalRandom.current().nextGaussian() * randomization.getValue();
                pitch += ThreadLocalRandom.current().nextGaussian() * randomization.getValue();
                break;
            }
            default: {
                break;
            }
        }

        float speed = MathUtils.getRandomInRange(
                minTurnSpeed.getValue().floatValue(),
                maxTurnSpeed.getValue().floatValue()
        );

        yaw = KillauraRotationUtil.smoothRotation(lastYaw, yaw, speed);
        pitch = KillauraRotationUtil.smoothRotation(lastPitch, pitch, speed);

        float[] fixedRotations = RotationUtils.getFixedRotations(
                new float[] { yaw, pitch },
                new float[] { lastYaw, lastPitch }
        );

        yaw = fixedRotations[0];
        pitch = fixedRotations[1];
    }

    private void runRotations(MotionEvent event) {
        if (silentRotations.isEnabled()) {
            event.setYaw(yaw);
            event.setPitch(pitch);
        } else {
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
        }

        if (showRotations.isEnabled())
            RotationUtils.setVisualRotations(yaw, pitch);
    }

    @Override
    public void onSlowDownEvent(SlowdownEvent event) {
        if (blockMode.getMode().equals("Watchdog")) {
            if (mc.thePlayer.hurtTime >= 2) {
                event.cancel();
            }
        }
    }

    private void runPreBlocking() {
        boolean shouldInteract = blockInteract.isEnabled();

        int chance = (int) Math.round(100 * Math.random());

        if (target != null && !blockMode.is("None")) {
            unblock();
        }

        if (chance <= blockChance.getValue()) {
            switch (blockMode.getMode()) {
                case "Vanilla": {
                    block(shouldInteract);
                    break;
                }
                case "Watchdog": {
                //    if (mc.thePlayer.hurtTime >= 2) {
                  //      block(true);
                 //   } else {
                 //       unblock();
                //    }
                    break;
                }
                case "BlocksMC": {
                    if (mc.thePlayer.ticksExisted % 3 == 0)
                        block(shouldInteract);
                    else
                        unblock();
                    break;
                }
                default:
                    break;
            }
        }
    }

    private void runAttackLoop() {
        if (attacking) {
            if (attackTimer.hasTimeElapsed(1000 / currentAPS)) {

                // Attack
                switch (attackMode.getMode()) {
                    case "Single":
                        target = (list.size() > 0) ? list.get(0) : null;

                        if (target != null)
                            attack(target);

                        break;
                    case "Switch":
                        if (list.size() >= targetIndex)
                            targetIndex = 0;

                        target = (list.size() > 0) ? list.get(targetIndex) : null;

                        if (target != null)
                            attack(target);

                        if (switchTimer.hasTimeElapsed(switchDelay.getValue())) {
                            targetIndex++;

                            switchTimer.reset();
                        }

                        break;
                    case "Multi":
                        target = (list.size() > 0) ? list.get(0) : null;
                        list.forEach(this::attack);
                        break;
                }

                currentAPS = Random.nextDouble(
                        minAPS.getValue(),
                        maxAPS.getValue()
                );

                attackTimer.reset();
            }
        }
    }

    private void runPostBlocking() {
        boolean shouldInteract = blockInteract.isEnabled();

        int chance = (int) Math.round(100 * Math.random());

        if (target != null && !blockMode.is("None")) {
            unblock();
        }

        if (chance <= blockChance.getValue()) {
            if (blockMode.getMode().equals("PostAttack")) {
                block(shouldInteract);
            }
            if (blockMode.getMode().equals("Watchdog")) {
                if (mc.thePlayer.hurtTime >= 2) {
                   // ChatUtil.print("Gay");
                    block(true);
                } else {
                    unblock();
                }
            }
        }
    }

    private void attack(EntityLivingBase entity) {
        if (mc.thePlayer.getDistanceToEntity(entity) <= swingRange.getValue() && ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            mc.thePlayer.swingItem();
        }

        if (mc.thePlayer.getDistanceToEntity(entity) <= blockRange.getValue()) {
            runPreBlocking();
        }

        if (!mc.thePlayer.canEntityBeSeen(entity) && mc.thePlayer.getDistanceToEntity(entity) > wallsRange.getValue())
            return;

        if (bypass.getSetting("Ray Tracing").isEnabled() && !RotationUtils.isMouseOver(yaw, pitch, target, attackRange.getValue().floatValue()))
            return;

        if (mc.thePlayer.getDistanceToEntity(entity) <= attackRange.getValue()) {
            Tenacity.INSTANCE.getEventProtocol().handleEvent(new AttackEvent(target));

            mc.playerController.attackEntity(mc.thePlayer, entity);
            mc.thePlayer.onCriticalHit(entity);
        }

        if (mc.thePlayer.getDistanceToEntity(entity) <= swingRange.getValue() && ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {
            mc.thePlayer.swingItem();
        }

        if (mc.thePlayer.getDistanceToEntity(entity) <= blockRange.getValue()) {
            runPostBlocking();
        }
    }

    private void updateTargets() {
        Teams teams = Tenacity.INSTANCE.getModuleCollection().getModule(Teams.class);
        AntiBot antiBot = Tenacity.INSTANCE.getModuleCollection().getModule(AntiBot.class);

        this.list = mc.theWorld.loadedEntityList

                .stream()

                .filter(entity -> entity instanceof EntityLivingBase)

                .map(entity -> (EntityLivingBase) entity)

                .filter(livingEntity -> {

                    if (!this.targets.getSetting("Players").isEnabled() && livingEntity instanceof EntityPlayer) {
                        return false;
                    }

                    if (
                            !this.targets.getSetting("Animals").isEnabled() &&
                                    (
                                            livingEntity instanceof EntityAnimal ||
                                            livingEntity instanceof EntitySquid ||
                                            livingEntity instanceof EntityVillager
                                    )
                    ) {
                        return false;
                    }

                    if (
                            !this.targets.getSetting("Monsters").isEnabled() &&
                                    (
                                            livingEntity instanceof EntityMob ||
                                            livingEntity instanceof EntitySlime
                                    )
                    ) {
                        return false;
                    }

                    if (!this.targets.getSetting("Invisible").isEnabled() && livingEntity.isInvisible()) {
                        return false;
                    }

                    if (teams.isEnabled() && teams.isTeammate(livingEntity)) {
                        return false;
                    }

                    if (antiBot.isEnabled() && antiBot.isBot(livingEntity)) {
                        return false;
                    }

                    if (livingEntity instanceof EntityArmorStand || livingEntity.deathTime != 0 || livingEntity.isDead) {
                        return false;
                    }

                    return livingEntity != mc.thePlayer;
                })

                .filter(entity -> mc.thePlayer.getDistanceToEntity(entity) <= rotationRange.getValue())

                .sorted(Comparator.comparingDouble(entity -> {
                    switch (sortingMode.getMode()) {
                        case "Health":
                            return entity.getHealth();
                        case "Range":
                            return mc.thePlayer.getDistanceToEntity(entity);
                        case "HurtTime":
                            return entity.getHurtTime();
                        case "Armor":
                            return entity.getTotalArmorValue();
                        default:
                            return -1;
                    }
                }))

                .collect(Collectors.toList());

        if (this.reverseSorting.isEnabled()) {
            Collections.reverse(list);
        }

    }

    private void block(boolean interact) {
        if (!canBlock()) {
            return;
        }

        if (!blocking) {
            if (interact && target != null && mc.objectMouseOver.entityHit == target) {
                mc.playerController.interactWithEntitySendPacket(mc.thePlayer, target);
            }

            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
            blocking = true;
        }
    }

    private void unblock() {
        if (blocking) {
            if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPosition.ORIGIN, EnumFacing.DOWN));
            } else {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
            }
            blocking = false;
        }
    }

    private boolean canBlock() {
        return mc.thePlayer.inventory.getCurrentItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword;
    }

    private final Animation auraESPAnim = new DecelerateAnimation(300, 1);

    @Override
    public void onRender3DEvent(Render3DEvent event) {
        auraESPAnim.setDirection(target != null ? Direction.FORWARDS : Direction.BACKWARDS);

        if (target != null) {
            renderTarget = target;
        } else {
            renderTarget = null;
        }

        if (auraESPAnim.finished(Direction.BACKWARDS)) {
            renderTarget = null;
        }

        Color color = HUDMod.getClientColors().getFirst();

        if (renderTarget != null) {
            if (renders.getSetting("Box").isEnabled()) {
                RenderUtil.renderBoundingBox(renderTarget, color, 1);
            }
            if (renders.getSetting("Circle").isEnabled()) {
                RenderUtil.drawCircle(renderTarget, event.getTicks(), .75f, color.getRGB(), auraESPAnim.getOutput().floatValue());
            }

            if (renders.getSetting("Tracer").isEnabled()) {
                RenderUtil.drawTracerLine(renderTarget, 4f, Color.BLACK, auraESPAnim.getOutput().floatValue());
                RenderUtil.drawTracerLine(renderTarget, 2.5f, color, auraESPAnim.getOutput().floatValue());
            }
        }
    }
}