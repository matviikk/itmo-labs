package ru.itmo.lab2.functions.system;

import ru.itmo.lab2.functions.Func;

public final class PositiveBranch implements Func {

    private final Func log2;
    private final Func log3;
    private final Func log5;
    private final Func log10;

    public PositiveBranch(Func log2, Func log3, Func log5, Func log10) {
        this.log2 = log2;
        this.log3 = log3;
        this.log5 = log5;
        this.log10 = log10;
    }

    @Override
    public double apply(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return Double.NaN;
        double l2  = log2.apply(x);
        double l3  = log3.apply(x);
        double l5  = log5.apply(x);
        double l10 = log10.apply(x);

        if (l10 == 0.0) {
            throw new ArithmeticException("System undefined at x = " + x + " (log_10(x) = 0)");
        }
        if (l3 == 0.0) {
            throw new ArithmeticException("System undefined at x = " + x + " (log_3(x) = 0)");
        }
        double numerator   = ((l2 - l5) / l10 + l5) - (l10 * l5);
        double denominator = (l10 / l3) + l5;
        if (denominator == 0.0) {
            throw new ArithmeticException("System undefined at x = " + x + " (denominator = 0)");
        }
        return numerator / denominator;
    }
}
