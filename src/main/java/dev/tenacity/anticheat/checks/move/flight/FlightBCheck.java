package dev.tenacity.anticheat.checks.move.flight;

import dev.tenacity.anticheat.Detection;
import dev.tenacity.anticheat.utils.MovementUtils;
import net.minecraft.entity.player.EntityPlayer;

public class FlightBCheck extends Detection {

    public FlightBCheck() {
        super("Flight B");
    }

    @Override
    public boolean runCheck(EntityPlayer player) {
        return player.airTicks > 20 && player.motionY == 0 && MovementUtils.isMoving(player);
    }
}
