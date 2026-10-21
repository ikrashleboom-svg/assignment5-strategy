package greenhouse.strategy.irrigation;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static greenhouse.strategy.support.GreenhouseAssertions.assertNotWatered;
import static greenhouse.strategy.support.GreenhouseAssertions.assertWatered;
import static greenhouse.strategy.support.ZoneBuilder.aZone;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScheduledIrrigationTest {

    private final IrrigationStrategy strategy = new ScheduledIrrigation();

    @Test
    void morningHour_watersTheFixedVolume() {
        assertWatered(strategy.plan(aZone().atTime("06:30").build()), 20.0);
    }

    @Test
    void eveningHour_watersTheFixedVolume() {
        assertWatered(strategy.plan(aZone().atTime("18:00").build()), 20.0);
    }

    @Test
    void soilMoistureIsIgnored() {
        assertWatered(strategy.plan(aZone().withSoilMoisture(100).atTime("06:00").build()), 20.0);
    }

    @Test
    void oneMinuteBeforeTheWindow_doesNotWater() {
        assertNotWatered(strategy.plan(aZone().atTime("17:59").build()));
    }

    @Test
    void customSchedule_isRespected() {
        IrrigationStrategy noon = new ScheduledIrrigation(Set.of(12), 5.0);

        assertWatered(noon.plan(aZone().atTime("12:10").build()), 5.0);
        assertNotWatered(noon.plan(aZone().atTime("06:10").build()));
    }

    @Test
    void negativeVolume_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> new ScheduledIrrigation(Set.of(6), -1.0));
    }
}
