package jackiecrazy.armorcurve;

import com.udojava.evalex.apothavoidance.Expression;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ArmorCurve.MODID, name = "armor_curve")
@Config.LangKey("taoism.config.combat.title")
public class CurveConfig {
    //that is, subtract damage by (d>40/(t+1))(d-40/(t+1))/2
    @Config.Comment("configure how much armor does against damage. Valid values are 'armor', 'damage', and 'toughness'. Set to \"damage\" to not modify damage at this step.")
    public static String firstFormula="damage";
    @Config.Comment("configure sudden death protection for armor toughness. Valid values are 'armor', 'damage', and 'toughness'. Set to \"damage\" to not modify damage at this step.")
    public static String secondFormula="damage*MAX(15/(15+armor+MIN(damage,toughness)),0.1)";
    @Config.Comment("configure the efficiency of protection enchantments. Valid values are 'enchant' and 'damage'. Set to \"damage\" to not modify damage at this step.")
    public static String enchantmentFormula="damage*10/(10+enchant)";
    @Config.Comment("configure how armor degrades. Valid values are 'remaining' and 'max'. Set to 1 to disable.")
    public static String degradationFormula="sqrt(remaining/MAX(max,1))";
    @Config.Ignore
    public static Expression first;
    @Config.Ignore
    public static Expression second;
    @Config.Ignore
    public static Expression enchants;
    @Config.Ignore
    public static Expression degrade;
    public static boolean degradeAll=false;

    private static void bake() throws ArithmeticException {
        try {
            first = new Expression(firstFormula);
            first.with("damage", "1").and("armor", "10").and("toughness", "0").eval();
        } catch (Expression.ExpressionException e) {
            first =new Expression("1");
            throw new ArithmeticException("invalid formula " + first);
        }
        try {
            second = new Expression(secondFormula);
            second.with("damage", "1").and("armor", "10").and("toughness", "0").eval();
        } catch (Expression.ExpressionException e) {
            second =new Expression("1");
            throw new ArithmeticException("invalid formula " + second);
        }
        try {
            enchants = new Expression(enchantmentFormula);
            enchants.with("damage", "1").and("enchant", "2").eval();
        } catch (Expression.ExpressionException e) {
            enchants=new Expression("1");
            throw new ArithmeticException("invalid formula " + enchants);
        }
        try {
            degrade = new Expression(degradationFormula);
            degrade.with("remaining", "100").and("max", "100").eval();
        } catch (Expression.ExpressionException e) {
            degrade=new Expression("1");
            throw new ArithmeticException("invalid formula " + degrade);
        }
    }

    @Mod.EventBusSubscriber(modid = ArmorCurve.MODID)
    private static class EventHandler {

        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(ArmorCurve.MODID)) {
                ConfigManager.sync(ArmorCurve.MODID, Config.Type.INSTANCE);
                bake();
            }
        }
    }
}
