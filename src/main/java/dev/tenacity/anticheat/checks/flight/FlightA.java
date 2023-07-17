package dev.tenacity.anticheat.checks.flight;

import dev.tenacity.anticheat.Category;
import dev.tenacity.anticheat.Detection;
import dev.tenacity.anticheat.utils.MovementUtils;
import net.minecraft.entity.player.EntityPlayer;

public class FlightA extends Detection {

    public FlightA() {
        super("Flight A", Category.MOVEMENT);
    }

    @Override
    public boolean runCheck(EntityPlayer player) {
        return !player.onGround && player.motionY == 0 && MovementUtils.isMoving(player);
    }
}
