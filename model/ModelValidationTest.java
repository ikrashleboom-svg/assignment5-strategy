package greenhouse.strategy.model;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModelValidationTest {

    @Test
    void soilMoistureOutsidePercentRangeIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new ZoneConditions(101, 20, 50, LocalTime.NOON, CropType.MATURE));
    }

    @Test
    void humidityOutsidePercentRangeIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new ZoneConditions(50, 20, -1, LocalTime.NOON, CropType.MATURE));
    }

    @Test
    void negativeLitersAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new IrrigationPlan(-0.1, "bad"));
    }

    @Test
    void ventilationValuesOutsideRangeAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new VentilationSetting(101, 0));
        assertThrows(IllegalArgumentException.class, () -> new VentilationSetting(0, -1));
    }

    @Test
    void clampedForcesValuesIntoRange() {
        assertEquals(new VentilationSetting(100, 0), VentilationSetting.clamped(250.4, -20));
        assertEquals(new VentilationSetting(30, 61), VentilationSetting.clamped(29.6, 60.5));
    }
}
