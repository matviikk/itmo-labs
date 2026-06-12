package ru.itmo.lab2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.itmo.lab2.functions.Func;
import ru.itmo.lab2.functions.trig.CosFunction;
import ru.itmo.lab2.support.StubFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CosFunction — интеграция cos поверх sin")
class CosFunctionTest {

    @Test
    @DisplayName("Значения cos считаются через переданный sin")
    void integration() {
        double[] xs = {0.0, Math.PI / 3, Math.PI / 2};
        double[] sinArgs = new double[xs.length];
        for (int i = 0; i < xs.length; i++) sinArgs[i] = Math.PI / 2 - xs[i];

        Func cos = new CosFunction(StubFactory.sinStub(sinArgs));
        assertEquals(1.0, cos.apply(0.0),         1e-15);
        assertEquals(0.5, cos.apply(Math.PI / 3), 1e-15);
        assertEquals(0.0, cos.apply(Math.PI / 2), 1e-15);
    }
}
