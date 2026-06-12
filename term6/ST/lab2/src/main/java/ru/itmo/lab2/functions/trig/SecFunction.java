package ru.itmo.lab2.functions.trig;

import ru.itmo.lab2.functions.Func;

public final class SecFunction implements Func {

    private final Func cos;

    public SecFunction(Func cos) { this.cos = cos; }

    private static final double ZERO_TOL = 1e-12;

    @Override
    public double apply(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return Double.NaN;
        double c = cos.apply(x);
        if (Math.abs(c) < ZERO_TOL) {
            throw new ArithmeticException("sec(x) is undefined at x = " + x);
        }
        return 1.0 / c;
    }
}
