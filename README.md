# Assignment 5: Strategy Pattern in a Smart Greenhouse

Two families of interchangeable algorithms for the **Smart Greenhouse** domain,
each driven by a context class and chosen automatically by a selector class.

| Family | Question it answers | Strategies |
|---|---|---|
| Irrigation | How many liters does a zone get? | Moisture threshold, Scheduled, Forecast aware |
| Ventilation | How far do windows open, how fast does the fan run? | Temperature, Humidity, Wind safe |

## Build, test, run

Requires JDK 17 or newer and Maven (IntelliJ bundles Maven; the Maven tool window works too).

```
mvn clean test        # runs all JUnit 5 tests, writes target/site/jacoco/index.html
mvn exec:java         # runs the console demo
```

The demo (`GreenhouseStrategyDemo`) shows three things:
1. the same input processed by every strategy of a family;
2. manual strategy choice versus automatic choice by the selectors;
3. switching the strategy of one controller while the program runs.

## Classes and responsibilities

| Class | Responsibility |
|---|---|
| `CropType` | Names the growth stage of the plants in a zone. |
| `ZoneConditions` | Holds a validated snapshot of the readings taken in one zone. |
| `IrrigationPlan` | Describes how many liters to water, with a short reason. |
| `VentilationSetting` | Describes window opening with fan speed, each kept between zero to one hundred percent. |
| `WeatherProvider` | Declares the weather facts that strategies need from the outside world. |
| `SimulatedWeatherProvider` | Supplies weather values that the operator sets by hand during the demo. |
| `IrrigationStrategy` | Declares the contract for turning zone readings into a watering plan. |
| `MoistureThresholdIrrigation` | Waters in proportion to the soil moisture deficit once moisture drops below a threshold. |
| `ScheduledIrrigation` | Waters a fixed volume during fixed hours regardless of soil readings. |
| `ForecastAwareIrrigation` | Lowers watering by the rain forecast, skips it in heavy rain, raises it in heat. |
| `IrrigationController` | Holds the current irrigation strategy, delegates every watering request to it. |
| `VentilationStrategy` | Declares the contract for turning zone readings into a ventilation setting. |
| `TemperatureVentilation` | Opens windows in proportion to the excess temperature, starts the fan in strong heat. |
| `HumidityVentilation` | Opens windows in proportion to excess humidity, starts the fan in very damp air. |
| `WindSafeVentilation` | Follows temperature while limiting window opening in strong wind, replacing lost airflow with the fan. |
| `VentilationController` | Holds the current ventilation strategy, delegates every ventilation request to it. |
| `IrrigationSelector` | Chooses the irrigation strategy matching the crop stage, then the rain forecast. |
| `VentilationSelector` | Chooses the ventilation strategy matching the wind speed, then the air humidity. |
| `GreenhouseStrategyDemo` | Runs the console demonstration of comparison, automatic selection, runtime switching. |

Selector rules (the only place with `if`):

* **IrrigationSelector:** seedlings get `ScheduledIrrigation`; otherwise rain forecast >= 2.0 mm gets
  `ForecastAwareIrrigation`; otherwise `MoistureThresholdIrrigation`.
* **VentilationSelector:** wind >= 8.0 m/s gets `WindSafeVentilation`; otherwise humidity >= 80 % gets
  `HumidityVentilation`; otherwise `TemperatureVentilation`.

## Why this is Strategy, not State or Template Method

**Not State.** In State, an object moves through phases of its own life cycle: the current state decides what
happens next, and states usually trigger transitions to each other. Here nothing like that exists. The irrigation
strategies are stateless algorithms for the *same* job, they know nothing about each other, and they never
change the controller's strategy. The choice comes from outside, from the operator or the selector, according to
the situation, not from a transition inside the object.

**Not Template Method.** Template Method fixes the skeleton of one algorithm in a base class and lets subclasses
override single steps; the variation is bound at compile time through inheritance. Here whole algorithms are
replaced, not steps: `ScheduledIrrigation` does not even look at soil moisture, while `ForecastAwareIrrigation`
asks a weather provider. The three classes share nothing except the interface, and the controller swaps
them at runtime with `setStrategy()` through composition. There is no base class with a fixed skeleton at all.

## Isolation from change (Clean Code, chapter 10)

`ForecastAwareIrrigation`, `WindSafeVentilation`, and both selectors need weather data. They depend only on our own
`WeatherProvider` interface, like `PortfolioManager` depends on `StockExchange` in the lecture. The demo uses
`SimulatedWeatherProvider`; the tests use `FixedWeatherProvider`, a fake that always returns the same values, so
tests stay fast, repeatable, and independent of any real weather service. Swapping in a real weather API later means writing one
new class that implements `WeatherProvider`; no strategy or selector changes.

## Refactoring with Git history

The irrigation family was written twice, with the same behaviour tests (`IrrigationBehaviorTest`):

* **Before** (all three algorithms in one class, selected by a `switch`): https://github.com/ikrashleboom-svg/assignment5-strategy/commit/ffa4453c4a208cbf00d49961a3865ede038931c7
* **After** (Strategy pattern): https://github.com/ikrashleboom-svg/assignment5-strategy/commit/796c43d701eece68d9278f3616ef991de864533e

`IrrigationBehaviorTest` is byte-identical in both commits. The only test file that differs is the small helper
`IrrigationFixture`, which builds a controller from a mode name (it has to, because the construction API changes
from "enum" to "strategy object").

## Tests

* One test class per strategy, per selector, and per controller (controllers are tested with stub strategies).
* `StrategyFamilyContractTest`: `@ParameterizedTest` runs the same check on **all** strategies of a family over a grid
  of conditions (irrigation: liters are never negative; ventilation: window and fan stay within 0..100 percent).
* Every selector rule has its own test, including the boundary values.
* Test language: `aZone().withSoilMoisture(30).atTime("06:30").build()`, `assertWatered(plan, 15.0)`, `assertVentilation(setting, 30, 24)`.
