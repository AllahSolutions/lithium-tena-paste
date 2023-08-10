package dev.tenacity.module.impl.movement;

import dev.tenacity.event.impl.game.world.TickEvent;
import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.player.BoundingBoxEvent;
import dev.tenacity.event.impl.player.UpdateEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.MovingObjectPosition;

public final class ClickTeleport extends Module {

    private final TimerUtil timeHelper;
    private double[] xyz;
    private boolean shouldTeleport;
    private boolean teleported;
    public ClickTeleport() {
        super("Click Teleport", Category.MOVEMENT, "Tps");
        this.timeHelper = new TimerUtil();
        this.xyz = new double[3];
        this.teleported = false;
    }

    @Override
    public void onEnable() {
        this.xyz = new double[] { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };
        this.shouldTeleport = false;
        this.teleported = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.xyz = new double[3];
        this.shouldTeleport = false;
        super.onDisable();
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            e.cancel();
        }
        super.onPacketReceiveEvent(e);
    }
    @Override
    public void onBoundingBoxEvent(BoundingBoxEvent event) {
        final AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1, 5).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ());
        event.setBoundingBox(axisAlignedBB);
        super.onBoundingBoxEvent(event);
    }

    @Override
    public void onTickEvent(TickEvent event) {
        if (mc.gameSettings.keyBindAttack.isKeyDown() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.xyz[0] == Double.POSITIVE_INFINITY) {
            final BlockPosition blockPos = mc.objectMouseOver.getBlockPos();
            final Block block = mc.theWorld.getBlockState(blockPos).getBlock();
            this.xyz = new double[] {mc.objectMouseOver.getBlockPos().getX() + 0.5, mc.objectMouseOver.getBlockPos().getY() + block.getBlockBoundsMaxY(), mc.objectMouseOver.getBlockPos().getZ() + 0.5 };
            this.shouldTeleport = true;
            this.timeHelper.reset();
        }

        if (this.shouldTeleport) {
            mc.thePlayer.setPosition(this.xyz[0], this.xyz[1] + 0.3, this.xyz[2]);
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.xyz[0], this.xyz[1] + 0.3, this.xyz[2], false));
            this.shouldTeleport = false;
            this.teleported = true;
        }

        super.onTickEvent(event);
    }


}
