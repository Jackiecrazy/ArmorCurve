package jackiecrazy.armorcurve.mixin;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AttributeInstance.class)
public interface AttributeUpdater {

    @Invoker
    public void invokeSetDirty();
}
