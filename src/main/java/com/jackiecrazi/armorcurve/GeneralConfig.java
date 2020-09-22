package com.jackiecrazi.armorcurve;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ArmorCurve.MODID)
@Config.LangKey("armorcurve.general")
public class GeneralConfig {
    public static final Settings unarmoredMob = new Settings(), armoredMob = new Settings(), player = new Settings();
    @Config.Comment("Enables extra messages when hit by or hitting entities, for the curious.")
    public static boolean debugMode=false;
    public enum ReductionType {
        VANILLA,
        LINEAR,
        SIGMOID
    }

    public static class Settings {
        @Config.Comment("Type of curve to use. 0 is vanilla, 1 is linear, 2 is sigmoid. Vanilla will disable all mod mechanics for this type of entity.")
        @Config.RangeInt(min = 0, max = 2)
        public int curve = 1;
        @Config.Comment("How much to scale down (or up!) total effective armor value when calculating")
        @Config.RangeDouble(min = 0)
        public double armorMultiplier = 1;
        @Config.Comment("Enchantment reductions are scaled by this. Set to 0 to disable enchantment effects.")
        @Config.RangeDouble(min = 0)
        public double enchEfficiency = 1;
        @Config.Comment("Efficiency of armor toughness at preventing high damage effects is scaled by this. Set to 0 to disable sudden death prevention.")
        @Config.RangeDouble(min = 0)
        public double suddenDeathToughness = 1;
        @Config.Comment("How much to reduce overflow damage by. Set to 0 to negate all overflow damage, or 1 to effectively disable sudden death prevention.")
        @Config.RangeDouble(min = 0, max = 1)
        public double suddenDeathDivider = 0.5;
        public final ArmorSettings armor = new ArmorSettings();
        public static class ArmorSettings {
            @Config.Comment("Percentage of helmet damage at which it begins to degrade. Set to 0 to disable helmet degradation.")
            @Config.RangeDouble(min = 0, max = 1)
            public double helmetDegradeDurability = 1;
            @Config.Comment("Percentage of chestplate damage at which it begins to degrade. Set to 0 to disable chestplate degradation.")
            @Config.RangeDouble(min = 0, max = 1)
            public double chestplateDegradeDurability = 1;
            @Config.Comment("Percentage of legging damage at which it begins to degrade. Set to 0 to disable legging degradation.")
            @Config.RangeDouble(min = 0, max = 1)
            public double leggingsDegradeDurability = 1;
            @Config.Comment("Percentage of boot damage at which it begins to degrade. Set to 0 to disable boot degradation.")
            @Config.RangeDouble(min = 0, max = 1)
            public double bootsDegradeDurability = 1;
            @Config.Comment("Weight of helmet durability in degradation calculations.")
            @Config.RangeInt(min = 0)
            public int helmetDegradeWeight = 3;
            @Config.Comment("Weight of chestplate durability in degradation calculations.")
            @Config.RangeInt(min = 0)
            public int chestplateDegradeWeight = 8;
            @Config.Comment("Weight of leggings durability in degradation calculations.")
            @Config.RangeInt(min = 0)
            public int leggingsDegradeWeight = 6;
            @Config.Comment("Weight of boots durability in degradation calculations.")
            @Config.RangeInt(min = 0)
            public int bootsDegradeWeight = 3;
        }
    }

    @Mod.EventBusSubscriber(modid = ArmorCurve.MODID)
    private static class EventHandler {

        /**
         * Inject the new values and save to the config file when the config has been changed from the GUI.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(ArmorCurve.MODID)) {
                ConfigManager.sync(ArmorCurve.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
