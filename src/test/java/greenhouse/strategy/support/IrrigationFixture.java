package greenhouse.strategy.support;

import greenhouse.strategy.data.WeatherProvider;
import greenhouse.strategy.irrigation.IrrigationController;
import greenhouse.strategy.irrigation.IrrigationMode;

/**
 * Builds irrigation controllers for the behaviour contract test.
 * BEFORE the Strategy refactoring: maps a mode to the enum of the big-switch class.
 */
public final class IrrigationFixture {

    public enum Mode { THRESHOLD, SCHEDULED, FORECAST }

    private IrrigationFixture() {
    }

    public static IrrigationController controllerFor(Mode mode, WeatherProvider weather) {
        return new IrrigationController(IrrigationMode.valueOf(mode.name()), weather);
    }

    public static void switchTo(IrrigationController controller, Mode mode, WeatherProvider weather) {
        controller.setMode(IrrigationMode.valueOf(mode.name()));
    }
}
