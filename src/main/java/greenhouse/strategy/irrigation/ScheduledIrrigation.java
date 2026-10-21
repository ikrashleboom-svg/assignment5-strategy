package greenhouse.strategy.irrigation;

import greenhouse.strategy.model.IrrigationPlan;
import greenhouse.strategy.model.ZoneConditions;

import java.util.Set;

/** Waters a fixed volume during fixed hours of the day, ignoring the soil readings. */
public class ScheduledIrrigation implements IrrigationStrategy {

    private final Set<Integer> wateringHours;
    private final double litersPerSession;

    public ScheduledIrrigation() {
        this(Set.of(6, 18), 20.0);
    }

    public ScheduledIrrigation(Set<Integer> wateringHours, double litersPerSession) {
        if (!(litersPerSession >= 0)) {
            throw new IllegalArgumentException("litersPerSession must not be negative");
        }
        this.wateringHours = Set.copyOf(wateringHours);
        this.litersPerSession = litersPerSession;
    }

    @Override
    public IrrigationPlan plan(ZoneConditions conditions) {
        if (wateringHours.contains(conditions.time().getHour())) {
            return new IrrigationPlan(litersPerSession, "scheduled watering hour");
        }
        return IrrigationPlan.none("outside scheduled hours");
    }

    @Override
    public String name() {
        return "Scheduled";
    }
}
