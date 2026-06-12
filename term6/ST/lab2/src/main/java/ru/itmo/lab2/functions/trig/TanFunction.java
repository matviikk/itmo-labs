package ru.itmo.lab2.functions.trig;

import ru.itmo.lab2.functions.Func;

public final class TanFunction implements Func {

    private static final double ZERO_TOL = 1e-12;

    private final Func sin;
    private final Func cos;

    public TanFunction(Func sin, Func cos) {
        this.sin = sin;
        this.cos = cos;
    }

    @Override
    public double apply(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return Double.NaN;
        double c = cos.apply(x);
        if (Math.abs(c) < ZERO_TOL) {
            throw new ArithmeticException("tan(x) is undefined at x = " + x + " (cos(x) ≈ 0)");
        }
        return sin.apply(x) / c;
    }
}
