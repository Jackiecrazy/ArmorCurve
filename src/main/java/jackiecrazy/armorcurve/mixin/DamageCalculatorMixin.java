package jackiecrazy.armorcurve.mixin;

import jackiecrazy.armorcurve.CurveConfig;
import net.minecraft.util.CombatRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.math.BigDecimal;

@Mixin(CombatRules.class)
public class DamageCalculatorMixin {
    @Inject(cancellable = true, at = @At("HEAD"), method = "getDamageAfterAbsorb(FFF)F")
    private static void getDamageLeft(float damage, float armor, float armorToughness, CallbackInfoReturnable<Float> info) {
        BigDecimal ret = CurveConfig.armor.with("damage", new BigDecimal(damage)).and("armor", new BigDecimal(armor)).and("toughness", new BigDecimal(armorToughness)).eval();
        ret = CurveConfig.toughness.with("damage", ret).and("armor", new BigDecimal(armor)).and("toughness", new BigDecimal(armorToughness)).eval();
//        float reduction = 1 + armor / 5;
//        float afterDamage = damage / reduction;
//
//        if (armorToughness > 0) {
//            float cap = 40 / armorToughness;
//            if (afterDamage > cap)
//                afterDamage -= (afterDamage - cap) / 2;
//        }
        info.setReturnValue(ret.floatValue());
    }

    @Inject(cancellable = true, at = @At("HEAD"), method = "getDamageAfterMagicAbsorb(FF)F")
    private static void getInflictedDamage(float damage, float prot, CallbackInfoReturnable<Float> info) {
        BigDecimal ret = CurveConfig.enchants.with("damage", new BigDecimal(damage)).and("enchant", new BigDecimal(prot)).eval();

        //float reduction = 1 + prot / 5;
        info.setReturnValue(ret.floatValue());
    }
}
