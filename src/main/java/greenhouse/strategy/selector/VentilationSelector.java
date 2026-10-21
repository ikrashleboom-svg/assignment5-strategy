package greenhouse.strategy.selector;

import greenhouse.strategy.data.WeatherProvider;
import greenhouse.strategy.model.ZoneConditions;
import greenhouse.strategy.ventilation.HumidityVentilation;
import greenhouse.strategy.ventilation.TemperatureVentilation;
import greenhouse.strategy.ventilation.VentilationStrategy;
import greenhouse.strategy.ventilation.WindSafeVentilation;

import java.util.Objects;

/** Chooses the ventilation strategy matching the wind speed, then the air humidity. */
public class VentilationSelector {

    private static final double STRONG_WIND_MS = 8.0;
    private static final double HUMID_AIR_PERCENT = 80.0;

    private final WeatherProvider weather;
    private final VentilationStrategy windSafe;
    private final VentilationStrategy humidity = new HumidityVentilation();
    private final VentilationStrategy temperature = new TemperatureVentilation();

    public VentilationSelector(WeatherProvider weather) {
        this.weather = Objects.requireNonNull(weather, "weather");
        this.windSafe = new WindSafeVentilation(weather);
    }

    public VentilationStrategy select(ZoneConditions conditions) {
        if (weather.windSpeedMs() >= STRONG_WIND_MS) {
            return windSafe;
        }
        if (conditions.airHumidityPercent() >= HUMID_AIR_PERCENT) {
            return humidity;
        }
        return temperature;
    }
}
