package ru.itmo.lab2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.itmo.lab2.functions.Func;
import ru.itmo.lab2.functions.trig.CotFunction;
import ru.itmo.lab2.functions.trig.CscFunction;
import ru.itmo.lab2.functions.trig.SecFunction;
import ru.itmo.lab2.functions.trig.TanFunction;
import ru.itmo.lab2.support.StubFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("tan / cot / sec / csc — интеграция над sin, cos")
class TrigCompositionTest {

    @Test
    @DisplayName("Значения tan/cot/sec/csc в опорных точках")
    void values() {
        double[] xs = {Math.PI / 6, Math.PI / 4, Math.PI / 3, -Math.PI / 4};
        Func sin = StubFactory.sinStub(xs);
        Func cos = StubFactory.cosStub(xs);

        assertEquals(1.0,             new TanFunction(sin, cos).apply(Math.PI / 4), 1e-15);
        assertEquals(Math.sqrt(3),    new TanFunction(sin, cos).apply(Math.PI / 3), 1e-15);
        assertEquals(Math.sqrt(3),    new CotFunction(sin, cos).apply(Math.PI / 6), 1e-15);
        assertEquals(-1.0,            new CotFunction(sin, cos).apply(-Math.PI / 4), 1e-15);
        assertEquals(2.0 / Math.sqrt(3), new SecFunction(cos).apply(Math.PI / 6), 1e-15);
        assertEquals(2.0,             new CscFunction(sin).apply(Math.PI / 6), 1e-15);
    }

    @Test
    @DisplayName("Особые точки: деление на ноль → ArithmeticException")
    void singularities() {
        double[] sing = {0.0, Math.PI / 2};
        Func sin = StubFactory.sinStub(sing);
        Func cos = StubFactory.cosStub(sing);

        assertThrows(ArithmeticException.class, () -> new TanFunction(sin, cos).apply(Math.PI / 2));
        assertThrows(ArithmeticException.class, () -> new CotFunction(sin, cos).apply(0.0));
        assertThrows(ArithmeticException.class, () -> new SecFunction(cos).apply(Math.PI / 2));
        assertThrows(ArithmeticException.class, () -> new CscFunction(sin).apply(0.0));
    }
}
