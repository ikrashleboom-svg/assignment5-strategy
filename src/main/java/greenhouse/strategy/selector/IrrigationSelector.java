package greenhouse.strategy.selector;

import greenhouse.strategy.data.WeatherProvider;
import greenhouse.strategy.irrigation.ForecastAwareIrrigation;
import greenhouse.strategy.irrigation.IrrigationStrategy;
import greenhouse.strategy.irrigation.MoistureThresholdIrrigation;
import greenhouse.strategy.irrigation.ScheduledIrrigation;
import greenhouse.strategy.model.CropType;
import greenhouse.strategy.model.ZoneConditions;

import java.util.Objects;

/** Chooses the irrigation strategy matching the crop stage, then the rain forecast. */
public class IrrigationSelector {

    private static final double RAIN_FORECAST_MM = 2.0;

    private final WeatherProvider weather;
    private final IrrigationStrategy scheduled = new ScheduledIrrigation();
    private final IrrigationStrategy forecastAware;
    private final IrrigationStrategy moistureThreshold = new MoistureThresholdIrrigation();

    public IrrigationSelector(WeatherProvider weather) {
        this.weather = Objects.requireNonNull(weather, "weather");
        this.forecastAware = new ForecastAwareIrrigation(weather);
    }

    public IrrigationStrategy select(ZoneConditions conditions) {
        if (conditions.crop() == CropType.SEEDLING) {
            return scheduled;
        }
        if (weather.expectedRainMm() >= RAIN_FORECAST_MM) {
            return forecastAware;
        }
        return moistureThreshold;
    }
}
