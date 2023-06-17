package dev.tenacity.module.impl.movement.longs;

import dev.tenacity.event.ListenerAdapter;
import dev.tenacity.module.impl.movement.longs.impl.LatestNCPLongJump;
import dev.tenacity.utils.Utils;

import java.util.HashMap;

public class LongJumpMode extends ListenerAdapter implements Utils {

    public String name;

    public static final HashMap<Class<? extends LongJumpMode>, LongJumpMode> jumps = new HashMap<>();

    public LongJumpMode(String name) {
        this.name = name;
    }

    public static <T extends LongJumpMode> T get(Class <? extends LongJumpMode> input) {
        return (T) jumps.get(input);
    }

    public static LongJumpMode get(String name) {
        return jumps.values().stream().filter(jump -> jump.getName().equals(name)).findFirst().orElse(null);
    }

    public static void init() {
        jumps.put(LatestNCPLongJump.class, new LatestNCPLongJump());
    }

    public String getName() {
        return name;
    }

    public void onEnable() { /* */ };
    public void onDisable() { /* */ };
}
