package greenhouse.strategy.irrigation;

import greenhouse.strategy.model.IrrigationPlan;
import greenhouse.strategy.model.ZoneConditions;

/** Waters only when the soil is too dry, in proportion to the moisture deficit. */
public class MoistureThresholdIrrigation implements IrrigationStrategy {

    private static final double START_BELOW_PERCENT = 35.0;
    private static final double TARGET_PERCENT = 60.0;
    private static final double LITERS_PER_PERCENT = 0.5;

    @Override
    public IrrigationPlan plan(ZoneConditions conditions) {
        double moisture = conditions.soilMoisturePercent();
        if (moisture >= START_BELOW_PERCENT) {
            return IrrigationPlan.none("soil moisture is sufficient");
        }
        double liters = (TARGET_PERCENT - moisture) * LITERS_PER_PERCENT;
        return new IrrigationPlan(liters, "soil below threshold, refilling to target");
    }

    @Override
    public String name() {
        return "Moisture threshold";
    }
}
