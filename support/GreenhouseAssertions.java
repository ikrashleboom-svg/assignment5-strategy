package greenhouse.strategy.support;

import greenhouse.strategy.model.IrrigationPlan;
import greenhouse.strategy.model.VentilationSetting;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Assertions that read like sentences of the greenhouse domain. */
public final class GreenhouseAssertions {

    private static final double EPSILON = 1e-9;

    private GreenhouseAssertions() {
    }

    public static void assertWatered(IrrigationPlan plan, double expectedLiters) {
        assertEquals(expectedLiters, plan.liters(), EPSILON, "liters (" + plan.reason() + ")");
    }

    public static void assertNotWatered(IrrigationPlan plan) {
        assertEquals(0.0, plan.liters(), EPSILON, "expected no watering (" + plan.reason() + ")");
    }

    public static void assertVentilation(VentilationSetting actual, int expectedWindow, int expectedFan) {
        assertEquals(new VentilationSetting(expectedWindow, expectedFan), actual);
    }

    public static void assertClosed(VentilationSetting actual) {
        assertVentilation(actual, 0, 0);
    }
}
