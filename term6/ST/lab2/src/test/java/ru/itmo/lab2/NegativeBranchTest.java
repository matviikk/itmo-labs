package ru.itmo.lab2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.itmo.lab2.functions.Func;
import ru.itmo.lab2.functions.system.NegativeBranch;
import ru.itmo.lab2.functions.trig.CosFunction;
import ru.itmo.lab2.functions.trig.CotFunction;
import ru.itmo.lab2.functions.trig.CscFunction;
import ru.itmo.lab2.functions.trig.SecFunction;
import ru.itmo.lab2.functions.trig.SinFunction;
import ru.itmo.lab2.functions.trig.TanFunction;
import ru.itmo.lab2.support.Reference;
import ru.itmo.lab2.support.StubFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("NegativeBranch — интеграция ветки x <= 0")
class NegativeBranchTest {

    @Test
    @DisplayName("Формула ветки собирается из значений заглушек")
    void values() {
        for (double x : new double[]{-0.3, -1.0, -2.5}) {
            Func branch = new NegativeBranch(
                StubFactory.cscStub(x),
                StubFactory.tanStub(x),
                StubFactory.cotStub(x),
                StubFactory.secStub(x));
            assertEquals(Reference.negativeBranch(x), branch.apply(x), 1e-12, "x = " + x);
        }
    }

    @Test
    @DisplayName("С реальными модулями (снизу вверх: sin → cos → tan/cot/sec/csc → ветка)")
    void withRealModules() {
        Func sin = new SinFunction(1e-13);
        Func cos = new CosFunction(sin);
        Func branch = new NegativeBranch(
            new CscFunction(sin),
            new TanFunction(sin, cos),
            new CotFunction(sin, cos),
            new SecFunction(cos));
        for (double x : new double[]{-0.3, -0.5, -1.2, -2.5, -4.0}) {
            assertEquals(Reference.negativeBranch(x), branch.apply(x), 1e-9, "x = " + x);
        }
    }

    @Test
    @DisplayName("Особые точки x = 0, -π, -π/2 → ArithmeticException")
    void singularitiesWithRealModules() {
        Func sin = new SinFunction(1e-12);
        Func cos = new CosFunction(sin);
        Func branch = new NegativeBranch(
            new CscFunction(sin),
            new TanFunction(sin, cos),
            new CotFunction(sin, cos),
            new SecFunction(cos));
        assertThrows(ArithmeticException.class, () -> branch.apply(0.0));
        assertThrows(ArithmeticException.class, () -> branch.apply(-Math.PI));
        assertThrows(ArithmeticException.class, () -> branch.apply(-Math.PI / 2));
    }

    @Test
    @DisplayName("Если заглушка бросает ArithmeticException, ветка его пробрасывает")
    void singularityPropagation() {
        Func throwing = x -> { throw new ArithmeticException("zero"); };
        Func any = x -> 1.0;
        Func branch = new NegativeBranch(throwing, any, any, any);
        assertThrows(ArithmeticException.class, () -> branch.apply(0.0));
    }
}
