package greenhouse.strategy.irrigation;

import greenhouse.strategy.data.WeatherProvider;
import greenhouse.strategy.model.IrrigationPlan;
import greenhouse.strategy.model.ZoneConditions;

import java.util.Objects;
import java.util.Set;

/**
 * BEFORE refactoring (the "bad" version): all three irrigation algorithms live in
 * ONE class and are selected with a switch. Adding a fourth algorithm means editing
 * this class (violates Open/Closed), and every algorithm is tangled with the others.
 */
public class IrrigationController {

    private IrrigationMode mode;
    private final WeatherProvider weather;

    public IrrigationController(IrrigationMode mode, WeatherProvider weather) {
        this.mode = Objects.requireNonNull(mode, "mode");
        this.weather = Objects.requireNonNull(weather, "weather");
    }

    public void setMode(IrrigationMode mode) {
        this.mode = Objects.requireNonNull(mode, "mode");
    }

    public IrrigationPlan irrigate(ZoneConditions conditions) {
        switch (mode) {
            case THRESHOLD: {
                double moisture = conditions.soilMoisturePercent();
                if (moisture >= 35.0) {
                    return IrrigationPlan.none("soil moisture is sufficient");
                }
                return new IrrigationPlan((60.0 - moisture) * 0.5, "soil below threshold, refilling to target");
            }
            case SCHEDULED: {
                Set<Integer> hours = Set.of(6, 18);
                if (hours.contains(conditions.time().getHour())) {
                    return new IrrigationPlan(20.0, "scheduled watering hour");
                }
                return IrrigationPlan.none("outside scheduled hours");
            }
            case FORECAST: {
                double rainMm = weather.expectedRainMm();
                if (rainMm >= 5.0) {
                    return IrrigationPlan.none("heavy rain expected");
                }
                double deficit = 60.0 - conditions.soilMoisturePercent() - rainMm * 2.0;
                if (deficit <= 0) {
                    return IrrigationPlan.none("soil plus expected rain reach the target");
                }
                double heatFactor = conditions.airTemperatureC() >= 30.0 ? 1.2 : 1.0;
                return new IrrigationPlan(deficit * 0.5 * heatFactor, "deficit after rain forecast");
            }
            default:
                throw new IllegalStateException("Unknown mode: " + mode);
        }
    }
}
