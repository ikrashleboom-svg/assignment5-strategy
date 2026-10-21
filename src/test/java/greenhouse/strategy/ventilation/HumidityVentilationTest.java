package greenhouse.strategy.ventilation;

import org.junit.jupiter.api.Test;

import static greenhouse.strategy.support.GreenhouseAssertions.assertClosed;
import static greenhouse.strategy.support.GreenhouseAssertions.assertVentilation;
import static greenhouse.strategy.support.ZoneBuilder.aZone;

class HumidityVentilationTest {

    private final VentilationStrategy strategy = new HumidityVentilation();

    @Test
    void comfortableAir_everythingStaysClosed() {
        assertClosed(strategy.decide(aZone().withAirHumidity(70).build()));
    }

    @Test
    void dampAir_opensWindowsProportionally() {
        assertVentilation(strategy.decide(aZone().withAirHumidity(80).build()), 30, 0);
    }

    @Test
    void veryDampAir_startsTheFan() {
        assertVentilation(strategy.decide(aZone().withAirHumidity(90).build()), 60, 60);
    }

    @Test
    void saturatedAir_isCappedAtLegalLimits() {
        assertVentilation(strategy.decide(aZone().withAirHumidity(100).build()), 90, 100);
    }

    @Test
    void temperatureIsIgnored() {
        assertClosed(strategy.decide(aZone().withAirTemperature(40).withAirHumidity(50).build()));
    }
}
