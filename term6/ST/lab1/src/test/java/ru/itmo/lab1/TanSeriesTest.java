package ru.itmo.lab1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import org.junit.jupiter.params.provider.Arguments;

public class TanSeriesTest {

    private static final double EPS = 1e-12;
    private static final int MAX_TERMS = 400;
    private static final double DELTA = 1e-10;

    static Stream<Arguments> safePoints() {
        return Stream.of(
                arguments(0.0),
                arguments(Math.PI / 6.0),
                arguments(-Math.PI / 4.0),
                arguments(1.0),
                arguments(-1.0),
                arguments(0.5),
                arguments(-0.5));
    }

    @ParameterizedTest(name = "tan({0}) ~= Math.tan({0})")
    @MethodSource("safePoints")
    @DisplayName("Correctness on safe points vs Math.tan")
    void correctnessOnSafePoints(double x) {
        double expected = Math.tan(x);
        double actual = TanSeries.tan(x, EPS, MAX_TERMS);
        assertEquals(expected, actual, DELTA);
    }

    @Test
    @DisplayName("Property: oddness tan(-x) = -tan(x)")
    void oddnessProperty() {
        double x = 0.7;
        double a = TanSeries.tan(x, EPS, MAX_TERMS);
        double b = TanSeries.tan(-x, EPS, MAX_TERMS);
        assertEquals(-a, b, DELTA);
    }

    @Test
    @DisplayName("Property: periodicity tan(x + pi) = tan(x)")
    void periodicityProperty() {
        double x = -0.9;
        double a = TanSeries.tan(x, EPS, MAX_TERMS);
        double b = TanSeries.tan(x + Math.PI, EPS, MAX_TERMS);
        assertEquals(a, b, DELTA);
    }

    @Test
    @DisplayName("Large x: tan(1e6 + pi/6) ~= Math.tan(x)")
    void largeXReduction1() {
        double x = 1e6 + Math.PI / 6.0;
        double expected = Math.tan(x);
        double actual = TanSeries.tan(x, EPS, MAX_TERMS);
        assertEquals(expected, actual, 1e-10);
    }

    @Test
    @DisplayName("Large x: tan(-1e6 - pi/4) ~= Math.tan(x)")
    void largeXReduction2() {
        double x = -1e6 - Math.PI / 4.0;
        double expected = Math.tan(x);
        double actual = TanSeries.tan(x, EPS, MAX_TERMS);
        assertEquals(expected, actual, 1e-10);
    }

    @Test
    @DisplayName("Near pi/2 but safe: x = pi/2 - 1e-3 should NOT throw")
    void nearDiscontinuitySafe() {
        double x = Math.PI / 2.0 - 1e-3;
        double actual = TanSeries.tan(x, EPS, MAX_TERMS);
        assertTrue(Double.isFinite(actual));
        assertEquals(Math.tan(x), actual, 1e-6);
    }

    @Test
    @DisplayName("Discontinuity: x = pi/2 should throw ArithmeticException")
    void discontinuityAtPiOver2Throws() {
        assertThrows(ArithmeticException.class, () -> TanSeries.tan(Math.PI / 2.0, EPS, MAX_TERMS));
    }

    @Test
    @DisplayName("Discontinuity neighborhood: if |cos(x)| < eps => ArithmeticException")
    void discontinuityNeighborhoodThrows() {
        double d = EPS / 2.0;
        double x = Math.PI / 2.0 - d;

        assertThrows(ArithmeticException.class, () -> TanSeries.tan(x, EPS, MAX_TERMS));
    }

    @Test
    @DisplayName("Invalid x: NaN => IllegalArgumentException")
    void invalidXNaN() {
        assertThrows(IllegalArgumentException.class, () -> TanSeries.tan(Double.NaN, EPS, MAX_TERMS));
    }

    @Test
    @DisplayName("Invalid x: Infinity => IllegalArgumentException")
    void invalidXInfinity() {
        assertThrows(IllegalArgumentException.class, () -> TanSeries.tan(Double.POSITIVE_INFINITY, EPS, MAX_TERMS));
        assertThrows(IllegalArgumentException.class, () -> TanSeries.tan(Double.NEGATIVE_INFINITY, EPS, MAX_TERMS));
    }

    @Test
    @DisplayName("Invalid eps: eps <= 0 => IllegalArgumentException")
    void invalidEps() {
        assertThrows(IllegalArgumentException.class, () -> TanSeries.tan(0.1, 0.0, MAX_TERMS));
        assertThrows(IllegalArgumentException.class, () -> TanSeries.tan(0.1, -1e-6, MAX_TERMS));
        assertThrows(IllegalArgumentException.class, () -> TanSeries.tan(0.1, Double.NaN, MAX_TERMS));
    }

    @Test
    @DisplayName("Invalid maxTerms: maxTerms <= 0 => IllegalArgumentException")
    void invalidMaxTerms() {
        assertThrows(IllegalArgumentException.class, () -> TanSeries.tan(0.1, EPS, 0));
        assertThrows(IllegalArgumentException.class, () -> TanSeries.tan(0.1, EPS, -10));
    }
}