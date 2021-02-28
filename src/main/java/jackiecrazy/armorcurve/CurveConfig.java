package jackiecrazy.armorcurve;

import com.udojava.evalex.Expression;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = ArmorCurve.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CurveConfig {
    //that is, subtract damage by (d>40/(t+1))(d-40/(t+1))/2
    public static final CurveConfig CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;
    private final ForgeConfigSpec.ConfigValue<String> _armor;
    private final ForgeConfigSpec.ConfigValue<String> _armorToughness;
    private final ForgeConfigSpec.ConfigValue<String> _enchantments;
    private final ForgeConfigSpec.ConfigValue<String> _degradation;
    public static Expression armor, toughness, enchants, degrade;

    static {
        final Pair<CurveConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CurveConfig::new);
        CONFIG = specPair.getLeft();
        CONFIG_SPEC = specPair.getRight();
    }

    public CurveConfig(ForgeConfigSpec.Builder b) {
        _armor = b.translation("armorcurve.armor").comment("configure how much armor does against damage. Valid values are 'armor', 'damage', and 'toughness'").define("damage reduction formula", "damage*10/(10+armor)");
        _armorToughness = b.translation("armorcurve.armorToughness").comment("configure sudden death protection for armor toughness. Valid values are 'armor', 'damage', and 'toughness'. Change to 1 to disable").define("armor toughness damage reduction formula", "damage-(damage>40/(toughness+1))(damage-40/(toughness+1))/2");
        _enchantments = b.translation("armorcurve.enchants").comment("configure the efficiency of protection enchantments. Valid values are 'enchant' and 'damage'").define("enchantment damage reduction formula", "damage*10/(10+enchant)");
        _degradation = b.translation("armorcurve.degrade").comment("configure how armor degrades. Valid values are 'remaining' and 'max'").define("armor degradation formula", "remaining/max");
    }

    private static void bake() throws ArithmeticException {
        armor = new Expression(CONFIG._armor.get());
        toughness = new Expression(CONFIG._armorToughness.get());
        enchants = new Expression(CONFIG._enchantments.get());
        degrade = new Expression(CONFIG._degradation.get());
        try {
            armor.with("damage", "1").and("armor", "10").and("toughness", "0").eval();
        } catch (Expression.ExpressionException e) {
            armor=new Expression("1");
            throw new ArithmeticException("invalid formula " + armor);
        }
        try {
            toughness.with("damage", "1").and("armor", "10").and("toughness", "0").eval();
        } catch (Expression.ExpressionException e) {
            toughness=new Expression("1");
            throw new ArithmeticException("invalid formula " + toughness);
        }
        try {
            enchants.with("damage", "1").and("enchant", "2").eval();
        } catch (Expression.ExpressionException e) {
            enchants=new Expression("1");
            throw new ArithmeticException("invalid formula " + enchants);
        }
        try {
            degrade.with("remaining", "100").and("max", "100").eval();
        } catch (Expression.ExpressionException e) {
            degrade=new Expression("1");
            throw new ArithmeticException("invalid formula " + degrade);
        }
    }

    @SubscribeEvent
    public static void loadConfig(ModConfig.ModConfigEvent e) {
        if (e.getConfig().getSpec() == CONFIG_SPEC) {
            try {
                bake();
            } catch (ArithmeticException validationException) {
                ArmorCurve.LOGGER.fatal("Armor Curve Configs do not look right, they have been replaced with dummy expressions. Double check your configs.");
            }
        }
    }
}
