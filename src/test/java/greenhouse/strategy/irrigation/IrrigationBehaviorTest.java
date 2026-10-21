package greenhouse.strategy.irrigation;

import greenhouse.strategy.support.FixedWeatherProvider;
import greenhouse.strategy.support.IrrigationFixture.Mode;
import org.junit.jupiter.api.Test;

import static greenhouse.strategy.support.GreenhouseAssertions.assertNotWatered;
import static greenhouse.strategy.support.GreenhouseAssertions.assertWatered;
import static greenhouse.strategy.support.IrrigationFixture.controllerFor;
import static greenhouse.strategy.support.IrrigationFixture.switchTo;
import static greenhouse.strategy.support.ZoneBuilder.aZone;

/**
 * Behaviour contract of the irrigation family. These tests are IDENTICAL on the
 * "before" commit (one class with a switch) and on the "after" commit (Strategy).
 */
class IrrigationBehaviorTest {

    private final FixedWeatherProvider rainyWeather = FixedWeatherProvider.rain(3.0);

    @Test
    void thresholdMode_watersDrySoilInProportionToTheDeficit() {
        var controller = controllerFor(Mode.THRESHOLD, rainyWeather);

        assertWatered(controller.irrigate(aZone().withSoilMoisture(30).build()), 15.0);
    }

    @Test
    void thresholdMode_leavesMoistSoilAlone() {
        var controller = controllerFor(Mode.THRESHOLD, rainyWeather);

        assertNotWatered(controller.irrigate(aZone().withSoilMoisture(35).build()));
    }

    @Test
    void scheduledMode_watersAtScheduledHourEvenWhenSoilIsWet() {
        var controller = controllerFor(Mode.SCHEDULED, rainyWeather);

        assertWatered(controller.irrigate(aZone().withSoilMoisture(90).atTime("06:00").build()), 20.0);
    }

    @Test
    void scheduledMode_staysDryOutsideScheduledHours() {
        var controller = controllerFor(Mode.SCHEDULED, rainyWeather);

        assertNotWatered(controller.irrigate(aZone().withSoilMoisture(10).atTime("12:00").build()));
    }

    @Test
    void forecastMode_subtractsExpectedRainFromTheDeficit() {
        var controller = controllerFor(Mode.FORECAST, rainyWeather);

        assertWatered(controller.irrigate(aZone().withSoilMoisture(40).build()), 7.0);
    }

    @Test
    void forecastMode_skipsWateringInHeavyRain() {
        var controller = controllerFor(Mode.FORECAST, FixedWeatherProvider.rain(5.0));

        assertNotWatered(controller.irrigate(aZone().withSoilMoisture(10).build()));
    }

    @Test
    void switchingTheModeAtRuntimeChangesTheResult() {
        var zone = aZone().withSoilMoisture(30).atTime("12:00").build();
        var controller = controllerFor(Mode.THRESHOLD, rainyWeather);
        assertWatered(controller.irrigate(zone), 15.0);

        switchTo(controller, Mode.SCHEDULED, rainyWeather);

        assertNotWatered(controller.irrigate(zone));
    }
}
