package greenhouse.strategy.support;

import greenhouse.strategy.data.WeatherProvider;
import greenhouse.strategy.irrigation.ForecastAwareIrrigation;
import greenhouse.strategy.irrigation.IrrigationController;
import greenhouse.strategy.irrigation.IrrigationStrategy;
import greenhouse.strategy.irrigation.MoistureThresholdIrrigation;
import greenhouse.strategy.irrigation.ScheduledIrrigation;

/**
 * Builds irrigation controllers for the behaviour contract test.
 * AFTER the Strategy refactoring: maps a mode to a strategy object.
 */
public final class IrrigationFixture {

    public enum Mode { THRESHOLD, SCHEDULED, FORECAST }

    private IrrigationFixture() {
    }

    public static IrrigationController controllerFor(Mode mode, WeatherProvider weather) {
        return new IrrigationController(strategyFor(mode, weather));
    }

    public static void switchTo(IrrigationController controller, Mode mode, WeatherProvider weather) {
        controller.setStrategy(strategyFor(mode, weather));
    }

    private static IrrigationStrategy strategyFor(Mode mode, WeatherProvider weather) {
        return switch (mode) {
            case THRESHOLD -> new MoistureThresholdIrrigation();
            case SCHEDULED -> new ScheduledIrrigation();
            case FORECAST -> new ForecastAwareIrrigation(weather);
        };
    }
}
