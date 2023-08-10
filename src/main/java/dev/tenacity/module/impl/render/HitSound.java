package dev.tenacity.module.impl.render;

import dev.tenacity.event.impl.player.input.AttackEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.utils.misc.SoundUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;


public class HitSound extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Skeet", "UwU","Skeet");

    public HitSound() {
        super("Hit Sound", Category.RENDER, "Plays a sound when u hit somebody");
        addSettings(mode);
    }

    private final ResourceLocation skeet = new ResourceLocation("Tenacity/Sounds/hit1.wav");

    private final ResourceLocation UwU = new ResourceLocation("Tenacity/Sounds/UwU.wav");

    private Entity lastAttackedEntity;


    @Override
    public void onAttackEvent(AttackEvent e) {
        lastAttackedEntity = e.getTargetEntity();
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        this.setSuffix(mode.getMode());
        if (event.isPre() && lastAttackedEntity != null && lastAttackedEntity.hurtResistantTime == 19) {
            if (mc.thePlayer.getDistanceToEntity(lastAttackedEntity) < 10) {
                if(mode.is("Skeet")) {
                    SoundUtils.playSound(skeet, 1);
                }
                if(mode.is("UwU")) {
                    SoundUtils.playSound(UwU, 1);
                }

            }
            lastAttackedEntity = null;
        }
    }



}
