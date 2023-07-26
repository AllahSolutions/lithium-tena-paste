package dev.tenacity.module.impl.movement;

import com.sun.javafx.geom.Vec2d;
import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.event.impl.player.*;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.Skid.BlockUtil;
import dev.tenacity.utils.Skid.FuckingNIgger;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.player.ScaffoldUtils;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.ToDoubleFunction;

public class NewScaffold extends Module {
    private final TimerUtil startTimeHelper;
    private final TimerUtil startTimeHelper2;
    private final TimerUtil adTimeHelper;
    private final FuckingNIgger rotationUtil;
    private final double[] lastXYZ;
    private final HashMap<float[], MovingObjectPosition> hashMap;
    public ModeSetting mode;
    public NumberSetting yawSpeed;
    public NumberSetting pitchSpeed;
    public BooleanSetting moveFix;
    public BooleanSetting esp;
    public BooleanSetting adStrafe;
    public ModeSetting silentMode;
    ArrayList<double[]> hitpoints;
    private float[] lastRots;
    private float[] rots;
    private int slotID;
    private ItemStack block;
    private int lastSlotID;
    private EnumFacing enumFacing;
    private BlockPos blockPos;
    private boolean start;
    private double[] xyz;
    private static float yaw;
    private MovingObjectPosition objectPosition;

    public NewScaffold() {
        super("ScaffoldWalk", Category.MOVEMENT, "Automatically bridges for you.");
        this.startTimeHelper = new TimerUtil();
        this.startTimeHelper2 = new TimerUtil();
        this.adTimeHelper = new TimerUtil();
        this.lastXYZ = new double[3];
        this.hashMap = new HashMap<float[], MovingObjectPosition>();
        this.mode = new ModeSetting("Mode", "Legit", "Legit","Basic");
        this.yawSpeed = new NumberSetting("YawSpeed", 40.0, 180.0, 0.0, 10);
        this.pitchSpeed = new NumberSetting("PitchSpeed", 40.0, 180.0, 0.0, 10);
        this.moveFix = new BooleanSetting("MoveFix",  true);
        this.esp = new BooleanSetting("ESP",  true);
        this.adStrafe = new BooleanSetting("AdStrafe", true);
        this.silentMode = new ModeSetting("SilentMode", "Spoof", "Spoof", "Switch", "Spoof", "None");
        this.hitpoints = new ArrayList<double[]>();
        this.lastRots = new float[] { 0.0f, 0.0f };
        this.rots = new float[] { 0.0f, 0.0f };
        this.start = true;
        this.xyz = new double[3];
        this.objectPosition = null;
        this.rotationUtil = new FuckingNIgger();

        this.addSettings(
                mode, yawSpeed, pitchSpeed,
                moveFix, esp, adStrafe, silentMode
        );
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.thePlayer != null && mc.theWorld != null) {
            this.restRotation();
            this.slotID = mc.thePlayer.inventory.currentItem;
            this.lastSlotID = mc.thePlayer.inventory.currentItem;
            this.start = true;
            this.startTimeHelper.reset();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.thePlayer.inventory.currentItem != this.slotID) {}
        this.slotID = mc.thePlayer.inventory.currentItem;
    }

    @Override
    public void onTickEvent(final TickEvent eventEarlyTick) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }
        this.blockPos = this.getAimBlockPos();
        this.start = ((mc.thePlayer.motionX == 0.0 && mc.thePlayer.motionZ == 0.0 && mc.thePlayer.onGround) || !this.startTimeHelper.hasTimeElapsed(200L));
        if (this.start) {
            this.startTimeHelper2.reset();
        }
        if (this.blockPos != null) {
            float[] floats = { 1.0f, 1.0f };
            final String selected = this.mode.getMode();
            switch (selected) {
                case "Legit": {
                    floats = this.getNearestRotation();
                    break;
                }
                case "Basic": {
                    floats = this.basicRotation();
                    break;
                }
            }
            this.lastRots = this.rots;
            if (floats != null) {
                this.rots = floats;
            }

            yaw = this.rots[0];
        }
        super.onTickEvent(eventEarlyTick);
    }

    @Override
    public void onRaycastEvent(final RaycastEvent eventRayCast) {
        if (this.objectPosition != null) {
            mc.objectMouseOver = this.objectPosition;
        }
    }

    private float[] basicRotation() {
        final double x = mc.thePlayer.posX;
        final double z = mc.thePlayer.posZ;
        final double add1 = 1.05;
        final double add2 = 0.05;
        this.xyz = new double[] { mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ };
        final double maX = this.blockPos.getX() + add1;
        final double miX = this.blockPos.getX() - add2;
        final double maZ = this.blockPos.getZ() + add1;
        final double miZ = this.blockPos.getZ() - add2;
        if (x > maX || x < miX || z > maZ || z < miZ) {
            final double[] ex = this.getAdvancedDiagonalExpandXZ(this.blockPos);

            // Rotation
            float[] f = this.rotationUtil.scaffoldRots(
                    this.blockPos.getX() + ex[0],
                    this.blockPos.getY() + 0.85,
                    this.blockPos.getZ() + ex[1],
                    this.lastRots[0],
                    this.lastRots[1],
                    this.yawSpeed.getValue().floatValue(),
                    this.pitchSpeed.getValue().floatValue(),
                    false
            );

            return new float[] { mc.thePlayer.rotationYaw - 180.0f, f[1] };
        }
        return new float[] { mc.thePlayer.rotationYaw - 180.0f, this.rots[1] };
    }

    private float[] getNearestRotation() {
        this.objectPosition = null;
        final float[] floats = this.rots;
        final BlockPos b = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ);
        this.hashMap.clear();
        if (this.start) {

            // Yaw ???
            float yaw = this.rotationUtil.rotateToYaw(
                    this.yawSpeed.getValue().floatValue(),
                    this.rots[0], mc.thePlayer.rotationYaw - 180.0f
            );

            FuckingNIgger.mouseSens(yaw, 80.34f, this.rots[0], this.rots[1]);

            floats[1] = 80.34f;
            floats[0] = yaw;
        }
        else {
            final float yaww = mc.thePlayer.rotationYaw - 180.0f;
            floats[0] = yaww;
            double x = mc.thePlayer.posX;
            double z = mc.thePlayer.posZ;
            final double add1 = 1.288;
            final double add2 = 0.288;
            if (!this.buildForward()) {
                x += mc.thePlayer.posX - this.xyz[0];
                z += mc.thePlayer.posZ - this.xyz[2];
            }
            this.xyz = new double[] { mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ };
            final double maX = this.blockPos.getX() + add1;
            final double miX = this.blockPos.getX() - add2;
            final double maZ = this.blockPos.getZ() + add1;
            final double miZ = this.blockPos.getZ() - add2;
            if (x > maX || x < miX || z > maZ || z < miZ) {
                final ArrayList<MovingObjectPosition> movingObjectPositions = new ArrayList<MovingObjectPosition>();
                final ArrayList<Float> pitchs = new ArrayList<Float>();
                for (float i = Math.max(this.rots[1] - 20.0f, -90.0f); i < Math.min(this.rots[1] + 20.0f, 90.0f); i += 0.05f) {
                    final float[] f = FuckingNIgger.mouseSens(yaww, i, this.rots[0], this.rots[1]);
                    final MovingObjectPosition m2 = mc.thePlayer.customRayTrace(4.5, 1.0f, yaww, f[1]);
                    if (m2.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.isOkBlock(m2.getBlockPos()) && !movingObjectPositions.contains(m2) && m2.getBlockPos().equalsBlockPos(this.blockPos) && m2.sideHit != EnumFacing.DOWN && m2.sideHit != EnumFacing.UP && m2.getBlockPos().getY() <= b.getY()) {
                        movingObjectPositions.add(m2);
                        this.hashMap.put(f, m2);
                        pitchs.add(f[1]);
                    }
                }
                movingObjectPositions.sort(Comparator.comparingDouble(m -> mc.thePlayer.getDistanceSq(m.getBlockPos().add(0.5, 0.5, 0.5))));
                MovingObjectPosition mm = null;
                if (movingObjectPositions.size() > 0) {
                    mm = movingObjectPositions.get(0);
                }
                if (mm != null) {
                    floats[0] = yaww;
                    pitchs.sort(Comparator.comparingDouble((ToDoubleFunction<? super Float>)this::distanceToLastPitch));
                    if (!pitchs.isEmpty()) {
                        floats[1] = pitchs.get(0);
                        this.objectPosition = this.hashMap.get(floats);
                    }
                    return floats;
                }
            }
            else {
                floats[1] = this.rots[1];
            }
        }
        return floats;
    }

    private double distanceToLastPitch(final float pitch) {
        return Math.abs(pitch - this.rots[1]);
    }

    private double[] getAdvancedDiagonalExpandXZ(final BlockPos blockPos) {
        final double[] xz = new double[2];
        final Vec2d difference = new Vec2d(blockPos.getX() - mc.thePlayer.posX, blockPos.getZ() - mc.thePlayer.posZ);
        if (difference.x > -1.0 && difference.x < 0.0 && difference.y < -1.0) {
            this.enumFacing = EnumFacing.SOUTH;
            xz[0] = difference.x * -1.0;
            xz[1] = 1.0;
        }
        if (difference.y < 0.0 && difference.y > -1.0 && difference.x < -1.0) {
            this.enumFacing = EnumFacing.EAST;
            xz[0] = 1.0;
            xz[1] = difference.y * -1.0;
        }
        if (difference.x > -1.0 && difference.x < 0.0 && difference.y > 0.0) {
            this.enumFacing = EnumFacing.NORTH;
            xz[0] = difference.x * -1.0;
            xz[1] = 0.0;
        }
        if (difference.y < 0.0 && difference.y > -1.0 && difference.x > 0.0) {
            this.enumFacing = EnumFacing.WEST;
            xz[0] = 0.0;
            xz[1] = difference.y * -1.0;
            this.enumFacing = EnumFacing.WEST;
        }
        if (difference.x >= 0.0 && difference.y < -1.0) {
            xz[1] = 1.0;
        }
        if (difference.y >= 0.0 & difference.x < -1.0) {
            xz[0] = 1.0;
        }
        if (difference.x < 0.0 || difference.y > 0.0) {}
        if (difference.y <= -1.0 && difference.x < -1.0) {
            xz[1] = (xz[0] = 1.0);
        }
        return xz;
    }

    private EnumFacing getPlaceSide() {
        final BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ);
        if (playerPos.equalsBlockPos(this.blockPos)) {
            System.out.println("Error");
        }
        if (playerPos.add(0, 1, 0).equalsBlockPos(this.blockPos)) {
            return EnumFacing.UP;
        }
        if (playerPos.add(0, -1, 0).equalsBlockPos(this.blockPos)) {
            return EnumFacing.DOWN;
        }
        if (playerPos.add(1, 0, 0).equalsBlockPos(this.blockPos)) {
            return EnumFacing.WEST;
        }
        if (playerPos.add(-1, 0, 0).equalsBlockPos(this.blockPos)) {
            return EnumFacing.EAST;
        }
        if (playerPos.add(0, 0, 1).equalsBlockPos(this.blockPos)) {
            return EnumFacing.NORTH;
        }
        if (playerPos.add(0, 0, -1).equalsBlockPos(this.blockPos)) {
            return EnumFacing.SOUTH;
        }
        if (playerPos.add(1, 0, 1).equalsBlockPos(this.blockPos)) {
            return EnumFacing.WEST;
        }
        if (playerPos.add(-1, 0, 1).equalsBlockPos(this.blockPos)) {
            return EnumFacing.EAST;
        }
        if (playerPos.add(-1, 0, 1).equalsBlockPos(this.blockPos)) {
            return EnumFacing.NORTH;
        }
        if (playerPos.add(-1, 0, -1).equalsBlockPos(this.blockPos)) {
            return EnumFacing.SOUTH;
        }
        return null;
    }

    @Override
    public void onBlockPlaceable(final BlockPlaceableEvent eventClick) {
        if (this.block == null) {
            this.block = mc.thePlayer.inventory.getCurrentItem();
        }
        if (this.blockPos == null || mc.currentScreen != null) {
            return;
        }
        final ItemStack lastItem = mc.thePlayer.inventory.getCurrentItem();
        ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();
        if (!this.silentMode.is("None")) {
            for (int i = 36; i < mc.thePlayer.inventoryContainer.inventorySlots.size(); ++i) {
                final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (itemStack != null && itemStack.getItem() instanceof ItemBlock && itemStack.stackSize > 0 && BlockUtil.getInstance().isValidStack(itemStack)) {
                    this.block = itemStack;
                    this.slotID = i - 36;
                    break;
                }
            }
            if (!this.silentMode.is("Spoof") || this.lastSlotID != this.slotID) {}
            itemstack = mc.thePlayer.inventoryContainer.getSlot(this.slotID + 36).getStack();
        }
        else {
            this.slotID = mc.thePlayer.inventory.currentItem;
            this.lastSlotID = mc.thePlayer.inventory.currentItem;
        }
        final String selected = this.mode.getMode();
        switch (selected) {
            case "Basic": {
                final double x = mc.thePlayer.posX;
                final double z = mc.thePlayer.posZ;
                final double add1 = 1.05;
                final double add2 = 0.05;
                this.xyz = new double[] { mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ };
                final double maX = this.blockPos.getX() + add1;
                final double miX = this.blockPos.getX() - add2;
                final double maZ = this.blockPos.getZ() + add1;
                final double miZ = this.blockPos.getZ() - add2;
                if (x > maX || x < miX || z > maZ || z < miZ) {
                    if (this.silentMode.is("Switch")) {
                        mc.thePlayer.inventory.setCurrentItem(this.block.getItem(), 0, false, false);
                    }
                    final EnumFacing e = this.getPlaceSide();
                    if (e != null) {
                        final double[] ex = this.getAdvancedDiagonalExpandXZ(this.blockPos);
                        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemstack, this.blockPos, e, new Vec3(this.blockPos.getX() + ex[0], this.blockPos.getY() - 0.5234234, this.blockPos.getZ() + ex[1]))) {
                            mc.thePlayer.swingItem();
                        }
                    }
                    if (itemstack != null && itemstack.stackSize == 0) {
                        mc.thePlayer.inventory.mainInventory[this.slotID] = null;
                    }
                    break;
                }
                break;
            }
            default: {
                final MovingObjectPosition blockOver = mc.objectMouseOver;
                if (blockOver == null) {
                    break;
                }
                final BlockPos blockpos = blockOver.getBlockPos();
                if (blockOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() == Material.air) {
                    break;
                }
                if (itemstack != null && !(itemstack.getItem() instanceof ItemBlock)) {
                    return;
                }
                this.hitpoints.add(new double[] { blockOver.hitVec.xCoord, blockOver.hitVec.yCoord, blockOver.hitVec.zCoord });
                if (mc.thePlayer.posY < blockpos.getY() + 1.5) {
                    if (blockOver.sideHit != EnumFacing.UP && blockOver.sideHit != EnumFacing.DOWN) {
                        if (this.silentMode.is("Switch")) {
                            mc.thePlayer.inventory.setCurrentItem(this.block.getItem(), 0, false, false);
                        }
                        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemstack, blockpos, blockOver.sideHit, blockOver.hitVec)) {
                            mc.thePlayer.swingItem();
                        }
                        if (itemstack != null && itemstack.stackSize == 0) {
                            mc.thePlayer.inventory.mainInventory[this.slotID] = null;
                        }
                        mc.sendClickBlockToController(mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown() && mc.inGameHasFocus);
                        break;
                    }
                    break;
                }
                else {
                    if (blockOver.sideHit != EnumFacing.DOWN && blockOver.getBlockPos().equalsBlockPos(this.blockPos) && mc.gameSettings.keyBindJump.isKeyDown()) {
                        if (this.silentMode.is("Switch")) {
                            mc.thePlayer.inventory.setCurrentItem(this.block.getItem(), 0, false, false);
                        }
                        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemstack, blockpos, blockOver.sideHit, blockOver.hitVec)) {
                            mc.thePlayer.swingItem();
                        }
                        if (itemstack != null && itemstack.stackSize == 0) {
                            mc.thePlayer.inventory.mainInventory[this.slotID] = null;
                        }
                        mc.sendClickBlockToController(mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown() && mc.inGameHasFocus);
                        break;
                    }
                    break;
                }
            }
        }
        if (lastItem != null && this.silentMode.is("Switch")) {
            mc.thePlayer.inventory.setCurrentItem(lastItem.getItem(), 0, false, false);
        }
        this.lastSlotID = this.slotID;
        super.onBlockPlaceable(eventClick);
    }

    @Override
    public void onMoveInputEvent(MoveInputEvent event) {
        if (this.moveFix.isEnabled()) {
            MovementUtils.fixMovement(event, yaw);
        }

        super.onMoveInputEvent(event);
    }

    @Override
    public void onStrafeEvent(final StrafeEvent eventMove) {
        if (this.moveFix.isEnabled()) {
            eventMove.setYaw(yaw);
        }
    }

    @Override
    public void onJumpFixEvent(JumpFixEvent eventJump) {
        if (this.moveFix.isEnabled()) {
            eventJump.setYaw(yaw);
        }
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        
        
        
        super.onMotionEvent(event);
    }

    private void setRotation(MotionEvent event) {
        if (mc.currentScreen != null) {
            return;
        }

        event.setYaw(this.rots[0]);
        event.setPitch(this.rots[1]);
    }

    private boolean buildForward() {
        final float realYaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
        return (realYaw > 77.5 && realYaw < 102.5) || (realYaw > 167.5 || realYaw < -167.0f) || (realYaw < -77.5 && realYaw > -102.5) || (realYaw > -12.5 && realYaw < 12.5);
    }

    private BlockPos getAimBlockPos() {
        final BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ);
        if ((mc.gameSettings.keyBindJump.isKeyDown() || !mc.thePlayer.onGround) && mc.thePlayer.moveForward == 0.0f && mc.thePlayer.moveStrafing == 0.0f && this.isOkBlock(playerPos.add(0, -1, 0))) {
            return playerPos.add(0, -1, 0);
        }
        BlockPos blockPos = null;
        final ArrayList<BlockPos> bp = this.getBlockPos();
        final ArrayList<BlockPos> blockPositions = new ArrayList<BlockPos>();
        if (bp.size() > 0) {
            for (int i = 0; i < Math.min(bp.size(), 18); ++i) {
                blockPositions.add(bp.get(i));
            }
            blockPositions.sort(Comparator.comparingDouble((ToDoubleFunction<? super BlockPos>)this::getDistanceToBlockPos));
            if (blockPositions.size() > 0) {
                blockPos = blockPositions.get(0);
            }
        }
        return blockPos;
    }

    private ArrayList<BlockPos> getBlockPos() {
        final BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ);
        final ArrayList<BlockPos> blockPoses = new ArrayList<BlockPos>();
        for (int x = playerPos.getX() - 2; x <= playerPos.getX() + 2; ++x) {
            for (int y = playerPos.getY() - 1; y <= playerPos.getY(); ++y) {
                for (int z = playerPos.getZ() - 2; z <= playerPos.getZ() + 2; ++z) {
                    if (this.isOkBlock(new BlockPos(x, y, z))) {
                        blockPoses.add(new BlockPos(x, y, z));
                    }
                }
            }
        }
        if (!blockPoses.isEmpty()) {
            blockPoses.sort(Comparator.comparingDouble(blockPos -> mc.thePlayer.getDistance(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5)));
        }
        return blockPoses;
    }

    private double getDistanceToBlockPos(final BlockPos blockPos) {
        double distance = 1337.0;
        for (float x = (float)blockPos.getX(); x <= blockPos.getX() + 1; x += (float)0.2) {
            for (float y = (float)blockPos.getY(); y <= blockPos.getY() + 1; y += (float)0.2) {
                for (float z = (float)blockPos.getZ(); z <= blockPos.getZ() + 1; z += (float)0.2) {
                    final double d0 = mc.thePlayer.getDistance(x, y, z);
                    if (d0 < distance) {
                        distance = d0;
                    }
                }
            }
        }
        return distance;
    }

    private boolean isOkBlock(final BlockPos blockPos) {
        final Block block = mc.theWorld.getBlockState(blockPos).getBlock();
        return !(block instanceof BlockLiquid) && !(block instanceof BlockAir) && !(block instanceof BlockChest) && !(block instanceof BlockFurnace);
    }

    private void restRotation() {
        this.rots[0] = mc.thePlayer.rotationYaw;
        this.rots[1] = mc.thePlayer.rotationPitch;
        this.lastRots[0] = mc.thePlayer.rotationYaw;
        this.lastRots[1] = mc.thePlayer.rotationPitch;
    }

}
