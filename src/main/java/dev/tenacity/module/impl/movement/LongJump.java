package dev.tenacity.module.impl.movement;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.BoundingBoxEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.event.impl.player.MoveEvent;
import dev.tenacity.event.impl.player.UpdateEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.ModuleCollection;
import dev.tenacity.module.impl.player.Blink;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.server.PacketUtils;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;

public final class LongJump extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Intave", "Intave");

    private boolean intaveStarted;

    public LongJump() {
        super("LongJump", Category.MOVEMENT, "Allows you to jump further.");
    }

    @Override
    public void onEnable() {

        intaveStarted = false;

        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {

        switch (mode.getMode()) {
            case "Intave":
                if (!intaveStarted) {
                    mc.thePlayer.motionX = 0;
                    mc.thePlayer.motionZ = 0;
                } else {
                    if (mc.thePlayer.motionY > 0) {
                        mc.thePlayer.motionY = MovementUtils.predictedMotion(0);
                    }
                }

                intaveStarted = true;
                break;
        }

        super.onUpdateEvent(event);
    }

    @Override
    public void onBoundingBoxEvent(BoundingBoxEvent event) {

        switch (mode.getMode()) {
            case "Intave":
                if (intaveStarted) {
                    if (!mc.gameSettings.keyBindSneak.isKeyDown()) {
                        double x = event.getBlockPos().getX(),
                        y = event.getBlockPos().getY(),
                        z = event.getBlockPos().getZ();

                        if (y < mc.thePlayer.posY) {
                            event.setBoundingBox(AxisAlignedBB.fromBounds(-64, -6, -64, 64, 6, 64).offset(x, y, z));
                        }
                    }
                }
                break;
            default:
                break;
        }

        super.onBoundingBoxEvent(event);
    }
}
