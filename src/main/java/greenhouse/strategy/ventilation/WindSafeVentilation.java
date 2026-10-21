package greenhouse.strategy.ventilation;

import greenhouse.strategy.data.WeatherProvider;
import greenhouse.strategy.model.VentilationSetting;
import greenhouse.strategy.model.ZoneConditions;

import java.util.Objects;

/** Follows temperature while limiting window opening in strong wind, replacing lost airflow with the fan. */
public class WindSafeVentilation implements VentilationStrategy {

    private static final double TARGET_C = 24.0;
    private static final double WINDOW_PERCENT_PER_DEGREE = 10.0;
    private static final double STRONG_WIND_MS = 8.0;
    private static final double STORM_WIND_MS = 12.0;
    private static final double STRONG_WIND_MAX_OPENING = 30.0;
    private static final double FAN_SHARE_OF_LOST_OPENING = 0.8;

    private final WeatherProvider weather;

    public WindSafeVentilation(WeatherProvider weather) {
        this.weather = Objects.requireNonNull(weather, "weather");
    }

    @Override
    public VentilationSetting decide(ZoneConditions conditions) {
        double wantedOpening = Math.max(0.0, conditions.airTemperatureC() - TARGET_C) * WINDOW_PERCENT_PER_DEGREE;
        double wind = weather.windSpeedMs();

        double allowedOpening = wantedOpening;
        if (wind >= STORM_WIND_MS) {
            allowedOpening = 0.0;
        } else if (wind >= STRONG_WIND_MS) {
            allowedOpening = Math.min(wantedOpening, STRONG_WIND_MAX_OPENING);
        }
        double fan = (wantedOpening - allowedOpening) * FAN_SHARE_OF_LOST_OPENING;
        return VentilationSetting.clamped(allowedOpening, fan);
    }

    @Override
    public String name() {
        return "Wind safe";
    }
}
