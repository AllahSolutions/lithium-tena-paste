package dev.tenacity.anticheat.checks.combat;

import dev.tenacity.anticheat.Category;
import dev.tenacity.anticheat.Detection;
import net.minecraft.entity.player.EntityPlayer;

public class ReachA extends Detection {

    public ReachA() {
        super("Reach A", Category.COMBAT);
    }

    @Override
    public boolean runCheck(EntityPlayer player) {
        return false;
    }
}
