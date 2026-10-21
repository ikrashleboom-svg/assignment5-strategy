package greenhouse.strategy.selector;

import greenhouse.strategy.support.FixedWeatherProvider;
import greenhouse.strategy.ventilation.HumidityVentilation;
import greenhouse.strategy.ventilation.TemperatureVentilation;
import greenhouse.strategy.ventilation.WindSafeVentilation;
import org.junit.jupiter.api.Test;

import static greenhouse.strategy.support.ZoneBuilder.aZone;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VentilationSelectorTest {

    @Test
    void rule1_strongWindGetsTheWindSafeStrategy() {
        var selector = new VentilationSelector(FixedWeatherProvider.wind(8.0));

        assertInstanceOf(WindSafeVentilation.class, selector.select(aZone().build()));
    }

    @Test
    void rule1_beatsHighHumidity() {
        var selector = new VentilationSelector(FixedWeatherProvider.wind(12.0));

        assertInstanceOf(WindSafeVentilation.class, selector.select(aZone().withAirHumidity(95).build()));
    }

    @Test
    void rule2_justBelowStrongWindFallsThroughToHumidity() {
        var selector = new VentilationSelector(FixedWeatherProvider.wind(7.9));

        assertInstanceOf(HumidityVentilation.class, selector.select(aZone().withAirHumidity(80).build()));
    }

    @Test
    void rule2_humidAirGetsTheHumidityStrategy() {
        var selector = new VentilationSelector(FixedWeatherProvider.calm());

        assertInstanceOf(HumidityVentilation.class, selector.select(aZone().withAirHumidity(88).build()));
    }

    @Test
    void rule3_justBelowHumidityBoundaryGetsTheTemperatureStrategy() {
        var selector = new VentilationSelector(FixedWeatherProvider.calm());

        assertInstanceOf(TemperatureVentilation.class, selector.select(aZone().withAirHumidity(79.9).build()));
    }

    @Test
    void weatherProviderIsRequired() {
        assertThrows(NullPointerException.class, () -> new VentilationSelector(null));
    }
}
