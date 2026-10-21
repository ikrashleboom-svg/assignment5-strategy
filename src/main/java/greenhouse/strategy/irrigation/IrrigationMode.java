package greenhouse.strategy.irrigation;

/** BEFORE refactoring: the mode is an enum, the algorithms live in one big switch. */
public enum IrrigationMode {
    THRESHOLD,
    SCHEDULED,
    FORECAST
}
