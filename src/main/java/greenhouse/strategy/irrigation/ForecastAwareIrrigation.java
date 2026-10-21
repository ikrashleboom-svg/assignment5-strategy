package greenhouse.strategy.irrigation;

import greenhouse.strategy.data.WeatherProvider;
import greenhouse.strategy.model.IrrigationPlan;
import greenhouse.strategy.model.ZoneConditions;

import java.util.Objects;

/** Lowers watering by the expected rain, skips it in heavy rain, raises it in hot weather. */
public class ForecastAwareIrrigation implements IrrigationStrategy {

    private static final double HEAVY_RAIN_MM = 5.0;
    private static final double TARGET_PERCENT = 60.0;
    private static final double MOISTURE_PERCENT_PER_RAIN_MM = 2.0;
    private static final double LITERS_PER_PERCENT = 0.5;
    private static final double HOT_TEMPERATURE_C = 30.0;
    private static final double HOT_WEATHER_FACTOR = 1.2;

    private final WeatherProvider weather;

    public ForecastAwareIrrigation(WeatherProvider weather) {
        this.weather = Objects.requireNonNull(weather, "weather");
    }

    @Override
    public IrrigationPlan plan(ZoneConditions conditions) {
        double rainMm = weather.expectedRainMm();
        if (rainMm >= HEAVY_RAIN_MM) {
            return IrrigationPlan.none("heavy rain expected");
        }
        double rainCredit = rainMm * MOISTURE_PERCENT_PER_RAIN_MM;
        double deficit = TARGET_PERCENT - conditions.soilMoisturePercent() - rainCredit;
        if (deficit <= 0) {
            return IrrigationPlan.none("soil plus expected rain reach the target");
        }
        double heatFactor = conditions.airTemperatureC() >= HOT_TEMPERATURE_C ? HOT_WEATHER_FACTOR : 1.0;
        return new IrrigationPlan(deficit * LITERS_PER_PERCENT * heatFactor, "deficit after rain forecast");
    }

    @Override
    public String name() {
        return "Forecast aware";
    }
}
