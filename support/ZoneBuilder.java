package greenhouse.strategy.support;

import greenhouse.strategy.model.CropType;
import greenhouse.strategy.model.ZoneConditions;

import java.time.LocalTime;

/** Tiny test language: aZone().withSoilMoisture(30).atTime("06:30").build(). */
public final class ZoneBuilder {

    private double soilMoisture = 50.0;
    private double temperature = 22.0;
    private double humidity = 60.0;
    private LocalTime time = LocalTime.NOON;
    private CropType crop = CropType.MATURE;

    private ZoneBuilder() {
    }

    public static ZoneBuilder aZone() {
        return new ZoneBuilder();
    }

    public ZoneBuilder withSoilMoisture(double percent) {
        this.soilMoisture = percent;
        return this;
    }

    public ZoneBuilder withAirTemperature(double celsius) {
        this.temperature = celsius;
        return this;
    }

    public ZoneBuilder withAirHumidity(double percent) {
        this.humidity = percent;
        return this;
    }

    public ZoneBuilder atTime(String hhmm) {
        this.time = LocalTime.parse(hhmm);
        return this;
    }

    public ZoneBuilder growing(CropType crop) {
        this.crop = crop;
        return this;
    }

    public ZoneConditions build() {
        return new ZoneConditions(soilMoisture, temperature, humidity, time, crop);
    }
}
