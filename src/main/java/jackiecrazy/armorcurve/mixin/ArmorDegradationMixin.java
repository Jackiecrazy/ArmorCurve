package jackiecrazy.armorcurve.mixin;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import jackiecrazy.armorcurve.CurveConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Mixin(ItemStack.class)
public abstract class ArmorDegradationMixin {

    private static final Cache<ItemStack, ImmutableMultimap<Attribute, AttributeModifier>> cache = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(1, TimeUnit.SECONDS).build();

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract boolean isEmpty();

    @Shadow
    public abstract int getDamageValue();

    @Shadow
    public abstract int getMaxDamage();

    @Shadow public abstract boolean isDamageableItem();

    @Inject(cancellable = true, at = @At("RETURN"), method = "getAttributeModifiers", locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getAttributeModifiers(EquipmentSlot equipmentSlot, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> info, Multimap<Attribute, AttributeModifier> m) {
        if (!this.isEmpty() && (equipmentSlot != EquipmentSlot.MAINHAND && equipmentSlot != EquipmentSlot.OFFHAND) && this.isDamageableItem()) {
            ImmutableMultimap<Attribute, AttributeModifier> cached = cache.getIfPresent((ItemStack) (Object) this);
            if (cached != null) info.setReturnValue(cached);
            ImmutableMultimap.Builder<Attribute, AttributeModifier> copy = ImmutableMultimap.builder();
            if (CurveConfig.degrade == null || CurveConfig.degrade.toString().equals("1")) return;
            float degrade = CurveConfig.degrade.with("remaining", new BigDecimal(this.getMaxDamage() - this.getDamageValue())).and("max", new BigDecimal(this.getMaxDamage())).eval().floatValue();
            for (Attribute e : m.keySet()) {
                boolean handle = true;
                if (e.getRegistryName() != null && e.getRegistryName().getNamespace().equals("wardance")) {
                    handle = e.getDescriptionId().equals("wardance.absorption") || e.getDescriptionId().equals("wardance.deflection") || e.getDescriptionId().equals("wardance.shatter") || e.getDescriptionId().equals("stealth");
                } else if (e != Attributes.ARMOR && !CurveConfig.degradeAll) handle = false;
                for (AttributeModifier eam : m.get(e)) {
                    AttributeModifier degradedEAM = new AttributeModifier(eam.getId(), eam.getName(), handle ? (degrade) * eam.getAmount() : eam.getAmount(), eam.getOperation());
                    copy.put(e, degradedEAM);
                }
            }
            cached = copy.build();
            cache.put((ItemStack) (Object) this, cached);
            info.setReturnValue(cached);
        }
    }
}
