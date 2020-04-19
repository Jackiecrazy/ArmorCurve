package com.jackiecrazi.armorcurve;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.MobEffects;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArmorEventHandler {
    private static float cache;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void spite(LivingHurtEvent e) {
        cache = e.getAmount();
    }

    //modifies damage after armor, since some items naturally ignore some armor.
    //♂ boy ♂ next ♂ door ♂
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void wail(LivingDamageEvent e) {
        EntityLivingBase uke = e.getEntityLiving();
        float amnt = cache;
        DamageSource ds = e.getSource();
        amnt = recalculateDamageIgnoreArmor(uke, ds, amnt);
        e.setAmount(amnt);
    }

    private static float applyPotionDamageCalculations(EntityLivingBase elb, DamageSource source, float damage) {
        if (source.isDamageAbsolute()) {
            return damage;
        } else {
            if (elb.isPotionActive(MobEffects.RESISTANCE) && source != DamageSource.OUT_OF_WORLD) {
                int i = (elb.getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
                int j = 25 - i;
                float f = damage * (float) j;
                damage = f / 25.0F;
            }

            if (damage <= 0.0F) {
                return 0.0F;
            } else {
                int k = EnchantmentHelper.getEnchantmentModifierDamage(elb.getArmorInventoryList(), source);

                if (k > 0) {
                    damage = CombatRules.getDamageAfterAbsorb(damage, (float) k, 0);
                }

                return damage;
            }
        }
    }

    private static float recalculateArmor(float damage, float totalArmor, float toughnessAttribute) {
        damage -= Math.min(toughnessAttribute / 2f,damage/2f);
        float e = (float) (Math.exp(-0.2 * totalArmor));
        float oneover = 1/(1 + e);
        oneover-=0.5f;
        oneover*=8.298;
        oneover+=1;
        return damage/oneover;
        //naked: 4
        //leather (7): 1.14
        //gold (11): 0.92
        //chain (12): 0.89
        //iron (15): 0.84
        //diamond (20:8): 0.4
    }

    private static float armorCalc(EntityLivingBase target, float armor, double tough, DamageSource ds, float damage) {
        if (!ds.isUnblockable())
            damage = recalculateArmor(damage, armor, (float) tough);
        if (!ds.isDamageAbsolute())
            damage = applyPotionDamageCalculations(target, ds, damage);
        return damage;
    }

    private static float recalculateDamageIgnoreArmor(EntityLivingBase target, DamageSource ds, float orig) {
        return armorCalc(target, target.getTotalArmorValue(), target.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue(), ds, orig);
    }
}
