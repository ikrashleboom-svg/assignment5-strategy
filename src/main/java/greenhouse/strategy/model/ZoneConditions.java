package greenhouse.strategy.model;

import java.time.LocalTime;
import java.util.Objects;

/** One validated snapshot of the readings taken in a greenhouse zone. */
public record ZoneConditions(
        double soilMoisturePercent,
        double airTemperatureC,
        double airHumidityPercent,
        LocalTime time,
        CropType crop) {

    public ZoneConditions {
        requirePercent(soilMoisturePercent, "soilMoisturePercent");
        requirePercent(airHumidityPercent, "airHumidityPercent");
        Objects.requireNonNull(time, "time");
        Objects.requireNonNull(crop, "crop");
    }

    private static void requirePercent(double value, String field) {
        if (!(value >= 0 && value <= 100)) {
            throw new IllegalArgumentException(field + " must be within 0..100 but was " + value);
        }
    }
}
