package greenhouse.strategy.ventilation;

import greenhouse.strategy.model.VentilationSetting;
import greenhouse.strategy.model.ZoneConditions;

import java.util.Objects;

/** Context: holds the current ventilation strategy, delegates every request to it. */
public class VentilationController {

    private VentilationStrategy strategy;

    public VentilationController(VentilationStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public void setStrategy(VentilationStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public VentilationSetting ventilate(ZoneConditions conditions) {
        return strategy.decide(conditions);
    }

    public String strategyName() {
        return strategy.name();
    }
}
