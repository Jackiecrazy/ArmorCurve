package com.jackiecrazi.armorcurve;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class GeneralConfig {
	public static Configuration general;
	public static void init(File path){
		general=new Configuration(path, ArmorCurve.VERSION);
		general.save();
	}
}
