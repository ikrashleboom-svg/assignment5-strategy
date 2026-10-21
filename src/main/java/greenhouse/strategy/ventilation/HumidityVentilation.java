package greenhouse.strategy.ventilation;

import greenhouse.strategy.model.VentilationSetting;
import greenhouse.strategy.model.ZoneConditions;

/** Opens windows in proportion to excess humidity, starts the fan in very damp air. */
public class HumidityVentilation implements VentilationStrategy {

    private static final double COMFORT_PERCENT = 70.0;
    private static final double WINDOW_PERCENT_PER_HUMIDITY_POINT = 3.0;
    private static final double FAN_START_PERCENT = 85.0;
    private static final double FAN_BASE_PERCENT = 30.0;
    private static final double FAN_PERCENT_PER_HUMIDITY_POINT = 6.0;

    @Override
    public VentilationSetting decide(ZoneConditions conditions) {
        double humidity = conditions.airHumidityPercent();
        if (humidity <= COMFORT_PERCENT) {
            return VentilationSetting.closed();
        }
        double window = (humidity - COMFORT_PERCENT) * WINDOW_PERCENT_PER_HUMIDITY_POINT;
        double fan = humidity >= FAN_START_PERCENT
                ? FAN_BASE_PERCENT + (humidity - FAN_START_PERCENT) * FAN_PERCENT_PER_HUMIDITY_POINT
                : 0.0;
        return VentilationSetting.clamped(window, fan);
    }

    @Override
    public String name() {
        return "Humidity";
    }
}
