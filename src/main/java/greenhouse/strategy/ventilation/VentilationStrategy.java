package greenhouse.strategy.ventilation;

import greenhouse.strategy.model.VentilationSetting;
import greenhouse.strategy.model.ZoneConditions;

/** One way of deciding how a zone is ventilated. */
public interface VentilationStrategy {

    VentilationSetting decide(ZoneConditions conditions);

    String name();
}
