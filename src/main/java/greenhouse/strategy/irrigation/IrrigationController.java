package greenhouse.strategy.irrigation;

import greenhouse.strategy.model.IrrigationPlan;
import greenhouse.strategy.model.ZoneConditions;

import java.util.Objects;

/** Context: holds the current irrigation strategy, delegates every request to it. */
public class IrrigationController {

    private IrrigationStrategy strategy;

    public IrrigationController(IrrigationStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public void setStrategy(IrrigationStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public IrrigationPlan irrigate(ZoneConditions conditions) {
        return strategy.plan(conditions);
    }

    public String strategyName() {
        return strategy.name();
    }
}
