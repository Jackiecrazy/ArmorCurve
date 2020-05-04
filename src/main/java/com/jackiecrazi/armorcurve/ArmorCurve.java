package com.jackiecrazi.armorcurve;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ArmorCurve.MODID, version = ArmorCurve.VERSION)
public class ArmorCurve {
    public static final String MODID = "armorcurve";
    public static final String VERSION = "1.2.0";
    @Mod.Instance(ArmorCurve.MODID)
    public static ArmorCurve INST = new ArmorCurve();
    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(ArmorEventHandler.class);
    }

}
