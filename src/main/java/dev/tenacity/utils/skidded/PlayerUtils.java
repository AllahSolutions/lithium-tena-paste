package dev.tenacity.utils.skidded;

import dev.tenacity.utils.Utils;

public class PlayerUtils implements Utils {

    public static boolean isInLiquid() {
        return mc.thePlayer != null && (mc.thePlayer.isInWater() || mc.thePlayer.isInLava());
    }

}
