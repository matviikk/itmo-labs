package ru.itmo.lab2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.itmo.lab2.functions.system.SystemFunction;
import ru.itmo.lab2.support.Reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SystemFunction — диспетчер веток и сквозная сборка")
class SystemFunctionTest {

    private final SystemFunction dispatcher = new SystemFunction(x -> -1.0, x -> 1.0);
    private final SystemFunction real = SystemFunction.withRealModules(1e-13);

    @Test
    @DisplayName("Роутинг по знаку x: x ≤ 0 → negative, x > 0 → positive")
    void routing() {
        assertEquals(-1.0, dispatcher.apply(-0.0001));
        assertEquals(-1.0, dispatcher.apply(0.0));
        assertEquals(1.0, dispatcher.apply(0.0001));
    }

    @Test
    @DisplayName("NaN на входе пробрасывается в выход")
    void nanPropagation() {
        assertTrue(Double.isNaN(dispatcher.apply(Double.NaN)));
        assertTrue(Double.isNaN(real.apply(Double.NaN)));
    }

    @ParameterizedTest(name = "negative branch x = {0}")
    @ValueSource(doubles = { -0.3, -1.0, -2.5, -4.0 })
    @DisplayName("Сквозная сборка: ветка x ≤ 0 совпадает с эталоном")
    void negativeBranchValues(double x) {
        assertEquals(Reference.system(x), real.apply(x), 1e-9);
    }

    @ParameterizedTest(name = "positive branch x = {0}")
    @ValueSource(doubles = { 0.5, 1.5, 2.718281828, 25.0 })
    @DisplayName("Сквозная сборка: ветка x > 0 совпадает с эталоном")
    void positiveBranchValues(double x) {
        assertEquals(Reference.system(x), real.apply(x), 1e-9);
    }

    @Test
    @DisplayName("Сквозная сборка: особые точки x = 0, -π, -π/2, 1 → ArithmeticException")
    void singularities() {
        assertThrows(ArithmeticException.class, () -> real.apply(0.0));
        assertThrows(ArithmeticException.class, () -> real.apply(-Math.PI));
        assertThrows(ArithmeticException.class, () -> real.apply(-Math.PI / 2));
        assertThrows(ArithmeticException.class, () -> real.apply(1.0));
    }
}
