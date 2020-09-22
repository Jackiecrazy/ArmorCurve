package com.jackiecrazi.armorcurve;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.jackiecrazi.armorcurve.GeneralConfig.*;

public class ArmorEventHandler {
    private static final EntityEquipmentSlot[] slots = {EntityEquipmentSlot.FEET, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.HEAD};
    private static float cache, cacheAbsorption;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void spite(LivingHurtEvent e) {
        cache = e.getAmount();
        cacheAbsorption = e.getEntityLiving().getAbsorptionAmount();
    }

    //modifies damage after armor, since some items naturally ignore some armor.
    //♂ boy ♂ next ♂ door ♂
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void wail(LivingDamageEvent e) {
        EntityLivingBase uke = e.getEntityLiving();
        Settings touse;
        ReductionType rt;
        if (uke instanceof EntityPlayer) {
            touse = player;
        } else {
            touse = unarmoredMob;
            for (ItemStack is : uke.getArmorInventoryList()) {
                if (!is.isEmpty()) {
                    touse = armoredMob;
                    break;
                }
            }
        }
        rt = ReductionType.values()[touse.curve];
        if (rt == ReductionType.VANILLA) return;
        float amnt = cache;
        DamageSource ds = e.getSource();
        float totalArmor = (float) uke.getTotalArmorValue() * (float) touse.armorMultiplier;
        //armor damage setup

        if (touse != unarmoredMob) {//if(touse instanceof ExtraSettings) {
            double[] armorThreshold = {touse.armor.bootsDegradeDurability, touse.armor.leggingsDegradeDurability, touse.armor.chestplateDegradeDurability, touse.armor.helmetDegradeDurability};
            double totalWeight = touse.armor.bootsDegradeWeight + touse.armor.helmetDegradeWeight + touse.armor.chestplateDegradeWeight + touse.armor.leggingsDegradeWeight;
            float damageDebuff = 1;
            if (totalWeight != 0) {
                double[] armorWeight = {touse.armor.bootsDegradeWeight / totalWeight, touse.armor.leggingsDegradeWeight / totalWeight, touse.armor.chestplateDegradeWeight / totalWeight, touse.armor.helmetDegradeWeight / totalWeight};
                //int armorCurve[] = {touse.armor.bootsDegradeDurability, touse.armor.leggingsDegradingCurve, touse.armor.chestplateDegradeDurability, touse.armor.helmetDegradeDurability};
                int x = 0;
                for (EntityEquipmentSlot ees : slots) {
                    ItemStack is = uke.getItemStackFromSlot(ees);
                    if (!is.isEmpty() && is.isItemStackDamageable() && is.getItem().getDurabilityForDisplay(is) > 1d - armorThreshold[x]) {
                        damageDebuff -= armorWeight[x] * (is.getItem().getDurabilityForDisplay(is));
                    }
                    x++;
                }
            }
            totalArmor *= damageDebuff;
        }
        //}
        //magic!
        if (!ds.isUnblockable())
            amnt = recalculateArmor(amnt, totalArmor, rt);
        if (!ds.isDamageAbsolute())
            amnt = applyPotionDamageCalculations(uke, ds, amnt, (float) touse.enchEfficiency);
        //end magic!

        //armor toughness, here to save your protected butt.
        double tough = uke.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue() * touse.suddenDeathToughness;
        if (tough > 0 && !ds.isDamageAbsolute() && !ds.isUnblockable()) {
            float threshold = uke.getMaxHealth() / (float) tough;
            if (threshold < amnt) {
                amnt = threshold + ((amnt - threshold) * (float) touse.suddenDeathDivider);
            }
        }
        //armor toughness awayyyyyy

        if (debugMode) {
            if ((uke instanceof EntityPlayer)) {
                String message = String.format("Original damage: %.3f%nArmor: %.3f%nFinal damage: %.3f%n", cache, totalArmor, amnt);
                uke.sendMessage(new TextComponentString(message));
            }
            if (ds.getTrueSource() instanceof EntityPlayer) {
                String message = String.format("Original damage: %.3f%nArmor: %.3f%nFinal damage: %.3f%n", cache, totalArmor, amnt);
                ds.getTrueSource().sendMessage(new TextComponentString(message));
            }
        }

        amnt -= cacheAbsorption;
        if (amnt < 0) {
            uke.setAbsorptionAmount(-amnt);
            e.setAmount(0);
        } else e.setAmount(amnt);
    }

    private static float recalculateArmor(float damage, float totalArmor, ReductionType type) {
        //damage -= Math.min(toughnessAttribute / 2f, damage / 2f);
        float oneover = 0;
        switch (type) {
            case LINEAR:
                oneover = totalArmor / 5;
                break;
            case SIGMOID:
                float e = (float) (Math.exp(-0.2 * totalArmor));
                oneover = 1 / (1 + e);
                oneover -= 0.5f;
                oneover *= 8.298;
                break;
        }
        oneover += 1;
        return damage / oneover;
        //oneover=totalArmor/5+1
        //naked: 4
        //leather (7): 1.14
        //gold (11): 0.92
        //chain (12): 0.89
        //iron (15): 0.84
        //diamond (20:8): 0.4
    }

    private static float applyPotionDamageCalculations(EntityLivingBase elb, DamageSource source, float damage, float debuff) {
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
                    damage = getDamageAfterMagicAbsorb(damage, (float) k, debuff);
                }

                return damage;
            }
        }
    }

    private static float getDamageAfterMagicAbsorb(float damage, float enchantModifiers, float debuff) {
        float f = enchantModifiers * debuff;
        f = MathHelper.clamp(f, 0.0F, 20.0F);
        return damage * (1.0F - f / 25.0F);
    }
}
