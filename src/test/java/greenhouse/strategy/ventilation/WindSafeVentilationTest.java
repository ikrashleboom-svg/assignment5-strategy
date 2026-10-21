package greenhouse.strategy.ventilation;

import greenhouse.strategy.support.FixedWeatherProvider;
import org.junit.jupiter.api.Test;

import static greenhouse.strategy.support.GreenhouseAssertions.assertClosed;
import static greenhouse.strategy.support.GreenhouseAssertions.assertVentilation;
import static greenhouse.strategy.support.ZoneBuilder.aZone;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WindSafeVentilationTest {

    @Test
    void lightWind_behavesLikeTemperatureControl() {
        var strategy = new WindSafeVentilation(FixedWeatherProvider.wind(5.0));

        assertVentilation(strategy.decide(aZone().withAirTemperature(30).build()), 60, 0);
    }

    @Test
    void strongWind_limitsOpeningAndCompensatesWithTheFan() {
        var strategy = new WindSafeVentilation(FixedWeatherProvider.wind(9.0));

        assertVentilation(strategy.decide(aZone().withAirTemperature(30).build()), 30, 24);
    }

    @Test
    void stormWind_keepsWindowsShutAndRunsTheFan() {
        var strategy = new WindSafeVentilation(FixedWeatherProvider.wind(15.0));

        assertVentilation(strategy.decide(aZone().withAirTemperature(30).build()), 0, 48);
    }

    @Test
    void stormWindButCoolAir_needsNoVentilationAtAll() {
        var strategy = new WindSafeVentilation(FixedWeatherProvider.wind(15.0));

        assertClosed(strategy.decide(aZone().withAirTemperature(20).build()));
    }

    @Test
    void weatherProviderIsRequired() {
        assertThrows(NullPointerException.class, () -> new WindSafeVentilation(null));
    }
}
