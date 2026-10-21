package greenhouse.strategy.irrigation;

import greenhouse.strategy.support.FixedWeatherProvider;
import org.junit.jupiter.api.Test;

import static greenhouse.strategy.support.GreenhouseAssertions.assertNotWatered;
import static greenhouse.strategy.support.GreenhouseAssertions.assertWatered;
import static greenhouse.strategy.support.ZoneBuilder.aZone;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ForecastAwareIrrigationTest {

    @Test
    void noRain_refillsTheWholeDeficit() {
        var strategy = new ForecastAwareIrrigation(FixedWeatherProvider.calm());

        assertWatered(strategy.plan(aZone().withSoilMoisture(40).build()), 10.0);
    }

    @Test
    void lightRain_reducesTheVolume() {
        var strategy = new ForecastAwareIrrigation(FixedWeatherProvider.rain(3.0));

        assertWatered(strategy.plan(aZone().withSoilMoisture(40).build()), 7.0);
    }

    @Test
    void heavyRain_skipsWatering() {
        var strategy = new ForecastAwareIrrigation(FixedWeatherProvider.rain(5.0));

        assertNotWatered(strategy.plan(aZone().withSoilMoisture(10).build()));
    }

    @Test
    void rainThatCoversTheDeficit_skipsWatering() {
        var strategy = new ForecastAwareIrrigation(FixedWeatherProvider.rain(1.0));

        assertNotWatered(strategy.plan(aZone().withSoilMoisture(58).build()));
    }

    @Test
    void hotWeather_increasesTheVolume() {
        var strategy = new ForecastAwareIrrigation(FixedWeatherProvider.calm());

        assertWatered(strategy.plan(aZone().withSoilMoisture(40).withAirTemperature(30).build()), 12.0);
    }

    @Test
    void weatherProviderIsRequired() {
        assertThrows(NullPointerException.class, () -> new ForecastAwareIrrigation(null));
    }
}
