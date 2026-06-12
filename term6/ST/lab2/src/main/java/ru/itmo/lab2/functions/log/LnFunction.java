package ru.itmo.lab2.functions.log;

import ru.itmo.lab2.functions.Func;

public final class LnFunction implements Func {

    public static final double LN_2 = seriesRaw(2.0, 1e-18);
    private static final int MAX_TERMS = 10_000;

    private final double eps;

    public LnFunction() { this(1e-10); }
    public LnFunction(double eps) {
        if (eps <= 0 || !Double.isFinite(eps)) {
            throw new IllegalArgumentException("eps must be positive and finite");
        }
        this.eps = eps;
    }

    public double getEps() { return eps; }

    @Override
    public double apply(double x) {
        if (Double.isNaN(x)) return Double.NaN;
        if (x <= 0.0) {
            throw new ArithmeticException("ln(x) is undefined for x <= 0, got x = " + x);
        }
        if (Double.isInfinite(x)) return Double.POSITIVE_INFINITY;

        int n = 0;
        double m = x;
        while (m > 4.0 / 3.0) { m /= 2.0; n++; }
        while (m < 2.0 / 3.0) { m *= 2.0; n--; }
        return n * LN_2 + seriesRaw(m, eps);
    }

    static double seriesRaw(double x, double eps) {
        double y = (x - 1.0) / (x + 1.0);
        double y2 = y * y;
        double term = y;
        double sum = term;
        for (int k = 1; k < MAX_TERMS; k++) {
            term *= y2;
            double add = term / (2.0 * k + 1.0);
            sum += add;
            if (Math.abs(add) < eps * 0.5) break;
        }
        return 2.0 * sum;
    }
}
