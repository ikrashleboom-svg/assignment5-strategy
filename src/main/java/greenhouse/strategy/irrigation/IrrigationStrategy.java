package greenhouse.strategy.irrigation;

import greenhouse.strategy.model.IrrigationPlan;
import greenhouse.strategy.model.ZoneConditions;

/** One way of deciding how much water a zone receives. */
public interface IrrigationStrategy {

    IrrigationPlan plan(ZoneConditions conditions);

    String name();
}
