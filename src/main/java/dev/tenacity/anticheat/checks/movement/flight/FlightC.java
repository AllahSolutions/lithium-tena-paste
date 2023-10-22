package dev.tenacity.anticheat.checks.movement.flight;

import dev.tenacity.anticheat.Detection;
import net.minecraft.entity.player.EntityPlayer;

public class FlightC extends Detection {

    public FlightC() {
        super("Flight C");
    }

    @Override
    public boolean runCheck(EntityPlayer player) {
        return player.motionY > 0.42;
    }
}
