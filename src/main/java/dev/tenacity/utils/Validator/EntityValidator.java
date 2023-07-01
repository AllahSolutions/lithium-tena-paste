package dev.tenacity.utils.Validator;

import java.util.Iterator;
import net.minecraft.entity.Entity;
import java.util.HashSet;
import java.util.Set;

public final class EntityValidator
{
    private final Set<ICheck> checks;
    
    public EntityValidator() {
        this.checks = new HashSet<ICheck>();
    }
    
    public final boolean validate(final Entity entity) {
        for (final ICheck check : this.checks) {
            if (check.validate(entity)) {
                continue;
            }
            return false;
        }
        return true;
    }
    
    public void add(final ICheck check) {
        this.checks.add(check);
    }
}