package greenhouse.strategy.irrigation;

import org.junit.jupiter.api.Test;

import static greenhouse.strategy.support.GreenhouseAssertions.assertNotWatered;
import static greenhouse.strategy.support.GreenhouseAssertions.assertWatered;
import static greenhouse.strategy.support.ZoneBuilder.aZone;

class MoistureThresholdIrrigationTest {

    private final IrrigationStrategy strategy = new MoistureThresholdIrrigation();

    @Test
    void drySoil_isRefilledToTheTarget() {
        assertWatered(strategy.plan(aZone().withSoilMoisture(30).build()), 15.0);
    }

    @Test
    void justBelowThreshold_isStillWatered() {
        assertWatered(strategy.plan(aZone().withSoilMoisture(34).build()), 13.0);
    }

    @Test
    void atThreshold_noWateringIsNeeded() {
        assertNotWatered(strategy.plan(aZone().withSoilMoisture(35).build()));
    }

    @Test
    void completelyDrySoil_getsTheLargestVolume() {
        assertWatered(strategy.plan(aZone().withSoilMoisture(0).build()), 30.0);
    }

    @Test
    void hasAReadableName() {
        org.junit.jupiter.api.Assertions.assertEquals("Moisture threshold", strategy.name());
    }
}
