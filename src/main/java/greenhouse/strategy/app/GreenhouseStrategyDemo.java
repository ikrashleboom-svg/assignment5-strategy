package greenhouse.strategy.app;

import greenhouse.strategy.data.SimulatedWeatherProvider;
import greenhouse.strategy.irrigation.ForecastAwareIrrigation;
import greenhouse.strategy.irrigation.IrrigationController;
import greenhouse.strategy.irrigation.IrrigationStrategy;
import greenhouse.strategy.irrigation.MoistureThresholdIrrigation;
import greenhouse.strategy.irrigation.ScheduledIrrigation;
import greenhouse.strategy.model.CropType;
import greenhouse.strategy.model.IrrigationPlan;
import greenhouse.strategy.model.VentilationSetting;
import greenhouse.strategy.model.ZoneConditions;
import greenhouse.strategy.selector.IrrigationSelector;
import greenhouse.strategy.selector.VentilationSelector;
import greenhouse.strategy.ventilation.HumidityVentilation;
import greenhouse.strategy.ventilation.TemperatureVentilation;
import greenhouse.strategy.ventilation.VentilationController;
import greenhouse.strategy.ventilation.VentilationStrategy;
import greenhouse.strategy.ventilation.WindSafeVentilation;

import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
public final class GreenhouseStrategyDemo {

    private GreenhouseStrategyDemo() {
    }

    public static void main(String[] args) {
        SimulatedWeatherProvider weather = new SimulatedWeatherProvider(3.0, 9.0);

        part1AllStrategiesOnOneInput(weather);
        part2ManualVersusAutomatic(weather);
        part3SwitchingAtRuntime(weather);
    }
    private static void part1AllStrategiesOnOneInput(SimulatedWeatherProvider weather) {
        heading("PART 1: same input, every strategy");
        weather.update(3.0, 9.0);
        ZoneConditions zone = zone(30, 31, 82, "06:15", CropType.MATURE);
        System.out.println("Input: " + describe(zone) + " | rain forecast 3.0 mm, wind 9.0 m/s");

        System.out.println("-- Irrigation family");
        List<IrrigationStrategy> irrigation = List.of(
                new MoistureThresholdIrrigation(),
                new ScheduledIrrigation(),
                new ForecastAwareIrrigation(weather));
        for (IrrigationStrategy strategy : irrigation) {
            IrrigationPlan plan = new IrrigationController(strategy).irrigate(zone);
            printIrrigation(strategy.name(), plan);
        }

        System.out.println("-- Ventilation family");
        List<VentilationStrategy> ventilation = List.of(
                new TemperatureVentilation(),
                new HumidityVentilation(),
                new WindSafeVentilation(weather));
        for (VentilationStrategy strategy : ventilation) {
            VentilationSetting setting = new VentilationController(strategy).ventilate(zone);
            printVentilation(strategy.name(), setting);
        }
    }
    private static void part2ManualVersusAutomatic(SimulatedWeatherProvider weather) {
        heading("PART 2: manual choice versus automatic choice");

        System.out.println("-- Manual: operator fixes the strategy");
        IrrigationController manual = new IrrigationController(new MoistureThresholdIrrigation());
        ZoneConditions dry = zone(28, 26, 55, "10:00", CropType.MATURE);
        weather.update(0.0, 2.0);
        printIrrigation(manual.strategyName() + " (manual)", manual.irrigate(dry));

        System.out.println("-- Automatic: selector picks the irrigation strategy per situation");
        IrrigationSelector irrigationSelector = new IrrigationSelector(weather);
        IrrigationController auto = new IrrigationController(new MoistureThresholdIrrigation());

        weather.update(0.0, 2.0);
        autoIrrigate("Seedlings, dry weather", auto, irrigationSelector, zone(28, 24, 60, "06:30", CropType.SEEDLING));
        weather.update(3.5, 2.0);
        autoIrrigate("Mature plants, rain soon", auto, irrigationSelector, zone(40, 24, 60, "10:00", CropType.MATURE));
        weather.update(0.0, 2.0);
        autoIrrigate("Mature plants, dry weather", auto, irrigationSelector, zone(28, 24, 60, "10:00", CropType.MATURE));

        System.out.println("-- Automatic: selector picks the ventilation strategy per situation");
        VentilationSelector ventilationSelector = new VentilationSelector(weather);
        VentilationController ventilationAuto = new VentilationController(new TemperatureVentilation());

        weather.update(0.0, 14.0);
        autoVentilate("Storm wind", ventilationAuto, ventilationSelector, zone(50, 31, 60, "13:00", CropType.MATURE));
        weather.update(0.0, 3.0);
        autoVentilate("Calm, humid air", ventilationAuto, ventilationSelector, zone(50, 22, 88, "05:00", CropType.MATURE));
        weather.update(0.0, 3.0);
        autoVentilate("Calm, hot, normal humidity", ventilationAuto, ventilationSelector, zone(50, 33, 55, "14:00", CropType.MATURE));
    }
    private static void part3SwitchingAtRuntime(SimulatedWeatherProvider weather) {
        heading("PART 3: switching the strategy at runtime");
        weather.update(1.0, 4.0);
        ZoneConditions zone = zone(32, 27, 60, "18:00", CropType.MATURE);
        System.out.println("Input: " + describe(zone) + " | rain forecast 1.0 mm");

        IrrigationController controller = new IrrigationController(new MoistureThresholdIrrigation());
        printIrrigation("1) " + controller.strategyName(), controller.irrigate(zone));

        controller.setStrategy(new ScheduledIrrigation());
        printIrrigation("2) " + controller.strategyName(), controller.irrigate(zone));

        controller.setStrategy(new ForecastAwareIrrigation(weather));
        printIrrigation("3) " + controller.strategyName(), controller.irrigate(zone));
    }

    // ---- helpers ----
    private static void autoIrrigate(String situation, IrrigationController controller,
                                     IrrigationSelector selector, ZoneConditions zone) {
        controller.setStrategy(selector.select(zone));
        IrrigationPlan plan = controller.irrigate(zone);
        System.out.println(String.format(Locale.ROOT, "  %-27s -> %-18s %6.1f L  (%s)",
                situation, controller.strategyName(), plan.liters(), plan.reason()));
    }

    private static void autoVentilate(String situation, VentilationController controller,
                                      VentilationSelector selector, ZoneConditions zone) {
        controller.setStrategy(selector.select(zone));
        VentilationSetting setting = controller.ventilate(zone);
        System.out.println(String.format(Locale.ROOT, "  %-27s -> %-12s window %3d%%, fan %3d%%",
                situation, controller.strategyName(), setting.windowOpenPercent(), setting.fanSpeedPercent()));
    }

    private static void printIrrigation(String label, IrrigationPlan plan) {
        System.out.println(String.format(Locale.ROOT, "  %-28s %6.1f L  (%s)", label, plan.liters(), plan.reason()));
    }

    private static void printVentilation(String label, VentilationSetting setting) {
        System.out.println(String.format(Locale.ROOT, "  %-28s window %3d%%, fan %3d%%",
                label, setting.windowOpenPercent(), setting.fanSpeedPercent()));
    }

    private static ZoneConditions zone(double soil, double temperature, double humidity, String time, CropType crop) {
        return new ZoneConditions(soil, temperature, humidity, LocalTime.parse(time), crop);
    }

    private static String describe(ZoneConditions zone) {
        return String.format(Locale.ROOT, "soil %.0f%%, air %.0f C, humidity %.0f%%, time %s, %s",
                zone.soilMoisturePercent(), zone.airTemperatureC(), zone.airHumidityPercent(),
                zone.time(), zone.crop());
    }

    private static void heading(String text) {
        System.out.println();
        System.out.println("=== " + text + " ===");
    }
}
