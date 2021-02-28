package jackiecrazy.armorcurve.mixin;

import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ModifiableAttributeInstance.class)
public interface AttributeUpdater {

    @Invoker("compute")
    public void invokeUpdateAttribute();
}
