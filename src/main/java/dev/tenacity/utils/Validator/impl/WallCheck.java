package dev.tenacity.utils.Validator.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import dev.tenacity.utils.Validator.ICheck;

public final class WallCheck implements ICheck
{
    @Override
    public boolean validate(final Entity entity) {
        return Minecraft.getMinecraft().thePlayer.canEntityBeSeen(entity);
    }
}