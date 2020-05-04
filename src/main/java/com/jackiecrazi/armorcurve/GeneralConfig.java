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

    public enum ReductionType {
        VANILLA,
        LINEAR,
        SIGMOID
    }

    public static class Settings {
        @Config.Comment("Type of curve to use. 0 is vanilla, 1 is linear, 2 is sigmoid.")
        @Config.RangeInt(min = 0, max = 2)
        public int curve = 1;
        @Config.Comment("how much to scale down (or up!) effective armor value when calculating")
        @Config.RangeDouble(min = 0)
        public double armorMultiplier = 1;
        @Config.Comment("Enchantment reductions are scaled by this. Set to 0 to disable enchantment effects.")
        public double enchEfficiency = 1;
        @Config.Comment("Whether to enable toughness's protection effect")
        public boolean suddenDeathPrevention=true;
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
