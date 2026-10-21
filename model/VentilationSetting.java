package greenhouse.strategy.model;

/** Result of a ventilation strategy: window opening and fan speed, each in percent. */
public record VentilationSetting(int windowOpenPercent, int fanSpeedPercent) {

    public VentilationSetting {
        requirePercent(windowOpenPercent, "windowOpenPercent");
        requirePercent(fanSpeedPercent, "fanSpeedPercent");
    }

    public static VentilationSetting closed() {
        return new VentilationSetting(0, 0);
    }

    /** Rounds both values and forces them into the legal 0..100 range. */
    public static VentilationSetting clamped(double windowOpen, double fanSpeed) {
        return new VentilationSetting(toPercent(windowOpen), toPercent(fanSpeed));
    }

    private static int toPercent(double value) {
        return (int) Math.max(0, Math.min(100, Math.round(value)));
    }

    private static void requirePercent(int value, String field) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException(field + " must be within 0..100 but was " + value);
        }
    }
}
