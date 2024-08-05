package jackiecrazy.armorcurve.mixin;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import jackiecrazy.armorcurve.CurveConfig;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Mixin(ItemStack.class)
public abstract class ArmorDegradationMixin {

    private static final Cache<ItemStack, ImmutableMultimap<String, AttributeModifier>> cache = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(1, TimeUnit.SECONDS).build();


    @Shadow
    public abstract boolean isEmpty();

    @Shadow public abstract boolean isItemStackDamageable();

    @Shadow public abstract int getMaxDamage();

    @Shadow public abstract int getItemDamage();

    @Inject(cancellable = true, at = @At("RETURN"), method = "getAttributeModifiers", locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getAttributeModifiers(EntityEquipmentSlot equipmentSlot, CallbackInfoReturnable<Multimap<String, AttributeModifier>> info, Multimap<String, AttributeModifier> m) {
        if (!this.isEmpty() && (equipmentSlot != EntityEquipmentSlot.MAINHAND && equipmentSlot != EntityEquipmentSlot.OFFHAND) && this.isItemStackDamageable()) {
            ImmutableMultimap<String, AttributeModifier> cached = cache.getIfPresent((ItemStack) (Object) this);
            if (cached != null) info.setReturnValue(cached);
            ImmutableMultimap.Builder<String, AttributeModifier> copy = ImmutableMultimap.builder();
            if (CurveConfig.degrade == null || CurveConfig.degrade.toString().equals("1")) return;
            float degrade = CurveConfig.degrade.with("remaining", new BigDecimal(this.getMaxDamage() - this.getItemDamage())).and("max", new BigDecimal(this.getMaxDamage())).eval().floatValue();

            for (String a : m.keySet()) {
                boolean handle = true;
                if (a.contains("stealth")) {
                    handle = a.equals("stealth");
                } else if (!Objects.equals(a, SharedMonsterAttributes.ARMOR.getName()) && !CurveConfig.degradeAll) handle = false;
                for (AttributeModifier eam : m.get(a)) {
                    AttributeModifier degradedEAM = new AttributeModifier(eam.getID(), eam.getName(), handle ? (degrade) * eam.getAmount() : eam.getAmount(), eam.getOperation());
                    copy.put(a, degradedEAM);
                }
            }
            cached = copy.build();
            cache.put((ItemStack) (Object) this, cached);
            info.setReturnValue(cached);
        }
    }
}
