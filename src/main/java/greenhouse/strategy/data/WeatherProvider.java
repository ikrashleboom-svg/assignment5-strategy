package greenhouse.strategy.data;

/**
 * Our own interface for outside weather data.
 * Strategies depend on this abstraction, never on a concrete weather service.
 */
public interface WeatherProvider {

    /** Rain expected during the next six hours, in millimetres. */
    double expectedRainMm();

    /** Current wind speed in metres per second. */
    double windSpeedMs();
}
