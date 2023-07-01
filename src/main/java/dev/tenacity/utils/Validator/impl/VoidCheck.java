package dev.tenacity.utils.Validator.impl;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import dev.tenacity.utils.Validator.ICheck;

public final class VoidCheck implements ICheck
{
    @Override
    public boolean validate(final Entity entity) {
        return this.isBlockUnder(entity);
    }
    
    private boolean isBlockUnder(final Entity entity) {
        for (int offset = 0; offset < entity.posY + entity.getEyeHeight(); offset += 2) {
            final AxisAlignedBB boundingBox = entity.getEntityBoundingBox().offset(Minecraft.getMinecraft().thePlayer.motionX * 4.0, (double)(-offset), Minecraft.getMinecraft().thePlayer.motionZ * 4.0);
            if (!Minecraft.getMinecraft().theWorld.getCollidingBoundingBoxes(entity, boundingBox).isEmpty()) {
                return true;
            }
        }
        return false;
    }
}