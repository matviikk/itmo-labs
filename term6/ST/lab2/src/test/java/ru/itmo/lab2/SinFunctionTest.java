package ru.itmo.lab2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.itmo.lab2.functions.trig.SinFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("SinFunction — базовый тригонометрический модуль")
class SinFunctionTest {

    private final SinFunction sin = new SinFunction(1e-10);

    @Test
    @DisplayName("Значения в опорных точках совпадают с Math.sin")
    void values() {
        assertEquals(0.0, sin.apply(0.0), 1e12);
        assertEquals(0.5, sin.apply(Math.PI / 6), 1e-9);
        assertEquals(1.0, sin.apply(Math.PI / 2), 1e-9);
    }

    @Test
    @DisplayName("Некорректный eps → IllegalArgumentException")
    void invalid() {
        assertThrows(IllegalArgumentException.class, () -> new SinFunction(0));
    }
}
