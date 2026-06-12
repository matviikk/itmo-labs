package ru.itmo.lab2.functions.trig;

import ru.itmo.lab2.functions.Func;

public final class CotFunction implements Func {

    private final Func sin;
    private final Func cos;

    public CotFunction(Func sin, Func cos) {
        this.sin = sin;
        this.cos = cos;
    }

    private static final double ZERO_TOL = 1e-12;

    @Override
    public double apply(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return Double.NaN;
        double s = sin.apply(x);
        if (Math.abs(s) < ZERO_TOL) {
            throw new ArithmeticException("cot(x) is undefined at x = " + x + " (sin(x) ≈ 0)");
        }
        return cos.apply(x) / s;
    }
}
