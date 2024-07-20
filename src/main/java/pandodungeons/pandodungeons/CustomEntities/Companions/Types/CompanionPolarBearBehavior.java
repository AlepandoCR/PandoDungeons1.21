package pandodungeons.pandodungeons.CustomEntities.Companions.Types;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.level.Level;

public class CompanionPolarBearBehavior extends PolarBear {
    public CompanionPolarBearBehavior(EntityType<? extends PolarBear> type, Level world) {
        super(type, world);
    }
}
