package jackiecrazy.armorcurve.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ValueUpdateMixin {

    @Shadow public abstract ModifiableAttributeInstance getAttribute(Attribute attribute);

    @Inject(at = @At("HEAD"), method = "applyArmorCalculations")
    protected void applyArmorToDamage(DamageSource source, float damage, CallbackInfoReturnable<Float> cir) {
        ((AttributeUpdater)(this.getAttribute(Attributes.ARMOR))).invokeUpdateAttribute();
    }
}
