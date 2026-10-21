package greenhouse.strategy.data;

/** Simulated weather station: the operator sets the values by hand (no real API needed). */
public class SimulatedWeatherProvider implements WeatherProvider {

    private double rainMm;
    private double windMs;

    public SimulatedWeatherProvider(double rainMm, double windMs) {
        requireNotNegative(rainMm, windMs);
        this.rainMm = rainMm;
        this.windMs = windMs;
    }

    public void update(double rainMm, double windMs) {
        requireNotNegative(rainMm, windMs);
        this.rainMm = rainMm;
        this.windMs = windMs;
    }

    @Override
    public double expectedRainMm() {
        return rainMm;
    }

    @Override
    public double windSpeedMs() {
        return windMs;
    }

    private static void requireNotNegative(double rainMm, double windMs) {
        if (rainMm < 0 || windMs < 0) {
            throw new IllegalArgumentException("weather values must not be negative");
        }
    }
}
