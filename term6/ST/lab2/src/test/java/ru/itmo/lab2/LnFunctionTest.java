package ru.itmo.lab2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.itmo.lab2.functions.log.LnFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("LnFunction — базовый логарифмический модуль")
class LnFunctionTest {

    private final LnFunction ln = new LnFunction(1e-10);

    @Test
    @DisplayName("Значения в опорных точках совпадают с Math.log")
    void values() {
        assertEquals(0.0, ln.apply(1.0),   1e-12);
        assertEquals(1.0, ln.apply(Math.E), 1e-9);
        assertEquals(Math.log(2.0), ln.apply(2.0), 1e-9);
    }

    @Test
    @DisplayName("Домен x ≤ 0 → ArithmeticException")
    void invalid() {
        assertThrows(ArithmeticException.class, () -> ln.apply(0.0));
        assertThrows(ArithmeticException.class, () -> ln.apply(-1.0));
    }
}
