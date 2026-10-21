package greenhouse.strategy;

import greenhouse.strategy.data.WeatherProvider;
import greenhouse.strategy.irrigation.ForecastAwareIrrigation;
import greenhouse.strategy.irrigation.IrrigationStrategy;
import greenhouse.strategy.irrigation.MoistureThresholdIrrigation;
import greenhouse.strategy.irrigation.ScheduledIrrigation;
import greenhouse.strategy.model.CropType;
import greenhouse.strategy.model.IrrigationPlan;
import greenhouse.strategy.model.VentilationSetting;
import greenhouse.strategy.model.ZoneConditions;
import greenhouse.strategy.support.FixedWeatherProvider;
import greenhouse.strategy.ventilation.HumidityVentilation;
import greenhouse.strategy.ventilation.TemperatureVentilation;
import greenhouse.strategy.ventilation.VentilationStrategy;
import greenhouse.strategy.ventilation.WindSafeVentilation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** One check, run on EVERY strategy of a family, over a grid of conditions. */
class StrategyFamilyContractTest {

    private static final WeatherProvider STORMY_AND_WET = new FixedWeatherProvider(4.0, 14.0);

    static Stream<Arguments> irrigationStrategies() {
        return Stream.of(
                Arguments.of("Moisture threshold", new MoistureThresholdIrrigation()),
                Arguments.of("Scheduled", new ScheduledIrrigation()),
                Arguments.of("Forecast aware", new ForecastAwareIrrigation(STORMY_AND_WET)));
    }

    static Stream<Arguments> ventilationStrategies() {
        return Stream.of(
                Arguments.of("Temperature", new TemperatureVentilation()),
                Arguments.of("Humidity", new HumidityVentilation()),
                Arguments.of("Wind safe", new WindSafeVentilation(STORMY_AND_WET)));
    }

    @ParameterizedTest(name = "{0}: liters are never negative")
    @MethodSource("irrigationStrategies")
    void irrigationLitersAreNeverNegative(String label, IrrigationStrategy strategy) {
        for (ZoneConditions zone : conditionGrid()) {
            IrrigationPlan plan = strategy.plan(zone);
            assertTrue(plan.liters() >= 0, label + " returned negative liters for " + zone);
            assertTrue(!plan.reason().isBlank(), label + " returned an empty reason for " + zone);
        }
    }

    @ParameterizedTest(name = "{0}: window and fan stay within 0..100 percent")
    @MethodSource("ventilationStrategies")
    void ventilationValuesStayInsideLegalRange(String label, VentilationStrategy strategy) {
        for (ZoneConditions zone : conditionGrid()) {
            VentilationSetting setting = strategy.decide(zone);
            assertTrue(setting.windowOpenPercent() >= 0 && setting.windowOpenPercent() <= 100,
                    label + " window out of range for " + zone);
            assertTrue(setting.fanSpeedPercent() >= 0 && setting.fanSpeedPercent() <= 100,
                    label + " fan out of range for " + zone);
        }
    }

    @ParameterizedTest(name = "{0}: reports its own name")
    @MethodSource("irrigationStrategies")
    void irrigationStrategiesHaveNames(String label, IrrigationStrategy strategy) {
        Assertions.assertEquals(label, strategy.name());
    }

    @ParameterizedTest(name = "{0}: reports its own name")
    @MethodSource("ventilationStrategies")
    void ventilationStrategiesHaveNames(String label, VentilationStrategy strategy) {
        Assertions.assertEquals(label, strategy.name());
    }

    private static List<ZoneConditions> conditionGrid() {
        List<ZoneConditions> grid = new ArrayList<>();
        for (int soil = 0; soil <= 100; soil += 10) {
            for (int temperature = -10; temperature <= 50; temperature += 10) {
                for (int humidity = 0; humidity <= 100; humidity += 25) {
                    for (int hour = 0; hour < 24; hour += 6) {
                        for (CropType crop : CropType.values()) {
                            grid.add(new ZoneConditions(soil, temperature, humidity, LocalTime.of(hour, 0), crop));
                        }
                    }
                }
            }
        }
        return grid;
    }
}
