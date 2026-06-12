package ru.itmo.lab2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.itmo.lab2.functions.Func;
import ru.itmo.lab2.functions.log.LogN;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("LogN — интеграция log_N поверх ln")
class LogNTest {

    private final Func lnStub = Math::log;

    @Test
    @DisplayName("Значения и граничная точка x = 1")
    void values() {
        assertEquals(3.0, new LogN(lnStub, 2).apply(8),  1e-15);
        assertEquals(2.0, new LogN(lnStub, 5).apply(25), 1e-15);
        assertEquals(0.0, new LogN(lnStub, 10).apply(1.0), 1e-15);
    }

    @Test
    @DisplayName("Недопустимые основания → IllegalArgumentException")
    void invalid() {
        assertThrows(IllegalArgumentException.class, () -> new LogN(lnStub, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new LogN(lnStub, -3.0));
    }
}
