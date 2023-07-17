package dev.tenacity.anticheat.checks.phase;

import dev.tenacity.anticheat.Category;
import dev.tenacity.anticheat.Detection;
import net.minecraft.block.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;

import java.util.Arrays;
import java.util.List;

public class PhaseA extends Detection {

    private static final List<Block> PHASE_BLOCKS = Arrays.asList(

            // Air
            Blocks.air,

            // Torches
            Blocks.torch,
            Blocks.redstone_torch,
            Blocks.unlit_redstone_torch,

            // Carpets
            Blocks.carpet,
            Blocks.snow_layer


    );

    public int blocksPhased;

    public PhaseA() {
        super("Phase A", Category.MOVEMENT);
    }

    @Override
    public boolean runCheck(EntityPlayer player) {

        Block block = mc.theWorld.getBlockState(
                player.getPosition()
        ).getBlock();

        boolean flag = !PHASE_BLOCKS.contains(block);

        if (
                block.getUnlocalizedName().contains("FENCE") ||
                block.getUnlocalizedName().contains("DOOR")
        ) {
            flag = false;
        }

        if (
                block instanceof BlockGrass ||
                block instanceof BlockBush ||
                block instanceof BlockFire ||
                block instanceof BlockRedstoneWire
        ) {
            flag = false;
        }

        if (
                block instanceof BlockDynamicLiquid ||
                block instanceof BlockStaticLiquid
        ) {
            flag = false;
        }

        if (flag && (violations += 1.0D + (++blocksPhased / 10.0D)) > 1.0) {
            flag = true;
        }

        return flag;
    }
}
