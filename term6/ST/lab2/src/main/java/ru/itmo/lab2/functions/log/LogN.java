package ru.itmo.lab2.functions.log;

import ru.itmo.lab2.functions.Func;

public final class LogN implements Func {

    private final Func ln;
    private final double base;
    private final double lnBase;

    public LogN(Func ln, double base) {
        if (base <= 0 || base == 1.0 || Double.isNaN(base) || Double.isInfinite(base)) {
            throw new IllegalArgumentException("Invalid log base: " + base);
        }
        this.ln = ln;
        this.base = base;
        this.lnBase = ln.apply(base);
        if (lnBase == 0.0 || Double.isNaN(lnBase)) {
            throw new IllegalStateException("ln(base) produced invalid value for base " + base);
        }
    }

    public double getBase() { return base; }

    @Override
    public double apply(double x) {
        if (Double.isNaN(x)) return Double.NaN;
        return ln.apply(x) / lnBase;
    }
}
