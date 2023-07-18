package dev.tenacity.anticheat.checks.flight;

import dev.tenacity.anticheat.Category;
import dev.tenacity.anticheat.Detection;
import dev.tenacity.anticheat.utils.MovementUtils;
import net.minecraft.entity.player.EntityPlayer;

public class FlightB extends Detection {

    public FlightB() {
        super("Flight B", Category.MOVEMENT);
    }

    @Override
    public boolean runCheck(EntityPlayer player) {
        return player.airTicks > 20 && player.motionY == 0 && MovementUtils.isMoving(player);
    }
}
