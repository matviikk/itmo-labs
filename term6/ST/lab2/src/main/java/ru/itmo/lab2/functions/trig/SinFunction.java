package ru.itmo.lab2.functions.trig;

import ru.itmo.lab2.functions.Func;

public final class SinFunction implements Func {

    public static final double TWO_PI = 2.0 * Math.PI;
    private static final int MAX_TERMS = 200;

    private final double eps;

    public SinFunction() { this(1e-10); }
    public SinFunction(double eps) {
        if (eps <= 0 || !Double.isFinite(eps)) {
            throw new IllegalArgumentException("eps must be positive and finite");
        }
        this.eps = eps;
    }

    public double getEps() { return eps; }

    @Override
    public double apply(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            return Double.NaN;
        }
        double xr = reduce(x);
        double term = xr;
        double sum = xr;
        double xr2 = xr * xr;
        for (int k = 1; k < MAX_TERMS; k++) {
            term = -term * xr2 / ((2.0 * k) * (2.0 * k + 1.0));
            sum += term;
            if (Math.abs(term) < eps) break;
        }
        return sum;
    }

    static double reduce(double x) {
        double y = x - TWO_PI * Math.floor((x + Math.PI) / TWO_PI);
        if (y > Math.PI) y -= TWO_PI;
        if (y <= -Math.PI) y += TWO_PI;
        return y;
    }
}
