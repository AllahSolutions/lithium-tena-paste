package dev.tenacity.anticheat.checks.move.sprint;

import dev.tenacity.anticheat.Detection;
import net.minecraft.entity.player.EntityPlayer;

public class SprintBCheck extends Detection {

    public SprintBCheck() {
        super("Sprint B");
    }

    @Override
    public boolean runCheck(EntityPlayer player) {
        return player.isBlocking() && player.isSprinting();
    }
}
