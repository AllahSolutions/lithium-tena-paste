package dev.tenacity.anticheat.checks.move.sprint;

import dev.tenacity.anticheat.Detection;
import net.minecraft.entity.player.EntityPlayer;

public class SprintACheck extends Detection {

    public SprintACheck() {
        super("Sprint A");
    }

    @Override
    public boolean runCheck(EntityPlayer player) {
        return player.moveForward <= 0 && player.isSprinting();
    }
}
