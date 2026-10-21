package greenhouse.strategy.model;

import java.util.Objects;

/** Result of an irrigation strategy: how many liters to water, with a short reason. */
public record IrrigationPlan(double liters, String reason) {

    public IrrigationPlan {
        if (!(liters >= 0)) {
            throw new IllegalArgumentException("liters must not be negative but was " + liters);
        }
        Objects.requireNonNull(reason, "reason");
    }

    public static IrrigationPlan none(String reason) {
        return new IrrigationPlan(0.0, reason);
    }
}
