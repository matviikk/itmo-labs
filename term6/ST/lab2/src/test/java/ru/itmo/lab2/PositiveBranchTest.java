package ru.itmo.lab2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.itmo.lab2.functions.Func;
import ru.itmo.lab2.functions.log.LnFunction;
import ru.itmo.lab2.functions.log.LogN;
import ru.itmo.lab2.functions.system.PositiveBranch;
import ru.itmo.lab2.support.Reference;
import ru.itmo.lab2.support.StubFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("PositiveBranch — интеграция ветки x > 0")
class PositiveBranchTest {

    @Test
    @DisplayName("Формула ветки собирается из значений заглушек")
    void values() {
        for (double x : new double[]{0.5, 2.0, 25.0}) {
            Func branch = new PositiveBranch(
                StubFactory.logNStub(2.0,  x),
                StubFactory.logNStub(3.0,  x),
                StubFactory.logNStub(5.0,  x),
                StubFactory.logNStub(10.0, x));
            assertEquals(Reference.positiveBranch(x), branch.apply(x), 1e-12, "x = " + x);
        }
    }

    @Test
    @DisplayName("С реальным LnFunction + LogN по диапазону")
    void withRealModules() {
        Func ln = new LnFunction(1e-13);
        Func branch = new PositiveBranch(
            new LogN(ln, 2.0),
            new LogN(ln, 3.0),
            new LogN(ln, 5.0),
            new LogN(ln, 10.0));
        for (double x : new double[]{0.1, 0.5, 1.5, Math.E, 7.0, 100.0}) {
            assertEquals(Reference.positiveBranch(x), branch.apply(x), 1e-10, "x = " + x);
        }
    }

    @Test
    @DisplayName("Особая точка x = 1 на реальных модулях: 0/0 → ArithmeticException")
    void singularityWithRealModules() {
        Func ln = new LnFunction();
        Func branch = new PositiveBranch(
            new LogN(ln, 2.0),
            new LogN(ln, 3.0),
            new LogN(ln, 5.0),
            new LogN(ln, 10.0));
        assertThrows(ArithmeticException.class, () -> branch.apply(1.0));
    }

    @Test
    @DisplayName("Особая точка x = 1 на заглушках: все log дают 0 → 0/0 → ArithmeticException")
    void singularity() {
        Func zero = x -> 0.0;
        Func branch = new PositiveBranch(zero, zero, zero, zero);
        assertThrows(ArithmeticException.class, () -> branch.apply(1.0));
    }
}
