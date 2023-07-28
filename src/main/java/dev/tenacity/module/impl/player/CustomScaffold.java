package dev.tenacity.module.impl.player;

import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.event.impl.player.LegitClick;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.player.RotationUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class CustomScaffold extends Module {

    public NumberSetting customYaw = new NumberSetting("Custom Yaw", 180, 180, 0, 5);
    public NumberSetting customPitch = new NumberSetting("Custom Pitch", 80, 90, -90, 0.5);

    private static class BlockInfo {

        @Getter private final BlockPosition position;
        @Getter private final EnumFacing enumFacing;

        public BlockInfo(BlockPosition position, EnumFacing enumFacing) {
            this.position = position;
            this.enumFacing = enumFacing;
        }

    }

    public float[] rotations, lastRotations;

    public CustomScaffold() {
        super("CustomScaffold", Category.PLAYER, "Automatically places blocks under you.");

        this.addSettings(
                customYaw, customPitch
        );
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
    public void onDisable() {
        super.onDisable();
    }

    public void calculateRotations() {
        this.lastRotations = this.rotations;

        this.rotations = RotationUtils.getFixedRotations(
                new float[] {
                        mc.thePlayer.rotationYaw + customYaw.getValue().floatValue(),
                        customPitch.getValue().floatValue()
                },
                this.lastRotations
        );
    }

    @Override
    public void onTickEvent(TickEvent event) {
        this.calculateRotations();
        super.onTickEvent(event);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {

        event.setRotations(
                this.rotations[0],
                this.rotations[1]
        );

        RotationUtils.setVisualRotations(event);

        super.onMotionEvent(event);
    }

    @Override
    public void onLegitClick(LegitClick event) {
        super.onLegitClick(event);
    }

}
