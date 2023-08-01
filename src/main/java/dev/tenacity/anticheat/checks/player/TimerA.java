package dev.tenacity.anticheat.checks.player;

import dev.tenacity.anticheat.Detection;
import net.minecraft.entity.player.EntityPlayer;

public class TimerA extends Detection {

    public long lastTime = System.currentTimeMillis();

    public TimerA() {
        super("Timer A");
    }

    @Override
    public boolean runCheck(EntityPlayer player) {

        long deltaTime = System.currentTimeMillis() - lastTime;

        if (deltaTime < 30) {
            return true;
        }

        lastTime = System.currentTimeMillis();

        return false;
    }
}
