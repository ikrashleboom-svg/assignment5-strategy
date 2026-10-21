package greenhouse.strategy.irrigation;

import greenhouse.strategy.model.IrrigationPlan;
import greenhouse.strategy.model.ZoneConditions;
import org.junit.jupiter.api.Test;

import static greenhouse.strategy.support.ZoneBuilder.aZone;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** The context must only delegate: tested with hand-made stub strategies. */
class IrrigationControllerTest {

    private static final class StubStrategy implements IrrigationStrategy {
        private final String name;
        private final IrrigationPlan answer;
        ZoneConditions lastInput;

        StubStrategy(String name, double liters) {
            this.name = name;
            this.answer = new IrrigationPlan(liters, "stub " + name);
        }

        @Override
        public IrrigationPlan plan(ZoneConditions conditions) {
            lastInput = conditions;
            return answer;
        }

        @Override
        public String name() {
            return name;
        }
    }

    @Test
    void delegatesTheRequestToTheCurrentStrategy() {
        var stub = new StubStrategy("A", 7.0);
        var controller = new IrrigationController(stub);
        var zone = aZone().build();

        IrrigationPlan result = controller.irrigate(zone);

        assertSame(stub.answer, result);
        assertSame(zone, stub.lastInput);
    }

    @Test
    void setStrategy_replacesTheAlgorithmAtRuntime() {
        var controller = new IrrigationController(new StubStrategy("A", 1.0));
        var zone = aZone().build();

        controller.setStrategy(new StubStrategy("B", 2.0));

        assertEquals(2.0, controller.irrigate(zone).liters());
        assertEquals("B", controller.strategyName());
    }

    @Test
    void nullStrategyIsRejected() {
        assertThrows(NullPointerException.class, () -> new IrrigationController(null));
        var controller = new IrrigationController(new StubStrategy("A", 1.0));
        assertThrows(NullPointerException.class, () -> controller.setStrategy(null));
    }
}
