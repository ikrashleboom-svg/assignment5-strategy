package greenhouse.strategy.ventilation;

import org.junit.jupiter.api.Test;

import static greenhouse.strategy.support.GreenhouseAssertions.assertClosed;
import static greenhouse.strategy.support.GreenhouseAssertions.assertVentilation;
import static greenhouse.strategy.support.ZoneBuilder.aZone;

class TemperatureVentilationTest {

    private final VentilationStrategy strategy = new TemperatureVentilation();

    @Test
    void atOrBelowTarget_everythingStaysClosed() {
        assertClosed(strategy.decide(aZone().withAirTemperature(24).build()));
    }

    @Test
    void warmAir_opensWindowsProportionally() {
        assertVentilation(strategy.decide(aZone().withAirTemperature(27).build()), 30, 0);
    }

    @Test
    void atFanThreshold_fanIsStillOff() {
        assertVentilation(strategy.decide(aZone().withAirTemperature(30).build()), 60, 0);
    }

    @Test
    void hotAir_startsTheFan() {
        assertVentilation(strategy.decide(aZone().withAirTemperature(33).build()), 90, 60);
    }

    @Test
    void extremeHeat_isCappedAtOneHundredPercent() {
        assertVentilation(strategy.decide(aZone().withAirTemperature(45).build()), 100, 100);
    }
}
