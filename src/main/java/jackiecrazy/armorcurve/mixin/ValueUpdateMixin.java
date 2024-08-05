package jackiecrazy.armorcurve.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class ValueUpdateMixin {

    @Shadow public abstract IAttributeInstance getEntityAttribute(IAttribute attribute);

    @Inject(at = @At("HEAD"), method = "applyArmorCalculations")
    protected void applyArmorToDamage(DamageSource source, float p_70655_2_, CallbackInfoReturnable<Float> cir) {
        if(!source.isUnblockable())
            ((AttributeUpdater)(this.getEntityAttribute(SharedMonsterAttributes.ARMOR))).invokeFlagForUpdate();
    }
}
