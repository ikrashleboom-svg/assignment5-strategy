package greenhouse.strategy.selector;

import greenhouse.strategy.irrigation.ForecastAwareIrrigation;
import greenhouse.strategy.irrigation.MoistureThresholdIrrigation;
import greenhouse.strategy.irrigation.ScheduledIrrigation;
import greenhouse.strategy.model.CropType;
import greenhouse.strategy.support.FixedWeatherProvider;
import org.junit.jupiter.api.Test;

import static greenhouse.strategy.support.ZoneBuilder.aZone;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IrrigationSelectorTest {

    @Test
    void rule1_seedlingsGetTheScheduledStrategy() {
        var selector = new IrrigationSelector(FixedWeatherProvider.calm());

        assertInstanceOf(ScheduledIrrigation.class, selector.select(aZone().growing(CropType.SEEDLING).build()));
    }

    @Test
    void rule1_beatsTheRainForecast() {
        var selector = new IrrigationSelector(FixedWeatherProvider.rain(9.0));

        assertInstanceOf(ScheduledIrrigation.class, selector.select(aZone().growing(CropType.SEEDLING).build()));
    }

    @Test
    void rule2_matureCropWithRainSoonGetsTheForecastStrategy() {
        var selector = new IrrigationSelector(FixedWeatherProvider.rain(3.0));

        assertInstanceOf(ForecastAwareIrrigation.class, selector.select(aZone().growing(CropType.MATURE).build()));
    }

    @Test
    void rule2_boundaryIsInclusiveAtTwoMillimetres() {
        var selector = new IrrigationSelector(FixedWeatherProvider.rain(2.0));

        assertInstanceOf(ForecastAwareIrrigation.class, selector.select(aZone().build()));
    }

    @Test
    void rule3_justBelowTheBoundaryFallsBackToTheThresholdStrategy() {
        var selector = new IrrigationSelector(FixedWeatherProvider.rain(1.9));

        assertInstanceOf(MoistureThresholdIrrigation.class, selector.select(aZone().build()));
    }

    @Test
    void rule3_matureCropInDryWeatherGetsTheThresholdStrategy() {
        var selector = new IrrigationSelector(FixedWeatherProvider.calm());

        assertInstanceOf(MoistureThresholdIrrigation.class, selector.select(aZone().build()));
    }

    @Test
    void weatherProviderIsRequired() {
        assertThrows(NullPointerException.class, () -> new IrrigationSelector(null));
    }
}
