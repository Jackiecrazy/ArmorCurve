package jackiecrazy.armorcurve;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(ArmorCurve.MODID)
public class ArmorCurve {
    public static final String MODID = "armorcurve";
    public static final String VERSION = "1.2.0";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public ArmorCurve(){
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CurveConfig.CONFIG_SPEC);
    }
}
