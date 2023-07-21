package dev.tenacity.module.impl.combat;

import dev.tenacity.event.impl.player.AttackEvent;
import dev.tenacity.event.impl.player.UpdateEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.NumberSetting;
import lombok.Getter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.*;

public class BackTrack extends Module {

    // The Position Limit.
    private NumberSetting amount = new NumberSetting("Positions", 0, 50, 0, 1.0);

    public BackTrack() {
        super("BackTrack", Category.COMBAT, "Allows you to hit entities from their previous positions.");

        this.addSettings(amount);
    }

    private int ticks;

    public EntityLivingBase target;
    public List<Position> positions = new ArrayList<>();

    public static class Position {
        @Getter public double x, y, z;

        public Position(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    @Override
    public void onEnable() {
        positions.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        positions.clear();
        super.onDisable();
    }

    @Override
    public void onAttackEvent(AttackEvent event) {
        if (event.getTargetEntity() instanceof EntityPlayer) {
            this.target = event.getTargetEntity();
        }

        ticks = 0;
        super.onAttackEvent(event);
    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {

        if (target == null) {
            return;
        }

        positions.add(new Position(target.posX, target.posY, target.posZ));

        double deltaX = (target.posX - target.lastTickPosX) * 3;
        double deltaZ = (target.posZ - target.lastTickPosZ) * 3;

        while (positions.size() > amount.getValue()) {
            positions.remove(0);
        }

        ++ticks;

        super.onUpdateEvent(event);
    }
}