package dev.tenacity.module.impl.combat;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.event.impl.player.*;
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
import dev.tenacity.utils.misc.Random;
import dev.tenacity.utils.player.Advancedrots;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.player.RotationUtils;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.server.PacketUtils;
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
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class KillAura extends Module {

    public ModeSetting attackMode = new ModeSetting("Attack Mode", "Single", "Single", "Switch", "Multi"),
            blockMode = new ModeSetting("Blocking Mode", "Vanilla", "None", "Fake", "Vanilla", "PostAttack", "BlocksMC"),
            rotationMode = new ModeSetting("Rotation Mode", "Normal", "None", "Normal","Advanced", "Smooth"),
            sortingMode = new ModeSetting("Sorting Mode", "Health", "Health", "Range", "HurtTime"),
            attackTiming = new ModeSetting("Attack Timing", "Pre", "Pre", "Post", "All"),
            blockTiming = new ModeSetting("Block Timing", "Pre", "Pre", "Post", "All");

    public BooleanSetting blockInteract = new BooleanSetting("Block Interact", false);

    public NumberSetting maxTargets = new NumberSetting("Max Targets", 2, 10, 2, 1);
    public NumberSetting minAPS = new NumberSetting("Min APS", 9, 20, 1, 0.1),
            maxAPS = new NumberSetting("Max APS", 12, 20, 1, 0.1);

    public NumberSetting swingRange = new NumberSetting("Swing Range", 3, 6, 3, 0.1),
            attackRange = new NumberSetting("Attack Range", 3, 6, 3, 0.1),
            wallsRange = new NumberSetting("Walls Range", 0.5, 6, 0.5, 0.1),
            blockRange = new NumberSetting("Block Range", 3, 6, 3, 0.1),
            rotationRange = new NumberSetting("Rotation Range", 3, 6, 3, 0.1);

    public NumberSetting blockChance = new NumberSetting("Block Chance", 100, 100, 0, 1);
    public NumberSetting switchDelay = new NumberSetting("Switch Delay", 350, 5000, 50, 50);

    public BooleanSetting silentRotations = new BooleanSetting("Silent Rotations", true),
            showRotations = new BooleanSetting("Show Rotations", true);

    public NumberSetting rotationSmoothness = new NumberSetting("Rotation Smoothness", 10, 180, 10, 10);

    public MultipleBoolSetting targets = new MultipleBoolSetting(
            "Targets",
            new BooleanSetting("Players", true),
            new BooleanSetting("Animals", false),
            new BooleanSetting("Monsters", false),
            new BooleanSetting("Others", false),
            new BooleanSetting("Invisible", false)

    );

    public MultipleBoolSetting bypass = new MultipleBoolSetting(
            "Bypass",
            new BooleanSetting("Keep Sprinting", true),
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
        this.maxTargets.addParent(attackMode, a -> !attackMode.is("Single"));

        this.blockChance.addParent(blockMode, a -> !blockMode.is("None") && !blockMode.is("Fake"));
        this.switchDelay.addParent(rotationMode, a -> rotationMode.is("Switch"));

        this.silentRotations.addParent(rotationMode, a -> !rotationMode.is("None"));
        this.showRotations.addParent(rotationMode, a -> !rotationMode.is("None"));

        this.rotationSmoothness.addParent(rotationMode, a -> rotationMode.is("Smooth"));

        this.addSettings(
                attackMode, blockMode, rotationMode, sortingMode, attackTiming, blockTiming,
                blockInteract, maxTargets, blockChance, switchDelay,

                minAPS, maxAPS,
                swingRange, attackRange, wallsRange, blockRange, rotationRange,

                silentRotations, showRotations,
                rotationSmoothness,

                targets, bypass, features, renders
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
                target == null
        ) {
            return;
        }

        attacking = !list.isEmpty() && !Tenacity.INSTANCE.isEnabled(Scaffold.class);

        if (attacking) {
            if (attackTimer.hasTimeElapsed(1000 / currentAPS)) {
                runAttackLoop(event);

                currentAPS = Random.nextDouble(
                        minAPS.getValue(),
                        maxAPS.getValue()
                );

                attackTimer.reset();
            }
        }

        super.onMotionEvent(event);
    }

    @Override
    public void onMoveInputEvent(MoveInputEvent event) {
        if (bypass.getSetting("Movement Correction").isEnabled() && target != null) {
            MovementUtils.fixMovement(event, yaw);
        }
    }

    @Override
    public void onStrafeEvent(StrafeEvent event) {
        if (bypass.getSetting("Movement Correction").isEnabled() && target != null) {
            event.setYaw(yaw);
        }
    }

    @Override
    public void onJumpFixEvent(JumpFixEvent event) {
        if (bypass.getSetting("Movement Correction").isEnabled() && target != null) {
            event.setYaw(yaw);
        }
    }

    private void calculateRotations(EntityLivingBase target) {
        lastYaw = yaw;
        lastPitch = pitch;

        float[] rotations = new float[] {0, 0};

        switch (rotationMode.getMode()) {
            case "Normal":
                rotations = RotationUtils.getRotationsNeeded(target);
                break;
            case "Advanced":
                rotations = Advancedrots.basicRotation(target, lastYaw, lastPitch,false);
                break;
            case "Smooth":
                rotations = RotationUtils.getSmoothRotations(target, rotationSmoothness.getValue().floatValue());
                break;
        }

        float[] fixedRotations = RotationUtils.getFixedRotations(rotations, new float[] { lastYaw, lastPitch });

        yaw = fixedRotations[0];
        pitch = fixedRotations[1];
    }

    private void runRotations(MotionEvent event) {
        if (!rotationMode.is("None")) {
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
    }

    private void runPreBlocking(MotionEvent event) {
        boolean shouldInteract = blockInteract.isEnabled();

        if (
                (event.isPre() && blockTiming.is("Post")) ||
                (event.isPost() && blockTiming.is("Pre"))
        ) {
            return;
        }

        int chance = (int) Math.round(100 * Math.random());

        if (chance <= blockChance.getValue()) {
            switch (blockMode.getMode()) {
                case "Vanilla":
                    block(shouldInteract);
                    break;
                case "BlocksMC":
                    if (mc.thePlayer.ticksExisted % 3 == 0)
                        block(shouldInteract);
                    else
                        unblock();
                    break;
                default:
                    break;
            }
        }
    }

    private void runAttackLoop(MotionEvent event) {

        // Attack
        switch (attackMode.getMode()) {
            case "Single":
                target = (list.size() > 0) ? list.get(0) : null;

                if (target != null)
                    attack(event, target);

                break;
            case "Switch":

                if (list.size() >= targetIndex)
                    targetIndex = 0;

                target = (list.size() > 0) ? list.get(targetIndex) : null;

                if (target != null)
                    attack(event, target);

                if (switchTimer.hasTimeElapsed(switchDelay.getValue())) {
                    targetIndex++;

                    switchTimer.reset();
                }

                break;
            case "Multi":
                target = (list.size() > 0) ? list.get(0) : null;
                list.forEach(entity -> this.attack(event, entity));
                break;
        }
    }

    private void runPostBlocking(MotionEvent event) {
        boolean shouldInteract = blockInteract.isEnabled();

        if (
                (event.isPre() && blockTiming.is("Post")) ||
                (event.isPost() && blockTiming.is("Pre"))
        ) {
            return;
        }

        int chance = (int) Math.round(100 * Math.random());

        if (chance <= blockChance.getValue()) {
            switch (blockMode.getMode()) {
                case "PostAttack":
                    block(shouldInteract);
                    break;
                default:
                    break;
            }
        }
    }

    private void attack(MotionEvent event, EntityLivingBase entity) {

        if (mc.thePlayer.getDistanceToEntity(entity) <= swingRange.getValue()) {
            mc.thePlayer.swingItem();
        }

        if (mc.thePlayer.getDistanceToEntity(entity) <= blockRange.getValue()) {
            runPreBlocking(event);
        }

        if (!mc.thePlayer.canEntityBeSeen(entity) && mc.thePlayer.getDistanceToEntity(entity) > wallsRange.getValue())
            return;

        if (bypass.getSetting("Ray Tracing").isEnabled() && !RotationUtils.isMouseOver(yaw, pitch, target, attackRange.getValue().floatValue()))
            return;

        if (mc.thePlayer.getDistanceToEntity(entity) <= attackRange.getValue()) {

            Tenacity.INSTANCE.getEventProtocol().handleEvent(new AttackEvent(target));

            if (bypass.getSetting("Movement Correction").isEnabled()) {
                mc.thePlayer.setSprinting(false);
            }

            if (bypass.getSetting("Keep Sprinting").isEnabled()) {
                PacketUtils.sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
            } else {
                mc.playerController.attackEntity(mc.thePlayer, entity);
            }

            mc.thePlayer.onCriticalHit(entity);
        }

        if (mc.thePlayer.getDistanceToEntity(entity) <= blockRange.getValue()) {
            runPostBlocking(event);
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
                            return entity.hurtTime;
                        default:
                            return -1;
                    }
                }))

                .collect(Collectors.toList());

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
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
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
                RenderUtil.renderBoundingBox(renderTarget, color, auraESPAnim.getOutput().floatValue());
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