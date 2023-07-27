package dev.tenacity.anticheat;

import dev.tenacity.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;

@Getter
@Setter
public abstract class Detection implements Utils {

    private String name;
    private long lastViolated;

    protected double violations = 0.0D;

    public Detection(String name) {
        this.name = name;
    }

    public abstract boolean runCheck(EntityPlayer player);
}
