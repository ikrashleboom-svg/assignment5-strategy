package greenhouse.strategy.ventilation;

import greenhouse.strategy.model.VentilationSetting;
import greenhouse.strategy.model.ZoneConditions;
import org.junit.jupiter.api.Test;

import static greenhouse.strategy.support.ZoneBuilder.aZone;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** The context must only delegate: tested with hand-made stub strategies. */
class VentilationControllerTest {

    private static final class StubStrategy implements VentilationStrategy {
        private final String name;
        private final VentilationSetting answer;

        StubStrategy(String name, int window, int fan) {
            this.name = name;
            this.answer = new VentilationSetting(window, fan);
        }

        @Override
        public VentilationSetting decide(ZoneConditions conditions) {
            return answer;
        }

        @Override
        public String name() {
            return name;
        }
    }

    @Test
    void delegatesTheRequestToTheCurrentStrategy() {
        var controller = new VentilationController(new StubStrategy("A", 10, 20));

        assertEquals(new VentilationSetting(10, 20), controller.ventilate(aZone().build()));
    }

    @Test
    void setStrategy_replacesTheAlgorithmAtRuntime() {
        var controller = new VentilationController(new StubStrategy("A", 10, 20));

        controller.setStrategy(new StubStrategy("B", 50, 60));

        assertEquals(new VentilationSetting(50, 60), controller.ventilate(aZone().build()));
        assertEquals("B", controller.strategyName());
    }

    @Test
    void nullStrategyIsRejected() {
        assertThrows(NullPointerException.class, () -> new VentilationController(null));
    }
}
