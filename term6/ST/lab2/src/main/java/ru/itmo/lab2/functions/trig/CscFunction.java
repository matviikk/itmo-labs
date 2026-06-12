package ru.itmo.lab2.functions.trig;

import ru.itmo.lab2.functions.Func;

public final class CscFunction implements Func {

    private final Func sin;

    public CscFunction(Func sin) { this.sin = sin; }

    private static final double ZERO_TOL = 1e-12;

    @Override
    public double apply(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return Double.NaN;
        double s = sin.apply(x);
        if (Math.abs(s) < ZERO_TOL) {
            throw new ArithmeticException("csc(x) is undefined at x = " + x);
        }
        return 1.0 / s;
    }
}
