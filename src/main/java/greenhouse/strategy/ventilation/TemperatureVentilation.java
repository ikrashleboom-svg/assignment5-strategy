package greenhouse.strategy.ventilation;

import greenhouse.strategy.model.VentilationSetting;
import greenhouse.strategy.model.ZoneConditions;

/** Opens windows in proportion to the excess temperature, starts the fan in strong heat. */
public class TemperatureVentilation implements VentilationStrategy {

    private static final double TARGET_C = 24.0;
    private static final double WINDOW_PERCENT_PER_DEGREE = 10.0;
    private static final double FAN_START_C = 30.0;
    private static final double FAN_BASE_PERCENT = 30.0;
    private static final double FAN_PERCENT_PER_DEGREE = 10.0;

    @Override
    public VentilationSetting decide(ZoneConditions conditions) {
        double temperature = conditions.airTemperatureC();
        if (temperature <= TARGET_C) {
            return VentilationSetting.closed();
        }
        double window = (temperature - TARGET_C) * WINDOW_PERCENT_PER_DEGREE;
        double fan = temperature > FAN_START_C
                ? FAN_BASE_PERCENT + (temperature - FAN_START_C) * FAN_PERCENT_PER_DEGREE
                : 0.0;
        return VentilationSetting.clamped(window, fan);
    }

    @Override
    public String name() {
        return "Temperature";
    }
}
