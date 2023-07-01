package dev.tenacity.utils.Validator;

import net.minecraft.entity.Entity;

@FunctionalInterface
public interface ICheck
{
    boolean validate(final Entity p0);
}