package greenhouse.strategy.support;

import greenhouse.strategy.data.WeatherProvider;

/** Test fake: always returns the same weather, so tests are fast and repeatable. */
public final class FixedWeatherProvider implements WeatherProvider {

    private final double rainMm;
    private final double windMs;

    public FixedWeatherProvider(double rainMm, double windMs) {
        this.rainMm = rainMm;
        this.windMs = windMs;
    }

    public static FixedWeatherProvider rain(double rainMm) {
        return new FixedWeatherProvider(rainMm, 0.0);
    }

    public static FixedWeatherProvider wind(double windMs) {
        return new FixedWeatherProvider(0.0, windMs);
    }

    public static FixedWeatherProvider calm() {
        return new FixedWeatherProvider(0.0, 0.0);
    }

    @Override
    public double expectedRainMm() {
        return rainMm;
    }

    @Override
    public double windSpeedMs() {
        return windMs;
    }
}
